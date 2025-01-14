<template>
  <el-row class="online-custom-block" :gutter="0">
    <el-col :span="24">
      <el-row :gutter="form().gutter">
        <VueDraggable
          class="custom-block-draggable"
          :class="form().mode === 'pc' || isEdit ? 'el-row' : ''"
          draggable=".custom-widget-item"
          v-model="values"
          group="componentsGroup"
          ghostClass="ghost"
          chosenClass="chosen"
          :style="getDrableBoxStyle"
          style="position: relative; overflow: hidden; width: 100%"
          :disabled="!isEdit"
          :move="onDragMove"
          :onSort="onDragSort"
        >
          <template v-if="Array.isArray(values) && values.length > 0">
            <el-col
              class="custom-widget-item"
              :style="{
                display: isEdit || form().getWidgetVisible(subWidget) ? undefined : 'none',
              }"
              :class="{ active: isEdit && form().isActive(subWidget) }"
              v-for="subWidget in values"
              :key="subWidget.variableName"
              :span="subWidget.props.span || (subWidget.props.basicInfo || {}).span"
            >
              <div
                v-if="isEdit || form().getWidgetVisible(subWidget)"
                class="widget-item"
                :class="{ active: isEdit && form().isActive(subWidget) }"
                @click.stop="onWidgetClick(subWidget)"
              >
                <div
                  v-if="subWidget.widgetType === SysCustomWidgetType.Table"
                  :style="getTableStyle(subWidget)"
                >
                  <OnlineCardTable
                    :widget="subWidget"
                    :ref="subWidget.variableName"
                    :value="form().getTableData(subWidget)"
                    @input="(dataList:ANY_OBJECT[]) => form().setTableData(subWidget, dataList)"
                    @click.stop="onWidgetClick(subWidget)"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.Block"
                  :style="getBlockStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomBlock
                    :ref="subWidget.variableName"
                    v-model:value="subWidget.childWidgetList"
                    :isEdit="isEdit"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.Card"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineBaseCard
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.Tabs"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomTabs
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.TableContainer"
                  :style="{
                    'margin-bottom': (subWidget.props.paddingBottom || 0) + 'px',
                  }"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomTableContainer
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.CellGroup"
                  :style="{
                    'margin-bottom': (subWidget.props.paddingBottom || 0) + 'px',
                    'margin-top': (subWidget.props.paddingTop || 0) + 'px',
                    background: '#F6F7F9',
                  }"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomCellGroup
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.LineChart"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="getWidgetValue(subWidget)"
                    @input="val => onValueChange(subWidget, val)"
                    @change="(val: ANY_OBJECT|undefined, detail: ANY_OBJECT|null) => onWidgetValueChange(subWidget, val, detail)"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.BarChart"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="getWidgetValue(subWidget)"
                    @input="val => onValueChange(subWidget, val)"
                    @change="(val: ANY_OBJECT|undefined, detail: ANY_OBJECT|null) => onWidgetValueChange(subWidget, val, detail)"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.PieChart"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="getWidgetValue(subWidget)"
                    @input="val => onValueChange(subWidget, val)"
                    @change="(val: ANY_OBJECT|undefined, detail: ANY_OBJECT|null) => onWidgetValueChange(subWidget, val, detail)"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.ScatterChart"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="getWidgetValue(subWidget)"
                    @input="val => onValueChange(subWidget, val)"
                    @change="(val: ANY_OBJECT|undefined, detail: ANY_OBJECT|null) => onWidgetValueChange(subWidget, val, detail)"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.PivotTable"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="undefined"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.DataViewTable"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="undefined"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.Carousel"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="undefined"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.RichText"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="undefined"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.GaugeChart"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="undefined"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.RadarChart"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="undefined"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.FunnelChart"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="undefined"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.ProgressBar"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="undefined"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.ProgressCircle"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="undefined"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.DataCard"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="undefined"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.DataProgressCard"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="undefined"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.CommonList"
                  :style="getChartStyle(subWidget)"
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="undefined"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.Text"
                  :style="{
                    'margin-bottom': (subWidget.props.paddingBottom || 0) + 'px',
                  }"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="getWidgetValue(subWidget) || subWidget.props.text"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <div
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.Image"
                  :style="{
                    'margin-bottom': (subWidget.props.paddingBottom || 0) + 'px',
                  }"
                >
                  <OnlineCustomWidget
                    :ref="subWidget.variableName"
                    :widget="subWidget"
                    :isEdit="isEdit"
                    :value="getWidgetValue(subWidget)"
                    :src="subWidget.props.src"
                    @widgetClick="onWidgetClick"
                  />
                </div>
                <OnlineCustomImageCard
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.ImageCard"
                  :ref="subWidget.variableName"
                  :widget="subWidget"
                  :isEdit="isEdit"
                  :style="{
                    'margin-bottom': (subWidget.props.paddingBottom || 0) + 'px',
                  }"
                  @widgetClick="onWidgetClick"
                />
                <OnlineCustomQueryList
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.QueryList"
                  :widget="subWidget"
                  :isEdit="isEdit"
                  @widgetClick="onWidgetClick"
                />
                <OnlineCustomBaseList
                  v-else-if="subWidget.widgetType === SysCustomWidgetType.List"
                  :widget="subWidget"
                  :isEdit="isEdit"
                  @widgetClick="onWidgetClick"
                />
                <component
                  :is="form().mode === 'pc' ? ElFormItem : 'div'"
                  v-else
                  :label="subWidget.showName"
                  inset
                  :prop="subWidget.propString"
                  :class="{
                    'mobile-item': form().mode === 'mobile',
                    'rich-input': subWidget.widgetType === SysCustomWidgetType.RichEditor,
                  }"
                  :label-width="
                    subWidget.showName == null || subWidget.showName === ''
                      ? isEdit
                        ? '0px'
                        : '0px'
                      : undefined
                  "
                  @click.stop="onWidgetClick(subWidget)"
                >
                  <OnlineCustomWidget
                    :widget="subWidget"
                    :ref="subWidget.variableName"
                    :value="getWidgetValue(subWidget)"
                    @input="val => onValueChange(subWidget, val)"
                    @change="(val: ANY_OBJECT|undefined, detail: ANY_OBJECT|null) => onWidgetValueChange(subWidget, val, detail)"
                  />
                </component>
                <ActiveWidgetMenu
                  v-if="isEdit && form().isActive(subWidget)"
                  :widget="subWidget"
                  :clone="form().cloneWidget"
                  @copy="onCopyWidget"
                  @delete="onDeleteWidget(subWidget)"
                />
              </div>
            </el-col>
          </template>
          <div v-else-if="isEdit" class="info mover">
            <div style="width: 100px; height: 100px">
              <el-icon><UploadFilled /></el-icon>
            </div>
            <span>请拖入组件进行编辑</span>
          </div>
        </VueDraggable>
      </el-row>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { UploadFilled } from '@element-plus/icons-vue';
import { VueDraggable } from 'vue-draggable-plus';
import { ElMessageBox, ElFormItem } from 'element-plus';
import { ANY_OBJECT } from '@/types/generic';
import { SysCustomWidgetType } from '@/common/staticDict';
import OnlineCustomCellGroup from './OnlineCustomCellGroup.vue';
import ActiveWidgetMenu from './ActiveWidgetMenu.vue';
import OnlineCustomWidget from './OnlineCustomWidget.vue';
import OnlineCustomBaseList from './OnlineCustomList/index.vue';
import OnlineCustomTabs from './OnlineCustomTabs.vue';
import OnlineCustomTableContainer from './OnlineCustomTableContainer.vue';
import OnlineBaseCard from './OnlineBaseCard.vue';
import OnlineCardTable from './OnlineCardTable.vue';
import OnlineCustomImageCard from './OnlineCustomImageCard.vue';
import OnlineCustomQueryList from './OnlineCustomQueryList.vue';

interface IEmit {
  (event: 'widgetClick', value: ANY_OBJECT | null): void;
  (event: 'dragAdd', value: ANY_OBJECT): void;
  (event: 'update:value', value: ANY_OBJECT | ANY_OBJECT[]): void;
  (event: 'change', value: ANY_OBJECT | ANY_OBJECT[]): void;
}
const emit = defineEmits<IEmit>();

interface IProps {
  // v-model:value 调用者必须使用这种方式赋值，才能被更新
  value: Array<ANY_OBJECT>;
  // 是否显示边框
  showBorder?: boolean;
  isEdit?: boolean;
  height?: string;
}
const props = withDefaults(defineProps<IProps>(), {
  value: () => [],
  isEdit: false,
  showBorder: true,
});

const form = inject('form', () => {
  console.error('OnlineCustomBlock: form not injected');
  return { isEdit: false } as ANY_OBJECT;
});

const getDrableBoxStyle = computed(() => {
  let tempHeight = props.height;
  if (props.height == null || props.height === '') {
    tempHeight = props.isEdit && values.value.length <= 0 ? '150px' : '0px';
  }
  return {
    // 'min-height': tempHeight,
    // 'padding-bottom': props.isEdit ? '100px' : '0px',
  };
});
const getTableStyle = (widget: ANY_OBJECT) => {
  if (widget.widgetType !== SysCustomWidgetType.Table) return;
  return {
    'margin-bottom': (widget.props.paddingBottom || 0) + 'px',
  };
};
const getBlockStyle = (widget: ANY_OBJECT) => {
  return {
    'margin-bottom':
      (widget.props.paddingBottom || (widget.props.basicInfo || {}).paddingBottom || 0) + 'px',
    padding: props.isEdit ? '5px' : undefined,
    border: props.isEdit ? '1px solid #e8eaec' : undefined,
  };
};
const getChartStyle = (widget: ANY_OBJECT) => {
  return {
    'margin-bottom':
      (widget.props.paddingBottom || (widget.props.basicInfo || {}).paddingBottom || 0) + 'px',
  };
};

const values = shallowRef(props.value);

watch(
  () => props.value,
  (newValue: Array<ANY_OBJECT>) => {
    values.value = newValue;
  },
);

const onWidgetClick = (widget: ANY_OBJECT | null = null) => {
  emit('widgetClick', widget);
};

const onDragSort = () => {
  emit('change', values.value);
  emit('update:value', values.value);
};

const onDragMove = (e: ANY_OBJECT) => {
  console.log('block dragMove', e);
  // 容器组件不能改变位置
  let widgetType = e.relatedContext.element ? e.relatedContext.element.widgetType : undefined;
  return widgetType !== SysCustomWidgetType.Block && widgetType !== SysCustomWidgetType.Card;
};
const onCopyWidget = (widget: ANY_OBJECT) => {
  let tempList = [...values.value, widget];
  values.value = tempList;
  emit('change', tempList);
  emit('update:value', tempList);
};
const onDeleteWidget = (widget: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此组件？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      let tempList = values.value.filter(item => item !== widget);
      values.value = tempList;
      emit('change', tempList);
      emit('update:value', tempList);
      onWidgetClick(null);
    })
    .catch(e => {
      console.warn(e);
    });
};

const getWidgetValue = (widget: ANY_OBJECT) => {
  return form().getWidgetValue(widget);
};
const onValueChange = (
  widget: ANY_OBJECT,
  value: string | boolean | Date | number | ANY_OBJECT | Array<ANY_OBJECT> | undefined | null,
) => {
  if (value instanceof Event) {
    return;
  }
  return form().onValueChange(widget, value);
};
const onWidgetValueChange = (
  widget: ANY_OBJECT,
  value: ANY_OBJECT | null | undefined,
  detail: ANY_OBJECT | null,
) => {
  return form().onWidgetValueChange(widget, value, detail);
};

onMounted(() => {
  console.log('Block Block Block => form', form());
});
</script>

<style lang="scss" scoped>
.ghost {
  height: 30px;
  border-radius: 3px;
}
.info {
  // position: absolute;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  text-align: center;
  color: #999;
  flex-direction: column;
  vertical-align: middle;
}

.info div {
  width: 80px;
  height: 80px;
  font-size: 60px;
  text-align: center;
  border: 1px dashed #d9dbdd;
  border-radius: 6px;
  line-height: 100px;
}
.info span {
  margin-top: 10px;
  font-size: 16px;
}
</style>
