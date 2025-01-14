package io.wangk.peekaboo.common.flow.object;

import lombok.Data;

import java.util.List;

/**
 * 自动化任务调用HTTP请求的对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
public class AutoHttpRequestInfo {

    public static final String BODY_TYPE_FORMDATA = "formData";
    public static final String BODY_TYPE_RAW = "raw";
    public static final String RAW_TYPE_TEXT = "text";
    public static final String RAW_TYPE_JSON = "json";

    /**
     * 请求地址。
     */
    private String url;
    /**
     * POST/GET/PUT ...
     */
    private String httpMethod;
    /**
     * 请求body的类型。
     */
    private String bodyType;
    /**
     * 仅当bodyType为raw的时候可用。
     */
    private String rawType;
    /**
     * 当bodyType为raw的时候，请求体的数据。
     */
    private String bodyData;
    /**
     * HTTP请求头列表。
     */
    private List<AutoTaskConfig.ValueInfo> headerList;
    /**
     * 仅当bodyType为formData时可用。
     */
    private List<AutoTaskConfig.ValueInfo> formDataList;
    /**
     * url参数。
     */
    private List<AutoTaskConfig.ValueInfo> urlParamList;
}
