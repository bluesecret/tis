package io.wangk.peekaboo.common.social.util;

import io.wangk.peekaboo.common.redis.util.CommonRedisUtil;
import io.wangk.peekaboo.common.social.config.CommonSocialProperties;
import io.wangk.peekaboo.common.social.object.WechatEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 事件回调处理服务。
 * 官方文档：<a href="https://developers.weixin.qq.com/doc/offiaccount/Getting_Started/Overview">...</a>
 * 参考资料：<a href="https://www.cnblogs.com/niumoo/p/18118422">...</a>
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class WechatEventHandler {

    /**
     * 微信扫码登录缓存访问令牌的键前缀
     */
    public static final String WECHAT_LOGIN_SESSION_KEY = "wechat:ticket:";
    /**
     * 微信二维码有效时间（秒），该时长最好大于申请的临时二维码的有效时长，以保证该二维码只能被使用一次。
     */
    public static final int WECHAT_QR_CODE_EXPIRE_SECONDS = 600;

    @Autowired
    private CommonSocialProperties prop;
    @Autowired
    private CommonRedisUtil redisUtil;

    public String handleEvent(WechatEvent event) {
        String eventType = event.getEvent();
        switch (eventType) {
            case "subscribe":
                return handleSubscribeEvent(event);
            case "unsubscribe":
                return handleUnsubscribeEvent(event);
            case "SCAN":
                return handleScanEvent(event);
            default:
                log.warn("Unhandled wechat event type: {}", eventType);
                return "success";
        }
    }

    /**
     * 检查二维码是否已扫码。
     *
     * @param ticket 二维码的ticket。
     * @return true表示已扫码，false表示未扫码。
     */
    public boolean checkStatus(String ticket) {
        return redisUtil.getFromCache(WECHAT_LOGIN_SESSION_KEY + ticket, null, id -> null, String.class) != null;
    }

    private String handleSubscribeEvent(WechatEvent event) {
        String result = "欢迎关注";
        if (StringUtils.equals(event.getEventKey(), prop.getWechatScene())) {
            String openId = redisUtil.getFromCache(
                    WECHAT_LOGIN_SESSION_KEY + event.getTicket(),
                    event.getFromUserName(),
                    id -> event.getFromUserName(),
                    String.class,
                    WECHAT_QR_CODE_EXPIRE_SECONDS
            );
            result = StringUtils.equals(openId, event.getFromUserName()) ? "扫码成功" : "欢迎关注";
        }
        return buildResponseXml(event, result);
    }

    private String handleUnsubscribeEvent(WechatEvent event) {
        return "success";
    }

    private String handleScanEvent(WechatEvent event) {
        if (StringUtils.equals(event.getEventKey(), prop.getWechatScene())) {
            String openId = redisUtil.getFromCache(
                    WECHAT_LOGIN_SESSION_KEY + event.getTicket(),
                    event.getFromUserName(),
                    id -> event.getFromUserName(),
                    String.class,
                    WECHAT_QR_CODE_EXPIRE_SECONDS
            );
            return buildResponseXml(event, StringUtils.equals(openId, event.getFromUserName()) ? "扫码成功" : "二维码已失效");
        }
        return "success";
    }

    public String handleMessage(WechatEvent event) {
        log.info("Handling message: {}", event.getMsgType());
        return "success";
    }

    private String buildResponseXml(WechatEvent event, String result) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<xml>")
                .append("<ToUserName><![CDATA[").append(event.getFromUserName()).append("]]></ToUserName>")
                .append("<FromUserName><![CDATA[").append(event.getToUserName()).append("]]></FromUserName>")
                .append("<CreateTime>").append(System.currentTimeMillis() / 1000).append("</CreateTime>")
                .append("<MsgType><![CDATA[text]]></MsgType>")
                .append("<Content><![CDATA[").append(result).append("]]></Content>")
                .append("</xml>");
        return xmlBuilder.toString();
    }
}
