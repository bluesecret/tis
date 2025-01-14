package io.wangk.peekaboo.common.flow.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.flow.model.FlowAutoVariableLog;

/**
 * 自动化流程变量操作服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface FlowAutoVariableLogService extends IBaseService<FlowAutoVariableLog, Long> {

    /**
     * 保存数据对象。
     *
     * @param flowAutoVariableLog 数据对象。
     */
    void saveNew(FlowAutoVariableLog flowAutoVariableLog);

    /**
     * 获取指定自动化流程实例的最新变量对象。
     *
     * @param processInstanceId 流程实例Id。
     * @return 自动化流程实例的最新变量对象。
     */
    FlowAutoVariableLog getAutoVariableByProcessInstanceId(String processInstanceId);

    /**
     * 删除指定流程实例的变量日志。
     *
     * @param processInstanceId 流程实例Id。
     */
    void deleteByProcessInstanceId(String processInstanceId);
}
