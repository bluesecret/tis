<template>
  <vxe-column v-bind="$attrs">
    <template v-slot="scope">
      <el-form-item v-if="isEdit(scope.row)" label="" :prop="$attrs.field">
        <el-cascader
          ref="inputWidget"
          :size="size"
          :clearable="clearable"
          :collapse-tags="true"
          :filterable="true"
          :options="dropdownDataList"
          :props="{
            value: 'id',
            label: 'name',
            children: 'children',
            multiple: multiple,
            checkStrictly: checkStrictly,
          }"
          :model-value="getRowData"
          @update:modelValue="onChange"
        />
      </el-form-item>
      <slot v-else :row="scope.row" />
    </template>
  </vxe-column>
</template>

<script setup lang="ts">
import { VxeColumn } from 'vxe-table';
import { findTreeNodeObjectPath, findTreeNodePath } from '@/common/utils';
import { DictData } from '@/common/staticDict/types';
import { ANY_OBJECT } from '@/types/generic';
import { useDropdown } from '@/common/hooks/useDropdown';
import { DropdownOptions, ListData } from '@/common/types/list';
import useTableInlineColumn from './useTableInlineColumn';

const emit = defineEmits(['change']);
type InlineCascaderColumnProps = {
  size: string;
  multiple: boolean;
  clearable: boolean;
  checkStrictly: boolean;
  loadDropdownData?: <T>() => Promise<T>;
};
const props = withDefaults(defineProps<InlineCascaderColumnProps>(), {
  size: 'default',
  multiple: false,
  clearable: true,
  checkStrictly: false,
  loadDropdownData: undefined,
});

const loadDropdownDataList = (): Promise<ListData<DictData>> => {
  if (props.loadDropdownData != null) {
    return new Promise((resolve, reject) => {
      props
        .loadDropdownData<ListData<DictData>>((editConfig.value || {}).rowCopy)
        .then(data => {
          resolve({
            dataList: data.map(item => {
              return {
                ...item,
                children: undefined,
              };
            }),
          });
        })
        .catch(() => {
          reject();
        });
    });
  } else {
    return Promise.resolve([]);
  }
};

const dropdownOptions: DropdownOptions<DictData> = {
  loadData: loadDropdownDataList,
  isTree: true,
};
const dropdownWidget = useDropdown(dropdownOptions);
const { dropdownList: dropdownDataList } = dropdownWidget;

const getFieldValue = (value?: Array<ANY_OBJECT>) => {
  if (!Array.isArray(value) || value.length === 0) {
    return undefined;
  }
  if (props.multiple) {
    let temp = value
      .map(item => {
        if (Array.isArray(item) && item.length > 0) {
          return item[item.length - 1];
        }
        return null;
      })
      .filter(item => item != null);
    return temp.length > 0 ? temp.join(',') : undefined;
  } else {
    return value[value.length - 1];
  }
};

const getFieldDictData = (value?: Array<ANY_OBJECT>) => {
  if (!Array.isArray(value) || value.length === 0) return undefined;
  if (props.multiple) {
    return value
      .map(item => {
        if (Array.isArray(item) && item.length > 0) {
          let path = findTreeNodeObjectPath(dropdownDataList.value, item[item.length - 1], 'id');
          return path[path.length - 1];
        } else {
          return null;
        }
      })
      .filter(item => item != null);
  } else {
    let path = findTreeNodeObjectPath(dropdownDataList.value, value[value.length - 1], 'id');
    return path[path.length - 1];
  }
};

const attrs = useAttrs();
const getRowData = computed(() => {
  let row = (editConfig.value || {}).rowCopy;
  if (row == null || attrs.field == null) {
    return undefined;
  }
  let path = attrs.field.split('.');
  let temp = row;
  for (let i = 0; i < path.length; i++) {
    temp = temp[path[i]];
    if (temp == null) break;
  }
  if (temp != null) {
    temp = temp + '';
    temp = props.multiple ? temp.split(',') : [temp];
    temp = temp
      .map(item => {
        return findTreeNodePath(dropdownDataList.value, item, 'id');
      })
      .filter(item => item.length > 0);
    if (!props.multiple) temp = temp[0];
  }
  return temp;
});

const reset = () => {
  dropdownWidget.setDirty(true);
  dropdownWidget.refresh();
};

const inputWidget = ref();
const { editConfig, isEdit, onChange } = useTableInlineColumn(
  false,
  emit,
  inputWidget,
  getFieldValue,
  getFieldDictData,
);

watch(
  () => editConfig.value.rowData,
  () => {
    if (editConfig.value && editConfig.value.rowData) {
      reset();
    }
  },
  {
    immediate: true,
  },
);

defineExpose({
  reset,
  focus: () => {
    // element-ui cascader does not have a focus method
    // if (inputWidget.value) inputWidget.value.focus();
  },
});
</script>

<style></style>
