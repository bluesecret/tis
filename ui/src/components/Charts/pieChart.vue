<template>
  <div @click.stop="onChartClick" ref="container" />
</template>

<script setup lang="ts">
import * as echarts from 'echarts/core';
import { PieChart } from 'echarts/charts';
import { ANY_OBJECT } from '@/types/generic';
import { getRowDataByColumnName, fixedValue } from '@/components/Charts/utils';
import { pieChartData as defaultData } from './defaultData';
import {
  getDefaultGrid,
  getDefaultLabel,
  getDefaultTooltip,
  getDefaultTitle,
  getDefaultLegend,
  getDefaultSeriePie,
  buildChartOptions,
} from './chartOption';
const defaultChartOptions = {
  // 坐标系表格设置
  grid: {
    ...getDefaultGrid(),
  },
  // 标题设置
  title: {
    ...getDefaultTitle(),
  },
  // 图例设置
  legend: {
    ...getDefaultLegend(),
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

const defaultPieOptions = {
  ...getDefaultSeriePie(),
};

// 注册组件
echarts.use(PieChart);

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

const categrayColumnData = computed(() => {
  let temp = [];
  if (Array.isArray(props.categrayColumnList) && Array.isArray(props.data)) {
    temp = props.data.map(item => {
      let categrayLabelName = null;
      if (props.callback && typeof props.callback.buildCategrayColumnLabel === 'function')
        categrayLabelName = props.callback.buildCategrayColumnLabel(item, props.categrayColumnList);
      if (categrayLabelName == null) {
        let retObj = '';
        for (const columnInfo of props.categrayColumnList) {
          if (columnInfo && columnInfo.columnName) {
            if (retObj != null) {
              retObj += '\r\n';
            } else {
              retObj = '';
            }
            retObj += getRowDataByColumnName(item, columnInfo.columnName);
          }
        }
        categrayLabelName = retObj;
      }
      return categrayLabelName;
    });
  }

  return temp;
});
const series = computed(() => {
  let arr = [];
  let center = ['50%', '50%'];
  let radius = ['0%', '70%'];
  if (Array.isArray(props.valueColumnList) && Array.isArray(props.data)) {
    arr = props.valueColumnList.map(valueItem => {
      let serieData = categrayColumnData.value.map((name, index) => {
        return {
          value: getRowDataByColumnName(props.data[index], valueItem.columnName, valueItem.fixed),
          name: name,
        };
      });
      if (props.options?.series.centerX != null && props.options?.series.centerX !== '')
        center[0] = props.options?.series.centerX + '%';
      if (props.options?.series.centerY != null && props.options?.series.centerY !== '')
        center[1] = props.options?.series.centerY + '%';
      if (props.options?.series.radiusInner != null && props.options?.series.radiusInner !== '')
        radius[0] = props.options?.series.radiusInner;
      if (props.options?.series.radiusOuter != null && props.options?.series.radiusOuter !== '')
        radius[1] = props.options?.series.radiusOuter;
      return {
        ...defaultPieOptions,
        ...props.options?.series,
        roseType: props.options?.series.isRose ? 'area' : undefined,
        center: center,
        radius: radius,
        label: props.options?.basic.label,
        data: serieData,
      };
    });
  }

  return arr.length > 0
    ? arr
    : [
        {
          ...defaultPieOptions,
          ...props.options?.series,
          roseType: props.options?.series.isRose ? 'area' : undefined,
          center: center,
          radius: radius,
          label: props.options?.basic.label,
          data: defaultData,
        },
      ];
});
const pieOptions = computed(() => {
  let options = {
    ...defaultChartOptions,
    ...props.options?.basic,
    series: series.value,
    xAxis: undefined,
    yAxis: undefined,
    label: undefined,
  };
  return options;
});

const emit = defineEmits<{
  click: [];
  'update:data': [ANY_OBJECT | null];
  change: [ANY_OBJECT | null];
  dblclick: [ANY_OBJECT];
}>();

const onChartClick = () => {
  emit('click');
};
const refresh = () => {
  if (echart != null) {
    if (props.data.length === 0) {
      echart.clear();
    }
    const options = buildChartOptions(pieOptions.value);
    echart.setOption(options, true);
    echart.resize();
  }
};
const resize = () => {
  if (echart != null) {
    echart.resize();
  }
};
const onClick = (params: ANY_OBJECT) => {
  let clickData: ANY_OBJECT | null = props.data[params.dataIndex];
  if (props.value === clickData) clickData = null;
  emit('update:data', clickData);
  emit('change', clickData);
  selectDataIndex = params.dataIndex;
  selectSeriesIndex = params.seriesIndex;
};
const onDblClick = (params: ANY_OBJECT) => {
  let clickData = props.data[params.dataIndex];
  emit('dblclick', clickData);
};

defineExpose({ resize });

watch(
  () => pieOptions.value,
  () => {
    refresh();
  },
  {
    immediate: true,
  },
);

onMounted(() => {
  echart = echarts.init(container.value);
  echart.on('click', 'series', onClick);
  echart.on('dblclick', 'series', onDblClick);
  refresh();
});

onBeforeMount(() => {
  if (echart != null) {
    echart.dispose();
    echart = null;
  }
});
</script>
