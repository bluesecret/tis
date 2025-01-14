<template>
  <el-form
    label-position="top"
    size="default"
    v-if="basicInfo"
    style="padding: 10px 16px 0"
    @submit.prevent
  >
    <el-form-item class="view-attribute-item" label="组件宽度">
      <el-slider style="width: 95%; margin-left: 5px" :min="1" :max="24" v-model="basicInfo.span" />
    </el-form-item>
    <el-form-item
      class="view-attribute-item"
      label="组件高度"
      v-if="widget.widgetType !== SysCustomWidgetType.RichText"
    >
      <el-input clearable v-model="basicInfo.height" />
    </el-form-item>
    <el-form-item
      class="view-attribute-item"
      label="底部距离"
      v-if="basicInfo.paddingBottom != null"
    >
      <el-input-number
        controls-position="right"
        clearable
        style="width: 100%"
        v-model="basicInfo.paddingBottom"
      />
    </el-form-item>
    <el-form-item class="view-attribute-item" label="边框颜色" v-if="basicInfo.borderColor != null">
      <el-color-picker v-model="basicInfo.borderColor" />
    </el-form-item>
    <el-form-item
      class="view-attribute-item"
      label="背景色"
      v-if="basicInfo.cellBackgroundColor != null"
    >
      <el-color-picker v-model="basicInfo.cellBackgroundColor" />
    </el-form-item>
    <el-form-item
      class="view-attribute-item"
      label="字体颜色"
      v-if="basicInfo.cellFontColor != null"
    >
      <el-color-picker v-model="basicInfo.cellFontColor" />
    </el-form-item>
    <el-form-item
      class="view-attribute-item"
      label="字体大小"
      v-if="basicInfo.cellFontSize != null"
    >
      <el-slider
        style="width: 95%; margin-left: 5px"
        v-model="basicInfo.cellFontSize"
        :min="10"
        :max="40"
        size="default"
      />
    </el-form-item>
    <el-form-item class="view-attribute-item" label="行高" v-if="basicInfo.cellRowHeight != null">
      <el-slider
        style="width: 95%; margin-left: 5px"
        v-model="basicInfo.cellRowHeight"
        :min="10"
        :max="100"
        size="default"
      />
    </el-form-item>
    <el-form-item
      class="view-attribute-item"
      label="高度"
      v-if="basicInfo.cellRowMaxHeight != null"
    >
      <el-slider
        style="width: 95%; margin-left: 5px"
        v-model="basicInfo.cellRowMaxHeight"
        :min="10"
        :max="100"
        size="default"
      />
    </el-form-item>
    <el-form-item class="view-attribute-item" label="对齐方式" v-if="basicInfo.cellAlign != null">
      <el-radio-group v-model="basicInfo.cellAlign">
        <el-radio-button value="left">居左</el-radio-button>
        <el-radio-button value="center">居中</el-radio-button>
        <el-radio-button value="right">居右</el-radio-button>
      </el-radio-group>
    </el-form-item>
    <el-form-item
      class="view-attribute-item"
      label="指示器位置"
      v-if="basicInfo.indicatorPosition != null"
    >
      <el-select v-model="basicInfo.indicatorPosition" placeholder="">
        <el-option label="外部" value="outside" />
        <el-option label="不显示" value="none" />
      </el-select>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { SysCustomWidgetType } from '@/common/staticDict';
import { ANY_OBJECT } from '@/types/generic';

const formConfig = inject('formConfig', () => {
  console.error('PivotTableBasicSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});

const widget = computed(() => {
  return formConfig().currentWidget;
});
const basicInfo = computed(() => {
  return widget.value ? (widget.value.props || {}).basicInfo : undefined;
});
</script>
