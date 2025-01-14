import { SysCustomWidgetType, SysCustomWidgetOperationType } from '@/common/staticDict/index';

import { getDefaultCommonList } from '@/components/Charts/chartOption';

const commonList = {
  datasetInfo: {
    name: '数据集',
    value: {
      // 图表数据集
      datasetId: null,
      // api数据集绑定字段
      bindColumnId: null,

      titleColumn: [],
      textColumn: [],
      timeColumn: [],
      // 数据集参数设置
      datasetFilterParams: [],
      // 数据集过滤
      filterList: [],
      // 排序
      orderInfoList: [],

      hidePageParam: false,
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
      height: '325px',
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
      text: '通用列表',
      left: 'left',
      bold: true,
      italics: false,
      textStyle: {
        color: '#333333',
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
      ...getDefaultCommonList(),
    },
    customComponent: {
      component: 'ChartSeriesSetting',
    },
  },
};

const commonListConfig = {
  widgetType: SysCustomWidgetType.CommonList,
  icon: 'online-icon icon-common-list',
  attribute: commonList,
  allowEventList: [],
  operationList: [
    {
      id: 1,
      type: SysCustomWidgetOperationType.ROUTE,
      name: SysCustomWidgetOperationType.getValue(SysCustomWidgetOperationType.ROUTE),
      enabled: false,
      routeName: '',
      routeParams: [],
    },
  ],
  supportBindTable: false,
  supportBindColumn: false,
};

export default commonListConfig;
