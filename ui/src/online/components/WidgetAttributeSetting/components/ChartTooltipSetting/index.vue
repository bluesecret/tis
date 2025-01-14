<template>
  <el-form
    label-position="top"
    size="default"
    v-if="tooltipSetting"
    style="padding: 10px 16px 0"
    @submit.prevent
  >
    <el-form-item label="是否显示">
      <el-switch v-model="tooltipSetting.show" />
    </el-form-item>
    <el-form-item v-show="tooltipSetting.show" label="字体大小">
      <el-slider v-model="tooltipSetting.textStyle.fontSize" :min="10" :max="40" size="default" />
    </el-form-item>
    <el-form-item v-show="tooltipSetting.show" label="字体颜色">
      <el-color-picker v-model="tooltipSetting.textStyle.color" />
    </el-form-item>
    <template v-if="showTrigger">
      <el-form-item v-show="tooltipSetting.show" label="触发位置">
        <el-select v-model="tooltipSetting.trigger" placeholder="" style="width: 100%">
          <el-option
            v-for="item in TipsTriggerType.getList()"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
    </template>
    <el-form-item v-show="tooltipSetting.show" label="内容格式">
      <template v-slot:label>
        <span>内容格式</span>
        <el-tooltip class="item" effect="dark" placement="right-start">
          <template #content>
            <div>
              模版变量有 {a}, {b}，{c}，{d}，分别表示系列名，数据名，数据值等。<br />
              在 触发位置 为 '坐标轴' 的时候，会有多个系列的数据，此时可以通过 {a0}, {a1}, {a2}
              这种后面加索引的方式表示系列的索引。<br />
              不同图表类型下的 {a}，{b}，{c}，{d} 含义不一样。 其中变量{a}, {b}, {c},
              {d}在不同图表类型下代表数据含义为：<br />
              折线（区域）图、柱状（条形）图、仪表盘 :
              {a}（系列名称），{b}（类目值），{c}（数值）<br />
              饼图、漏斗图: {a}（系列名称），{b}（数据项名称），{c}（数值）, {d}（百分比）
            </div>
          </template>
          <el-icon><InfoFilled /></el-icon>
        </el-tooltip>
      </template>
      <el-input v-model="tooltipSetting.formatter" type="textarea" :rows="4" />
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { InfoFilled } from '@element-plus/icons-vue';
import { ANY_OBJECT } from '@/types/generic';
import { SysCustomWidgetType } from '@/common/staticDict';
import { TipsTriggerType } from '@/common/staticDict/report';

const formConfig = inject('formConfig', () => {
  console.error('ChartTooltipSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});

const widget = computed(() => {
  return formConfig().currentWidget;
});
const tooltipSetting = computed(() => {
  return widget.value ? (widget.value.props || {}).tooltipSetting : undefined;
});
const showTrigger = computed(() => {
  return ![
    SysCustomWidgetType.FunnelChart,
    SysCustomWidgetType.RadarChart,
    SysCustomWidgetType.GaugeChart,
  ].find(x => x === widget.value.widgetType);
});
</script>
