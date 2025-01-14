import {
  SysCustomWidgetType,
  SysOnlineFormType,
  OnlineFormEventType,
} from '@/common/staticDict/index';
import { ANY_OBJECT } from '@/types/generic';

const stepper = {
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
  min: {
    name: '最小值',
    widgetType: SysCustomWidgetType.NumberInput,
    value: undefined,
  },
  max: {
    name: '最大值',
    widgetType: SysCustomWidgetType.NumberInput,
    value: undefined,
  },
  step: {
    name: '步长',
    widgetType: SysCustomWidgetType.NumberInput,
    value: 1,
  },
  'input-width': {
    name: '输入框宽度',
    widgetType: SysCustomWidgetType.Input,
    value: '32px',
  },
  'button-size': {
    name: '按钮大小',
    widgetType: SysCustomWidgetType.Input,
    value: '28px',
  },
  integer: {
    name: '是否只允许整数',
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

const stepperConfig = {
  widgetType: SysCustomWidgetType.Stepper,
  icon: 'online-icon icon-input-number',
  attribute: stepper,
  allowEventList: [
    OnlineFormEventType.CHANGE,
    OnlineFormEventType.DISABLE,
    OnlineFormEventType.VISIBLE,
  ],
  supportBindTable: true,
  supportBindColumn: true,
};

export default stepperConfig;
