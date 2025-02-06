<template>
  <div
    class="online-workflow-order-form"
    :style="{ height: height ? height : '100%' }"
    v-show="isReady"
  >
    <el-form
      class="query-filter-box"
      ref="onlineWorkOrder"
      :model="formFilter"
      label-width="80px"
      :size="layoutStore.defaultFormItemSize"
      label-position="right"
      @submit.prevent
    >
      <OnlineFilterBox
        class="query-filter-box"
        :isEdit="dialogParams.isEdit"
        ref="filterBox"
        :itemWidth="form2.filterItemWidth || 350"
        :style="{ 'margin-bottom': dialogParams.isEdit ? '10px' : '0px' }"
        :widgetList="activeWidgetList"
        :formData="formData"
        :hasSlot="true"
        @widgetClick="onWidgetClick"
        @search="refreshTable(true)"
        @reset="onReset"
        @copy="onCopyWidget"
        @delete="onDeleteWidget"
      >
        <el-form-item label="工单状态" prop="flowStatus">
          <el-select
            class="filter-item"
            v-model="formFilter.flowStatus"
            :clearable="true"
            placeholder="工单状态"
          >
            <el-option
              v-for="item in SysFlowWorkOrderStatus.getList()"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="创建日期" prop="createTime">
          <date-range
            class="filter-item"
            v-model="formFilter.createTime"
            :clearable="true"
            :allowTypes="['day']"
            align="left"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="工单编号" prop="workOrderCode">
          <el-input
            class="filter-item"
            v-model="formFilter.workOrderCode"
            :clearable="true"
            placeholder="工单编号"
          />
        </el-form-item>
      </OnlineFilterBox>
    </el-form>
    <el-row
      type="flex"
      justify="space-between"
      style="padding: 16px 24px 0; background-color: white"
    >
      <el-button
        type="primary"
        :icon="Plus"
        :size="layoutStore.defaultFormItemSize"
        :disabled="processDefinitionKey == null"
        @click="onStartFlow()"
      >
        新建
      </el-button>
    </el-row>
    <div class="online-query-form">
      <div
        class="query-table-box custom-widget-item widget-item"
        :class="{ active: isEdit && currentWidget === queryTable }"
        :style="{
          padding: isEdit && currentWidget === queryTable ? '2px' : '0px',
        }"
        @click.stop="onTableClick"
      >
        <OnlineCustomWorkFlowTable
          v-if="isReady"
          :dataList="queryTableWidget.dataList"
          :isEdit="isEdit"
          :widget="queryTable"
          :getTableIndex="queryTableWidget.getTableIndex"
          :sortChange="queryTableWidget.onSortChange"
          @viewWorkOrder="onView"
          @handlerWorkOrder="onSubmit"
          @cancelWorkOrder="onCancelWorkOrder"
          @handlerRemind="onRemindClick"
        />
      </div>
      <el-row
        type="flex"
        justify="end"
        style="margin-top: 16px"
        v-if="queryTable && queryTable.props.paged"
      >
        <el-pagination
          :total="queryTableWidget.totalCount"
          :current-page="queryTableWidget.currentPage"
          :page-size="queryTableWidget.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, prev, pager, next, sizes"
          @current-change="queryTableWidget.onCurrentPageChange"
          @size-change="queryTableWidget.onPageSizeChange"
        />
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import OnlineCustomWorkFlowTable from '@/online/components/OnlineCustomWorkFlowTable.vue';
import { SysOnlineColumnFilterType } from '@/common/staticDict/online';
import {
  OnlineFormEventType,
  SysCustomWidgetBindDataType,
  SysCustomWidgetOperationType,
} from '@/common/staticDict';
import { useTable } from '@/common/hooks/useTable';
import { TableOptions } from '@/common/types/pagination';
import { ANY_OBJECT } from '@/types/generic';
import {
  SysFlowTaskOperationType,
  SysFlowTaskType,
  SysFlowWorkOrderStatus,
} from '@/common/staticDict/flow';
import { FlowEntryController, FlowOperationController } from '@/api/flow';
import widgetData from '@/online/config/index';
import { useLayoutStore } from '@/store';
import { useFormExpose } from '../hooks/useFormExpose';
import { useForm } from '../hooks/useForm';
import { useDict } from '../../hooks/useDict';
import OnlineFilterBox from '../OnlineQueryForm/OnlineFilterBox.vue';

const emit = defineEmits<{
  widgetClick: [ANY_OBJECT | null];
  tableClick: [ANY_OBJECT];
}>();

const router = useRouter();
console.log('router router router', router);

const props = withDefaults(
  defineProps<{
    entryId?: string;
    formConfig: ANY_OBJECT;
    height?: string;
    // 主表数据
    masterTableData?: ANY_OBJECT;
    // 是否表单编辑模式
    isEdit?: boolean;
    // 当前选中组件
    currentWidget?: ANY_OBJECT | null;
    mode?: string;
  }>(),
  {
    isEdit: false,
  },
);

const layoutStore = useLayoutStore();
const { getDictDataList } = useDict();
const {
  isReady,
  dialogParams,
  form: form2,
  formData,
  getWidgetValue,
  getWidgetProp,
  getWidgetVisible,
  onValueChange,
  onWidgetValueChange,
  getDropdownParams,
  checkOperationPermCode,
  checkOperationDisabled,
  checkOperationVisible,
  cloneWidget,
  handlerOperation,
  loadOnlineFormConfig,
  getIgnoreMaskFields,
  onPrint,
  initPage,
  initFormWidgetList,
  initWidgetLinkage,
  loadOnlineDictList,
} = useForm(props);

provide('form', () => {
  return {
    ...form2.value,
    mode: props.mode || 'pc',
    isEdit: dialogParams.value.isEdit,
    checkOperationPermCode: checkOperationPermCode,
    checkOperationDisabled: checkOperationDisabled,
    checkOperationVisible: checkOperationVisible,
    getWidgetValue: getWidgetValue,
    getWidgetProp: getWidgetProp,
    getWidgetVisible: getWidgetVisible,
    onValueChange: onValueChange,
    onWidgetValueChange: onWidgetValueChange,
    getDropdownParams: getDropdownParams,
    cloneWidget: cloneWidget,
    handlerOperation: handlerOperation,
    getDictDataList: getDictDataList,
    loadOnlineDictList: loadOnlineDictList,
    loadOnlineFormConfig: loadOnlineFormConfig,
    isActive: (widget: ANY_OBJECT) => {
      return props.currentWidget === widget;
    },
    getWidgetObject: widgetData.getWidgetObject,
    instanceData: () => useFormExpose(formData, props),
  };
});

const processDefinitionKey = ref<string>();
const processDefinitionName = ref<string>();
const formFilter = reactive<ANY_OBJECT>({
  createTime: [],
  workOrderCode: undefined,
  flowStatus: undefined,
});

const activeWidgetList = computed(() => {
  return form2.value.widgetList;
});

const queryTable = computed(() => {
  return form2.value.tableWidget;
});

const onWidgetClick = (widget: ANY_OBJECT | null) => {
  emit('widgetClick', widget);
};

const onCopyWidget = (widget: ANY_OBJECT) => {
  activeWidgetList.value.push(widget);
};
const onDeleteWidget = (widget: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此组件？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      form2.value.widgetList = form2.value.widgetList.filter((item: ANY_OBJECT) => item !== widget);
      onWidgetClick(null);
    })
    .catch(e => {
      console.warn(e);
    });
};

const getQueryParams = () => {
  if (Array.isArray(activeWidgetList.value)) {
    return activeWidgetList.value
      .map(widget => {
        if (
          widget.bindData.dataType !== SysCustomWidgetBindDataType.Column ||
          widget.column == null
        )
          return null;
        let column = widget.column;
        let paramValue = getWidgetValue(widget);
        if (
          paramValue == null ||
          paramValue === '' ||
          (Array.isArray(paramValue) && paramValue.length === 0)
        )
          return null;
        let temp: ANY_OBJECT = {
          tableName: widget.table.tableName,
          columnName: widget.column.columnName,
          filterType: widget.column.filterType,
          columnValue:
            widget.column.filterType !== SysOnlineColumnFilterType.RANFGE_FILTER
              ? paramValue
              : undefined,
        };

        if (column.filterType === SysOnlineColumnFilterType.RANFGE_FILTER) {
          temp.columnValueStart = paramValue[0];
          temp.columnValueEnd = paramValue[1];
        }

        return temp;
      })
      .filter(item => item != null);
  }
  return [];
};

const loadTableData = (params: ANY_OBJECT) => {
  return new Promise((resolve, reject) => {
    params = {
      ...params,
      filterDtoList: getQueryParams(),
      flowWorkOrderDtoFilter: {
        flowStatus: formFilter.flowStatus,
        workOrderCode: formFilter.workOrderCode,
        createTimeStart: Array.isArray(formFilter.createTime)
          ? formFilter.createTime[0]
          : undefined,
        createTimeEnd: Array.isArray(formFilter.createTime) ? formFilter.createTime[1] : undefined,
      },
    };
    // 脱敏设置
    params.ignoreMaskFields = getIgnoreMaskFields();
    if (
      !props.isEdit &&
      typeof queryTable.value.eventInfo[OnlineFormEventType.BEFORE_LOAD_TABLE_DATA] === 'function'
    ) {
      params = queryTable.value.eventInfo[OnlineFormEventType.BEFORE_LOAD_TABLE_DATA](
        params,
        useFormExpose(formData, props),
      );
    }
    if (params == null) {
      reject();
      return;
    }
    FlowOperationController.listWorkOrder(params, {
      processDefinitionKey: processDefinitionKey.value,
    })
      .then(res => {
        res.data.dataList = res.data.dataList.map(item => {
          let initTaskInfo = item.initTaskInfo == null ? {} : JSON.parse(item.initTaskInfo);
          let runtimeTaskInfo =
            Array.isArray(item.runtimeTaskInfoList) && item.runtimeTaskInfoList.length > 0
              ? item.runtimeTaskInfoList[0]
              : {};
          return {
            ...item,
            flowStatusShowName: SysFlowWorkOrderStatus.getValue(item.flowStatus),
            initTaskInfo,
            runtimeTaskInfo,
          };
        });
        if (
          !props.isEdit &&
          typeof queryTable.value.eventInfo[OnlineFormEventType.AFTER_LOAD_TABLE_DATA] ===
            'function'
        ) {
          res.data.dataList = queryTable.value.eventInfo[OnlineFormEventType.AFTER_LOAD_TABLE_DATA](
            res.data.dataList,
            useFormExpose(formData, props),
          );
        }
        resolve({
          dataList: res.data.dataList,
          totalCount: res.data.totalCount,
        });
      })
      .catch((e: Error) => {
        reject(e);
      });
  });
};
const loadTableDataVerify = () => {
  return true;
};
const tableOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadTableData,
  verifyTableParameter: loadTableDataVerify,
  paged: props.formConfig.tableWidget.props.paged,
};
const queryTableWidget = reactive(useTable(tableOptions));

const onTableClick = () => {
  emit('tableClick', queryTable.value);
};
const onStartFlow = () => {
  if (props.isEdit || processDefinitionKey.value == null) return;
  let params = {
    processDefinitionKey: processDefinitionKey.value,
  };
  FlowOperationController.viewInitialTaskInfo(params)
    .then(res => {
      if (res.data && res.data.taskType === SysFlowTaskType.USER_TASK && res.data.assignedMe) {
        router.push({
          name: res.data.routerName || 'handlerFlowTask',
          query: {
            processDefinitionKey: processDefinitionKey.value,
            formId: res.data.formId,
            routerName: res.data.routerName,
            readOnly: res.data.readOnly,
            taskName: '启动流程',
            isDraft: 'true',
            flowEntryName: processDefinitionName.value,
            operationList: JSON.stringify(
              (res.data.operationList || []).filter((item: ANY_OBJECT) => {
                return (
                  item.type !== SysFlowTaskOperationType.CO_SIGN &&
                  item.type !== SysFlowTaskOperationType.REVOKE &&
                  item.type !== SysFlowTaskOperationType.SIGN_REDUCTION
                );
              }),
            ),
            variableList: JSON.stringify(res.data.variableList),
          },
        });
      } else {
        FlowOperationController.startOnly({
          processDefinitionKey: processDefinitionKey.value,
        })
          .then(() => {
            ElMessage.success('启动成功！');
          })
          .catch((e: Error) => {
            console.warn(e);
          });
      }
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
const onSubmit = (row: ANY_OBJECT) => {
  if (props.isEdit || processDefinitionKey.value == null) return;
  let taskId =
    Array.isArray(row.runtimeTaskInfoList) && row.runtimeTaskInfoList.length > 0
      ? row.runtimeTaskInfoList[0].taskId
      : undefined;
  let params = {
    processInstanceId: row.processInstanceId,
    processDefinitionId: row.processDefinitionId,
    taskId: taskId,
  };

  FlowOperationController.viewRuntimeTaskInfo(params)
    .then(res => {
      if (res.data) {
        router.push({
          name: res.data.routerName || 'handlerFlowTask',
          query: {
            isRuntime: 'true',
            isDraft: row.flowStatus === SysFlowWorkOrderStatus.DRAFT ? 'true' : 'false',
            taskId: taskId,
            processDefinitionKey: row.processDefinitionKey,
            processInstanceId: row.processInstanceId,
            processDefinitionId: row.processDefinitionId,
            formId: res.data.formId,
            routerName: res.data.routerName,
            readOnly: res.data.readOnly,
            taskName: (row.runtimeInitialTask || {}).taskName,
            flowEntryName: row.processDefinitionName,
            processInstanceInitiator: row.processInstanceInitiator,
            operationList: JSON.stringify(
              (res.data.operationList || []).filter(
                (item: ANY_OBJECT) => item.type !== SysFlowTaskOperationType.CO_SIGN,
              ),
            ),
            variableList: JSON.stringify(res.data.variableList),
          },
        });
      }
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
const onView = (row: ANY_OBJECT) => {
  if (props.isEdit || processDefinitionKey.value == null) return;
  let params = {
    processInstanceId: row.processInstanceId,
  };

  FlowOperationController.viewInitialHistoricTaskInfo(params)
    .then(res => {
      if (res.data) {
        router.push({
          name: res.data.routerName || 'handlerFlowTask',
          query: {
            isRuntime: 'false',
            processDefinitionKey: row.processDefinitionKey,
            processInstanceId: row.processInstanceId,
            processDefinitionId: row.processDefinitionId,
            formId: res.data.formId,
            routerName: res.data.routerName,
            readOnly: 'true',
            taskId: row.runtimeTaskInfo.taskId,
            flowEntryName: row.processDefinitionName,
            processInstanceInitiator: row.processInstanceInitiator,
          },
        });
      }
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
const onRemindClick = (row: ANY_OBJECT) => {
  FlowOperationController.remindRuntimeTask({
    workOrderId: row.workOrderId,
  })
    .then(() => {
      ElMessage.success('催办成功');
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
const onCancelWorkOrder = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否撤销此工单？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      let params = {
        workOrderId: row.workOrderId,
        cancelReason: '主动撤销',
      };

      FlowOperationController.cancelWorkOrder(params)
        .then(() => {
          ElMessage.success('撤销成功！');
          onSearch();
        })
        .catch((e: Error) => {
          console.warn(e);
        });
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
const refreshTable = (reloadData = false) => {
  if (props.isEdit) return;
  if (reloadData) {
    queryTableWidget.refreshTable(true, 1);
  } else {
    queryTableWidget.refreshTable();
  }
};
const onReset = () => {
  formFilter.createTime = [];
  formFilter.workOrderCode = undefined;
  formFilter.flowStatus = undefined;
  refreshTable(true);
};
const onSearch = () => {
  refreshTable(true);
};

onMounted(() => {
  isReady.value = false;
  if (!props.isEdit) {
    if (props.entryId) {
      FlowEntryController.viewDict({
        entryId: props.entryId,
      })
        .then(res => {
          processDefinitionKey.value = res.data.processDefinitionKey;
          processDefinitionName.value = res.data.processDefinitionName;
          refreshTable(true);
        })
        .catch((e: Error) => {
          console.warn(e);
        });
    }
  }
  isReady.value = true;
});
</script>

<style scoped>
.online-workflow-order-form {
  display: flex;
  flex-direction: column;
}
.online-workflow-order-form .query-filter-box {
  flex-grow: 0;
  flex-shrink: 0;
  margin-bottom: 16px;
}
.online-query-form {
  display: flex;
  padding: 16px 24px;
  background-color: white;
  flex-direction: column;
  flex: 1;
}
.online-workflow-order-form .query-table-box {
  flex-shrink: 1;
  height: 200px;
  flex-grow: 1;
}
</style>
