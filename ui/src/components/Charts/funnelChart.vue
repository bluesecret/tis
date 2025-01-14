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
import { FunnelChart } from 'echarts/charts';
import { CanvasRenderer } from 'echarts/renderers';
import { ANY_OBJECT } from '@/types/generic';
import {
  fixedValue,
  getCategoryColumnValue,
  getRowDataByColumnName,
} from '@/components/Charts/utils';
import { funnelChartData as defaultData } from './defaultData';
import {
  getDefaultLabel,
  getDefaultTooltip,
  getDefaultTitle,
  getDefaultLegend,
  buildChartOptions,
  getDefaultSerieFunnel,
} from './chartOption';

const defaultChartOptions = {
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

const defaultfunnelOptions = {
  ...getDefaultSerieFunnel(),
};

// 注册组件
echarts.use([
  GridComponent,
  TitleComponent,
  LegendComponent,
  TooltipComponent,
  FunnelChart,
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

const container = ref();
let echart: ANY_OBJECT | null = null;

const label = computed(() => {
  return props.options?.basic.label;
});
const legendData = computed(() => {
  if (Array.isArray(props.data)) {
    return props.data.map(dataItem => {
      return getCategoryColumnValue(dataItem, props.categrayColumnList);
    });
  } else {
    let tmp: string[] = [];
    props.valueColumnList.forEach(x => {
      tmp.push(x.name);
    });

    return tmp;
  }
});
const series = computed(() => {
  let series = {
    ...props.options?.series,
    left: props.options?.series.left + '%',
    width: props.options?.series.width + '%',
    minSize: props.options?.series.minSize + '%',
    maxSize: props.options?.series.maxSize + '%',
  };

  if (Array.isArray(props.valueColumnList) && Array.isArray(props.data)) {
    let arr = [];
    arr = props.valueColumnList.map((valueItem, index) => {
      let lineData = props.data.map(dataItem => {
        let xx = getRowDataByColumnName(dataItem, valueItem.columnName);
        return {
          value: fixedValue(xx, valueItem.fixed),
          name: getCategoryColumnValue(dataItem, props.categrayColumnList),
        };
      });

      return {
        name: legendData.value[index],
        ...defaultfunnelOptions,
        ...series,
        label: label.value,
        data: lineData,
      };
    });
    return arr.length > 0
      ? arr
      : [
          {
            name: 'test',
            ...defaultfunnelOptions,
            ...series,
            label: label.value,
            data: defaultData,
          },
        ];
  } else {
    let seriesList = [];

    let tmp: ANY_OBJECT[] = [];
    props.valueColumnList.forEach((x: ANY_OBJECT) => {
      let data = props.data as ANY_OBJECT;
      let value = data[x.columnName + '__DictMap']
        ? data[x.columnName + '__DictMap']
        : data[x.columnName];
      tmp.push({
        name: x.name,
        value: fixedValue(value, x.fixed),
      });
    });

    seriesList.push({
      name: legendData.value,
      ...defaultfunnelOptions,
      ...series,
      label: label.value,
      data: tmp,
    });

    return seriesList.length > 0
      ? seriesList
      : [
          {
            name: 'test',
            ...defaultfunnelOptions,
            ...series,
            label: label.value,
            data: defaultData,
          },
        ];
  }
});
const funnelOptions = computed(() => {
  let options = {
    ...defaultChartOptions,
    ...props.options?.basic,
    series: series.value,
  };
  options.legend.data = legendData.value;
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
    const options = buildChartOptions(funnelOptions.value);
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
  //emit('change', clickData);
};
const onDblClick = (params: ANY_OBJECT) => {
  let data = props.data as ANY_OBJECT[];
  let clickData = data[params.dataIndex];
  emit('dblclick', clickData);
};

defineExpose({ resize });

watch(
  () => funnelOptions.value,
  () => {
    refresh();
  },
  {
    deep: true,
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
