package io.wangk.peekaboo.common.flow.service;

import io.wangk.peekaboo.common.core.base.service.IBaseService;
import io.wangk.peekaboo.common.flow.model.FlowTransProducer;

import java.util.List;
import java.util.Set;

/**
 * 流程引擎审批操作的生产者流水服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface FlowTransProducerService extends IBaseService<FlowTransProducer, Long> {

    /**
     * 保存新数据。
     *
     * @param data 数据对象。
     * @return 更新后的对象。
     */
    FlowTransProducer saveNew(FlowTransProducer data);

    /**
     * 根据流程实例Id集合查询，每个流程实例关联的生产者流水数据。
     *
     * @param processInstanceIds 流程实例Id集合。
     * @return 每个流程实例关联的生产者流水数据。
     */
    List<FlowTransProducer> getListByProcessInstanceIds(Set<String> processInstanceIds);

    /**
     * 根据流程实例Id和任务Id查询关联的生产者流水数据。
     *
     * @param processInstanceId 流程实例Id。
     * @param taskId            流程任务Id。
     * @return 关联的生产者流水数据。
     */
    FlowTransProducer getByProcessInstanceIdAndTaskId(String processInstanceId, String taskId);

    /**
     * 删除指定流程实例Id的生产者流水数据。
     *
     * @param processInstanceId 流程实例Id。
     */
    void deleteByProcessInstanceId(String processInstanceId);
}
