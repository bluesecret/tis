<template>
  <vxe-column v-bind="$attrs">
    <template v-slot="scope">
      <el-form-item v-if="isEdit(scope.row)" label="" :prop="$attrs.field">
        <el-switch
          ref="inputWidget"
          :model-value="switchValue"
          :size="size"
          @update:modelValue="onSwitchChange"
        />
      </el-form-item>
      <!-- 判断是否有default的slot -->
      <slot v-else-if="$slots.default" :row="scope.row" />
      <!-- 默认显示内容 -->
      <span v-else>{{ getRowValue(scope.row) ? '是' : '否' }}</span>
    </template>
  </vxe-column>
</template>

<script setup lang="ts">
import { VxeColumn } from 'vxe-table';
import useTableInlineColumn from './useTableInlineColumn';

const emit = defineEmits(['change']);
type InlineSwitchColumnProps = {
  size: string;
};
const props = withDefaults(defineProps<InlineSwitchColumnProps>(), {
  size: 'default',
});
const inputWidget = ref();
const { isEdit, getRowData, getRowValue, onChange, reset } = useTableInlineColumn(
  false,
  emit,
  inputWidget,
);

const switchValue = computed(() => {
  let tempValue = getRowValue();
  if (tempValue == null) return false;
  if (typeof tempValue === 'string') {
    tempValue = tempValue === 'true';
  } else if (typeof tempValue === 'number') {
    tempValue = tempValue === 1;
  }
  return tempValue;
});

const onSwitchChange = (value: boolean) => {
  let tempValue = getRowValue();
  if (tempValue == null) tempValue = value;
  else if (typeof tempValue === 'string') tempValue = value ? 'true' : 'false';
  else if (typeof tempValue === 'number') tempValue = value ? 1 : 0;
  onChange(tempValue);
};

defineExpose({
  reset,
  focus: () => {
    if (inputWidget.value) inputWidget.value.focus();
  },
});
</script>

<style></style>
