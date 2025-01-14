<template>
  <el-row class="progress-bar" @click.stop="onTableClick" style="background: white">
    <el-col
      ref="title"
      class="title"
      v-if="title != null && title !== ''"
      :span="24"
      :style="getTitleStyle"
    >
      {{ title }}
    </el-col>
    <el-col
      v-if="data.dataList && data.dataList.length > 0"
      class="progress-bar-container"
      :span="24"
    >
      <div
        class="progress-container"
        v-for="(item, index) in data.dataList"
        :key="index"
        @dblclick="onDblClick(item)"
      >
        <el-progress
          type="line"
          :percentage="item.percentage"
          :define-back-color="options?.seriesSetting.defineBackColor"
          :stroke-width="options?.seriesSetting.strokeWidth"
          :show-text="options?.seriesSetting.showText"
          :text-inside="true"
          :text-color="options?.seriesSetting.textStyle.color"
          :color="options?.seriesSetting.color"
          :format="percentage => textFormat(percentage, item)"
        />
        <div
          class="progress-text"
          v-if="!options?.seriesSetting.textInside"
          :style="getProgressTextStyle"
        >
          <div class="text" v-html="outerTextFormat(percentage(item), item)"></div>
        </div>
      </div>
    </el-col>
    <el-col v-else class="progress-bar-container" :span="24">
      <div class="progress-container">
        <el-progress
          type="line"
          :percentage="10"
          :define-back-color="options?.seriesSetting.defineBackColor"
          :stroke-width="options?.seriesSetting.strokeWidth"
          :show-text="options?.seriesSetting.showText"
          :text-inside="true"
          :text-color="options?.seriesSetting.textStyle.color"
          :color="options?.seriesSetting.color"
          :format="() => ''"
        />
        <div class="progress-text" :style="getProgressTextStyle">
          <div class="text">10%</div>
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { getColumnValue } from './utils';

const props = defineProps<{
  data: ANY_OBJECT;
  options?: ANY_OBJECT;
}>();

const title = computed(() => {
  if (props.options == null || props.options.title == null || !props.options.title.show) {
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
const getProgressTextStyle = computed(() => {
  return {
    color: props.options?.seriesSetting.textStyle.color,
    width: props.options?.seriesSetting.textStyle.width + 'px',
    'font-size': props.options?.seriesSetting.textStyle.fontSize + 'px',
    'font-weight': props.options?.seriesSetting.textStyle.bold ? 600 : undefined,
    'font-style': props.options?.seriesSetting.textStyle.italics ? 'italic' : undefined,
  };
});
const watchPercentage = computed(() => {
  return [props.data.dataList, props.options?.seriesSetting.maxValue];
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
    props.options?.datasetInfo[columnName] &&
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
  const val = getColumnValue(data || dataObject.value, propertyName);

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

const emit = defineEmits<{
  click: [];
  'update:data': [ANY_OBJECT | null];
  change: [ANY_OBJECT | null];
  dblclick: [ANY_OBJECT];
}>();

const onTableClick = () => {
  emit('click');
};
const onDblClick = (clickData: ANY_OBJECT) => {
  emit('dblclick', clickData);
};
const textFormat = (percentage: number, item: ANY_OBJECT) => {
  if (!props.options?.seriesSetting.textInside) return '';

  let text = getDataString('textColumn', '', item);
  return props.options.seriesSetting.textStyle.text
    .replace(/\{percent\}/gi, percentage)
    .replace(/\{text\}/gi, text);
};
const outerTextFormat = (percentage: number, item: ANY_OBJECT) => {
  let text = getDataString('textColumn', '', item);
  return props.options?.seriesSetting.textStyle.text
    .replace(/:/gi, text ? ':' : '') // 说明字段未绑定或无文字不显示冒号
    .replace(/\{percent\}/gi, percentage)
    .replace(/\{text\}/gi, text);
};
const percentage = (item: ANY_OBJECT) => {
  try {
    if (!getDataPropertyName('progressColumn') || !props.options) return 0;
    let percentage = Number(
      (
        (getDataNumber('progressColumn', item) / props.options.seriesSetting.maxValue) *
        100
      ).toFixed(2),
    );
    return percentage > 100 ? 100 : percentage;
  } catch (e) {
    console.warn(e);
    return 0;
  }
};

watch(
  () => watchPercentage.value,
  () => {
    if (Array.isArray(props.data.dataList)) {
      const dataList = props.data.dataList;
      for (let i = 0; i < props.data.dataList.length; i++) {
        dataList[i].percentage = percentage(dataList[i]);
      }
    }
  },
  {
    deep: true,
    immediate: true,
  },
);
</script>

<style scoped lang="scss">
.progress-bar {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.progress-bar .title {
  flex-shrink: 0;
  height: 40px;
  padding: 0 2px;
  line-height: 30px;
  border-bottom: solid 1px #f6f6f6;
  flex-grow: 0;
}

.progress-bar .progress-bar-container {
  display: inline-flex;
  justify-content: center;
  overflow: hidden;
  height: 100%;
  padding: 20px 10px 0;
  flex-direction: column;
  box-sizing: border-box;
}

.progress-container {
  position: relative;
  display: inline-flex;
  width: 100%;
  text-align: center;
}

.progress-container + .progress-container {
  margin-top: 20px;
}

.progress-text {
  display: inline-block;
  flex-shrink: 0;
  text-align: right;
  flex-grow: 0;
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
.progress-bar :deep(.el-progress) {
  flex-grow: 1;
}
:deep(.el-progress-bar) {
  flex-grow: 1;
}
</style>
