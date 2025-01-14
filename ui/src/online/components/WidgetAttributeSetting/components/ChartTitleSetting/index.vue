<template>
  <el-form
    label-position="top"
    size="default"
    v-if="titleSetting"
    style="padding: 10px 16px 0"
    @submit.prevent
  >
    <el-form-item label="是否显示">
      <el-switch v-model="titleSetting.show" />
    </el-form-item>
    <el-form-item v-show="titleSetting.show" label="标题">
      <el-input v-model="titleSetting.text" clearable />
    </el-form-item>
    <el-form-item
      v-show="titleSetting.show && titleSetting.textStyle.fontSize != null"
      label="字体大小"
    >
      <el-slider v-model="titleSetting.textStyle.fontSize" :min="10" :max="40" size="default" />
    </el-form-item>
    <el-form-item
      v-show="titleSetting.show && titleSetting.textStyle.color != null"
      label="字体颜色"
    >
      <el-color-picker v-model="titleSetting.textStyle.color" />
    </el-form-item>
    <el-form-item v-show="titleSetting.left && titleSetting.left != null" label="水平位置">
      <el-select v-model="titleSetting.left" placeholder="">
        <el-option label="左" value="left" />
        <el-option label="中" value="center" />
        <el-option label="右" value="right" />
      </el-select>
    </el-form-item>
    <el-form-item v-show="titleSetting.show && titleSetting.top != null" label="垂直位置">
      <el-select v-model="titleSetting.top" placeholder="">
        <el-option label="上" value="top" />
        <el-option label="中" value="middle" />
        <el-option label="下" value="bottom" />
      </el-select>
    </el-form-item>
    <el-form-item v-show="titleSetting.show" label="字体样式">
      <el-checkbox v-model="titleSetting.bold">粗体</el-checkbox>
      <el-checkbox v-model="titleSetting.italics">斜体</el-checkbox>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';

const formConfig = inject('formConfig', () => {
  console.error('ChartTitleSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});

const widget = computed(() => {
  return formConfig().currentWidget;
});
const titleSetting = computed(() => {
  return widget.value ? (widget.value.props || {}).titleSetting : undefined;
});
</script>
