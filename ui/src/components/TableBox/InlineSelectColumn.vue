<template>
  <vxe-column v-bind="$attrs">
    <template v-slot="scope">
      <el-form-item v-if="isEdit(scope.row)" label="" :prop="$attrs.field">
        <el-select
          ref="inputWidget"
          :size="size"
          :clearable="clearable"
          :multiple="multiple"
          :collapse-tags="true"
          :filterable="true"
          :model-value="getRowData"
          @update:modelValue="onChange"
        >
          <el-option
            v-for="item in dropdownDataList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <!-- 判断是否有default的slot -->
      <slot v-else-if="$slots.default" :row="scope.row" />
      <!-- 默认显示内容 -->
      <span v-else>{{ getRowValue(scope.row) }}</span>
    </template>
  </vxe-column>
</template>

<script setup lang="ts">
import { VxeColumn } from 'vxe-table';
import { DictData } from '@/common/staticDict/types';
import { ANY_OBJECT } from '@/types/generic';
import { useDropdown } from '@/common/hooks/useDropdown';
import { DropdownOptions, ListData } from '@/common/types/list';
import useTableInlineColumn from './useTableInlineColumn';

const emit = defineEmits(['change']);
type InlineSelectColumnProps = {
  size: string;
  multiple: boolean;
  clearable: boolean;
  loadDropdownData?: <T>() => Promise<T>;
};
const props = withDefaults(defineProps<InlineSelectColumnProps>(), {
  size: 'default',
  multiple: false,
  clearable: true,
  loadDropdownData: undefined,
});

const loadDropdownDataList = (): Promise<ListData<DictData>> => {
  if (props.loadDropdownData != null) {
    return new Promise((resolve, reject) => {
      props
        .loadDropdownData<ListData<DictData>>((editConfig.value || {}).rowCopy)
        .then(data => {
          resolve({
            dataList: data,
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
  isTree: false,
};
const dropdownWidget = useDropdown(dropdownOptions);
const { dropdownList: dropdownDataList } = dropdownWidget;

const getFieldValue = (value: ANY_OBJECT) => {
  return props.multiple ? (value as string).join(',') : value;
};

const getFieldDictData = (value: ANY_OBJECT) => {
  if (props.multiple) {
    return dropdownDataList.value.filter(item => value.includes(item.id));
  } else {
    return dropdownDataList.value.find(item => item.id === value);
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
    if (temp !== '') {
      temp = props.multiple ? temp.split(',') : temp;
    } else {
      temp = props.multiple ? [] : '';
    }
  }
  return temp;
});

const reset = () => {
  dropdownWidget.setDirty(true);
  dropdownWidget.refresh();
};

const inputWidget = ref();
const { editConfig, isEdit, getRowValue, onChange } = useTableInlineColumn(
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
    if (inputWidget.value) inputWidget.value.focus();
  },
});
</script>

<style></style>
