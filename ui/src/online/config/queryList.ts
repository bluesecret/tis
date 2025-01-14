import {
  SysCustomWidgetType,
  OnlineFormEventType,
  SysCustomWidgetOperationType,
  SysOnlineFormType,
} from '@/common/staticDict/index';
import { ANY_OBJECT } from '@/types/generic';

const queryList = {
  span: {
    name: '组件宽度',
    widgetType: SysCustomWidgetType.Slider,
    value: 24,
    min: 1,
    max: 24,
  },
  height: {
    name: '组件高度',
    widgetType: SysCustomWidgetType.NumberInput,
    value: undefined,
    visible: function (formConfig: ANY_OBJECT) {
      return formConfig && formConfig.form.formType !== SysOnlineFormType.QUERY;
    },
    min: 100,
  },
  paddingBottom: {
    name: '底部距离',
    widgetType: SysCustomWidgetType.NumberInput,
    value: 0,
    visible: function (formConfig: ANY_OBJECT) {
      return formConfig && formConfig.form.formType !== SysOnlineFormType.QUERY;
    },
    min: 0,
  },
  card: {
    name: '显示组件',
    widgetType: SysCustomWidgetType.Select,
    value: SysCustomWidgetType.ImageCard,
    dropdownList: [
      {
        id: SysCustomWidgetType.ImageCard,
        name: SysCustomWidgetType.getValue(SysCustomWidgetType.ImageCard),
      },
    ],
    props: {
      clearable: false,
    },
  },
  orderList: {
    name: '排序字段',
    value: [],
    customComponent: {
      component: 'OnlineCustomListOrderSetting',
    },
  },
};

const queryListConfig = {
  widgetType: SysCustomWidgetType.QueryList,
  icon: 'online-icon icon-card',
  attribute: queryList,
  allowEventList: [OnlineFormEventType.VISIBLE],
  operationList: [
    {
      id: 1,
      type: SysCustomWidgetOperationType.BATCH_DELETE,
      name: SysCustomWidgetOperationType.getValue(SysCustomWidgetOperationType.BATCH_DELETE),
      enabled: false,
      builtin: true,
      rowOperation: false,
      btnType: 'danger',
      plain: true,
      formId: undefined,
      readOnly: false,
      showOrder: 0,
      eventList: [],
    },
    {
      id: 2,
      type: SysCustomWidgetOperationType.ADD,
      name: SysCustomWidgetOperationType.getValue(SysCustomWidgetOperationType.ADD),
      enabled: false,
      builtin: true,
      rowOperation: false,
      btnType: 'primary',
      plain: false,
      formId: undefined,
      readOnly: false,
      showOrder: 1,
      eventList: [],
    },
    {
      id: 3,
      type: SysCustomWidgetOperationType.EDIT,
      name: SysCustomWidgetOperationType.getValue(SysCustomWidgetOperationType.EDIT),
      enabled: false,
      builtin: true,
      rowOperation: true,
      btnClass: 'table-btn success',
      formId: undefined,
      readOnly: false,
      showOrder: 10,
      eventList: [],
    },
  ],
  supportOperate: true,
  supportBindTable: true,
  supportBindColumn: false,
  supportOperation: true,
};

export default queryListConfig;
