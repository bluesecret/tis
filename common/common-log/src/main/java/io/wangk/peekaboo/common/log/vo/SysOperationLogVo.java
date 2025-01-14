package io.wangk.peekaboo.common.log.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 操作日志记录表
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "操作日志VO")
@Data
public class SysOperationLogVo {

    /**
     * 操作日志主键Id。
     */
    @Schema(description = "操作日志主键Id")
    private Long logId;

    /**
     * 日志描述。
     */
    @Schema(description = "日志描述")
    private String description;

    /**
     * 操作类型。
     * 常量值定义可参考SysOperationLogType对象。
     */
    @Schema(description = "操作类型")
    private Integer operationType;

    /**
     * 接口所在服务名称。
     * 通常为spring.application.name配置项的值。
     */
    @Schema(description = "接口所在服务名称")
    private String serviceName;

    /**
     * 调用的controller全类名。
     * 之所以为独立字段，是为了便于查询和统计接口的调用频度。
     */
    @Schema(description = "调用的controller全类名")
    private String apiClass;

    /**
     * 调用的controller中的方法。
     * 格式为：接口类名 + "." + 方法名。
     */
    @Schema(description = "调用的controller中的方法")
    private String apiMethod;

    /**
     * 用户会话sessionId。
     * 主要是为了便于统计，以及跟踪查询定位问题。
     */
    @Schema(description = "用户会话sessionId")
    private String sessionId;

    /**
     * 每次请求的Id。
     * 对于微服务之间的调用，在同一个请求的调用链中，该值是相同的。
     */
    @Schema(description = "每次请求的Id")
    private String traceId;

    /**
     * 调用时长。
     */
    @Schema(description = "调用时长")
    private Long elapse;

    /**
     * HTTP 请求方法，如GET。
     */
    @Schema(description = "HTTP 请求方法")
    private String requestMethod;

    /**
     * HTTP 请求地址。
     */
    @Schema(description = "HTTP 请求地址")
    private String requestUrl;

    /**
     * controller接口参数。
     */
    @Schema(description = "controller接口参数")
    private String requestArguments;

    /**
     * controller应答结果。
     */
    @Schema(description = "controller应答结果")
    private String responseResult;

    /**
     * 请求IP。
     */
    @Schema(description = "请求IP")
    private String requestIp;

    /**
     * 应答状态。
     */
    @Schema(description = "应答状态")
    private Boolean success;

    /**
     * 错误信息。
     */
    @Schema(description = "错误信息")
    private String errorMsg;

    /**
     * 租户Id。
     * 仅用于多租户系统，是便于进行对租户的操作查询和统计分析。
     */
    @Schema(description = "租户Id")
    private Long tenantId;

    /**
     * 操作员Id。
     */
    @Schema(description = "操作员Id")
    private Long operatorId;

    /**
     * 操作员名称。
     */
    @Schema(description = "操作员名称")
    private String operatorName;

    /**
     * 操作时间。
     */
    @Schema(description = "操作时间")
    private Date operationTime;
}
