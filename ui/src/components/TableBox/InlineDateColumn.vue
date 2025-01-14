<template>
  <vxe-column v-bind="$attrs">
    <template v-slot="scope">
      <el-form-item v-if="isEdit(scope.row)" label="" :prop="$attrs.field">
        <el-date-picker
          ref="inputWidget"
          style="width: 100%"
          :type="type"
          start-placeholder=""
          end-placeholder=""
          :clearable="true"
          :size="size"
          :format="format"
          :value-format="valueFormat"
          :model-value="getRowData"
          @update:modelValue="onChange"
        />
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
import useTableInlineColumn from './useTableInlineColumn';

const emit = defineEmits(['change']);
type InlineDateColumnProps = {
  size: string;
  type: string;
  format: string;
  valueFormat: string;
  clearable: boolean;
};
const props = withDefaults(defineProps<InlineDateColumnProps>(), {
  size: 'default',
  type: 'date',
  format: 'YYYY-MM-DD HH:mm:ss',
  valueFormat: 'YYYY-MM-DD HH:mm:ss',
  clearable: true,
});
const inputWidget = ref();
const { isEdit, getRowData, getRowValue, onChange, reset } = useTableInlineColumn(
  false,
  emit,
  inputWidget,
);

defineExpose({
  reset,
  focus: () => {
    if (inputWidget.value) inputWidget.value.focus();
  },
});
</script>

<style></style>
