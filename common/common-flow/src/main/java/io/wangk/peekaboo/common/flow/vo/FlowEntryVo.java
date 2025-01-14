package io.wangk.peekaboo.common.flow.vo;

import io.wangk.peekaboo.common.flow.object.AutoTaskVariable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 流程的Vo对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "流程的Vo对象")
@Data
public class FlowEntryVo {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    private Long entryId;

    /**
     * 应用编码。为空时，表示非第三方应用接入。
     */
    @Schema(description = "应用编码")
    private String appCode;

    /**
     * 流程名称。
     */
    @Schema(description = "流程名称")
    private String processDefinitionName;

    /**
     * 流程标识Key。
     */
    @Schema(description = "流程标识Key")
    private String processDefinitionKey;

    /**
     * 流程分类。
     */
    @Schema(description = "流程分类")
    private Long categoryId;

    /**
     * 工作流部署的发布主版本Id。
     */
    @Schema(description = "工作流部署的发布主版本Id")
    private Long mainEntryPublishId;

    /**
     * 最新发布时间。
     */
    @Schema(description = "最新发布时间")
    private Date latestPublishTime;

    /**
     * 流程状态。
     */
    @Schema(description = "流程状态")
    private Integer status;

    /**
     * 流程定义的xml。
     */
    @Schema(description = "流程定义的xml")
    private String bpmnXml;

    /**
     * 流程图类型。0: 普通流程图，1: 钉钉风格的流程图。
     */
    @Schema(description = "流程图类型。0: 普通流程图，1: 钉钉风格的流程图")
    private Integer diagramType;

    /**
     * 绑定表单类型。
     */
    @Schema(description = "绑定表单类型")
    private Integer bindFormType;

    /**
     * 流程类型。
     */
    @Schema(description = "流程类型")
    private Integer flowType;

    /**
     * 自动化流程的参数。
     */
    @Schema(description = "自动化流程的参数")
    private String autoParamJson;

    /**
     * 在线表单的页面Id。
     */
    @Schema(description = "在线表单的页面Id")
    private Long pageId;

    /**
     * 在线表单Id。
     */
    @Schema(description = "在线表单Id")
    private Long defaultFormId;

    /**
     * 在线表单的缺省路由名称。
     */
    @Schema(description = "在线表单的缺省路由名称")
    private String defaultRouterName;

    /**
     * 工单表编码字段的编码规则，如果为空则不计算工单编码。
     */
    @Schema(description = "工单表编码字段的编码规则")
    private String encodedRule;

    /**
     * 流程的自定义扩展数据(JSON格式)。
     */
    @Schema(description = "流程的自定义扩展数据")
    private String extensionData;

    /**
     * 更新时间。
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 更新者Id。
     */
    @Schema(description = "更新者Id")
    private Long updateUserId;

    /**
     * 创建时间。
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 创建者Id。
     */
    @Schema(description = "创建者Id")
    private Long createUserId;

    /**
     * categoryId 的一对一关联数据对象，数据对应类型为FlowCategoryVo。
     */
    @Schema(description = "categoryId 的一对一关联数据对象")
    private Map<String, Object> flowCategory;

    /**
     * mainEntryPublishId 的一对一关联数据对象，数据对应类型为FlowEntryPublishVo。
     */
    @Schema(description = "mainEntryPublishId 的一对一关联数据对象")
    private Map<String, Object> mainFlowEntryPublish;

    /**
     * 关联的在线表单列表。
     */
    @Schema(description = "关联的在线表单列表")
    private List<Map<String, Object>> formList;

    /**
     * 自动化流程任务变量列表。
     */
    @Schema(description = "自动化流程任务变量列表")
    private List<AutoTaskVariable> autoTaskVariableList;
}
