import { ScatterSymbolType } from '@/common/staticDict';
import { ANY_OBJECT } from '@/types/generic';

function getDefaultGrid() {
  const grid = {
    left: '4%',
    right: '4%',
    top: 60,
    bottom: 40,
    containLabel: true,
  };
  return JSON.parse(JSON.stringify(grid));
}

function getDefaultLabel() {
  const label = {
    show: false,
    position: 'top',
    color: '#909399',
    fontSize: 12,
    formatter: '{c}',
  };
  return JSON.parse(JSON.stringify(label));
}

function getDefaultTooltip() {
  const tooltip = {
    show: true,
    trigger: 'item',
    backgroundColor: 'white',
    borderColor: '#909399',
    borderWidth: 1,
    extraCssText: 'box-shadow: 0 0 3px rgba(0, 0, 0, 0.3);',
    padding: 6,
    formatter: undefined,
    textStyle: {
      color: '#909399',
      fontSize: 12,
    },
  };
  return JSON.parse(JSON.stringify(tooltip));
}

function getDefaultXAxis() {
  const xAxis = {
    show: true,
    boundaryGap: false,
    position: 'bottom',
    type: 'category',
    name: undefined,
    nameTextStyle: {
      color: undefined,
      fontSize: 12,
    },
    splitLine: {
      show: false,
      lineStyle: {
        color: '#909399',
        width: 1,
        type: 'solid',
      },
    },
    axisLabel: {
      show: true,
      color: undefined,
      rotate: 0,
      fontSize: 12,
      formatter: '{value}',
    },
  };
  return JSON.parse(JSON.stringify(xAxis));
}

function getDefaultYAxis() {
  const yAxis = {
    show: true,
    position: 'left',
    yAxisValueAuto: true,
    min: undefined,
    max: undefined,
    interval: undefined,
    type: 'value',
    name: undefined,
    nameTextStyle: {
      color: undefined,
      fontSize: 12,
    },
    splitLine: {
      show: true,
      lineStyle: {
        color: '#909399',
        width: 1,
        type: 'solid',
      },
    },
    axisLabel: {
      show: true,
      color: undefined,
      rotate: 0,
      fontSize: 12,
      formatter: '{value}',
    },
    axisLine: {
      show: false,
    },
  };
  return JSON.parse(JSON.stringify(yAxis));
}

function getDefaultTitle() {
  const title = {
    show: true,
    text: '',
    left: 'center',
    top: 'top',
    bold: false,
    italics: false,
    textStyle: {
      color: '#333',
      fontSize: 18,
    },
  };
  return JSON.parse(JSON.stringify(title));
}

function getDefaultLegend() {
  const legend = {
    show: true,
    orient: 'horizontal',
    left: 'center',
    bottom: 10,
    icon: 'circle',
    textStyle: {
      color: '#333',
      fontSize: 12,
    },
  };
  return JSON.parse(JSON.stringify(legend));
}

function getDefaultSerieLine() {
  const serieLine = {
    type: 'line',
    symbol: 'emptyCircle',
    symbolSize: 8,
    smooth: true,
    lineStyle: {
      width: 2,
      type: 'solid',
    },
    // itemStyle: {
    //   normal: {},
    //   emphasis: {
    //     shadowBlur: 10,
    //     shadowOffsetX: 0,
    //     shadowColor: 'rgba(0, 0, 0, 0.5)',
    //   },
    // },
    stack: false,
    step: undefined,
    areaStyle: false,
  };
  return JSON.parse(JSON.stringify(serieLine));
}

function getDefaultSerieBar() {
  const serieBar = {
    type: 'bar',
    autoWidth: true,
    barWidth: 20,
    showBackground: false,
    backgroundStyle: {
      color: '#B4B4B4',
      opacity: 0.2,
    },
    stack: false,
    lateral: false,
  };
  return JSON.parse(JSON.stringify(serieBar));
}

function getDefaultSeriePie() {
  const seriePie = {
    type: 'pie',
    radius: '50%',
    centerX: 50,
    centerY: 50,
    radiusInner: '0%',
    radiusOuter: '70%',
    roseType: undefined,
    label: {
      show: false,
    },
    emphasis: {
      label: {
        show: true,
      },
      itemStyle: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)',
      },
    },
  };
  return JSON.parse(JSON.stringify(seriePie));
}

function getDefaultSerieFunnel() {
  const serieFunnel = {
    name: 'Funnel',
    type: 'funnel',
    top: 60,
    bottom: 60,
    left: 10,
    width: 80,
    minSize: 0,
    maxSize: 100,
    sort: 'descending',
    gap: 2,
    itemStyle: {
      borderColor: '#fff',
      borderWidth: 1,
    },
    emphasis: {
      label: {
        fontSize: 20,
      },
      itemStyle: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)',
      },
    },
  };
  return JSON.parse(JSON.stringify(serieFunnel));
}

function getDefaultRadar() {
  const radar = {
    indicator: [],
    center: ['50%', '50%'],
    centerX: 50,
    centerY: 50,
    radius: 80,
    startAngle: 90,
    shape: 'circle',
    splitNumber: 4,
    splitArea: {
      areaStyle: {},
    },
    axisLine: {
      lineStyle: {
        color: 'rgba(200,200,200, 0.8)',
      },
    },
    splitLine: {
      lineStyle: {
        color: 'rgba(200,200,200, 0.8)',
      },
    },
    axisName: {
      show: true,
      color: '#fff',
      fontSize: 12,
      fontWeight: 'normal',
      backgroundColor: '#666',
      borderRadius: 3,
      padding: [3, 5],
    },
  };
  return JSON.parse(JSON.stringify(radar));
}

function getDefaultSerieRadar() {
  const serieRadar = {
    name: 'Radar',
    type: 'radar',
    itemStyle: {
      borderColor: '',
      borderWidth: 1,
    },
    areaStyleOpacity: 0,
    symbol: 'circle',
    symbolSize: 4,
    colorBy: 'data',
    emphasis: {
      label: {
        fontSize: 20,
      },
      lineStyle: {
        width: 2,
      },
    },
  };
  return JSON.parse(JSON.stringify(serieRadar));
}

function getDefaultSerieGauge() {
  const serieRadar = {
    name: 'Gauge',
    type: 'gauge',
    min: 0,
    max: 1000,
    startAngle: 220,
    endAngle: -40,
    splitNumber: 5,
    radius: '100%',
    center: ['50%', '50%'],
    centerX: 50,
    centerY: 50,
    itemStyle: {
      borderColor: '#fff',
      borderWidth: 1,
      shadowColor: 'rgba(0,138,255,0.45)',
      shadowBlur: 10,
      shadowOffsetX: 2,
      shadowOffsetY: 2,
    },
    progress: {
      show: true,
      roundCap: true,
      width: 4,
    },
    axisLine: {
      roundCap: true,
      lineStyle: {
        width: 18,
        color: [[1, '#5470C6']],
      },
    },
    axisTick: {
      splitNumber: 2,
      lineStyle: {
        width: 2,
        color: '#999',
      },
    },
    splitLine: {
      length: 25,
      lineStyle: {
        width: 3,
        color: '#999',
      },
    },
    axisLabel: {
      distance: 5,
      color: '#999',
      fontSize: 12,
    },
    emphasis: {
      label: {
        fontSize: 20,
      },
      lineStyle: {
        width: 2,
      },
    },
    detail: {
      backgroundColor: 'transparent',
      width: '60%',
      lineHeight: 40,
      height: 40,
      borderRadius: 8,
      fontSize: 20,
      offsetCenter: [0, '35%'],
      offsetCenterX: 0,
      offsetCenterY: 35,
      valueAnimation: true,
    },
  };
  return JSON.parse(JSON.stringify(serieRadar));
}

function getDefaultSerieScatter() {
  const serieScatter = {
    type: 'scatter',
    symbolType: ScatterSymbolType.FIXED,
    itemSize: 20,
    symbolSize: function (data: ANY_OBJECT[]) {
      return data[2];
    },
  };
  return JSON.parse(JSON.stringify(serieScatter));
}

function getDefaultPivotTable() {
  const pivotTable = {
    // 列分组背景色
    headerCellBackgroundColor: '#F8F8F9',
    // 列分组字体颜色
    headerCellFontColor: '#383838',
    // 列分组字体大小
    headerCellFontSize: 14,
    // 列跟组对齐方式
    headerAlign: 'center',
    // 行分组背景色
    rowGroupBackgroundColor: '#F8F8F9',
    // 行分组字体颜色
    rowGroupFontColor: '#383838',
    // 行分组字体大小
    rowGroupFontSize: 12,
    // 行分组对齐方式
    rowGroupAlign: 'center',
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
    // 表头行高
    headerRowHeight: 30,
    // 单元格行高
    cellRowHeight: 30,
    // 总计设置
    totalSetting: {
      row: {
        show: false,
        showName: '总计',
        position: 'top',
      },
      col: {
        show: false,
        showName: '总计',
        position: 'left',
      },
    },
    // 小计设置
    subTotalSetting: {
      row: {
        show: false,
        showName: '小计',
        position: 'top',
      },
      col: {
        show: false,
        showName: '小计',
        position: 'left',
      },
    },
    // 标题设置
    title: {
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
    // 单元格规则设置
    cellRule: {
      // 单元格阈值规则
      cellThreshold: [],
    },
  };
  return JSON.parse(JSON.stringify(pivotTable));
}

function buildChartOptions(options: ANY_OBJECT) {
  const tempOption = JSON.parse(JSON.stringify(options));
  // 标题设置转换
  if (tempOption.title) {
    if (tempOption.title.top === 'top') tempOption.title.top = 10;
    if (tempOption.title.top === 'bottom' && tempOption.title.bottom == null) {
      tempOption.title.bottom = 10;
      tempOption.title.top = undefined;
    }
    if (tempOption.title.left === 'left') tempOption.title.left = 10;
    if (tempOption.title.left === 'right') {
      tempOption.title.right = 10;
      tempOption.title.left = undefined;
    }
  }
  // 图例设置转换
  if (tempOption.legend) {
    if (tempOption.legend.top === 'top') tempOption.legend.top = 10;
    if (tempOption.legend.top === 'bottom' && tempOption.legend.bottom == null) {
      tempOption.legend.bottom = 10;
      tempOption.legend.top = undefined;
    }
    if (tempOption.legend.left === 'left') tempOption.legend.left = 10;
    if (tempOption.legend.left === 'right') {
      tempOption.legend.right = 10;
      tempOption.legend.left = undefined;
    }
  }
  return tempOption;
}

function getDefaultProgressBar() {
  const series = {
    defineBackColor: '#ECECEC',
    strokeWidth: 12,
    color: '#F77712',
    showText: true,
    textInside: false,
    maxValue: 100,
    textStyle: {
      text: '{percent}%',
      color: '#333333',
    },
  };
  return JSON.parse(JSON.stringify(series));
}

function getDefaultProgressCircle() {
  const series = {
    defineBackColor: '#ECECEC',
    strokeWidth: 12,
    color: '#F77712',
    width: 200,
    strokeLinecap: 'round',
    maxValue: 100,
    textStyle: {
      text: '{percent}%',
      fontSize: 33,
      color: '#525252',
      bold: true,
      italics: false,
    },
  };
  return JSON.parse(JSON.stringify(series));
}

function getDefaultDataCard() {
  const series = {
    icon: '',
    iconRadius: 30,
    iconSize: 60,
    mainTextStyle: {
      text: '',
      fontSize: 14,
      color: '#333333',
      bold: false,
      italics: false,
    },
    numTextStyle: {
      fontSize: 30,
      color: '#333333',
      bold: true,
      italics: false,
    },
    footTextStyle: {
      fontSize: 14,
      textAlign: 'left',
      color: '#999999',
    },
    footNumTextStyle: {
      fontSize: 14,
      textAlign: 'right',
      color: '#999999',
    },
  };
  return JSON.parse(JSON.stringify(series));
}

function getDefaultDataProgressCard() {
  const series = {
    icon: '',
    iconRadius: 30,
    iconSize: 60,
    mainTextStyle: {
      text: '',
      fontSize: 14,
      color: '#333333',
      bold: false,
      italics: false,
    },
    numTextStyle: {
      fontSize: 30,
      color: '#333333',
      bold: true,
      italics: false,
    },
    progress: {
      ...getDefaultProgressBar(),
      strokeWidth: 8,
      textStyle: {
        ...getDefaultProgressBar().textStyle,
        text: '{percent}%',
        textPosition: 'right',
        width: 60,
        fontSize: 14,
        color: '#999999',
        bold: false,
        italics: false,
      },
    },
  };
  return JSON.parse(JSON.stringify(series));
}

function getDefaultCommonList() {
  const series = {
    listIcon: 'circle',
    listIconColor: '#FF7701',
    titleStyle: {
      text: '',
      fontSize: 14,
      color: '#333333',
      bold: true,
      italics: false,
    },
    textStyle: {
      fontSize: 14,
      color: '#999999',
      bold: false,
      italics: false,
    },
    timeStyle: {
      fontSize: 12,
      color: '#333333',
    },
  };
  return JSON.parse(JSON.stringify(series));
}

export {
  getDefaultGrid,
  getDefaultLabel,
  getDefaultTooltip,
  getDefaultXAxis,
  getDefaultYAxis,
  getDefaultTitle,
  getDefaultLegend,
  getDefaultSerieLine,
  getDefaultSerieBar,
  getDefaultSeriePie,
  getDefaultSerieFunnel,
  getDefaultRadar,
  getDefaultSerieRadar,
  getDefaultSerieGauge,
  getDefaultSerieScatter,
  getDefaultPivotTable,
  getDefaultProgressBar,
  getDefaultProgressCircle,
  getDefaultDataCard,
  getDefaultDataProgressCard,
  getDefaultCommonList,
  buildChartOptions,
};
