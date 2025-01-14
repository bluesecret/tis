// const radarseries = [{
//   'name': '手机',
//   'type': 'radar',
//   'symbol': 'emptyCircle',
//   'symbolSize': 8,
//   'smooth': true,
//   'lineStyle': {'width': 2, 'type': 'solid'},
//   'itemStyle': {'normal': {}, 'emphasis': {'shadowBlur': 10, 'shadowOffsetX': 0, 'shadowColor': 'rgba(0, 0, 0, 0.5)'}},
//   'data': []
// }]
const baseData = [96, 86, 73, 68, 82, 92, 62, 82];
// const dataMax = [{name: '易用性', max: 100}, {name: '易用性', max: 100}, {name: '功能', max: 100}, {name: '跑分', max: 100}, {name: '续航', max: 100}]
export const lineChartData = baseData;
export const barChartData = baseData;
export const pieChartData = [
  { value: 1048, name: 'Search Engine' },
  { value: 735, name: 'Direct' },
  { value: 580, name: 'Email' },
  { value: 484, name: 'Union Ads' },
  { value: 300, name: 'Video Ads' },
];
export const scatterChartData = baseData;
export const funnelChartData = baseData;
export const radarChartData = [
  {
    name: '华为手机',
    value: [80, 90, 80, 82, 90],
  },
  {
    name: '中兴手机',
    value: [70, 82, 75, 70, 78],
  },
];
