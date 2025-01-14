package io.wangk.peekaboo.common.dbutil.provider;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SQL Server JDBC配置。
 *
 * @author wangk
 * @date 2025-01-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SqlServerConfig extends JdbcConfig {

    /**
     * JDBC 驱动名。
     */
    private String driver = "net.sourceforge.jtds.jdbc.Driver";
    /**
     * 数据库JDBC连接串的扩展部分。
     */
    private String extraParams = ";sslProtocol=TLSv1.2";

    /**
     * 获取拼好后的JDBC连接串。
     *
     * @return 拼好后的JDBC连接串。
     */
    @Override
    public String getJdbcConnectionString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("jdbc:jtds:sqlserver://")
                .append(getHost())
                .append(":")
                .append(getPort())
                .append(";databaseName=")
                .append(getDatabase())
                .append(extraParams);
        return sb.toString();
    }
}
