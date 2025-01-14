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
    <el-form-item class="view-attribute-item" label="组件高度" v-if="basicInfo.height != null">
      <el-input clearable v-model="basicInfo.height" />
    </el-form-item>
    <el-form-item class="view-attribute-item" label="图表底部距离" v-if="basicInfo.paddingBottom">
      <el-input-number
        controls-position="right"
        clearable
        style="width: 100%"
        v-model="basicInfo.paddingBottom"
      />
    </el-form-item>
    <template v-if="basicInfo.grid">
      <el-form-item class="view-attribute-item" label="图表左侧间距">
        <el-input v-model="basicInfo.grid.left" placeholder="" />
      </el-form-item>
      <el-form-item class="view-attribute-item" label="图表右侧间距">
        <el-input v-model="basicInfo.grid.right" placeholder="" />
      </el-form-item>
      <el-form-item class="view-attribute-item" label="图表顶部间距">
        <el-input v-model="basicInfo.grid.top" placeholder="" />
      </el-form-item>
      <el-form-item class="view-attribute-item" label="图表底部间距">
        <el-input v-model="basicInfo.grid.bottom" placeholder="" />
      </el-form-item>
    </template>
    <el-form-item class="view-attribute-item" label="配色方案" v-if="basicInfo.chartColors">
      <ChartColorSelect v-model:value="basicInfo.chartColors" />
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import ChartColorSelect from './chartColorSelect.vue';

const formConfig = inject('formConfig', () => {
  console.error('ChartDatasetSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});
console.log('ChartBasicSetting formConfig()', formConfig());

const widget = computed(() => {
  return formConfig().currentWidget;
});
const basicInfo = computed(() => {
  return widget.value ? (widget.value.props || {}).basicInfo : undefined;
});
</script>
