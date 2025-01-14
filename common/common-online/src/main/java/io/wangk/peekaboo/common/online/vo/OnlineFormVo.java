package io.wangk.peekaboo.common.online.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 在线表单VO对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单VO对象")
@Data
public class OnlineFormVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long formId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码。为空时，表示非第三方应用接入")
    private String appCode;

    /**
     * 页面Id。
     */
    @Schema(description = "页面Id")
    private Long pageId;

    /**
     * 表单编码。
     */
    @Schema(description = "表单编码")
    private String formCode;

    /**
     * 表单名称。
     */
    @Schema(description = "表单名称")
    private String formName;

    /**
     * 表单类型。
     */
    @Schema(description = "表单类型")
    private Integer formType;

    /**
     * 表单类别。
     */
    @Schema(description = "表单类别")
    private Integer formKind;

    /**
     * 表单主表Id。
     */
    @Schema(description = "表单主表Id")
    private Long masterTableId;

    /**
     * 表单组件JSON。
     */
    @Schema(description = "表单组件JSON")
    private String widgetJson;

    /**
     * 表单参数JSON。
     */
    @Schema(description = "表单参数JSON")
    private String paramsJson;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 创建者。
     */
    @Schema(description = "创建者")
    private Long createUserId;

    /**
     * 更新时间。
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 更新者。
     */
    @Schema(description = "更新者")
    private Long updateUserId;

    /**
     * masterTableId 的一对一关联数据对象，数据对应类型为OnlineTableVo。
     */
    @Schema(description = "asterTableId 的一对一关联数据对象")
    private Map<String, Object> onlineTable;

    /**
     * masterTableId 字典关联数据。
     */
    @Schema(description = "masterTableId 字典关联数据")
    private Map<String, Object> masterTableIdDictMap;

    /**
     * formType 常量字典关联数据。
     */
    @Schema(description = "formType 常量字典关联数据")
    private Map<String, Object> formTypeDictMap;

    /**
     * 当前表单关联的数据源Id集合。
     */
    @Schema(description = "当前表单关联的数据源Id集合")
    private List<Long> datasourceIdList;
}
