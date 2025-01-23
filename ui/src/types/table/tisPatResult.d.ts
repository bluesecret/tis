import { DictData } from '@/common/staticDict/types';

interface TisPatResult {
  // 主键Id
  id?: number | undefined;
  // 患者ID
  patId?: number | undefined;
  // 检测项目
  projectName?: string | undefined;
  // 检测结果
  result?: string | undefined;
  // 备用字段1
  remark1?: string | undefined;
  // 备用字段2
  remark2?: string | undefined;
  // 备用字段3
  remark3?: string | undefined;
  // 创建时间
  createTime?: string | undefined;
  // 创建用户
  createUserId?: number | undefined;
  // 修改时间
  updateTime?: string | undefined;
  // 修改用户
  updateUserId?: number | undefined;
}

export default TisPatResult;
