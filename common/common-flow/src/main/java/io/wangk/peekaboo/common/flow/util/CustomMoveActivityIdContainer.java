package io.wangk.peekaboo.common.flow.util;

import org.flowable.engine.impl.runtime.MoveActivityIdContainer;

import java.util.List;

/**
 * 自定义移动任务Id的容器类。
 *
 * @author wangk
 * @date 2025-01-14
 */
public class CustomMoveActivityIdContainer extends MoveActivityIdContainer {

    public CustomMoveActivityIdContainer(String singleActivityId, String moveToActivityId) {
        super(singleActivityId, moveToActivityId);
    }

    public CustomMoveActivityIdContainer(List<String> activityIds, List<String> moveToActivityIds) {
        super(activityIds.get(0), moveToActivityIds.get(0));
        this.activityIds = activityIds;
        this.moveToActivityIds = moveToActivityIds;
    }
}
