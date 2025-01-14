<template>
  <BaseChart ref="chart" :options="getFinalOptions" />
</template>

<script>
import { deepMerge } from '@/common/utils';
import BaseChart from './base.vue';
import { titleOption, tooltipOption, legendOption, funnelSeries } from './options.js';

export default {
  name: 'FunnelCharts',
  components: {
    BaseChart,
  },
  props: {
    // 标题
    title: {
      type: String,
      default: '',
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
    position: {
      type: Object,
      default: () => {
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
        // 没有设置维度，返回指标数组
        const data = (this.data || [])[0];
        if (data == null) return [];
        return (this.indexList || []).map(item => {
          return {
            name: item.showName || item.fieldName,
            value: data[item.fieldName],
          };
        });
      } else {
        // 设置维度，根据维度分组取第一个指标值
        if (!Array.isArray(this.indexFieldName) || this.indexFieldName.length === 0) return [];
        return (this.data || []).map(data => {
          return {
            name: data[this.dimension],
            value: data[this.indexFieldName[0]],
          };
        });
      }
    },
    getSeries() {
      return (this.indexList || []).map(item => {
        return {
          ...funnelSeries,
          ...this.position,
          name: item.showName || item.fieldName,
          data: this.getSeriesData,
        };
      });
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
      console.log(this.getOptions);
      return deepMerge(this.getOptions, this.options);
    },
  },
};
</script>

<style></style>
