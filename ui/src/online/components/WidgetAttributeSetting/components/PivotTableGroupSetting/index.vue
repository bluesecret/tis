<template>
  <el-form
    label-position="top"
    size="default"
    v-if="groupSetting"
    style="padding: 10px 16px 0"
    @submit.prevent
  >
    <el-form-item class="view-attribute-item" label="背景色">
      <el-color-picker v-model="groupSetting.backgroundColor" />
    </el-form-item>
    <el-form-item class="view-attribute-item" label="字体颜色">
      <el-color-picker v-model="groupSetting.fontColor" />
    </el-form-item>
    <el-form-item class="view-attribute-item" label="字体大小">
      <el-slider
        style="width: 95%; margin-left: 5px"
        v-model="groupSetting.fontSize"
        :min="10"
        :max="40"
        size="default"
      />
    </el-form-item>
    <el-form-item
      class="view-attribute-item"
      label="行高"
      v-if="groupSetting.headerRowHeight != null"
    >
      <el-slider
        style="width: 95%; margin-left: 5px"
        v-model="groupSetting.headerRowHeight"
        :min="10"
        :max="100"
        size="default"
      />
    </el-form-item>
    <el-form-item class="view-attribute-item" label="对齐方式">
      <el-radio-group v-model="groupSetting.align">
        <el-radio-button value="left">居左</el-radio-button>
        <el-radio-button value="center">居中</el-radio-button>
        <el-radio-button value="right">居右</el-radio-button>
      </el-radio-group>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';

const props = withDefaults(defineProps<{ fieldName?: string }>(), { fieldName: 'rowGroupSetting' });
const formConfig = inject('formConfig', () => {
  console.error('PivotTableGroupSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});

const widget = computed(() => {
  return formConfig().currentWidget;
});
const groupSetting = computed(() => {
  let pps = (widget.value || {}).props;
  if (pps) {
    return pps[props.fieldName];
  } else {
    return {};
  }
});
</script>
