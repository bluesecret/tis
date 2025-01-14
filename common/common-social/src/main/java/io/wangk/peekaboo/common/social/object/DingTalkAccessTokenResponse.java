package io.wangk.peekaboo.common.social.object;

import lombok.Data;

/**
 * 钉钉访问令牌应答对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class DingTalkAccessTokenResponse {

    /**
     * 访问令牌。
     */
    private String access_token;
    /**
     * 过期时间。
     */
    private int expires_in;
    /**
     * 错误码。
     */
    private int errcode;
    /**
     * 错误信息。
     */
    private String errmsg;
}
