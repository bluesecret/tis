<template>
  <el-row class="common-list" @click.stop="onTableClick" style="background: white">
    <el-col
      ref="title"
      class="title"
      v-if="title != null && title !== ''"
      :span="24"
      :style="getTitleStyle"
    >
      {{ title }}
    </el-col>
    <el-col class="common-list-container" :span="24">
      <div
        class="common-list-item"
        v-for="(item, index) in listData"
        :key="index"
        @dblclick="onDblClick(item)"
      >
        <div class="common-list-item-wrapper">
          <div class="item-icon" v-if="options?.seriesSetting.listIcon">
            <span
              v-if="options.seriesSetting.listIcon === 'digit'"
              :style="{ color: options.seriesSetting.listIconColor }"
              >{{ index + 1 }}</span
            >
            <svg width="14" height="14" v-if="options.seriesSetting.listIcon === 'circle'">
              <circle
                cx="7"
                cy="7"
                r="3.5"
                fill="none"
                :stroke="options.seriesSetting.listIconColor"
                stroke-width="2"
              />
            </svg>
          </div>
          <div class="item-title" :style="getListTitleStyle">
            {{ getDataString(getColumnName('titleColumn'), '标题', item) }}
          </div>
          <div
            class="item-date"
            :style="getListTimeStyle"
            v-if="getDataString(getColumnName('timeColumn'), '', item)"
          >
            {{ getDataString(getColumnName('timeColumn'), '', item) }}
          </div>
        </div>
        <div class="item-content" :style="getListTextStyle">
          {{ getDataString(getColumnName('textColumn'), '内容', item) }}
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { getColumnValue, getColumnName } from './utils';

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

const onTableClick = () => {
  emit('click');
};
const onDblClick = (item: ANY_OBJECT) => {
  emit('dblclick', item);
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
    fontWeight: props.options?.seriesSetting[textStyleName].bold ? 'bold' : 'normal',
    fontSize: props.options?.seriesSetting[textStyleName].fontSize + 'px',
  };
};

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

const title = computed(() => {
  if (props.options == null || props.options?.title == null || !props.options?.title.show) {
    return undefined;
  }
  return props.options ? props.options?.title.text : undefined;
});
const getTitleStyle = computed(() => {
  if (props.options == null || props.options?.title == null) return undefined;
  return {
    color: props.options?.title.textStyle.color,
    'font-size': props.options?.title.textStyle.fontSize + 'px',
    'font-weight': props.options?.title.bold ? 600 : undefined,
    'text-align': props.options?.title.left,
    'font-style': props.options?.title.italics ? 'italic' : undefined,
  };
});
const getListTitleStyle = computed(() => {
  return getTextStyle('titleStyle');
});
const getListTextStyle = computed(() => {
  return {
    ...getTextStyle('textStyle'),
    paddingLeft: props.options?.seriesSetting.listIcon ? '20px' : '0px',
  };
});
const getListTimeStyle = computed(() => {
  return getTextStyle('timeStyle');
});
const listData = computed(() => {
  if (props.data && Array.isArray(props.data)) {
    return props.data;
  } else if (props.data && !Array.isArray(props.data)) {
    return [props.data];
  } else {
    return [];
  }
});
</script>

<style scoped lang="scss">
.common-list {
  display: flex;
  height: 100%;
  flex-direction: column;
}
.common-list .title {
  flex-shrink: 0;
  height: 40px;
  padding: 0 2px;
  line-height: 30px;
  border-bottom: solid 1px #f6f6f6;
  flex-grow: 0;
}

.common-list-container {
  overflow-y: auto;
  width: 100%;
  padding: 5px 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei',
    '微软雅黑', Arial, sans-serif;
  box-sizing: border-box;
  .common-list-item-wrapper {
    display: flex;
    justify-items: center;
  }
  .common-list-item {
    padding: 5px;
    margin-bottom: 5px;
    background-color: #fff;
    border-bottom: 1px solid #efefef;
    box-sizing: border-box;
    cursor: pointer;
    &:last-of-type {
      border: none;
    }
  }
  .common-list-item:hover {
    background-color: #f5f5f5;
  }
  .item-icon {
    display: inline-flex;
    align-items: center;
    flex-shrink: 0;
    width: 20px;
    flex-grow: 0;
  }
  .item-title {
    flex-shrink: 1;
    overflow: hidden;
    width: auto;
    height: 30px;
    font-size: 14px;
    text-overflow: ellipsis;
    white-space: nowrap;
    color: #666;
    font-weight: bold;
    line-height: 30px;
    flex-grow: 1;
    word-break: break-all;
  }
  .item-date {
    flex-shrink: 0;
    overflow: hidden;
    max-width: 130px;
    height: 30px;
    font-size: 12px;
    text-align: right;
    color: #999;
    flex-grow: 0;
    line-height: 30px;
  }
  .item-content {
    overflow: hidden;
    width: 100%;
    max-height: 50px;
    text-align: justify;
    color: #adadad;
    line-height: 25px;
  }
}
</style>
