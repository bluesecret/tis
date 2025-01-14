import {
  SysCustomWidgetType,
  SysCustomWidgetOperationType,
  DirectionType,
} from '@/common/staticDict/index';

import {
  getDefaultGrid,
  getDefaultLabel,
  getDefaultTooltip,
  getDefaultXAxis,
  getDefaultYAxis,
  getDefaultTitle,
  getDefaultLegend,
  getDefaultSerieBar,
  // getDefaultSeriePie,
  // getDefaultSerieScatter,
  // getDefaultPivotTable
} from '@/components/Charts/chartOption';

const barChart = {
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
      grid: {
        ...getDefaultGrid(),
      },
    },
    customComponent: {
      component: 'ChartBasicSetting',
    },
  },
  seriesSetting: {
    name: '图表设置',
    value: {
      ...getDefaultSerieBar(),
    },
    customComponent: {
      component: 'ChartSeriesSetting',
    },
  },
  labelSetting: {
    name: '标签设置',
    value: {
      ...getDefaultLabel(),
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
  xAxisSetting: {
    name: 'X 轴设置',
    value: {
      ...getDefaultXAxis(),
      type: 'category',
      boundaryGap: true,
    },
    customComponent: {
      component: 'ChartAxisSetting',
      props: {
        axisType: DirectionType.HORIZONTAL,
      },
    },
  },
  yAxisSetting: {
    name: 'Y 轴设置',
    value: {
      ...getDefaultYAxis(),
    },
    customComponent: {
      component: 'ChartAxisSetting',
      props: {
        axisType: DirectionType.VERTICAL,
      },
    },
  },
  titleSetting: {
    name: '标题设置',
    value: {
      ...getDefaultTitle(),
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

const barChartConfig = {
  widgetType: SysCustomWidgetType.BarChart,
  icon: 'online-icon icon-barchart',
  attribute: barChart,
  allowEventList: [],
  operationList: [
    {
      id: 1,
      type: SysCustomWidgetOperationType.DRILL,
      name: SysCustomWidgetOperationType.getValue(SysCustomWidgetOperationType.DRILL),
      builtin: true,
      enabled: false,
      groupId: undefined,
      pageId: undefined,
      pageCode: undefined,
      filterList: [],
    },
  ],
  supportBindTable: true,
  supportBindColumn: false,
};

export default barChartConfig;
