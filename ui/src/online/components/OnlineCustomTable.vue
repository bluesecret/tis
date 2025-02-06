<template>
  <table-box
    ref="table"
    style="height: 100%"
    :data="dataList"
    :key="tableKey"
    :rowKey="rowKey || widget.primaryColumnName"
    show-overflow="title"
    show-header-overflow="title"
    class="draggable-widget page-table"
    header-cell-class-name="table-header-gray"
    :hasImageColumn="hasImageColumn"
    :size="layoutStore.defaultFormItemSize"
    :tree-config="treeConfig"
    :seq-config="{ seqMethod }"
    :hasExtend="hasExtend"
    :supportTableConfig="true"
    :sort-config="{ remote: remoteSort }"
    :inlineRules="inlineRules"
    :customQueryOptions="customQueryOptions"
    :customConfig="customConfig"
    @custom-query="onCustomQuery"
    @table-config="onCustomTableConfigChange"
    @sort-change="onSortChange"
    @checkbox-select-change="onCheckBoxChange"
    @radio-select-change="onRadioSelectChange"
    @refresh="onRefresh()"
  >
    <template
      v-slot:operator
      v-if="
        operationList.filter(row => {
          return (
            (row.enabled && !row.rowOperation) ||
            row.type === SysCustomWidgetOperationType.INLINE_ADD
          );
        }).length > 0 && !form().readOnly
      "
    >
      <el-button
        class="table-operation"
        v-if="operationVisible(SysCustomWidgetOperationType.BATCH_DELETE)"
        :size="layoutStore.defaultFormItemSize"
        :type="getOperation(SysCustomWidgetOperationType.BATCH_DELETE).btnType"
        :plain="getOperation(SysCustomWidgetOperationType.BATCH_DELETE).plain"
        :disabled="operationDisabled(SysCustomWidgetOperationType.BATCH_DELETE)"
        @click="onOperationClick(getOperation(SysCustomWidgetOperationType.BATCH_DELETE))"
        :icon="Delete"
        >{{ getOperation(SysCustomWidgetOperationType.BATCH_DELETE).name || '批量删除' }}</el-button
      >
      <el-button
        class="table-operation"
        v-if="operationVisible(SysCustomWidgetOperationType.EXPORT)"
        :size="layoutStore.defaultFormItemSize"
        :type="getOperation(SysCustomWidgetOperationType.EXPORT).btnType"
        :plain="getOperation(SysCustomWidgetOperationType.EXPORT).plain"
        :disabled="operationDisabled(SysCustomWidgetOperationType.EXPORT)"
        @click="onOperationClick(getOperation(SysCustomWidgetOperationType.EXPORT))"
        :icon="Download"
        >{{ getOperation(SysCustomWidgetOperationType.EXPORT).name || '导出' }}</el-button
      >
      <el-upload
        class="table-operation"
        v-if="operationVisible(SysCustomWidgetOperationType.IMPORT)"
        style="display: inline-block"
        action=""
        :show-file-list="false"
        accept=".xlsx, .xls"
        :before-upload="onImportClick"
      >
        <el-button
          :size="layoutStore.defaultFormItemSize"
          :type="getOperation(SysCustomWidgetOperationType.IMPORT).btnType"
          :plain="getOperation(SysCustomWidgetOperationType.IMPORT).plain"
          :disabled="operationDisabled(SysCustomWidgetOperationType.IMPORT)"
          :icon="Upload"
          >{{ getOperation(SysCustomWidgetOperationType.IMPORT).name || '导入' }}</el-button
        >
      </el-upload>
      <el-button
        class="table-operation"
        v-if="
          operationVisible(SysCustomWidgetOperationType.PRINT) &&
          !getOperation(SysCustomWidgetOperationType.PRINT).rowOperation
        "
        :size="layoutStore.defaultFormItemSize"
        :type="getOperation(SysCustomWidgetOperationType.PRINT).btnType"
        :plain="getOperation(SysCustomWidgetOperationType.PRINT).plain"
        :disabled="operationDisabled(SysCustomWidgetOperationType.PRINT)"
        @click="onOperationClick(getOperation(SysCustomWidgetOperationType.PRINT))"
        >{{ getOperation(SysCustomWidgetOperationType.PRINT).name || '打印' }}</el-button
      >
      <el-button
        class="table-operation"
        v-if="operationVisible(SysCustomWidgetOperationType.ADD)"
        :size="layoutStore.defaultFormItemSize"
        :type="getOperation(SysCustomWidgetOperationType.ADD).btnType"
        :plain="getOperation(SysCustomWidgetOperationType.ADD).plain"
        :disabled="operationDisabled(SysCustomWidgetOperationType.ADD)"
        @click="onOperationClick(getOperation(SysCustomWidgetOperationType.ADD))"
        :icon="Plus"
        >{{ getOperation(SysCustomWidgetOperationType.ADD).name || '新建' }}</el-button
      >
      <el-button
        class="table-operation"
        v-if="operationVisible(SysCustomWidgetOperationType.INLINE_ADD)"
        :size="layoutStore.defaultFormItemSize"
        :type="getOperation(SysCustomWidgetOperationType.INLINE_ADD).btnType"
        :plain="getOperation(SysCustomWidgetOperationType.INLINE_ADD).plain"
        :disabled="operationDisabled(SysCustomWidgetOperationType.INLINE_ADD)"
        @click="onOperationClick(getOperation(SysCustomWidgetOperationType.INLINE_ADD))"
        :icon="Plus"
        >{{ getOperation(SysCustomWidgetOperationType.ADD).name || '新建' }}</el-button
      >
      <!-- 批量更新 -->
      <el-button
        class="table-operation"
        v-for="operation in tableUpdateOperationList"
        :key="operation.id"
        :size="layoutStore.defaultFormItemSize"
        :type="operation.btnType"
        :plain="operation.plain"
        :disabled="
          !form().checkOperationPermCode(operation) || form().checkOperationDisabled(operation)
        "
        @click="onOperationClick(operation)"
        >{{ operation.name || '更新' }}</el-button
      >
    </template>
    <vxe-column v-if="hasBatchOperation && !form().readOnly" type="checkbox" :width="50" />
    <vxe-column v-if="singleSelect" type="radio" align="center" :width="50" />
    <vxe-column
      v-if="finalTableColumnList.length > 0"
      type="seq"
      title="序号"
      :width="treeConfig != null ? 90 : 50"
      :tree-node="treeConfig != null"
    />
    <template v-for="tableColumn in finalTableColumnList" :key="tableColumn.columnId">
      <template v-if="tableColumn.inlineEdit">
        <!-- Input组件 -->
        <table-box-inline-input-column
          v-if="tableColumn.inlineEditWidgetType === SysCustomWidgetType.INLINE_INPUT"
          :ref="tableColumn.columnId"
          :title="tableColumn.showName"
          :width="tableColumn.columnWidth"
          :field="tableColumn.editFieldName"
          :sortable="tableColumn.sortable"
        />
        <!-- 数字输入组件 -->
        <table-box-inline-number-column
          v-if="tableColumn.inlineEditWidgetType === SysCustomWidgetType.INLINE_NUMBER_INPUT"
          :ref="tableColumn.columnId"
          :title="tableColumn.showName"
          :width="tableColumn.columnWidth"
          :field="tableColumn.editFieldName"
          :sortable="tableColumn.sortable"
        />
        <!-- 日期选择组件 -->
        <table-box-inline-date-column
          v-if="tableColumn.inlineEditWidgetType === SysCustomWidgetType.INLINE_DATE"
          :ref="tableColumn.columnId"
          :title="tableColumn.showName"
          :width="tableColumn.columnWidth"
          :field="tableColumn.editFieldName"
          :sortable="tableColumn.sortable"
        />
        <!-- 开关组件 -->
        <table-box-inline-switch-column
          v-if="tableColumn.inlineEditWidgetType === SysCustomWidgetType.INLINE_SWITCH"
          :ref="tableColumn.columnId"
          :title="tableColumn.showName"
          :width="tableColumn.columnWidth"
          :field="tableColumn.editFieldName"
          :sortable="tableColumn.sortable"
        />
        <!-- 上传组件 -->
        <table-box-inline-upload-column
          v-if="tableColumn.inlineEditWidgetType === SysCustomWidgetType.INLINE_UPLOAD"
          :ref="tableColumn.columnId"
          :title="tableColumn.showName"
          :width="tableColumn.columnWidth"
          :field="tableColumn.showFieldName"
          :type="
            tableColumn.column && tableColumn.column.fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE
              ? 'image'
              : 'file'
          "
          :action="getActionUrl(tableColumn)"
          :data="getUploadData(tableColumn)"
          :limit="getUploadLimit(tableColumn)"
          :rowKey="widget.primaryColumnName"
          :parseUploadDataFunction="(val, row) => parseInlineUploadData(tableColumn, row, val)"
          fileListType="dropdown"
        >
          <template v-slot="scope">
            <upload-file-list
              :file-list="parseTableUploadData(tableColumn, scope.row)"
              :type="
                tableColumn.column.fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE ? 'card' : 'text'
              "
              direction="horizontal"
              :readonly="true"
            />
          </template>
        </table-box-inline-upload-column>
        <!-- Select组件 -->
        <table-box-inline-select-column
          v-if="tableColumn.inlineEditWidgetType === SysCustomWidgetType.INLINE_SELECT"
          :ref="tableColumn.columnId"
          :title="tableColumn.showName"
          :width="tableColumn.columnWidth"
          :field="tableColumn.editFieldName"
          :sortable="tableColumn.sortable"
          :multiple="tableColumn.column && tableColumn.column.multiple"
          :loadDropdownData="row => loadTableColummnDropdownData(row, tableColumn)"
          @change="(val, row) => onTableDropdownChange(val, row, tableColumn)"
        >
          <template #default="{ row }">
            <span>{{ (row[tableColumn.editFieldName + 'DictMap'] || {}).name }}</span>
          </template>
        </table-box-inline-select-column>
        <!-- Cascader组件 -->
        <table-box-inline-cascader-column
          v-if="tableColumn.inlineEditWidgetType === SysCustomWidgetType.INLINE_CASCADER"
          :ref="tableColumn.columnId"
          :title="tableColumn.showName"
          :width="tableColumn.columnWidth"
          :field="tableColumn.editFieldName"
          :sortable="tableColumn.sortable"
          :loadDropdownData="row => loadTableColummnDropdownData(row, tableColumn)"
        >
          <template #default="{ row }">
            <span>{{ (row[tableColumn.editFieldName + 'DictMap'] || {}).name }}</span>
          </template>
        </table-box-inline-cascader-column>
      </template>
      <template v-else>
        <!-- Boolean类型字段 -->
        <vxe-column
          :key="tableColumn.id + 'Boolean'"
          v-if="tableColumn.column && tableColumn.column.objectFieldType === 'Boolean'"
          :title="tableColumn.showName"
          :width="tableColumn.columnWidth"
        >
          <template v-slot="scope">
            <el-tag
              :size="layoutStore.defaultFormItemSize"
              :type="getObjectValue(scope.row, tableColumn.showFieldName) ? 'success' : 'danger'"
            >
              {{ getObjectValue(scope.row, tableColumn.showFieldName) ? '是' : '否' }}
            </el-tag>
          </template>
        </vxe-column>
        <!-- 图片类型字段 -->
        <vxe-column
          v-else-if="
            tableColumn.column && tableColumn.column.fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE
          "
          :key="tableColumn.id + 'Image'"
          :title="tableColumn.showName"
          :width="tableColumn.columnWidth"
        >
          <template v-slot="scope">
            <upload-file-list
              :file-list="parseTableUploadData(tableColumn, scope.row)"
              type="card"
              direction="horizontal"
              :readonly="true"
            />
          </template>
        </vxe-column>
        <!-- 文件下载类型字段 -->
        <vxe-column
          v-else-if="
            tableColumn.column && tableColumn.column.fieldKind === SysOnlineFieldKind.UPLOAD
          "
          :key="tableColumn.id + 'File'"
          :title="tableColumn.showName"
          :width="tableColumn.columnWidth"
        >
          <template v-slot="scope">
            <upload-file-list
              :file-list="parseTableUploadData(tableColumn, scope.row)"
              type="text"
              direction="horizontal"
              :readonly="true"
            />
          </template>
        </vxe-column>
        <!-- 日期类型 -->
        <vxe-column
          v-else-if="tableColumn.column && tableColumn.column.objectFieldType === 'Date'"
          :key="tableColumn.columnId"
          :title="tableColumn.showName"
          :width="tableColumn.columnWidth"
        >
          <template v-slot="scope">
            <span>{{ getDateColumnValue(tableColumn, scope.row) }}</span>
          </template>
        </vxe-column>
        <!-- 其他类型 -->
        <vxe-column
          v-else
          :key="tableColumn.id + 'Other'"
          :title="tableColumn.showName"
          :field="tableColumn.showFieldName"
          :width="tableColumn.columnWidth"
          :sortable="tableColumn.sortable"
        />
      </template>
    </template>
    <table-box-inline-auto-save-column
      v-if="supportAutoEditNext"
      @save="row => onSaveRowClick(row, true)"
    />
    <vxe-column
      title="操作"
      :width="widget.props.operationColumnWidth || 160"
      fixed="right"
      :show-overflow="false"
      v-if="tableOperationList.length > 0 && finalTableColumnList.length > 0"
    >
      <template #default="{ row }">
        <template v-if="!table.isRowEditing(row)">
          <el-button
            v-for="operation in tableOperationList"
            :key="operation.id"
            v-show="form().checkOperationVisible(operation, row)"
            :size="layoutStore.defaultFormItemSize"
            link
            :class="operation.btnClass"
            :disabled="
              !form().checkOperationPermCode(operation) ||
              form().checkOperationDisabled(operation, row) ||
              table.isTableEditing(row)
            "
            @click="onOperationClick(operation, row)"
          >
            {{ operation.name }}
          </el-button>
        </template>
        <template v-else>
          <el-button
            link
            type="primary"
            :size="layoutStore.defaultFormItemSize"
            @click.stop="onSaveRowClick(row, false)"
          >
            保存
          </el-button>
          <el-button
            link
            type="primary"
            :size="layoutStore.defaultFormItemSize"
            @click.stop="onCancelRowClick(row)"
          >
            取消
          </el-button>
        </template>
      </template>
    </vxe-column>
    <template v-slot:empty>
      <div class="table-empty unified-font">
        <img src="@/assets/img/empty.png" />
        <span>暂无数据</span>
      </div>
    </template>
    <template v-slot:pagination>
      <slot name="pagination" />
    </template>
  </table-box>
</template>

<script setup lang="ts">
import { VxeColumn } from 'vxe-table';
import { ElMessage } from 'element-plus';
import { Picture, Delete, Download, Plus, Upload } from '@element-plus/icons-vue';
import TableBox from '@/components/TableBox/index.vue';
import { useDownload } from '@/common/hooks/useDownload';
import { ANY_OBJECT } from '@/types/generic';
import { post, get } from '@/common/http/request';
import { findItemFromList, getObjectValue, stringCase, formatDate } from '@/common/utils';
import {
  SysCustomWidgetOperationType,
  SysCustomWidgetType,
  SysOnlineDictType,
  SysOnlineFormType,
  CustomQueryFilterValueType,
  CriteriaFilterType,
  LogicOperatorType,
} from '@/common/staticDict';
import {
  SysOnlineFieldKind,
  SysOnlineParamValueType,
  SysOnlineRelationType,
} from '@/common/staticDict/online';
import { OnlineFormController } from '@/api/online';
import { useUpload } from '@/common/hooks/useUpload';
import { SortInfo } from '@/common/types/sortinfo';
import { API_CONTEXT } from '@/api/config';
import { useLayoutStore } from '@/store';
import { buildColumnRules } from '@/online/utils/index.js';
import InlineInputColumn from '@/components/TableBox/InlineInputColumn.vue';
import InlineNumberColumn from '@/components/TableBox/InlineNumberColumn.vue';
import InlineSelectColumn from '@/components/TableBox/InlineSelectColumn.vue';
import InlineDateColumn from '@/components/TableBox/InlineDateColumn.vue';
import InlineSwitchColumn from '@/components/TableBox/InlineSwitchColumn.vue';
import InlineCascaderColumn from '@/components/TableBox/InlineCascaderColumn.vue';
import InlineUploadColumn from '@/components/TableBox/InlineUploadColumn.vue';
import InlineAutoSaveColumn from '@/components/TableBox/InlineAutoSaveColumn.vue';

const layoutStore = useLayoutStore();
const { downloadFile } = useDownload();
const { parseUploadData } = useUpload();

const emit = defineEmits<{
  delete: [ANY_OBJECT];
  operationClick: [ANY_OBJECT, ANY_OBJECT | null];
  refresh: [];
}>();
const props = withDefaults(
  defineProps<{
    dataList?: Array<ANY_OBJECT>;
    height?: string | number;
    border?: string;
    // 是否支持多选
    multiSelect?: boolean;
    // 是否支持单选
    singleSelect?: boolean;
    // 表格操作列表
    operationList?: Array<ANY_OBJECT>;
    widget?: ANY_OBJECT;
    // 获取行序号
    getTableIndex?: (value: number) => number;
    // 排序改变
    sortChange?: (value: SortInfo) => void;
    // 多选选中改变
    onSelectChange?: (values: Array<ANY_OBJECT>) => void;
    // 单选中改变
    onRadioChange?: (value: ANY_OBJECT) => void;
    treeConfig?: ANY_OBJECT;
    saveRow?: (row: ANY_OBJECT, autoEditNext: boolean, res: ANY_OBJECT) => void;
    rowKey?: string;
    supportAutoEditNext: boolean;
    supportCustomQuery: boolean;
  }>(),
  {
    dataList: () => [],
    height: 'auto',
    border: 'full',
    rowEdit: false,
    multiSelect: false,
    singleSelect: false,
    operationList: () => [],
    widget: undefined,
    treeConfig: undefined,
    saveRow: undefined,
    rowKey: undefined,
    supportAutoEditNext: true,
    supportCustomQuery: true,
  },
);

const table = ref();
const form = inject('form', () => {
  console.error('OnlineCustomTable: form not injected');
  return { isEdit: false } as ANY_OBJECT;
});

const slots = useSlots();

const sortInfo = ref<SortInfo | null>(null);

const dictList = ref([]);
const formCustomConfig = ref();

const buildFlowParam = computed(() => {
  let flowParam: ANY_OBJECT = {};
  let flowData = form().flowData;
  if (flowData) {
    if (flowData.processDefinitionKey)
      flowParam.processDefinitionKey = flowData.processDefinitionKey;
    if (flowData.processInstanceId) flowParam.processInstanceId = flowData.processInstanceId;
    if (flowData.taskId) flowParam.taskId = flowData.taskId;
  }

  return flowParam;
});
const tableColumnList = computed(() => {
  let tempList =
    props.widget && props.widget.props && Array.isArray(props.widget.props.tableColumnList)
      ? props.widget.props.tableColumnList
      : [];
  const res: ANY_OBJECT[] = [];
  for (const tempItem of tempList) {
    const item = { ...tempItem };
    if (item.fieldType === 0 || item.fieldType == null) {
      // 绑定表字段
      if (item.column) item.showFieldName = item.column.columnName;
      if (props.widget.relation == null && item.relation != null) {
        item.showFieldName = item.relation.variableName + '.' + item.showFieldName;
      }
      item.editFieldName = item.showFieldName;
      if (item.column && item.column.dictInfo) {
        item.showFieldName = item.showFieldName + 'DictMap.name';
      }
    } else {
      // 自定义字段
      item.showFieldName = item.customFieldName;
    }
    res.push(item);
  }
  return res;
});

const tableUpdateOperationList = computed(() => {
  return props.operationList.filter(row => {
    return (
      row.enabled &&
      !row.rowOperation &&
      (row.type === SysCustomWidgetOperationType.UPDATE ||
        row.type === SysCustomWidgetOperationType.UPDATE_DIALOG)
    );
  });
});

const formConfig = computed(() => {
  return form().customConfig;
});

const tableCustomConfig = computed(() => {
  return formCustomConfig.value && formCustomConfig.value.extraData
    ? formCustomConfig.value.extraData[props.widget.variableName]
    : undefined;
});

const configTableColumnList = computed(() => {
  let tempList = tableColumnList.value || [];
  const customConfig = tableCustomConfig.value;
  return tempList.map(item => {
    if (
      customConfig &&
      customConfig.tableColumnConfig &&
      customConfig.tableColumnConfig[item.columnId]
    ) {
      item = {
        ...item,
        ...customConfig.tableColumnConfig[item.columnId],
      };
    } else {
      item.show = true;
    }
    return item;
  });
});

const finalTableColumnList = computed(() => {
  return configTableColumnList.value.filter(item => item.show);
});

const customConfig = computed(() => {
  return {
    tableColumnList: configTableColumnList.value,
  };
});

const customQueryHistory = computed(() => {
  if (
    formCustomConfig.value &&
    formCustomConfig.value.extraData &&
    formCustomConfig.value.extraData[props.widget.variableName]
  ) {
    return formCustomConfig.value.extraData[props.widget.variableName].queryHistory || [];
  } else {
    return [];
  }
});

const tableOperationList = computed(() => {
  return props.operationList.filter(item => {
    let temp = item.enabled && item.rowOperation;
    if (temp && form().readOnly) {
      temp = temp && item.readOnly;
    }
    return temp;
  });
});
const hasBatchOperation = computed(() => {
  let batchDeleteOperation = findItemFromList(
    props.operationList,
    SysCustomWidgetOperationType.BATCH_DELETE,
    'type',
  );
  let printOperation = findItemFromList(
    props.operationList,
    SysCustomWidgetOperationType.PRINT,
    'type',
  );
  let updateOperation = props.operationList.find(item => {
    if (
      !item.rowOperation &&
      (item.type === SysCustomWidgetOperationType.UPDATE ||
        item.type === SysCustomWidgetOperationType.UPDATE_DIALOG)
    ) {
      return true;
    } else {
      return false;
    }
  });
  return (
    (batchDeleteOperation != null && batchDeleteOperation.enabled) ||
    (printOperation != null && printOperation.enabled && !printOperation.rowOperation) ||
    (updateOperation != null && updateOperation.enabled && !updateOperation.rowOperation)
  );
});
const hasImageColumn = computed(() => {
  return (
    finalTableColumnList.value.filter((tableColumn: ANY_OBJECT) => {
      return tableColumn.column && tableColumn.column.fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE;
    }).length > 0
  );
});
const tableKey = computed(() => {
  return (props.widget || {}).variableName + new Date().getTime() + tableColumnList.value.length;
});
const remoteSort = computed(() => {
  if (
    form().formType === SysOnlineFormType.QUERY ||
    form().formType === SysOnlineFormType.ADVANCE_QUERY
  ) {
    return props.widget ? (props.widget.props || {}).paged : false;
  } else {
    return false;
  }
});
const hasExtend = computed(() => {
  return (
    (props.operationList.filter(row => {
      return row.enabled && !row.rowOperation;
    }).length > 0 &&
      !form().readOnly) ||
    (slots.operator && slots.operator()) != null
  );
});

const getDateColumnValue = (tableColumn, row) => {
  let value = getObjectValue(row, tableColumn.showFieldName);
  if (value == null) return '';
  if (tableColumn.column.objectFieldType === 'Date') {
    return formatDate(value, tableColumn.dateType || 'YYYY-MM-DD HH:mm:ss');
  } else {
    return value;
  }
};

const instance = getCurrentInstance();
const fistInlineColumnRef = computed(() => {
  let tempList = finalTableColumnList.value.filter(item => item.inlineEdit);
  if (tempList.length === 0) return null;
  const proxy = instance?.proxy;
  let refsList = [];
  if (proxy != null) {
    console.log(proxy.$refs);
    refsList = proxy.$refs[tempList[0].columnId];
  }
  if (Array.isArray(refsList)) {
    return refsList[0];
  } else {
    return refsList;
  }
});

const inlineRules = computed(() => {
  let rules = {};
  tableColumnList.value.forEach(tableColumn => {
    if (tableColumn.inlineEdit && tableColumn.column) {
      let ruleItemList = buildColumnRules(tableColumn.column, tableColumn.showName);
      if (ruleItemList && ruleItemList.length > 0) {
        rules[tableColumn.editFieldName] = ruleItemList;
      }
    }
  });
  return rules;
});

const customQueryFieldList = computed(() => {
  if (props.widget.datasource && props.widget.relation && props.widget.relation.slaveTable) {
    // 绑定从表，只显示从表字段
    return [
      {
        id: props.widget.relation.slaveTable.tableName,
        name: props.widget.relation.relationName,
        children: (props.widget.relation.slaveTable.columnList || []).map(column => {
          return {
            tableName: props.widget.relation.slaveTable.tableName,
            id: column.columnName,
            name: column.columnComment,
            ...column,
          };
        }),
      },
    ];
  } else if (props.widget.datasource && props.widget.datasource.masterTable) {
    // 绑定主表，显示主表和一对一从表字段
    let relationList = [];
    form().relationMap.forEach((relation, key) => {
      if (relation.relationType === SysOnlineRelationType.ONE_TO_ONE) {
        const temp = {
          id: relation.slaveTable.tableName,
          name: relation.relationName,
          children: (relation.slaveTable.columnList || []).map(column => {
            return {
              tableName: relation.slaveTable.tableName,
              id: column.columnName,
              name: column.columnComment,
              ...column,
            };
          }),
        };
        relationList.push(temp);
      }
    });
    return [
      ...relationList,
      {
        id: props.widget.datasource.masterTable.tableName,
        name: props.widget.datasource.datasourceName,
        children: props.widget.datasource.masterTable.columnList.map(column => {
          return {
            tableName: props.widget.datasource.masterTable.tableName,
            id: column.columnName,
            name: column.columnComment,
            ...column,
          };
        }),
      },
    ];
  }
  return [];
});

const customQueryOptions = computed(() => {
  return props.supportCustomQuery
    ? {
        dictList: dictList.value,
        fieldList: customQueryFieldList.value,
        getDictDataList: form().getDictDataList,
        customQueryHistory: customQueryHistory.value,
      }
    : undefined;
});

const onCustomTableConfigChange = config => {
  if (formCustomConfig.value == null) {
    formCustomConfig.value = {
      id: undefined,
      onlineFormId: form().rawData.onlineForm.formId,
      extraData: {},
    };
  }
  if (formCustomConfig.value.extraData == null) {
    formCustomConfig.value.extraData = {};
  }
  if (formCustomConfig.value.extraData[props.widget.variableName] == null)
    formCustomConfig.value.extraData[props.widget.variableName] = {};
  formCustomConfig.value.extraData[props.widget.variableName].tableColumnConfig = config;
  formCustomConfig.value = {
    ...formCustomConfig.value,
  };
  // 保存表格列配置
  const formId = form().rawData.onlineForm.formId;
  OnlineFormController.saveFormUserExt(
    {
      id: (formCustomConfig.value || {}).id,
      onlineFormId: formId,
      extraData: JSON.stringify(formCustomConfig.value.extraData),
    },
    { showMask: false },
  )
    .then(res => {
      console.log(res);
      formCustomConfig.value.id = res.data;
    })
    .catch(e => {
      console.log(e);
    });
};

const onCustomQuery = groupList => {
  let custromQueryParams = groupList.map(group => {
    return {
      logicOperator: group.logicOperator,
      filterList: group.filterList
        .map(filter => {
          let columnValue, columnValueList;
          let bindTable = customQueryFieldList.value.find(item => item.id === filter.tableName);
          let bindColumn = bindTable
            ? bindTable.children.find(item => item.id === filter.columnName)
            : null;
          if (
            filter.filterType === CriteriaFilterType.IN ||
            filter.filterType === CriteriaFilterType.NOT_IN
          ) {
            if (
              bindColumn &&
              (bindColumn.objectFieldType === 'String' || bindColumn.objectFieldType === 'Date')
            ) {
              columnValueList = filter.filterItemValue.map(item => {
                let temp = Array.isArray(item) ? item[item.length - 1] : undefined;
                return temp ? `'${temp}'` : undefined;
              });
            } else {
              columnValueList = filter.filterItemValue.map(item => {
                return Array.isArray(item) ? item[item.length - 1] : undefined;
              });
            }
            columnValueList = columnValueList.filter(item => item != null);
            if (columnValueList.length === 0) {
              columnValueList = null;
            }
          } else {
            if (Array.isArray(filter.filterItemValue)) {
              columnValue = filter.filterItemValue[filter.filterItemValue.length - 1];
            } else {
              columnValue = filter.filterItemValue;
            }
            if (
              bindColumn &&
              (bindColumn.objectFieldType === 'String' || bindColumn.objectFieldType === 'Date') &&
              filter.filterType !== CriteriaFilterType.LIKE
            ) {
              columnValue = `'${columnValue}'`;
            }
          }
          let tempFilterType;
          switch (filter.filterType) {
            case CriteriaFilterType.EQ:
              tempFilterType = 1;
              break;
            case CriteriaFilterType.NOT_EQ:
              tempFilterType = 9;
              break;
            case CriteriaFilterType.GE:
              tempFilterType = 10;
              break;
            case CriteriaFilterType.GT:
              tempFilterType = 11;
              break;
            case CriteriaFilterType.LE:
              tempFilterType = 12;
              break;
            case CriteriaFilterType.LT:
              tempFilterType = 13;
              break;
            case CriteriaFilterType.LIKE:
              tempFilterType = 3;
              break;
            case CriteriaFilterType.IN:
              tempFilterType = 4;
              break;
            case CriteriaFilterType.NOT_IN:
              tempFilterType = 6;
              break;
            case CriteriaFilterType.IS_NULL:
              tempFilterType = 7;
              break;
            case CriteriaFilterType.NOT_NULL:
              tempFilterType = 8;
              break;
            default:
              tempFilterType = 1;
              break;
          }
          return {
            tableName: filter.tableName,
            columnName: filter.columnName,
            columnValue: columnValue,
            columnValueList: columnValueList,
            filterType: tempFilterType,
            logicOperator: filter.logicOperator,
          };
        })
        .filter(item => {
          return (
            item.tableName != null &&
            item.columnName != null &&
            (item.columnValue != null || item.columnValueList != null)
          );
        }),
    };
  });
  custromQueryParams = custromQueryParams.filter(item => item.filterList.length > 0);
  if (custromQueryParams.length > 0) {
    // 保存历史搜索记录
    if (formCustomConfig.value == null) {
      formCustomConfig.value = {
        id: undefined,
        onlineFormId: form().rawData.onlineForm.formId,
        extraData: {},
      };
    }
    if (formCustomConfig.value.extraData == null) {
      formCustomConfig.value.extraData = {};
    }
    if (formCustomConfig.value.extraData[props.widget.variableName] == null)
      formCustomConfig.value.extraData[props.widget.variableName] = {};
    if (formCustomConfig.value.extraData[props.widget.variableName].queryHistory == null)
      formCustomConfig.value.extraData[props.widget.variableName].queryHistory = [];
    let queryData = JSON.stringify(groupList);
    let pos = formCustomConfig.value.extraData[props.widget.variableName].queryHistory.findIndex(
      item => item.data === queryData,
    );
    if (pos === -1) {
      formCustomConfig.value.extraData[props.widget.variableName].queryHistory.unshift({
        id: new Date().getTime(),
        name: formatDate(new Date(), 'YYYY-MM-DD HH:mm:ss'),
        data: queryData,
      });
      // 只保留前10条记录
      formCustomConfig.value.extraData[props.widget.variableName].queryHistory =
        formCustomConfig.value.extraData[props.widget.variableName].queryHistory.slice(0, 10);
    } else {
      // 把当前搜索记录放到第一位
      formCustomConfig.value.extraData[props.widget.variableName].queryHistory.splice(pos, 1);
      formCustomConfig.value.extraData[props.widget.variableName].queryHistory.unshift({
        id: new Date().getTime(),
        name: formatDate(new Date(), 'YYYY-MM-DD HH:mm:ss'),
        data: queryData,
      });
    }
    formCustomConfig.value = {
      ...formCustomConfig.value,
    };
    const formId = form().rawData.onlineForm.formId;
    OnlineFormController.saveFormUserExt(
      {
        id: (formCustomConfig.value || {}).id,
        onlineFormId: formId,
        extraData: JSON.stringify(formCustomConfig.value.extraData),
      },
      { showMask: false },
    )
      .then(res => {
        console.log(res);
        formCustomConfig.value.id = res.data;
      })
      .catch(e => {
        console.log(e);
      });
  }
  onRefresh(custromQueryParams);
};

const onRefresh = custromQueryParams => {
  emit('refresh', custromQueryParams);
};
const hasOperator = (type: string) => {
  let temp = getOperation(type);
  return temp && temp.enabled;
};
const getOperation = (type: string) => {
  return findItemFromList(props.operationList, type, 'type') || {};
};
const operationVisible = (type: string) => {
  let operation = getOperation(type);
  if (!operation) return false;
  return !form().readOnly && hasOperator(type) && form().checkOperationVisible(operation);
};
const operationDisabled = (type: string) => {
  let operation = getOperation(type);
  return form().checkOperationDisabled(operation) || !form().checkOperationPermCode(operation);
};
const seqMethod = (data: ANY_OBJECT) => {
  if (props.getTableIndex && props.treeConfig == null) {
    return props.getTableIndex(data.seq - 1);
  } else {
    return data.seq;
  }
};
const onSortChange = (data: ANY_OBJECT) => {
  if (!props.widget.props.paged) return;
  let fieldName = data.property.replace('DictMap.name', '');
  let order = data.order;
  if (order == null) {
    fieldName = undefined;
  }
  if (order === 'asc') order = 'ascending';
  if (
    sortInfo.value != null &&
    sortInfo.value?.prop === fieldName &&
    sortInfo.value?.order === order
  ) {
    return;
  }
  sortInfo.value = {
    prop: fieldName,
    order: order,
  };
  if (props.sortChange) {
    props.sortChange(sortInfo.value);
  }
};
const onCheckBoxChange = () => {
  if (table.value && typeof props.onSelectChange === 'function') {
    let selectRows = table.value.getTableImpl().getCheckboxRecords(true);
    props.onSelectChange(selectRows);
  }
};
const onRadioSelectChange = () => {
  if (table.value && typeof props.onRadioChange === 'function') {
    let selectRow = table.value.getTableImpl().getRadioRecord();
    props.onRadioChange(selectRow);
  }
};
const setSelectedRow = (rowNum: number) => {
  table.value.getTableImpl().setRadioRow(props.dataList[rowNum]);
  nextTick(onRadioSelectChange);
};

const onImportClick = (file: ANY_OBJECT) => {
  onOperationClick(getOperation(SysCustomWidgetOperationType.IMPORT), file);
  return false;
};

const focusFirstInlineColumn = () => {
  if (fistInlineColumnRef.value != null) {
    nextTick(() => {
      fistInlineColumnRef.value.focus();
    });
  }
};

const onOperationClick = (operation: ANY_OBJECT, row: ANY_OBJECT | null = null) => {
  if (operation.type === SysCustomWidgetOperationType.INLINE_ADD) {
    let newRow = {};
    let columnList = [];
    if (props.widget.datasource && props.widget.relation && props.widget.relation.slaveTable) {
      columnList = props.widget.relation.slaveTable.columnList;
    } else if (props.widget.datasource && props.widget.datasource.masterTable) {
      columnList = props.widget.datasource.masterTable.columnList;
    }
    columnList.forEach(column => {
      newRow[column.columnName] = undefined;
    });
    table.value.editRow(newRow, true);
    focusFirstInlineColumn();
  } else if (operation.type === SysCustomWidgetOperationType.INLINE_EDIT) {
    table.value.editRow(row, false);
    focusFirstInlineColumn();
  } else {
    emit('operationClick', operation, row);
  }
};

const refreshColumn = () => {
  nextTick(() => {
    if (table.value) table.value.getTableImpl().refreshColumn();
  });
};
const getDownloadUrl = (tableColumn: ANY_OBJECT) => {
  let downloadUrl = null;
  if (form().flowData != null) {
    downloadUrl = API_CONTEXT + '/flow/flowOnlineOperation/download';
  } else {
    if (tableColumn.relationId) {
      downloadUrl =
        API_CONTEXT +
        '/online/onlineOperation/downloadOneToManyRelation/' +
        (props.widget.datasource || {}).variableName;
    } else {
      downloadUrl =
        API_CONTEXT +
        '/online/onlineOperation/downloadDatasource/' +
        (props.widget.datasource || {}).variableName;
    }
  }

  return downloadUrl;
};

const parseInlineUploadData = (tableColumn, row, val) => {
  if (val == null) return [];
  let flowData = form().flowData;
  let downloadParams: ANY_OBJECT = {
    ...buildFlowParam.value,
    datasourceId: props.widget.datasource.datasourceId,
    relationId: tableColumn.relationId,
    fieldName: tableColumn.column.columnName,
    asImage: tableColumn.column?.fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE,
    processDefinitionKey: flowData ? flowData.processDefinitionKey : undefined,
  };
  if (props.widget.primaryColumnName != null) {
    downloadParams.dataId = row[props.widget.primaryColumnName] || '';
  }
  let downloadUrl = getDownloadUrl(tableColumn);
  let temp = JSON.parse(val);
  temp = Array.isArray(temp)
    ? temp.map(item => {
        return {
          ...item,
          downloadUri: downloadUrl,
        };
      })
    : [];
  return parseUploadData(JSON.stringify(temp), downloadParams);
};

const parseTableUploadData = (tableColumn: ANY_OBJECT, row: ANY_OBJECT) => {
  let jsonData = getObjectValue(row, tableColumn.showFieldName);
  return parseInlineUploadData(tableColumn, row, jsonData);
};
const getTablePictureList = (tableColumn: ANY_OBJECT, row: ANY_OBJECT) => {
  let temp = parseTableUploadData(tableColumn, row);
  if (Array.isArray(temp)) {
    return temp.map(item => item.url);
  }
};
const formatListData = (data: ANY_OBJECT) => {
  if (data == null) return;
  Object.keys(data).forEach(key => {
    let subData = data[key];
    if (typeof subData === 'object' && key.indexOf('DictMap') === -1) {
      formatListData(subData);
    } else {
      // 如果是多选字典字段，那么把选中的字典值拼接成DictMap去显示
      if (key.indexOf('DictMapList') !== -1 && Array.isArray(data[key])) {
        let newKey = key.replace('DictMapList', 'DictMap');
        data[newKey] = {
          id: data[key.replace('DictMapList', '')],
          name: data[key].map((subItem: ANY_OBJECT) => subItem.name).join(','),
        };
      }
    }
  });
};

const getInlineEditWidgetRef = (tableColumn?: ANY_OBJECT) => {
  const proxy = instance?.proxy;
  if (tableColumn == null || tableColumn.column == null || proxy == null) return null;
  const columnRef = proxy.$refs[tableColumn.column.columnId];
  if (Array.isArray(columnRef)) {
    return columnRef[0];
  } else {
    return columnRef;
  }
};

const onSaveRowClick = (row: ANY_OBJECT, autoEditNext: boolean) => {
  table.value
    .saveRow(autoEditNext)
    .then(res => {
      if (props.saveRow) {
        props.saveRow(row, autoEditNext, res);
      } else {
        let { rowData, nextRow, isNew, isDirty } = res;
        const datasource = props.widget.datasource;
        const relation = props.widget.relation;
        if (datasource != null) {
          let params = {
            datasourceId: datasource.datasourceId,
            relationId: relation ? relation.relationId : null,
            masterData: relation ? undefined : rowData,
            slaveData: relation ? rowData : undefined,
          };
          let url;
          if (isNew) {
            url = relation
              ? API_CONTEXT + '/online/onlineOperation/addOneToManyRelation/'
              : API_CONTEXT + '/online/onlineOperation/addDatasource/';
          } else {
            url = relation
              ? API_CONTEXT + '/online/onlineOperation/updateOneToManyRelation/'
              : API_CONTEXT + '/online/onlineOperation/updateDatasource/';
          }
          url += datasource.variableName;
          post(url, params)
            .then(res => {
              ElMessage.success('保存成功！');
              onRefresh();
            })
            .catch(e => {
              console.warn(e);
            });
        }
      }
    })
    .catch(e => {
      const proxy = instance?.proxy;
      if (e != null && e.message != null && e.message !== '') {
        let tableColumn = finalTableColumnList.value.find(
          item => item.column.columnName === e.message,
        );
        let columnRef = getInlineEditWidgetRef(tableColumn);
        if (columnRef) {
          columnRef.focus();
        }
      }
    });
};

const onCancelRowClick = (row: ANY_OBJECT) => {
  table.value.cancelEditRow(true);
};

const getValueTableColumn = (value: ANY_OBJECT) => {
  return finalTableColumnList.value.find(item => {
    if (item.inlineEdit && item.column) {
      return item.columnId === value;
    } else {
      return false;
    }
  });
};

const getParamValue = (valueType, valueData, row) => {
  switch (valueType) {
    case SysOnlineParamValueType.TABLE_COLUMN: {
      let tempColumn = getValueTableColumn(valueData);
      if (tempColumn != null && row) {
        return row[tempColumn.editFieldName];
      } else {
        return undefined;
      }
    }
    case SysOnlineParamValueType.STATIC_DICT:
      return Array.isArray(valueData) ? valueData[1] : undefined;
    case SysOnlineParamValueType.INPUT_VALUE:
      return valueData;
  }
};

const loadTableColummnDropdownData = (row: ANY_OBJECT, tableColumn: ANY_OBJECT) => {
  return new Promise((resolve, reject) => {
    if (tableColumn.column.dictInfo == null) {
      reject();
      return;
    }
    let params = {};
    (tableColumn.dictParams || []).forEach(param => {
      let value = getParamValue(param.dictValueType, param.dictValue, row);
      if (value && value !== '') params[param.dictParamName] = value;
    });
    // 如果字典是树形字典，使用select组件显示，并且没有过滤参数，则只返回顶层数据而不是所有数据
    let rootDataWhenNoParentId = false;
    let dictInfo = tableColumn.column.dictInfo;
    if (
      dictInfo.dictType === SysOnlineDictType.TABLE &&
      dictInfo.treeFlag &&
      tableColumn.inlineEditWidgetType === SysCustomWidgetType.INLINE_SELECT &&
      Object.keys(params).length === 0
    ) {
      rootDataWhenNoParentId = true;
    }
    form()
      .getDictDataList(dictInfo, params, rootDataWhenNoParentId)
      .then(res => {
        res.forEach(item => {
          item.id = item.id + '';
          if (item.parentId) item.parentId = item.parentId + '';
        });
        resolve(res);
      })
      .catch(e => {
        console.log(e);
      });
  });
};

const onTableDropdownChange = (val?: string | number, row: ANY_OBJECT, tableColumn: ANY_OBJECT) => {
  const fieldName = tableColumn.editFieldName;
  finalTableColumnList.value.forEach(item => {
    if (item.dictParams && item.inlineEdit) {
      item.dictParams.forEach(param => {
        if (param.dictValueType === SysOnlineParamValueType.TABLE_COLUMN) {
          let tempColumn = getValueTableColumn(param.dictValue);
          if (tempColumn && tempColumn.editFieldName === fieldName) {
            row[item.editFieldName] = undefined;
            if (item.inlineEdit && item.column) {
              let columnRef = getInlineEditWidgetRef(item);
              if (columnRef) {
                columnRef.reset();
              }
            }
          }
        }
      });
    }
  });
};

const getActionUrl = (tableColumn: ANY_OBJECT) => {
  const datasource = props.widget.datasource;
  const relation = props.widget.relation;
  let url;
  if (datasource != null) {
    if (relation) {
      url = API_CONTEXT + '/online/onlineOperation/uploadOneToManyRelation/';
    } else {
      url = API_CONTEXT + '/online/onlineOperation/uploadDatasource/';
    }
    url += datasource.variableName;
  }
  return url;
};

const getUploadData = (tableColumn: ANY_OBJECT) => {
  let temp = {
    ...buildFlowParam.value,
    datasourceId: (props.widget.datasource || {}).datasourceId,
    asImage: (tableColumn.column || {}).fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE,
    fieldName: (tableColumn.column || {}).columnName,
  };
  if ((props.widget.relation || {}).relationId)
    temp.relationId = (props.widget.relation || {}).relationId;
  let flowData = form().flowData;
  if (flowData && flowData.processDefinitionKey)
    temp.processDefinitionKey = flowData.processDefinitionKey;
  return temp;
};

const getUploadLimit = (tableColumn: ANY_OBJECT) => {
  return (tableColumn.column || {}).maxFileCount;
};

watch(
  () => formConfig.value,
  val => {
    if (val != null) {
      formCustomConfig.value = val;
    }
  },
  {
    immediate: true,
    deep: true,
  },
);

watch(
  () => props.dataList,
  () => {
    if (Array.isArray(props.dataList)) {
      props.dataList.forEach(item => {
        formatListData(item);
      });
      setTimeout(() => {
        focusFirstInlineColumn();
      });
    }
  },
  {
    immediate: true,
  },
);
watch(
  () => finalTableColumnList.value,
  () => {
    refreshColumn();
  },
  {
    immediate: true,
  },
);
watch(
  () => props.widget.props.operationColumnWidth,
  () => {
    refreshColumn();
  },
);

onMounted(() => {
  if (form().loadOnlineDictList != null) {
    form()
      .loadOnlineDictList()
      .then(res => {
        dictList.value = res;
      })
      .catch(e => {
        console.log(e);
      });
  }
});

defineExpose({
  setSelectedRow,
});
</script>

<style scoped>
.table-operation + .table-operation {
  margin-left: 8px;
}
</style>
