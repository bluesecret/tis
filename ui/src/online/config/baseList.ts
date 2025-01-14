import {
  SysCustomWidgetType,
  OnlineFormEventType,
  SysCustomWidgetOperationType,
} from '@/common/staticDict/index';

const baseList = {
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
};

const baseListConfig = {
  widgetType: SysCustomWidgetType.List,
  icon: 'online-icon icon-card',
  attribute: baseList,
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
    {
      id: 4,
      type: SysCustomWidgetOperationType.DELETE,
      name: SysCustomWidgetOperationType.getValue(SysCustomWidgetOperationType.DELETE),
      enabled: false,
      builtin: true,
      rowOperation: true,
      btnClass: 'table-btn delete',
      formId: undefined,
      readOnly: false,
      showOrder: 15,
      eventList: [],
    },
  ],
  supportOperate: true,
  supportBindTable: true,
  supportBindColumn: false,
  supportOperation: true,
};

export default baseListConfig;
