package io.wangk.peekaboo.common.social.object;

import lombok.Data;

/**
 * 钉钉的用户应答。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class DingTalkUserResponse {

    /**
     * 错误码。
     */
    private int errcode;
    /**
     * 错误信息。
     */
    private String errmsg;
    /**
     * 联系类型。
     */
    private int contactType;
    /**
     * 用户Id。
     */
    private String userid;

    @Override
    public String toString() {
        return "DingTalkUserResponse{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                ", contactType=" + contactType +
                ", userid='" + userid + '\'' +
                '}';
    }
}

