import { SysCustomWidgetType, OnlineFormEventType } from '@/common/staticDict/index';

const switchFilter = {
  'active-color': {
    name: '打开背景色',
    widgetType: SysCustomWidgetType.ColorPicker,
    value: '#1989fa',
  },
  'inactive-color': {
    name: '关闭背景色',
    widgetType: SysCustomWidgetType.ColorPicker,
    value: '#E8E8E8',
  },
};

const switchFilterConfig = {
  widgetType: SysCustomWidgetType.MobileSwitchFilter,
  icon: 'online-icon icon-switch',
  attribute: switchFilter,
  allowEventList: [
    OnlineFormEventType.CHANGE,
    OnlineFormEventType.DISABLE,
    OnlineFormEventType.VISIBLE,
  ],
  supportBindTable: true,
  supportBindColumn: true,
};

export default switchFilterConfig;
