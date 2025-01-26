<template>

</template>

<script setup lang="ts">
const activeName = ref('about');
const tabPaneList = ref([
  {
    title: 'PC 端在线表单',
    tableData: [
      {
        menuName: '在线工单示例 / 合同审批',
        flowPage: '是',
        bindFlow: '合同审批 (在线)',
        caseDesc: '一对多数据多标签展示、会签和加减签、并行网关驳回。',
      },
      {
        menuName: '在线工单示例 / 报销申请',
        flowPage: '是',
        bindFlow: '报销申请 (在线)',
        caseDesc: '钉钉风格流程编辑器。',
      },
      {
        menuName: '在线工单示例 / 自由跳示例',
        flowPage: '是',
        bindFlow: '自由跳演示',
        caseDesc:
          '在流程发布时，橙单做了静态的流程分析，因此可以极为准确高效的执行驳回和自由跳逻辑。',
      },
      {
        menuName: '在线工单示例 / 文章管理',
        flowPage: '否',
        bindFlow: '文章审批',
        caseDesc: '数据列表页面可以多次发起审批流程，支持数据表字段作为下一任务审批人。',
      },
      {
        menuName: '在线表单示例 / 老师管理',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '左树右表页面配置，一对一关联选择组件。',
      },
      {
        menuName: '在线表单示例 / 学生管理',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '新建弹框支持「线框表单」、省市区划级联、批量删除。',
      },
      {
        menuName: '在线表单示例 / 课程管理',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '支持数据表字典和全局编码字典关联，支持一对多从表数据聚合计算显示，如章节数量。',
      },
      {
        menuName: '在线统计页面 / 图表展示',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '统计图表和自定义卡片展示。',
      },
      {
        menuName: '在线统计页面 / 卡片展示',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '自定义卡片、API 数据源和透视表展示。',
      },
      {
        menuName: '在线统计页面 / 下钻展示',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '图表组件「双击」后弹框显示下钻数据。',
      },
    ],
  },
  {
    title: 'PC 端路由表单',
    tableData: [
      {
        menuName: '路由工单示例 / 合同审批',
        flowPage: '是',
        bindFlow: '合同审批',
        caseDesc: '一对多数据多标签展示、会签和加减签、并行网关驳回。',
      },
      {
        menuName: '路由工单示例 / 报销申请',
        flowPage: '是',
        bindFlow: '报销申请',
        caseDesc: '功能完全等同于在线表单的报销申请流程。',
      },
      {
        menuName: '路由表单示例 / 班级管理',
        flowPage: '否',
        bindFlow: '创建班级审批',
        caseDesc:
          '左树右表、多对多级联添加。数据列表页面可以「多次发起审批流程」，列表支持进度条和从表数据聚合计算，如学生数量。',
      },
      {
        menuName: '路由表单示例 / 作业管理',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '列表页面即可打开多对多关联数据列表。',
      },
      {
        menuName: '路由表单示例 / 课程管理',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '支持一对多对多复杂关联、一对多的级联添加，编辑页面支持多标签，和显示权限控制。',
      },
      {
        menuName: '路由表单示例 / 学生管理',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '支持部分更新。',
      },
      {
        menuName: '路由表单示例 / 视频课程',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '支持主子表布局。',
      },
      {
        menuName: '路由统计页面 / 学生行为统计',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '点击表格单元中的统计数字，可「下钻」打开详情列表页面。',
      },
      {
        menuName: '路由统计页面 / 视频统计',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '支持图表展示。',
      },
    ],
  },
  {
    title: '移动端表单功能',
    tableData: [
      {
        menuName: '在线表单 / 合同审批',
        flowPage: '是',
        bindFlow: '合同审批 (在线)',
        caseDesc: '一对多数据多标签展示、会签和加减签、并行网关驳回。',
      },
      {
        menuName: '在线表单 / 课程管理',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '支持数据表字典和全局编码字典关联，支持一对多从表数据级联添加和编辑。',
      },
      {
        menuName: '在线表单 / 报表页面',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '统计图表和自定义卡片展示。',
      },
      {
        menuName: '路由表单 / 课程管理',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '支持一对多的级联添加。',
      },
      {
        menuName: '路由表单 / 班级管理',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '支持多对多的级联添加。',
      },
      {
        menuName: '路由表单 / 合同审批',
        flowPage: '是',
        bindFlow: '合同审批',
        caseDesc: '一对多数据多标签展示、会签和加减签、并行网关驳回。',
      },
      {
        menuName: '路由表单 / 视频统计',
        flowPage: '否',
        bindFlow: '无',
        caseDesc: '支持图表展示。',
      },
    ],
  },
]);

const mainContextHeight = inject('mainContextHeight', ref(200));
const getContentStyle = computed(() => {
  return [{ 'min-height': mainContextHeight.value - 221 - 24 - 16 + 'px' }];
});
</script>

<style scoped>
:deep(.el-collapse) {
  border: none;
}

:deep(.el-divider--horizontal) {
  margin: 0;
}
:deep(.el-tag) {
  padding: 0 30px;
}
:deep(.el-table th) {
  background-color: #f6f7f9 !important;
}
:deep(.el-table th .cell) {
  font-size: 14px !important;
  color: #333 !important;
  font-weight: bold !important;
}
:deep(.el-tabs__active-bar) {
  background-color: inherit;
}
:deep(li::marker) {
  font-size: 18px;
}
:deep(.el-tabs__header.is-left) {
  padding-top: 10px;
}
:deep(.el-tabs__item.is-left) {
  justify-content: left;
}
:deep(.el-tabs__item.is-left li) {
  list-style-type: disc;
}
</style>

<style lang="scss" scoped>
.title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 88px;
  padding-right: 58px;
  padding-left: 25px;
  background: #f7f9fc;
}

.title p {
  height: 40px;
  margin: 0;
  font-size: 18px;
  color: #333;
  line-height: 40px;
  font-weight: bold;
}

.title p span {
  font-size: 20px;
  color: $color-primary;
}

.title-img {
  height: 100%;
}

.about {
  color: #333;
}

:deep(.el-tabs--card > .el-tabs__header .el-tabs__nav) {
  border-radius: 0;
  border-top: none;
}
:deep(.el-tabs--card > .el-tabs__header) {
  height: 35px;
}
:deep(.el-tabs--card > .el-tabs__header .el-tabs__item.is-top) {
  width: 102px;
  height: 35px !important;
  font-size: 14px !important;
  text-align: center;
  background: #f6f7f9;
  border-top: 2px solid #ebebeb !important;
  border-bottom: 1px solid #ebebeb !important;
  line-height: 35px;
}
:deep(.el-tabs--card > .el-tabs__header .el-tabs__item.is-active) {
  color: #f70;
  background-color: #fff;
  border-bottom-color: #fff !important;
  border-top: 2px solid #f70 !important;
}

.item-block {
  padding: 24px 5px 20px 15px;
  border: 1px solid #ebebeb;
  border-radius: 0;
  transition: 0.2s;
}

.item-block :deep(.el-tabs__item) {
  font-size: 14px !important;
}

.item-title {
  font-size: 16px;
  font-weight: bold;
}

.item-list {
  padding-top: 14px;
  padding-left: 4px;
  margin: 0 0 0 2px;
}
.item-list li {
  margin: 5px 0 6px 14px;
  list-style: outside;
}

.item {
  display: flex;
  align-items: center;
  height: 48px;
}

.qrcode {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.qrcode img {
  display: block;
  margin-bottom: 5px;
}
.qrcode .icon-name {
  font-size: 14px;
}

:deep(.el-tabs__nav.is-left) {
  padding: 0 9px;
}
:deep(.el-tabs__item) {
  color: #333;
}
:deep(.el-tabs--left .el-tabs__item.is-left) {
  margin-bottom: 7px !important;
  text-align: left !important;
  list-style-type: disc !important;
}
:deep(.el-tabs--left .el-tabs__header.is-left) {
  float: inherit;
  height: auto;
}
:deep(.cases > .el-tabs__header .el-tabs__item) {
  width: 168px;
  height: 40px;
  line-height: 40px;
}
:deep(.cases > .el-tabs__header .el-tabs__item.is-active) {
  color: #f70 !important;
  background-color: #fdf2e6 !important;
  border-radius: 4px;
  font-weight: normal !important;
}
.cases-content {
  padding: 20px;
}
.cases-content .sub-title {
  font-size: 14px;
  color: #333;
}
:deep(.cases > .el-tabs__content) {
  width: 100%;
}

.qq {
  color: #999;
  font-weight: 400;
}

.content-table :deep(.cell) {
  font-size: 14px !important;
}
:deep(.el-tabs--left .el-tabs__nav-wrap.is-left::after) {
  width: 1px;
}
</style>
