package io.wangk.peekaboo.common.flow.object;

import lombok.Data;

import java.util.List;

/**
 * 流程用户任务扩展数据对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class FlowUserTaskExtData {

    /**
     * 邮件通知。
     */
    public static final String NOTIFY_TYPE_EMAIL = "email";
    /**
     * 站内信通知。
     */
    public static final String NOTIFY_TYPE_MSG = "message";
    /**
     * 微信通知。
     */
    public static final String NOTIFY_TYPE_WECHAT = "wechat";
    /**
     * 企微通知。
     */
    public static final String NOTIFY_TYPE_WEWORK = "wework";
    /**
     * 钉钉通知。
     */
    public static final String NOTIFY_TYPE_DINGTALK = "dingtalk";
    /**
     * 飞书通知。
     */
    public static final String NOTIFY_TYPE_FEISHU = "feishu";

    public static final String TIMEOUT_AUTO_COMPLETE = "autoComplete";
    public static final String TIMEOUT_SEND_MSG = "sendMessage";

    public static final String EMPTY_USER_TO_ASSIGNEE = "toAssignee";
    public static final String EMPTY_USER_AUTO_REJECT = "autoReject";
    public static final String EMPTY_USER_AUTO_COMPLETE = "autoComplete";

    /**
     * 拒绝后再提交，走重新审批。
     */
    public static final String REJECT_TYPE_REDO = "0";
    /**
     * 拒绝后再提交，直接回到驳回前的节点。
     */
    public static final String REJECT_TYPE_BACK_TO_SOURCE = "1";

    /**
     * 任务通知类型列表。
     */
    private List<String> flowNotifyTypeList;
    /**
     * 拒绝后再次提交的审批类型。
     */
    private String rejectType = REJECT_TYPE_REDO;
    /**
     * 到期提醒的小时数(从待办任务被创建的时候开始计算)。
     */
    private Integer timeoutHours;
    /**
     * 任务超时的处理方式。
     */
    private String timeoutHandleWay;
    /**
     * 默认审批人。
     */
    private String defaultAssignee;
    /**
     * 空用户审批处理方式。
     */
    private String emptyUserHandleWay;
    /**
     * 空用户审批时设定的审批人。
     */
    private String emptyUserToAssignee;
    /**
     * 是否为多实例会签节点。
     */
    private Boolean multiInstance = false;
    /**
     * 是否为串行多实例会签节点。
     */
    private Boolean sequenceMultiInstance = false;
    /**
     * 通知消息模板。
     */
    private String notifyMessage;
}
