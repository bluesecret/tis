<template>
  <BaseChart ref="chart" :options="getFinalOptions" />
</template>

<script>
import { deepMerge } from '@/common/utils';
import BaseChart from './base.vue';
import { titleOption, tooltipOption, legendOption, pieSeries } from './options.js';

export default {
  name: 'PieCharts',
  components: {
    BaseChart,
  },
  props: {
    // 标题
    title: {
      type: String,
      default: '',
    },
    // 是否展示成南丁格尔图，通过半径区分数据大小。可选择两种模式：radius，area
    roseType: {
      type: [Boolean, String],
      default: false,
    },
    // 饼图半径
    radius: {
      type: Array,
      default: () => ['0%', '70%'],
    },
    // 饼图中心点
    center: {
      type: Array,
      default: () => ['50%', '50%'],
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
    getLegend() {
      return {
        ...legendOption,
        formatter: this.legendFormatterImpl,
        data: this.getSeriesData.map(item => item.name),
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
    getSeriesData() {
      if (this.dimension == null || this.dimension === '') {
        // 没有设置维度，使用指标值列表
        const data = (this.data || [])[0];
        return (this.indexList || []).map(item => {
          return {
            value: item ? (data || {})[item.fieldName] : undefined,
            name: item.showName || item.fieldName,
          };
        });
      } else {
        // 设置了维度，使用维度分组取第一个指标
        const indexKey = this.indexFieldName[0];
        return (this.data || []).map(item => {
          return {
            value: indexKey ? item[indexKey] : undefined,
            name: item[this.dimension],
          };
        });
      }
    },
    getSeries() {
      return {
        ...pieSeries,
        radius: this.radius || pieSeries.radius,
        center: this.center || pieSeries.center,
        roseType: this.roseType,
        data: this.getSeriesData,
      };
    },
    getOptions() {
      return {
        title: this.getTitleOption,
        legend: this.getLegend,
        tooltip: this.getTooltipOption,
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
