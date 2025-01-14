import { DictionaryBase } from './types';

const ClassLevel = new DictionaryBase('班级级别', [
  {
    id: 0,
    name: '初级班',
    symbol: 'LOWER',
  },
  {
    id: 1,
    name: '培优班',
    symbol: 'HIGH',
  },
  {
    id: 2,
    name: '冲刺提分班',
    symbol: 'INCREASE',
  },
  {
    id: 3,
    name: '竞赛班',
    symbol: 'MATH',
  },
]);

const StudentExpLevel = new DictionaryBase('学生经验等级', [
  {
    id: 0,
    name: '初级学员',
    symbol: 'LOWER',
  },
  {
    id: 1,
    name: '中级学员',
    symbol: 'NORMAL',
  },
  {
    id: 2,
    name: '高级学员',
    symbol: 'HIGH',
  },
  {
    id: 3,
    name: '资深学员',
    symbol: 'SENIOR',
  },
]);

const Gender = new DictionaryBase('性别字典', [
  {
    id: 1,
    name: '男',
    symbol: 'MALE',
  },
  {
    id: 0,
    name: '女',
    symbol: 'FEMALE',
  },
]);

const TeacherLevelType = new DictionaryBase('教师职级', [
  {
    id: 0,
    name: '初级教师',
    symbol: 'LOWER',
  },
  {
    id: 1,
    name: '中级教师',
    symbol: 'NORMAL',
  },
  {
    id: 2,
    name: '高级教师',
    symbol: 'HIGH',
  },
]);

const CourseDifficult = new DictionaryBase('课程难度', [
  {
    id: 0,
    name: '容易',
    symbol: 'EASY',
  },
  {
    id: 1,
    name: '普通',
    symbol: 'NORMAL',
  },
  {
    id: 2,
    name: '困难',
    symbol: 'DIFFICULT',
  },
]);

const StudentActionType = new DictionaryBase('学生行为', [
  {
    id: 0,
    name: '充值',
    symbol: 'RECHARGE',
  },
  {
    id: 1,
    name: '购买课程',
    symbol: 'BUY_COURSE',
  },
  {
    id: 2,
    name: '上课签到',
    symbol: 'SIGNIN_COURSE',
  },
  {
    id: 3,
    name: '下课签退',
    symbol: 'SIGNOUT_COURSE',
  },
  {
    id: 4,
    name: '看视频课',
    symbol: 'WATCH_VIDEO',
  },
  {
    id: 5,
    name: '做作业',
    symbol: 'DO_PAPER',
  },
  {
    id: 6,
    name: '刷题',
    symbol: 'REFRESH_EXERCISE',
  },
  {
    id: 7,
    name: '献花',
    symbol: 'PRESENT_FLOWER',
  },
  {
    id: 8,
    name: '购买视频课',
    symbol: 'BUY_VIDEO_COURSE',
  },
  {
    id: 9,
    name: '购买鲜花',
    symbol: 'BUY_FLOWER',
  },
  {
    id: 10,
    name: '购买作业',
    symbol: 'BUY_PAPER',
  },
]);

export {
  ClassLevel,
  StudentExpLevel,
  Gender,
  TeacherLevelType,
  CourseDifficult,
  StudentActionType,
};
