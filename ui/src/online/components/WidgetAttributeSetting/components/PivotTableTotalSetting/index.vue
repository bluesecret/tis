<template>
  <el-form
    label-position="top"
    size="default"
    v-if="totalSetting"
    style="padding: 10px 16px 0"
    @submit.prevent
  >
    <el-form-item class="view-attribute-item" label="是否显示">
      <el-switch v-model="totalSetting.show" />
    </el-form-item>
    <el-form-item class="view-attribute-item" label="别名">
      <el-input v-model="totalSetting.showName" :disabled="!totalSetting.show" />
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';

const props = withDefaults(defineProps<{ fieldName?: string }>(), { fieldName: 'rowTotalSetting' });
const formConfig = inject('formConfig', () => {
  console.error('PivotTableTotalSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});

const widget = computed(() => {
  return formConfig().currentWidget;
});
const totalSetting = computed(() => {
  let pps = (widget.value || {}).props;
  if (pps) {
    return pps[props.fieldName];
  } else {
    return {};
  }
});
</script>
