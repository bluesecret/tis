<template>
  <el-row class="rich-text" @click.stop="onTableClick" style="background: white">
    <el-col
      ref="title"
      class="title"
      v-if="title != null && title !== ''"
      :span="24"
      :style="getTitleStyle"
    >
      {{ title }}
    </el-col>
    <el-col class="table" :span="24" v-show="ready">
      <div :style="getTextStyle" v-html="richTextData" class="html-text"></div>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { getColumnValue } from './utils';

const props = defineProps<{
  data?: ANY_OBJECT | ANY_OBJECT[];
  height?: string;
  options?: ANY_OBJECT;
  widget?: ANY_OBJECT;
  textColumn?: ANY_OBJECT[];
}>();

const ready = ref(true);

const title = computed(() => {
  if (props.options == null || props.options.title == null || !props.options.title.show)
    return undefined;
  return props.options ? props.options.title.text : undefined;
});
const getTitleStyle = computed(() => {
  if (props.options == null || props.options.title == null) return undefined;
  return {
    color: props.options.title.textStyle.color,
    'font-size': props.options.title.textStyle.fontSize + 'px',
    'font-weight': props.options.title.bold ? 600 : undefined,
    'text-align': props.options.title.left,
    'font-style': props.options.title.italics ? 'italic' : undefined,
  };
});
const getTextStyle = computed(() => {
  if (props.options == null || props.options.basicInfo == null) return undefined;
  return {
    color: props.options.basicInfo.cellFontColor,
    'line-height': (props.options.basicInfo.cellRowHeight || 30) + 'px',
    'background-color': props.options.basicInfo.cellBackgroundColor + 'A0',
    'font-size': props.options.basicInfo.cellFontSize + 'px',
    'text-align': props.options.basicInfo.cellAlign,
  };
});

const getRichTextColumnValue = (
  data: ANY_OBJECT | ANY_OBJECT[] | undefined,
  columnList: ANY_OBJECT[] | undefined,
) => {
  if (columnList && columnList.length > 0) {
    return getColumnValue(data, columnList[0].columnName);
  }
  return undefined;
};
const richTextData = computed(() => {
  if (Array.isArray(props.data)) {
    return getRichTextColumnValue(props.data[0], props.textColumn);
  } else {
    return getRichTextColumnValue(props.data, props.textColumn);
  }
});

const emit = defineEmits<{ click: [] }>();
const onTableClick = () => {
  emit('click');
};
</script>

<style scoped>
.rich-text .title {
  flex-shrink: 0;
  padding: 10px 2px;
  flex-grow: 0;
}

.rich-text .html-text {
  overflow: hidden;
  padding: 5px;
  white-space: normal;
  word-break: break-all;
}
</style>
