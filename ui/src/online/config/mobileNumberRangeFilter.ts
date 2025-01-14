import { SysCustomWidgetType, OnlineFormEventType } from '@/common/staticDict/index';

const numberRangeFilter = {
  'start-placeholder': {
    name: '最小值提示',
    widgetType: SysCustomWidgetType.Input,
    value: '',
  },
  'end-placeholder': {
    name: '最大值提示',
    widgetType: SysCustomWidgetType.Input,
    value: '',
  },
};

const numberRangeFilterConfig = {
  widgetType: SysCustomWidgetType.MobileNumberRangeFilter,
  icon: 'online-icon icon-number-range',
  attribute: numberRangeFilter,
  allowEventList: [
    OnlineFormEventType.CHANGE,
    OnlineFormEventType.DISABLE,
    OnlineFormEventType.VISIBLE,
  ],
  supportBindTable: true,
  supportBindColumn: true,
};

export default numberRangeFilterConfig;
