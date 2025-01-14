package io.wangk.peekaboo.common.report.object.view;

import lombok.Data;

/**
 * 视图显示的过滤数据对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ViewFilterData {

    /**
     * 列Id。
     */
    private Long columnId;

    /**
     * 过滤类型(in eq ...)。
     */
    private Integer filterType;

    /**
     * 值类型。
     */
    private Integer valueType;

    /**
     * 值数据。
     */
    private Integer valueJson;
}
