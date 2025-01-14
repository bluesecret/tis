import { SysCustomWidgetType } from '@/common/staticDict/index';

import { getDefaultProgressBar } from '@/components/Charts/chartOption';

const progressBar = {
  datasetInfo: {
    name: '数据集',
    value: {
      // 图表数据集
      datasetId: null,
      // api数据集绑定字段
      bindColumnId: null,

      textColumn: [],
      progressColumn: [],
      // 数据集参数设置
      datasetFilterParams: [],
      // 数据集过滤
      filterList: [],
      // 排序
      orderInfoList: [],

      hidePageParam: true,
      pageParam: {
        pageNum: 1,
        pageSize: 4,
      },
    },
    customComponent: {
      component: 'ChartDatasetSetting',
    },
  },
  basicInfo: {
    name: '基础信息',
    value: {
      span: 12,
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
      text: '普通进度条',
      left: 'left',
      bold: true,
      italics: false,
      textStyle: {
        color: '##333333',
        fontSize: 16,
      },
    },
    customComponent: {
      component: 'ChartTitleSetting',
    },
  },
  seriesSetting: {
    name: '图表设置',
    value: {
      ...getDefaultProgressBar(),
      width: undefined,
      textStyle: {
        text: '{text}:{percent}%',
        textPosition: 'right',
        width: 80,
        fontSize: 14,
        color: '#333333',
        bold: false,
        italics: false,
      },
    },
    customComponent: {
      component: 'ChartSeriesSetting',
    },
  },
};

const progressBarConfig = {
  widgetType: SysCustomWidgetType.ProgressBar,
  icon: 'online-icon icon-progress',
  attribute: progressBar,
  allowEventList: [],
  supportBindTable: true,
  supportBindColumn: false,
};

export default progressBarConfig;
