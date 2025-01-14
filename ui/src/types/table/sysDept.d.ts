import { DictData } from '@/common/staticDict/types';

interface SysDept {
  // 部门Id
  deptId?: number | undefined;
  // 部门名称
  deptName?: string | undefined;
  // 显示顺序
  showOrder?: number | undefined;
  // 父部门Id
  parentId?: number | undefined;
  // 删除标记(1: 正常 -1: 已删除)
  deletedFlag?: number | undefined;
  // 创建者Id
  createUserId?: number | undefined;
  // 更新者Id
  updateUserId?: number | undefined;
  // 创建时间
  createTime?: string | undefined;
  // 更新时间
  updateTime?: string | undefined;
}

export default SysDept;
