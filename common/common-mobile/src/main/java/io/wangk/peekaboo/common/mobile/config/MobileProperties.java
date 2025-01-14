package io.wangk.peekaboo.common.mobile.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 移动端的配置对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@ConfigurationProperties(prefix = "common-mobile")
public class MobileProperties {

    /**
     * 在线表单业务操作的URL前缀。
     */
    private String urlPrefix;
    /**
     * 上传文件的根路径。
     */
    private String uploadFileBaseDir;
}
