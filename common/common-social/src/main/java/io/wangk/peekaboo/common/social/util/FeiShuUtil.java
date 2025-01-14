package io.wangk.peekaboo.common.social.util;

import cn.hutool.core.text.StrFormatter;
import com.alibaba.fastjson.JSON;
import com.google.gson.JsonParser;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.authen.v1.model.*;
import com.lark.oapi.service.im.v1.enums.ReceiveIdTypeEnum;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import io.wangk.peekaboo.common.social.config.CommonSocialProperties;
import io.wangk.peekaboo.common.social.constant.AuthSourceType;
import io.wangk.peekaboo.common.social.object.AuthUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 飞书的工具类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Component
public class FeiShuUtil {

    private final CommonSocialProperties prop;

    private final Client client;

    public FeiShuUtil(CommonSocialProperties prop) {
        this.prop = prop;
        client = Client.newBuilder(prop.getFeiShuClientId(), prop.getFeiShuClientSecret()).build();
    }

    /**
     * 根据第三方验证码获取用户信息。
     *
     * @param authCode 第三方的验证码。
     * @return 用户信息。
     */
    public AuthUserInfo getUserInfo(String authCode) {
        try {
            String userAccessToken = this.getUserAccessToken(authCode);
            GetUserInfoRespBody user = this.getUserResponseBody(userAccessToken);
            log.info("fei shu user:{}", JSON.toJSONString(user));
            AuthUserInfo authUser = new AuthUserInfo();
            authUser.setOpenId(user.getOpenId());
            authUser.setUnionId(user.getUnionId());
            authUser.setUserId(user.getUserId());
            authUser.setNickName(user.getName());
            authUser.setAvatar(user.getAvatarUrl());
            authUser.setSource(AuthSourceType.FEI_SHU);
            return authUser;
        } catch (Exception e) {
            throw new MyRuntimeException(e);
        }
    }

    public void sendTextMessage(ReceiveIdTypeEnum receiveIdType, String receiveId, String message) {
        // 创建请求对象
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .receiveIdType(receiveIdType.getValue())
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(receiveId)
                        .msgType("text")
                        .content(String.format("{\"text\":\"%s\"}", message))
                        .build())
                .build();
        try {
            CreateMessageResp r = client.im().message().create(req);
            // 处理服务端错误
            if (!r.success()) {
                String respData = Jsons.createGSON(true, false)
                        .toJson(JsonParser.parseString(new String(r.getRawResponse().getBody(), StandardCharsets.UTF_8)));
                String msg = String.format("code:%s,msg:%s,reqId:%s, resp:%s", r.getCode(), r.getMsg(), r.getRequestId(), respData);
                log.error(msg);
                throw new MyRuntimeException(msg);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new MyRuntimeException(e);
        }
    }

    private String getUserAccessToken(String code) throws Exception {
        // 创建请求对象
        CreateOidcAccessTokenReq req = CreateOidcAccessTokenReq.newBuilder()
                .createOidcAccessTokenReqBody(CreateOidcAccessTokenReqBody.newBuilder()
                        .grantType("authorization_code")
                        .code(code)
                        .build()).build();
        // 发起请求
        CreateOidcAccessTokenResp r = client.authen().oidcAccessToken().create(req);
        // 处理服务端错误
        if (!r.success()) {
            String respData = Jsons.createGSON(true, false)
                    .toJson(JsonParser.parseString(new String(r.getRawResponse().getBody(), StandardCharsets.UTF_8)));
            String msg = StrFormatter.format(
                    "code:{},msg:{},reqId:{}, resp:{}", r.getCode(), r.getMsg(), r.getRequestId(), respData);
            log.error(msg);
            throw new MyRuntimeException(msg);
        }
        return r.getData().getAccessToken();
    }

    private GetUserInfoRespBody getUserResponseBody(String accessToken) throws Exception {
        GetUserInfoResp r = client.authen()
                .userInfo().get(RequestOptions.newBuilder().userAccessToken(accessToken).build());
        // 处理服务端错误
        if (!r.success()) {
            String respData = Jsons.createGSON(true, false)
                    .toJson(JsonParser.parseString(new String(r.getRawResponse().getBody(), StandardCharsets.UTF_8)));
            String msg = StrFormatter.format(
                    "code:{},msg:{},reqId:{}, resp:{}", r.getCode(), r.getMsg(), r.getRequestId(), respData);
            log.error(msg);
            throw new MyRuntimeException(msg);
        }
        return r.getData();
    }
}
