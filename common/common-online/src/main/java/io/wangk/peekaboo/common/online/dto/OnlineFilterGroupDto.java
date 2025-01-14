package io.wangk.peekaboo.common.online.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 在线表单数据过滤分组参数对象，主要用于自定义查询。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单数据过滤分组参数对象")
@Data
public class OnlineFilterGroupDto {

    /**
     * 过滤参数列表。
     */
    @Schema(description = "过滤参数列表")
    private List<OnlineFilterDto> filterList;

    /**
     * 逻辑运算符。
     */
    @Schema(description = "逻辑运算符")
    private String logicOperator;
}
