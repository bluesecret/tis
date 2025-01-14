<template>
  <div class="form-single-fragment third-party-dlg">
    <el-form
      ref="form"
      :model="formData"
      class="form-box"
      :rules="rules"
      style="width: 100%"
      label-width="100px"
      :size="itemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row class="full-width-input">
        <el-col :span="24">
          <el-form-item label="绑定类型">
            <el-radio-group
              v-model="formData.fieldType"
              :disabled="rowData != null"
              @change="onBindTypeChange"
            >
              <el-radio-button :value="0">表字段</el-radio-button>
              <el-radio-button :value="1">自定义</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="formData.fieldType === 1">
          <el-form-item label="字段名" prop="customFieldName">
            <el-input v-model="formData.customFieldName" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="formData.fieldType === 0">
          <el-form-item label="字段数据表" prop="tableId">
            <el-select
              class="input-item"
              v-model="formData.tableId"
              :disabled="dialogParams.rowData != null"
              :clearable="true"
              placeholder="字段数据表"
              @change="onTableChange"
            >
              <el-option
                v-for="(table, index) in dialogParams.tableList"
                :key="table.tableId"
                :label="table.tableName"
                :value="table.tableId"
              >
                <el-row type="flex" justify="space-between" align="middle">
                  <span>{{ table.tableName }}</span>
                  <el-tag
                    :type="table.relationType == null ? 'success' : ''"
                    style="margin-left: 30px"
                    :size="itemSize"
                    effect="dark"
                  >
                    {{ table.relationType == null || index === 0 ? '主表' : '一对一关联' }}
                  </el-tag>
                </el-row>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="formData.fieldType === 0">
          <el-form-item label="绑定列字段" prop="columnId">
            <el-select
              class="input-item"
              v-model="formData.columnId"
              :disabled="dialogParams.rowData != null"
              :clearable="true"
              placeholder="字段数据表"
              @change="onColumnChange"
            >
              <el-option
                v-for="column in getTableColumnList"
                :key="column.columnId"
                :label="column.columnName"
                :value="column.columnId"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="表格列名" prop="showName">
            <el-input
              class="input-item"
              v-model="formData.showName"
              :clearable="true"
              placeholder="表格列名"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="bindColumn && bindColumn.objectFieldType === 'Date'">
          <el-form-item label="日期格式" prop="dateType">
            <el-select
              class="input-item"
              v-model="formData.dateType"
              :clearable="true"
              placeholder="日期格式"
            >
              <el-option
                v-for="item in dateTypeList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="表格列宽">
            <el-input-number
              class="input-item"
              v-model="formData.columnWidth"
              placeholder="表格列宽"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="支持排序">
            <el-switch v-model="formData.sortable" :disabled="formData.fieldType == 1" />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="supportInlineEdit">
          <el-form-item label="行内编辑">
            <el-switch
              v-model="formData.inlineEdit"
              :disabled="formData.fieldType == 1 || bindColumn.aggregationColumn"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="formData.inlineEdit">
          <el-form-item label="编辑控件">
            <el-select
              class="input-item"
              v-model="formData.inlineEditWidgetType"
              :disabled="formData.fieldType == 1 || !formData.inlineEdit"
              :clearable="true"
              placeholder="编辑控件"
            >
              <el-option
                v-for="item in inlineEditWidgetTypeList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="formData.inlineEdit && dictParamList.length > 0">
          <el-form-item label="字典参数">
            <MultiItemBox
              :data="formData.dictParams"
              addText="添加字典过滤参数"
              :maxCount="dictParamList ? dictParamList.length : 0"
              @add="onEditDictParamValue()"
              @edit="onEditDictParamValue"
              @delete="onRemoveDictParamValue"
              :prop="{
                label: 'dictParamName',
                value: 'dictParamName',
              }"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-row class="no-scroll menu-box" type="flex" justify="end">
      <el-button :size="itemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="itemSize" @click="onSubmit"> 保存 </el-button>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ElMessageBox } from 'element-plus';
import EditDictParamValue from '../CustomWidgetDictSetting/EditDictParamValue.vue';
import { SysCustomWidgetType } from '@/common/staticDict/index';
import { SysOnlineFieldKind } from '@/common/staticDict/online';
import { findItemFromList } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import { Dialog } from '@/components/Dialog';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
import MultiItemBox from '@/components/MultiItemBox/index.vue';

const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  rowData?: ANY_OBJECT;
  tableList?: ANY_OBJECT[];
  usedColumnList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = defineProps<IProps>();
const itemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const form = ref();
const formData = ref<ANY_OBJECT>({
  columnId: undefined,
  tableId: undefined,
  showName: undefined,
  sortable: false,
  inlineEdit: false,
  inlineEditWidgetType: undefined,
  dictParams: undefined,
  columnWidth: undefined,
  fieldType: 0,
  customFieldName: undefined,
  dateType: 'YYYY-MM-DD',
});

const dateTypeList = ref([
  { id: 'YYYY-MM-DD', name: '日' },
  { id: 'YYYY-MM', name: '月' },
  { id: 'YYYY', name: '年' },
  { id: 'YYYY-MM-DD HH:mm:ss', name: '时间' },
]);

const oldColumnId = ref<string>();
const rules: ANY_OBJECT = {
  showName: [{ required: true, message: '表格列名不能为空', trigger: 'blur' }],
  tableId: [{ required: true, message: '表格列绑定数据表不能为空', trigger: 'blur' }],
  columnId: [{ required: true, message: '表格列绑定字段不能为空', trigger: 'blur' }],
  customFieldName: [{ required: true, message: '自定义字段名不能为空', trigger: 'blur' }],
};
const dialogParams = computed<ANY_OBJECT>(() => {
  return {
    rowData: props.rowData || thirdParams.value.rowData,
    tableList: props.tableList || thirdParams.value.tableList,
    usedColumnList: props.usedColumnList || thirdParams.value.usedColumnList,
  };
});

const getCurrentTable = computed(() => {
  return findItemFromList(dialogParams.value.tableList, formData.value.tableId, 'tableId');
});

const getTableColumnList = computed<ANY_OBJECT[]>(() => {
  if (getCurrentTable.value != null) {
    return getCurrentTable.value.columnList.filter((item: ANY_OBJECT) => {
      return (
        dialogParams.value.usedColumnList.indexOf(item.columnId) === -1 ||
        oldColumnId.value === item.columnId
      );
    });
  } else {
    return [];
  }
});

const bindColumn = computed(() => {
  return getTableColumnList.value.find(item => item.columnId === formData.value.columnId);
});

const dictParamList = computed(() => {
  if (bindColumn.value == null || bindColumn.value.dictInfo == null) return [];
  let dictInfo = bindColumn.value.dictInfo;
  let dictData = dictInfo.dictDataJson ? JSON.parse(dictInfo.dictDataJson) : undefined;
  return dictData.paramList;
});

const supportInlineEdit = computed(() => {
  console.log('supportInlineEdit', bindColumn.value, formData.value.tableId);
  return (
    bindColumn.value != null && dialogParams.value.tableList[0].tableId === formData.value.tableId
  );
});

const getColumnDataType = computed(() => {
  if (bindColumn.value == null) return undefined;
  switch (bindColumn.value.objectFieldType) {
    case 'String':
      return 'String';
    case 'Date':
      return 'Date';
    case 'Boolean':
      return 'Boolean';
    case 'Integer':
    case 'Long':
    case 'Float':
    case 'Double':
    case 'BigDecimal':
      return 'Number';
    default:
      return undefined;
  }
});

const inlineEditWidgetTypeList = computed(() => {
  if (bindColumn.value == null || !formData.value.inlineEdit) return [];
  let validWidgetTypeList = [];
  switch (getColumnDataType.value) {
    case 'String':
      validWidgetTypeList = [SysCustomWidgetType.INLINE_INPUT, SysCustomWidgetType.INLINE_DATE];
      break;
    case 'Date':
      validWidgetTypeList = [SysCustomWidgetType.INLINE_DATE];
      break;
    case 'Boolean':
      validWidgetTypeList = [SysCustomWidgetType.INLINE_SWITCH];
      break;
    case 'Number':
      validWidgetTypeList = [
        SysCustomWidgetType.INLINE_NUMBER_INPUT,
        SysCustomWidgetType.INLINE_SWITCH,
      ];
      break;
    default:
      validWidgetTypeList = [];
      break;
  }
  if (bindColumn.value.dictInfo != null) {
    validWidgetTypeList = [SysCustomWidgetType.INLINE_SELECT];
    if (bindColumn.value.dictInfo.treeFlag) {
      validWidgetTypeList.push(SysCustomWidgetType.INLINE_CASCADE);
    }
  } else if (
    bindColumn.value.fieldKind === SysOnlineFieldKind.UPLOAD ||
    bindColumn.value.fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE
  ) {
    validWidgetTypeList = [SysCustomWidgetType.INLINE_UPLOAD];
  }
  let tempList = validWidgetTypeList.map(item => {
    return {
      id: item,
      name: SysCustomWidgetType.getValue(item),
    };
  });
  return tempList;
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};
const onSubmit = () => {
  form.value.validate((valid: boolean) => {
    if (!valid) return;
    // 生成唯一ID
    if (formData.value.fieldType === 1) {
      if (formData.value.id == null) formData.value.id = 'custom_' + new Date().getTime();
    } else {
      if (formData.value.id == null) formData.value.id = 'column_' + formData.value.columnId;
    }

    formData.value.table = getCurrentTable.value;
    formData.value.relationId =
      (getCurrentTable.value || {}).relationType == null ? undefined : getCurrentTable.value?.id;
    if (props.dialog) {
      props.dialog.submit(formData.value);
    } else {
      onCloseThirdDialog(true, dialogParams.value.rowData, formData.value);
    }
  });
};

const onTableChange = () => {
  formData.value.columnId = undefined;
};

const onColumnChange = () => {
  formData.value.column = findItemFromList(
    getTableColumnList.value,
    formData.value.columnId,
    'columnId',
  );
  formData.value.showName = formData.value.column ? formData.value.column.columnComment : undefined;
};

const onBindTypeChange = () => {
  formData.value.customFieldName = undefined;
  formData.value.tableId = undefined;
  formData.value.columnId = undefined;
  formData.value.sortable = false;
  formData.value.inlineEdit = false;
  formData.value.inlineEditWidgetType = undefined;
  formData.value.dictParams = undefined;
  if (form.value) form.value.clearValidate();
};

const handlerEditDictParams = (row: ANY_OBJECT, res: ANY_OBJECT) => {
  if (row == null) {
    if (formData.value.dictParams == null) formData.value.dictParams = [];
    formData.value.dictParams.push(res);
  } else {
    formData.value.dictParams = formData.value.dictParams.map(item => {
      return item.dictParamName === row.dictParamName ? res : item;
    });
  }
};

const onEditDictParamValue = (row: ANY_OBJECT) => {
  let validParamList = (dictParamList.value || []).filter(item => {
    let usedParamList = formData.value.dictParams;
    let temp = findItemFromList(usedParamList, item.dictParamName, 'dictParamName');
    return temp == null;
  });
  let columnList = [];
  if (getCurrentTable.value != null) {
    columnList = getCurrentTable.value.columnList;
  }
  Dialog.show<ANY_OBJECT>(
    row ? '编辑参数' : '添加参数',
    EditDictParamValue,
    {
      area: '600px',
    },
    {
      rowData: row,
      paramList: validParamList,
      columnList: columnList,
      path: 'thirdEditDictParamValue',
    },
    {
      width: '600px',
      height: '400px',
      pathName: '/thirdParty/thirdEditDictParamValue',
    },
  )
    .then(res => {
      handlerEditDictParams(row, res);
    })
    .catch(e => {
      console.warn(e);
    });
};

const onRemoveDictParamValue = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此参数？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    formData.value.dictParams = formData.value.dictParams.filter(
      item => item.dictParamName !== row.dictParamName,
    );
  });
};

onMounted(() => {
  if (dialogParams.value.rowData) {
    formData.value = {
      ...formData.value,
      ...dialogParams.value.rowData,
    };
    oldColumnId.value = formData.value.columnId;
  }
});
</script>
