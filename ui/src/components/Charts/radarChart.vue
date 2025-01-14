<template>
  <div @click.stop="onChartClick" ref="container" />
</template>

<script setup lang="ts">
import * as echarts from 'echarts/core';
import {
  GridComponent,
  TitleComponent,
  LegendComponent,
  TooltipComponent,
} from 'echarts/components';
import { RadarChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
import { ANY_OBJECT } from '@/types/generic';
import { getCategoryColumnValue, getRowDataByValueColumns } from '@/components/Charts/utils';
import { radarChartData as defaultData } from './defaultData';
import {
  getDefaultLabel,
  getDefaultTooltip,
  getDefaultTitle,
  buildChartOptions,
  getDefaultSerieRadar,
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

const defaultRadarOptions = {
  ...getDefaultSerieRadar(),
};

// 注册组件
echarts.use([
  GridComponent,
  TitleComponent,
  LegendComponent,
  TooltipComponent,
  RadarChart,
  CanvasRenderer,
]);

const props = withDefaults(
  defineProps<{
    data?: ANY_OBJECT[] | ANY_OBJECT;
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
    const options = buildChartOptions(radarOptions.value);
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
  let data = props.data as ANY_OBJECT[];
  let clickData: ANY_OBJECT | null = data[params.dataIndex];
  if (props.value === clickData) clickData = null;
  emit('update:data', clickData);
  emit('change', clickData);
  selectDataIndex = params.dataIndex;
  selectSeriesIndex = params.seriesIndex;
};
const onDblClick = (params: ANY_OBJECT) => {
  let data = props.data as ANY_OBJECT[];
  let clickData = data[params.dataIndex];
  emit('dblclick', clickData);
};

defineExpose({ resize });

const label = computed(() => {
  return { label: props.options?.basic.label };
});
const series = computed(() => {
  let series = {
    ...props.options?.series,
  };
  if (Array.isArray(props.valueColumnList) && Array.isArray(props.data)) {
    let lineData = props.data.map((dataItem, index) => {
      let arr = props.data as ANY_OBJECT[];
      let data = {
        name: getCategoryColumnValue(dataItem, arr),
        value: getRowDataByValueColumns(dataItem, props.valueColumnList),
        areaStyle: { opacity: (props.options?.series.areaStyleOpacity || 0) / 10 },
      };

      return data;
    });

    return [
      {
        ...defaultRadarOptions,
        ...series,
        //...this.labelSetting,
        data: lineData.length > 0 ? lineData : defaultData,
      },
    ];
  } else {
    let lineData = [];
    let value: ANY_OBJECT[] = [];

    props.valueColumnList.forEach((x: ANY_OBJECT) => {
      let data = props.data as ANY_OBJECT;
      value.push(
        data[x.columnName + '__DictMap'] ? data[x.columnName + '__DictMap'] : data[x.columnName],
      );
    });

    let data = props.data as ANY_OBJECT[];
    lineData.push({
      name: getCategoryColumnValue(props.data, data),
      value: value,
      areaStyle: { opacity: (props.options?.series.areaStyleOpacity || 0) / 10 },
    });

    return [
      {
        ...defaultRadarOptions,
        ...series,
        ...this.labelSetting,
        data: lineData.length > 0 ? lineData : defaultData,
      },
    ];
  }
});
const radar = computed(() => {
  let radar = props.options?.radar;
  let indicator = props.options?.datasetInfo.valueColumnList.map((x: ANY_OBJECT) => {
    return {
      text: x.showName,
      max: x.max,
      min: x.min,
    };
  });

  for (let i = 0; i < 3 - props.valueColumnList.length; i++) {
    indicator.push({});
  }
  radar.indicator = indicator;
  return radar;
});
const color = computed(() => {
  return (
    props.options?.basic.color || [
      '#5470C6',
      '#91CC75',
      '#FAC858',
      '#EE6666',
      '#73C0DE',
      '#3BA272',
      '#FC8452',
      '#9A60B4',
      '#EA7CCC',
    ]
  );
});
const radarOptions = computed(() => {
  let options = {
    ...defaultChartOptions,
    ...props.options?.basic,
    color: color.value,
    radar: radar.value,
    label: label.value,
    series: series.value,
  };

  return options;
});

watch(
  () => radarOptions.value,
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
