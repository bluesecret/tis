package io.wangk.peekaboo.common.flow.object;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 流程扩展数据对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class FlowEntryExtensionData {

    /**
     * 通知类型。
     */
    private List<String> notifyTypes;
    /**
     * 流程审批状态字典数据列表。Map的key是id和name。
     */
    private List<Map<String, String>> approvalStatusDict;
    /**
     * 级联删除业务数据。
     */
    private Boolean cascadeDeleteBusinessData = false;
    /**
     * 是否支持流程复活。
     */
    private Boolean supportRevive = false;
    /**
     * 复活数据保留天数。0表示永久保留。
     */
    private Integer keptReviveDays = 0;
}
