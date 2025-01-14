<template>
  <div
    class="data-card"
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
      {{ getDataString('mainTextColumn', '卡片标题', dataObject) }}
    </div>
    <div class="data-card-number" :style="getNumTextStyle">
      {{ getDataString('numTextColumn', '', dataObject) }}
    </div>
    <div class="data-card-footer" v-if="isShowFootText || isShowFootNum">
      <span v-if="isShowFootText" :style="getFootTextStyle">
        {{ getDataString('footTextColumn', '', dataObject) }}
      </span>
      <span v-if="isShowFootNum" :style="getFootNumTextStyle">
        <el-icon v-if="getFooterNumIcon !== ''"><component :is="getFooterNumIcon" /></el-icon>
        {{ getShowFooterNumText }}
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { TopRight, BottomLeft } from '@element-plus/icons-vue';
import { ANY_OBJECT } from '@/types/generic';
import { getColumnValue } from './utils';

const props = withDefaults(
  defineProps<{
    // 透视表数据
    data?: ANY_OBJECT;
    // 透视表设置
    options?: ANY_OBJECT;
  }>(),
  {},
);

const emit = defineEmits<{
  click: [];
  dblclick: [ANY_OBJECT];
}>();

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

const onTableClick = () => {
  emit('click');
};
const onDblClick = () => {
  emit('dblclick', dataObject.value);
};

const dataObject = computed(() => {
  if (Array.isArray(props.data)) {
    return props.data[0];
  } else {
    return props.data;
  }
});
const getTextStyle = (textStyleName: string) => {
  return {
    ...props.options?.seriesSetting[textStyleName],
    fontStyle: props.options?.seriesSetting[textStyleName].italics ? 'italic' : undefined,
    fontWeight: props.options?.seriesSetting[textStyleName].bold ? 'bold' : undefined,
    fontSize: props.options?.seriesSetting[textStyleName].fontSize + 'px',
  };
};

// const getTitleStyle = computed(() => {
//   if (props.options == null || props.options?.title == null) return undefined;
//   return {
//     color: props.options?.title.textStyle.color,
//     'font-size': props.options?.title.textStyle.fontSize + 'px',
//     'font-weight': props.options?.title.bold ? 600 : undefined,
//     'text-align': props.options?.title.left,
//     'font-style': props.options?.title.italics ? 'italic' : undefined,
//   };
// });
const isShowFootText = computed(() => {
  return (
    props.options?.datasetInfo.footTextColumn &&
    props.options?.datasetInfo.footTextColumn.length > 0
  );
});
const isShowFootNum = computed(() => {
  return (
    props.options?.datasetInfo.footNumTextColumn &&
    props.options?.datasetInfo.footNumTextColumn.length > 0
  );
});
const getMainTextStyle = computed(() => {
  return getTextStyle('mainTextStyle');
});
const getNumTextStyle = computed(() => {
  return getTextStyle('numTextStyle');
});
const getFootTextStyle = computed(() => {
  return getTextStyle('footTextStyle');
});
const getFootNumTextStyle = computed(() => {
  return {
    ...getTextStyle('footNumTextStyle'),
    color: getFooterNumIcon.value === TopRight ? '#00A241' : '#EF502F',
  };
});
const getFooterNumIcon = computed(() => {
  if (!isNaN(getFooterNumText.value)) {
    // 如果是数字
    if (getFooterNumText.value >= 0) {
      return TopRight;
    } else {
      return BottomLeft;
    }
  } else {
    return '';
  }
});
const getFooterNumText = computed(() => {
  let numText = getDataString('footNumTextColumn', '', dataObject.value);
  if (!isNaN(numText)) {
    return Number(numText);
  } else {
    return numText;
  }
});
const getShowFooterNumText = computed(() => {
  if (!isNaN(getFooterNumText.value)) {
    return Math.abs(getFooterNumText.value);
  } else {
    return getFooterNumText.value;
  }
});
const getDataCardImageStyle = computed(() => {
  return {
    height: props.options?.seriesSetting.iconSize + 'px',
    width: props.options?.seriesSetting.iconSize + 'px',
    borderRadius: props.options?.seriesSetting.iconRadius + 'px',
  };
});
</script>

<style scoped>
.data-card {
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
  padding-top: 10px;
  flex-grow: 0;
  border-top: 1px solid #e8e8e8;
}

.data-card-image {
  position: absolute;
  top: 0;
  right: 0;
}
</style>
