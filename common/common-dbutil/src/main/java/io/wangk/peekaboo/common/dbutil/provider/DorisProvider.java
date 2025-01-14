package io.wangk.peekaboo.common.dbutil.provider;

import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.dbutil.constant.DblinkType;

/**
 * Doris数据源的提供者实现类。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class DorisProvider extends MySqlProvider {

    @Override
    public int getDblinkType() {
        return DblinkType.DORIS;
    }

    @Override
    public JdbcConfig getJdbcConfig(String configuration) {
        return JSON.parseObject(configuration, DorisConfig.class);
    }
}