package io.wangk.peekaboo.common.social.object;

import lombok.Data;

/**
 * 第三方授权用户的信息。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class AuthUserInfo {
    /**
     * openId
     */
    private String openId;
    /**
     * unionId
     */
    private String unionId;
    /**
     * 第三方平台的userId，钉钉发送文本消息时，需要使用到，通常是三方平台的成员时才会有该属性值
     */
    private String userId;
    /**
     * 别名。
     */
    private String nickName;
    /**
     * 头像。
     */
    private String avatar;
    /**
     * 第三方授权源。
     */
    private String source;
}
