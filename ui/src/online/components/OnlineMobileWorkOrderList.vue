<template>
  <div class="online-custom-work-order-list" style="height: 100%">
    <!-- 筛选、排序区域 -->
    <el-row class="header" align="middle">
      <div class="order-box" />
      <div class="filter-btn" @click.stop="showFilterDlg = true">
        <span>筛选</span>
        <i class="online-icon icon-filter" />
      </div>
    </el-row>
    <!-- 列表区域 -->
    <el-row class="list-box" style="border-top: 2px solid #f6f6f6">
      <el-row type="flex" style="height: 100%">
        <div style="flex-grow: 1">
          <OnlineCustomImageCard
            class="widget-item"
            :class="{ active: isEdit && form().isActive(cardWidget) }"
            style="background: white"
            :isEdit="isEdit"
            :supportContent="false"
            :widget="cardWidget"
            @widgetClick="onWidgetClick"
          >
            <template v-slot:content>
              <el-row>
                <el-col :span="24">
                  <span class="text-item">工单编号：L938485757352727</span>
                </el-col>
                <el-col :span="24">
                  <span class="text-item">发起人：Admin</span>
                </el-col>
                <el-col :span="24">
                  <span class="text-item">当前任务：录入系统</span>
                </el-col>
                <el-col :span="24">
                  <span class="text-item">创建时间：2023-03-23 12:00:09</span>
                </el-col>
              </el-row>
            </template>
            <template v-slot:menu>
              <el-row type="flex" align="middle" class="menu-box" style="margin: 0 15px">
                <span class="status" style="flex-grow: 1">已提交</span>
                <el-button type="danger" size="default" :plain="true">撤销</el-button>
                <el-button type="primary" size="default" :plain="true">催办</el-button>
                <el-button type="primary" size="default">办理</el-button>
              </el-row>
            </template>
          </OnlineCustomImageCard>
        </div>
      </el-row>
    </el-row>
    <!-- 弹出筛选窗口 -->
    <div v-show="showFilterDlg" class="filter-dlg">
      <div class="filter">
        <div class="filter-dlg-header">
          <div style="font-size: 14px; color: #333">筛选</div>
          <el-icon class="close" @click="showFilterDlg = false"><Close /></el-icon>
        </div>
        <el-scrollbar class="filter-content">
          <MobileInputFilter label="工单编号" />
          <MobileSelectFilter label="工单状态" :dictDataList="SysFlowWorkOrderStatus.getList()" />
          <MobileDateRangeFilter label="创建时间" />
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
import { Close } from '@element-plus/icons-vue';
import OnlineCustomImageCard from '@/online/components/OnlineCustomImageCard.vue';
import MobileInputFilter from '@/online/components/mobile/MobileInputFilter.vue';
import MobileSelectFilter from '@/online/components/mobile/MobileSelectFilter.vue';
import MobileDateRangeFilter from '@/online/components/mobile/MobileDateRangeFilter.vue';
import { ANY_OBJECT } from '@/types/generic';
import { SysCustomWidgetType } from '@/common/staticDict';
import { SysFlowWorkOrderStatus } from '@/common/staticDict/flow';
import imageCardConfig from '@/online/config/imageCard';

interface IEmit {
  (event: 'widgetClick', value: ANY_OBJECT | null): void;
}
const emit = defineEmits<IEmit>();

interface IProps {
  widget: ANY_OBJECT;
  isEdit?: boolean;
  isTree?: boolean;
  leftWidget?: ANY_OBJECT;
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
  console.error('OnlineMobileWorkOrderList: form not injected');
  return { isEdit: false } as ANY_OBJECT;
});

const showFilterDlg = ref(false);
const filterWidgetList = ref([]);

const cardWidget = computed(() => {
  return Array.isArray(props.widget.childWidgetList) && props.widget.childWidgetList.length > 0
    ? props.widget.childWidgetList[0]
    : {};
});

const onWidgetClick = (widget: ANY_OBJECT | null = null) => {
  emit('widgetClick', widget);
};

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

onMounted(() => {
  if (!Array.isArray(props.widget.childWidgetList) || props.widget.childWidgetList.length === 0) {
    let cardWidget = getCardWidget();
    if (cardWidget) {
      let widget = props.widget;
      widget.childWidgetList = [cardWidget];
    }
  }
  filterWidgetList.value = props.widget.childWidgetList.slice(1);
});

watch(
  filterWidgetList,
  newValue => {
    let widget = props.widget;
    widget.childWidgetList = [cardWidget.value, ...newValue];
  },
  {
    deep: true,
  },
);

watch(
  () => props.widget?.props?.card,
  () => {
    let widget = props.widget;
    let cardWidget = getCardWidget();
    widget.childWidgetList = [cardWidget, ...filterWidgetList.value];
  },
  {
    deep: true,
  },
);
</script>

<style scoped>
.online-custom-work-order-list {
  position: relative;
  display: flex;
  flex-direction: column;
}
.online-custom-work-order-list .header {
  flex-shrink: 0;
  padding: 8px 15px;
  background: white;
  flex-grow: 0;
}
.online-custom-work-order-list .header .filter-btn {
  height: 20px;
  line-height: 20px;
  font-size: 14px;
  cursor: pointer;
}
.online-custom-work-order-list .list-box {
  flex-shrink: 1;
  padding: 10px;
  background: #f6f6f6;
  flex-grow: 1;
}
.online-custom-work-order-list .header .order-box {
  flex-shrink: 1;
  overflow-x: auto;
  overflow-y: hidden;
  width: 200px;
  margin-right: 10px;
  white-space: nowrap;
  flex-grow: 1;
}
.online-custom-work-order-list .filter-dlg {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1000;
  width: 100%;
  height: 100%;
  background: rgb(0 0 0 / 40%);
}
.online-custom-work-order-list .filter-dlg .filter {
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
.online-custom-work-order-list .filter-dlg .filter .filter-content {
  flex-grow: 1;
  flex-shrink: 1;
  height: 200px;
}
.online-custom-work-order-list .filter-dlg .filter .filter-btn-box {
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
.filter-content :deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}
.menu-box {
  border-top: 1px solid #e8e8e8;
  padding: 15px 0;
}
.menu-box .status {
  font-size: 12px;
  color: #00ae1c;
}
.text-item {
  height: 24px;
  line-height: 24px;
  font-size: 12px;
  color: #666;
}
</style>
../../pages/online/editOnlinePage/formDesign/config/imageCard
