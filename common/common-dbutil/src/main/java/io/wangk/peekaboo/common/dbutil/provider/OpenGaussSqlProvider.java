package io.wangk.peekaboo.common.dbutil.provider;

import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.dbutil.constant.DblinkType;

/**
 * OpenGauss数据源的提供者实现类。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class OpenGaussSqlProvider extends PostgreSqlProvider {

    @Override
    public int getDblinkType() {
        return DblinkType.OPENGAUSS;
    }

    @Override
    public JdbcConfig getJdbcConfig(String configuration) {
        return JSON.parseObject(configuration, OpenGaussSqlConfig.class);
    }
}
