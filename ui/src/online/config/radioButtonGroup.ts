import { ANY_OBJECT } from '@/types/generic';
import {
  SysCustomWidgetType,
  SysOnlineFormType,
  OnlineFormEventType,
} from '@/common/staticDict/index';

const radioButtonGroup = {
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
  groupCount: {
    name: '分组数据个数统计',
    value: false,
    widgetType: SysCustomWidgetType.Switch,
  },
};

const radioButtonGroupConfig = {
  widgetType: SysCustomWidgetType.RadioButtonGroup,
  icon: 'online-icon icon-radio',
  attribute: radioButtonGroup,
  allowEventList: [OnlineFormEventType.CHANGE],
  supportBindTable: true,
  supportBindColumn: true,
};

export default radioButtonGroupConfig;
