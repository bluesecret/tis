import { SysCustomWidgetType, OnlineFormEventType } from '@/common/staticDict/index';

const dateRangeFilter = {
  'confirm-text': {
    name: '确认按钮文字',
    widgetType: SysCustomWidgetType.Input,
    value: '确定',
  },
  color: {
    name: '主题色',
    widgetType: SysCustomWidgetType.ColorPicker,
    value: '#ee0a24',
  },
};

const dateRangeFilterConfig = {
  widgetType: SysCustomWidgetType.MobileDateRangeFilter,
  icon: 'online-icon icon-date',
  attribute: dateRangeFilter,
  allowEventList: [
    OnlineFormEventType.CHANGE,
    OnlineFormEventType.DISABLE,
    OnlineFormEventType.VISIBLE,
  ],
  supportBindTable: true,
  supportBindColumn: true,
};

export default dateRangeFilterConfig;
