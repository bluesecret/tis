<template>
  <div class="report-design" @drop.prevent.stop>
    <!-- 左侧菜单 -->
    <div class="left-menu">
      <div
        class="left-menu-item"
        :class="{ active: activeLeftMenu === 'form' }"
        @click="activeLeftMenu = 'form'"
      >
        <i class="online-icon icon-component" />
        <span style="margin-top: 4px">组件</span>
      </div>
    </div>
    <div class="left-panel">
      <el-card
        class="base-card"
        shadow="never"
        :body-style="{ padding: '0px' }"
        style="border: none; flex-grow: 1"
      >
        <template #header>
          <div class="base-card-header">
            <i class="online-icon">
              <span>组件</span>
            </i>
            <div class="base-card-operation">
              <el-input
                class="round-search"
                prefix-icon="el-icon-search"
                v-model="filter.widgetName"
                clearable
                placeholder="组件名称"
                size="default"
                style="width: 140px"
              />
            </div>
          </div>
        </template>
        <el-row style="height: calc(100% - 65px)">
          <el-scrollbar style="height: 100%">
            <el-col :span="24" style="padding: 0 16px">
              <el-row class="widget-group" v-for="group in widgetGroup" :key="group.id">
                <div class="group-title">{{ group.groupName }}</div>
                <VueDraggable
                  class="group-widget-list"
                  draggable=".group-widget-item"
                  v-model="group.widgetList"
                  :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
                  :clone="cloneWidget"
                  :sort="false"
                >
                  <div
                    class="group-widget-item"
                    v-for="widget in group.widgetList"
                    :key="widget.id"
                    @click="onAddWidget(widget)"
                  >
                    <div class="icon" :title="SysCustomWidgetType.getValue(widget.widgetType)">
                      <i class="" :class="widget.icon || ''" />
                    </div>
                    <div class="name">{{ SysCustomWidgetType.getValue(widget.widgetType) }}</div>
                  </div>
                  <div style="width: 64px" />
                </VueDraggable>
              </el-row>
            </el-col>
          </el-scrollbar>
        </el-row>
      </el-card>
    </div>
    <div class="design-panel">
      <el-row type="flex" justify="space-between" align="middle" style="height: 48px">
        <div style="font-size: 18px">
          <i
            class="device-item online-icon icon-phone"
            :class="{ active: activeMode === 'mobile' }"
            title="手机端"
            @click="onActiveModeChange('mobile')"
          />
          <i
            class="device-item online-icon icon-pc"
            :class="{ active: activeMode === 'pc' }"
            title="PC 端"
            @click="onActiveModeChange('pc')"
          />
        </div>
        <el-button
          link
          size="default"
          :icon="Refresh"
          style="font-size: 14px; color: #999; font-weight: normal"
          @click="onClearWidget"
        >
          重置表单
        </el-button>
      </el-row>
      <div class="design-box">
        <div style="max-width: 100%" :style="getDesignBoxStyle" @click="onFormClick">
          <ReportForm
            v-if="activeMode === 'pc'"
            ref="reportForm"
            :isEdit="true"
            :height="layoutStore.documentClientHeight - 200 + 'px'"
            :formConfig="pageInfo.formInfo[activeMode]"
            :currentWidget="currentWidget"
            @widgetClick="onWidgetClick"
            :defaultFormItemSize="layoutStore.defaultFormItemSize"
          />
          <MobileReportForm
            v-if="activeMode === 'mobile'"
            ref="mobileReportForm"
            :isEdit="true"
            :height="layoutStore.documentClientHeight - 200 + 'px'"
            :formConfig="pageInfo.formInfo[activeMode]"
            :currentWidget="currentWidget"
            @widgetClick="onWidgetClick"
          />
        </div>
      </div>
    </div>
    <div class="attribute-panel">
      <el-tabs v-model="rightActive">
        <el-tab-pane label="属性" name="props">
          <template #label>
            <span>基础</span>
          </template>
          <el-scrollbar :style="{ height: layoutStore.documentClientHeight - 138 + 'px' }">
            <el-collapse v-if="currentWidget == null" style="border: none">
              <el-collapse-item title="表单配置">
                <template #title>
                  <el-row class="attribute-item-title">
                    <span style="padding: 0 16px">表单配置</span>
                  </el-row>
                </template>
                <CustomFormSetting style="padding: 10px 16px 0" />
              </el-collapse-item>
            </el-collapse>
            <template v-else>
              <template v-if="isCollapse">
                <CollapseAttributeSetting
                  v-model:value="currentWidget.props"
                  :widget="currentWidget"
                >
                  <el-collapse-item title="组件数据">
                    <template #title>
                      <el-row class="attribute-item-title">
                        <span style="padding: 0 16px">组件数据</span>
                      </el-row>
                    </template>
                    <CustomBindDataSetting
                      v-model:value="currentWidget"
                      style="padding: 10px 16px 0"
                    />
                  </el-collapse-item>
                </CollapseAttributeSetting>
              </template>
              <template v-else>
                <el-collapse style="border: none">
                  <el-collapse-item title="组件数据">
                    <template #title>
                      <el-row class="attribute-item-title">
                        <span style="padding: 0 16px">组件数据</span>
                      </el-row>
                    </template>
                    <CustomBindDataSetting
                      v-model:value="currentWidget"
                      style="padding: 10px 16px 0"
                    />
                  </el-collapse-item>
                  <el-collapse-item title="组件属性">
                    <template #title>
                      <el-row class="attribute-item-title">
                        <span style="padding: 0 16px">组件属性</span>
                      </el-row>
                    </template>
                    <CustomWidgetAttributeSetting
                      class="attribute-setting"
                      v-model:value="currentWidget.props"
                      style="padding: 10px 16px 0"
                      :widget="currentWidget"
                    />
                  </el-collapse-item>
                </el-collapse>
              </template>
            </template>
          </el-scrollbar>
        </el-tab-pane>
        <el-tab-pane
          label="操作"
          name="operate"
          v-if="
            currentWidget != null &&
            currentWidget.operationList != null &&
            currentWidget.operationList.length > 0
          "
        >
          <template #label>
            <span>
              <!--<i class="online-icon icon-operator" style="margin-right: 5px;" />-->
              操作
            </span>
          </template>
          <el-scrollbar :style="{ height: layoutStore.documentClientHeight - 138 + 'px' }">
            <CustomReportOperationSetting
              v-model:value="currentWidget.operationList"
              :groupList="pageGroupList"
            />
          </el-scrollbar>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Refresh } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { VueDraggable } from 'vue-draggable-plus';
import {
  ReportDatasetController,
  ReportDatasetGroupController,
  ReportDictController,
} from '@/api/report';
import { treeDataTranslate } from '@/common/utils';
import CustomWidgetAttributeSetting from '@/online/components/WidgetAttributeSetting/AttributeForm/index.vue';
import CollapseAttributeSetting from '@/online/components/WidgetAttributeSetting/AttributeCollapse/index.vue';
import { ANY_OBJECT } from '@/types/generic';
import { SysCustomWidgetType } from '@/common/staticDict';
import { useLayoutStore } from '@/store';
import { getAllWidgetList } from '../utils';
import widgetData from '../reportRender/components/index';
import ReportForm from '../reportRender/ReportForm/index.vue';
import MobileReportForm from '../reportRender/MobileReportForm/index.vue';
import CustomFormSetting from './components/CustomFormSetting/index.vue';
import CustomBindDataSetting from './components/CustomBindDataSetting/index.vue';
import CustomReportOperationSetting from './components/CustomReportOperationSetting/index.vue';
const layoutStore = useLayoutStore();

const emit = defineEmits<{ updateForm: [ANY_OBJECT] }>();
const props = withDefaults(
  defineProps<{
    pageInfo: ANY_OBJECT;
    pageGroupList?: Array<ANY_OBJECT>;
    dictList?: Array<ANY_OBJECT>;
  }>(),
  {
    dictList: () => [],
    pageGroupList: () => [],
  },
);

console.log('props.pageInfo', props);

const reportForm = ref();
const mobileReportForm = ref();
const activeLeftMenu = ref('form');
const rightActive = ref('props');
const activeMode = ref('pc');
const filter = ref<ANY_OBJECT>({
  widgetName: undefined,
});
const currentWidget = ref<ANY_OBJECT>();
const datasetGroupList = ref<ANY_OBJECT[]>([]);
const datasetList = ref<ANY_OBJECT[]>([]);

const widgetGroup = computed(() => {
  return widgetData.formWidgetGroupList[activeMode.value];
});
const isCollapse = computed(() => {
  if (!currentWidget.value) return false;
  let temp =
    [
      SysCustomWidgetType.PivotTable,
      SysCustomWidgetType.LineChart,
      SysCustomWidgetType.BarChart,
      SysCustomWidgetType.PieChart,
      SysCustomWidgetType.ScatterChart,
      SysCustomWidgetType.DataViewTable,
      SysCustomWidgetType.Carousel,
      SysCustomWidgetType.RichText,
      SysCustomWidgetType.GaugeChart,
      SysCustomWidgetType.FunnelChart,
      SysCustomWidgetType.RadarChart,
      SysCustomWidgetType.ProgressBar,
      SysCustomWidgetType.ProgressCircle,
      SysCustomWidgetType.DataCard,
      SysCustomWidgetType.DataProgressCard,
      SysCustomWidgetType.CommonList,
    ].indexOf(currentWidget.value.widgetType) !== -1;
  return temp;
});
const currentForm = computed(() => {
  return (props.pageInfo || {}).formInfo || {};
});
// eslint-disable-next-line @typescript-eslint/no-unused-vars

// eslint-disable-next-line @typescript-eslint/no-unused-vars

const getAllWidget = computed(() => {
  let formInfo = currentForm.value[activeMode.value];
  if (formInfo && Array.isArray(formInfo.widgetList)) {
    return getAllWidgetList(formInfo.widgetList, activeMode.value, true);
  }

  return [];
});
const getReportDatasetTree = computed(() => {
  if (Array.isArray(datasetList.value) && Array.isArray(datasetGroupList.value)) {
    let datsetGroupMap = new Map();
    datasetList.value.forEach(item => {
      item.id = item.datasetId;
      item.name = item.datasetName;
      item.isGroup = false;
      let temp = datsetGroupMap.get(item.groupId);
      if (temp == null) {
        temp = [];
        datsetGroupMap.set(item.groupId, temp);
      }
      temp.push(item);
    });
    let datasetGroupTree = treeDataTranslate(
      datasetGroupList.value.map(item => {
        return {
          id: item.groupId,
          name: item.groupName,
          isGroup: true,
          children: datsetGroupMap.get(item.groupId) || [],
          ...item,
        };
      }),
      'groupId',
      'parentId',
    );
    return datasetGroupTree;
  }
  return [];
});
const getDesignBoxStyle = computed(() => {
  let width, padding, background;
  if (activeMode.value === 'pc') {
    width = '100%';
    padding = '0px';
    background = 'white';
  } else {
    width = '375px';
    padding = '0px';
    background = undefined;
  }
  return {
    width,
    padding,
    background,
    border: activeMode.value === 'pc' ? undefined : '1px solid #E8E8E8',
  };
});

const onActiveModeChange = (mode: string) => {
  activeMode.value = mode;
  // refreshFormInfo();
};
const onClearWidget = () => {
  ElMessageBox.confirm('是否重置表单？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      if (currentForm.value && currentForm.value[activeMode.value])
        currentForm.value[activeMode.value].widgetList = [];
      if (mobileReportForm.value) mobileReportForm.value.clearWidget();
    })
    .catch(e => {
      console.warn(e);
    });
};
const onWidgetClick = (widget: ANY_OBJECT | null) => {
  console.log('reportDesign widgetClick', widget);
  rightActive.value = 'props';
  currentWidget.value = widget || undefined;
};
const onAddWidget = (widget: ANY_OBJECT) => {
  if (
    currentWidget.value != null &&
    (currentWidget.value.widgetType === SysCustomWidgetType.Block ||
      currentWidget.value.widgetType === SysCustomWidgetType.Card)
  ) {
    currentWidget.value.childWidgetList.push(cloneWidget(widget));
  } else {
    if (currentForm.value && currentForm.value[activeMode.value]) {
      currentForm.value[activeMode.value].widgetList.push(cloneWidget(widget));
    }
  }
};
const cloneWidget = (widget: ANY_OBJECT) => {
  let temp: ANY_OBJECT = widgetData.getWidgetObject(widget);
  if (activeMode.value === 'mobile') {
    temp.props.span = 24;
  }
  temp.widgetId = new Date().getTime();
  return temp;
};
const onFormClick = () => {
  rightActive.value = 'props';
  currentWidget.value = undefined;
};
const saveFormInfo = () => {
  emit('updateForm', currentForm.value);
};

defineExpose({ saveFormInfo });

const loadDatasetList = () => {
  datasetGroupList.value = [];
  datasetList.value = [];
  ReportDatasetController.list({})
    .then(res => {
      datasetList.value = res.data.dataList;
    })
    .catch(e => {
      console.warn(e);
    });
  ReportDatasetGroupController.list({})
    .then(res => {
      datasetGroupList.value = res.data;
    })
    .catch(e => {
      console.warn(e);
    });
};

const loadDatasetInfo = (datasetId: string) => {
  return new Promise((resolve, reject) => {
    let params = {
      datasetId: datasetId,
    };
    ReportDatasetController.view(params)
      .then(res => {
        //console.log('buildDatasetInfo loadDatasetInfo', res);
        try {
          let info = JSON.parse(res.data.datasetInfo);
          res.data.datasetParamList = info.paramList;
          res.data.columnList = info.columnList || res.data.columnList;
        } catch (e) {
          res.data.datasetParamList = [];
        }
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const listDictData = (dictId: string) => {
  return new Promise((resolve, reject) => {
    ReportDictController.listDictData({
      dictId: dictId,
    })
      .then(res => {
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const listColumnData = (datasetId: string, columnId: string) => {
  return new Promise((resolve, reject) => {
    ReportDatasetController.listDataWithColumn({
      datasetId: datasetId,
      columnId: columnId,
    })
      .then(res => {
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
      });
  });
};

const formConfig = computed(() => {
  return {
    form: currentForm.value[activeMode.value],
    activeMode: activeMode.value,
    currentWidget: currentWidget.value,
    getWidgetAttribute: widgetData.getWidgetAttribute,
    getWidgetObject: widgetData.getWidgetObject,
    reportDatasetTree: getReportDatasetTree.value,
    datasetList: datasetList.value,
    allWidgetList: getAllWidget.value,
    loadDatasetInfo: loadDatasetInfo,
    listDictData: listDictData,
    listColumnData: listColumnData,
    dictList: props.dictList,
  };
});

provide('formConfig', () => formConfig.value);

onMounted(() => {
  loadDatasetList();
});
</script>

<style lang="scss" scoped>
.report-design {
  display: flex;
  height: 100%;
}

.report-design .left-menu {
  width: 48px;
  height: 100%;
  padding: 23px 0;
  background: #2d3039;
}

.report-design .left-menu .left-menu-item {
  display: flex;
  font-size: 14px;
  text-align: center;
  color: white;
  flex-direction: column;
  justify-items: center;
  font-weight: 400;
  cursor: pointer;

  i {
    width: 34px;
    height: 34px;
    margin-left: 7px;
    font-size: 20px;
    color: white;
    background: $color-primary;
    border-radius: 4px;
    line-height: 34px;
  }
}

.report-design .left-menu .left-menu-item.active {
  color: $color-primary;
}

.report-design .left-panel {
  display: flex;
  flex-direction: column;
  width: 240px;
  height: 100%;

  .form-card {
    flex-grow: 0;
    flex-shrink: 0;
    height: 280px;
  }

  .base-card-header {
    height: 56px;
    line-height: 56px;

    i {
      font-size: 16px;
      color: #999;

      span {
        font-size: 14px;
        color: #333;
        font-weight: bold;
      }
    }
  }
}

.form-item {
  height: 50px;
  .form-item-title {
    span {
      overflow: hidden;
      max-width: 170px;
      font-size: 16px;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: #333;
      font-weight: 400;
      cursor: pointer;
    }
    .tag {
      padding: 3px 8px;
      margin-left: 8px;
      font-size: 14px;
      color: #999;
      background: #f6f7f9;
      border-radius: 4px;
    }
  }
  .font-item-menu {
    font-size: 18px;
    color: #999;
  }
}
.form-item.active {
  .form-item-title {
    span {
      color: $color-primary;
    }
    .tag {
      color: #ffb800;
      background: #fff8e5;
    }
  }
}
.widget-group {
  .group-title {
    height: 45px !important;
    color: #333 !important;
    font-weight: bold !important;
    line-height: 45px !important;
  }
  .group-widget-list {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;

    .group-widget-item {
      margin-bottom: 10px;
      .icon {
        width: 64px;
        height: 64px;
        font-size: 26px;
        text-align: center;
        color: #666;
        border: 1px solid #e8e8e8;
        border-radius: 4px;
        cursor: pointer;
        line-height: 64px;
      }
      .icon:hover {
        background: #f6f7f9;
      }
      .name {
        overflow: hidden;
        width: 64px;
        height: 26px;
        font-size: 12px;
        text-align: center;
        text-overflow: ellipsis;
        white-space: nowrap;
        color: #666;
        line-height: 26px;
        font-weight: 400;
      }
    }
  }
}

.design-panel {
  flex-grow: 1;
  width: 200px;
  padding: 0 24px 24px;

  .design-box {
    display: flex;
    justify-content: center;
    width: 100%;
    height: calc(100% - 48px);
  }
}

.attribute-panel {
  flex-shrink: 0;
  width: 288px;
  background: white;
  flex-grow: 0;

  .attribute-item-title unified-font {
    height: 40px;
    padding: 0 16px;
    line-height: 40px;

    span {
      font-size: 14px;
      color: #333;
    }
  }
}

.data-card .data-column-title {
  padding-left: 24px;
  font-size: 14px;
  color: black;
  font-weight: 400;

  span {
    overflow: hidden;
    max-width: 165px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
.data-card .table-column-item {
  height: 44px;
  padding: 0 24px;
  font-size: 14px;
  color: #666;

  .online-icon.flag {
    width: 18px;
    height: 18px;
    font-size: 16px;
    text-align: center;
    color: #999;
    background: #f1f2f3;
    line-height: 18px;
  }

  .online-icon.flag.active {
    color: #ffb800;
    background: #fff8e5;
  }
}
.device-item {
  display: inline-block;
  width: 32px;
  height: 32px;
  font-size: 24px;
  text-align: center;
  color: #666;
  line-height: 32px;
  cursor: pointer;
}
.device-item.active {
  color: $color-primary;
  background: #fff1e5;
  cursor: pointer;
}

.round-search :deep(.el-input__inner) {
  border-radius: 16px;
}

.base-card :deep(.el-card__body) {
  height: 100%;
}
.attribute-panel :deep(.el-tabs__item) {
  height: 57px;
  padding-top: 10px;
  padding-right: 20px;
  padding-left: 20px;
  font-size: 14px;
}
.attribute-panel :deep(.el-tabs__header) {
  margin: 0;
}
.attribute-panel :deep(.el-tabs__nav) {
  margin-left: 16px;
}
.attribute-panel :deep(.el-form-item) {
  margin-bottom: 12px;
}
.attribute-panel :deep(.el-form-item__label) {
  padding-right: 0;
  padding-bottom: 0;
  font-size: 14px;
  color: #333;
  line-height: 25px;
}
.attribute-panel :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}
.attribute-panel :deep(.el-radio__label) {
  padding-left: 8px;
}
.attribute-panel :deep(.el-radio) {
  margin-right: 24px;
}
.attribute-panel :deep(.el-radio:last-child) {
  margin-right: 0;
}
.attribute-panel :deep(.el-collapse-item__wrap) {
  background: #fafbfc;
  border: none;
}
.attribute-panel :deep(.el-collapse-item__content) {
  padding-bottom: 0;
  background: #fafbfc;
}
.data-card :deep(.el-collapse-item__content) {
  padding-bottom: 0;
  background: #fafbfc;
}
.data-card :deep(.el-collapse-item__header) {
  height: 50px;
  line-height: 50px;
}
.data-card :deep(.el-collapse-item__arrow) {
  margin-right: 24px;
}
.data-card .table-column-item :deep(.el-link--inner) {
  overflow: hidden;
  max-width: 240px;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.data-card :deep(.el-tag.el-tag--success) {
  color: #00ae1c;
  background-color: #e5f7e7;
  border-color: #e5f7e7;
}
.data-card :deep(.el-tag.el-tag--warning) {
  color: #f70;
  background-color: #fff1e5;
  border-color: #fff1e5;
}
.data-card :deep(.el-tag.el-tag--primary) {
  color: #ffb800;
  background-color: #fff8e5;
  border-color: #fff8e5;
}
</style>
