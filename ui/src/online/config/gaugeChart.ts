import { SysCustomWidgetType } from '@/common/staticDict/index';

import {
  getDefaultTooltip,
  getDefaultTitle,
  getDefaultSerieGauge,
} from '@/components/Charts/chartOption';

const gaugeChart = {
  datasetInfo: {
    name: '数据集',
    value: {
      // 图表数据集
      datasetId: null,
      // api数据集绑定字段
      bindColumnId: null,
      // 数据集参数设置
      datasetFilterParams: [],
      // 维度列表
      categroyColumnList: [],
      // 指标列表
      valueColumnList: [],
      // 数据集过滤
      filterList: [],
      // 排序
      orderInfoList: [],
    },
    customComponent: {
      component: 'ChartDatasetSetting',
    },
  },
  basicInfo: {
    name: '基础设置',
    value: {
      span: 24,
      height: '200px',
      paddingBottom: 20,
      backgroundColor: '#FFFFFF',
      borderRaduis: 5,
      alpha: 100,
      chartColors: [
        '#5470C6',
        '#91CC75',
        '#FAC858',
        '#EE6666',
        '#73C0DE',
        '#3BA272',
        '#FC8452',
        '#9A60B4',
        '#EA7CCC',
      ],
      chartAlpha: 100,
    },
    customComponent: {
      component: 'ChartBasicSetting',
    },
  },
  seriesSetting: {
    name: '图表设置',
    value: {
      ...getDefaultSerieGauge(),
    },
    customComponent: {
      component: 'ChartSeriesSetting',
    },
  },
  titleSetting: {
    name: '标题设置',
    value: {
      ...getDefaultTitle(),
      text: '仪表盘',
    },
    customComponent: {
      component: 'ChartTitleSetting',
    },
  },
  tooltipSetting: {
    name: '提示设置',
    value: {
      ...getDefaultTooltip(),
    },
    customComponent: {
      component: 'ChartTooltipSetting',
    },
  },
};

const gaugeChartConfig = {
  widgetType: SysCustomWidgetType.GaugeChart,
  icon: 'online-icon icon-pivottable',
  attribute: gaugeChart,
  allowEventList: [],
  supportBindTable: true,
  supportBindColumn: false,
};

export default gaugeChartConfig;
