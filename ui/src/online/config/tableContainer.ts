import {
  SysCustomWidgetType,
  SysOnlineFormType,
  OnlineFormEventType,
} from '@/common/staticDict/index';
import { ANY_OBJECT } from '@/types/generic';

const tableContainer = {
  span: {
    name: '组件宽度',
    widgetType: SysCustomWidgetType.Slider,
    value: 24,
    visible: function (formConfig: ANY_OBJECT) {
      return formConfig && formConfig.form.formType !== SysOnlineFormType.QUERY;
    },
    min: 1,
    max: 24,
  },
  backgroundColor: {
    name: '背景色',
    widgetType: SysCustomWidgetType.ColorPicker,
    value: '#FFFFFF',
  },
  borderColor: {
    name: '边框颜色',
    widgetType: SysCustomWidgetType.ColorPicker,
    value: '#eaedf4',
  },
  paddingBottom: {
    name: '底部距离',
    widgetType: SysCustomWidgetType.NumberInput,
    value: 20,
    min: 0,
  },
  table: {
    name: '表格',
    showLabel: false,
    value: {
      render: {},
    },
    customComponent: {
      component: 'CustomTableContainerSetting',
    },
  },
  actions: {
    name: '表格操作',
    value: {},
    visible: false,
  },
};

const tableContainerConfig = {
  widgetType: SysCustomWidgetType.TableContainer,
  icon: 'online-icon icon-table-container',
  attribute: tableContainer,
  allowEventList: [
    OnlineFormEventType.CHANGE,
    OnlineFormEventType.DISABLE,
    OnlineFormEventType.VISIBLE,
  ],
  supportBindTable: false,
  supportBindColumn: false,
};

export default tableContainerConfig;
