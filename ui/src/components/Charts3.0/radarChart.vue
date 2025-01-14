<template>
  <BaseChart ref="chart" :options="getFinalOptions" />
</template>

<script>
import { deepMerge } from '@/common/utils';
import BaseChart from './base.vue';
import { titleOption, tooltipOption, legendOption, radarOption, radarSeries } from './options.js';

export default {
  name: 'RadarCharts',
  components: {
    BaseChart,
  },
  props: {
    // 标题
    title: {
      type: String,
      default: '',
    },
    // 中心点
    center: {
      type: Array,
      default: () => ['50%', '60%'],
    },
    // 半径
    radius: {
      type: String,
      default: '60%',
    },
    // 形状
    shape: {
      type: String,
      default: 'polygon',
    },
    // 分隔段数
    splitNumber: {
      type: Number,
      default: 5,
    },
    // 数据
    data: {
      type: Array,
      default: () => [],
    },
    // 指标字段名数组
    indexList: {
      type: Array,
      default: () => [],
    },
    // 图例显示名格式化函数
    legendFormatter: {
      type: [Function, String],
      default: '{name}',
    },
    // 维度设置
    dimension: {
      type: String,
      default: undefined,
    },
    // 是否显示提示
    tooltip: {
      type: Boolean,
      default: false,
    },
    // 图表配置项（会覆盖上面的选项）
    options: {
      type: Object,
      default: () => {
        return {};
      },
    },
  },
  methods: {
    resize() {
      if (this.$refs.chart) this.$refs.chart.resize();
    },
  },
  computed: {
    // 图例显示名称格式化
    legendFormatterImpl() {
      if (typeof this.legendFormatter === 'string') {
        return this.legendFormatter;
      } else if (typeof this.legendFormatter === 'function') {
        return this.legendFormatter;
      } else {
        return '{name}';
      }
    },
    indexFieldName() {
      return (this.indexList || []).map(item => item.fieldName);
    },
    indexShowName() {
      return (this.indexList || []).map(item => item.showName || item.fieldName);
    },
    getLegend() {
      return {
        ...legendOption,
        formatter: this.legendFormatterImpl,
        data: this.getSeriesData.map(item => item.name).filter(item => item != null),
      };
    },
    getTitleOption() {
      return {
        ...titleOption,
        show: this.title != null && this.title !== '',
        text: this.title,
      };
    },
    getTooltipOption() {
      return {
        ...tooltipOption,
        show: this.tooltip,
      };
    },
    getRadarOption() {
      const indicator = (this.indexShowName || []).map(item => ({ name: item }));
      return {
        ...radarOption,
        center: this.center,
        radius: this.radius,
        shape: this.shape,
        splitNumber: this.splitNumber,
        indicator: indicator,
      };
    },
    getSeriesData() {
      if (this.dimension == null || this.dimension === '') {
        // 没有设置维度，取第一条数据
        const data = (this.data || [])[0];
        if (data == null) return [];
        return [
          {
            value: (this.indexFieldName || []).map(item => {
              return item ? (data || {})[item] : undefined;
            }),
          },
        ];
      } else {
        // 设置了维度，使用维度分组取第一个指标
        return (this.data || [])
          .map(data => {
            if (data == null) return undefined;
            const name = data[this.dimension];
            return {
              value: (this.indexFieldName || []).map(item => {
                return item ? (data || {})[item] : undefined;
              }),
              name,
            };
          })
          .filter(item => item != null);
      }
    },
    getSeries() {
      return [
        {
          ...radarSeries,
          data: this.getSeriesData,
        },
      ];
    },
    getOptions() {
      return {
        title: this.getTitleOption,
        legend: this.getLegend,
        tooltip: this.getTooltipOption,
        radar: this.getRadarOption,
        series: this.getSeries,
      };
    },
    getFinalOptions() {
      console.log(this.getOptions, this.options, deepMerge(this.getOptions, this.options));
      return deepMerge(this.getOptions, this.options);
    },
  },
};
</script>

<style></style>
