import { ElMessageBox } from 'element-plus';
import { ANY_OBJECT } from '@/types/generic';
import { WidgetProps, WidgetEmit } from '../types/widget';

export const useWidget = (props: WidgetProps, emit: WidgetEmit) => {
  const propsWidget = computed<ANY_OBJECT>(() => {
    return props.widget;
  });

  const childWidgetList = computed<ANY_OBJECT[]>({
    get() {
      return props.widget.childWidgetList || [];
    },
    set(values: ANY_OBJECT[]) {
      if (propsWidget.value) {
        propsWidget.value.childWidgetList = [...values];
      }
    },
  });

  const onWidgetClick = (widget: ANY_OBJECT | null = null) => {
    emit('widgetClick', widget);
  };

  const onDeleteWidget = (widget: ANY_OBJECT) => {
    ElMessageBox.confirm('是否删除此组件？', '', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(() => {
        propsWidget.value.childWidgetList = propsWidget.value.childWidgetList.filter(
          (item: ANY_OBJECT) => item !== widget,
        );
        onWidgetClick(null);
      })
      .catch(e => {
        console.warn(e);
      });
  };

  const onCopyWidget = (widget: ANY_OBJECT) => {
    const childWidgetList = props.widget.childWidgetList;
    childWidgetList.push(widget);
  };

  return { propsWidget, childWidgetList, onWidgetClick, onDeleteWidget, onCopyWidget };
};
