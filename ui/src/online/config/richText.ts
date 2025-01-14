import { SysCustomWidgetType } from '@/common/staticDict/index';

const richText = {
  datasetInfo: {
    name: '数据集',
    value: {
      // 图表数据集
      datasetId: null,
      // api数据集绑定字段
      bindColumnId: null,
      textColumn: [],
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
      span: 24,
      paddingBottom: 20,
    },
    customComponent: {
      component: 'PivotTableBasicSetting',
    },
  },
  titleSetting: {
    name: '标题',
    value: {
      show: true,
      text: '富文本',
      left: 'center',
      top: 'top',
      bold: false,
      italics: false,
      textStyle: {
        color: '#333',
        fontSize: 18,
      },
    },
    customComponent: {
      component: 'ChartTitleSetting',
    },
  },
  cellRule: {
    name: '单元格规则设置',
    value: {
      cellThreshold: [],
    },
    visible: false,
  },
};

const richTextConfig = {
  widgetType: SysCustomWidgetType.RichText,
  icon: 'online-icon icon-richtext',
  attribute: richText,
  allowEventList: [],
  supportBindTable: true,
  supportBindColumn: false,
};

export default richTextConfig;
