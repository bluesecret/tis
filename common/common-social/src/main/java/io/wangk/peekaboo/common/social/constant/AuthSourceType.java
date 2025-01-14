package io.wangk.peekaboo.common.social.constant;

/**
 * 第三方授权类型。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class AuthSourceType {

    /**
     * 钉钉。
     */
    public static final String DING_TALK = "dingtalk";
    /**
     * 飞书。
     */
    public static final String FEI_SHU = "feishu";
    /**
     * 企微。
     */
    public static final String WE_WORK = "wework";
    /**
     * 微信。
     */
    public static final String WE_CHAT = "wechat";

    private AuthSourceType() {
        //避免SonarQube警告。
    }
}
