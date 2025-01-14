package io.wangk.peekaboo.common.flow.config;

import io.wangk.peekaboo.common.core.config.DataSourceContextHolder;
import io.wangk.peekaboo.common.core.constant.ApplicationConstant;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.EngineConfigurator;

/**
 * 服务启动过程中动态切换flowable引擎内置表所在的数据源。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class CustomEngineConfigurator implements EngineConfigurator {

    @Override
    public void beforeInit(AbstractEngineConfiguration engineConfiguration) {
        DataSourceContextHolder.setDataSourceType(ApplicationConstant.COMMON_FLOW_AND_ONLINE_DATASOURCE_TYPE);
    }

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        // 默认实现。
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
