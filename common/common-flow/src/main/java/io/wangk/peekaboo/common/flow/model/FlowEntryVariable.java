package io.wangk.peekaboo.common.flow.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * 流程变量实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_flow_entry_variable")
public class FlowEntryVariable {

    /**
     * 主键Id。
     */
    @TableId(value = "variable_id")
    private Long variableId;

    /**
     * 流程Id。
     */
    @TableField(value = "entry_id")
    private Long entryId;

    /**
     * 变量名。
     */
    @TableField(value = "variable_name")
    private String variableName;

    /**
     * 显示名。
     */
    @TableField(value = "show_name")
    private String showName;

    /**
     * 流程变量类型。
     */
    @TableField(value = "variable_type")
    private Integer variableType;

    /**
     * 绑定数据源Id。
     */
    @TableField(value = "bind_datasource_id")
    private Long bindDatasourceId;

    /**
     * 绑定数据源关联Id。
     */
    @TableField(value = "bind_relation_id")
    private Long bindRelationId;

    /**
     * 绑定字段Id。
     */
    @TableField(value = "bind_column_id")
    private Long bindColumnId;

    /**
     * 是否内置。
     */
    @TableField(value = "builtin")
    private Boolean builtin;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;
    
    public static FlowEntryVariable createAutoVariable(String variableName, String showName, Integer type) {
        FlowEntryVariable variable = new FlowEntryVariable();
        variable.variableName = variableName;
        variable.showName = showName;
        variable.variableType = type;
        variable.builtin = true;
        return variable;
    }

    public static FlowEntryVariable createFlowVariable(String variableName, String showName) {
        FlowEntryVariable variable = new FlowEntryVariable();
        variable.variableName = variableName;
        variable.showName = showName;
        variable.builtin = true;
        return variable;
    }
}
