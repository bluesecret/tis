package io.wangk.peekaboo.common.online.dto;

import io.wangk.peekaboo.common.core.validator.ConstDictRef;
import io.wangk.peekaboo.common.core.validator.UpdateGroup;
import io.wangk.peekaboo.common.online.model.constant.FormKind;
import io.wangk.peekaboo.common.online.model.constant.FormType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 在线表单Dto对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "在线表单Dto对象")
@Data
public class OnlineFormDto {

    /**
     * 主键Id。
     */
    @Schema(description = "主键Id")
    @NotNull(message = "数据验证失败，主键Id不能为空！", groups = {UpdateGroup.class})
    private Long formId;

    /**
     * 页面id。
     */
    @Schema(description = "页面id")
    @NotNull(message = "数据验证失败，页面id不能为空！")
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
    @NotBlank(message = "数据验证失败，表单名称不能为空！")
    private String formName;

    /**
     * 表单类别。
     */
    @Schema(description = "表单类别")
    @NotNull(message = "数据验证失败，表单类别不能为空！")
    @ConstDictRef(constDictClass = FormKind.class, message = "数据验证失败，表单类别为无效值！")
    private Integer formKind;

    /**
     * 表单类型。
     */
    @Schema(description = "表单类型")
    @NotNull(message = "数据验证失败，表单类型不能为空！")
    @ConstDictRef(constDictClass = FormType.class, message = "数据验证失败，表单类型为无效值！")
    private Integer formType;

    /**
     * 表单主表id。
     */
    @Schema(description = "表单主表id")
    @NotNull(message = "数据验证失败，表单主表id不能为空！")
    private Long masterTableId;

    /**
     * 当前表单关联的数据源Id集合。
     */
    @Schema(description = "当前表单关联的数据源Id集合")
    private List<Long> datasourceIdList;

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
}
