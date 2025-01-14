<template>
  <div>
    <progressSetting v-model:value="seriesSetting"></progressSetting>
    <progressFont
      v-if="seriesSetting.showText"
      v-model:value="seriesSetting.textStyle"
      textName="进度条"
    ></progressFont>
  </div>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import progressSetting from './progress.vue';
import progressFont from './font.vue';

const props = defineProps<{ value: ANY_OBJECT }>();
const formConfig = inject('formConfig', () => {
  console.error('dataCardSetting: formConfig not injected');
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
