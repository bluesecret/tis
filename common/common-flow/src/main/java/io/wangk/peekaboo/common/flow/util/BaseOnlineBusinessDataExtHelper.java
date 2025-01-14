package io.wangk.peekaboo.common.flow.util;

import cn.hutool.core.lang.Assert;
import io.wangk.peekaboo.common.flow.base.service.BaseFlowOnlineService;
import io.wangk.peekaboo.common.flow.model.FlowWorkOrder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 面向在线表单工作流的业务数据扩展帮助实现类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Setter
public class BaseOnlineBusinessDataExtHelper {

    private BaseFlowOnlineService onlineBusinessService;

    /**
     * 更新在线表单主表数据的流程状态字段值。
     *
     * @param workOrder 工单对象。
     */
    public void updateFlowStatus(FlowWorkOrder workOrder) {
        Assert.notNull(workOrder.getOnlineTableId());
        if (this.onlineBusinessService != null && workOrder.getBusinessKey() != null) {
            onlineBusinessService.updateFlowStatus(workOrder);
        }
    }

    /**
     * 根据工单对象级联删除业务数据。
     *
     * @param workOrder 工单对象。
     */
    public void deleteBusinessData(FlowWorkOrder workOrder) {
        Assert.notNull(workOrder.getOnlineTableId());
        if (this.onlineBusinessService != null && workOrder.getBusinessKey() != null) {
            onlineBusinessService.deleteBusinessData(workOrder);
        }
    }
}
