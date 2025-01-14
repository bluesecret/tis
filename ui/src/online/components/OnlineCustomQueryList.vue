<template>
  <div class="online-custom-query-list" style="height: 100%">
    <!-- 筛选、排序区域 -->
    <el-row class="header" align="middle" @click.stop="onWidgetClick(widget)">
      <div class="order-box">
        <div
          class="order-item"
          v-for="orderItem in widget.props.orderList"
          :key="orderItem.columnId"
        >
          <span>{{ orderItem.showName }}</span>
          <el-icon><CaretBottom /></el-icon>
        </div>
      </div>
      <div class="filter-btn" @click.stop="showFilterDlg = true">
        <span>筛选</span>
        <i class="online-icon icon-filter" />
      </div>
    </el-row>
    <!-- 列表区域 -->
    <el-row
      class="list-box"
      style="border-top: 2px solid #f6f6f6"
      :style="{
        padding: isTree ? '0px' : undefined,
        background: isTree ? 'white' : undefined,
      }"
    >
      <el-row style="width: 100%; height: 100%">
        <van-sidebar
          v-if="isTree"
          style="width: 100px; height: 100%; background: #f7f8fa"
          @click.stop="onWidgetClick(leftWidget)"
        >
          <van-sidebar-item title="标签A" />
          <van-sidebar-item title="标签B" />
          <van-sidebar-item title="标签C" />
        </van-sidebar>
        <div style="flex-grow: 1">
          <slot name="card" />
          <component
            v-if="cardWidget && customCard"
            class="widget-item"
            :is="getComponent"
            :class="{ active: isEdit && form().isActive(cardWidget) }"
            style="background: white"
            :isEdit="isEdit"
            :widget="cardWidget"
            @widgetClick="onWidgetClick"
          />
          <el-empty v-else-if="customCard" description="请选择列表使用的卡片" />
        </div>
      </el-row>
    </el-row>
    <!-- 弹出筛选窗口 -->
    <div v-show="showFilterDlg" class="filter-dlg">
      <div class="filter">
        <div class="filter-dlg-header">
          <div style="font-size: 14px; color: #333">筛选</div>
          <el-icon class="close" @click.stop="showFilterDlg = false"><Close /></el-icon>
        </div>
        <el-scrollbar class="filter-content">
          <slot name="filter" />
          <OnlineCustomBlock
            v-if="customFilter"
            v-model:value="filterWidgetList"
            :isEdit="isEdit"
            @widgetClick="onWidgetClick"
          />
        </el-scrollbar>
        <el-row class="filter-btn-box" type="flex" align="bottom">
          <el-button type="default" size="default" style="width: 100px">重置</el-button>
          <el-button type="primary" size="default" style="flex-grow: 1">确定</el-button>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { SidebarItem as VanSidebarItem } from 'vant';
import { CaretBottom, Close } from '@element-plus/icons-vue';
import { ANY_OBJECT } from '@/types/generic';
import { SysCustomWidgetType } from '@/common/staticDict';
import OnlineCustomBlock from '@/online/components/OnlineCustomBlock.vue';
import imageCardConfig from '@/online/config/imageCard';
import { WidgetProps, WidgetEmit } from './types/widget';
import { useWidget } from './hooks/widget';
import OnlineCustomImageCard from './OnlineCustomImageCard.vue';

const emit = defineEmits<WidgetEmit>();

interface IProps extends WidgetProps {
  leftWidget?: ANY_OBJECT;
  isTree?: boolean;
  customFilter?: boolean;
  customCard?: boolean;
}
const props = withDefaults(defineProps<IProps>(), {
  isEdit: false,
  isTree: false,
  customFilter: true,
  customCard: true,
});
const form = inject('form', () => {
  console.error('OnlineCustomQueryList: form not injected');
  return { isEdit: false } as ANY_OBJECT;
});

const showFilterDlg = ref(false);
const filterWidgetList = ref<ANY_OBJECT[]>([]);

const { childWidgetList, onWidgetClick } = useWidget(props, emit);

const cardWidget = computed(() => {
  return Array.isArray(props.widget.childWidgetList) && props.widget.childWidgetList.length > 0
    ? props.widget.childWidgetList[0]
    : undefined;
});
const getComponent = computed(() => {
  switch ((cardWidget.value || {}).widgetType) {
    case SysCustomWidgetType.ImageCard:
      return OnlineCustomImageCard;
    default:
      return '';
  }
});

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
    temp['relation'] = undefined;
    temp['datasource'] = undefined;
    temp['column'] = undefined;
  }
  return temp;
};

watch(
  filterWidgetList,
  newValue => {
    childWidgetList.value = [cardWidget.value, ...newValue];
  },
  {
    deep: true,
  },
);
watch(
  () => props.widget.props.card,
  () => {
    let cardWidget = getCardWidget();
    childWidgetList.value = [cardWidget, ...filterWidgetList.value];
  },
);

onMounted(() => {
  if (!Array.isArray(props.widget.childWidgetList) || props.widget.childWidgetList.length === 0) {
    let cardWidget = getCardWidget();
    if (cardWidget) {
      childWidgetList.value = [cardWidget];
    }
  }
  filterWidgetList.value = props.widget.childWidgetList.slice(1);
});
</script>

<style scoped>
.online-custom-query-list {
  position: relative;
  display: flex;
  flex-direction: column;
}
.online-custom-query-list .header {
  flex-shrink: 0;
  padding: 8px 15px;
  background: white;
  flex-grow: 0;
}
.online-custom-query-list .header .order-box {
  flex-shrink: 1;
  overflow-x: auto;
  overflow-y: hidden;
  width: 200px;
  margin-right: 10px;
  white-space: nowrap;
  flex-grow: 1;
}
.online-custom-query-list .header .filter-btn {
  height: 20px;
  line-height: 20px;
  font-size: 14px;
  cursor: pointer;
}
.online-custom-query-list .list-box {
  flex-shrink: 1;
  padding: 10px;
  background: #f6f6f6;
  flex-grow: 1;
}
.online-custom-query-list .filter-dlg {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1000;
  width: 100%;
  height: 100%;
  background: rgb(0 0 0 / 40%);
}
.online-custom-query-list .filter-dlg .filter {
  position: absolute;
  bottom: 0;
  left: 0;
  display: flex;
  width: 100%;
  height: 70%;
  min-height: 300px;
  max-height: 100%;
  padding: 0 20px 20px;
  background: white;
  border-radius: 12px 12px 0 0;
  flex-direction: column;
}
.online-custom-query-list .filter-dlg .filter .filter-content {
  flex-grow: 1;
  flex-shrink: 1;
  height: 200px;
}
.online-custom-query-list .filter-dlg .filter .filter-btn-box {
  flex-grow: 0;
  flex-shrink: 0;
  height: 40px;
}
.filter-dlg-header {
  position: relative;
  height: 40px;
  text-align: center;
  line-height: 40px;
}
.filter-dlg-header .close {
  position: absolute;
  top: 12px;
  right: 2px;
  font-size: 16px;
  cursor: pointer;
}
.online-custom-query-list .order-box .order-item {
  display: inline-block;
  height: 20px;
  line-height: 20px;
}
.order-item + .order-item {
  margin-left: 20px;
}
.filter-content :deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}
</style>
