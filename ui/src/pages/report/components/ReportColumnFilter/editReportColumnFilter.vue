<template>
  <el-form
    class="edit-report-template-filter third-party-dlg"
    ref="form"
    :model="formData"
    :rules="rules"
    label-width="92px"
    size="default"
    @submit.prevent
  >
    <el-row class="full-width-input form-box">
      <el-col :span="24">
        <el-form-item label="过滤字段" prop="paramName">
          <el-cascader
            v-model="formData.paramPath"
            :options="dialogParams.columnList"
            :props="{ label: 'name', value: 'id' }"
            style="width: 100%"
            size="default"
            @change="onFilterColumnChange"
          >
            <template v-slot="{ data }">
              <el-row type="flex" justify="space-between" align="middle">
                <span>{{ data.name }}</span>
                <el-tag v-if="data.dictId != null" size="default" style="margin-left: 20px"
                  >字典</el-tag
                >
              </el-row>
            </template>
          </el-cascader>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="是否必填" required>
          <el-switch v-model="formData.required" />
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="过滤规则" prop="filterType">
          <el-select v-model="formData.filterType" size="default" @change="onFilterTypeChange">
            <el-option
              v-for="item in getValidCriteriaFilterType"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="过滤值类型" prop="filterValueType">
          <el-select
            v-model="formData.filterValueType"
            size="default"
            placeholder=""
            :disabled="!needFilterValue"
            @change="onFilterValueTypeChange"
          >
            <el-option
              v-for="item in getValidFilteValueType"
              :key="item"
              :label="FilterValueKind.getValue(item)"
              :value="item"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <!-- 参数值 -->
      <el-col :span="24">
        <!-- 表单输入参数 -->
        <el-form-item
          v-if="formData.filterValueType === FilterValueKind.FORM_PARAM"
          label="过滤值"
          :prop="needFilterValue ? 'paramValue' : undefined"
          :disabled="!needFilterValue"
        >
          <el-select v-model="formData.paramValue" size="default" clearable placeholder="">
            <el-option
              v-for="name in dialogParams.formParamList"
              :key="name"
              :label="name"
              :value="name"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <!-- 组件数据参数 -->
        <el-form-item
          v-if="formData.filterValueType === FilterValueKind.WIDGET_DATA"
          label="过滤组件"
          :prop="needFilterValue ? 'formWidgetId' : undefined"
          :disabled="!needFilterValue"
        >
          <el-select
            v-model="formData.formWidgetId"
            key="formWidgetId"
            size="default"
            clearable
            placeholder=""
            @change="formData.formWidgetColumn = undefined"
          >
            <el-option
              v-for="widget in dialogParams.widgetList"
              :key="widget.id"
              :label="widget.showName"
              :value="widget.id"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item
          v-if="
            formData.filterValueType === FilterValueKind.WIDGET_DATA &&
            getFilterWidget != null &&
            Array.isArray(getFilterWidget.children) &&
            getFilterWidget.children.length > 0
          "
          label="组件字段"
          :prop="needFilterValue ? 'formWidgetColumn' : undefined"
          :disabled="!needFilterValue"
        >
          <el-select
            v-model="formData.formWidgetColumn"
            key="formWidgetColumn"
            size="default"
            clearable
            placeholder=""
          >
            <el-option
              v-for="column in (getFilterWidget || {}).children"
              :key="column.id"
              :label="column.showName"
              :value="column.id"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <!-- 字典参数 -->
        <el-form-item
          v-if="formData.filterValueType === FilterValueKind.DICT_DATA"
          label="过滤值字典"
          :prop="needFilterValue ? 'paramDictId' : undefined"
          :disabled="!needFilterValue"
        >
          <el-row type="flex">
            <el-select
              v-model="formData.paramDictId"
              key="paramDictId"
              style="width: 200px; flex-grow: 1"
              size="default"
              clearable
              placeholder=""
              :disabled="getFilterColumn == null || getFilterColumn.dictId != null"
              @change="formData.paramValue = undefined"
            >
              <el-option
                v-for="dict in getValidDictList"
                :key="dict.id"
                :label="dict.name"
                :value="dict.id"
                :disabled="dict.disabled"
              />
            </el-select>
            <span
              v-if="(getFilterColumn || {}).dictId != null"
              style="margin-left: 15px; color: #e6a23c; flex-grow: 0"
              >只显示字段绑定字典</span
            >
          </el-row>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item
          v-if="formData.filterValueType === FilterValueKind.DICT_DATA"
          label="字典数据"
          :prop="needFilterValue ? 'paramValue' : undefined"
          :disabled="!needFilterValue"
        >
          <el-select
            v-model="formData.paramValue"
            size="default"
            clearable
            placeholder=""
            :multiple="
              formData.filterType === CriteriaFilterType.IN ||
              formData.filterType === CriteriaFilterType.NOT_IN
            "
          >
            <el-option
              v-for="data in paramDictDataList"
              :key="data.id"
              :label="data.name"
              :value="data.id"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <!-- 字段参数 -->
        <el-form-item
          v-if="formData.filterValueType === FilterValueKind.COLUMN_DATA"
          label="参数值"
          :prop="needFilterValue ? 'paramValue' : undefined"
          :disabled="!needFilterValue"
        >
          <el-select
            v-model="formData.paramValue"
            size="default"
            clearable
            placeholder=""
            :multiple="
              formData.filterType === CriteriaFilterType.IN ||
              formData.filterType === CriteriaFilterType.NOT_IN
            "
          >
            <el-option
              v-for="data in (getFilterColumn || {}).columnDataList"
              :key="data.id"
              :label="data.name"
              :value="data.id"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <!-- 打印模版输入参数 -->
        <el-form-item
          v-if="formData.filterValueType === FilterValueKind.PRINT_INPUT_PARAM"
          label="过滤值"
          :prop="needFilterValue ? 'paramValue' : undefined"
        >
          <el-select
            v-model="formData.paramValue"
            size="default"
            clearable
            placeholder=""
            :disabled="!needFilterValue"
          >
            <el-option
              v-for="name in dialogParams.formParamList"
              :key="name"
              :label="name"
              :value="name"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <!-- 内置变量 -->
        <el-form-item
          v-if="formData.filterValueType === FilterValueKind.INNER_VARIABLE"
          label="过滤值"
          :prop="needFilterValue ? 'paramValue' : undefined"
        >
          <el-select
            v-model="formData.paramValue"
            size="default"
            clearable
            placeholder=""
            :disabled="!needFilterValue"
          >
            <el-option
              v-for="item in CustomDateValueType.getList()"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <!-- 自定义参数 -->
        <el-form-item
          v-if="formData.filterValueType === FilterValueKind.INPUT_DATA"
          label="过滤值"
          :prop="needFilterValue ? 'paramValue' : undefined"
        >
          <!-- 数字类型字段 -->
          <el-input-number
            v-if="
              getFilterColumn != null &&
              ['Integer', 'Long', 'Double', 'BigDecimal'].indexOf(getFilterColumn.fieldType) !== -1
            "
            v-model="formData.paramValue"
            controls-position="right"
            size="default"
            placeholder=""
            :disabled="!needFilterValue"
          />
          <!-- Boolean类型字段 -->
          <el-switch
            v-else-if="getFilterColumn != null && getFilterColumn.fieldType === 'Boolean'"
            v-model="formData.paramValue"
            size="default"
            :disabled="!needFilterValue"
          />
          <!-- 日期类型字段 -->
          <el-date-picker
            v-else-if="getFilterColumn != null && getFilterColumn.fieldType === 'Date'"
            v-model="formData.paramValue"
            type="datetime"
            size="default"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            :disabled="!needFilterValue"
          >
          </el-date-picker>
          <!-- 字符串类型字段 -->
          <el-input
            v-else
            :disabled="!needFilterValue"
            v-model="formData.paramValue"
            size="default"
            placeholder=""
            clearable
          />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row type="flex" justify="end" class="menu-box" style="margin-top: 15px">
      <el-button :size="itemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="itemSize" @click="onSubmit"> 确定 </el-button>
    </el-row>
  </el-form>
</template>

<script setup lang="ts">
import { CascaderValue } from 'element-plus';
import {
  getFilterTypeByFieldType,
  getValidFilterValueTypeByFieldType,
} from '@/components/Charts/utils';
import {
  CriteriaFilterType,
  CustomDateValueType,
  FilterValueKind,
} from '@/common/staticDict/report';
import { ReportDictController, ReportDatasetController } from '@/api/report';
import { findItemFromList } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  rowData?: ANY_OBJECT;
  columnList?: ANY_OBJECT[];
  // 打印模版输入参数 / 表单输入参数
  formParamList?: string[];
  // 报表字典列表
  reportDictList?: ANY_OBJECT[];
  // 组件列表
  widgetList?: ANY_OBJECT[];
  // 可用的过滤值类型
  validFilterValueType?: number[];
  defaultFormItemSize: Ref<'' | 'default' | 'small' | 'large'>;
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
  paramId: undefined,
  paramName: undefined,
  required: false,
  filterType: undefined,
  filterValueType: FilterValueKind.INPUT_DATA,
  // 字典过滤
  paramDictId: undefined,
  // 表单组件参数
  formWidgetId: undefined,
  formWidgetColumn: undefined,
  // 参数值
  paramValue: undefined,
  relationId: undefined,
  paramPath: [],
});
// 字典数据值
const paramDictDataList = ref<ANY_OBJECT[]>([]);
const rules: ANY_OBJECT = {
  paramName: [{ required: true, message: '过滤字段不能为空', trigger: 'blur' }],
  filterType: [{ required: true, message: '过滤规则不能为空', trigger: 'blur' }],
  filterValueType: [{ required: true, message: '过滤值类型不能为空', trigger: 'blur' }],
  paramDictId: [{ required: true, message: '过滤值字典不能为空', trigger: 'blur' }],
  formWidgetId: [{ required: true, message: '过滤组件不能为空', trigger: 'blur' }],
  formWidgetColumn: [{ required: true, message: '过滤组件字段不能为空', trigger: 'blur' }],
  paramValue: [{ required: true, message: '过滤值不能为空', trigger: 'blur' }],
};

const dialogParams = computed(() => {
  return {
    rowData: props.rowData || thirdParams.value.rowData,
    columnList: props.columnList || thirdParams.value.columnList || [],
    formParamList: props.formParamList || (thirdParams.value.formParamList as string[]) || [],
    reportDictList:
      props.reportDictList || (thirdParams.value.reportDictList as ANY_OBJECT[]) || [],
    widgetList: props.widgetList || thirdParams.value.widgetList || [],
    validFilterValueType:
      props.validFilterValueType || (thirdParams.value.validFilterValueType as number[]) || [],
  };
});
const needFilterValue = computed(() => {
  return (
    formData.value.filterType !== CriteriaFilterType.NOT_NULL &&
    formData.value.filterType !== CriteriaFilterType.IS_NULL
  );
});
const getFilterColumn = computed(() => {
  if (formData.value.relationId == null) {
    // 绑定主数据集字段
    return findItemFromList(dialogParams.value.columnList, formData.value.paramName, 'id');
  } else {
    // 绑定关联数据集字段
    let relation = findItemFromList(dialogParams.value.columnList, formData.value.relationId, 'id');
    if (relation != null) {
      return findItemFromList(relation.children, formData.value.paramName, 'id');
    }
  }

  return null;
});
const getValidCriteriaFilterType = computed(() => {
  let column = getFilterColumn.value;
  if (column == null) return [];
  let temp = getFilterTypeByFieldType(column.fieldType);
  return temp;
});
const getValidFilteValueType = computed(() => {
  let column = getFilterColumn.value;
  if (column == null) return dialogParams.value.validFilterValueType;
  return getValidFilterValueTypeByFieldType(
    dialogParams.value.validFilterValueType,
    formData.value.filterType,
    column,
  );
});
const getValidDictList = computed(() => {
  if (getFilterColumn.value == null) return [] as ANY_OBJECT[];
  return dialogParams.value.reportDictList.map(item => {
    return {
      ...item,
      // disabled: getFilterColumn.value.dictId == null ? false : (item.id !== getFilterColumn.value.dictId),
      dictDataList: item.dictDataList,
    } as ANY_OBJECT;
  });
});
const getFilterValueDict = computed(() => {
  for (let i = 0; i < getValidDictList.value.length; i++) {
    if (formData.value.paramDictId === getValidDictList.value[i].id)
      return getValidDictList.value[i];
  }

  return null;
});
const getFilterWidget = computed(() => {
  return findItemFromList(dialogParams.value.widgetList, formData.value.formWidgetId, 'widgetId');
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
    if (props.dialog) {
      props.dialog.submit(formData.value);
    } else {
      onCloseThirdDialog(true, dialogParams.value.rowData, formData.value);
    }
  });
};
const onFilterColumnChange = (val: CascaderValue) => {
  formData.value.filterType = CriteriaFilterType.EQ;
  formData.value.required = false;
  formData.value.relationId = undefined;
  if (Array.isArray(val) && val.length > 0) {
    formData.value.paramName = val[val.length - 1];
    if (val.length === 2) {
      formData.value.relationId = val[0];
    }
  } else {
    formData.value.paramName = undefined;
  }
  onFilterTypeChange(CriteriaFilterType.EQ);
};
const onFilterTypeChange = (val: number) => {
  if (val === CriteriaFilterType.IN || val === CriteriaFilterType.NOT_IN) {
    formData.value.filterValueType = FilterValueKind.DICT_DATA;
  } else {
    formData.value.filterValueType = FilterValueKind.INPUT_DATA;
  }
  onFilterValueTypeChange();
};
const onFilterValueTypeChange = () => {
  formData.value.formInputParam = undefined;
  formData.value.reportPrintVariable = undefined;
  formData.value.customParam = undefined;
  formData.value.paramDictId = (getFilterColumn.value || {}).dictId;
  formData.value.paramDictData = undefined;
  formData.value.paramColumnData = undefined;
  formData.value.innerVeriable = undefined;
  formData.value.paramValue = undefined;
};
const loadReportDictDataList = (node: ANY_OBJECT | null) => {
  if (node == null || Array.isArray(node.dictDataList)) {
    paramDictDataList.value = (node || {}).dictDataList;
    return;
  }
  ReportDictController.listDictData({
    dictId: node.id,
  })
    .then(res => {
      paramDictDataList.value = res.data;
      node.dictDataList = res.data;
    })
    .catch(e => {
      console.warn(e);
    });
};
const loadColumnDataList = () => {
  if (getFilterColumn.value == null) return;
  if (Array.isArray(getFilterColumn.value.columnDataList)) return;
  ReportDatasetController.listDataWithColumn({
    datasetId: getFilterColumn.value.datasetId,
    columnId: getFilterColumn.value.columnId,
  })
    .then(res => {
      let columnName = getFilterColumn.value?.columnName;
      if (getFilterColumn.value) {
        getFilterColumn.value.columnDataList = res.data.map(item => {
          if (item[columnName + '__DictMap'] != null) {
            return {
              ...item[columnName + '__DictMap'],
            };
          } else {
            return {
              id: item[columnName],
              name: item[columnName],
            };
          }
        });
      }
    })
    .catch(e => {
      console.warn(e);
    });
};

watch(
  () => getFilterValueDict.value,
  newValue => {
    loadReportDictDataList(newValue);
  },
  {
    immediate: true,
  },
);
watch(
  () => getFilterColumn.value,
  () => {
    loadColumnDataList();
  },
  {
    immediate: true,
  },
);

onMounted(() => {
  if (dialogParams.value.rowData != null) {
    formData.value = {
      ...formData.value,
      ...dialogParams.value.rowData,
    };

    formData.value.paramPath =
      formData.value.relationId == null
        ? [formData.value.paramName]
        : [formData.value.relationId, formData.value.paramName];
    loadReportDictDataList(getFilterValueDict.value);
    loadColumnDataList();
  } else {
    formData.value.paramId = new Date().getTime();
  }
});
</script>
