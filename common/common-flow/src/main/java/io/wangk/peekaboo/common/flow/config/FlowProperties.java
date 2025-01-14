package io.wangk.peekaboo.common.flow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 工作流的配置对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@ConfigurationProperties(prefix = "common-flow")
public class FlowProperties {

    /**
     * 工作落工单操作接口的URL前缀。
     */
    private String urlPrefix;
    /**
     * 流程工单的打印接口路径。
     */
    private String printUrlPath;
}
