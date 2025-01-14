import { ANY_OBJECT } from '@/types/generic';
import { SysCustomWidgetType } from '@/common/staticDict/index';
import widgetData from './reportRender/components/index';

export function getAllWidgetList(rootList: ANY_OBJECT[], activeMode: string, onlyFilter = false) {
  // 查找过滤组件类型
  const widgetGroup = widgetData.formWidgetGroupList
    ? widgetData.formWidgetGroupList[activeMode]
    : [];
  let allowFilterWidgetList: ANY_OBJECT[] = [];
  if (Array.isArray(widgetGroup)) {
    widgetGroup.forEach(group => {
      if (group.id === 'filter') {
        allowFilterWidgetList = group.widgetList.map((item: ANY_OBJECT) => item.widgetType);
      }
    });
  }
  function addWidgetToList(widget: ANY_OBJECT, widgetList: ANY_OBJECT[] = []) {
    if (!onlyFilter || allowFilterWidgetList.indexOf(widget.widgetType) !== -1) {
      widgetList.push(widget);
    }
    if (Array.isArray(widget.childWidgetList)) {
      widget.childWidgetList.forEach(subWidget => {
        addWidgetToList(subWidget, widgetList);
      });
    }
  }

  const temp: ANY_OBJECT[] = [];
  if (Array.isArray(rootList)) {
    rootList.forEach(widget => {
      addWidgetToList(widget, temp);
    });
  }

  return temp;
}

export function isChart(widgetType: number) {
  return (
    [
      SysCustomWidgetType.LineChart,
      SysCustomWidgetType.BarChart,
      SysCustomWidgetType.PieChart,
      SysCustomWidgetType.ScatterChart,
      SysCustomWidgetType.PivotTable,
      SysCustomWidgetType.DataViewTable,
      SysCustomWidgetType.Carousel,
      SysCustomWidgetType.RichText,
      SysCustomWidgetType.GaugeChart,
      SysCustomWidgetType.RadarChart,
      SysCustomWidgetType.FunnelChart,
      SysCustomWidgetType.ProgressBar,
      SysCustomWidgetType.ProgressCircle,
      SysCustomWidgetType.DataCard,
      SysCustomWidgetType.DataProgressCard,
      SysCustomWidgetType.CommonList,
    ].indexOf(widgetType) !== -1
  );
}
