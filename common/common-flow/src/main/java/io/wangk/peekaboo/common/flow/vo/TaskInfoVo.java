package io.wangk.peekaboo.common.flow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * 流程任务信息Vo对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Schema(description = "流程任务信息Vo对象")
@Data
public class TaskInfoVo {

    /**
     * 流程节点任务类型。具体值可参考FlowTaskType常量值。
     */
    @Schema(description = "流程节点任务类型")
    private Integer taskType;

    /**
     * 指定人。
     */
    @Schema(description = "指定人")
    private String assignee;

    /**
     * 任务标识。
     */
    @Schema(description = "任务标识")
    private String taskKey;

    /**
     * 是否分配给当前登录用户的标记。
     * 当该值为true时，登录用户启动流程时，就自动完成了第一个用户任务。
     */
    @Schema(description = "是否分配给当前登录用户的标记")
    private Boolean assignedMe;

    /**
     * 动态表单Id。
     */
    @Schema(description = "动态表单Id")
    private Long formId;

    /**
     * PC端静态表单路由。
     */
    @Schema(description = "PC端静态表单路由")
    private String routerName;

    /**
     * 移动端静态表单路由。
     */
    @Schema(description = "移动端静态表单路由")
    private String mobileRouterName;

    /**
     * 候选组类型。
     */
    @Schema(description = "候选组类型")
    private String groupType;

    /**
     * 只读标记。
     */
    @Schema(description = "只读标记")
    private Boolean readOnly;

    /**
     * 前端所需的操作列表。
     */
    @Schema(description = "前端所需的操作列表")
    List<JSONObject> operationList;

    /**
     * 任务节点的自定义变量列表。
     */
    @Schema(description = "任务节点的自定义变量列表")
    List<JSONObject> variableList;
}
