import { RouteLocationNormalizedLoaded, RouteRecordRaw } from 'vue-router';
import Layout from '@/components/layout/index.vue';
import Welcome from '@/pages/welcome/index.vue';
import Login from '@/pages/login/index.vue';
import { useLayoutStore } from '@/store';

function getProps(route: RouteLocationNormalizedLoaded) {
  return route.query;
}

// 系统生成路由
export const routers: Array<RouteRecordRaw> = [
  {
    path: '/login',
    component: Login,
    name: 'login',
    meta: {
      title: '登录',
    },
  },
  {
    path: '/',
    component: Layout,
    name: 'main',
    props: getProps,
    redirect: {
      name: 'welcome',
    },
    meta: {
      title: '主页',
      showOnly: true,
    },
    children: [
      {
        path: 'welcome',
        component: Welcome,
        name: 'welcome',
        meta: { title: '欢迎', keepalive: false },
      },
      {
        path: 'formSysMenu',
        component: () =>
          useLayoutStore().supportColumn
            ? import('@/pages/upms/formSysMenu/formSysColumnMenu.vue')
            : import('@/pages/upms/formSysMenu/index.vue'),
        name: 'formSysMenu',
        meta: { title: '菜单列表', keepalive: true },
      },
      {
        path: 'formSysUser',
        component: () => import('@/pages/upms/formSysUser/index.vue'),
        name: 'formSysUser',
        meta: { title: '用户列表', keepalive: true },
      },
      {
        path: 'formSysDept',
        component: () => import('@/pages/upms/formSysDept/index.vue'),
        name: 'formSysDept',
        meta: { title: '部门列表', keepalive: true },
      },
      {
        path: 'formSysRole',
        component: () => import('@/pages/upms/formSysRole/index.vue'),
        name: 'formSysRole',
        meta: { title: '角色管理', keepalive: true },
      },
      {
        path: 'formSysDataPerm',
        component: () => import('@/pages/upms/formSysDataPerm/index.vue'),
        name: 'formSysDataPerm',
        meta: { title: '数据权限管理', keepalive: true },
      },
      {
        path: 'formSysLoginUser',
        component: () => import('@/pages/upms/formSysLoginUser/index.vue'),
        name: 'formSysLoginUser',
        meta: { title: '在线用户', keepalive: true },
      },
      // 岗位模块路由配置
      {
        path: 'formSysPost',
        component: () => import('@/pages/upms/formSysPost/index.vue'),
        name: 'formSysPost',
        meta: { title: '岗位管理', keepalive: true },
      },
      {
        path: 'formSysDeptPost',
        component: () => import('@/pages/upms/formSysDeptPost/index.vue'),
        name: 'formSysDeptPost',
        props: getProps,
        meta: { title: '设置部门岗位', keepalive: true },
      },
      {
        path: 'formSysDict',
        component: () => import('@/pages/upms/formSysDict/index.vue'),
        name: 'formSysDict',
        meta: { title: '字典管理', keepalive: true },
      },
      {
        path: 'formSysOperationLog',
        component: () => import('@/pages/upms/formSysOperationLog/index.vue'),
        name: 'formSysOperationLog',
        meta: { title: '操作日志', keepalive: true },
      },
      // 404
      {
        path: '/:pathMatch(.*)*',
        component: () => import('@/pages/error/404.vue'),
        name: 'NotFound',
        meta: {
          title: '404',
        },
      },
      // 在线表单
      {
        path: 'formOnlineDblink',
        component: () => import('@/pages/online/formOnlineDblink/index.vue'),
        name: 'formOnlineDblink',
        props: getProps,
        meta: { title: '数据库链接', keepalive: true },
      },
      {
        path: 'formOnlineDict',
        component: () => import('@/pages/online/formOnlineDict/index.vue'),
        name: 'formOnlineDict',
        props: getProps,
        meta: { title: '在线表单字典管理', keepalive: true },
      },
      {
        path: 'formOnlinePage',
        component: () => import('@/pages/online/formOnlinePage/index.vue'),
        name: 'formOnlinePage',
        props: getProps,
        meta: { title: '在线表单管理', keepalive: true },
      },
      {
        path: 'onlineForm',
        component: () => import('@/pages/online/OnlinePageRender/index.vue'),
        name: 'onlineForm',
        props: getProps,
        meta: { title: '在线表单', keepalive: true },
      },
      // 工作流模块
      {
        path: 'formMessage',
        component: () => import('@/pages/workflow/formMessage/index.vue'),
        name: 'formMessage',
        props: getProps,
        meta: { title: '催办消息', keepalive: true },
      },
      {
        path: 'formFlowCategory',
        component: () => import('@/pages/workflow/flowCategory/formFlowCategory.vue'),
        name: 'formFlowCategory',
        props: getProps,
        meta: { title: '流程分类管理', keepalive: true },
      },
      {
        path: 'formFlowEntry',
        component: () => import('@/pages/workflow/flowEntry/formFlowEntry.vue'),
        name: 'formFlowEntry',
        props: getProps,
        meta: { title: '流程设计', keepalive: true },
      },
      {
        path: 'formFlowDblink',
        component: () => import('@/pages/workflow/formFlowDblink/index.vue'),
        name: 'formFlowDblink',
        props: getProps,
        meta: { title: '流程数据库链接', keepalive: true},
      },
      {
        path: 'formAllInstance',
        component: () => import('@/pages/workflow/taskManager/formAllInstance.vue'),
        name: 'formAllInstance',
        props: getProps,
        meta: { title: '流程实例', keepalive: true },
      },
      {
        path: 'handlerFlowTask',
        component: () => import('@/pages/workflow/handlerFlowTask/index.vue'),
        name: 'handlerFlowTask',
        props: getProps,
        meta: { title: '流程处理' },
        children: [
          // 静态表单路由设置
        ],
      },
      {
        path: 'formMyTask',
        component: () => import('@/pages/workflow/taskManager/formMyTask.vue'),
        name: 'formMyTask',
        props: getProps,
        meta: { title: '我的待办', keepalive: true },
      },
      {
        path: 'formMyApprovedTask',
        component: () => import('@/pages/workflow/taskManager/formMyApprovedTask.vue'),
        name: 'formMyApprovedTask',
        props: getProps,
        meta: { title: '已办任务', keepalive: true },
      },
      {
        path: 'formMyHistoryTask',
        component: () => import('@/pages/workflow/taskManager/formMyHistoryTask.vue'),
        name: 'formMyHistoryTask',
        props: getProps,
        meta: { title: '历史流程', keepalive: true },
      },
      // 移动端管理
      {
        path: 'formSudoku',
        component: () => import('@/pages/mobile/formMobileEntry/formSudoku.vue'),
        name: 'formSudoku',
        props: {
          entryType: 1,
        },
        meta: { title: '九宫格配置', keepalive: true },
      },
      {
        path: 'formBanner',
        component: () => import('@/pages/mobile/formMobileEntry/formBanner.vue'),
        name: 'formBanner',
        props: {
          entryType: 0,
        },
        meta: { title: '轮播图配置配置', keepalive: true},
      },
      // 报表路由
      {
        path: 'reportRender',
        component: () => import('@/pages/report/formReportPage/reportRender/index.vue'),
        name: 'reportRender',
        props: getProps,
        meta: { title: '报表', keepalive: true },
      },
      {
        path: 'formReportDblink',
        component: () => import('@/pages/report/formReportDblink/index.vue'),
        name: 'formReportDblink',
        meta: { title: '报表数据源', keepalive: true },
      },
      {
        path: 'formReportDataset',
        component: () => import('@/pages/report/formReportDataset/index.vue'),
        name: 'formReportDataset',
        meta: { title: '报表数据集', keepalive: true },
      },
      {
        path: 'formReportDict',
        component: () => import('@/pages/report/formReportDict/index.vue'),
        name: 'formReportDict',
        meta: { title: '报表字典', keepalive: true},
      },
      {
        path: 'formReportPage',
        component: () => import('@/pages/report/formReportPage/index.vue'),
        name: 'formReportPage',
        meta: { title: '报表页面', keepalive: true},
      },
      {
        path: 'formPrintManage',
        component: () => import('@/pages/report/formPrint/index.vue'),
        name: 'formPrintManage',
        meta: { title: '打印管理', keepalive: true },
      },
    ],
  },
  // TODO 第三方接入路由
  {
    path: '/thirdParty',
    component: () => import('@/components/thirdParty/index.vue'),
    name: 'thirdParty',
    props: getProps,
    children: [
      // 流程分类列表
      {
        path: 'thirdFormFlowCategory',
        name: 'thirdFormFlowCategory',
        props: getProps,
        component: () => import('@/pages/workflow/flowCategory/formFlowCategory.vue'),
      },
      // 流程分类 新增、编辑
      {
        path: 'thirdAddFormFlowCategory',
        name: 'thirdAddFormFlowCategory',
        props: getProps,
        component: () => import('@/pages/workflow/flowCategory/formEditFlowCategory.vue'),
      },
      // 流程实例列表
      {
        path: 'thirdFormAllInstance',
        name: 'thirdFormAllInstance',
        props: getProps,
        component: () => import('@/pages/workflow/taskManager/formAllInstance.vue'),
      },
      // 流程图
      {
        path: 'thirdFormTaskProcessViewer',
        name: 'thirdFormTaskProcessViewer',
        props: getProps,
        component: () => import('@/pages/workflow/taskManager/formTaskProcessViewer.vue'),
      },
      // 流程干涉
      {
        path: 'thirdFormInterposeInstance',
        name: 'thirdFormInterposeInstance',
        props: getProps,
        component: () => import('@/pages/workflow/taskManager/formInterposeInstance.vue'),
      },
      // 流程复活
      {
        path: 'thirdFormRestartInstance',
        name: 'thirdFormRestartInstance',
        props: getProps,
        component: () => import('@/pages/workflow/taskManager/formRestart.vue'),
      },
      // 数据补偿
      {
        path: 'thirdFormCompensationInstance',
        name: 'thirdFormCompensationInstance',
        props: getProps,
        component: () => import('@/pages/workflow/taskManager/processCompensation.vue'),
      },
      // 流程终止
      {
        path: 'thirdFormStopTaskInstance',
        name: 'thirdFormStopTaskInstance',
        props: getProps,
        component: () => import('@/pages/workflow/taskManager/stopTask.vue'),
      },
      // 选择用户-处理用户
      {
        path: 'thirdTaskUserSelect',
        name: 'thirdTaskUserSelect',
        props: getProps,
        component: () => import('@/pages/workflow/components/TaskUserSelect.vue'),
      },
      // 选择用户-跳转节点
      {
        path: 'thirdSelectUserTask',
        name: 'thirdSelectUserTask',
        props: getProps,
        component: () => import('@/pages/workflow/components/UserTaskSelect/userTaskSelectDlg.vue'),
      },
      // 流程设计
      {
        path: 'thirdFlowDblink',
        name: 'thirdFlowDblink',
        props: getProps,
        component: () => import('@/pages/workflow/formFlowDblink/index.vue'),
      },
      {
        path: 'thirdEditFlowDblink',
        name: 'thirdEditFlowDblink',
        props: getProps,
        component: () => import('@/pages/workflow/formFlowDblink/editFlowDblink.vue'),
      },
      {
        path: 'thirdFormFlowEntry',
        name: 'thirdFormFlowEntry',
        props: getProps,
        component: () => import('@/pages/workflow/flowEntry/formFlowEntry.vue'),
      },
      {
        path: 'thirdFormEditFlowEntry',
        name: 'thirdFormEditFlowEntry',
        props: getProps,
        component: () => import('@/pages/workflow/flowEntry/formEditFlowEntry.vue'),
      },
      {
        path: 'thirdFormPublishedFlowEntry',
        name: 'thirdFormPublishedFlowEntry',
        props: getProps,
        component: () => import('@/pages/workflow/flowEntry/formPublishedFlowEntry.vue'),
      },
      {
        path: 'thirdHandlerFlowTask',
        name: 'thirdHandlerFlowTask',
        props: getProps,
        component: () => import('@/pages/workflow/handlerFlowTask/index.vue'),
      },
      // 流程设计-候选用户组
      {
        path: 'thirdTaskGroupSelect',
        name: 'thirdTaskGroupSelect',
        props: getProps,
        component: () => import('@/pages/workflow/components/TaskGroupSelect.vue'),
      },
      // 流程设计-选择岗位
      {
        path: 'thirdTaskPostSelect',
        name: 'thirdTaskPostSelect',
        props: getProps,
        component: () => import('@/pages/workflow/components/TaskPostSelect.vue'),
      },
      // 流程设计-抄送
      {
        path: 'thirdAddCopyForItem',
        name: 'thirdAddCopyForItem',
        props: getProps,
        component: () => import('@/pages/workflow/components/CopyForSelect/addCopyForItem.vue'),
      },
      // 流程设计-任务按钮
      {
        path: 'thirdEditOperation',
        name: 'thirdEditOperation',
        props: getProps,
        component: () => import('@/pages/workflow/package/refactor/form/formEditOperation.vue'),
      },
      // 流程设计-任务表单权限
      {
        path: 'thirdSetOnlineFormAuth',
        name: 'thirdSetOnlineFormAuth',
        props: getProps,
        component: () => import('@/pages/workflow/package/refactor/form/formSetOnlineFormAuth.vue'),
      },
      // 流程设计-添加变量
      {
        path: 'thirdFormEditFlowEntryVariable',
        name: 'thirdFormEditFlowEntryVariable',
        props: getProps,
        component: () => import('@/pages/workflow/flowEntry/formEditFlowEntryVariable.vue'),
      },
      // 流程设计- 新建状态
      {
        path: 'thirdFormEditFlowEntryStatus',
        name: 'thirdFormEditFlowEntryStatus',
        props: getProps,
        component: () => import('@/pages/workflow/flowEntry/formEditFlowEntryStatus.vue'),
      },
      // 流程设计- 任务提交
      {
        path: 'thirdTaskCommit',
        name: 'thirdTaskCommit',
        props: getProps,
        component: () => import('@/pages/workflow/components/TaskCommit.vue'),
      },
      // 自动化任务 - 编辑过滤条件
      {
        path: 'thirdEditTableFilter',
        name: 'thirdEditTableFilter',
        props: getProps,
        component: () => import('@/pages/workflow/package/refactor/form/editSrcTableFilter.vue'),
      },
      // 自动化任务 - 编辑更新字段
      {
        path: 'thirdEditFieldUpdateData',
        name: 'thirdEditFieldUpdateData',
        props: getProps,
        component: () => import('@/pages/workflow/package/refactor/form/editInsertData.vue'),
      },
      // 自动化任务 - 编辑响应字段
      {
        path: 'thirdEditResponseData',
        name: 'thirdEditResponseData',
        props: getProps,
        component: () => import('@/pages/workflow/package/refactor/form/editResponseData.vue'),
      },
      // 自动化任务 - 编辑HTTP请求参数
      {
        path: 'thirdEditHttpRequestParam',
        name: 'thirdEditHttpRequestParam',
        props: getProps,
        component: () => import('@/pages/workflow/package/refactor/form/editHttpParameter.vue'),
      },
      // 自动化任务 - 编辑HTTP请求头
      {
        path: 'thirdEditHttpHeader',
        name: 'thirdEditHttpHeader',
        props: getProps,
        component: () => import('@/pages/workflow/package/refactor/form/editHttpHeader.vue'),
      },
      // 自动化任务 - 编辑聚合计算字段
      {
        path: 'thirdEditAggregationData',
        name: 'thirdEditAggregationData',
        props: getProps,
        component: () => import('@/pages/workflow/package/refactor/form/editAggregationData.vue'),
      },
      // 自动化任务 - 编辑计算字段
      {
        path: 'thirdEditFormulaData',
        name: 'thirdEditFormulaData',
        props: getProps,
        component: () => import('@/pages/workflow/package/refactor/form/editFormulaData.vue'),
      },
      // 待办任务
      {
        path: 'thirdFormMyTask',
        name: 'thirdFormMyTask',
        props: getProps,
        component: () => import('@/pages/workflow/taskManager/formMyTask.vue'),
      },
      // 历史任务
      {
        path: 'thirdFormMyHistoryTask',
        name: 'thirdFormMyHistoryTask',
        props: getProps,
        component: () => import('@/pages/workflow/taskManager/formMyHistoryTask.vue'),
      },
      // 已办任务
      {
        path: 'thirdFormMyApprovedTask',
        name: 'thirdFormMyApprovedTask',
        props: getProps,
        component: () => import('@/pages/workflow/taskManager/formMyApprovedTask.vue'),
      },
      // 在线表单部分
      {
        path: 'thirdOnlineForm',
        name: 'thirdOnlineForm',
        props: getProps,
        component: () => import('@/pages/online/OnlinePageRender/index.vue'),
      },
      {
        path: 'thirdOnlineEditForm',
        name: 'thirdOnlineEditForm',
        props: getProps,
        component: () => import('@/pages/online/OnlinePageRender/OnlineEditForm/index.vue'),
      },
      {
        path: 'thirdFormOnlineDict',
        name: 'thirdFormOnlineDict',
        props: getProps,
        component: () => import('@/pages/online/formOnlineDict/index.vue'),
      },
      {
        path: 'thirdEditOnlineDict',
        name: 'thirdEditOnlineDict',
        props: getProps,
        component: () => import('@/pages/online/formOnlineDict/EditOnlineDict.vue'),
      },
      {
        path: 'thirdOnlinePage',
        name: 'thirdOnlinePage',
        props: getProps,
        component: () => import('@/pages/online/formOnlinePage/index.vue'),
      },
      {
        path: 'thirdEditOnlinePage',
        name: 'thirdEditOnlinePage',
        props: getProps,
        component: () => import('@/pages/online/editOnlinePage/index.vue'),
      },
      {
        path: 'thirdEditOnlineForm',
        name: 'thirdEditOnlineForm',
        props: getProps,
        component: () => import('@/pages/online/editOnlinePage/editOnlineForm.vue'),
      },
      {
        path: 'thirdEditPageDatasource',
        name: 'thirdEditPageDatasource',
        props: getProps,
        component: () => import('@/pages/online/editOnlinePage/editOnlinePageDatasource.vue'),
      },
      {
        path: 'thirdEditPageRelation',
        name: 'thirdEditPageRelation',
        props: getProps,
        component: () =>
          import('@/pages/online/editOnlinePage/editOnlinePageDatasourceRelation.vue'),
      },
      {
        path: 'thirdSetOnlineTableColumnRule',
        name: 'thirdSetOnlineTableColumnRule',
        props: getProps,
        component: () => import('@/pages/online/editOnlinePage/setOnlineTableColumnRule.vue'),
      },
      {
        path: 'thirdEditVirtualColumnFilter',
        name: 'thirdEditVirtualColumnFilter',
        props: getProps,
        component: () => import('@/pages/online/editOnlinePage/editVirtualColumnFilter.vue'),
      },
      {
        path: 'thirdEditTableColumn',
        name: 'thirdEditTableColumn',
        props: getProps,
        component: () => import('@/pages/online/editOnlinePage/formDesign/editTableColumn.vue'),
      },
      {
        path: 'thirdEditCustomFormOperate',
        name: 'thirdEditCustomFormOperate',
        props: getProps,
        component: () =>
          import('@/pages/online/editOnlinePage/formDesign/components/EditCustomFormOperate.vue'),
      },
      {
        path: 'thirdEditFormField',
        name: 'thirdEditFormField',
        props: getProps,
        component: () =>
          import('@/pages/online/editOnlinePage/formDesign/components/EditFormField.vue'),
      },
      {
        path: 'thirdEditDictParamValue',
        name: 'thirdEditDictParamValue',
        props: getProps,
        component: () =>
          import(
            '@/pages/online/editOnlinePage/formDesign/components/CustomWidgetDictSetting/EditDictParamValue.vue'
          ),
      },
      {
        path: 'thirdEditOnlineTableColumn',
        name: 'thirdEditOnlineTableColumn',
        props: getProps,
        component: () =>
          import(
            '@/pages/online/editOnlinePage/formDesign/components/OnlineTableColumnSetting/editOnlineTableColumn.vue'
          ),
      },
      {
        path: 'thirdEditOnlineTabPanel',
        name: 'thirdEditOnlineTabPanel',
        props: getProps,
        component: () =>
          import(
            '@/pages/online/editOnlinePage/formDesign/components/OnlineTabPanelSetting/editOnlineTabPanel.vue'
          ),
      },
      {
        path: 'thirdOnlineDblink',
        name: 'thirdOnlineDblink',
        props: getProps,
        component: () => import('@/pages/online/formOnlineDblink/index.vue'),
      },
      {
        path: 'thirdEditOnlineDblink',
        name: 'thirdEditOnlineDblink',
        props: getProps,
        component: () => import('@/pages/online/formOnlineDblink/editOnlineDblink.vue'),
      },
      // 打印部分
      {
        path: 'thirdPrint',
        name: 'thirdPrint',
        props: getProps,
        component: () => import('@/pages/report/formPrint/index.vue'),
      },
      {
        path: 'thirdEditPrintGroup',
        name: 'thirdEditPrintGroup',
        props: getProps,
        component: () => import('@/pages/report/formPrint/editPrintManageGroup.vue'),
      },
      {
        path: 'thirdPrintPreview',
        name: 'thirdPrintPreview',
        props: getProps,
        component: () => import('@/pages/report/formPrint/components/previewPrint.vue'),
      },
      {
        path: 'thirdPrintSpreadSheet',
        name: 'thirdPrintSpreadSheet',
        props: getProps,
        component: () => import('@/pages/report/formPrint/components/previewPrintSpreadSheet.vue'),
      },
      {
        path: 'thirdEditPrint',
        name: 'thirdEditPrint',
        props: getProps,
        component: () => import('@/pages/report/formPrint/editPrintTemplate.vue'),
      },
      // 设置数据集参数
      {
        path: 'thirdSetReportDatasetParam',
        name: 'thirdSetReportDatasetParam',
        props: getProps,
        component: () => import('@/pages/report/components/SetReportDatasetParam.vue'),
      },
      // 设置字段过滤
      {
        path: 'thirdEditReportColumnFilter',
        name: 'thirdEditReportColumnFilter',
        props: getProps,
        component: () =>
          import('@/pages/report/components/ReportColumnFilter/editReportColumnFilter.vue'),
      },
      // 设置字段排序
      {
        path: 'thirdEditReportColumnOrder',
        name: 'thirdEditReportColumnOrder',
        props: getProps,
        component: () =>
          import('@/pages/report/components/ReportColumnOrder/editReportColumnOrder.vue'),
      },
      // 数据集
      {
        path: 'thirdReportDataset',
        name: 'thirdReportDataset',
        props: getProps,
        component: () => import('@/pages/report/formReportDataset/index.vue'),
      },
      {
        path: 'thirdEditReportDataset',
        name: 'thirdEditReportDataset',
        props: getProps,
        component: () => import('@/pages/report/formReportDataset/editReportDataset.vue'),
      },
      {
        path: 'thirdEditReportDatasetGroup',
        name: 'thirdEditReportDatasetGroup',
        props: getProps,
        component: () => import('@/pages/report/formReportDataset/editReportDatasetGroup.vue'),
      },
      {
        path: 'thirdEditDatasetParam',
        name: 'thirdEditDatasetParam',
        props: getProps,
        component: () => import('@/pages/report/formReportDataset/components/editDatasetParam.vue'),
      },
      {
        path: 'thirdEditDatasetRelation',
        name: 'thirdEditDatasetRelation',
        props: getProps,
        component: () =>
          import('@/pages/report/formReportDataset/components/editReportRelation.vue'),
      },
      {
        path: 'thirdPrintEditTemplateWord',
        name: 'thirdPrintEditTemplateWord',
        props: getProps,
        component: () =>
          import('@/pages/report/formPrint/components/editTemplateWordDataset.vue')
      },
      {
        path: 'thirdPrintEditTemplateWordColumn',
        name: 'thirdPrintEditTemplateWordColumn',
        props: getProps,
        component: () =>
          import('@/pages/report/formPrint/components/editTemplateWordColumn.vue')
      },
      // 数据库链接
      {
        path: 'thirdReportDblink',
        name: 'thirdReportDblink',
        props: getProps,
        component: () => import('@/pages/report/formReportDblink/index.vue'),
      },
      {
        path: 'thirdEditReportDblink',
        name: 'thirdEditReportDblink',
        props: getProps,
        component: () => import('@/pages/report/formReportDblink/editReportDblink.vue'),
      },
      // 报表字典
      {
        path: 'thirdReportDict',
        name: 'thirdReportDict',
        props: getProps,
        component: () => import('@/pages/report/formReportDict/index.vue'),
      },
      {
        path: 'thirdEditReportDict',
        name: 'thirdEditReportDict',
        props: getProps,
        component: () => import('@/pages/report/formReportDict/editReportDict.vue'),
      },
      // 报表
      {
        path: 'thirdReportPage',
        name: 'thirdReportPage',
        props: getProps,
        component: () => import('@/pages/report/formReportPage/index.vue'),
      },
      {
        path: 'thirdEditReportPageGroup',
        name: 'thirdEditReportPageGroup',
        props: getProps,
        component: () => import('@/pages/report/formReportPage/editReportPageGroup.vue'),
      },
      {
        path: 'thirdEditReportPage',
        name: 'thirdEditReportPage',
        props: getProps,
        component: () => import('@/pages/report/formReportPage/editReportPage/index.vue'),
      },
      {
        path: 'thirdEditReportFormParam',
        name: 'thirdEditReportFormParam',
        props: getProps,
        component: () =>
          import(
            '@/pages/report/formReportPage/reportDesign/components/CustomFormSetting/editFormParam.vue'
          ),
      },
      {
        path: 'thirdEditReportFormOperate',
        name: 'thirdEditReportFormOperate',
        props: getProps,
        component: () =>
          import(
            '@/pages/report/formReportPage/reportDesign/components/CustomReportOperationSetting/editReportOperation.vue'
          ),
      },
      {
        path: 'thirdEditReportRouterOperate',
        name: 'thirdEditReportRouterOperate',
        props: getProps,
        component: () =>
          import(
            '@/pages/report/formReportPage/reportDesign/components/CustomReportOperationSetting/editRouteOperation.vue'
          ),
      },
      {
        path: 'thirdReport',
        name: 'thirdReport',
        props: getProps,
        component: () => import('@/pages/report/formReportPage/reportRender/index.vue'),
      },
      {
        path: 'thirdReportForm',
        name: 'thirdReportForm',
        props: getProps,
        component: () => import('@/pages/report/formReportPage/reportRender/ReportForm/index.vue'),
      },
      // 通用
      {
        path: 'thirdEditDictParamValue2',
        name: 'thirdEditDictParamValue2',
        props: getProps,
        component: () =>
          import(
            '@/online/components/WidgetAttributeSetting/components/CustomWidgetDictSetting/editDictParamValue.vue'
          ),
      },
      {
        path: 'thirdEditOnlineTableColumn2',
        name: 'thirdEditOnlineTableColumn2',
        props: getProps,
        component: () =>
          import(
            '@/online/components/WidgetAttributeSetting/components/OnlineTableColumnSetting/editOnlineTableColumn.vue'
          ),
      },
      {
        path: 'thirdEditOnlineTabPanel2',
        name: 'thirdEditOnlineTabPanel2',
        props: getProps,
        component: () =>
          import(
            '@/online/components/WidgetAttributeSetting/components/OnlineTabPanelSetting/editOnlineTabPanel.vue'
          ),
      },
      {
        path: 'thirdSelectDept',
        name: 'thirdSelectDept',
        props: getProps,
        component: () => import('@/components/DeptSelect/DeptSelectDlg.vue'),
      },
      {
        path: 'thirdSelectUser',
        name: 'thirdSelectUser',
        props: getProps,
        component: () => import('@/components/UserSelect/UserSelectDlg.vue'),
      },
      {
        path: 'thirdSelectData',
        name: 'thirdSelectData',
        props: getProps,
        component: () => import('@/online/components/OnlineCustomDataSelect/DataSelectDlg.vue'),
      },
      {
        path: 'thirdEditDataViewColumn',
        name: 'thirdEditDataViewColumn',
        props: getProps,
        component: () =>
          import(
            '@/online/components/WidgetAttributeSetting/components/ChartDatasetSetting/editDataViewTableColumn.vue'
          ),
      },
      {
        path: 'thirdEditWidgetColumnBind',
        name: 'thirdEditWidgetColumnBind',
        props: getProps,
        component: () =>
          import(
            '@/online/components/WidgetAttributeSetting/components/ChartDatasetSetting/editColumnBind.vue'
          ),
      },
      {
        path: 'thirdSetDatasetPaam',
        name: 'thirdSetDatasetPaam',
        props: getProps,
        component: () => import('@/online/components/SetReportDatasetParam/index.vue'),
      },
      {
        path: 'thirdEditWidgetCategroyColumn',
        name: 'thirdEditWidgetCategroyColumn',
        props: getProps,
        component: () =>
          import(
            '@/online/components/WidgetAttributeSetting/components/ChartDatasetSetting/editWidgetCategoryColumn.vue'
          ),
      },
      {
        path: 'thirdEditWidgetIndexColumn',
        name: 'thirdEditWidgetIndexColumn',
        props: getProps,
        component: () =>
          import(
            '@/online/components/WidgetAttributeSetting/components/ChartDatasetSetting/editWidgetIndexColumn.vue'
          ),
      },
      {
        path: 'thirdEditWidgetOrderInfo',
        name: 'thirdEditWidgetOrderInfo',
        props: getProps,
        component: () =>
          import(
            '@/online/components/WidgetAttributeSetting/components/ChartDatasetSetting/editWidgetOrderInfo.vue'
          ),
      },
      {
        path: 'thirdEditReportColumnFilter',
        name: 'thirdEditReportColumnFilter',
        props: getProps,
        component: () =>
          import(
            '@/online/components/WidgetAttributeSetting/components/ChartDatasetSetting/editReportColumnFilter.vue'
          ),
      },
      {
        path: 'thirdEditCustomEvent',
        name: 'thirdEditCustomEvent',
        props: getProps,
        component: () => import('@/online/components/CustomEventSetting/editCustomEvent.vue'),
      },
    ],
  },
];
