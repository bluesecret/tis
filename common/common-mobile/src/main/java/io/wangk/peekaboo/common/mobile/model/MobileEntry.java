package io.wangk.peekaboo.common.mobile.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.common.core.annotation.RelationManyToMany;
import io.wangk.peekaboo.common.core.annotation.UploadFlagColumn;
import io.wangk.peekaboo.common.core.upload.UploadStoreTypeEnum;
import io.wangk.peekaboo.common.mobile.object.MobileEntryExtraData;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 移动端入口实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@TableName(value = "zz_mobile_entry")
public class MobileEntry {

    /**
     * 主键Id。
     */
    @TableId(value = "entry_id")
    private Long entryId;

    /**
     * 租户管理端的Id。
     */
    @TableField(value = "tenant_admin_entry_id")
    private Long tenantAdminEntryId;

    /**
     * 租户Id。
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 父Id。
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 显示名称。
     */
    @TableField(value = "entry_name")
    private String entryName;

    /**
     * 入口类型，具体值可参考MobileEntryType常量类。
     */
    @TableField(value = "entry_type")
    private Integer entryType;

    /**
     * 是否对所有角色可见。
     */
    @TableField(value = "common_entry")
    private Boolean commonEntry;

    /**
     * 附件信息。
     */
    @TableField(value = "extra_data")
    private String extraData;

    /**
     * 显示图片。
     */
    @UploadFlagColumn(storeType = UploadStoreTypeEnum.LOCAL_SYSTEM)
    @TableField(value = "image_data")
    private String imageData;

    /**
     * 显示顺序。
     */
    @TableField(value = "show_order")
    private Integer showOrder;

    /**
     * 租户移动端入口对于当前租户是否可用标记。
     */
    @TableField(value = "tenant_available")
    private Boolean tenantAvailable;

    /**
     * 是否为租户自定义移动端入口。
     */
    @TableField(value = "tenant_custom")
    private Boolean tenantCustom;

    /**
     * 创建时间。
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 创建者。
     */
    @TableField(value = "create_user_id")
    private Long createUserId;

    /**
     * 更新时间。
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 更新者。
     */
    @TableField(value = "update_user_id")
    private Long updateUserId;

    @TableField(exist = false)
    private MobileEntryExtraData extraObject;

    @RelationManyToMany(
            relationMasterIdField = "entryId",
            relationModelClass = MobileEntryRole.class)
    @TableField(exist = false)
    private List<MobileEntryRole> mobileEntryRoleList;
}
