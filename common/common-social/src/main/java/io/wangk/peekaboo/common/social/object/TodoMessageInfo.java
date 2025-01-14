package io.wangk.peekaboo.common.social.object;

import lombok.Data;

import java.util.List;

/**
 * 推送待办消息对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class TodoMessageInfo {

    /**
     * 消息标题。
     */
    private String title;
    /**
     * 消息内容。
     */
    private String message;
    /**
     * 到期时间。
     */
    private Long dueTime;
    /**
     * 用户访问令牌。
     */
    private String userAccessToken;
    /**
     * 待发送的用户Id集合。
     */
    List<String> unionIds;
}
