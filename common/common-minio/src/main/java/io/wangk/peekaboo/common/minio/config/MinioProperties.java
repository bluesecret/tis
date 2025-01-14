package io.wangk.peekaboo.common.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * common-minio模块的配置类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /**
     * 访问入口地址。
     */
    private String endpoint;
    /**
     * 访问安全的key。
     */
    private String accessKey;
    /**
     * 访问安全的密钥。
     */
    private String secretKey;
    /**
     * 缺省桶名称。
     */
    private String bucketName;
}
