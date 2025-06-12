import { DictData } from '@/common/staticDict/types';

interface TisPatInfo {
  // 主键Id
  id?: number | undefined;
  // 姓名
  patName?: string | undefined;
  // 批次号
  batchNo?: string | undefined;
  // 年龄
  age?: string | undefined;
  // 检测项目
  projectId?: string | undefined;
  // 性别
  sex?: string | undefined;
  // 样本编号
  sampleNo?: string | undefined;
  // 患者编号
  patNo?: string | undefined;
  serNo?: string | undefined;
  address?: string | undefined;
  // 样本类型
  sampleType?: string | undefined;
  // 操作人员
  operator?: string | undefined;
  // cotful值
  cutoffVal?: string | undefined;
  // 范围
  rangeVal?: string | undefined;
  // 患者卡条图片路径
  picPath?: string | undefined;
  // 检测时间
  testTime?: string | undefined;
  // 检测单位
  testUnit?: string | undefined;
  // 检测状态
  testStat?: string | undefined;
  // txt文件路径
  filePath?: string | undefined;
  // 备用字段1
  remark1?: string | undefined;
  // 备用字段2
  remark2?: string | undefined;
  // 备用字段3
  remark3?: string | undefined;
  // 创建时间
  createTime?: string | undefined;
  // 创建用户
  createdUserId?: number | undefined;
  // 修改时间
  updateTime?: string | undefined;
  // 修改用户
  updateUserId?: number | undefined;
}

export default TisPatInfo;
