<template>
  <div ref="container" style="width: 100%; height: 100%" />
</template>

<script setup lang="ts">
import * as echarts from 'echarts/core';
// 引入提示框，标题，直角坐标系，数据集，内置数据转换器组件，组件后缀都为 Component
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  LegendComponent,
} from 'echarts/components';

// 标签自动布局、全局过渡动画等特性
import { LabelLayout, UniversalTransition } from 'echarts/features';
// 引入 Canvas 渲染器，注意引入 CanvasRenderer 或者 SVGRenderer 是必须的一步
import { CanvasRenderer } from 'echarts/renderers';
import { ANY_OBJECT } from '@/types/generic';

// 注册必须的组件
echarts.use([
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DatasetComponent,
  TransformComponent,
  LabelLayout,
  UniversalTransition,
  CanvasRenderer,
  LegendComponent,
]);

const props = withDefaults(defineProps<{ options?: ANY_OBJECT }>(), {
  options: () => {
    return {};
  },
});

const container = ref<HTMLElement>();
const chart = ref();

const init = () => {
  chart.value = echarts.init(container.value);
  console.log('chart options >>>', props.options);
  chart.value.setOption(props.options);
};
const resize = () => {
  chart.value.resize();
};

defineExpose({ resize });

onMounted(() => {
  init();
});

watch(
  () => props.options,
  newVal => {
    if (chart.value) {
      chart.value.setOption(newVal);
    }
  },
  {
    deep: true,
  },
);
</script>
