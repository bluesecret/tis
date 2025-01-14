package io.wangk.peekaboo.common.flow.dao;

import io.wangk.peekaboo.common.core.base.dao.BaseDaoMapper;
import io.wangk.peekaboo.common.flow.model.FlowTaskExt;

import java.util.List;

/**
 * 流程任务扩展数据操作访问接口。
 *
 * @author wangk
 * @date 2025-01-14
 */
public interface FlowTaskExtMapper extends BaseDaoMapper<FlowTaskExt> {

    /**
     * 批量插入流程任务扩展信息列表。
     *
     * @param flowTaskExtList 流程任务扩展信息列表。
     */
    void insertList(List<FlowTaskExt> flowTaskExtList);
}
