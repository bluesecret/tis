import { SysCustomWidgetType, SysCustomWidgetOperationType } from '@/common/staticDict/index';

import { getDefaultDataCard } from '@/components/Charts/chartOption';

const dataCard = {
  datasetInfo: {
    name: '数据集',
    value: {
      // 图表数据集
      datasetId: null,
      // api数据集绑定字段
      bindColumnId: null,
      mainTextColumn: [],
      numTextColumn: [],
      footTextColumn: [],
      footNumIconColumn: [],
      footNumTextColumn: [],
      // 数据集参数设置
      datasetFilterParams: [],
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
    name: '基础信息',
    value: {
      span: 6,
      paddingBottom: 20,
    },
    customComponent: {
      component: 'ChartBasicSetting',
    },
  },
  seriesSetting: {
    name: '图表设置',
    value: {
      ...getDefaultDataCard(),
    },
    customComponent: {
      component: 'ChartSeriesSetting',
    },
  },
};

const dataCardConfig = {
  widgetType: SysCustomWidgetType.DataCard,
  icon: 'online-icon icon-common-card',
  attribute: dataCard,
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
  supportBindTable: true,
  supportBindColumn: false,
};

export default dataCardConfig;
