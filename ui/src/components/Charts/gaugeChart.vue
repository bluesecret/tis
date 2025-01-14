<template>
  <div ref="container">
    {{ gaugeOptions }}
  </div>
</template>

<script setup lang="ts">
import * as echarts from 'echarts/core';
import { GaugeChart } from 'echarts/charts';
import { ANY_OBJECT } from '@/types/generic';
import { getRowDataByColumnName, getRowDataByValueColumns } from '@/components/Charts/utils';
import {
  getDefaultLabel,
  getDefaultTooltip,
  getDefaultTitle,
  getDefaultSerieGauge,
  buildChartOptions,
} from './chartOption';

const defaultChartOptions = {
  // 标题设置
  title: {
    ...getDefaultTitle(),
  },
  // 标签设置
  label: {
    ...getDefaultLabel(),
  },
  // 提示设置
  tooltip: {
    ...getDefaultTooltip(),
  },
};

const defaultGuageOptions = {
  ...getDefaultSerieGauge(),
};

// 注册组件
echarts.use(GaugeChart);

const props = withDefaults(
  defineProps<{
    data?: ANY_OBJECT[];
    value?: number | ANY_OBJECT[] | string | Date | ANY_OBJECT;
    options?: ANY_OBJECT;
    categrayColumnList?: ANY_OBJECT[];
    valueColumnList?: ANY_OBJECT[];
    // 回调函数
    callback?: ANY_OBJECT;
  }>(),
  {
    data: () => [],
    categrayColumnList: () => [],
    valueColumnList: () => [],
    callback: () => {
      return {};
    },
  },
);

const container = ref();
let echart: ANY_OBJECT | null = null;
let selectDataIndex = null;
let selectSeriesIndex = null;

const categrayColumnName = computed(() => {
  if (Array.isArray(props.categrayColumnList) && props.categrayColumnList.length > 0) {
    return props.categrayColumnList[0].columnName;
  }
  return '';
});
const series = computed(() => {
  if (Array.isArray(props.valueColumnList) && Array.isArray(props.data) && props.data.length > 0) {
    let data = {
      name: getRowDataByColumnName(props.data[0], categrayColumnName.value),
      value: getRowDataByValueColumns(props.data[0], props.valueColumnList),
    };

    let series = {
      ...props.options?.series,
    };

    return [
      {
        ...defaultGuageOptions,
        ...series,
        //...this.labelSetting,
        data: [data],
      },
    ];
  }
  return [];
});
const gaugeOptions = computed(() => {
  let options = {
    ...defaultChartOptions,
    ...props.options?.basic,
    color: props.options?.basic.chartColors,
    series: series.value,
  };
  Object.keys(options).forEach(x => {
    if (!options[x]) delete options[x];
  });
  return options;
});

const refresh = () => {
  if (echart != null) {
    if (props.data.length === 0) {
      echart.clear();
    }
    const options = buildChartOptions(gaugeOptions.value);
    echart.setOption(options, true);
    echart.resize();
  }
};
const resize = () => {
  if (echart != null) {
    echart.resize();
  }
};

defineExpose({ resize });

watch(
  () => gaugeOptions.value,
  () => {
    refresh();
  },
  {
    immediate: true,
  },
);

onMounted(() => {
  echart = echarts.init(container.value);
  refresh();
});

onBeforeMount(() => {
  if (echart != null) {
    echart.dispose();
    echart = null;
  }
});
</script>
