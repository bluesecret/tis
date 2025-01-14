package io.wangk.peekaboo.common.social.util;

import com.alibaba.fastjson.JSONObject;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.redis.util.CommonRedisUtil;
import io.wangk.peekaboo.common.social.constant.AuthSourceType;
import io.wangk.peekaboo.common.social.object.AuthUserInfo;
import io.wangk.peekaboo.common.social.object.WechatTemplateMessage;
import io.wangk.peekaboo.common.social.object.WechatQrCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 微信的工具类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class WechatUtil {

    @Autowired
    private CommonRedisUtil redisUtil;

    /**
     * 微信获取访问令牌的API模板
     */
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    /**
     * 微信创建二维码的API模板
     */
    private static final String QR_CODE_CREATE_API_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=%s";
    /**
     * 微信二维码图片的API前缀
     */
    private static final String QR_CODE_URL_PREFIX = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
    /**
     * 访问令牌过期前的安全缓冲时间（秒）
     */
    private static final int EXPIRE_BUFFER_SECONDS = 10;
    /**
     * 微信发送模板消息的API模板
     */
    private static final String TEMPLATE_MESSAGE_SEND_API_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";

    private static final Map<String, AtomicReference<CacheEntry>> ACCESS_TOKEN_CACHES = new ConcurrentHashMap<>();

    /**
     * 公众号通过openId不可获取用户的昵称、头像等信息。
     * 参考官方文档：<a href="https://developers.weixin.qq.com/doc/offiaccount/User_Management/Get_users_basic_information_UnionID.html#UinonId">...</a>
     *
     * @param ticket 微信登录票据。
     * @return 微信用户信息对象。
     */
    public AuthUserInfo getWechatUserInfo(String ticket) {
        String openId = redisUtil.getFromCache(
                WechatEventHandler.WECHAT_LOGIN_SESSION_KEY + ticket, null, id -> null, String.class);
        if (openId == null) {
            return null;
        }
        AuthUserInfo authUser = new AuthUserInfo();
        authUser.setUserId(openId);
        authUser.setOpenId(openId);
        authUser.setSource(AuthSourceType.WE_CHAT);
        return authUser;
    }

    /**
     * 获取二维码 Ticket
     * <a href="https://developers.weixin.qq.com/doc/offiaccount/Account_Management/Generating_a_Parametric_QR_Code.html">...</a>
     *
     * @param appId         微信应用ID
     * @param appSecret     微信应用密钥
     * @param scene         场景值
     * @param expireSeconds 二维码有效时间（秒）
     * @return 二维码对象
     */
    public WechatQrCode getQrCode(String appId, String appSecret, String scene, int expireSeconds) {
        validateAppCredentials(appId, appSecret);
        validateScene(scene);
        return getQrCode(appId, appSecret, scene, expireSeconds, 0);
    }

    /**
     * 发送模板消息
     *
     * @param appId     微信应用ID
     * @param appSecret 微信应用密钥
     * @param message   模板消息对象
     */
    public void sendTemplateMessage(String appId, String appSecret, WechatTemplateMessage message) {
        validateAppCredentials(appId, appSecret);
        validateMessage(message);
        sendTemplateMessage(appId, appSecret, message, 0);
    }

    private void sendTemplateMessage(String appId, String appSecret, WechatTemplateMessage message, int retryTimes) {
        if (retryTimes > 1) {
            throw new MyRuntimeException("Failed to send template message after retrying");
        }
        try {
            String accessToken = getAccessToken(appId, appSecret);
            String api = String.format(TEMPLATE_MESSAGE_SEND_API_URL, accessToken);
            String jsonBody = message.toJsonString();
            log.info("Sending template message with body: {}", jsonBody);
            String result = HttpUtil.post(api, jsonBody);
            parseJsonFromResponse(result);
        } catch (AccessTokenExpiredException e) {
            ACCESS_TOKEN_CACHES.remove(appId);
            sendTemplateMessage(appId, appSecret, message, retryTimes + 1);
        } catch (Exception e) {
            log.error("Failed to send template message", e);
            throw new MyRuntimeException("Failed to send template message");
        }
    }

    private String getAccessToken(String appId, String appSecret) {
        validateAppCredentials(appId, appSecret);
        AtomicReference<CacheEntry> cacheRef = ACCESS_TOKEN_CACHES.computeIfAbsent(appId, k -> new AtomicReference<>());
        CacheEntry cacheEntry = cacheRef.get();
        if (cacheEntry != null && cacheEntry.expiresAt.isAfter(LocalDateTime.now())) {
            return cacheEntry.accessToken;
        }
        synchronized (cacheRef) {
            cacheEntry = cacheRef.get();
            if (cacheEntry != null && cacheEntry.expiresAt.isAfter(LocalDateTime.now())) {
                return cacheEntry.accessToken;
            }

            String url = String.format(ACCESS_TOKEN_URL, appId, appSecret);
            JSONObject jsonObject = parseJsonFromResponse(HttpUtil.get(url));
            String accessToken = jsonObject.getString("access_token");
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jsonObject.getLong("expires_in") - EXPIRE_BUFFER_SECONDS);
            cacheRef.set(new CacheEntry(accessToken, expiresAt));
            return accessToken;
        }
    }

    private WechatQrCode getQrCode(String appId, String appSecret, String scene, int expireSeconds, int retryTimes) {
        if (retryTimes > 1) {
            throw new MyRuntimeException("Failed to get QR code after retrying");
        }
        String accessToken = getAccessToken(appId, appSecret);
        String api = String.format(QR_CODE_CREATE_API_URL, accessToken);
        String jsonBody = createQrCodeRequestBody(scene, expireSeconds);
        String result = HttpUtil.post(api, jsonBody);
        JSONObject jsonObject;
        try {
            jsonObject = parseJsonFromResponse(result);
        } catch (AccessTokenExpiredException e) {
            ACCESS_TOKEN_CACHES.remove(appId);
            return getQrCode(appId, appSecret, scene, expireSeconds, retryTimes + 1);
        }
        WechatQrCode weChatQrCode = jsonObject.toJavaObject(WechatQrCode.class);
        weChatQrCode.setQrCodeUrl(QR_CODE_URL_PREFIX + URI.create(weChatQrCode.getTicket()).toASCIIString());
        return weChatQrCode;
    }

    private void validateMessage(WechatTemplateMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("Template message cannot be null");
        }
        validateOpenid(message.getOpenId());
        validateTemplateId(message.getTemplateId());
    }

    private void validateOpenid(String openId) {
        if (openId == null || openId.isEmpty()) {
            throw new IllegalArgumentException("openId must not be null or empty");
        }
    }

    private void validateTemplateId(String templateId) {
        if (templateId == null || templateId.isEmpty()) {
            throw new IllegalArgumentException("templateId must not be null or empty");
        }
    }

    private void validateAppCredentials(String appId, String appSecret) {
        if (appId == null || appSecret == null) {
            throw new IllegalArgumentException("appId and appSecret must not be null");
        }
    }

    private void validateScene(String scene) {
        if (scene == null || scene.isEmpty()) {
            throw new IllegalArgumentException("scene must not be null or empty");
        }
    }

    private String createQrCodeRequestBody(String scene, int expireSeconds) {
        return String.format("{\"expire_seconds\": %d, \"action_name\": \"QR_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"%s\"}}}", expireSeconds, scene);
    }

    private JSONObject parseJsonFromResponse(String response) {
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (jsonObject.containsKey("errcode")) {
            int errorCode = jsonObject.getIntValue("errcode");
            if (errorCode != 0) {
                String errorMessage = jsonObject.getString("errmsg");
                if (errorCode == 40001 && errorMessage.contains("access_token is invalid or not latest")) {
                    throw new AccessTokenExpiredException();
                }
                throw new MyRuntimeException(String.format("Failed request from WeiXin API: errcode=%d, errmsg=%s", errorCode, errorMessage));
            }
        }
        return jsonObject;
    }

    private static class CacheEntry {
        final String accessToken;
        final LocalDateTime expiresAt;

        CacheEntry(String accessToken, LocalDateTime expiresAt) {
            this.accessToken = accessToken;
            this.expiresAt = expiresAt;
        }
    }

    static class AccessTokenExpiredException extends RuntimeException {
    }
}
