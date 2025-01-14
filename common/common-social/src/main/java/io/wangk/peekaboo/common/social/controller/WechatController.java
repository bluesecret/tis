package io.wangk.peekaboo.common.social.controller;

import io.wangk.peekaboo.common.core.annotation.MyRequestBody;
import io.wangk.peekaboo.common.core.object.ResponseResult;
import io.wangk.peekaboo.common.social.config.CommonSocialProperties;
import io.wangk.peekaboo.common.social.object.WechatEvent;
import io.wangk.peekaboo.common.social.object.WechatQrCode;
import io.wangk.peekaboo.common.social.util.StringUtil;
import io.wangk.peekaboo.common.social.util.WechatEventHandler;
import io.wangk.peekaboo.common.social.util.WechatUtil;
import io.wangk.peekaboo.common.social.util.WechatXmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * 微信公众号扫码相关接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@RestController
@RequestMapping("/wechat")
public class WechatController {

    @Autowired
    private CommonSocialProperties prop;
    @Autowired
    private WechatEventHandler wechatEventHandler;
    @Autowired
    private WechatUtil wechatUtil;

    /**
     * 请求微信公众号二维码。
     *
     * @return 微信公众号二维码对象。
     */
    @GetMapping("/mp/generateQrCode")
    public ResponseResult<WechatQrCode> generateQrCode() {
        return ResponseResult.success(wechatUtil.getQrCode(prop.getWechatAppId(), prop.getWechatSecret(),
                prop.getWechatScene(), prop.getWechatQrCodeExpireSeconds()));
    }

    /**
     * 微信回调，验证服务器可用性。
     *
     * @param signature 微信服务器推送的签名。
     * @param timestamp 微信服务器推送的时间戳。
     * @param nonce     微信服务器推送的随机字符串。
     * @param echostr   微信服务器推送的随机字符串。
     * @param response  应答对象。
     */
    @GetMapping
    public void verifyWeChatServer(
            @RequestParam(required = false) String signature,
            @RequestParam(required = false) String timestamp,
            @RequestParam(required = false) String nonce,
            @RequestParam(required = false) String echostr,
            HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        try {
            if (checkSignature(signature, timestamp, nonce)) {
                writeResponse(response, echostr);
            } else {
                writeResponse(response, "Invalid request");
            }
        } catch (IOException e) {
            log.error("Error verifying WeChat server: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理微信公众号事件推送。
     *
     * @param requestBody 微信服务器推送的XML格式的事件数据
     * @return 处理结果的XML字符串
     */
    @PostMapping
    public String handleWechatEvent(@RequestBody String requestBody) throws IOException {
        WechatEvent weChatEvent = WechatXmlUtil.xmlToWeChatEvent(requestBody);
        if ("event".equals(weChatEvent.getMsgType())) {
            return wechatEventHandler.handleEvent(weChatEvent);
        } else {
            return wechatEventHandler.handleMessage(weChatEvent);
        }
    }

    /**
     * 检查扫码状态
     *
     * @param ticket 微信服务器推送的扫码ticket。
     * @return 检查结果。
     */
    @PostMapping(value = "/mp/checkStatus")
    public ResponseResult<Boolean> checkStatus(@MyRequestBody String ticket) {
        return ResponseResult.success(wechatEventHandler.checkStatus(ticket));
    }

    private boolean checkSignature(String signature, String timestamp, String nonce) {
        String[] arr = new String[]{prop.getWechatToken(), timestamp, nonce};
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (String anArr : arr) {
            content.append(anArr);
        }
        String tmpStr = StringUtil.sha1(content.toString());
        return tmpStr != null && tmpStr.equals(signature.toUpperCase());
    }

    private void writeResponse(HttpServletResponse response, String content) throws IOException {
        try (PrintWriter writer = response.getWriter()) {
            writer.write(content);
            writer.flush();
        }
    }
}
