package io.wangk.peekaboo.common.log.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 操作日志记录表
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "操作日志Dto")
@Data
public class SysOperationLogDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long logId;

    /**
     * 操作类型。
     * 常量值定义可参考SysOperationLogType对象。
     */
    @Schema(description = "操作类型")
    private Integer operationType;

    /**
     * 每次请求的Id。
     * 对于微服务之间的调用，在同一个请求的调用链中，该值是相同的。
     */
    @Schema(description = "每次请求的Id")
    private String traceId;

    /**
     * HTTP 请求地址。
     */
    @Schema(description = "HTTP 请求地址")
    private String requestUrl;

    /**
     * 应答状态。
     */
    @Schema(description = "应答状态")
    private Boolean success;

    /**
     * 操作员名称。
     */
    @Schema(description = "操作员名称")
    private String operatorName;

    /**
     * 调用时长最小值。
     */
    @Schema(description = "调用时长最小值")
    private Long elapseMin;

    /**
     * 调用时长最大值。
     */
    @Schema(description = "调用时长最大值")
    private Long elapseMax;

    /**
     * 操作开始时间。
     */
    @Schema(description = "操作开始时间")
    private String operationTimeStart;

    /**
     * 操作开始时间。
     */
    @Schema(description = "操作开始时间")
    private String operationTimeEnd;
}
