import {
  SysCustomWidgetType,
  SysOnlineFormType,
  OnlineFormEventType,
} from '@/common/staticDict/index';
import { ANY_OBJECT } from '@/types/generic';

const label = {
  span: {
    name: '组件宽度',
    widgetType: SysCustomWidgetType.Slider,
    value: 12,
    visible: function (formConfig: ANY_OBJECT) {
      return formConfig && formConfig.form.formType !== SysOnlineFormType.QUERY;
    },
    min: 1,
    max: 24,
  },
  dateType: {
    name: '显示格式',
    widgetType: SysCustomWidgetType.Select,
    value: 'YYYY-MM-DD',
    visible: function (formConfig: ANY_OBJECT) {
      if (
        formConfig &&
        formConfig.currentWidget &&
        formConfig.currentWidget.column &&
        formConfig.currentWidget.column.objectFieldType === 'Date'
      ) {
        return true;
      } else {
        return false;
      }
    },
    dropdownList: [
      { id: 'YYYY-MM-DD', name: '日' },
      { id: 'YYYY-MM', name: '月' },
      { id: 'YYYY', name: '年' },
      { id: 'YYYY-MM-DD HH:mm:ss', name: '时间' },
    ],
  },
};

const labelConfig = {
  widgetType: SysCustomWidgetType.Label,
  icon: 'online-icon icon-text',
  attribute: label,
  allowEventList: [OnlineFormEventType.VISIBLE],
  supportBindTable: true,
  supportBindColumn: true,
};

export default labelConfig;
