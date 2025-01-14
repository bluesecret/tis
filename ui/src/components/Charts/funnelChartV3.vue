<template>
  <BaseChart ref="chart" :options="getFinalOptions" />
</template>

<script setup lang="ts">
import * as echarts from 'echarts/core';
import { FunnelChart } from 'echarts/charts';
import { deepMerge } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import BaseChart from './base.vue';
import { titleOption, tooltipOption, legendOption, funnelSeries } from './options';

// 注册组件
echarts.use(FunnelChart);

const props = withDefaults(
  defineProps<{
    // 标题
    title?: string;
    // 数据
    data?: ANY_OBJECT[];
    // 指标字段名数组，例如：[{fieldName: 'a', showName: 'a'}]
    indexList?: ANY_OBJECT[];
    // 图例显示名格式化函数
    legendFormatter?: string | ((name: string) => string);
    // 维度设置
    dimension?: string;
    // 是否显示提示
    tooltip?: boolean;
    position?: ANY_OBJECT;
    // 图表配置项（会覆盖上面的选项）
    options?: ANY_OBJECT;
  }>(),
  {
    title: '',
    data: () => [],
    indexList: () => [],
    legendFormatter: '{name}',
    tooltip: false,
    position: () => {
      return {
        left: '10%',
        right: '10%',
        top: 60,
        bottom: 60,
        width: '80%',
        orient: 'vertical',
        sort: 'descending',
      };
    },
    options: () => {
      return {};
    },
  },
);

const chart = ref();
const resize = () => {
  if (chart.value) chart.value.resize();
};
defineExpose({ resize });

// 图例显示名称格式化
const legendFormatterImpl = computed(() => {
  if (typeof props.legendFormatter === 'string') {
    return props.legendFormatter;
  } else if (typeof props.legendFormatter === 'function') {
    return props.legendFormatter;
  } else {
    return '{name}';
  }
});
const indexFieldName = computed(() => {
  return (props.indexList || []).map(item => item.fieldName);
});

const getLegend = computed(() => {
  return {
    ...legendOption,
    formatter: legendFormatterImpl.value,
    data: getSeriesData.value.map(item => item.name),
  };
});
const getTitleOption = computed(() => {
  return {
    ...titleOption,
    show: props.title != null && props.title !== '',
    text: props.title,
  };
});
const getTooltipOption = computed(() => {
  return {
    ...tooltipOption,
    show: props.tooltip,
  };
});
const getSeriesData = computed(() => {
  if (!props.dimension) {
    // 没有设置维度，返回指标数组
    const data = (props.data || [])[0];
    if (data == null) return [];
    return (props.indexList || []).map(item => {
      return {
        name: item.showName || item.fieldName,
        value: data[item.fieldName],
      };
    });
  } else {
    // 设置维度，根据维度分组取第一个指标值
    if (!Array.isArray(indexFieldName.value) || indexFieldName.value.length === 0) return [];
    const dimension = props.dimension;
    return (props.data || []).map(data => {
      return {
        name: data[dimension],
        value: data[indexFieldName.value[0]],
      };
    });
  }
});
const getSeries = computed(() => {
  return (indexFieldName.value || []).map(item => {
    return {
      ...funnelSeries,
      ...props.position,
      name: item.showName || item.fieldName,
      data: getSeriesData.value,
    };
  });
});
const getOptions = computed(() => {
  return {
    title: getTitleOption.value,
    legend: getLegend.value,
    tooltip: getTooltipOption.value,
    series: getSeries.value,
  };
});
const getFinalOptions = computed(() => {
  console.log('funnel chart getFinalOptions', getOptions.value, props.options);
  return deepMerge(getOptions.value, props.options);
});
</script>
