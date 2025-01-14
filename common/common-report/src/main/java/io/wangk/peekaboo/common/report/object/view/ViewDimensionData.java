package io.wangk.peekaboo.common.report.object.view;

import lombok.Data;

/**
 * 视图显示的维度数据对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ViewDimensionData {

    /**
     * 列Id。
     */
    private Long columnId;

    /**
     * 应用名称。
     */
    private String showName;

    /**
     * 排序类型。
     */
    private Integer orderType;
}
