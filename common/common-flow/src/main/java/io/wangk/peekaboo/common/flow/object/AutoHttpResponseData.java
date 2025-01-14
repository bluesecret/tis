package io.wangk.peekaboo.common.flow.object;

import lombok.Data;

/**
 * 自动化任务调用HTTP的应答对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class AutoHttpResponseData {

    public static final String STOP_ON_FAIL = "stop";
    public static final String CONTINUE_ON_FAIL = "continue";

    /**
     * 应答成功的HTTP状态码，多个状态码之间逗号分隔。
     */
    private String successStatusCode;
    /**
     * 如果请求状态码为成功，还需要进一步根据应答体中的指定字段，进一步判断是否成功。
     * 如：data.isSuccess。
     */
    private String successBodyField = "success";
    /**
     * 请求体中错误信息字段。
     */
    private String errorMessageBodyField = "errorMessage";
    /**
     * 失败处理类型。
     */
    private String failHandleType;
    /**
     * HTTP请求的应答体定义。
     */
    private String httpResponseBody;
}
