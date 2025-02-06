<template>
  <div @click.stop="onChartClick" ref="container" />
</template>

<script setup lang="ts">
import * as echarts from 'echarts/core';
import { ScatterChart } from 'echarts/charts';
import { ANY_OBJECT } from '@/types/generic';
import { getRowDataByColumnName } from '@/components/Charts/utils';
import { ScatterSymbolType } from '@/common/staticDict';
import { scatterChartData as defaultData } from './defaultData';
import {
  getDefaultGrid,
  getDefaultLabel,
  getDefaultTooltip,
  getDefaultXAxis,
  getDefaultYAxis,
  getDefaultTitle,
  getDefaultLegend,
  getDefaultSerieScatter,
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
  // x轴设置
  xAxis: {
    ...getDefaultXAxis(),
  },
  // y轴设置
  yAxis: {
    ...getDefaultYAxis(),
  },
};

const defaultScatterOptions = {
  ...getDefaultSerieScatter(),
};

// 注册组件
echarts.use(ScatterChart);

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
const legendData = computed(() => {
  if (Array.isArray(props.valueColumnList)) {
    return props.valueColumnList.map(item => item.name);
  }

  return [];
});
const series = computed(() => {
  let arr = [];
  if (Array.isArray(props.valueColumnList) && Array.isArray(props.data)) {
    arr = props.valueColumnList.map((valueItem, index) => {
      let serieData = categrayColumnData.value.map((xValue, i) => {
        let yValue = getRowDataByColumnName(props.data[i], valueItem.columnName, valueItem.fixed);
        let symbolSize = props.options?.series.itemSize;
        if (props.options?.series.symbolType === ScatterSymbolType.VALUE) {
          symbolSize = yValue;
        }

        return [xValue, yValue, symbolSize];
      });

      return {
        name: legendData.value[index],
        ...defaultScatterOptions,
        ...props.options?.series,
        symbolSize: props.options?.series.itemSize,
        /*
            symbolSize: function (data) {
              console.log(data);
              return data[2];
            },
            */
        data: serieData,
      };
    });
  }
  return arr.length > 0
    ? arr
    : [
        {
          name: 'test',
          ...defaultScatterOptions,
          ...props.options?.series,
          symbolSize: props.options?.series.itemSize,
          data: defaultData,
        },
      ];
});
const scatterOptions = computed(() => {
  let options = {
    ...defaultChartOptions,
    ...props.options?.basic,
    series: series.value,
  };

  options.legend.data = legendData.value;
  options.xAxis.data = categrayColumnData.value;
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
    const options = buildChartOptions(scatterOptions.value);
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
  () => scatterOptions.value,
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
