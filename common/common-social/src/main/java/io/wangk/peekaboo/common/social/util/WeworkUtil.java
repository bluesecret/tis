package io.wangk.peekaboo.common.social.util;

import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.social.config.CommonSocialProperties;
import io.wangk.peekaboo.common.social.constant.AuthSourceType;
import io.wangk.peekaboo.common.social.object.AuthUserInfo;
import io.wangk.peekaboo.common.social.object.WeworkAccessTokenResponse;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * 企微的工具类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class WeworkUtil {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CommonSocialProperties prop;

    public static final String API_GET_ACCESS_TOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
    public static final String API_GET_USER_DETAILS = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s";
    public static final String API_GET_USER_INFO = "https://qyapi.weixin.qq.com/cgi-bin/auth/getuserinfo?access_token=%s&code=%s";
    public static final String API_SEND_MESSAGE = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";

    /**
     * 根据第三方验证码获取用户信息。
     *
     * @param authCode 第三方的验证码。
     * @return 用户信息。
     */
    public AuthUserInfo getUserInfo(String authCode) {
        String accessToken = this.getAccessToken();
        Assert.notNull(accessToken, "没有取得ACCESS_TOKEN，请检查appId和secret是否正确");
        String url = String.format(API_GET_USER_INFO, accessToken, authCode);
        UserInfo info = restTemplate.getForObject(url, UserInfo.class);
        log.info("wework user:{}", JSON.toJSONString(info));
        if (info.getErrcode() != 0) {
            log.error("Failed to get Wework userInfo, errcode={}, errmsg={}", info.getErrcode(), info.getErrmsg());
            return null;
        }
        AuthUserInfo authUser = new AuthUserInfo();
        authUser.setUserId(info.getUserid());
        // 企业微信非第三方应用没有openId，用户userId来代替
        authUser.setOpenId(info.getUserid());
        authUser.setSource(AuthSourceType.WE_WORK);
        return authUser;
    }

    public void sendTextMessage(String accessToken, int agentId, String userId, String message) {
        String url = String.format(API_SEND_MESSAGE, accessToken);
        String body = String.format("{\"touser\":\"%s\",\"msgtype\":\"text\",\"agentid\":%d,\"text\":{\"content\":\"%s\"},\"safe\":0,\"enable_id_trans\":0,\"enable_duplicate_check\":0}",
                userId, agentId, message);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<MessageResponse> resp = restTemplate.exchange(url, HttpMethod.POST, httpEntity, MessageResponse.class);
        if (resp.getBody().getErrcode() != 0) {
            String msg = String.format(
                    "Failed to call WeworkUti sendTextMessage, code: %d, message: %s", resp.getBody().getErrcode(), resp.getBody().getErrmsg());
            throw new MyRuntimeException(msg);
        }
    }

    public String getAccessToken() {
        String url = String.format(API_GET_ACCESS_TOKEN, prop.getWeworkCorpId(), prop.getWeworkSecret());
        WeworkAccessTokenResponse resp = restTemplate.getForObject(url, WeworkAccessTokenResponse.class);
        Assert.notNull(resp, "WeworkAccessTokenResponse can not be null");
        if (resp.getErrcode() != 0) {
            throw new MyRuntimeException(String.format("code: %d, message: %s", resp.getErrcode(), resp.getErrmsg()));
        }
        return resp.getAccess_token();
    }

    private String getUserDetails(String accessToken, String userId) {
        String url = String.format(API_GET_USER_DETAILS, accessToken, userId);
        return restTemplate.getForObject(url, String.class);
    }

    private WeworkUtil() {
        //为了避免Sonar的警告
    }

    @Data
    private static class UserInfo {
        private String userid;
        private String openid;
        private int errcode;
        private String errmsg;
    }

    @Data
    private static class MessageResponse {
        private int errcode;
        private String errmsg;
        private String msgid;
    }
}
