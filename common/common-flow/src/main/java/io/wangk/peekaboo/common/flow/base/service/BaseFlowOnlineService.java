package io.wangk.peekaboo.common.flow.base.service;

import io.wangk.peekaboo.common.flow.model.FlowWorkOrder;

/**
 * 工作流在线表单的服务接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface BaseFlowOnlineService {

    /**
     * 更新在线表单主表数据的流程状态字段值。
     *
     * @param workOrder 工单对象。
     */
    void updateFlowStatus(FlowWorkOrder workOrder);

    /**
     * 根据工单对象级联删除业务数据。
     *
     * @param workOrder 工单对象。
     */
    void deleteBusinessData(FlowWorkOrder workOrder);
}
