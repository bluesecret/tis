<template>
  <imageUploader v-model:value="seriesSetting" textName="卡片图片"></imageUploader>
  <font
    v-if="seriesSetting.mainTextStyle"
    v-model:value="seriesSetting.mainTextStyle"
    textName="卡片标题"
  ></font>
  <el-divider></el-divider>
  <font
    v-if="seriesSetting.numTextStyle"
    v-model:value="seriesSetting.numTextStyle"
    textName="卡片内容"
  ></font>
  <el-divider></el-divider>
  <progressSetting v-model:value="seriesSetting.progress"></progressSetting>
  <font v-model:value="seriesSetting.progress.textStyle" textName="进度条"></font>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import progressSetting from './progress.vue';
import font from './font.vue';
import imageUploader from './imageUploader.vue';

const props = defineProps<{ value: ANY_OBJECT }>();
const formConfig = inject('formConfig', () => {
  console.error('dataProgressCardSetting: formConfig not injected');
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
    // props.value与widget.value.props.seriesSetting是同一对象，通过上面响应变量的代理，值的变化直接产生在本体
    // 也可以通过update:value事件进行更新
    console.log('seriesSetting >>>', seriesSetting.value, props.value);
  },
  {
    immediate: true,
  },
);
</script>
