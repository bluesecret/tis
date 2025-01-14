import { SysCustomWidgetType } from '@/common/staticDict/index';

import { getDefaultProgressCircle } from '@/components/Charts/chartOption';

const progressCircle = {
  datasetInfo: {
    name: '数据集',
    value: {
      // 图表数据集
      datasetId: null,
      // api数据集绑定字段
      bindColumnId: null,
      progressColumn: [],
      // 数据集参数设置
      datasetFilterParams: [],
      // 数据集过滤
      filterList: [],
      // 排序
      orderInfoList: [],
      hidePageParam: true,
    },
    customComponent: {
      component: 'ChartDatasetSetting',
    },
  },
  basicInfo: {
    name: '基础信息',
    value: {
      span: 6,
      height: '300px',
      paddingBottom: 20,
    },
    customComponent: {
      component: 'ChartBasicSetting',
    },
  },
  titleSetting: {
    name: '标题',
    value: {
      show: true,
      text: '环形进度条',
      left: 'left',
      bold: true,
      italics: false,
      textStyle: {
        color: '#333333',
        fontSize: 16,
        bold: true,
        italics: false,
      },
    },
    customComponent: {
      component: 'ChartTitleSetting',
    },
  },
  seriesSetting: {
    name: '图表设置',
    value: {
      ...getDefaultProgressCircle(),
    },
    customComponent: {
      component: 'ChartSeriesSetting',
    },
  },
};

const progressCircleConfig = {
  widgetType: SysCustomWidgetType.ProgressCircle,
  icon: 'online-icon icon-circle-progress',
  attribute: progressCircle,
  allowEventList: [],
  supportBindTable: true,
  supportBindColumn: false,
};

export default progressCircleConfig;
