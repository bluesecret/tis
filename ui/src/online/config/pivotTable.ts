import { SysCustomWidgetType } from '@/common/staticDict/index';

const pivotTable = {
  datasetInfo: {
    name: '数据集',
    value: {
      // 图表数据集
      datasetId: null,
      // api数据集绑定字段
      bindColumnId: null,
      // 数据集参数设置
      datasetFilterParams: [],
      // 行分组
      rowGroupColumnList: [],
      // 列分组
      columnGroupColumnList: [],
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
    name: '基础信息',
    value: {
      span: 24,
      height: '200px',
      paddingBottom: 20,
      // 边框颜色
      borderColor: '#D7D7D7',
      // 单元格背景色
      cellBackgroundColor: '#FFFFFF',
      // 单元格字体颜色
      cellFontColor: '#383838',
      // 单元格字体大小
      cellFontSize: 12,
      // 单元格对齐方式
      cellAlign: 'left',
      // 单元格行高
      cellRowHeight: 30,
    },
    customComponent: {
      component: 'PivotTableBasicSetting',
    },
  },
  rowGroupSetting: {
    name: '行分组设置',
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
        fieldName: 'rowGroupSetting',
      },
    },
  },
  columnGroupSetting: {
    name: '列分组设置',
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
        fieldName: 'columnGroupSetting',
      },
    },
  },
  rowTotalSetting: {
    name: '行总计',
    value: {
      show: false,
      showName: '总计',
      position: 'bottom',
    },
    customComponent: {
      component: 'PivotTableTotalSetting',
      props: {
        fieldName: 'rowTotalSetting',
      },
    },
  },
  colTotalSetting: {
    name: '列总计',
    value: {
      show: false,
      showName: '总计',
      position: 'right',
    },
    customComponent: {
      component: 'PivotTableTotalSetting',
      props: {
        fieldName: 'colTotalSetting',
      },
    },
  },
  rowSubTotalSetting: {
    name: '行小计',
    value: {
      show: false,
      showName: '总计',
      position: 'bottom',
    },
    visible: false,
    customComponent: {
      component: 'PivotTableTotalSetting',
      props: {
        fieldName: 'rowSubTotalSetting',
      },
    },
  },
  colSubTotalSetting: {
    name: '列小计',
    value: {
      show: false,
      showName: '总计',
      position: 'right',
    },
    visible: false,
    customComponent: {
      component: 'PivotTableTotalSetting',
      props: {
        fieldName: 'colSubTotalSetting',
      },
    },
  },
  titleSetting: {
    name: '标题',
    value: {
      show: true,
      text: '透视表',
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

const pivotTableConfig = {
  widgetType: SysCustomWidgetType.PivotTable,
  icon: 'online-icon icon-pivottable',
  attribute: pivotTable,
  allowEventList: [],
  supportBindTable: true,
  supportBindColumn: false,
};

export default pivotTableConfig;
