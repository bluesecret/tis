package io.wangk.peekaboo.common.flow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.common.flow.model.constant.FlowBindFormType;
import io.wangk.peekaboo.common.flow.model.constant.FlowEntryStatus;
import io.wangk.peekaboo.common.flow.model.constant.FlowEntryType;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 流程的Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "流程的Dto对象")
@Data
public class FlowEntryDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键不能为空！", groups = {UpdateGroup.class})
    private Long entryId;

    /**
     * 流程名称。
     */
    @Schema(description = "流程名称")
    @NotBlank(message = "数据验证失败，流程名称不能为空！")
    private String processDefinitionName;

    /**
     * 流程标识Key。
     */
    @Schema(description = "流程标识Key")
    @NotBlank(message = "数据验证失败，流程标识Key不能为空！")
    private String processDefinitionKey;

    /**
     * 流程分类。
     */
    @Schema(description = "流程分类")
    @NotNull(message = "数据验证失败，流程分类不能为空！")
    private Long categoryId;

    /**
     * 流程状态。
     */
    @Schema(description = "流程状态")
    @ConstDictRef(constDictClass = FlowEntryStatus.class, message = "数据验证失败，工作流状态为无效值！")
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
    @ConstDictRef(constDictClass = FlowBindFormType.class, message = "数据验证失败，工作流绑定表单类型为无效值！")
    @NotNull(message = "数据验证失败，工作流绑定表单类型不能为空！")
    private Integer bindFormType;

    /**
     * 流程类型。
     */
    @Schema(description = "流程类型")
    @ConstDictRef(constDictClass = FlowEntryType.class, message = "数据验证失败，工作流绑定表单类型为无效值！")
    @NotNull(message = "数据验证失败，流程类型不能为空！")
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
     * 在线表单的缺省路由名称。
     */
    @Schema(description = "在线表单的缺省路由名称")
    private String defaultRouterName;

    /**
     * 在线表单Id。
     */
    @Schema(description = "在线表单Id")
    private Long defaultFormId;

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
}
