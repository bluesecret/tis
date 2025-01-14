package io.wangk.peekaboo.common.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 报表字典过滤参数对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "报表字典过滤参数对象")
@Data
public class ReportDictFilterDto {

    /**
     * 过滤字段名。
     */
    @Schema(description = "过滤字段名")
    private String columnName;

    /**
     * 过滤值。
     */
    @Schema(description = "过滤值")
    private Object columnValue;
}
