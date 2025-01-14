<template>
  <div
    class="data-card-progress"
    @click.stop="onTableClick"
    @dblclick="onDblClick"
    style="background: white"
  >
    <el-image
      class="data-card-image"
      v-if="options?.seriesSetting.icon"
      :src="options.seriesSetting.icon"
      :style="getDataCardImageStyle"
    />
    <div class="data-card-title" :style="getMainTextStyle">
      {{ getDataString('mainTextColumn', '卡片标题') }}
    </div>
    <div class="data-card-number" :style="getNumTextStyle">
      {{ getDataString('numTextColumn') }}
    </div>
    <div class="data-card-footer">
      <el-progress
        type="line"
        style="flex-grow: 1"
        :percentage="percentage"
        :define-back-color="options?.seriesSetting.progress.defineBackColor"
        :stroke-width="options?.seriesSetting.progress.strokeWidth"
        :show-text="options?.seriesSetting.progress.showText"
        :text-inside="true"
        :text-color="options?.seriesSetting.progress.textStyle.color"
        :color="options?.seriesSetting.progress.color"
        :format="textFormat"
      />
      <div
        class="progress-text"
        v-if="!options?.seriesSetting.progress.textInside"
        :style="getProgressTextStyle"
      >
        <div class="text" v-html="outerTextFormat(percentage)"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ProgressFn } from 'element-plus';
import { ANY_OBJECT } from '@/types/generic';
import { getColumnValue } from './utils';

const props = withDefaults(
  defineProps<{
    data: ANY_OBJECT;
    options?: ANY_OBJECT;
  }>(),
  {},
);

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
  defaultString?: string,
  data?: ANY_OBJECT | null,
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

const dataObject = computed(() => {
  if (Array.isArray(props.data)) {
    return props.data[0];
  } else {
    return props.data;
  }
});
const getMainTextStyle = computed(() => {
  return getTextStyle('mainTextStyle');
});
const getNumTextStyle = computed(() => {
  return getTextStyle('numTextStyle');
});
const getProgressTextStyle = computed(() => {
  return {
    color: props.options?.seriesSetting.progress.textStyle.color,
    width: props.options?.seriesSetting.progress.textStyle.width + 'px',
    'font-size': props.options?.seriesSetting.progress.textStyle.fontSize + 'px',
    'font-weight': props.options?.seriesSetting.progress.textStyle.bold ? 600 : undefined,
    'font-style': props.options?.seriesSetting.progress.textStyle.italics ? 'italic' : undefined,
  };
});
const percentage = computed(() => {
  if (!props.options || !getDataPropertyName('progressColumn')) return 0;
  return (
    Number(
      (getDataNumber('progressColumn') / props.options.seriesSetting.progress.maxValue).toFixed(2),
    ) * 100
  );
});
const getDataCardImageStyle = computed(() => {
  return {
    height: props.options?.seriesSetting.iconSize + 'px',
    width: props.options?.seriesSetting.iconSize + 'px',
    borderRadius: props.options?.seriesSetting.iconRadius + 'px',
  };
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
const getTextStyle = (textStyleName: string) => {
  return {
    ...props.options?.seriesSetting[textStyleName],
    fontStyle: props.options?.seriesSetting[textStyleName].italics ? 'italic' : undefined,
    fontWeight: props.options?.seriesSetting[textStyleName].bold ? 'bold' : undefined,
    fontSize: props.options?.seriesSetting[textStyleName].fontSize + 'px',
  };
};
const textFormat: ProgressFn = (percentage: number) => {
  if (!props.options?.seriesSetting.progress.textInside) return '';

  return props.options.seriesSetting.progress.textStyle.text.replace('{percent}', percentage);
};
const outerTextFormat = (percentage: number) => {
  return props.options?.seriesSetting.progress.textStyle.text.replace(/\{percent\}/gi, percentage);
};

watch(
  () => props,
  () => {
    console.log('<><><> props', props, 'data', props.data);
  },
  {
    deep: true,
  },
);
</script>

<style scoped>
.data-card-progress {
  position: relative;
  display: flex;
  min-height: 110px;
  flex-direction: column;
  cursor: pointer;
}

.data-card-number {
  flex-shrink: 0;
  height: 40px;
  text-align: left;
  line-height: 2;
  flex-grow: 1;
}

.data-card-title {
  flex-grow: 0;
  flex-shrink: 0;
}

.data-card-footer {
  display: flex;
  justify-content: space-between;
  flex-shrink: 0;
  flex-grow: 0;
}

.data-card-image {
  position: absolute;
  top: 0;
  right: 0;
}

.progress-text {
  text-align: right;
}
</style>
