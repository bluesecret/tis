<template>
  <el-form
    label-position="top"
    size="default"
    v-if="legendSetting"
    style="padding: 10px 16px 0"
    @submit.prevent
  >
    <el-form-item label="是否显示">
      <el-switch v-model="legendSetting.show" />
    </el-form-item>
    <el-form-item v-show="legendSetting.show" label="图标">
      <el-select v-model="legendSetting.icon" placeholder="">
        <el-option
          v-for="item in ShapeType.getList()"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item v-show="legendSetting.show" label="方向">
      <el-radio-group v-model="legendSetting.orient">
        <el-radio-button value="horizontal">横向</el-radio-button>
        <el-radio-button value="vertical">纵向</el-radio-button>
      </el-radio-group>
    </el-form-item>
    <el-form-item v-show="legendSetting.show" label="字体大小">
      <el-slider v-model="legendSetting.textStyle.fontSize" :min="10" :max="40" size="default" />
    </el-form-item>
    <el-form-item v-show="legendSetting.show" label="字体颜色">
      <el-color-picker v-model="legendSetting.textStyle.color" />
    </el-form-item>
    <el-form-item v-show="legendSetting.show" label="水平位置">
      <el-select v-model="legendSetting.left" placeholder="">
        <el-option label="左" value="left" />
        <el-option label="中" value="center" />
        <el-option label="右" value="right" />
      </el-select>
    </el-form-item>
    <el-form-item v-show="legendSetting.show" label="垂直位置">
      <el-select v-model="legendSetting.top" placeholder="">
        <el-option label="上" value="top" />
        <el-option label="中" value="middle" />
        <el-option label="下" value="" />
      </el-select>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { ShapeType } from '@/common/staticDict/report';

const formConfig = inject('formConfig', () => {
  console.error('ChartLabelSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});

const widget = computed(() => {
  return formConfig().currentWidget;
});
const legendSetting = computed(() => {
  return widget.value ? (widget.value.props || {}).legendSetting : undefined;
});
</script>
