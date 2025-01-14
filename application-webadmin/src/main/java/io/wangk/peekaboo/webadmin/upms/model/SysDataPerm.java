package io.wangk.peekaboo.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.util.MyCommonUtil;
import io.wangk.peekaboo.common.core.annotation.RelationManyToMany;
import io.wangk.peekaboo.common.core.base.model.BaseModel;
import io.wangk.peekaboo.common.mobile.model.MobileEntryDataPerm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

/**
 * 数据权限实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_data_perm")
public class SysDataPerm extends BaseModel {

    /**
     * 主键Id。
     */
    @TableId(value = "data_perm_id")
    private Long dataPermId;

    /**
     * 显示名称。
     */
    @TableField(value = "data_perm_name")
    private String dataPermName;

    /**
     * 数据权限规则类型(参考DataPermRuleType常量类)。
     */
    @TableField(value = "rule_type")
    private Integer ruleType;

    /**
     * 扩展数据。
     */
    @TableField(value = "extra_data")
    private String extraData;

    @TableField(exist = false)
    private String deptIdListString;

    @RelationManyToMany(
            relationMasterIdField = "dataPermId",
            relationModelClass = SysDataPermDept.class)
    @TableField(exist = false)
    private List<SysDataPermDept> dataPermDeptList;

    @RelationManyToMany(
            relationMasterIdField = "dataPermId",
            relationModelClass = SysDataPermMenu.class)
    @TableField(exist = false)
    private List<SysDataPermMenu> dataPermMenuList;

    @RelationManyToMany(
            relationMasterIdField = "dataPermId",
            relationModelClass = MobileEntryDataPerm.class)
    @TableField(exist = false)
    private List<MobileEntryDataPerm> dataPermMobileEntryList;

    @TableField(exist = false)
    private String searchString;

    public void setSearchString(String searchString) {
        this.searchString = MyCommonUtil.replaceSqlWildcard(searchString);
    }
}
