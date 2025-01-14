package io.wangk.peekaboo.common.social.util;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.wangk.peekaboo.common.social.object.WechatEvent;

import java.io.IOException;

public class WechatXmlUtil {

    private static final XmlMapper xmlMapper = new XmlMapper();

    /**
     * 将XML字符串转换为WeChatEvent对象。
     *
     * @param xml XML字符串
     * @return WeChatEvent对象
     * @throws IOException 如果XML解析失败
     */
    public static WechatEvent xmlToWeChatEvent(String xml) throws IOException {
        return xmlMapper.readValue(xml, WechatEvent.class);
    }
}
