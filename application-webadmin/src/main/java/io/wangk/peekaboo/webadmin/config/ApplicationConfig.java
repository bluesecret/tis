package io.wangk.peekaboo.webadmin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 应用程序自定义的程序属性配置文件。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationConfig {
    /**
     * 用户密码被重置之后的缺省密码
     */
    private String defaultUserPassword;
    /**
     * 上传文件的基础目录
     */
    private String uploadFileBaseDir;
    /**
     * 授信ip列表，没有填写表示全部信任。多个ip之间逗号分隔，如: http://10.10.10.1:8080,http://10.10.10.2:8080
     */
    private String credentialIpList;
    /**
     * Session的用户权限在Redis中的过期时间(秒)。一定要和sa-token.timeout
     * 缺省值是 one day
     */
    private int sessionExpiredSeconds = 86400;
    /**
     * 是否排他登录。
     */
    private Boolean excludeLogin = false;
}
