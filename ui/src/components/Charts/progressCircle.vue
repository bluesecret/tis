<template>
  <el-row class="progress-circle" @click.stop="onTableClick" style="background: white">
    <el-col
      ref="title"
      class="title"
      v-if="title != null && title !== ''"
      :span="24"
      :style="getTitleStyle"
    >
      {{ title }}
    </el-col>
    <el-col class="progress-circle-container" :span="24">
      <div class="progress-circle-wrapper" @dblclick="onDblClick">
        <el-progress
          type="circle"
          :percentage="percentage"
          :stroke-linecap="options?.seriesSetting.strokeLinecap"
          :define-back-color="options?.seriesSetting.defineBackColor"
          :stroke-width="options?.seriesSetting.strokeWidth"
          :width="options?.seriesSetting.width"
          :show-text="false"
          :color="options?.seriesSetting.color"
        ></el-progress>
        <div class="progress-text" :style="getProgressTextStyle">
          <div class="text" v-html="getProgressText"></div>
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { getColumnValue } from './utils';

const props = defineProps<{
  data?: ANY_OBJECT | ANY_OBJECT[];
  options?: ANY_OBJECT;
}>();

const title = computed(() => {
  if (!props.options || !props.options.title || !props.options.title.show) {
    return undefined;
  }
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
const getProgressText = computed(() => {
  return props.options?.seriesSetting.textStyle.text.replace('{percent}', percentage.value);
});
const getProgressTextStyle = computed(() => {
  if (!props.options) {
    return {};
  }
  return {
    ...props.options.seriesSetting.textStyle,
    fontStyle: props.options.seriesSetting.textStyle.italics ? 'italic' : undefined,
    fontWeight: props.options.seriesSetting.textStyle.bold ? 'bold' : undefined,
    fontSize: props.options.seriesSetting.textStyle.fontSize + 'px',
  };
});
const dataObject = computed(() => {
  if (Array.isArray(props.data)) {
    return props.data[0];
  } else {
    return props.data;
  }
});

/*
 * 取出配置的数据字段
 * @params columnName 组件配置 datasetInfo 中的配置名称
 */
const getDataPropertyName = (columnName: string) => {
  if (
    props.options &&
    props.options.datasetInfo[columnName] &&
    Array.isArray(props.options.datasetInfo[columnName]) &&
    props.options.datasetInfo[columnName].length > 0
  ) {
    return props.options.datasetInfo[columnName][0].columnName;
  } else {
    return undefined;
  }
};
// 根据配置的字段取字符串数据
const getDataString = (
  columnName: string | ANY_OBJECT,
  defaultString: string,
  data: ANY_OBJECT | null,
) => {
  let propertyName;
  if (typeof columnName === 'string') {
    propertyName = getDataPropertyName(columnName);
  } else {
    propertyName = columnName.columnName;
  }
  if (!propertyName) return defaultString;
  let val = getColumnValue(data || dataObject.value, propertyName);

  return val || defaultString;
};
// 根据配置的字段去数字数据
const getDataNumber = (columnName: string, data: ANY_OBJECT | null = null) => {
  let val = getDataString(columnName, '0', data);

  if (typeof val === 'string') {
    return isNaN(Number(val)) ? 0 : Number(val);
  } else {
    return val;
  }
};

const percentage = computed(() => {
  if (!getDataPropertyName('progressColumn')) return 0;
  if (!props.options) {
    return 0;
  }
  return (
    Number((getDataNumber('progressColumn') / props.options.seriesSetting.maxValue).toFixed(2)) *
    100
  );
});

const emit = defineEmits<{
  click: [];
  'update:data': [ANY_OBJECT | null];
  change: [ANY_OBJECT | null];
  dblclick: [ANY_OBJECT];
}>();

const onTableClick = () => {
  emit('click');
};
const onDblClick = () => {
  emit('dblclick', dataObject.value);
};
</script>

<style lang="scss" scoped>
.progress-circle {
  display: flex;
  height: 100%;
  // flex-direction: column;
}
.progress-circle .title {
  flex-shrink: 0;
  height: 40px;
  padding: 0 2px;
  line-height: 30px;
  border-bottom: solid 1px #f6f6f6;
  flex-grow: 0;
}
.progress-circle-container {
  display: inline-flex;
  justify-content: center;
  height: 100%;
  padding-top: 10px;
  flex-direction: column;
  box-sizing: border-box;
}
.progress-circle-wrapper {
  position: relative;
  display: inline-block;
  width: 100%;
  text-align: center;
}

.progress-text {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 1000;
  display: inline-block;
  width: 100%;
  text-align: center;
  vertical-align: middle;
}
.progress-text::before {
  display: inline-block;
  width: 0;
  height: 100%;
  content: '';
  vertical-align: middle;
}
.progress-text .text {
  line-height: 20px;
  display: inline-block;
  vertical-align: middle;
}
</style>
