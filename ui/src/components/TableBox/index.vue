<template>
  <div class="table-box">
    <el-row
      type="flex"
      justify="space-between"
      align="middle"
      class="operator-box"
      v-if="hasExtend || $slots.operator"
    >
      <el-row type="flex">
        <slot name="operator" />
        <el-button
          v-if="customQueryOptions != null"
          type="primary"
          style="margin-left: 8px"
          :size="layoutStore.defaultFormItemSize"
          plain
          @click="onCustomQueryClick"
          >高级查询</el-button
        >
      </el-row>
      <div class="extend-box" v-if="hasExtend">
        <i
          class="online-icon icon-custom-refresh"
          style="font-size: 20px; color: #333; cursor: pointer"
          @click="refresh"
        />
        <i
          class="online-icon icon-table-row-height"
          style="font-size: 20px; color: #333; cursor: pointer; margin-left: 4px"
          @click="onRowHeightToogle"
        />
        <el-icon
          v-if="supportTableConfig"
          style="font-size: 20px; color: #333333; cursor: pointer; margin-left: 4px"
          @click="onCustomTableConfigClick"
        >
          <Setting />
        </el-icon>
      </div>
    </el-row>
    <div class="vxe-table-box">
      <el-form
        class="inline-row-form"
        ref="inlineEditForm"
        :model="inlineEditConfig.rowCopy"
        :rules="props.inlineRules"
        :show-message="false"
        size="small"
        label-width="0px"
        label-position="left"
        style="height: 100%"
        @submit.prevent
      >
        <vxe-table
          ref="table"
          border="inner"
          height="100%"
          :data="data"
          :class="rowHeightClass"
          :key="tableKey"
          v-bind="$attrs"
          :row-config="{ ...($attrs.rowConfig || {}), isHover: true }"
          :checkbox-config="{ checkMethod: checkedMethod }"
          header-cell-class-name="table-header-gray"
          @checkbox-change="onCheckBoxChange"
          @checkbox-all="onCheckAllChange"
          @radio-change="onRadioSelectChange"
        >
          <slot></slot>
          <template v-slot:empty>
            <div class="table-empty unified-font">
              <img src="@/assets/img/empty.png" />
              <span>暂无数据</span>
            </div>
          </template>
        </vxe-table>
      </el-form>
    </div>
    <slot class="pagination-box" name="pagination"></slot>
  </div>
</template>

<script setup lang="ts">
import { useAttrs } from 'vue';
import { FormInstance, ElMessage } from 'element-plus';
import { VxeTable, VxeTableInstance, VxeTableEvents, VxeTablePropTypes } from 'vxe-table';
import { useLayoutStore } from '@/store';
import { Dialog } from '@/components/Dialog';
import { ANY_OBJECT, T } from '@/types/generic';
import { traverseTree } from '@/common/utils';
import { InlineTableEditConifg } from './types';
import CustomQuery from './CustomQuery/index.vue';
import CustomConfig from './CustomConfig/index.vue';

const layoutStore = useLayoutStore();

const attrs = useAttrs();
// 定义事件
const emit = defineEmits<{
  (e: 'refresh'): void;
  (e: 'update:value', rows: T[]): void;
  (e: 'checkbox-select-change', params: T[]): void;
  (e: 'radio-select-change', param: ANY_OBJECT | null): void;
  (e: 'change', rows: T[], multi: boolean): void;
  (e: 'custom-query', param: ANY_OBJECT): void;
  (e: 'table-config', param: ANY_OBJECT): void;
}>();

// Props
const props = withDefaults(
  defineProps<{
    data: T[];
    value?: T | T[];
    height?: string | number;
    hasImageColumn?: boolean;
    hasExtend?: boolean;
    supportTableConfig?: boolean;
    checkedMethod?: () => boolean;
    inlineRules?: ANY_OBJECT;
    rowKey?: string;
    customQueryOptions?: ANY_OBJECT;
    customConfig?: ANY_OBJECT;
  }>(),
  {
    hasExtend: true,
    height: 'auto',
    hasImageColumn: false,
    supportTableConfig: false,
    checkedMethod: () => {
      return true;
    },
    inlineRules: undefined,
    rowKey: undefined,
  },
);

// 组件引用
const table = ref<VxeTableInstance>();
// form组件引用
const inlineEditForm = ref<FormInstance>();

const inlineEditConfig = ref<InlineTableEditConifg>({
  // 编辑行数据
  rowData: undefined,
  // 编辑行数据备份
  rowCopy: undefined,
  // 编辑行数据是否发生变化
  isDirty: false,
  // 是否自动编辑下一行数据
  autoEditNext: false,
  // 下一行数据
  nextRow: undefined,
  // 是否新建
  isNew: false,
});

// 变量
let expandRows: T[] = [];
const tableKey = new Date().getTime();

const rowHeightStatus = ref('default');

// computed属性，树型表格参数
const treeConfig = computed<VxeTablePropTypes.TreeConfig>(() => {
  const config: VxeTablePropTypes.TreeConfig = attrs['tree-config'] as VxeTablePropTypes.TreeConfig;
  if (config && !config.toggleMethod) {
    config.toggleMethod = ({ expanded, row }) => {
      if (treeRowKey.value) {
        let id = row[treeRowKey.value];
        if (id) {
          if (expanded) {
            if (expandRows.indexOf(id) === -1) expandRows.push(id);
          } else {
            expandRows = expandRows.filter(item => item !== id);
          }
        }
      }
      return true;
    };
  }
  return config;
});

const treeRowKey = computed(() => {
  return treeConfig.value ? treeConfig.value.rowField : undefined;
});

const rowHeightClass = computed(() => {
  const classArr = ['row-height-' + rowHeightStatus.value];
  if (props.hasImageColumn) {
    classArr.push('row-height-image');
  }
  return classArr;
});

// 方法
const refresh = () => {
  emit('refresh');
};

// 高级查询
const onCustomQueryClick = () => {
  if (props.customQueryOptions == null) return;
  Dialog.show(
    '高级查询',
    CustomQuery,
    {
      area: ['1000px'],
    },
    {
      data: [],
      dictList: props.customQueryOptions.dictList,
      fieldList: props.customQueryOptions.fieldList,
      getDictDataList: props.customQueryOptions.getDictDataList,
      customQueryHistory: props.customQueryOptions.customQueryHistory,
    },
  )
    .then(res => {
      emit('custom-query', res);
    })
    .catch(() => {
      console.log('cancel');
    });
};

// 表格配置
const onCustomTableConfigClick = () => {
  if (!props.supportTableConfig || props.customConfig == null) return;
  Dialog.show(
    '表格配置',
    CustomConfig,
    {
      area: ['800px', '545px'],
    },
    {
      data: props.customConfig.tableColumnList || [],
    },
  )
    .then(res => {
      let temp = (res || []).reduce((acc, item) => {
        acc[item.columnId] = {
          show: item.show,
        };
        return acc;
      }, {});
      emit('table-config', temp);
    })
    .catch(() => {
      console.log('cancel');
    });
};

provide('inlineEditConfig', inlineEditConfig.value);
// 获取行内编辑信息
const getInlineEditConfig = () => {
  return inlineEditConfig.value;
};
// 判断传入的行是否正在编辑状态
const isRowEditing = row => {
  return inlineEditConfig.value.rowData === row;
};
// 判断表格是否正处于行内编辑状态
const isTableEditing = () => {
  return inlineEditConfig.value.rowData != null;
};
// 清除行内组件校验结果
const clearRowValidate = () => {
  if (inlineEditForm.value) {
    inlineEditForm.value.clearValidate();
  }
};
// 行内编辑组件校验
const validateRowData = callback => {
  if (inlineEditForm.value && callback) {
    return inlineEditForm.value.validate(callback);
  }
};
// 编辑行数据
const editRow = (row: T, isNew: boolean): InlineTableEditConifg => {
  if (row == null) return;
  if (inlineEditConfig.value.rowData != null && inlineEditConfig.value.isDirty) {
    ElMessage.warning('请先保存当前编辑的数据');
    return;
  }
  if (isNew) {
    // eslint-disable-next-line vue/no-mutating-props
    props.data.unshift(row);
  }
  inlineEditConfig.value.rowData = row;
  inlineEditConfig.value.rowCopy = { ...row };
  inlineEditConfig.value.isNew = isNew;
  nextTick(() => {
    inlineEditConfig.value.isDirty = false;
    clearRowValidate();
  });
};
// 保存行内编辑数据
const saveRow = autoEditNext => {
  return new Promise((resolve, reject) => {
    if (inlineEditForm.value == null) {
      reject();
    } else {
      inlineEditForm.value.validate((valid, data) => {
        if (!valid) {
          // 表单校验失败
          let firstFaildColumn = null;
          if (data) {
            Object.keys(data).forEach((key, index) => {
              let errorItem = data[key];
              if (Array.isArray(errorItem) && errorItem.length > 0) {
                if (index === 0) {
                  firstFaildColumn = key;
                }
                setTimeout(() => {
                  ElMessage.error(errorItem[0].message);
                });
              }
            });
          }
          reject(new Error(firstFaildColumn));
        } else {
          // 验证通过
          inlineEditConfig.value.autoEditNext = autoEditNext;
          if (inlineEditConfig.value.autoEditNext) {
            // 查找下一行数据
            let index = props.data.indexOf(inlineEditConfig.value.rowData);
            if (index !== -1 && index < props.data.length - 1) {
              inlineEditConfig.value.nextRow = props.data[index + 1];
            }
          }
          let res = {
            rowData: inlineEditConfig.value.rowCopy,
            nextRow: inlineEditConfig.value.nextRow,
            isNew: inlineEditConfig.value.isNew,
            isDirty: inlineEditConfig.value.isDirty,
          };
          inlineEditConfig.value.isDirty = false;
          resolve(res);
        }
      });
    }
  });
};
// 取消行内编辑
const cancelEditRow = (deleteRow = true) => {
  inlineEditConfig.value.rowData = undefined;
  inlineEditConfig.value.rowCopy = undefined;
  inlineEditConfig.value.autoEditNext = false;
  inlineEditConfig.value.isDirty = false;
  inlineEditConfig.value.nextRow = undefined;
  if (inlineEditConfig.value.isNew && deleteRow) {
    // eslint-disable-next-line vue/no-mutating-props
    props.data.shift();
  }
};

const onRowHeightToogle = () => {
  if (rowHeightStatus.value == 'default') {
    rowHeightStatus.value = 'mini';
  } else {
    rowHeightStatus.value = 'default';
  }
};

const onSelectChange = (rows: T[], multi: boolean) => {
  emit('update:value', rows);
  emit('change', rows, multi);
};

const onCheckBoxChange: VxeTableEvents.CheckboxChange<T> = ({ checked }) => {
  let selectRows: T[] = [];
  if (table.value) {
    selectRows = table.value.getCheckboxRecords(true);
    selectRows = selectRows.concat(table.value.getCheckboxIndeterminateRecords(true));
  }
  onSelectChange(selectRows, true);
  emit('checkbox-select-change', selectRows);
};

const onCheckAllChange: VxeTableEvents.CheckboxAll<T> = ({ checked }) => {
  let selectRows: T[] = [];
  if (table.value) {
    selectRows = table.value.getCheckboxRecords(true);
    selectRows = selectRows.concat(table.value.getCheckboxIndeterminateRecords(true));
  }
  onSelectChange(selectRows, true);
  emit('checkbox-select-change', selectRows);
};

const onRadioSelectChange: VxeTableEvents.RadioChange<T> = (data: T) => {
  let selectRow: T | null = null;
  if (table.value) {
    selectRow = table.value.getRadioRecord();
  }
  onSelectChange(selectRow != null ? [selectRow] : [], false);
  emit('radio-select-change', selectRow);
};

const setTableSelectRow = () => {
  nextTick(() => {
    if (table.value) {
      table.value.clearRadioRow();
      table.value.clearCheckboxRow();
      if (props.data == null) return;
      if (Array.isArray(props.value)) {
        // 多选
        table.value.setCheckboxRow(props.value, true);
      } else {
        // 单选
        table.value.setRadioRow(props.value);
      }
    }
  });
};

// 监听属性变化
watch(
  () => props.value,
  () => {
    setTableSelectRow();
  },
  { deep: true },
);
watch(
  () => props.data,
  (newVal, oldValue) => {
    if (newVal == oldValue) return;
    if (treeRowKey.value) {
      let nodeList: T[] = [];
      traverseTree(props.data, node => nodeList.push(node), 'children');
      let defaultExpandRows = nodeList.filter((row: T) => {
        if (!treeRowKey.value) {
          return false;
        }
        return expandRows.indexOf(row[treeRowKey.value]) != -1;
      });
      expandRows = defaultExpandRows.map(item => item[treeRowKey.value || 'id']);
      nextTick(() => {
        if (table.value) {
          table.value.setTreeExpand(defaultExpandRows, true);
        }
      });
    }
    let nextRow;
    if (inlineEditConfig.value.autoEditNext) {
      if (inlineEditConfig.value.isNew) {
        // 新建下一行数据
        let newRow = Object.keys(inlineEditConfig.value.rowCopy).reduce((acc, key) => {
          acc[key] = undefined;
          return acc;
        }, {});
        // eslint-disable-next-line vue/no-mutating-props
        props.data.unshift(newRow);
        nextRow = newRow;
      } else {
        if (inlineEditConfig.value.nextRow != null) {
          nextRow = (newVal || []).find(item => {
            if (props.rowKey == null) {
              return item === inlineEditConfig.value.nextRow;
            } else {
              return item[props.rowKey] === inlineEditConfig.value.nextRow[props.rowKey];
            }
          });
        }
        if (nextRow == null) {
          nextRow = (newVal || [])[0];
        }
      }
    }
    inlineEditConfig.value.rowData = nextRow;
    inlineEditConfig.value.rowCopy = nextRow ? { ...nextRow } : undefined;
    inlineEditConfig.value.nextRow = undefined;
    inlineEditConfig.value.autoEditNext = false;
  },
  { immediate: true, deep: true },
);

// 生命周期
onMounted(() => {
  console.log('treeConfig', treeConfig);
});

const getTableImpl = () => {
  return table.value;
};

defineExpose({
  getTableImpl,
  getInlineEditConfig,
  isRowEditing,
  isTableEditing,
  clearRowValidate,
  validateRowData,
  editRow,
  saveRow,
  cancelEditRow,
});
</script>

<style lang="scss" scoped>
.table-box {
  display: flex;
  background-color: white;
  flex-direction: column;
  flex: 1;
  .vxe-table-box {
    flex-shrink: 1;
    height: 200px;
    flex-grow: 1;
  }
  .operator-box {
    margin-bottom: 16px;
    flex-grow: 0;
  }
  .extend-box {
    display: flex;
    justify-content: flex-end;
  }
  .pagination-box {
    flex-grow: 0;
  }
}
.table-box :deep(.vxe-body--column) {
  padding: 0 !important;
}
.vxe-table.page-table {
  padding: 0 !important;
}
.row-height-mini.row-height-image :deep(.vxe-body--column) {
  height: 70px !important;
}
.row-height-default.row-height-image :deep(.vxe-body--column) {
  height: 80px !important;
}
.row-height-mini :deep(.vxe-body--column) {
  height: 35px !important;
}
.row-height-default :deep(.vxe-body--column) {
  height: 50px !important;
}
.inline-row-form ::v-deep .el-form-item {
  margin-bottom: 0px;
}
</style>
