import {
  SysCustomWidgetType,
  SysOnlineFormType,
  OnlineFormEventType,
} from '@/common/staticDict/index';
import { ANY_OBJECT } from '@/types/generic';

const rate = {
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
  count: {
    name: '图标总数',
    widgetType: SysCustomWidgetType.Slider,
    value: 5,
    min: 1,
    max: 10,
  },
  size: {
    name: '图标大小',
    widgetType: SysCustomWidgetType.Input,
    value: '20px',
  },
  gutter: {
    name: '图标间距',
    widgetType: SysCustomWidgetType.Input,
    value: '4px',
  },
  color: {
    name: '选中颜色',
    widgetType: SysCustomWidgetType.ColorPicker,
    value: '#FFD21E',
  },
  'void-color': {
    name: '未选中颜色',
    widgetType: SysCustomWidgetType.ColorPicker,
    value: '#c8c9cc',
  },
  'disabled-color': {
    name: '禁用颜色',
    widgetType: SysCustomWidgetType.ColorPicker,
    value: '#c8c9cc',
  },
  'allow-half': {
    name: '允许半选',
    value: false,
    widgetType: SysCustomWidgetType.Switch,
    dropdownList: [
      {
        id: true,
        name: '是',
      },
      {
        id: false,
        name: '否',
      },
    ],
  },
  required: {
    name: '是否必填',
    value: false,
    widgetType: SysCustomWidgetType.Switch,
    dropdownList: [
      {
        id: true,
        name: '必填',
      },
      {
        id: false,
        name: '非必填',
      },
    ],
  },
  disabled: {
    name: '是否禁用',
    value: false,
    visible: function (formConfig: ANY_OBJECT) {
      return formConfig && formConfig.form.formType !== SysOnlineFormType.QUERY;
    },
    widgetType: SysCustomWidgetType.Switch,
    dropdownList: [
      {
        id: false,
        name: '启用',
      },
      {
        id: true,
        name: '禁用',
      },
    ],
  },
};

const rateConfig = {
  widgetType: SysCustomWidgetType.Rate,
  icon: 'online-icon icon-input-number',
  attribute: rate,
  allowEventList: [
    OnlineFormEventType.CHANGE,
    OnlineFormEventType.DISABLE,
    OnlineFormEventType.VISIBLE,
  ],
  supportBindTable: true,
  supportBindColumn: true,
};

export default rateConfig;
