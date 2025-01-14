<template>
  <el-form-item class="view-attribute-item" label="列表符号">
    <el-select v-model="seriesSetting.listIcon">
      <el-option label="无" value="" />
      <el-option label="数字" value="digit" />
      <el-option label="圆圈" value="circle" />
    </el-select>
  </el-form-item>
  <el-form-item v-show="seriesSetting.listIcon" label="列表符号颜色">
    <el-color-picker v-model="seriesSetting.listIconColor" />
  </el-form-item>
  <progressFont
    v-if="seriesSetting.titleStyle"
    v-model:value="seriesSetting.titleStyle"
    textName="内容标题"
  ></progressFont>
  <el-divider></el-divider>
  <progressFont
    v-if="seriesSetting.textStyle"
    v-model:value="seriesSetting.textStyle"
    textName="内容"
  ></progressFont>
  <el-divider></el-divider>
  <progressFont
    v-if="seriesSetting.timeStyle"
    v-model:value="seriesSetting.timeStyle"
    textName="日期"
  ></progressFont>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import progressFont from './font.vue';

const formConfig = inject('formConfig', () => {
  console.error('commonListSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});
const seriesSetting = ref<ANY_OBJECT>({});
const widget = computed(() => {
  return formConfig().currentWidget;
});

watch(
  () => widget.value,
  () => {
    seriesSetting.value = widget.value?.props.seriesSetting;
  },
  {
    immediate: true,
  },
);
</script>
