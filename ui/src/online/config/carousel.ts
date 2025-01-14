import { SysCustomWidgetType } from '@/common/staticDict/index';

const carousel = {
  datasetInfo: {
    name: '数据集',
    value: {
      // 图表数据集
      datasetId: null,
      // api数据集绑定字段
      bindColumnId: null,

      imagePath: [],
      imageName: [],
      // 数据集参数设置
      datasetFilterParams: [],
      // 数据集过滤
      filterList: [],
      // 排序
      orderInfoList: [],
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
      height: '200px',
      cellBackgroundColor: 'rgba(0,0,0,0.6)',
      cellFontSize: 14,
      cellFontColor: '#FFFFFF',
      cellRowHeight: 30,
      cellRowMaxHeight: 30,
      objectFit: 'cover',
      paddingBottom: 20,
      indicatorPosition: 'none',

      componentName: 'carousel',
    },
    customComponent: {
      component: 'PivotTableBasicSetting',
    },
  },
  titleSetting: {
    name: '标题',
    value: {
      show: true,
      text: '轮播图',
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

const carouselConfig = {
  widgetType: SysCustomWidgetType.Carousel,
  icon: 'online-icon icon-carousel',
  attribute: carousel,
  allowEventList: [],
  supportBindTable: true,
  supportBindColumn: false,
};

export default carouselConfig;
