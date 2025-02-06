<template>
  <div @click.stop="onChartClick" ref="container" />
</template>

<script setup lang="ts">
import * as echarts from 'echarts/core';
import {
  TitleComponent,
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  MarkLineComponent,
  MarkPointComponent,
} from 'echarts/components';
import { BarChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
import { ANY_OBJECT } from '@/types/generic';
import { getRowDataByColumnName } from '@/components/Charts/utils';
import { barChartData as defaultData } from './defaultData';
import {
  getDefaultGrid,
  getDefaultLabel,
  getDefaultTooltip,
  getDefaultXAxis,
  getDefaultYAxis,
  getDefaultTitle,
  getDefaultLegend,
  getDefaultSerieBar,
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

const defaultBarOptions = {
  ...getDefaultSerieBar(),
};

// 注册组件
echarts.use([
  TitleComponent,
  ToolboxComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  MarkLineComponent,
  MarkPointComponent,
  BarChart,
  CanvasRenderer,
]);

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

console.log('barChart props', props);

const container = ref();
let echart: echarts.ECharts | null = null;
let selectDataIndex = null;
let selectSeriesIndex = null;

const categrayColumnData = computed(() => {
  let temp: string[] = [];
  if (Array.isArray(props.categrayColumnList) && Array.isArray(props.data)) {
    temp = props.data.map((item: ANY_OBJECT) => {
      let categrayLabelName: string | null = null;
      if (props.callback && typeof props.callback.buildCategrayColumnLabel === 'function')
        categrayLabelName = props.callback.buildCategrayColumnLabel(item, props.categrayColumnList);
      console.log('barChart categrayLabelName 1', categrayLabelName);
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
        console.log('barChart categrayLabelName 2', categrayLabelName);
      }
      return categrayLabelName;
    });
  }

  console.log('barChart categrayColumnData', temp);
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
      let serieData = props.data.map(dataItem => {
        return getRowDataByColumnName(dataItem, valueItem.columnName, valueItem.fixed);
      });
      return {
        name: legendData.value[index],
        ...defaultBarOptions,
        ...props.options?.series,
        data: serieData,
        stack: props.options?.series.stack ? 'D' : undefined,
        lateral: undefined,
      };
    });
  }
  return arr.length > 0
    ? arr
    : [
        {
          name: 'test',
          ...defaultBarOptions,
          ...props.options?.series,
          data: defaultData,
          stack: props.options?.series.stack ? 'D' : undefined,
          lateral: undefined,
        },
      ];
});
const barOptions = computed(() => {
  let options = {
    ...defaultChartOptions,
    ...props.options?.basic,
    series: series.value,
  };

  options.legend.data = legendData.value;
  options.xAxis.data = categrayColumnData.value;

  if (props.options?.series.lateral) {
    let xAxis = options.xAxis;
    let yAxis = options.yAxis;
    options.xAxis = yAxis;
    options.yAxis = xAxis;
  }

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
  let options = buildChartOptions(barOptions.value);
  console.log('barChart options 1', options);
  if (echart != null) {
    if (props.data.length === 0) {
      echart.clear();
    }

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
  () => barOptions.value,
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
