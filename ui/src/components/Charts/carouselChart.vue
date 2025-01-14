<template>
  <el-row class="carousel-chart" @click.stop="onTableClick" style="background: white">
    <el-col
      ref="titleRef"
      class="title"
      v-if="title != null && title !== ''"
      :span="24"
      :style="getTitleStyle"
    >
      {{ title }}
    </el-col>
    <el-col class="table" :span="24" v-show="ready">
      <el-carousel
        :indicator-position="options && options.basicInfo.indicatorPosition"
        :height="tableHeight"
      >
        <el-carousel-item v-for="(item, index) in carouselData" :key="index">
          <div class="carousel-item-wrapper">
            <img
              class="carousel-item-image"
              :src="item.url"
              :style="{ 'object-fit': options && options.basicInfo.objectFit }"
            />
            <div class="carousel-item-text" v-if="item.description">
              <div :style="getCarouselItemStyle">{{ item.description }}</div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { StyleValue } from 'vue';
import { ANY_OBJECT } from '@/types/generic';

const props = defineProps<{
  // 透视表数据
  data?: ANY_OBJECT | ANY_OBJECT[];
  // 透视表高度
  height?: string;
  // 透视表设置
  options?: ANY_OBJECT;
  imagePath: ANY_OBJECT[];
  imageName: ANY_OBJECT[];
}>();

const ready = ref(false);
let buildDataTimer: number | null = null;
// 表格高度
const tableHeight = ref<string>();

const title = computed(() => {
  console.log('title >>>', props.options);
  if (!props.options || props.options.title == null || !props.options.title.show) return undefined;
  return props.options.title.text;
});
const getTitleStyle = computed(() => {
  if (!props.options || props.options.title == null) return undefined;
  return {
    color: props.options.title.textStyle.color,
    'font-size': props.options.title.textStyle.fontSize + 'px',
    'font-weight': props.options.title.bold ? 600 : undefined,
    'text-align': props.options.title.left,
    'font-style': props.options.title.italics ? 'italic' : undefined,
  };
});
const getCarouselItemStyle = computed<StyleValue | undefined>(() => {
  if (props.options == null || props.options.basicInfo == null) return undefined;
  return {
    color: props.options.basicInfo.cellFontColor,
    lineHeight: (props.options.basicInfo.cellRowHeight || 30) + 'px',
    height: (props.options.basicInfo.cellRowMaxHeight || 30) + 'px',
    'background-color': props.options.basicInfo.cellBackgroundColor,
    'font-size': props.options.basicInfo.cellFontSize + 'px',
    padding: '0px 5px',
    boxSizing: 'border-box',
    'text-align': props.options.basicInfo.cellAlign,
  };
});
const carouseCount = computed(() => {
  if (Array.isArray(props.data)) {
    return props.data.length < props.options.pageParam.pageSize
      ? props.data.length
      : props.options.pageParam.pageSize;
  } else {
    return 1;
  }
});
/*
 * @param {object} row  是当前行数据比如 {a: { b: 'xxxx' }}
 * @param {array} columnNameList 列名数组，如：['a','b'];
 * @returns {object} 取出的值
 */
const getColumnValue = (row: ANY_OBJECT, columnNameList: string[]) => {
  if (row == null) return undefined;
  if (Array.isArray(columnNameList)) {
    let dataValue = columnNameList.length > 0 ? row : undefined;

    for (let i = 0; i < columnNameList.length; i++) {
      let name = columnNameList[i];
      if (name == null || dataValue == null) {
        dataValue = undefined;
        break;
      }
      let dictName = name + '__DictMap';
      dataValue = dataValue[dictName] ? dataValue[dictName].name : dataValue[name];
    }
    return dataValue;
  } else {
    let columnName = columnNameList;
    let dictName = columnName + '__DictMap';
    return row[dictName] ? row[dictName].name : row[columnName];
  }
};
const getImageColumnValue = (data: ANY_OBJECT, columnList: ANY_OBJECT[]) => {
  if (columnList && columnList.length > 0) {
    return getColumnValue(data, columnList[0].columnName);
  }
  return undefined;
};
const carouselData = computed(() => {
  let temp: ANY_OBJECT[] = [];
  if (Array.isArray(props.data)) {
    props.data.filter((dataItem, index) => {
      if (index < carouseCount.value) {
        // 图片字段为数组，把数组展开
        temp.push({
          url: getImageColumnValue(dataItem, props.imagePath),
          description: getImageColumnValue(dataItem, props.imageName),
        });
      }
    });
    return temp;
  } else {
    temp.push({
      url: getImageColumnValue(props.data, props.imagePath),
      description: getImageColumnValue(props.data, props.imageName),
    });
    return temp;
  }
});

const emit = defineEmits<{ click: [] }>();
const onTableClick = () => {
  emit('click');
};

// 计算表格高度
const calcTableHeight = () => {
  tableHeight.value = props.height;
};
watch(
  () => props.height,
  () => {
    nextTick(() => {
      calcTableHeight();
    });
  },
  {
    immediate: true,
  },
);

// 构建透视表渲染信息
const buildTableInfo = () => {
  if (buildDataTimer != null) clearTimeout(buildDataTimer);
  ready.value = false;
  buildDataTimer = setTimeout(() => {
    // this.tableGroupInfo = [];
    // this.tableDataList = [];
    // this.footerDataList = [];
    // this.showFooter = false;
    buildDataTimer = null;
    ready.value = true;
  }, 50);
};
watch(
  () => props.data,
  () => {
    nextTick(() => {
      buildTableInfo();
    });
  },
  {
    immediate: true,
  },
);
watch(
  () => props.options,
  () => {
    nextTick(() => {
      buildTableInfo();
    });
  },
  {
    deep: true,
    immediate: true,
  },
);
</script>

<style scoped>
/*
  .carousel-chart {
    display: flex;
    flex-direction: column;
  }
  */
.carousel-chart .title {
  flex-shrink: 0;
  padding: 10px 2px;
  flex-grow: 0;
}

.carousel-chart .carousel-item-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

.carousel-chart .carousel-item-image {
  display: inline-block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.carousel-chart .carousel-item-text {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  display: inline-flex;
  align-items: flex-end;
  width: 100%;
}

.carousel-chart .carousel-item-text div {
  display: inline-block;
  width: 100%;
  min-height: 30px;
  padding: 8px 10px;
}
</style>
