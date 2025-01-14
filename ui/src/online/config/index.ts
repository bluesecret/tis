import blockConfig from './customBlock';
import baseCardConfig from './baseCard';
import tabsConfig from './tabs';
import tableContainerConfig from './tableContainer';
import textConfig from './text';
import imageConfig from './image';
import labelConfig from './label';
import inputConfig from './input';
import numberInputConfig from './numberInput';
import numberRangeConfig from './numberRange';
import switchConfig from './switch';
import radioConfig from './radio';
import checkboxConfig from './checkbox';
import selectConfig from './select';
import cascaderConfig from './cascader';
import dateConfig from './date';
import dateRangeConfig from './dateRange';
import userSelectConfig from './userSelect';
import deptSelectConfig from './deptSelect';
import dataSelectConfig from './dataSelect';
import uploadConfig from './upload';
import richEditorConfig from './richEditor';
import tableConfig from './table';
import linkConfig from './link';
import groupConfig from './cellGroup';
import mobileInputFilterConfig from './mobileInputFilter';
import mobileRadioFilterConfig from './mobileRadioFilter';
import mobileCheckboxFilterConfig from './mobileCheckboxFilter';
import mobileSwitchFilterConfig from './mobileSwitchFilter';
import mobileDateRangeFilterConfig from './mobileDateRangeFilter';
import mobileNumberRangeFilterConfig from './mobileNumberRangeFilter';
import rateConfig from './rate';
import stepperConfig from './stepper';
import calendarConfig from './calendar';
import baseListConfig from './baseList';
import imageCardConfig from './imageCard';
import treeConfig from './tree';
import queryListConfig from './queryList';
import { ANY_OBJECT } from '@/types/generic';
import { SysCustomWidgetType, SysCustomWidgetBindDataType } from '@/common/staticDict/index';
import { SysCustomWidgetBindValueType } from '@/common/staticDict/online';

const formWidgetGroupList: ANY_OBJECT = {
  pc: [
    {
      id: 'layout',
      groupName: '布局组件',
      widgetList: [
        blockConfig,
        baseCardConfig,
        tabsConfig,
        tableContainerConfig,
        textConfig,
        imageConfig,
      ],
    },
    {
      id: 'filter',
      groupName: '过滤组件',
      widgetList: [
        labelConfig,
        inputConfig,
        numberInputConfig,
        numberRangeConfig,
        switchConfig,
        radioConfig,
        checkboxConfig,
        selectConfig,
        cascaderConfig,
        dateConfig,
        dateRangeConfig,
        userSelectConfig,
        deptSelectConfig,
        dataSelectConfig,
      ],
    },
    {
      id: 'base',
      groupName: '基础组件',
      widgetList: [
        labelConfig,
        inputConfig,
        numberInputConfig,
        numberRangeConfig,
        switchConfig,
        radioConfig,
        checkboxConfig,
        selectConfig,
        cascaderConfig,
        dateConfig,
        dateRangeConfig,
        uploadConfig,
        richEditorConfig,
        tableConfig,
        linkConfig,
      ],
    },
    {
      id: 'advance',
      groupName: '高级组件',
      widgetList: [userSelectConfig, deptSelectConfig, dataSelectConfig],
    },
  ],
  mobile: [
    {
      id: 'layout',
      groupName: '布局组件',
      widgetList: [groupConfig, tabsConfig, textConfig, imageConfig],
    },
    {
      id: 'filter',
      groupName: '过滤组件',
      widgetList: [
        mobileInputFilterConfig,
        mobileRadioFilterConfig,
        //mobileCheckboxFilterConfig,
        mobileSwitchFilterConfig,
        mobileDateRangeFilterConfig,
        mobileNumberRangeFilterConfig,
      ],
    },
    {
      id: 'base',
      groupName: '基础组件',
      widgetList: [
        labelConfig,
        inputConfig,
        switchConfig,
        radioConfig,
        checkboxConfig,
        selectConfig,
        cascaderConfig,
        rateConfig,
        stepperConfig,
        calendarConfig,
        uploadConfig,
        baseListConfig,
      ],
    },
    {
      id: 'advance',
      groupName: '高级组件',
      widgetList: [userSelectConfig, deptSelectConfig, dataSelectConfig],
    },
  ],
};

function getDefaultVariableName(widgetType: number) {
  const tempTime = new Date().getTime();
  switch (widgetType) {
    case SysCustomWidgetType.Label:
      return 'label' + tempTime;
    case SysCustomWidgetType.Input:
      return 'input' + tempTime;
    case SysCustomWidgetType.NumberInput:
      return 'numberInput' + tempTime;
    case SysCustomWidgetType.NumberRange:
      return 'numberRange' + tempTime;
    case SysCustomWidgetType.Switch:
      return 'switch' + tempTime;
    case SysCustomWidgetType.Slider:
      return 'slider' + tempTime;
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
    case SysCustomWidgetType.Upload:
      return 'upload' + tempTime;
    case SysCustomWidgetType.RichEditor:
      return 'richEditor' + tempTime;
    case SysCustomWidgetType.Divider:
      return 'divider' + tempTime;
    case SysCustomWidgetType.Text:
      return 'text' + tempTime;
    case SysCustomWidgetType.Image:
      return 'image' + tempTime;
    case SysCustomWidgetType.ImageCard:
      return 'imageCard' + tempTime;
    case SysCustomWidgetType.Table:
      return 'table' + tempTime;
    case SysCustomWidgetType.PivotTable:
      return 'pivotTable' + tempTime;
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
    case SysCustomWidgetType.Link:
      return 'link' + tempTime;
    case SysCustomWidgetType.UserSelect:
      return 'userSelect' + tempTime;
    case SysCustomWidgetType.DeptSelect:
      return 'deptSelect' + tempTime;
    case SysCustomWidgetType.DataSelect:
      return 'dataSelect' + tempTime;
    case SysCustomWidgetType.Card:
      return 'baseCard' + tempTime;
    case SysCustomWidgetType.Tabs:
      return 'tabs' + tempTime;
    case SysCustomWidgetType.Tree:
      return 'tree' + tempTime;
    case SysCustomWidgetType.TableContainer:
      return 'tableContainer' + tempTime;
    case SysCustomWidgetType.List:
      return 'baseList' + tempTime;
    case SysCustomWidgetType.Rate:
      return 'rate' + tempTime;
    case SysCustomWidgetType.Stepper:
      return 'stepper' + tempTime;
    case SysCustomWidgetType.Calendar:
      return 'calendar' + tempTime;
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

function getWidgetAttribute(widgetType: number): ANY_OBJECT | null {
  switch (widgetType) {
    case SysCustomWidgetType.Label:
      return labelConfig;
    case SysCustomWidgetType.Text:
      return textConfig;
    case SysCustomWidgetType.Image:
      return imageConfig;
    case SysCustomWidgetType.ImageCard:
      return imageCardConfig;
    case SysCustomWidgetType.Input:
      return inputConfig;
    case SysCustomWidgetType.NumberInput:
      return numberInputConfig;
    case SysCustomWidgetType.NumberRange:
      return numberRangeConfig;
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
      return dateRangeConfig;
    case SysCustomWidgetType.Upload:
      return uploadConfig;
    case SysCustomWidgetType.RichEditor:
      return richEditorConfig;
    case SysCustomWidgetType.Table:
      return tableConfig;
    case SysCustomWidgetType.Block:
      return blockConfig;
    case SysCustomWidgetType.Link:
      return linkConfig;
    case SysCustomWidgetType.UserSelect:
      return userSelectConfig;
    case SysCustomWidgetType.DeptSelect:
      return deptSelectConfig;
    case SysCustomWidgetType.DataSelect:
      return dataSelectConfig;
    case SysCustomWidgetType.Card:
      return baseCardConfig;
    case SysCustomWidgetType.Tabs:
      return tabsConfig;
    case SysCustomWidgetType.Tree:
      return treeConfig;
    case SysCustomWidgetType.TableContainer:
      return tableContainerConfig;
    case SysCustomWidgetType.QueryList:
      return queryListConfig;
    case SysCustomWidgetType.List:
      return baseListConfig;
    case SysCustomWidgetType.Rate:
      return rateConfig;
    case SysCustomWidgetType.Stepper:
      return stepperConfig;
    case SysCustomWidgetType.Calendar:
      return calendarConfig;
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

function getWidgetObject(widget: ANY_OBJECT): ANY_OBJECT {
  const temp = {
    // ...widget,
    widgetType: widget.widgetType,
    bindData: {
      dataType: SysCustomWidgetBindDataType.Column,
      defaultValue: {
        valueType: SysCustomWidgetBindValueType.INPUT_DATA,
        // 默认值是字典，字典ID
        dictId: undefined,
        value: undefined,
        // 内置系统变量
        systemVariable: undefined,
      },
    },
    operationList: widget.operationList
      ? JSON.parse(JSON.stringify(widget.operationList))
      : undefined,
    showName: SysCustomWidgetType.getValue(widget.widgetType),
    variableName: getDefaultVariableName(widget.widgetType),
    props: Object.keys(widget.attribute).reduce((retObj: ANY_OBJECT, key) => {
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
    supportOperation: widget.supportOperation == null ? false : widget.supportOperation,
  };
  return temp;
}

function supportBindTable(widget: ANY_OBJECT) {
  const widgetInfo = getWidgetAttribute(widget.widgetType);
  return widgetInfo ? widgetInfo.supportBindTable : false;
}

function supportBindColumn(widget: ANY_OBJECT) {
  const widgetInfo = getWidgetAttribute(widget.widgetType);
  return widgetInfo ? widgetInfo.supportBindColumn : false;
}

export default {
  formWidgetGroupList,
  getWidgetObject,
  getWidgetAttribute,
  supportBindTable,
  supportBindColumn,
};
