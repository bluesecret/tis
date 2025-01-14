import { ANY_OBJECT } from '@/types/generic';
// 标题配置
export const titleOption: ANY_OBJECT = {
  show: true,
  text: undefined,
  textStly: {
    color: '#333333',
    fontStyle: 'normal',
    fontWeight: 'bold',
    fontSize: 18,
    textBorderColor: undefined,
    textBorderWidth: undefined,
    textBorderType: 'solid',
  },
  subtext: undefined,
  subtextStyle: {
    color: '#aaaaaa',
    fontStyle: 'normal',
    fontWeight: 'bold',
    fontSize: 12,
    textBorderColor: undefined,
    textBorderWidth: undefined,
    textBorderType: 'solid',
  },
  top: '15',
  left: 'center',
};
// 图例配置
export const legendOption: ANY_OBJECT = {
  show: true,
  type: 'plain',
  left: 'center',
  top: 'bottom',
  orient: 'horizontal',
  align: 'auto',
  padding: 5,
  formatter: '{name}',
  selectedMode: true,
};
// 二维网格坐标系配置
export const gridOption: ANY_OBJECT = {
  show: false,
  left: '10%',
  top: 60,
  right: '10%',
  bottom: 60,
  containLabel: false,
  backgroundColor: 'transparent',
};
// 雷达坐标系配置
export const radarOption: ANY_OBJECT = {
  center: ['50%', '60%'],
  radius: '60%',
  shape: 'polygon',
  splitNumber: 5,
  axisName: {
    show: true,
    formatter: '{value}',
    color: '#333333',
    fontSize: 12,
  },
};
// X轴配置
export const xAxisOption: ANY_OBJECT = {
  show: true,
  position: 'bottom',
  /*
   * 坐标轴类型
   * 'value' 数值轴，适用于连续数据
   * 'category' 类目轴，适用于离散的类目数据，为该类型时必须通过 data 设置类目数据
   * 'time' 时间轴，适用于连续的时序数据，与数值轴相比时间轴带有时间的格式化，在刻度计算上也有所不同
   * 'log' 对数轴。适用于对数数据
   */
  type: 'category',
  boundaryGap: false,
  offset: 2,
  // 轴线设置
  axisLine: {
    show: true,
    symbol: 'none',
    symbolSize: 10,
    symbolOffset: 0,
    // 轴线设置
    lineStyle: {
      color: '#333333',
      width: 1,
      type: 'solid',
      opacity: 1,
    },
  },
  // 刻度设置
  axisTick: {
    show: true,
    inside: false,
    length: 5,
  },
  // 刻度标签
  axisLabel: {
    show: true,
    interval: 'auto',
    inside: false,
    rotate: 0,
    margin: 8,
    formatter: '{value}',
    hideOverlap: true,
    fontSize: 12,
  },
  // 分割线设置
  splitLine: {
    show: false,
    interval: 'auto',
    lineStyle: {
      color: '#cccccc',
      width: 1,
      type: 'solid',
      opacity: 1,
    },
  },
};
// Y轴配置
export const yAxisOption: ANY_OBJECT = {
  show: true,
  position: 'left',
  /*
   * 坐标轴类型
   * 'value' 数值轴，适用于连续数据
   * 'category' 类目轴，适用于离散的类目数据，为该类型时必须通过 data 设置类目数据
   * 'time' 时间轴，适用于连续的时序数据，与数值轴相比时间轴带有时间的格式化，在刻度计算上也有所不同
   * 'log' 对数轴。适用于对数数据
   */
  type: 'value',
  boundaryGap: false,
  offset: 0,
  // 轴线设置
  axisLine: {
    show: true,
    symbol: 'none',
    symbolSize: 10,
    symbolOffset: 0,
    // 轴线设置
    lineStyle: {
      color: '#333333',
      width: 1,
      type: 'solid',
      opacity: 1,
    },
  },
  // 刻度设置
  axisTick: {
    show: true,
    inside: false,
    length: 5,
  },
  // 刻度标签
  axisLabel: {
    show: true,
    interval: 'auto',
    inside: false,
    rotate: 0,
    margin: 8,
    formatter: '{value}',
    hideOverlap: true,
    fontSize: 12,
  },
  // 分割线设置
  splitLine: {
    show: true,
    interval: 'auto',
    lineStyle: {
      color: '#cccccc',
      width: 1,
      type: 'solid',
      opacity: 1,
    },
  },
};
// 提示配置
export const tooltipOption: ANY_OBJECT = {
  show: true,
  trigger: 'item',
  alwaysShowContent: false,
  triggerOn: 'mousemove|click',
  textStyle: {
    color: '#ffffff',
    fontSize: 14,
  },
};
// 折线图
export const lineSeries: ANY_OBJECT = {
  type: 'line',
  coordinateSystem: 'cartesian2d',
  showSymbol: true,
  symbol: 'emptyCircle',
  symbolSize: 4,
  symbolRotate: 0,
  symbolOffset: [0, 0],
  stack: undefined,
  // 是否阶梯[start, middle, end]
  step: false,
  label: {
    show: false,
    position: 'top',
    distance: 5,
    rotate: 0,
    offset: [0, 0],
    formatter: '{c}',
    color: 'inherit',
    fontSize: 12,
  },
  lineStyle: {
    width: 2,
    type: 'solid',
    opacity: 1,
  },
  areaStyle: {
    opacity: 0,
  },
};
// 柱状图
export const barSeries: ANY_OBJECT = {
  type: 'bar',
  colorBy: 'series',
  coordinateSystem: 'cartesian2d',
  showBackground: false,
  barWidth: undefined,
  backgroundStyle: {
    color: 'rgba(180, 180, 180, 0.2)',
    opacity: 1,
  },
  label: {
    show: false,
    position: 'top',
    distance: 5,
    rotate: 0,
    offset: [0, 0],
    formatter: '{c}',
    color: 'inherit',
    fontSize: 12,
  },
};
// 饼图
export const pieSeries: ANY_OBJECT = {
  type: 'pie',
  radius: ['0%', '70%'],
  center: ['50%', '50%'],
  roseType: false,
  avoidLabelOverlap: true,
  label: {
    show: false,
    position: 'outside',
    distanceToLabelLine: 5,
    rotate: 0,
    offset: [0, 0],
    formatter: '{b}: {c}',
    color: 'inherit',
    fontSize: 12,
  },
  labelLine: {
    show: true,
    length: 5,
    length2: 5,
    smooth: false,
    lineStyle: {
      width: 1,
      type: 'solid',
      opacity: 1,
    },
  },
  itemStyle: {
    borderWidth: 0,
    borderColor: '#ffffff',
    borderType: 'solid',
    opacity: 1,
  },
};
// 雷达图
export const radarSeries: ANY_OBJECT = {
  type: 'radar',
  symbol: 'emptyCircle',
  symbolSize: 4,
  symbolRotate: 0,
  symbolOffset: [0, 0],
  label: {
    show: false,
    position: 'top',
    distance: 5,
    rotate: 0,
    offset: [0, 0],
    formatter: '{c}',
    color: 'inherit',
    fontSize: 12,
  },
  lineStyle: {
    width: 2,
    type: 'solid',
    opacity: 1,
  },
  areaStyle: {
    opacity: 0,
  },
};
// 散点图
export const scatterSeries: ANY_OBJECT = {
  type: 'scatter',
  coordinateSystem: 'cartesian2d',
  symbol: 'circle',
  symbolSize: 10,
  label: {
    show: false,
    position: 'top',
    distance: 5,
    rotate: 0,
    offset: [0, 0],
    formatter: '{c}',
    color: 'inherit',
    fontSize: 12,
  },
};
// 漏斗图
export const funnelSeries: ANY_OBJECT = {
  type: 'funnel',
  left: '10%',
  right: '10%',
  top: 60,
  bottom: 60,
  width: '80%',
  orient: 'vertical',
  sort: 'descending',
  gap: 2,
  label: {
    show: true,
    position: 'inside',
  },
  labelLine: {
    length: 10,
    lineStyle: {
      width: 1,
      type: 'solid',
    },
  },
  itemStyle: {
    borderColor: '#fff',
    borderWidth: 1,
  },
  emphasis: {
    label: {
      fontSize: 20,
    },
  },
};
