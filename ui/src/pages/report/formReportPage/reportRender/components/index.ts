import { SysCustomWidgetType } from '@/common/staticDict/index';
import { ReportBindDataType } from '@/common/staticDict/report';
import inputConfig from '@/online/config/input';
import numberInputConfig from '@/online/config/numberInput';
import switchConfig from '@/online/config/switch';
import radioConfig from '@/online/config/radio';
import checkboxConfig from '@/online/config/checkbox';
import selectConfig from '@/online/config/select';
import cascaderConfig from '@/online/config/cascader';
import dateConfig from '@/online/config/date';
import dateRengConfig from '@/online/config/dateRange';
import blockConfig from '@/online/config/customBlock';
import tabsConfig from '@/online/config/tabs';
import userSelectConfig from '@/online/config/userSelect';
import deptSelectConfig from '@/online/config/deptSelect';
import lineChartConfig from '@/online/config/lineChart';
import barChartConfig from '@/online/config/barChart';
import pieChartConfig from '@/online/config/pieChart';
import scatterChartConfig from '@/online/config/scatterChart';
import pivotTableConfig from '@/online/config/pivotTable';
import dataViewTableConfig from '@/online/config/dataViewTable';
import carouselConfig from '@/online/config/carousel';
import richTextConfig from '@/online/config/richText';
import funnelChartConfig from '@/online/config/funnelChart';
import radarChartConfig from '@/online/config/radarChart';
import progressBarConfig from '@/online/config/progressBar';
import progressCircleConfig from '@/online/config/progressCircle';
import dataCardConfig from '@/online/config/dataCard';
import dataProgressCardConfig from '@/online/config/dataProgressCard';
import commonListConfig from '@/online/config/commonList';
import textConfig from '@/online/config/text';
import imageConfig from '@/online/config/image';
import groupConfig from '@/online/config/cellGroup';
// 移动端过滤
import mobileRadioFilterConfig from '@/online/config/mobileRadioFilter';
import mobileCheckboxFilterConfig from '@/online/config/mobileCheckboxFilter';
import mobileInputFilterConfig from '@/online/config/mobileInputFilter';
import mobileSwitchFilterConfig from '@/online/config/mobileSwitchFilter';
import mobileDateRangeFilterConfig from '@/online/config/mobileDateRangeFilter';
import mobileNumberRangeFilterConfig from '@/online/config/mobileNumberRangeFilter';
import { ANY_OBJECT } from '@/types/generic';

const widgetGroupList: ANY_OBJECT[] = [
  {
    id: 'layout',
    groupName: '布局组件',
    widgetList: [blockConfig, tabsConfig, textConfig, imageConfig],
  },
  {
    id: 'filter',
    groupName: '过滤组件',
    widgetList: [
      inputConfig,
      numberInputConfig,
      switchConfig,
      radioConfig,
      checkboxConfig,
      selectConfig,
      cascaderConfig,
      dateConfig,
      dateRengConfig,
      userSelectConfig,
      deptSelectConfig,
    ],
  },
  {
    id: 'charts',
    groupName: '图表组件',
    widgetList: [
      lineChartConfig,
      barChartConfig,
      pieChartConfig,
      scatterChartConfig,
      pivotTableConfig,
      dataViewTableConfig,
      carouselConfig,
      richTextConfig,
      funnelChartConfig,
      radarChartConfig,
    ],
  },
  {
    id: 'advance',
    groupName: '高级组件',
    widgetList: [
      progressBarConfig,
      progressCircleConfig,
      dataCardConfig,
      dataProgressCardConfig,
      commonListConfig,
    ],
  },
];

const formWidgetGroupList: ANY_OBJECT = {
  pc: [
    {
      id: 'layout',
      groupName: '布局组件',
      widgetList: [blockConfig, tabsConfig, textConfig, imageConfig],
    },
    {
      id: 'filter',
      groupName: '过滤组件',
      widgetList: [
        inputConfig,
        numberInputConfig,
        switchConfig,
        radioConfig,
        checkboxConfig,
        selectConfig,
        cascaderConfig,
        dateConfig,
        dateRengConfig,
        userSelectConfig,
        deptSelectConfig,
      ],
    },
    {
      id: 'charts',
      groupName: '图表组件',
      widgetList: [
        lineChartConfig,
        barChartConfig,
        pieChartConfig,
        scatterChartConfig,
        pivotTableConfig,
        dataViewTableConfig,
        carouselConfig,
        richTextConfig,
        // gaugeChartConfig,
        funnelChartConfig,
        radarChartConfig,
      ],
    },
    {
      id: 'advance',
      groupName: '高级组件',
      widgetList: [
        progressBarConfig,
        progressCircleConfig,
        dataCardConfig,
        dataProgressCardConfig,
        commonListConfig,
      ],
    },
  ],
  mobile: [
    {
      id: 'layout',
      groupName: '布局组件',
      widgetList: [textConfig, imageConfig],
    },
    {
      id: 'filter',
      groupName: '过滤组件',
      widgetList: [
        mobileInputFilterConfig,
        mobileRadioFilterConfig,
        mobileSwitchFilterConfig,
        mobileDateRangeFilterConfig,
        mobileNumberRangeFilterConfig,
      ],
    },
    {
      id: 'charts',
      groupName: '图表组件',
      widgetList: [
        lineChartConfig,
        barChartConfig,
        pieChartConfig,
        scatterChartConfig,
        carouselConfig,
        richTextConfig,
        // gaugeChartConfig,
        funnelChartConfig,
        radarChartConfig,
      ],
    },
    {
      id: 'advance',
      groupName: '高级组件',
      widgetList: [progressBarConfig, progressCircleConfig, dataCardConfig, dataProgressCardConfig],
    },
  ],
};

function getDefaultVariableName(widgetType: number) {
  const tempTime = new Date().getTime();
  switch (widgetType) {
    case SysCustomWidgetType.Input:
      return 'input' + tempTime;
    case SysCustomWidgetType.NumberInput:
      return 'numberInput' + tempTime;
    case SysCustomWidgetType.Switch:
      return 'switch' + tempTime;
    case SysCustomWidgetType.Radio:
      return 'radio' + tempTime;
    case SysCustomWidgetType.CheckBox:
      return 'checkBox' + tempTime;
    case SysCustomWidgetType.Select:
      return 'select' + tempTime;
    case SysCustomWidgetType.Cascader:
      return 'cascader' + tempTime;
    case SysCustomWidgetType.Date:
      return 'date' + tempTime;
    case SysCustomWidgetType.DateRange:
      return 'dateRange' + tempTime;
    case SysCustomWidgetType.PivotTable:
      return 'pivotTable' + tempTime;
    case SysCustomWidgetType.DataViewTable:
      return 'dataViewTable' + tempTime;
    case SysCustomWidgetType.Carousel:
      return 'carousel' + tempTime;
    case SysCustomWidgetType.RichText:
      return 'richText' + tempTime;
    case SysCustomWidgetType.FunnelChart:
      return 'funnelChart' + tempTime;
    case SysCustomWidgetType.RadarChart:
      return 'radarChart' + tempTime;
    case SysCustomWidgetType.LineChart:
      return 'lineChart' + tempTime;
    case SysCustomWidgetType.BarChart:
      return 'barChart' + tempTime;
    case SysCustomWidgetType.PieChart:
      return 'pieChart' + tempTime;
    case SysCustomWidgetType.ScatterChart:
      return 'scatterChart' + tempTime;
    case SysCustomWidgetType.Block:
      return 'block' + tempTime;
    case SysCustomWidgetType.UserSelect:
      return 'userSelect' + tempTime;
    case SysCustomWidgetType.DeptSelect:
      return 'deptSelect' + tempTime;
    case SysCustomWidgetType.Tabs:
      return 'tabs' + tempTime;
    case SysCustomWidgetType.Text:
      return 'text' + tempTime;
    case SysCustomWidgetType.Image:
      return 'image' + tempTime;
    case SysCustomWidgetType.ProgressBar:
      return 'progressBar' + tempTime;
    case SysCustomWidgetType.ProgressCircle:
      return 'progressCircle' + tempTime;
    case SysCustomWidgetType.DataCard:
      return 'dataCard' + tempTime;
    case SysCustomWidgetType.DataProgressCard:
      return 'dataProgressCard' + tempTime;
    case SysCustomWidgetType.CommonList:
      return 'commonList' + tempTime;
    case SysCustomWidgetType.CellGroup:
      return 'group' + tempTime;
    case SysCustomWidgetType.MobileRadioFilter:
      return 'mbileRadioFilter' + tempTime;
    case SysCustomWidgetType.MobileCheckBoxFilter:
      return 'mobileCheckBoxFilter' + tempTime;
    case SysCustomWidgetType.MobileInputFilter:
      return 'mobileInputFilter' + tempTime;
    case SysCustomWidgetType.MobileSwitchFilter:
      return 'mobileSwitchFilter' + tempTime;
    case SysCustomWidgetType.MobileDateRangeFilter:
      return 'mobileDateRangeFilter' + tempTime;
    case SysCustomWidgetType.MobileNumberRangeFilter:
      return 'mobileNumberRangeFilter' + tempTime;
  }
}

function getWidgetAttribute(widgetType: number) {
  switch (widgetType) {
    case SysCustomWidgetType.Input:
      return inputConfig;
    case SysCustomWidgetType.NumberInput:
      return numberInputConfig;
    case SysCustomWidgetType.Switch:
      return switchConfig;
    case SysCustomWidgetType.Radio:
      return radioConfig;
    case SysCustomWidgetType.CheckBox:
      return checkboxConfig;
    case SysCustomWidgetType.Select:
      return selectConfig;
    case SysCustomWidgetType.Cascader:
      return cascaderConfig;
    case SysCustomWidgetType.Date:
      return dateConfig;
    case SysCustomWidgetType.DateRange:
      return dateRengConfig;
    case SysCustomWidgetType.Block:
      return blockConfig;
    case SysCustomWidgetType.UserSelect:
      return userSelectConfig;
    case SysCustomWidgetType.DeptSelect:
      return deptSelectConfig;
    case SysCustomWidgetType.Tabs:
      return tabsConfig;
    case SysCustomWidgetType.LineChart:
      return lineChartConfig;
    case SysCustomWidgetType.BarChart:
      return barChartConfig;
    case SysCustomWidgetType.PieChart:
      return pieChartConfig;
    case SysCustomWidgetType.ScatterChart:
      return scatterChartConfig;
    case SysCustomWidgetType.PivotTable:
      return pivotTableConfig;
    case SysCustomWidgetType.DataViewTable:
      return dataViewTableConfig;
    case SysCustomWidgetType.Carousel:
      return carouselConfig;
    case SysCustomWidgetType.RichText:
      return richTextConfig;
    case SysCustomWidgetType.FunnelChart:
      return funnelChartConfig;
    case SysCustomWidgetType.RadarChart:
      return radarChartConfig;
    case SysCustomWidgetType.ProgressBar:
      return progressBarConfig;
    case SysCustomWidgetType.ProgressCircle:
      return progressCircleConfig;
    case SysCustomWidgetType.DataCard:
      return dataCardConfig;
    case SysCustomWidgetType.DataProgressCard:
      return dataProgressCardConfig;
    case SysCustomWidgetType.CommonList:
      return commonListConfig;
    case SysCustomWidgetType.Text:
      return textConfig;
    case SysCustomWidgetType.Image:
      return imageConfig;
    case SysCustomWidgetType.CellGroup:
      return groupConfig;
    case SysCustomWidgetType.MobileRadioFilter:
      return mobileRadioFilterConfig;
    case SysCustomWidgetType.MobileCheckBoxFilter:
      return mobileCheckboxFilterConfig;
    case SysCustomWidgetType.MobileInputFilter:
      return mobileInputFilterConfig;
    case SysCustomWidgetType.MobileSwitchFilter:
      return mobileSwitchFilterConfig;
    case SysCustomWidgetType.MobileDateRangeFilter:
      return mobileDateRangeFilterConfig;
    case SysCustomWidgetType.MobileNumberRangeFilter:
      return mobileNumberRangeFilterConfig;
    default:
      return null;
  }
}

function getWidgetObject(widget: ANY_OBJECT) {
  const temp = {
    widgetType: widget.widgetType,
    bindData: {
      datasetId: undefined,
      systemVariableType: undefined,
      dataType: ReportBindDataType.CUSTOM,
    },
    operationList: widget.operationList ? JSON.parse(JSON.stringify(widget.operationList)) : [],
    showName: SysCustomWidgetType.getValue(widget.widgetType),
    variableName: getDefaultVariableName(widget.widgetType),
    props: Object.keys(widget.attribute).reduce((retObj: ANY_OBJECT, key: string) => {
      let tempValue;
      if (typeof widget.attribute[key].value === 'function') {
        tempValue = widget.attribute[key].value();
      } else {
        tempValue = widget.attribute[key].value;
      }
      if (Array.isArray(tempValue) || tempValue instanceof Object) {
        retObj[key] = JSON.parse(JSON.stringify(tempValue));
      } else {
        retObj[key] = tempValue;
      }
      return retObj;
    }, {}),
    eventList: [],
    childWidgetList: [],
    style: {},
  };
  return temp;
}
/**
 * 是否支持绑定数据集（只有图表组件支持绑定）
 * @param {*} widget 组件对象
 * @returns 是否支持绑定数据集
 */
function supportBindDataset(widget: ANY_OBJECT) {
  for (let i = 0; i < widgetGroupList[1].widgetList.length; i++) {
    const temp = widgetGroupList[1].widgetList[i];
    if (temp.widgetType === widget.widgetType) return true;
  }
  return false;
}

export default {
  widgetGroupList,
  formWidgetGroupList,
  getDefaultVariableName,
  getWidgetObject,
  getWidgetAttribute,
  supportBindDataset,
};
