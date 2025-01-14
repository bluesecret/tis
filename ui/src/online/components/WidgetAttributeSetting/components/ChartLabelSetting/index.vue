<template>
  <el-form
    label-position="top"
    size="default"
    v-if="labelSetting"
    style="padding: 10px 16px 0"
    @submit.prevent
  >
    <el-form-item label="是否显示">
      <el-switch v-model="labelSetting.show" />
    </el-form-item>
    <el-form-item v-show="labelSetting.show" label="字体大小">
      <el-slider v-model="labelSetting.fontSize" :min="10" :max="40" size="default" />
    </el-form-item>
    <el-form-item v-show="labelSetting.show" label="字体颜色">
      <el-color-picker v-model="labelSetting.color" />
    </el-form-item>
    <el-form-item v-show="labelSetting.show" label="标签位置">
      <template v-if="widget.widgetType === SysCustomWidgetType.FunnelChart">
        <el-select v-model="labelSetting.position" placeholder="" style="width: 100%">
          <el-option
            v-for="item in InsidePosition.getList()"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </template>
      <template v-else>
        <el-select v-model="labelSetting.position" placeholder="" style="width: 100%">
          <el-option
            v-for="item in VerticalPosition.getList()"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </template>
    </el-form-item>
    <el-form-item v-show="labelSetting.show" label="内容格式">
      <template v-slot:label>
        <span>内容格式</span>
        <el-tooltip class="item" effect="dark" placement="right-start">
          <template #content>
            <div>
              字符串模版 模版变量有：<br />
              {a}：系列名。<br />
              {b}：数据名。<br />
              {c}：数据值。<br />
              {d}：百分比（用于饼图等）。
            </div>
          </template>
          <el-icon><InfoFilled /></el-icon>
        </el-tooltip>
      </template>
      <el-input v-model="labelSetting.formatter" type="textarea" :rows="4" />
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { InfoFilled } from '@element-plus/icons-vue';
import { ANY_OBJECT } from '@/types/generic';
import { SysCustomWidgetType } from '@/common/staticDict';
import { InsidePosition, VerticalPosition } from '@/common/staticDict/report';

const formConfig = inject('formConfig', () => {
  console.error('ChartLabelSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});

const widget = computed(() => {
  return formConfig().currentWidget;
});
const labelSetting = computed(() => {
  return widget.value ? (widget.value.props || {}).labelSetting : undefined;
});
</script>
