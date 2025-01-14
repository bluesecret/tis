package io.wangk.peekaboo.common.report.object.view;

import lombok.Data;

/**
 * 视图字段排序数据对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ViewOrderData {

    /**
     * 列Id。
     */
    private Long columnId;

    /**
     * 计算方式。
     */
    private Integer calculateType;

    /**
     * 排序类型。
     */
    private Integer orderType;
}
