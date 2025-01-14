package io.wangk.peekaboo.common.flow.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.flow.model.FlowMessageIdentityOperation;
import org.apache.ibatis.annotations.Param;

/**
 * 流程任务消息所属用户的操作数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface FlowMessageIdentityOperationMapper extends BaseDaoMapper<FlowMessageIdentityOperation> {

    /**
     * 删除指定流程实例的消息关联数据。
     *
     * @param processInstanceId 流程实例Id。
     */
    void deleteByProcessInstanceId(@Param("processInstanceId") String processInstanceId);
}
