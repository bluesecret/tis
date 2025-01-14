<template>
  <el-row class="form-edit-widget-categroy-column third-party-dlg">
    <el-form
      ref="formEditWidgetCategroyColumn"
      :model="formData"
      class="full-width-input form-box"
      :rules="rules"
      style="width: 100%; height: 570px"
      label-width="100px"
      :size="formItemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="isTreeColumn ? 24 : 12">
          <el-form-item label="数据集字段" prop="columnId">
            <el-select
              v-if="!isTreeColumn"
              v-model="formData.columnId"
              placeholder=""
              clearable
              :disabled="dialogParams.rowData != null"
              @change="onDatasetColumnChange"
            >
              <el-option
                v-for="item in dialogParams.columnList"
                :key="item.columnId"
                :label="item.columnName"
                :value="item.columnId"
                :disabled="item.disabled"
              >
                <el-row type="flex" justify="space-between" align="middle">
                  <span>{{ item.columnName }}</span>
                  <el-tag size="default" :type="item.dimension ? 'info' : 'success'">
                    {{ item.dimension ? '维度字段' : '指标字段' }}
                  </el-tag>
                </el-row>
              </el-option>
            </el-select>
            <el-cascader
              v-else
              :options="columnTree"
              placeholder=""
              clearable
              :modelValue="columnPath"
              @update:modelValue="onTreeColumnInput"
              :disabled="dialogParams.rowData != null"
              :props="{ label: 'columnName', value: 'columnId' }"
              @change="onDatasetColumnChange"
            >
              <template v-slot="{ data }">
                <el-row type="flex" justify="space-between" align="middle">
                  <span>{{ data.columnName }}</span>
                  <el-tag
                    v-if="data.fieldType !== 'Object' && data.fieldType !== 'Array'"
                    size="default"
                    :type="data.dimension ? 'info' : 'success'"
                  >
                    {{ data.dimension ? '维度字段' : '指标字段' }}
                  </el-tag>
                </el-row>
              </template>
            </el-cascader>
          </el-form-item>
        </el-col>
        <el-col :span="isTreeColumn ? 24 : 12">
          <el-form-item label="显示名称" prop="showName">
            <el-input v-model="formData.showName" placeholder="" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="isTreeColumn ? 24 : 12" v-if="!isTreeColumn">
          <el-form-item label="汇总方式" prop="calculateType">
            <el-select v-model="formData.calculateType" placeholder="">
              <el-option
                v-for="item in CalculateType.getList()"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="isTreeColumn ? 24 : 12">
          <el-form-item label="小数位数" prop="calculateType">
            <el-input-number
              v-model="formData.fixed"
              controls-position="right"
              :min="0"
              :max="10"
            />
          </el-form-item>
        </el-col>
        <el-col :span="isTreeColumn ? 24 : 12" v-if="dialogParams.supportColumnWidth">
          <el-form-item label="字段最小宽度">
            <el-input
              v-model="formData.columnWidth"
              placeholder="最小列宽度，会自动将剩余空间按比例分配"
              clearable
            />
          </el-form-item>
        </el-col>
        <el-col :span="24" style="margin-bottom: 15px" v-if="!isTreeColumn">
          <el-row type="flex" justify="space-between" align="middle">
            <span class="el-form-item__label"></span>
            <el-button type="text" @click="onEditFilter()" :size="formItemSize">添加过滤</el-button>
          </el-row>
          <el-row>
            <el-table
              :data="formData.filterList"
              :size="formItemSize"
              header-cell-class-name="table-header-gray"
              height="350px"
            >
              <el-table-column label="过滤字段" width="200px">
                <template>
                  <span>{{ (getFilterColumn || {}).columnName }}</span>
                </template>
              </el-table-column>
              <el-table-column label="过滤规则" width="150px">
                <template v-slot="scope">
                  <el-select
                    v-model="scope.row.filterType"
                    size="default"
                    @change="val => onFilterTypeChange(scope.row, val)"
                  >
                    <el-option
                      v-for="item in getValidCriteriaFilterType"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="过滤值类型" width="150px">
                <template v-slot="scope">
                  <el-select
                    v-model="scope.row.filterValueType"
                    size="default"
                    placeholder=""
                    :disabled="!needFilterValue(scope.row)"
                    @change="onFilterValueTypeChange(scope.row)"
                  >
                    <el-option
                      v-for="item in getValidFilteValueType(scope.row)"
                      :key="item"
                      :label="FilterValueKind.getValue(item)"
                      :value="item"
                    />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="过滤值">
                <template v-slot="scope">
                  <!-- 表单输入参数 -->
                  <el-select
                    v-if="scope.row.filterValueType === FilterValueKind.FORM_PARAM"
                    style="width: 200px"
                    v-model="scope.row.paramValue"
                    size="default"
                    clearable
                    placeholder=""
                  >
                    <el-option
                      v-for="name in dialogParams.formParamList"
                      :key="name"
                      :label="name"
                      :value="name"
                    />
                  </el-select>
                  <!-- 组件数据参数 -->
                  <el-select
                    v-if="scope.row.filterValueType === FilterValueKind.WIDGET_DATA"
                    style="width: 200px"
                    v-model="scope.row.formWidgetId"
                    key="formWidgetId"
                    size="default"
                    clearable
                    placeholder=""
                    @change="scope.row.formWidgetColumn = undefined"
                  >
                    <el-option
                      v-for="widget in dialogParams.widgetList"
                      :key="widget.widgetId"
                      :label="widget.showName"
                      :value="widget.widgetId"
                    />
                  </el-select>
                  <el-select
                    v-if="
                      scope.row.filterValueType === FilterValueKind.WIDGET_DATA &&
                      getFilterWidget(scope.row) != null &&
                      Array.isArray(getFilterWidget(scope.row)?.children) &&
                      getFilterWidget(scope.row)?.children.length > 0
                    "
                    v-model="scope.row.formWidgetColumn"
                    key="formWidgetColumn"
                    style="width: 200px; margin-left: 15px"
                    size="default"
                    clearable
                    placeholder=""
                  >
                    <el-option
                      v-for="column in (getFilterWidget(scope.row) || {}).children"
                      :key="column.id"
                      :label="column.showName"
                      :value="column.id"
                    />
                  </el-select>
                  <!-- 字典参数 -->
                  <el-select
                    v-if="scope.row.filterValueType === FilterValueKind.DICT_DATA"
                    style="width: 200px"
                    placeholder="请选择参数值字典"
                    v-model="scope.row.paramDictId"
                    size="default"
                    clearable
                    @change="onFilterDictChange(scope.row)"
                  >
                    <el-option
                      v-for="dict in getValidDictList"
                      :key="dict.id"
                      :label="dict.name"
                      :value="dict.id"
                      :disabled="dict.disabled"
                    />
                  </el-select>
                  <el-select
                    v-if="scope.row.filterValueType === FilterValueKind.DICT_DATA"
                    style="width: 200px; margin-left: 15px"
                    v-model="scope.row.paramValue"
                    size="default"
                    clearable
                    placeholder="请选择参数值"
                    :multiple="
                      scope.row.filterType === CriteriaFilterType.IN ||
                      scope.row.filterType === CriteriaFilterType.NOT_IN
                    "
                  >
                    <el-option
                      v-for="data in scope.row.paramDictDataList"
                      :key="data.id"
                      :label="data.name"
                      :value="data.id"
                    />
                  </el-select>
                  <!-- 字段参数 -->
                  <el-select
                    v-if="scope.row.filterValueType === FilterValueKind.COLUMN_DATA"
                    style="width: 200px"
                    v-model="scope.row.paramValue"
                    size="default"
                    clearable
                    placeholder=""
                    :multiple="
                      scope.row.filterType === CriteriaFilterType.IN ||
                      scope.row.filterType === CriteriaFilterType.NOT_IN
                    "
                  >
                    <el-option
                      v-for="data in (getFilterColumn || {}).columnDataList"
                      :key="data.id"
                      :label="data.name"
                      :value="data.id"
                    />
                  </el-select>
                  <!-- 内置变量 -->
                  <el-select
                    v-if="scope.row.filterValueType === FilterValueKind.INNER_VARIABLE"
                    style="width: 200px"
                    v-model="scope.row.paramValue"
                    size="default"
                    clearable
                    placeholder=""
                    :disabled="!needFilterValue(scope.row)"
                  >
                    <el-option
                      v-for="item in CustomDateValueType.getList()"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                  <!-- 自定义参数 -->
                  <template v-if="scope.row.filterValueType === FilterValueKind.INPUT_DATA">
                    <!-- 数字类型字段 -->
                    <el-input-number
                      style="width: 200px"
                      v-model="scope.row.paramValue"
                      controls-position="right"
                      size="default"
                      placeholder=""
                      :disabled="!needFilterValue(scope.row)"
                    />
                  </template>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150px">
                <template v-slot="scope">
                  <el-button type="text" @click="onRemoveFilter(scope.row)" :size="formItemSize"
                    >删除</el-button
                  >
                </template>
              </el-table-column>
              <template v-slot:empty>
                <div class="table-empty unified-font">
                  <img src="@/assets/img/empty.png" />
                  <span>暂无数据</span>
                </div>
              </template>
            </el-table>
          </el-row>
        </el-col>
      </el-row>
    </el-form>
    <el-row class="no-scroll menu-box" type="flex" justify="end">
      <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="formItemSize" @click="onSubmit"> 保存 </el-button>
    </el-row>
  </el-row>
</template>

<script setup lang="ts">
import { CascaderValue, ElMessage, ElMessageBox } from 'element-plus';
import { treeDataTranslate, findTreeNodePath, findItemFromList } from '@/common/utils';
import { ReportDatasetController, ReportDictController } from '@/api/report';
import { getValidFilterValueTypeByFieldType } from '@/components/Charts/utils';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import {
  CalculateType,
  CriteriaFilterType,
  CustomDateValueType,
  DatasetType,
  FilterValueKind,
} from '@/common/staticDict/report';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  rowData?: ANY_OBJECT;
  columnList: ANY_OBJECT[];
  dataset?: ANY_OBJECT;
  supportColumnWidth?: boolean;
  formParamList?: (string | number)[];
  reportDictList?: ANY_OBJECT[];
  widgetList?: ANY_OBJECT[];
  defaultFormItemSize: Ref<'' | 'default' | 'small' | 'large'>;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
}
const props = withDefaults(defineProps<IProps>(), {
  supportColumnWidth: false,
});
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const formEditWidgetCategroyColumn = ref();
const formData = ref<ANY_OBJECT>({
  id: undefined,
  columnId: undefined,
  showName: undefined,
  fixed: undefined,
  calculateType: CalculateType.SUM,
  columnWidth: undefined,
  filterList: [],
});
const rules = {
  columnId: [{ required: true, message: '请选择数据集字段！', trigger: 'blur' }],
  showName: [{ required: true, message: '显示名称不能为空！', trigger: 'blur' }],
};

const isTreeColumn = computed(() => {
  return dialogParams.value.dataset.datasetType === DatasetType.API;
});
const columnTree = computed(() => {
  let tempList = dialogParams.value.columnList.map((item: ANY_OBJECT) => {
    return {
      ...item,
    };
  });
  return treeDataTranslate(tempList, 'columnId');
});
const columnPath = computed(() => {
  if (formData.value.columnId == null || formData.value.columnId === '') return [];
  return findTreeNodePath(columnTree.value, formData.value.columnId, 'columnId');
});
const dialogParams = computed(() => {
  return {
    rowData: props.rowData || thirdParams.value.rowData,
    dataset: props.dataset || thirdParams.value.dataset || {},
    supportColumnWidth: thirdParams.value.supportColumnWidth
      ? thirdParams.value.supportColumnWidth
      : props.supportColumnWidth,
    columnList: (props.columnList || thirdParams.value.columnList || []).map((item: ANY_OBJECT) => {
      return {
        ...item,
        children: undefined,
      } as ANY_OBJECT;
    }),
    formParamList: props.formParamList || thirdParams.value.formParamList || [],
    reportDictList: props.reportDictList || thirdParams.value.reportDictList || [],
    widgetList: props.widgetList || thirdParams.value.widgetList || [],
  };
});
const validFilterValueType = computed(() => {
  return [
    FilterValueKind.FORM_PARAM,
    FilterValueKind.WIDGET_DATA,
    FilterValueKind.DICT_DATA,
    FilterValueKind.COLUMN_DATA,
    FilterValueKind.INNER_VARIABLE,
    FilterValueKind.INPUT_DATA,
  ];
});
const getFilterColumn = computed(() => {
  return findItemFromList(dialogParams.value.columnList, formData.value.columnId, 'columnId');
});
const getValidCriteriaFilterType = computed(() => {
  return CriteriaFilterType.getList().filter((item: ANY_OBJECT) => {
    return (
      [
        CriteriaFilterType.EQ,
        CriteriaFilterType.NOT_EQ,
        CriteriaFilterType.GE,
        CriteriaFilterType.GT,
        CriteriaFilterType.LE,
        CriteriaFilterType.LT,
      ].indexOf(item.id) !== -1
    );
  });
});
const getValidDictList = computed(() => {
  if (getFilterColumn.value == null) return [];
  return dialogParams.value.reportDictList.map((item: ANY_OBJECT) => {
    return {
      ...item,
      disabled: !getFilterColumn.value?.dictId ? false : item.id !== getFilterColumn.value.dictId,
      dictDataList: item.dictDataList,
    } as ANY_OBJECT;
  });
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};
const onSubmit = () => {
  formEditWidgetCategroyColumn.value.validate((valid: boolean) => {
    if (!valid) return;
    if (!checkFilter()) return;
    if (props.dialog) {
      props.dialog.submit(formData.value);
    } else {
      onCloseThirdDialog(true, dialogParams.value.rowData, formData.value);
    }
  });
};

const onTreeColumnInput = (value: ANY_OBJECT) => {
  formData.value.columnId = Array.isArray(value) ? value[value.length - 1] : undefined;
};
const onEditFilter = (row: ANY_OBJECT | null = null) => {
  if (row == null) {
    formData.value.filterList.push({
      paramId: undefined,
      required: false,
      filterType: CriteriaFilterType.EQ,
      filterValueType: FilterValueKind.INPUT_DATA,
      // 字典过滤
      paramDictId: undefined,
      // 表单组件参数
      formWidgetId: undefined,
      formWidgetColumn: undefined,
      // 参数值
      paramValue: undefined,
      relationId: undefined,
      paramDictDataList: [],
    });
  }
};
const onRemoveFilter = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此过滤条件？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      formData.value.filterList = formData.value.filterList.filter((item: ANY_OBJECT) => {
        return row !== item;
      });
    })
    .catch(e => {
      console.warn(e);
    });
};
const onDatasetColumnChange = (value: CascaderValue) => {
  let temp = findItemFromList(dialogParams.value.columnList, value.toString(), 'columnId');
  formData.value.showName = temp ? temp.columnName : undefined;
  formData.value.filterList = [];
};
const checkFilter = () => {
  if (Array.isArray(formData.value.filterList)) {
    for (let i = 0; i < formData.value.filterList.length; i++) {
      let filterItem = formData.value.filterList[i];
      switch (filterItem.filterValueType) {
        case FilterValueKind.WIDGET_DATA: {
          let filterWidget = getFilterWidget(filterItem);
          let hasColumn = Array.isArray(filterWidget?.children) && filterWidget.children.length > 0;
          let selectColumn =
            filterItem.formWidgetColumn != null && filterItem.formWidgetColumn !== '';
          if (filterWidget == null || (hasColumn && !selectColumn)) {
            ElMessage.error('过滤条件存在空值！');
            return false;
          }
          break;
        }
        case FilterValueKind.FORM_PARAM:
        case FilterValueKind.DICT_DATA:
        case FilterValueKind.COLUMN_DATA:
        case FilterValueKind.PRINT_INPUT_PARAM:
        case FilterValueKind.INNER_VARIABLE:
        case FilterValueKind.INPUT_DATA:
          if (filterItem.paramValue == null || filterItem.paramValue === '') {
            ElMessage.error('过滤条件存在空值！');
            return false;
          }
          break;
      }
    }
  }

  return true;
};
const getValidFilteValueType = (row: ANY_OBJECT) => {
  let column = getFilterColumn.value;
  if (column == null) return validFilterValueType.value;
  // 指标字段必然为数字类型，所以修改filedType为Double
  return getValidFilterValueTypeByFieldType(validFilterValueType.value, row.filterType, {
    ...column,
    fieldType: 'Double',
  });
};
const needFilterValue = (row: ANY_OBJECT) => {
  return (
    row.filterType !== CriteriaFilterType.NOT_NULL && row.filterType !== CriteriaFilterType.IS_NULL
  );
};
const onFilterTypeChange = (row: ANY_OBJECT, val: number) => {
  if (val === CriteriaFilterType.IN || val === CriteriaFilterType.NOT_IN) {
    row.filterValueType = FilterValueKind.DICT_DATA;
  } else {
    row.filterValueType = FilterValueKind.INPUT_DATA;
  }
  onFilterValueTypeChange(row);
};
const onFilterValueTypeChange = (row: ANY_OBJECT) => {
  row.paramDictId = undefined;
  row.innerVeriable = undefined;
  row.paramValue = undefined;
  row.paramDictDataList = [];
};
const getFilterValueDict = (row: ANY_OBJECT) => {
  for (let i = 0; i < getValidDictList.value.length; i++) {
    if (row.paramDictId === getValidDictList.value[i].id) return getValidDictList.value[i];
  }

  return null;
};
const getFilterWidget = (row: ANY_OBJECT | null) => {
  return findItemFromList(dialogParams.value.widgetList, row?.formWidgetId, 'widgetId');
};
const onFilterDictChange = (row: ANY_OBJECT) => {
  loadReportDictDataList(row, getFilterValueDict(row));
  row.paramValue = undefined;
};
const listDictData = (dictId: string) => {
  return new Promise((resolve, reject) => {
    ReportDictController.listDictData({
      dictId: dictId,
    })
      .then(res => {
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const listColumnData = (datasetId: string, columnId: string) => {
  if (isTreeColumn.value) return Promise.resolve();
  return new Promise<ANY_OBJECT[]>((resolve, reject) => {
    ReportDatasetController.listDataWithColumn({
      datasetId: datasetId,
      columnId: columnId,
    })
      .then(res => {
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const loadReportDictDataList = (row: ANY_OBJECT, node: ANY_OBJECT | null) => {
  if (node == null || Array.isArray(node.dictDataList)) {
    row.paramDictDataList = (node || {}).dictDataList;
    return;
  }
  listDictData(node.id)
    .then(dataList => {
      row.paramDictDataList = dataList;
      node.dictDataList = dataList;
    })
    .catch(e => {
      console.warn(e);
    });
};
const loadColumnDataList = () => {
  if (!getFilterColumn.value) return;
  if (Array.isArray(getFilterColumn.value.columnDataList)) return;
  listColumnData(getFilterColumn.value.datasetId, getFilterColumn.value.columnId)
    .then(dataList => {
      if (getFilterColumn.value) {
        let columnName = getFilterColumn.value.columnName;
        getFilterColumn.value.columnDataList = (dataList || []).map((item: ANY_OBJECT) => {
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
    if (formData.value.fixed == null) formData.value.fixed = undefined;
    if (Array.isArray(formData.value.filterList)) {
      formData.value.filterList = formData.value.filterList.map((item: ANY_OBJECT) => {
        return {
          ...item,
        };
      });
      formData.value.filterList.forEach((item: ANY_OBJECT) => {
        loadReportDictDataList(item, getFilterValueDict(item));
      });
    }
    loadColumnDataList();
  }
});
</script>
