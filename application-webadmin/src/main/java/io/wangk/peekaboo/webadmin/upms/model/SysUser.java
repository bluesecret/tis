package io.wangk.peekaboo.webadmin.upms.model;

import com.baomidou.mybatisplus.annotation.*;
import io.wangk.peekaboo.webadmin.app.model.TisUserDevice;
import io.wangk.peekaboo.webadmin.upms.model.constant.SysUserType;
import io.wangk.peekaboo.webadmin.upms.model.constant.SysUserStatus;
import io.wangk.peekaboo.common.core.upload.UploadStoreTypeEnum;
import io.wangk.peekaboo.common.core.annotation.*;
import io.wangk.peekaboo.common.core.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.List;

/**
 * 用户管理实体对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user")
public class SysUser extends BaseModel {

    /**
     * 用户Id。
     */
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 登录用户名。
     */
    @TableField(value = "login_name")
    private String loginName;

    /**
     * 用户密码。
     */
    @TableField(value = "password")
    private String password;

    /**
     * 用户部门Id。
     */
    @TableField(value = "dept_id")
    private Long deptId;

    /**
     * 用户显示名称。
     */
    @TableField(value = "show_name")
    private String showName;

    /**
     * 用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)。
     */
    @TableField(value = "user_type")
    private Integer userType;

    /**
     * 用户头像的Url。
     */
    @UploadFlagColumn(storeType = UploadStoreTypeEnum.LOCAL_SYSTEM)
    @TableField(value = "head_image_url")
    private String headImageUrl;

    /**
     * 用户状态(0: 正常 1: 锁定)。
     */
    @TableField(value = "user_status")
    private Integer userStatus;

    /**
     * 用户邮箱。
     */
    @TableField(value = "email")
    private String email;

    /**
     * 用户手机。
     */
    @TableField(value = "mobile")
    private String mobile;

    /**
     * 第三方授权用户信息，这里是sys_user_auth表的冗余，主要用于发消息的时候提升运行时效率。。
     */
    @TableField(value = "user_auth_info")
    private String userAuthInfo;

    /**
     * 逻辑删除标记字段(1: 正常 -1: 已删除)。
     */
    @TableLogic
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;

    /**
     * createTime 范围过滤起始值(>=)。
     */
    @TableField(exist = false)
    private String createTimeStart;

    /**
     * createTime 范围过滤结束值(<=)。
     */
    @TableField(exist = false)
    private String createTimeEnd;

    /**
     * 多对多用户部门岗位数据集合。
     */
    @RelationManyToMany(
            relationMasterIdField = "userId",
            relationModelClass = SysUserPost.class)
    @TableField(exist = false)
    private List<SysUserPost> sysUserPostList;

    /**
     * 多对多用户角色数据集合。
     */
    @RelationManyToMany(
            relationMasterIdField = "userId",
            relationModelClass = SysUserRole.class)
    @TableField(exist = false)
    private List<SysUserRole> sysUserRoleList;

    /**
     * 多对多用户设备数据集合。
     */
    @RelationManyToMany(
            relationMasterIdField = "userId",
            relationModelClass = TisUserDevice.class)
    @TableField(exist = false)
    private List<TisUserDevice> tisUserDeviceList;

    /**
     * 多对多用户数据权限数据集合。
     */
    @RelationManyToMany(
            relationMasterIdField = "userId",
            relationModelClass = SysDataPermUser.class)
    @TableField(exist = false)
    private List<SysDataPermUser> sysDataPermUserList;

    @RelationDict(
            masterIdField = "deptId",
            slaveModelClass = SysDept.class,
            slaveIdField = "deptId",
            slaveNameField = "deptName")
    @TableField(exist = false)
    private Map<String, Object> deptIdDictMap;

    @RelationConstDict(
            masterIdField = "userType",
            constantDictClass = SysUserType.class)
    @TableField(exist = false)
    private Map<String, Object> userTypeDictMap;

    @RelationConstDict(
            masterIdField = "userStatus",
            constantDictClass = SysUserStatus.class)
    @TableField(exist = false)
    private Map<String, Object> userStatusDictMap;
}
