package io.wangk.peekaboo.common.report.object.view;

import io.wangk.peekaboo.common.report.object.ReportFilterParam;
import lombok.Data;

import java.util.List;

/**
 * 视图显示的指标数据对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class ViewIndexData {

    /**
     * 列Id。
     */
    private Long columnId;

    /**
     * 计算方式。
     */
    private Integer calculateType;

    /**
     * 应用名称。
     */
    private String showName;

    /**
     * 排序类型。
     */
    private Integer orderType;

    /**
     * 指标过滤参数。用于Having从句。
     */
    private List<ReportFilterParam> filterParams;
}
