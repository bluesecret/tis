package io.wangk.peekaboo.common.social.object;

import lombok.Data;

/**
 * 微信二维码对象，包含生成二维码所需的信息。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class WechatQrCode {

    /**
     * 二维码的ticket，用于换取二维码图片。
     */
    private String ticket;

    /**
     * 二维码的有效时间（秒）。
     */
    private Long expireSeconds;

    /**
     * 二维码的URL，通常是指向微信服务器的地址。
     */
    private String url;

    /**
     * 二维码图片的URL，可以直接用于展示二维码图片。
     */
    private String qrCodeUrl;
}
