package io.wangk.peekaboo.common.social.object;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 微信模板消息对象，用于构建并发送微信模板消息。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class WechatTemplateMessage {

    /**
     * 接收者的OpenID。
     */
    private String openId;

    /**
     * 模板ID。
     */
    private String templateId;

    /**
     * 点击模板消息后跳转的链接。
     */
    private String url;

    /**
     * 消息的数据内容，通常是一个JSONObject对象。
     */
    private JSONObject data;

    /**
     * 小程序信息，包括appid和pagepath。
     */
    private MiniProgram miniProgram;

    /**
     * 防重入ID，用于确保消息不被重复发送。
     */
    private String clientMsgId;

    /**
     * 构造函数，用于创建一个基本的微信模板消息对象。
     *
     * @param openId      接收者的OpenID
     * @param templateId 模板ID
     * @param url         点击模板消息后跳转的链接
     * @param data        消息的数据内容
     */
    public WechatTemplateMessage(String openId, String templateId, String url, JSONObject data) {
        this.openId = openId;
        this.templateId = templateId;
        this.url = url;
        this.data = data;
    }

    /**
     * 构造函数，用于创建一个带有小程序信息的微信模板消息对象。
     *
     * @param openId      接收者的OpenID
     * @param templateId 模板ID
     * @param data        消息的数据内容
     */
    public WechatTemplateMessage(String openId, String templateId, JSONObject data) {
        this.openId = openId;
        this.templateId = templateId;
        this.data = data;
    }

    /**
     * 构造函数，用于创建一个带有小程序信息的微信模板消息对象。
     *
     * @param openId      接收者的OpenID
     * @param templateId 模板ID
     * @param url         点击模板消息后跳转的链接
     * @param data        消息的数据内容
     */
    public WechatTemplateMessage(String openId, String templateId, JSONObject data, String url) {
        this.openId = openId;
        this.templateId = templateId;
        this.url = url;
        this.data = data;
    }

    /**
     * 构造函数，用于创建一个带有小程序信息的微信模板消息对象。
     *
     * @param openId      接收者的OpenID
     * @param templateId 模板ID
     * @param data        消息的数据内容
     * @param miniProgram 小程序信息
     */
    public WechatTemplateMessage(String openId, String templateId, JSONObject data, MiniProgram miniProgram) {
        this.openId = openId;
        this.templateId = templateId;
        this.data = data;
        this.miniProgram = miniProgram;
    }

    /**
     * 将当前对象转换为JSON字符串，便于发送微信模板消息。
     *
     * @return 当前对象的JSON字符串表示
     */
    public String toJsonString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("touser", this.openId);
        jsonObject.put("template_id", this.templateId);
        jsonObject.put("data", this.data);
        if (this.miniProgram != null) {
            jsonObject.put("miniprogram", this.miniProgram.toJsonString());
        }
        if (this.url != null && !this.url.isEmpty()) {
            jsonObject.put("url", this.url);
        }
        if (this.clientMsgId != null && !this.clientMsgId.isEmpty()) {
            jsonObject.put("client_msg_id", this.clientMsgId);
        }
        return jsonObject.toJSONString();
    }

    /**
     * 内部类，表示小程序信息，包括appid和pagepath。
     */
    @Data
    public static class MiniProgram {
        /**
         * 所需跳转到的小程序appid。
         */
        private String appid;

        /**
         * 所需跳转到小程序的具体页面路径，支持带参数（示例index?foo=bar）。
         */
        private String pagepath;

        /**
         * 构造函数，用于创建一个小程序信息对象。
         *
         * @param appid    小程序的appid
         * @param pagepath 小程序的具体页面路径
         */
        public MiniProgram(String appid, String pagepath) {
            this.appid = appid;
            this.pagepath = pagepath;
        }

        /**
         * 将当前对象转换为JSON字符串，便于发送微信模板消息。
         *
         * @return 当前对象的JSON字符串表示
         */
        public String toJsonString() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appid", this.appid);
            jsonObject.put("pagepath", this.pagepath);
            return jsonObject.toJSONString();
        }
    }
}
