import { DictData } from '@/common/staticDict/types';

interface SysUser {
  // 用户Id
  userId?: number | undefined;
  // 登录用户名
  loginName?: string | undefined;
  // 用户密码
  password?: string | undefined;
  // 用户部门Id
  deptId?: number | undefined;
  deptIdDictMap?: DictData,
  // 用户显示名称
  showName?: string | undefined;
  // 用户类型(0: 管理员 1: 系统管理用户 2: 系统业务用户)
  userType?: number | undefined;
  userTypeDictMap?: DictData,
  // 用户头像的Url
  headImageUrl?: string | undefined;
  // 用户状态(0: 正常 1: 锁定)
  userStatus?: number | undefined;
  userStatusDictMap?: DictData,
  // 用户邮箱
  email?: string | undefined;
  // 用户手机
  mobile?: string | undefined;
  // 第三方授权用户信息，这里是sys_user_auth表的冗余，主要用于发消息的时候提升运行时效率。
  userAuthInfo?: string | undefined;
  // 创建者Id
  createUserId?: number | undefined;
  // 更新者Id
  updateUserId?: number | undefined;
  // 创建时间
  createTime?: string | undefined;
  // 更新时间
  updateTime?: string | undefined;
  // 删除标记(1: 正常 -1: 已删除)
  deletedFlag?: number | undefined;
}

export default SysUser;
