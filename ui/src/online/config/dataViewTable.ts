import { SysCustomWidgetType } from '@/common/staticDict/index';

const dataViewTable = {
  datasetInfo: {
    name: '数据集',
    value: {
      // 图表数据集
      datasetId: null,
      // api数据集绑定字段
      bindColumnId: null,
      showColumnList: [],
      // 数据集参数设置
      datasetFilterParams: [],
      // 数据集过滤
      filterList: [],
      // 排序
      orderInfoList: [],

      hidePageSort: true,
      hidePageParam: true,
      pageParam: {
        pageNum: 1,
        pageSize: 10,
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
      paddingBottom: 20,
      // 边框颜色
      borderColor: '#D7D7D7',
      // 单元格行高
      cellRowHeight: 30,
      componentName: 'dataViewTable',
    },
    customComponent: {
      component: 'PivotTableBasicSetting',
    },
  },
  rowSetting: {
    name: '行设置',
    value: {
      // 行分组背景色
      backgroundColor: '#F8F8F9',
      // 行分组字体颜色
      fontColor: '#383838',
      // 行分组字体大小
      fontSize: 12,
      // 行分组对齐方式
      align: 'center',
    },
    customComponent: {
      component: 'PivotTableGroupSetting',
      props: {
        fieldName: 'rowSetting',
      },
    },
  },
  columnSetting: {
    name: '列设置',
    value: {
      // 列分组背景色
      backgroundColor: '#F8F8F9',
      // 列分组字体颜色
      fontColor: '#383838',
      // 列分组字体大小
      fontSize: 14,
      // 表头行高
      headerRowHeight: 30,
      // 列分组对齐方式
      align: 'center',
    },
    customComponent: {
      component: 'PivotTableGroupSetting',
      props: {
        fieldName: 'columnSetting',
      },
    },
  },
  pagerSetting: {
    name: '页码设置',
    value: {
      show: true,
      // 分页样式
      background: '0',
      // 大小
      size: 'default',
      // 对齐方式
      align: 'center',
    },
    customComponent: {
      component: 'DateViewTablePagerSetting',
      props: {
        fieldName: 'pagerSetting',
      },
    },
  },
  titleSetting: {
    name: '标题',
    value: {
      show: true,
      text: '普通表格',
      left: 'center',
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
};

const dataViewTableConfig = {
  widgetType: SysCustomWidgetType.DataViewTable,
  icon: 'online-icon icon-dataview',
  attribute: dataViewTable,
  allowEventList: [],
  supportBindTable: true,
  supportBindColumn: false,
};

export default dataViewTableConfig;
