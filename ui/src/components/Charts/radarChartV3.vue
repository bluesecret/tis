<template>
  <BaseChart ref="chart" :options="getFinalOptions" />
</template>

<script setup lang="ts">
import * as echarts from 'echarts/core';
import { RadarChart } from 'echarts/charts';
import { deepMerge } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import BaseChart from './base.vue';
import { titleOption, tooltipOption, legendOption, radarOption, radarSeries } from './options';

// 注册组件
echarts.use(RadarChart);

const props = withDefaults(
  defineProps<{
    // 标题
    title?: string;
    // 中心点
    center?: string[];
    // 半径
    radius?: string;
    // 形状
    shape?: string;
    // 分隔段数
    splitNumber?: number;
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
    // 图表配置项（会覆盖上面的选项）
    options?: ANY_OBJECT;
  }>(),
  {
    title: '',
    center: () => ['50%', '60%'],
    radius: '60%',
    shape: 'polygon',
    splitNumber: 5,
    data: () => [],
    indexList: () => [],
    legendFormatter: '{name}',
    tooltip: false,
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
const indexShowName = computed(() => {
  return (props.indexList || []).map(item => item.showName || item.fieldName);
});
const getLegend = computed(() => {
  return {
    ...legendOption,
    formatter: legendFormatterImpl.value,
    data: getSeriesData.value
      .map((item: ANY_OBJECT | undefined) => item?.name)
      .filter(item => !!item),
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
const getRadarOption = computed(() => {
  const indicator = (indexShowName.value || []).map(item => ({ name: item }));
  return {
    ...radarOption,
    center: props.center,
    radius: props.radius,
    shape: props.shape,
    splitNumber: props.splitNumber,
    indicator: indicator,
  };
});
const getSeriesData = computed(() => {
  if (!props.dimension) {
    // 没有设置维度，取第一条数据
    const data = (props.data || [])[0];
    if (data == null) return [];
    return [
      {
        value: (indexFieldName.value || []).map(item => {
          return item ? (data || {})[item] : undefined;
        }),
      },
    ];
  } else {
    const dimension = props.dimension;
    // 设置了维度，使用维度分组取第一个指标
    return (props.data || [])
      .map(data => {
        if (data == null) return undefined;
        const name = data[dimension];
        return {
          value: (indexFieldName.value || []).map(item => {
            return item ? (data || {})[item] : undefined;
          }),
          name,
        };
      })
      .filter(item => item != null);
  }
});
const getSeries = computed(() => {
  return [
    {
      ...radarSeries,
      data: getSeriesData.value,
    },
  ];
});
const getOptions = computed(() => {
  return {
    title: getTitleOption.value,
    legend: getLegend.value,
    tooltip: getTooltipOption.value,
    radar: getRadarOption.value,
    series: getSeries.value,
  };
});
const getFinalOptions = computed(() => {
  console.log('radar chart getFinalOptions', getOptions.value, props.options);
  return deepMerge(getOptions.value, props.options);
});
</script>
