<template>
  <div class="online-custom-list" style="padding: 2px 4px">
    <el-button
      v-if="operationVisible(SysCustomWidgetOperationType.ADD)"
      class="add-btn"
      size="default"
      type="success"
      :plain="true"
      :icon="Plus"
      >新增</el-button
    >
    <template v-if="isEdit">
      <component
        v-if="cardWidget"
        class="widget-item"
        :is="getComponent"
        style="background: #f6f7f9; border-radius: 8px"
        :isEdit="isEdit"
        :widget="cardWidget"
        :supportEdit="
          operationVisible(SysCustomWidgetOperationType.EDIT) ||
          operationVisible(SysCustomWidgetOperationType.DELETE)
        "
        :supportUpdate="operationVisible(SysCustomWidgetOperationType.EDIT)"
        :supportDelete="operationVisible(SysCustomWidgetOperationType.DELETE)"
        @widgetClick="onWidgetClick"
      />
      <el-empty v-else description="请选择列表使用的卡片" />
    </template>
  </div>
</template>

<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue';
import { findItemFromList } from '@/common/utils';
import imageCardConfig from '@/online/config/imageCard';
import { ANY_OBJECT } from '@/types/generic';
import { SysCustomWidgetOperationType, SysCustomWidgetType } from '@/common/staticDict';
import { WidgetProps, WidgetEmit } from '../types/widget';
import OnlineCustomImageCard from '../OnlineCustomImageCard.vue';

interface IProps extends WidgetProps {
  dataList?: ANY_OBJECT[];
  height?: string | number;
}

const form = inject('form', () => {
  console.error('OnlineCustomList: form not injected');
  return { isEdit: false } as ANY_OBJECT;
});

const emit = defineEmits<WidgetEmit>();

const props = withDefaults(defineProps<IProps>(), {
  dataList: () => [],
  height: 'auto',
  isEdit: false,
});

provide('parentWidget', props.widget);

const cardWidget = computed(() => {
  return Array.isArray(props.widget.childWidgetList) && props.widget.childWidgetList.length > 0
    ? props.widget.childWidgetList[0]
    : undefined;
});
const getComponent = computed(() => {
  switch (props.widget.props.card) {
    case SysCustomWidgetType.ImageCard:
      return OnlineCustomImageCard;
    default:
      return '';
  }
});

const onWidgetClick = (widget: ANY_OBJECT) => {
  emit('widgetClick', widget);
};
const getCardWidget = () => {
  let config;
  switch (props.widget.props.card) {
    case SysCustomWidgetType.ImageCard:
      config = imageCardConfig;
      break;
  }
  let temp;
  if (config) {
    temp = form().getWidgetObject(config);
    temp.showName = 'listCard';
    temp.variableName = 'listCard' + new Date().getTime();
    temp.relation = undefined;
    temp.datasource = undefined;
    temp.column = undefined;
  }
  return temp;
};
const hasOperator = (type: number) => {
  let temp = getOperation(type);
  return temp && temp.enabled;
};
const getOperation = (type: number) => {
  return findItemFromList(props.widget.operationList, type, 'type');
};
const operationVisible = (type: number) => {
  let operation = getOperation(type);
  return !form().readOnly && hasOperator(type) && form().checkOperationVisible(operation);
};
// const operationDisabled = (type: number) => {
//   let operation = getOperation(type);
//   return form().checkOperationDisabled(operation) || !form().checkOperationPermCode(operation);
// };

watch(
  () => props.widget.props?.card,
  () => {
    let cardWidget = getCardWidget();
    let widget = props.widget;
    widget.childWidgetList = [cardWidget];
  },
);

watch(
  () => props.widget.bindData.tableId,
  () => {
    let cardWidget = getCardWidget();
    let widget = props.widget;
    widget.childWidgetList = [cardWidget];
  },
);

onMounted(() => {
  if (!Array.isArray(props.widget.childWidgetList) || props.widget.childWidgetList.length === 0) {
    let cardWidget = getCardWidget();
    if (cardWidget) {
      let widget = props.widget;
      widget.childWidgetList = [cardWidget];
    }
  }
});
</script>

<style scoped>
.add-btn {
  width: 100%;
  margin-bottom: 14px;
  border-style: dashed;
}
</style>
