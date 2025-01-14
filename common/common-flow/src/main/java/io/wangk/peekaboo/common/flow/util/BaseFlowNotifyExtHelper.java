package io.wangk.peekaboo.common.flow.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import com.alibaba.fastjson.JSONObject;
import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import io.wangk.peekaboo.common.core.util.ApplicationContextHolder;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.util.MyDateUtil;
import io.wangk.peekaboo.common.flow.object.FlowUserTaskExtData;
import io.wangk.peekaboo.common.flow.vo.FlowTaskVo;
import io.wangk.peekaboo.common.flow.vo.FlowUserInfoVo;
import io.wangk.peekaboo.common.social.config.CommonSocialProperties;
import io.wangk.peekaboo.common.social.constant.AuthSourceType;
import io.wangk.peekaboo.common.social.object.WechatTemplateMessage;
import io.wangk.peekaboo.common.social.util.DingTalkUtil;
import io.wangk.peekaboo.common.social.util.FeiShuUtil;
import io.wangk.peekaboo.common.social.util.WechatUtil;
import io.wangk.peekaboo.common.social.util.WeworkUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 流程通知扩展帮助实现类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class BaseFlowNotifyExtHelper {

    protected static final ExecutorService EXECUTORS = Executors.newSingleThreadExecutor();

    private final DingTalkUtil dingTalkUtil = ApplicationContextHolder.getBean(DingTalkUtil.class);
    private final FeiShuUtil feiShuUtil = ApplicationContextHolder.getBean(FeiShuUtil.class);
    private final WeworkUtil weworkUtil = ApplicationContextHolder.getBean(WeworkUtil.class);
    private final WechatUtil wechatUtil = ApplicationContextHolder.getBean(WechatUtil.class);
    private final CommonSocialProperties props = ApplicationContextHolder.getBean(CommonSocialProperties.class);

    /**
     * 处理消息。
     *
     * @param notifyType   通知类型，具体值可参考FlowUserTaskExtData中NOTIFY_TYPE开头的常量。
     * @param userInfoList 待通知的用户信息列表。
     * @param taskInfo     流程任务信息。
     * @param message      通知消息模板。
     * @param variables    流程变量。
     */
    public void doNotify(
            String notifyType,
            List<FlowUserInfoVo> userInfoList,
            FlowTaskVo taskInfo,
            String message,
            Map<String, Object> variables) {
        variables.putAll(BeanUtil.beanToMap(taskInfo));
        String processInstanceStartTime =
                DateUtil.format(taskInfo.getProcessInstanceStartTime(), MyDateUtil.COMMON_SHORT_DATETIME_FORMAT);
        variables.put("processInstanceStartTime", processInstanceStartTime);
        String replacedMessage = MyCommonUtil.replaceAllWithVariableData(message, new JSONObject(variables));
        switch (notifyType) {
            case FlowUserTaskExtData.NOTIFY_TYPE_EMAIL:
                EXECUTORS.submit(() -> this.sendEmail(userInfoList, taskInfo, replacedMessage));
                break;
            case FlowUserTaskExtData.NOTIFY_TYPE_DINGTALK:
                EXECUTORS.submit(() -> this.sendDingTalkMessage(userInfoList, taskInfo, replacedMessage));
                break;
            case FlowUserTaskExtData.NOTIFY_TYPE_FEISHU:
                EXECUTORS.submit(() -> this.sendFeiShuMessage(userInfoList, taskInfo, replacedMessage));
                break;
            case FlowUserTaskExtData.NOTIFY_TYPE_WEWORK:
                EXECUTORS.submit(() -> this.sendWeworkMessage(userInfoList, taskInfo, replacedMessage));
                break;
            case FlowUserTaskExtData.NOTIFY_TYPE_WECHAT:
                EXECUTORS.submit(() -> this.sendWechatMessage(userInfoList, taskInfo, replacedMessage));
                break;
            default:
                break;
        }
    }

    /**
     * 邮件标题内容，子类可以根据需求实现自定义标题。
     *
     * @param taskInfo 流程任务对象。
     * @return 邮件标题。
     */
    protected String getEmailTitle(FlowTaskVo taskInfo) {
        return StrFormatter.format("[{}] 流程通知", taskInfo.getProcessDefinitionName());
    }

    /**
     * 发送邮件的方法，子类可以根据需求实现自定义实现。
     *
     * @param userInfoList 待通知的用户信息列表。
     * @param taskInfo     流程任务信息。
     * @param message      通知消息模板。
     */
    protected void sendEmail(List<FlowUserInfoVo> userInfoList, FlowTaskVo taskInfo, String message) {
        for (FlowUserInfoVo userInfo : userInfoList) {
            if (StrUtil.isNotBlank(userInfo.getEmail())) {
                MailUtil.send(userInfo.getEmail(), this.getEmailTitle(taskInfo), message, false);
            }
        }
    }

    /**
     * 推送钉钉消息的方法，子类可以根据需求实现自定义实现。
     *
     * @param userInfoList 待通知的用户信息列表。
     * @param taskInfo     流程任务信息。
     * @param message      通知消息模板。
     */
    protected void sendDingTalkMessage(List<FlowUserInfoVo> userInfoList, FlowTaskVo taskInfo, String message) {
        String accessToken = dingTalkUtil.getAccessToken();
        for (FlowUserInfoVo userInfo : userInfoList) {
            boolean hasSent = false;
            if (userInfo.getUserAuthList() != null) {
                for (FlowUserInfoVo.UserAuth au : userInfo.getUserAuthList()) {
                    if (AuthSourceType.DING_TALK.equals(au.getSource())) {
                        dingTalkUtil.sendTextMessage(accessToken, props.getDingTalkAgentId(), au.getAuthUserId(), message);
                        hasSent = true;
                    }
                }
            }
            if (!hasSent) {
                log.info("user [{}] doesn't send DingTalk message", userInfo.getShowName());
            }
        }
    }

    /**
     * 推送飞书消息的方法，子类可以根据需求实现自定义实现。
     *
     * @param userInfoList 待通知的用户信息列表。
     * @param taskInfo     流程任务信息。
     * @param message      通知消息模板。
     */
    protected void sendFeiShuMessage(List<FlowUserInfoVo> userInfoList, FlowTaskVo taskInfo, String message) {
        for (FlowUserInfoVo userInfo : userInfoList) {
            boolean hasSent = false;
            if (userInfo.getUserAuthList() != null) {
                for (FlowUserInfoVo.UserAuth au : userInfo.getUserAuthList()) {
                    if (AuthSourceType.FEI_SHU.equals(au.getSource())) {
                        feiShuUtil.sendTextMessage(ReceiveIdTypeEnum.USER_ID, au.getAuthUserId(), message);
                        hasSent = true;
                    }
                }
            }
            if (!hasSent) {
                log.info("user [{}] doesn't send Feishu message", userInfo.getShowName());
            }
        }
    }

    /**
     * 推送企微消息的方法，子类可以根据需求实现自定义实现。
     *
     * @param userInfoList 待通知的用户信息列表。
     * @param taskInfo     流程任务信息。
     * @param message      通知消息模板。
     */
    protected void sendWeworkMessage(List<FlowUserInfoVo> userInfoList, FlowTaskVo taskInfo, String message) {
        String accessToken = weworkUtil.getAccessToken();
        for (FlowUserInfoVo userInfo : userInfoList) {
            boolean hasSent = false;
            if (userInfo.getUserAuthList() != null) {
                for (FlowUserInfoVo.UserAuth au : userInfo.getUserAuthList()) {
                    if (AuthSourceType.WE_WORK.equals(au.getSource())) {
                        weworkUtil.sendTextMessage(accessToken, props.getWeworkAgentId(), au.getAuthUserId(), message);
                        hasSent = true;
                    }
                }
            }
            if (!hasSent) {
                log.info("user [{}] doesn't send Wework message", userInfo.getShowName());
            }
        }
    }

    /**
     * 推送微信模板消息的方法，子类可以根据需求实现自定义实现。
     *
     * @param userInfoList 待通知的用户信息列表。
     * @param taskInfo     流程任务信息。
     * @param message      通知消息模板。
     */
    protected void sendWechatMessage(List<FlowUserInfoVo> userInfoList, FlowTaskVo taskInfo, String message) {
        for (FlowUserInfoVo userInfo : userInfoList) {
            boolean hasSent = false;
            if (userInfo.getUserAuthList() != null) {
                for (FlowUserInfoVo.UserAuth au : userInfo.getUserAuthList()) {
                    if (AuthSourceType.WE_CHAT.equals(au.getSource())) {
                        WechatTemplateMessage msg = getWechatTemplateMessage(message, au);
                        wechatUtil.sendTemplateMessage(props.getWechatAppId(), props.getWechatSecret(), msg);
                        hasSent = true;
                    }
                }
            }
            if (!hasSent) {
                log.info("user [{}] doesn't send Wechat message", userInfo.getShowName());
            }
        }
    }

    private WechatTemplateMessage getWechatTemplateMessage(String message, FlowUserInfoVo.UserAuth au) {
        JSONObject cotent = new JSONObject();
        JSONObject keyword1 = new JSONObject();
        cotent.put(props.getWechatTemplateVar1(), keyword1);
        keyword1.put("value", "流程通知");
        JSONObject keyword2 = new JSONObject();
        cotent.put(props.getWechatTemplateVar2(), keyword2);
        keyword2.put("value", message);
        //注意messge字符串的长度在微信模板中是有限制的。
        return new WechatTemplateMessage(au.getOpenId(), props.getWechatTemplateId(), cotent);
    }
}
