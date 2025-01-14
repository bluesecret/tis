package io.wangk.peekaboo.common.dbutil.provider;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OpenGauss JDBC配置。
 *
 * @author wangk
 * @date 2025-01-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OpenGaussSqlConfig extends JdbcConfig {

    /**
     * JDBC 驱动名。
     */
    private String driver = "org.opengauss.Driver";

    /**
     * 获取拼好后的JDBC连接串。
     *
     * @return 拼好后的JDBC连接串。
     */
    @Override
    public String getJdbcConnectionString() {
        StringBuilder sb = new StringBuilder(512);
        sb.append("jdbc:opengauss://")
                .append(getHost())
                .append(":")
                .append(getPort())
                .append("/")
                .append(getDatabase());
        if (StrUtil.isNotBlank(getSchema())) {
            sb.append("?currentSchema=").append(getSchema());
        } else {
            sb.append("?currentSchema=public");
        }
        sb.append("&TimeZone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8");
        return sb.toString();
    }
}
