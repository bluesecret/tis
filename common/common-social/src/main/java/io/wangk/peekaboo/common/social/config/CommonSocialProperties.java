package io.wangk.peekaboo.common.social.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * common-social配置属性类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@ConfigurationProperties(prefix = "common-social")
public class CommonSocialProperties {

    /**
     * 钉钉接入客户端id。
     */
    @Value("${common-social.dingtalk.clientId:}")
    private String dingTalkClientId;
    /**
     * 钉钉接入客户端密码。
     */
    @Value("${common-social.dingtalk.clientSecret:}")
    private String dingTalkClientSecret;
    /**
     * 钉钉企业内部应用AgentId。
     */
    @Value("${common-social.dingtalk.agentId:}")
    private String dingTalkAgentId;
    /**
     * 飞书接入客户端id。
     */
    @Value("${common-social.feishu.clientId:}")
    private String feiShuClientId;
    /**
     * 飞书接入客户端密码。
     */
    @Value("${common-social.feishu.clientSecret:}")
    private String feiShuClientSecret;
    /**
     * 企微接入客户端id。
     */
    @Value("${common-social.wework.corpId:}")
    private String weworkCorpId;
    /**
     * 企微接入客户端密码。
     */
    @Value("${common-social.wework.secret:}")
    private String weworkSecret;
    /**
     * 企微应用AgentId。
     */
    @Value("${common-social.wework.agentId:0}")
    private Integer weworkAgentId;
    /**
     * 微信公众号接入客户端id。
     */
    @Value("${common-social.wechat.appId:}")
    private String wechatAppId;
    /**
     * 微信公众号接入客户端密码。
     */
    @Value("${common-social.wechat.secret:}")
    private String wechatSecret;
    /**
     * 微信公众号接入客户端token。
     */
    @Value("${common-social.wechat.token:}")
    private String wechatToken;
    /**
     * 微信公众号接入客户端场景值。
     */
    @Value("${common-social.wechat.sceneStr:}")
    private String wechatScene;
    /**
     * 微信公众号接入客户端二维码过期时间，单位秒。
     */
    @Value("${common-social.wechat.qrCodeExpireSeconds: 60}")
    private int wechatQrCodeExpireSeconds;
    /**
     * 微信公众号模板消息Id。
     */
    @Value("${common-social.wechat.templateId:}")
    private String wechatTemplateId;
    /**
     * 微信公众号模板消息变量1。
     */
    @Value("${common-social.wechat.templateVar1:}")
    private String wechatTemplateVar1;
    /**
     * 微信公众号模板消息变量1。
     */
    @Value("${common-social.wechat.templateVar2:}")
    private String wechatTemplateVar2;
}
