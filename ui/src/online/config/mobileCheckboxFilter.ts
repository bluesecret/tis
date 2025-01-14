import { SysCustomWidgetType, OnlineFormEventType } from '@/common/staticDict/index';

const checkBoxFilter = {
  dictInfo: {
    name: '下拉字典',
    value: {},
    customComponent: {
      component: 'CustomWidgetDictSetting',
    },
  },
};

const checkboxFilterConfig = {
  widgetType: SysCustomWidgetType.MobileCheckBoxFilter,
  icon: 'online-icon icon-checkbox',
  attribute: checkBoxFilter,
  allowEventList: [
    OnlineFormEventType.CHANGE,
    OnlineFormEventType.DISABLE,
    OnlineFormEventType.VISIBLE,
  ],
  supportBindTable: true,
  supportBindColumn: true,
};

export default checkboxFilterConfig;
