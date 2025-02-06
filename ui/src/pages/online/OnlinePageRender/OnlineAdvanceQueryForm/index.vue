<template>
  <div class="online-advance-query-form" v-show="isReady">
    <template v-if="dialogParams.fullscreen && !dialogParams.isEdit">
      <el-container>
        <el-header style="height: 72px; background: white">
          <el-row align="middle" style="justify-content: space-between; width: 100%; height: 100%">
            <div style="display: flex; height: 40px; line-height: 40px">
              <i
                class="header-logo online-icon icon-orange-icon"
                style="font-size: 40px; color: #fda834; background: rgb(255 119 0 / 10%)"
              />
              <span style="font-size: 22px; color: #333; font-weight: bold">橙单</span>
              <el-divider direction="vertical" style="height: 26px; margin: 7px 8px" />
              <span style="font-size: 18px; color: #333; font-weight: bold">{{
                form2.formName
              }}</span>
            </div>
            <el-button
              link
              size="default"
              :icon="Close"
              style="font-size: 24px; color: #909399"
              @click="onCancel"
            />
          </el-row>
        </el-header>
        <el-main style="width: 100%; padding: 25px; background: #f9f9f9">
          <el-container style="height: calc(100vh - 122px); padding: 25px; background: white">
            <el-aside
              width="265px"
              class="left-panel"
              v-if="leftWidget != null"
              @click.stop="onLeftWidgetClick"
            >
              <div class="title">
                <el-row align="middle" style="flex-wrap: nowrap">
                  <span class="name">{{ leftWidget.showName }}</span>
                  <el-input
                    class="search round-search"
                    :prefix-icon="Search"
                    v-model="leftFilter"
                    clearable
                    :size="layoutStore.defaultFormItemSize"
                    style="border-radius: 16px"
                    :placeholder="'输入' + leftWidget.showName"
                  />
                </el-row>
              </div>
              <el-scrollbar class="custom-scroll tree" style="flex-grow: 1">
                <div
                  class="widget-item"
                  :class="{ active: dialogParams.isEdit && currentWidget === leftWidget }"
                >
                  <OnlineCustomWidget
                    :widget="leftWidget"
                    :ref="leftWidget.variableName"
                    :value="getWidgetValue(leftWidget)"
                    @input="val => onValueChange(leftWidget, val as ANY_OBJECT)"
                    @change="onLeftWidgetChange"
                  />
                </div>
              </el-scrollbar>
            </el-aside>
            <el-main class="table-panel" style="margin-left: 15px">
              <el-row
                class="query-filter-box"
                type="flex"
                justify="end"
                style="margin-bottom: 20px"
              >
                <el-button
                  class="table-operation"
                  v-if="operationVisible(SysCustomWidgetOperationType.BATCH_DELETE)"
                  :size="layoutStore.defaultFormItemSize"
                  :type="getOperation(SysCustomWidgetOperationType.BATCH_DELETE)?.btnType"
                  :plain="getOperation(SysCustomWidgetOperationType.BATCH_DELETE)?.plain"
                  :disabled="operationDisabled(SysCustomWidgetOperationType.BATCH_DELETE)"
                  @click="onOperationClick(getOperation(SysCustomWidgetOperationType.BATCH_DELETE))"
                  :icon="Delete"
                  >{{
                    getOperation(SysCustomWidgetOperationType.BATCH_DELETE)?.name || '批量删除'
                  }}</el-button
                >
                <el-button
                  class="table-operation"
                  v-if="operationVisible(SysCustomWidgetOperationType.EXPORT)"
                  :size="layoutStore.defaultFormItemSize"
                  :type="getOperation(SysCustomWidgetOperationType.EXPORT)?.btnType"
                  :plain="getOperation(SysCustomWidgetOperationType.EXPORT)?.plain"
                  :disabled="operationDisabled(SysCustomWidgetOperationType.EXPORT)"
                  @click="onOperationClick(getOperation(SysCustomWidgetOperationType.EXPORT))"
                  :icon="Download"
                  >{{
                    getOperation(SysCustomWidgetOperationType.EXPORT)?.name || '导出'
                  }}</el-button
                >
                <el-upload
                  class="table-operation"
                  v-if="operationVisible(SysCustomWidgetOperationType.IMPORT)"
                  action=""
                  style="display: inline-block"
                  :show-file-list="false"
                  accept=".xlsx, .xls"
                  :before-upload="onImportClick"
                >
                  <el-button
                    :size="layoutStore.defaultFormItemSize"
                    :type="getOperation(SysCustomWidgetOperationType.IMPORT)?.btnType"
                    :plain="getOperation(SysCustomWidgetOperationType.IMPORT)?.plain"
                    :disabled="operationDisabled(SysCustomWidgetOperationType.IMPORT)"
                    icon="Upload"
                    >{{
                      getOperation(SysCustomWidgetOperationType.IMPORT)?.name || '导入'
                    }}</el-button
                  >
                </el-upload>
                <el-button
                  class="table-operation"
                  v-if="operationVisible(SysCustomWidgetOperationType.PRINT)"
                  :size="layoutStore.defaultFormItemSize"
                  :type="getOperation(SysCustomWidgetOperationType.PRINT)?.btnType"
                  :plain="getOperation(SysCustomWidgetOperationType.PRINT)?.plain"
                  :disabled="operationDisabled(SysCustomWidgetOperationType.PRINT)"
                  @click="onOperationClick(getOperation(SysCustomWidgetOperationType.PRINT))"
                  >{{ getOperation(SysCustomWidgetOperationType.PRINT)?.name || '打印' }}</el-button
                >
                <el-button
                  v-if="operationVisible(SysCustomWidgetOperationType.ADD)"
                  :size="layoutStore.defaultFormItemSize"
                  :type="getOperation(SysCustomWidgetOperationType.ADD)?.btnType"
                  :plain="getOperation(SysCustomWidgetOperationType.ADD)?.plain"
                  :disabled="operationDisabled(SysCustomWidgetOperationType.ADD)"
                  @click="onOperationClick(getOperation(SysCustomWidgetOperationType.ADD))"
                  :icon="Plus"
                  >{{ getOperation(SysCustomWidgetOperationType.ADD)?.name || '新建' }}</el-button
                >
              </el-row>
              <div
                class="query-filter-box"
                style="margin-bottom: 10px"
                v-if="activeWidgetList.length > 0 || isEdit"
                :style="{
                  background: dialogParams.isEdit ? undefined : '#F6F7F9',
                  border: dialogParams.isEdit ? '1px solid #e8eaec' : 'none',
                }"
              >
                <OnlineFilterBox
                  :isEdit="dialogParams.isEdit"
                  ref="filterBox"
                  :isAdvance="true"
                  :itemWidth="form2.filterItemWidth || 350"
                  :widgetList="activeWidgetList"
                  :formData="formData"
                  :operationList="activeOperationList"
                  @widgetClick="onWidgetClick"
                  @search="refreshTable(true)"
                  @reset="onReset"
                  @copy="onCopyWidget"
                  @delete="onDeleteWidget"
                  @operationClick="onOperationClick"
                />
              </div>
              <div class="table-wrap">
                <div
                  class="query-table-box widget-item"
                  :class="{ active: dialogParams.isEdit && currentWidget === queryTable }"
                  style="margin-top: 8px"
                  :style="{ padding: '16px 24px', backgroundColor: 'white' }"
                  @click.stop="onTableClick"
                >
                  <OnlineCustomTable
                    :dataList="queryTableWidget.dataList"
                    :isEdit="dialogParams.isEdit"
                    :widget="queryTable"
                    :multiSelect="batchDelete"
                    :operationList="activeOperationList"
                    :getTableIndex="queryTableWidget.getTableIndex"
                    :sortChange="queryTableWidget.onSortChange"
                    :onSelectChange="onSelectRowChange"
                    :treeConfig="getTableTreeConfig"
                    @refresh="customQueryParams => refreshTable(false, customQueryParams)"
                    @operationClick="onOperationClick"
                  >
                    <template #pagination>
                      <el-row
                        justify="end"
                        style="margin-top: 10px"
                        v-if="queryTable && queryTable.props.paged && getTableTreeConfig == null"
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
                    </template>
                  </OnlineCustomTable>
                </div>
              </div>
            </el-main>
          </el-container>
        </el-main>
      </el-container>
    </template>
    <template v-else>
      <el-container :style="{ height: height ? height : '100%' }">
        <el-aside
          width="265px"
          class="left-panel"
          v-if="leftWidget != null"
          @click.stop="onLeftWidgetClick"
        >
          <div class="title">
            <el-row align="middle" style="flex-wrap: nowrap">
              <span class="name">{{ leftWidget.showName }}</span>
              <el-input
                class="search round-search"
                :prefix-icon="Search"
                v-model="leftFilter"
                clearable
                :size="layoutStore.defaultFormItemSize"
                style="border-radius: 16px"
                :placeholder="'输入' + leftWidget.showName"
              />
            </el-row>
          </div>
          <el-scrollbar class="custom-scroll tree" style="flex-grow: 1">
            <div
              class="widget-item"
              :class="{ active: dialogParams.isEdit && currentWidget === leftWidget }"
            >
              <OnlineCustomWidget
                :widget="leftWidget"
                :ref="leftWidget.variableName"
                :value="getWidgetValue(leftWidget)"
                @input="val => onValueChange(leftWidget, val as ANY_OBJECT)"
                @change="onLeftWidgetChange"
              />
            </div>
          </el-scrollbar>
        </el-aside>
        <el-main class="table-panel" style="overflow: hidden; margin-left: 15px">
          <div
            class="query-filter-box"
            v-if="activeWidgetList.length > 0 || isEdit"
            :style="{
              background: dialogParams.isEdit ? undefined : '#F6F7F9',
              border: dialogParams.isEdit ? '1px solid #e8eaec' : 'none',
            }"
          >
            <OnlineFilterBox
              :isEdit="dialogParams.isEdit"
              ref="filterBox"
              :isAdvance="true"
              style="margin-bottom: 16px"
              :itemWidth="form2.filterItemWidth || 350"
              :widgetList="activeWidgetList"
              :formData="formData"
              :operationList="activeOperationList"
              @widgetClick="onWidgetClick"
              @search="refreshTable(true)"
              @reset="onReset"
              @copy="onCopyWidget"
              @delete="onDeleteWidget"
              @operationClick="onOperationClick"
            />
          </div>
          <div
            class="query-table-box widget-item"
            :class="{ active: dialogParams.isEdit && currentWidget === queryTable }"
            @click.stop="onTableClick"
          >
            <OnlineCustomTable
              :dataList="queryTableWidget.dataList"
              :isEdit="dialogParams.isEdit"
              :widget="queryTable"
              :multiSelect="batchDelete"
              :operationList="activeOperationList"
              :getTableIndex="queryTableWidget.getTableIndex"
              :sortChange="queryTableWidget.onSortChange"
              :onSelectChange="onSelectRowChange"
              :treeConfig="getTableTreeConfig"
              @operationClick="onOperationClick"
              @refresh="customQueryParams => refreshTable(false, customQueryParams)"
            >
              <template #pagination>
                <el-row
                  justify="end"
                  style="margin-top: 10px"
                  v-if="queryTable && queryTable.props.paged && getTableTreeConfig == null"
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
              </template>
            </OnlineCustomTable>
          </div>
        </el-main>
      </el-container>
    </template>
  </div>
</template>

<script setup lang="ts">
import { nextTick } from 'vue';
import { Close, Search, Plus, Delete, Download, Upload } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { findItemFromList, treeDataTranslate } from '@/common/utils';
import OnlineCustomWidget from '@/online/components/OnlineCustomWidget.vue';
import OnlineCustomTable from '@/online/components/OnlineCustomTable.vue';
import { DialogProp } from '@/components/Dialog/types';
import { ANY_OBJECT } from '@/types/generic';
import { useTable } from '@/common/hooks/useTable';
import { TableOptions } from '@/common/types/pagination';
import {
  OnlineFormEventType,
  SysCustomWidgetBindDataType,
  SysCustomWidgetOperationType,
} from '@/common/staticDict';
import { download, post } from '@/common/http/request';
import { SysOnlineColumnFilterType } from '@/common/staticDict/online';
import { TableData } from '@/common/types/table';
import { useUpload } from '@/common/hooks/useUpload';
import widgetData from '@/online/config/index';
import { useLayoutStore } from '@/store';
import { API_CONTEXT } from '@/api/config';
import OnlineFilterBox from '../OnlineQueryForm/OnlineFilterBox.vue';
import { useDict } from '../../hooks/useDict';
import { useForm } from '../hooks/useForm';
import { useFormExpose } from '../hooks/useFormExpose';

const { fetchUpload } = useUpload();

const emit = defineEmits<{
  widgetClick: [ANY_OBJECT | null];
  tableClick: [ANY_OBJECT];
}>();

const props = withDefaults(
  defineProps<{
    formConfig: ANY_OBJECT;
    height?: string;
    // 主表数据
    masterTableData?: ANY_OBJECT;
    // 是否表单编辑模式
    isEdit?: boolean;
    // 是否复制
    isCopy?: boolean;
    readOnly?: boolean;
    // 当前选中组件
    currentWidget?: ANY_OBJECT | null;
    // 需要编辑数据，如果是null则是新增
    rowData?: ANY_OBJECT;
    // 是否全屏弹窗
    fullscreen?: boolean;
    mode?: string;
    // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
    dialog?: DialogProp<ANY_OBJECT>;
  }>(),
  {
    isEdit: false,
    readOnly: false,
    fullscreen: false,
    mode: 'pc',
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
  getPrimaryData,
  getDropdownParams,
  checkOperationPermCode,
  checkOperationDisabled,
  checkOperationVisible,
  cloneWidget,
  handlerOperation,
  loadOnlineFormConfig,
  getTableData,
  setTableData,
  initPage,
  initFormWidgetList,
  getIgnoreMaskFields,
  initWidgetLinkage,
  onPrint,
  onStartFlow,
  loadOnlineDictList,
} = useForm(props);

provide('form', () => {
  return {
    ...form2.value,
    mode: props.mode || 'pc',
    isEdit: dialogParams.value.isEdit,
    readOnly: dialogParams.value.readOnly,
    formData: formData,
    getWidgetValue: getWidgetValue,
    getWidgetProp: getWidgetProp,
    getWidgetVisible: getWidgetVisible,
    onValueChange: onValueChange,
    onWidgetValueChange: onWidgetValueChange,
    getTableData: getTableData,
    setTableData: setTableData,
    getPrimaryData: getPrimaryData,
    getDropdownParams: getDropdownParams,
    checkOperationPermCode: checkOperationPermCode,
    checkOperationDisabled: checkOperationDisabled,
    checkOperationVisible: checkOperationVisible,
    cloneWidget: cloneWidget,
    handlerOperation: handlerOperation,
    getDictDataList: getDictDataList,
    loadOnlineFormConfig: loadOnlineFormConfig,
    loadOnlineDictList: loadOnlineDictList,
    isActive: (widget: ANY_OBJECT) => {
      return props.currentWidget === widget;
    },
    getWidgetObject: widgetData.getWidgetObject,
    filter: {
      name: leftFilter.value,
    },
    instanceData: () => useFormExpose(formData, props),
  };
});

const filterBox = ref();
const selectRows = ref<ANY_OBJECT[]>([]);
const batchDelete = ref(false);
const leftFilter = ref<string>();

const getQueryParamItem = (widget: ANY_OBJECT) => {
  if (widget == null) return;
  if (widget.bindData.dataType !== SysCustomWidgetBindDataType.Column || widget.column == null)
    return;
  let column = widget.column;
  let paramValue = getWidgetValue(widget);
  if (
    paramValue == null ||
    paramValue === '' ||
    (Array.isArray(paramValue) && paramValue.length === 0)
  )
    return;

  const temp: ANY_OBJECT = {
    tableName: widget.table.tableName,
    columnName: widget.column.columnName,
    filterType: widget.column.filterType,
    columnValue:
      widget.column.filterType !== SysOnlineColumnFilterType.RANFGE_FILTER ? paramValue : undefined,
  };

  if (column.filterType === SysOnlineColumnFilterType.RANFGE_FILTER) {
    temp.columnValueStart = paramValue[0];
    temp.columnValueEnd = paramValue[1];
  }

  return temp;
};
const getQueryParams = () => {
  let queryParams: ANY_OBJECT[] = [];
  if (Array.isArray(activeWidgetList.value)) {
    queryParams = activeWidgetList.value
      .map(widget => {
        return getQueryParamItem(widget);
      })
      .filter(item => item != null) as ANY_OBJECT[];
  }

  if (leftWidget.value != null) {
    let temp = getQueryParamItem(leftWidget.value);
    if (temp) queryParams.push(temp);
  }

  return queryParams;
};
const loadTableData = (params: ANY_OBJECT) => {
  return new Promise((resolve, reject) => {
    let table = form2.value.tableMap.get(queryTable.value.bindData.tableId);
    let httpCall = null;
    params.datasourceId = table.datasource.datasourceId;
    params.filterDtoList = getQueryParams();

    if (queryTable.value.relation != null && dialogParams.value.masterTableData != null) {
      // 如果是从主表跳转过来的，加上主表关联字段的过滤
      params.relationId = table.relation.relationId;
      params.filterDtoList.push({
        tableName: queryTable.value.table.tableName,
        columnName: queryTable.value.relation.slaveColumn.columnName,
        filterType: SysOnlineColumnFilterType.EQUAL_FILTER,
        columnValue: (dialogParams.value.masterTableData || {})[
          queryTable.value.relation.masterColumn.columnName
        ],
      });
    }
    // 自定义查询
    if (customQueryParams.value != null) {
      params.customFilterGroupList = customQueryParams.value;
    }
    // 脱敏设置
    params.ignoreMaskFields = getIgnoreMaskFields(queryTable.value);
    if (
      !dialogParams.value.isEdit &&
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

    if (table.relation != null) {
      httpCall = post<TableData<ANY_OBJECT>>(
        API_CONTEXT +
          '/online/onlineOperation/listByOneToManyRelationId/' +
          table.datasource.variableName,
        params,
      );
    } else {
      httpCall = post<TableData<ANY_OBJECT>>(
        API_CONTEXT + '/online/onlineOperation/listByDatasourceId/' + table.datasource.variableName,
        params,
      );
    }

    httpCall
      .then(res => {
        if (
          !dialogParams.value.isEdit &&
          typeof queryTable.value.eventInfo[OnlineFormEventType.AFTER_LOAD_TABLE_DATA] ===
            'function'
        ) {
          res.data.dataList = queryTable.value.eventInfo[OnlineFormEventType.AFTER_LOAD_TABLE_DATA](
            res.data.dataList,
            useFormExpose(formData, props),
          );
        }
        if (
          queryTable.value &&
          queryTable.value.props.treeFlag &&
          queryTable.value.props.parentIdColumn
        ) {
          res.data.dataList = treeDataTranslate(
            res.data.dataList,
            primaryColumnName.value,
            queryTable.value.props.parentIdColumn,
          );
        }
        selectRows.value = [];
        resolve({
          dataList: res.data.dataList,
          totalCount: res.data.totalCount,
        });
      })
      .catch(e => {
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
  paged: (props.formConfig || dialogParams.value.formConfig).tableWidget.props.paged,
};
const queryTableWidget = ref(useTable(tableOptions));

const leftWidget = computed(() => {
  return form2.value.leftWidget;
});
const queryTable = computed<ANY_OBJECT>(() => {
  return form2.value.tableWidget;
});
const activeWidgetList = computed(() => {
  return form2.value.widgetList;
});
const activeOperationList = computed(() => {
  return form2.value.operationList;
});
const primaryColumnName = computed(() => {
  if (dialogParams.value.isEdit) return;
  let table = form2.value.tableMap.get(queryTable.value.bindData.tableId);
  if (table && Array.isArray(table.columnList)) {
    for (let i = 0; i < table.columnList.length; i++) {
      let column = table.columnList[i];
      if (column.primaryKey) {
        return column.columnName;
      }
    }
  }
  return null;
});

const getTableTreeConfig = computed(() => {
  if (
    queryTable.value &&
    queryTable.value.props.treeFlag &&
    queryTable.value.props.parentIdColumn
  ) {
    return {
      rowField: primaryColumnName.value,
      parentField: queryTable.value.props.parentIdColumn,
    };
  } else {
    return null;
  }
});

const customQueryParams = ref<ANY_OBJECT>([]);

// TODO 弹窗关闭
const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  }
};
const onCopyWidget = (widget: ANY_OBJECT) => {
  activeWidgetList.value.push(widget);
};
const onWidgetClick = (widget: ANY_OBJECT | null) => {
  emit('widgetClick', widget);
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
const onTableClick = () => {
  emit('tableClick', queryTable.value);
};
const onLeftWidgetClick = () => {
  emit('widgetClick', form2.value.leftWidget);
};
const onSelectRowChange = (rows: ANY_OBJECT[]) => {
  selectRows.value = rows;
};
const refreshTable = (reloadData = false, queryParams = undefined) => {
  customQueryParams.value = queryParams;
  if (dialogParams.value.isEdit) return;
  if (reloadData) {
    queryTableWidget.value.refreshTable(true, 1);
  } else {
    queryTableWidget.value.refreshTable();
  }
};
const onBatchDelete = () => {
  if (selectRows.value.length <= 0) {
    ElMessage.error('请选择要批量删除的数据！');
    return;
  }
  ElMessageBox.confirm('是否删除选中数据？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    let table = form2.value.tableMap.get(queryTable.value.bindData.tableId);
    let params = {
      datasourceId: table.datasource.datasourceId,
      relationId: (table.relation || {}).relationId,
      dataIdList: selectRows.value.map(item => {
        return item[primaryColumnName.value];
      }),
    };

    let httpCall;
    if (params.relationId) {
      httpCall = post(
        API_CONTEXT +
          '/online/onlineOperation/deleteBatchOneToManyRelation/' +
          table.datasource.variableName,
        params,
      );
    } else {
      httpCall = post(
        API_CONTEXT +
          '/online/onlineOperation/deleteBatchDatasource/' +
          table.datasource.variableName,
        params,
      );
    }
    httpCall
      .then(() => {
        ElMessage.success('删除成功！');
        refreshTable(true);
      })
      .catch(e => {
        console.warn(e);
      });
  });
};
const onDeleteRow = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除当前数据？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      let table = form2.value.tableMap.get(queryTable.value.bindData.tableId);
      let params = {
        datasourceId: table.datasource.datasourceId,
        relationId: (table.relation || {}).relationId,
        dataId: row[primaryColumnName.value],
      };
      let httpCall = null;
      if (params.relationId) {
        httpCall = post(
          API_CONTEXT +
            '/online/onlineOperation/deleteOneToManyRelation/' +
            table.datasource.variableName,
          params,
        );
      } else {
        httpCall = post(
          API_CONTEXT + '/online/onlineOperation/deleteDatasource/' + table.datasource.variableName,
          params,
        );
      }

      httpCall
        .then(() => {
          ElMessage.success('删除成功！');
          refreshTable(true);
        })
        .catch(e => {
          console.warn(e);
        });
    })
    .catch(e => {
      console.warn(e);
    });
};
const onExport = (operation: ANY_OBJECT) => {
  ElMessageBox.confirm('是否导出表格数据？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      let table = form2.value.tableMap.get(queryTable.value.bindData.tableId);
      let params = {
        datasourceId: table.datasource.datasourceId,
        relationId: (table.relation || {}).relationId,
        filterDtoList: getQueryParams(),
        exportInfoList: (operation.exportColumnList || []).sort(
          (val1: ANY_OBJECT, val2: ANY_OBJECT) => {
            return val1.showOrder - val2.showOrder;
          },
        ),
      };

      if (queryTable.value.relation != null) {
        params.filterDtoList.push({
          tableName: queryTable.value.table.tableName,
          columnName: queryTable.value.relation.slaveColumn.columnName,
          filterType: SysOnlineColumnFilterType.EQUAL_FILTER,
          columnValue: (dialogParams.value.masterTableData || {})[
            queryTable.value.relation.masterColumn.columnName
          ],
        });
      }

      if (
        !dialogParams.value.isEdit &&
        typeof queryTable.value.eventInfo[OnlineFormEventType.BEFORE_LOAD_TABLE_DATA] === 'function'
      ) {
        params = queryTable.value.eventInfo[OnlineFormEventType.BEFORE_LOAD_TABLE_DATA](
          params,
          useFormExpose(formData, props),
        );
      }

      let httpCall;
      if (params.relationId) {
        httpCall = download(
          API_CONTEXT +
            '/online/onlineOperation/exportByOneToManyRelationId/' +
            table.datasource.variableName,
          params,
          queryTable.value.showName + '.xlsx',
        );
      } else {
        httpCall = download(
          API_CONTEXT +
            '/online/onlineOperation/exportByDatasourceId/' +
            table.datasource.variableName,
          params,
          queryTable.value.showName + '.xlsx',
        );
      }
      httpCall
        .then(() => {
          ElMessage.success('导出成功！');
        })
        .catch(e => {
          ElMessage.error(e);
        });
    })
    .catch(e => {
      console.warn(e);
    });
};
const onImport = (operation: ANY_OBJECT, file: ANY_OBJECT | null) => {
  let table = form2.value.tableMap.get(queryTable.value.bindData.tableId);
  if (file == null) {
    ElMessage.error('请选择要导入的文件！');
    return;
  }
  if (table == null) {
    ElMessage.error('未找到数据源！');
    return;
  }
  let url =
    API_CONTEXT + '/online/onlineOperation/importDatasource/' + table.datasource.variableName;
  let params = {
    datasourceId: table.datasource.datasourceId,
    importColumnIds: (operation.exportColumnList || [])
      .sort((val1: ANY_OBJECT, val2: ANY_OBJECT) => {
        return val1.showOrder - val2.showOrder;
      })
      .map((item: ANY_OBJECT) => {
        return item.columnId;
      }),
    skipHeader: operation.skipHeader,
    importFile: file,
  };
  fetchUpload(url, params)
    .then(() => {
      ElMessage.success('导入成功！');
      refreshTable(true);
    })
    .catch(e => {
      ElMessage.error(e);
    });
};
const onReset = () => {
  refreshTable(true);
};
const initFormData = () => {
  refreshTable(true);
};
const resetWidget = (widget: ANY_OBJECT) => {
  if (filterBox.value) filterBox.value.resetWidget(widget);
};
const hasOperator = (type: string) => {
  let temp = getOperation(type);
  return temp && temp.enabled;
};
const getOperation = (type: string) => {
  return findItemFromList(form2.value.operationList, type, 'type');
};
const operationVisible = (type: string) => {
  let operation = getOperation(type);
  return !form2.value.readOnly && hasOperator(type) && checkOperationVisible(operation);
};
const operationDisabled = (type: string) => {
  let operation = getOperation(type);
  return checkOperationDisabled(operation) || !checkOperationPermCode(operation);
};
const onLeftWidgetChange = (val: ANY_OBJECT | undefined, dictData: ANY_OBJECT | null) => {
  onValueChange(leftWidget.value, val);
  onWidgetValueChange(leftWidget.value, val, dictData);
  nextTick(() => {
    console.log('onLeftWidgetChange', val, formData.dsTeacher.level);
    refreshTable(true);
  });
};
const onImportClick = (file: ANY_OBJECT) => {
  onOperationClick(getOperation(SysCustomWidgetOperationType.IMPORT), file);
  return false;
};

const onUpdate = (operation: ANY_OBJECT | null, row: ANY_OBJECT | null = null) => {
  if (operation == null) return;
  let table = form2.value.tableMap.get(queryTable.value.bindData.tableId);
  let params = {
    datasourceId: table.datasource.datasourceId,
    relationId: (table.relation || {}).relationId,
    masterData: {},
    slaveData: {},
  };
  if (operation.relationId != null) {
    // 更新从表
    params.slaveData = operation.updateColumnList.reduce((pre, cur) => {
      pre[cur.updateColumnName] = cur.updateValue;
      return pre;
    }, {});
  } else {
    // 更新主表
    params.masterData = operation.updateColumnList.reduce((pre, cur) => {
      pre[cur.updateColumnName] = cur.updateValue;
      return pre;
    }, {});
  }
  if (!operation.rowOperation) {
    // 批量更新
    if (selectRows.value.length <= 0) {
      ElMessage.error('请选择要更新的数据！');
      return;
    }
    params.dataIdList = selectRows.value.map(item => {
      return item[primaryColumnName.value];
    });
  } else {
    // 单条更新
    if (row == null) {
      ElMessage.error('请选择要更新的数据！');
      return;
    }
    params.dataIdList = [row[primaryColumnName.value]];
  }
  let httpCall;
  let url;
  if (params.relationId) {
    httpCall = post(
      API_CONTEXT +
        '/online/onlineOperation/updateOneToManyRelationBatch/' +
        table.datasource.variableName,
      params,
    );
  } else {
    httpCall = post(
      API_CONTEXT +
        '/online/onlineOperation/updateDatasourceBatch/' +
        table.datasource.variableName,
      params,
    );
  }
  httpCall
    .then(res => {
      ElMessage.success(operation.name + '成功！');
      refreshTable(true);
    })
    .catch(e => {
      console.warn(e);
    });
};

const onOperationClick = (operation: ANY_OBJECT | null, row: ANY_OBJECT | null = null) => {
  if (dialogParams.value.isEdit) return;
  if (operation == null) {
    console.warn('operation is null');
    return;
  }
  if (operation.type === SysCustomWidgetOperationType.BATCH_DELETE) {
    onBatchDelete();
  } else if (operation.type === SysCustomWidgetOperationType.DELETE) {
    row && onDeleteRow(row);
  } else if (operation.type === SysCustomWidgetOperationType.EXPORT) {
    onExport(operation);
  } else if (operation.type === SysCustomWidgetOperationType.IMPORT) {
    onImport(operation, row);
  } else if (operation.type === SysCustomWidgetOperationType.PRINT) {
    onPrint(operation, row, selectRows.value, queryTable.value.showName);
  } else if (operation.type === SysCustomWidgetOperationType.START_FLOW) {
    onStartFlow(operation, row);
  } else if (operation.type === SysCustomWidgetOperationType.UPDATE) {
    onUpdate(operation, row);
  } else {
    let updateIdList = [];
    if (operation.type === SysCustomWidgetOperationType.UPDATE_DIALOG) {
      // 弹窗更新操作
      if (operation.rowOperation) {
        // 单条更新
        if (row == null) {
          ElMessage.error('请选择要更新的数据！');
          return;
        }
        updateIdList = [row[primaryColumnName.value]];
      } else {
        // 批量更新
        if (selectRows.value.length <= 0) {
          ElMessage.error('请选择要更新的数据！');
          return;
        }
        updateIdList = selectRows.value.map(item => {
          return item[primaryColumnName.value];
        });
      }
    }
    handlerOperation(operation, {
      isEdit: dialogParams.value.isEdit,
      isUpdate: operation.type === SysCustomWidgetOperationType.UPDATE_DIALOG,
      updateIdList: updateIdList,
      rowData: row,
      masterTableData: dialogParams.value.masterTableData || row,
      callback: () => {
        refreshTable();
      },
    });
  }
};

defineExpose({ resetWidget });

onMounted(() => {
  isReady.value = false;
  if (!dialogParams.value.isEdit) {
    if (
      !dialogParams.value.isEdit &&
      typeof form2.value.eventInfo[OnlineFormEventType.AFTER_CREATE_FORM] === 'function'
    ) {
      let params: ANY_OBJECT = { formData };
      form2.value.eventInfo[OnlineFormEventType.AFTER_CREATE_FORM](
        params,
        useFormExpose(formData, props),
      );
    }
    initFormData();
    initWidgetLinkage();
  }
  isReady.value = true;
});
</script>

<style scoped>
.round-search :deep(.el-input__wrapper) {
  border-radius: 16px;
}

.left-panel {
  display: flex;
  flex-direction: column;
  background: white;
  border-right: 2px solid #e8e8e8;
}
.left-panel .title {
  flex-shrink: 0;
  padding: 20px;
  font-size: 14px;
  color: #333;
  flex-grow: 0;
  font-weight: 400;
  border-bottom: 2px solid #e8e8e8;
}

.left-panel .title .name {
  flex-grow: 0;
  flex-shrink: 0;
}
.left-panel .title .search {
  flex-shrink: 1;
  min-width: 50px;
  margin-left: 10px;
  flex-grow: 1;
}
.online-advance-query-form {
  height: 100%;
}
.online-advance-query-form .table-panel {
  display: flex;
  flex-direction: column;
}
.online-advance-query-form .query-filter-box {
  flex-grow: 0;
  flex-shrink: 0;
}

.online-advance-query-form .query-table-box {
  flex-shrink: 1;
  height: 200px;
  flex-grow: 1;
}
.table-wrap {
  display: flex;
  padding-bottom: 16px;
  background-color: white;
  flex: 1;
  flex-direction: column;
}
.table-operation + .table-operation {
  margin-left: 8px;
}
</style>
