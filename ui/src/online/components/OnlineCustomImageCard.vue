<template>
  <div>
    <div class="online-custom-image-card" @click.stop="onWidgetClick(widget)">
      <OnlineCustomImage
        class="image widget-item"
        style="margin-right: 10px"
        :style="{ 'align-self': widget.props?.imageAlign }"
        v-if="widget.props && widget.props.imagePosition === 'left' && imageWidget"
        :class="{ active: isEdit && form().isActive(imageWidget) }"
        :value="getWidgetValue(imageWidget)"
        :src="imageWidget.props.src"
        :fit="imageWidget.props.fit"
        :width="imageWidget.props.width"
        :height="imageWidget.props.height"
        :round="imageWidget.props.round"
        :radius="imageWidget.props.radius"
        :widget="imageWidget"
        @click.stop="onWidgetClick(imageWidget)"
      />
      <div class="content">
        <slot name="content" />
        <OnlineCustomBlock
          v-if="supportContent"
          :value="widgetList"
          @change="onWidgetListChange"
          :isEdit="isEdit"
          @widgetClick="onWidgetClick"
        />
      </div>
      <OnlineCustomImage
        class="image"
        style="margin-left: 10px"
        :style="{ 'align-self': widget.props?.imageAlign }"
        v-if="widget.props && widget.props.imagePosition === 'right' && imageWidget"
        :class="{ active: isEdit && form().isActive(imageWidget) }"
        :value="getWidgetValue(imageWidget)"
        :src="imageWidget.props.src"
        :fit="imageWidget.props.fit"
        :width="imageWidget.props.width"
        :height="imageWidget.props.height"
        :round="imageWidget.props.round"
        :radius="imageWidget.props.radius"
        :widget="imageWidget"
        @click.stop="onWidgetClick(imageWidget)"
      />
    </div>
    <slot name="menu" />
    <el-row
      v-if="supportEdit"
      justify="end"
      style="padding-top: 10px; margin: 0 15px 10px; border-top: 1px solid #e0e2e3"
    >
      <el-icon style="font-size: 14px; color: #dc4c33"><Delete /></el-icon>
      <el-icon style="margin-left: 10px; font-size: 14px; color: #ee8133"><Edit /></el-icon>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { Delete, Edit } from '@element-plus/icons-vue';
import { ANY_OBJECT } from '@/types/generic';
import OnlineCustomBlock from '@/online/components/OnlineCustomBlock.vue';
import imageConfig from '@/online/config/image';
import { WidgetProps, WidgetEmit } from './types/widget';
import { useWidget } from './hooks/widget';
import OnlineCustomImage from './OnlineCustomImage.vue';

const emit = defineEmits<WidgetEmit>();

interface IProps extends WidgetProps {
  widget?: ANY_OBJECT;
  supportEdit?: boolean;
  supportDelete?: boolean;
  supportUpdate?: boolean;
  supportContent?: boolean;
}
const props = withDefaults(defineProps<IProps>(), {
  isEdit: false,
  supportEdit: false,
  supportDelete: false,
  supportUpdate: false,
  supportContent: true,
});

const form = inject('form', () => {
  console.warn('form not injected');
  return { isEdit: false } as ANY_OBJECT;
});

const { childWidgetList, onWidgetClick } = useWidget(props, emit);
const widgetList = ref<ANY_OBJECT[]>([]);

const imageWidget = computed(() => {
  if (
    props.widget &&
    Array.isArray(props.widget.childWidgetList) &&
    props.widget.childWidgetList.length > 0
  ) {
    return props.widget.childWidgetList[0];
  }
  return null;
});

const onWidgetListChange = val => {
  childWidgetList.value = [imageWidget.value, ...val];
};

const getWidgetValue = (widget: ANY_OBJECT) => {
  return form().getWidgetValue(widget);
};

watch(
  () => props.widget.childWidgetList,
  (newValue: ANY_OBJECT) => {
    if (!Array.isArray(props.widget.childWidgetList) || props.widget.childWidgetList.length === 0) {
      let temp = form().getWidgetObject(imageConfig);
      temp['relation'] = undefined;
      temp['datasource'] = undefined;
      temp['column'] = undefined;
      childWidgetList.value = [temp];
    }
    widgetList.value = props.widget.childWidgetList.slice(1);
  },
  {
    immediate: true,
  },
);
</script>

<style scoped>
.online-custom-image-card {
  display: flex;
  padding: 10px 15px;
  border-radius: 5px;
}
.online-custom-image-card .image {
  flex-grow: 0;
  flex-shrink: 0;
}
.online-custom-image-card .content {
  flex-grow: 1;
  flex-shrink: 1;
}
</style>
