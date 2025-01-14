<template>
  <el-form
    label-position="top"
    size="default"
    v-if="seriesSetting"
    style="padding: 10px 16px 0"
    @submit.prevent
  >
    <LineChartSetting
      v-if="widget.widgetType === ReportWidgetType.LineChart"
      v-model:value="widget.props.seriesSetting"
    />
    <BarChartSetting
      v-if="widget.widgetType === ReportWidgetType.BarChart"
      v-model:value="widget.props.seriesSetting"
    />
    <PieChartSetting
      v-if="widget.widgetType === ReportWidgetType.PieChart"
      v-model:value="widget.props.seriesSetting"
    />
    <FunnelChartSetting
      v-if="widget.widgetType === ReportWidgetType.FunnelChart"
      v-model:value="widget.props.seriesSetting"
    />
    <RadarSeriesSetting
      v-if="widget.widgetType === ReportWidgetType.RadarChart"
      v-model:value="widget.props.seriesSetting"
    />
    <RadarRadarSetting
      v-if="widget.widgetType === ReportWidgetType.RadarChart"
      v-model:value="widget.props.radarSetting"
    />
    <ScatterChartSetting
      v-if="widget.widgetType === ReportWidgetType.ScatterChart"
      v-model:value="widget.props.seriesSetting"
    />
    <GuageChartSetting
      v-if="widget.widgetType === ReportWidgetType.GaugeChart"
      v-model:value="widget.props.seriesSetting"
    />
    <ProgressBarSetting
      v-if="widget.widgetType === ReportWidgetType.ProgressBar"
      v-model:value="widget.props.seriesSetting"
    />
    <ProgressCircleSetting
      v-if="widget.widgetType === ReportWidgetType.ProgressCircle"
      v-model:value="widget.props.seriesSetting"
    />
    <DataCardSetting
      v-if="widget.widgetType === ReportWidgetType.DataCard"
      v-model:value="widget.props.seriesSetting"
    />
    <DataProgressCardSetting
      v-if="widget.widgetType === ReportWidgetType.DataProgressCard"
      v-model:value="widget.props.seriesSetting"
    />
    <CommonListSetting v-if="widget.widgetType === ReportWidgetType.CommonList" />
  </el-form>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { ReportWidgetType } from '@/common/staticDict/report';
import LineChartSetting from './lineSetting.vue';
import BarChartSetting from './barSetting.vue';
import PieChartSetting from './pieSetting.vue';
import FunnelChartSetting from './funnelSetting.vue';
import RadarSeriesSetting from './radarPropSeriesSetting.vue';
import RadarRadarSetting from './radarPropRadarSetting.vue';
import ScatterChartSetting from './scatterSetting.vue';
import GuageChartSetting from './gaugeSetting.vue';
import ProgressBarSetting from './progressBarSetting.vue';
import ProgressCircleSetting from './progressCircleSetting.vue';
import DataCardSetting from './dataCardSetting.vue';
import DataProgressCardSetting from './dataProgressCardSetting.vue';
import CommonListSetting from './commonListSetting.vue';

const formConfig = inject('formConfig', () => {
  console.error('ChartSeriesSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});
const widget = computed(() => {
  return formConfig().currentWidget;
});
const seriesSetting = computed(() => {
  return widget.value ? (widget.value.props || {}).seriesSetting : undefined;
});
</script>
