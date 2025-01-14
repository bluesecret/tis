package io.wangk.peekaboo.common.social.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.dingtalkcontact_1_0.models.GetUserHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.dingtalktodo_1_0.models.CreatePersonalTodoTaskHeaders;
import com.aliyun.dingtalktodo_1_0.models.CreatePersonalTodoTaskRequest;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import io.wangk.peekaboo.common.social.config.CommonSocialProperties;
import io.wangk.peekaboo.common.social.constant.AuthSourceType;
import io.wangk.peekaboo.common.social.exception.AuthException;
import io.wangk.peekaboo.common.social.object.AuthUserInfo;
import io.wangk.peekaboo.common.social.object.DingTalkAccessTokenResponse;
import io.wangk.peekaboo.common.social.object.DingTalkUserResponse;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.social.object.TodoMessageInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * 钉钉的工具类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class DingTalkUtil {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CommonSocialProperties prop;

    private final Config config;

    public static final String API_GET_USER_ID_BY_UNION_ID = "https://oapi.dingtalk.com/user/getUseridByUnionid?access_token=%s&unionid=%s";
    public static final String API_GET_ACCESS_TOKEN = "https://oapi.dingtalk.com/gettoken?appkey=%s&appsecret=%s";
    public static final String API_POST_TEXT_ACCESS_TOKEN = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2?access_token=%s";

    public DingTalkUtil() {
        config = new Config();
        config.protocol = "https";
        config.regionId = "central";
    }

    /**
     * 根据第三方验证码获取用户信息。
     *
     * @param authCode 第三方的验证码。
     * @return 用户信息。
     */
    public AuthUserInfo getUserInfo(String authCode) {
        GetUserResponseBody user;
        String userId;
        try {
            String userAccessToken = this.getUserAccessToken(authCode);
            user = this.getUserResponseBody(userAccessToken);
            log.info("ding ding user: {}", JSON.toJSONString(user));
            String accessToken = this.getAccessToken();
            userId = this.getUserId(accessToken, user.getUnionId());
        } catch (Exception e) {
            throw new MyRuntimeException(e);
        }
        if (StrUtil.isBlank(userId)) {
            throw new AuthException("数据验证失败，当前用户的userId不存在，请验证是否加入钉钉应用！");
        }
        AuthUserInfo authUser = new AuthUserInfo();
        authUser.setOpenId(user.getOpenId());
        authUser.setUnionId(user.getUnionId());
        authUser.setAvatar(user.getAvatarUrl());
        authUser.setNickName(user.getNick());
        authUser.setSource(AuthSourceType.DING_TALK);
        authUser.setUserId(userId);
        return authUser;
    }

    public void sendTextMessage(String accessToken, String agentId, String userIdList, String message) {
        String url = String.format(API_POST_TEXT_ACCESS_TOKEN, accessToken);
        String body = String.format("{\"msg\": {\"msgtype\":\"text\", \"text\":{\"content\":\"%s\"}},\"agent_id\": \"%s\",\"userid_list\": \"%s\"}",
                message, agentId, userIdList);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<MessageResponse> resp = restTemplate.exchange(url, HttpMethod.POST, httpEntity, MessageResponse.class);
        if (resp.getBody().errcode != 0) {
            String msg = String.format(
                    "Failed to call DingTalk sendTextMessage, code: %d, message: %s", resp.getBody().getErrcode(), resp.getBody().getErrmsg());
            throw new MyRuntimeException(msg);
        }
    }

    /**
     * 以个人身份创建个人待办
     *
     * @param msg 待办消息对象。
     */
    public void sendTodoMessage(TodoMessageInfo msg) {
        try {
            com.aliyun.dingtalktodo_1_0.Client client = this.todoClient();
            CreatePersonalTodoTaskHeaders createPersonalTodoTaskHeaders = new CreatePersonalTodoTaskHeaders();
            createPersonalTodoTaskHeaders.xAcsDingtalkAccessToken = msg.getUserAccessToken();
            CreatePersonalTodoTaskRequest.CreatePersonalTodoTaskRequestNotifyConfigs notifyConfigs =
                    new CreatePersonalTodoTaskRequest.CreatePersonalTodoTaskRequestNotifyConfigs().setDingNotify("1");
            CreatePersonalTodoTaskRequest createPersonalTodoTaskRequest = new CreatePersonalTodoTaskRequest()
                    .setSubject(msg.getTitle())
                    .setDescription(msg.getMessage())
                    .setDueTime(msg.getDueTime())
                    .setExecutorIds(msg.getUnionIds())
                    .setNotifyConfigs(notifyConfigs);
            client.createPersonalTodoTaskWithOptions(
                    createPersonalTodoTaskRequest, createPersonalTodoTaskHeaders, new RuntimeOptions());
        } catch (TeaException e) {
            if (!Common.empty(e.code) && !Common.empty(e.message)) {
                log.warn("code: {}, message: {}", e.code, e.message);
            }
            log.warn("detail: {}", e.getAccessDeniedDetail());
            throw new MyRuntimeException(e);
        } catch (Exception e2) {
            throw new MyRuntimeException(e2);
        }
    }

    private String getUserAccessToken(String authCode) throws Exception {
        com.aliyun.dingtalkoauth2_1_0.Client client = authClient();
        GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest()
                .setClientId(prop.getDingTalkClientId())
                .setClientSecret(prop.getDingTalkClientSecret())
                .setCode(authCode)
                .setGrantType("authorization_code");
        GetUserTokenResponse getUserTokenResponse = client.getUserToken(getUserTokenRequest);
        log.info("getUserTokenResponseBody:{}", JSON.toJSONString(getUserTokenResponse.getBody()));
        //获取用户个人token
        return getUserTokenResponse.getBody().getAccessToken();
    }

    private GetUserResponseBody getUserResponseBody(String accessToken) throws Exception {
        com.aliyun.dingtalkcontact_1_0.Client client = contactClient();
        GetUserHeaders getUserHeaders = new GetUserHeaders();
        getUserHeaders.xAcsDingtalkAccessToken = accessToken;
        //获取用户个人信息，如需获取当前授权人的信息，unionId参数必须传me
        return client.getUserWithOptions("me", getUserHeaders, new RuntimeOptions()).getBody();
    }

    public String getAccessToken() {
        String url = String.format(API_GET_ACCESS_TOKEN, prop.getDingTalkClientId(), prop.getDingTalkClientSecret());
        DingTalkAccessTokenResponse resp = restTemplate.getForObject(url, DingTalkAccessTokenResponse.class);
        Assert.notNull(resp, "DingTalkAccessTokenResponse can not be null");
        if (resp.getErrcode() != 0) {
            String msg = String.format("code: %d, message: %s", resp.getErrcode(), resp.getErrmsg());
            throw new MyRuntimeException(msg);
        }
        return resp.getAccess_token();
    }

    private String getUserId(String accessToken, String unionId) {
        String url = String.format(API_GET_USER_ID_BY_UNION_ID, accessToken, unionId);
        DingTalkUserResponse user = restTemplate.getForObject(url, DingTalkUserResponse.class);
        return user == null ? StrUtil.EMPTY : user.getUserid();
    }

    private com.aliyun.dingtalkoauth2_1_0.Client authClient() throws Exception {
        return new com.aliyun.dingtalkoauth2_1_0.Client(config);
    }

    private com.aliyun.dingtalkcontact_1_0.Client contactClient() throws Exception {
        return new com.aliyun.dingtalkcontact_1_0.Client(config);
    }

    private com.aliyun.dingtalktodo_1_0.Client todoClient() throws Exception {
        return new com.aliyun.dingtalktodo_1_0.Client(config);
    }

    @Data
    private static class MessageResponse {
        private int errcode;
        private String errmsg;
        private String task_id;
        private String request_id;
    }
}
