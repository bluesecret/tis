import { SysCustomWidgetType } from '@/common/staticDict/index';

import {
  getDefaultLabel,
  getDefaultTooltip,
  getDefaultTitle,
  getDefaultLegend,
  getDefaultSerieFunnel,
} from '@/components/Charts/chartOption';

const lineChart = {
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
      ...getDefaultSerieFunnel(),
    },
    customComponent: {
      component: 'ChartSeriesSetting',
    },
  },
  labelSetting: {
    name: '标签设置',
    value: {
      ...getDefaultLabel(),
      position: 'inside',
      formatter: '{b}',
    },
    customComponent: {
      component: 'ChartLabelSetting',
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
  titleSetting: {
    name: '标题设置',
    value: {
      ...getDefaultTitle(),
      text: '漏斗图',
    },
    customComponent: {
      component: 'ChartTitleSetting',
    },
  },
  legendSetting: {
    name: '图例设置',
    value: {
      ...getDefaultLegend(),
    },
    customComponent: {
      component: 'ChartLegendSetting',
    },
  },
};

const funnelChartConfig = {
  widgetType: SysCustomWidgetType.FunnelChart,
  icon: 'online-icon icon-funnel',
  attribute: lineChart,
  allowEventList: [],
  supportBindTable: true,
  supportBindColumn: false,
};

export default funnelChartConfig;
