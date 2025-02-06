<template>
  <div class="online-query-form" :style="{ height: height ? height : '100%' }" v-show="isReady">
    <OnlineFilterBox
      class="query-filter-box"
      v-if="(activeWidgetList && activeWidgetList.length > 0) || dialogParams.isEdit"
      :isEdit="dialogParams.isEdit"
      ref="filterBox"
      :itemWidth="form2.filterItemWidth || 350"
      style="margin-bottom: 16px"
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
    <div
      class="query-table-box custom-widget-item widget-item"
      :class="{ active: dialogParams.isEdit && currentWidget === queryTable }"
      :style="{ padding: dialogParams.isEdit ? '0' : '' }"
      @click.stop="onTableClick"
    >
      <el-row>
        <el-col
          :span="24"
          class="widget-item"
          :class="{ active: dialogParams.isEdit && currentWidget === groupWidget }"
          style="background: white; padding: 10px 16px 0px 16px"
          :style="{ 'min-height': dialogParams.isEdit ? '40px' : undefined }"
          @click.stop="onWidgetClick(groupWidget)"
        >
          <OnlineCustomWidget
            ref="groupWidgetRef"
            :widget="groupWidget"
            :value="getWidgetValue(groupWidget)"
            @input="val => onValueChange(groupWidget, val)"
            @change="onGroupWidgetChange"
          />
        </el-col>
      </el-row>
      <OnlineCustomTable
        :dataList="queryTableWidget.dataList"
        style="height: 100%; flex-grow: 1"
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
        <template v-slot:pagination>
          <el-row
            type="flex"
            justify="end"
            style="margin-top: 16px"
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
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus';
import { Close } from '@element-plus/icons-vue';
import { treeDataTranslate } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import OnlineCustomWidget from '@/online/components/OnlineCustomWidget.vue';
import OnlineCustomTable from '@/online/components/OnlineCustomTable.vue';
import { TableOptions } from '@/common/types/pagination';
import { useTable } from '@/common/hooks/useTable';
import {
  OnlineFormEventType,
  SysCustomWidgetBindDataType,
  SysCustomWidgetOperationType,
} from '@/common/staticDict';
import { SysOnlineColumnFilterType } from '@/common/staticDict/online';
import { download, post, get } from '@/common/http/request';
import { API_CONTEXT } from '@/api/config';
import { useUpload } from '@/common/hooks/useUpload';
import { DialogProp } from '@/components/Dialog/types';
import { ThirdProps } from '@/components/thirdParty/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import widgetData from '@/online/config/index';
import { useLoginStore } from '@/store';
import { useDict } from '../../hooks/useDict';
import { useForm } from '../hooks/useForm';
import { useFormExpose } from '../hooks/useFormExpose';
import OnlineFilterBox from '../OnlineQueryForm/OnlineFilterBox.vue';

interface IProps extends ThirdProps {
  formConfig: ANY_OBJECT;
  height?: string;
  masterTableData?: ANY_OBJECT;
  // 是否表单编辑模式
  isEdit?: boolean;
  readOnly?: boolean;
  // 当前选中组件
  currentWidget?: ANY_OBJECT | null;
  // 是否全屏弹窗
  fullscreen?: boolean;
  mode: string;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
}

const emit = defineEmits<{
  widgetClick: [ANY_OBJECT | null];
  tableClick: [ANY_OBJECT | null];
}>();

const props = withDefaults(defineProps<IProps>(), {
  isEdit: false,
  readOnly: false,
  fullscreen: false,
  mode: 'pc',
});

const { onCloseThirdDialog } = useThirdParty(props);
const { fetchUpload } = useUpload();

const batchDelete = ref(false);
const selectRows = ref<ANY_OBJECT[]>([]);
const groupWidgetRef = ref();

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
  onStartFlow,
  loadOnlineDictList,
} = useForm(props);

provide('form', () => {
  return {
    ...form2.value,
    mode: props.mode || 'pc',
    isEdit: props.isEdit,
    readOnly: props.readOnly,
    formData: formData,
    getWidgetValue: getWidgetValue,
    getWidgetProp: getWidgetProp,
    getWidgetVisible: getWidgetVisible,
    onValueChange: onValueChange,
    onWidgetValueChange: onWidgetValueChange,
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
    instanceData: () => useFormExpose(formData, props),
  };
});

const queryTable = computed(() => {
  return form2.value.tableWidget;
});
const groupWidget = computed(() => {
  return form2.value.groupWidget;
});
const activeWidgetList = computed(() => {
  return form2.value.widgetList;
});
const customQueryParams = ref<ANY_OBJECT>([]);

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
      .filter(item => item != null);
  }
  if (groupWidget.value != null) {
    let temp = getQueryParamItem(groupWidget.value);
    if (temp) {
      temp.groupCount =
        groupWidget.value.props.groupCount == null ? false : groupWidget.value.props.groupCount;
    } else {
      temp = {
        tableName: groupWidget.value.table.tableName,
        columnName: groupWidget.value.column.columnName,
        filterType: SysOnlineColumnFilterType.NONE,
        groupCount:
          groupWidget.value.props.groupCount == null ? false : groupWidget.value.props.groupCount,
        columnValue: undefined,
      };
    }
    queryParams.push(temp);
  }
  return queryParams;
};
const loadTableData = (params: ANY_OBJECT) => {
  return new Promise((resolve, reject) => {
    let table = form2.value.tableMap.get(queryTable.value.bindData.tableId);
    if (!table) {
      console.warn('table is undefined tableId=', queryTable.value.bindData.tableId);
      return;
    }
    let httpCall = null;
    params.datasourceId = table.datasource.datasourceId;
    params.filterDtoList = getQueryParams();

    if (queryTable.value.relation != null) {
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
      httpCall = post(
        API_CONTEXT +
          '/online/onlineOperation/listByOneToManyRelationId/' +
          table.datasource.variableName,
        params,
      );
    } else {
      httpCall = post(
        API_CONTEXT + '/online/onlineOperation/listByDatasourceId/' + table.datasource.variableName,
        params,
      );
    }

    httpCall
      .then((res: ANY_OBJECT) => {
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
        if (groupWidgetRef.value && groupWidgetRef.value.getRef()) {
          groupWidgetRef.value.getRef().setGroupData(
            res.data.extra.reduce((pre, cur) => {
              pre[cur.GROUP_KEY] = cur.GROUP_COUNT;
              return pre;
            }, {}),
          );
        }
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

let paged = true;
const tableWidget = dialogParams.value.formConfig?.tableWidget;
if (
  tableWidget &&
  tableWidget.props &&
  tableWidget.props.treeFlag &&
  tableWidget.props.parentIdColumn
) {
  paged = false;
} else {
  paged = tableWidget?.props?.paged;
}

const tableOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadTableData,
  verifyTableParameter: loadTableDataVerify,
  paged: paged,
};
const queryTableWidget = reactive(useTable(tableOptions));

const onTableClick = () => {
  emit('tableClick', queryTable.value);
};
const onWidgetClick = (widget: ANY_OBJECT | null) => {
  emit('widgetClick', widget);
};

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};

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
const onSelectRowChange = (rows: ANY_OBJECT[]) => {
  selectRows.value = rows;
};

const onGroupWidgetChange = val => {
  refreshTable(true);
};

const refreshTable = (reloadData = false, queryParams = undefined) => {
  customQueryParams.value = queryParams;
  if (dialogParams.value.isEdit) return;
  if (reloadData) {
    queryTableWidget.refreshTable(true, 1);
  } else {
    queryTableWidget.refreshTable();
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
  }).then(() => {
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
  });
};
const onImport = (operation: ANY_OBJECT, file: ANY_OBJECT | null) => {
  console.log(operation, file);
  let table = form2.value.tableMap.get(queryTable.value.bindData.tableId);
  if (file == null) {
    ElMessage.error('请选择要导入的文件！');
    return;
  }
  if (table == null) {
    ElMessage.error('未找到数据源！');
    return;
  }
  let url = '/admin/online/onlineOperation/importDatasource/' + table.datasource.variableName;
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
const onExport = (operation: ANY_OBJECT) => {
  ElMessageBox.confirm('是否导出表格数据？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
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
  });
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

const onOperationClick = (operation: ANY_OBJECT, row: ANY_OBJECT | null) => {
  if (dialogParams.value.isEdit) return;
  if (operation.type === SysCustomWidgetOperationType.BATCH_DELETE) {
    onBatchDelete();
  } else if (operation.type === SysCustomWidgetOperationType.DELETE) {
    if (row) onDeleteRow(row);
  } else if (operation.type === SysCustomWidgetOperationType.EXPORT) {
    onExport(operation);
  } else if (operation.type === SysCustomWidgetOperationType.IMPORT) {
    onImport(operation, row);
  } else if (operation.type === SysCustomWidgetOperationType.PRINT) {
    onPrint(operation, row, selectRows.value, queryTable.value.showName);
  } else if (operation.type === SysCustomWidgetOperationType.START_FLOW) {
    console.log('启动流程');
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
const onReset = () => {
  refreshTable(true);
};
const initFormData = () => {
  refreshTable(true);
};

onMounted(() => {
  isReady.value = false;
  if (!dialogParams.value.isEdit) {
    if (
      form2.value.eventInfo &&
      typeof form2.value.eventInfo[OnlineFormEventType.AFTER_CREATE_FORM] === 'function'
    ) {
      form2.value.eventInfo[OnlineFormEventType.AFTER_CREATE_FORM](useFormExpose(formData, props));
    }
    initFormData();
    initWidgetLinkage();
  }
  isReady.value = true;
});
</script>

<style scoped>
.header-logo {
  border-radius: 8px;
  height: 40px;
  line-height: 40px;
  text-align: center;
  width: 40px;
  background: rgba(255, 119, 0, 0.1);
  margin-right: 8px;
  color: #fda834;
  display: inline-block;
}
.el-divider--vertical {
  height: 26px;
  margin: 7px 15px;
}
.online-query-form {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  height: 100px;
}

.online-query-form .query-filter-box {
  flex-grow: 0;
  flex-shrink: 0;
}

.online-query-form .query-table-box {
  display: flex;
  flex-direction: column;
  height: 200px;
  flex-grow: 1;
  flex-shrink: 1;
}

.online-query-form /deep/ .el-tabs__header {
  margin: 0px;
}
</style>
