<template>
  <van-cell-group
    inset
    :style="{
      padding: (widget.props.padding ? widget.props.padding : 0) + 'px',
    }"
  >
    <OnlineCustomBlock
      v-model:value="childWidgetList"
      :isEdit="isEdit"
      @widgetClick="onWidgetClick"
    />
  </van-cell-group>
</template>

<script setup lang="ts">
import { CellGroup as VanCellGroup } from 'vant';
import OnlineCustomBlock from '@/online/components/OnlineCustomBlock.vue';
import { WidgetProps, WidgetEmit } from './types/widget';
import { useWidget } from './hooks/widget';

const emit = defineEmits<WidgetEmit>();
const props = withDefaults(defineProps<WidgetProps>(), { isEdit: false });

const { childWidgetList, onWidgetClick } = useWidget(props, emit);
</script>
