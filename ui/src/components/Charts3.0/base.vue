<template>
  <div style="width: 100%; height: 100%; background: white" />
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: 'baseChart',
  props: {
    options: {
      type: Object,
      default: () => {
        return {};
      },
    },
  },
  data() {
    return {
      chart: null,
    };
  },
  watch: {
    options: {
      handler() {
        if (this.chart) this.chart.setOption(this.options);
      },
      deep: true,
    },
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      // eslint-disable-next-line import/namespace
      this.chart = echarts.init(this.$el);
      this.chart.setOption(this.options);
    },
    resize() {
      this.chart.resize();
    },
  },
};
</script>

<style></style>
