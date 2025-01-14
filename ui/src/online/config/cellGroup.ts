import { SysCustomWidgetType, OnlineFormEventType } from '@/common/staticDict/index';

const group = {
  span: {
    name: '组件宽度',
    widgetType: SysCustomWidgetType.Slider,
    value: 12,
    min: 1,
    max: 24,
  },
  paddingTop: {
    name: '顶部距离',
    widgetType: SysCustomWidgetType.NumberInput,
    value: 0,
    min: 0,
  },
  paddingBottom: {
    name: '底部距离',
    widgetType: SysCustomWidgetType.NumberInput,
    value: 20,
    min: 0,
  },
  padding: {
    name: '内部边距',
    widgetType: SysCustomWidgetType.NumberInput,
    value: 0,
    min: 0,
  },
};

const groupConfig = {
  widgetType: SysCustomWidgetType.CellGroup,
  icon: 'online-icon icon-block',
  attribute: group,
  allowEventList: [OnlineFormEventType.VISIBLE],
  supportBindTable: false,
  supportBindColumn: false,
};

export default groupConfig;
