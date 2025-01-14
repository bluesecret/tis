package io.wangk.peekaboo.webadmin.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import io.wangk.peekaboo.common.core.config.BaseMultiDataSourceConfig;
import io.wangk.peekaboo.common.core.config.DynamicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 多数据源配置对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Configuration
@EnableTransactionManagement
@MapperScan(value = {"io.wangk.peekaboo.webadmin.*.dao", "io.wangk.peekaboo.common.*.dao"})
public class MultiDataSourceConfig extends BaseMultiDataSourceConfig {

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.main")
    public DataSource mainDataSource() {
        return super.applyCommonProps(DruidDataSourceBuilder.create().build());
    }

    /**
     * 默认生成的用于保存操作日志的数据源，可根据需求修改。
     * 这里我们还是非常推荐给操作日志使用独立的数据源，这样便于今后的数据迁移。
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.operation-log")
    public DataSource operationLogDataSource() {
        return super.applyCommonProps(DruidDataSourceBuilder.create().build());
    }

    /**
     * 默认生成的用于全局编码字典的数据源，可根据需求修改。
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.global-dict")
    public DataSource globalDictDataSource() {
        return super.applyCommonProps(DruidDataSourceBuilder.create().build());
    }

    /**
     * 默认生成的用于在线表单内部表的数据源，可根据需求修改。
     * 这里我们还是非常推荐使用独立数据源，这样便于今后的服务拆分。
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.common-flow-online")
    public DataSource commonFlowAndOnlineDataSource() {
        return super.applyCommonProps(DruidDataSourceBuilder.create().build());
    }

    /**
     * 默认生成的用于统计打印内部表的数据源，可根据需求修改。
     * 这里我们还是非常推荐使用独立数据源，这样便于今后的服务拆分。
     */
    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.datasource.druid.common-report")
    public DataSource commonReportDataSource() {
        return super.applyCommonProps(DruidDataSourceBuilder.create().build());
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>(1);
        targetDataSources.put(DataSourceType.MAIN, mainDataSource());
        targetDataSources.put(DataSourceType.OPERATION_LOG, operationLogDataSource());
        targetDataSources.put(DataSourceType.GLOBAL_DICT, globalDictDataSource());
        targetDataSources.put(DataSourceType.COMMON_FLOW_AND_ONLINE, commonFlowAndOnlineDataSource());
        targetDataSources.put(DataSourceType.COMMON_REPORT, commonReportDataSource());
        // 如果当前工程支持在线表单，这里请务必保证upms数据表所在数据库为缺省数据源。
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(mainDataSource());
        return dynamicDataSource;
    }
}
