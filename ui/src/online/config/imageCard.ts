import { SysCustomWidgetType } from '@/common/staticDict/index';

const imageCard = {
  parentWidget: {
    name: '父组件',
    value: undefined,
    widgetType: SysCustomWidgetType.Input,
    visible: false,
  },
  imagePosition: {
    name: '图片水平位置',
    value: 'left',
    widgetType: SysCustomWidgetType.Select,
    dropdownList: [
      {
        id: 'none',
        name: '不显示',
      },
      {
        id: 'left',
        name: '左侧',
      },
      {
        id: 'right',
        name: '右侧',
      },
    ],
  },
  imageAlign: {
    name: '图片垂直排列方式',
    value: 'center',
    widgetType: SysCustomWidgetType.Select,
    dropdownList: [
      {
        id: 'flex-start',
        name: '顶部',
      },
      {
        id: 'center',
        name: '居中',
      },
      {
        id: 'flex-end',
        name: '底部',
      },
    ],
  },
};

const imageCardConfig = {
  widgetType: SysCustomWidgetType.ImageCard,
  icon: 'online-icon icon-image',
  attribute: imageCard,
  allowEventList: [],
  supportBindTable: false,
  supportBindColumn: false,
};

export default imageCardConfig;
