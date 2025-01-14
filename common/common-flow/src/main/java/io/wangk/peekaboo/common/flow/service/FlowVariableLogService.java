package io.wangk.peekaboo.common.flow.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.flow.model.FlowVariableLog;

import java.util.List;
import java.util.Map;

/**
 * 流程请求日志操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface FlowVariableLogService extends IBaseService<FlowVariableLog, Long> {

    /**
     * 保存流程任务变量日志对象。
     *
     * @param flowVariableLog 流程任务变量日志对象。
     */
    void saveNew(FlowVariableLog flowVariableLog);

    /**
     * 获取指定流程实例和任务标识集合的变量日志。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskKeys          流程任务标识集合。
     * @return 键是taskKey，值是对应的变量数据。
     */
    Map<String, String> getVariableMap(String processInstanceId, List<String> taskKeys);
}
