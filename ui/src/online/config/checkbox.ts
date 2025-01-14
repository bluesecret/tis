import {
  SysCustomWidgetType,
  SysOnlineFormType,
  OnlineFormEventType,
} from '@/common/staticDict/index';
import { ANY_OBJECT } from '@/types/generic';
import CustomWidgetDictSetting from '@/online/components/WidgetAttributeSetting/components/CustomWidgetDictSetting/index.vue';

const checkbox = {
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
  dictInfo: {
    name: '下拉字典',
    value: {},
    customComponent: {
      component: 'CustomWidgetDictSetting',
    },
  },
  direction: {
    name: '选项排列方式',
    widgetType: SysCustomWidgetType.Radio,
    value: 'vertical',
    visible: function (formConfig: ANY_OBJECT) {
      return formConfig && formConfig.activeMode === 'mobile';
    },
    dropdownList: [
      {
        id: 'horizontal',
        name: '横向排列',
      },
      {
        id: 'vertical',
        name: '纵向排列',
      },
    ],
  },
  'icon-size': {
    name: '图标大小',
    visible: function (formConfig: ANY_OBJECT) {
      return formConfig && formConfig.activeMode === 'mobile';
    },
    widgetType: SysCustomWidgetType.Input,
    value: '20px',
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

const checkboxConfig = {
  widgetType: SysCustomWidgetType.CheckBox,
  icon: 'online-icon icon-checkbox',
  attribute: checkbox,
  allowEventList: [
    OnlineFormEventType.CHANGE,
    OnlineFormEventType.DISABLE,
    OnlineFormEventType.VISIBLE,
    OnlineFormEventType.DROPDOWN_CHANGE,
  ],
  supportBindTable: true,
  supportBindColumn: true,
};

export default checkboxConfig;
