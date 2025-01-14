package io.wangk.peekaboo.common.flow.util;

import org.flowable.engine.impl.RuntimeServiceImpl;
import org.flowable.engine.impl.runtime.ChangeActivityStateBuilderImpl;
import org.flowable.engine.runtime.ChangeActivityStateBuilder;

import java.util.List;

/**
 * 自定义修改活动状态构建器实现。主要用于支持多个源节点向多个目标节点跳转的功能。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class CustomChangeActivityStateBuilderImpl extends ChangeActivityStateBuilderImpl {

    public CustomChangeActivityStateBuilderImpl() {
        super();
    }

    public CustomChangeActivityStateBuilderImpl(RuntimeServiceImpl runtimeService) {
        super(runtimeService);
    }

    public ChangeActivityStateBuilder moveActivityIdsToActivityIds(List<String> activityIds, List<String> moveToActivityIds) {
        moveActivityIdList.add(new CustomMoveActivityIdContainer(activityIds, moveToActivityIds));
        return this;
    }
}
