<template>
  <el-form
    label-position="top"
    size="default"
    v-if="axixSetting"
    style="padding: 10px 16px 0"
    @submit.prevent
  >
    <el-form-item label="是否显示">
      <el-switch v-model="axixSetting.show" />
    </el-form-item>
    <el-form-item v-show="axixSetting.show" label="位置">
      <el-radio-group v-if="axisType === DirectionType.HORIZONTAL" v-model="axixSetting.position">
        <el-radio-button
          v-for="item in HorizontalAxisPostion.getList()"
          :key="item.id"
          :label="item.id"
          >{{ item.name }}</el-radio-button
        >
      </el-radio-group>
      <el-radio-group v-else v-model="axixSetting.position">
        <el-radio-button
          v-for="item in VerticalAxisPosition.getList()"
          :key="item.id"
          :label="item.id"
          >{{ item.name }}</el-radio-button
        >
      </el-radio-group>
    </el-form-item>
    <el-form-item v-show="axixSetting.show" label="名称">
      <el-input v-model="axixSetting.name" clearable />
    </el-form-item>
    <el-form-item v-show="axixSetting.show" label="名称颜色">
      <el-color-picker v-model="axixSetting.nameTextStyle.color" />
    </el-form-item>
    <el-form-item v-show="axixSetting.show" label="名称字号">
      <el-slider v-model="axixSetting.nameTextStyle.fontSize" :min="10" :max="40" size="default" />
    </el-form-item>
    <el-divider v-if="axixSetting.show && axisType === DirectionType.VERTICAL" />
    <!-- 轴值 -->
    <div v-if="axixSetting.show && axisType === DirectionType.VERTICAL">
      <el-form-item class="view-attribute-item" label="轴值">
        <template v-slot:label>
          <span>轴值</span>
          <el-tooltip class="item" effect="dark" placement="right-start">
            <template #content>
              <div>
                最小值、最大值、间隔均为数值类型；若不填，则该项视为自动。<br />
                请确保填写数值能正确计算，否则将无法正常显示轴值。
              </div>
            </template>
            <el-icon><InfoFilled /></el-icon>
          </el-tooltip>
        </template>
        <el-checkbox v-model="axixSetting.yAxisValueAuto">自动</el-checkbox>
      </el-form-item>
      <el-form-item v-show="!axixSetting.yAxisValueAuto" label="最小值">
        <el-input v-model="axixSetting.min" />
      </el-form-item>
      <el-form-item v-show="!axixSetting.yAxisValueAuto" label="最大值">
        <el-input v-model="axixSetting.max" />
      </el-form-item>
      <el-form-item v-show="!axixSetting.yAxisValueAuto" label="间隔">
        <el-input v-model="axixSetting.interval" />
      </el-form-item>
    </div>
    <el-divider v-if="axixSetting.show" />
    <!-- 轴线 -->
    <div v-show="axixSetting.show">
      <el-form-item label="轴线显示">
        <el-switch v-model="axixSetting.splitLine.show" />
      </el-form-item>
      <el-form-item v-show="axixSetting.splitLine.show" label="轴线颜色">
        <el-color-picker v-model="axixSetting.splitLine.lineStyle.color" />
      </el-form-item>
      <el-form-item v-show="axixSetting.splitLine.show" label="轴线宽度">
        <el-slider
          v-model="axixSetting.splitLine.lineStyle.width"
          :min="1"
          :max="10"
          size="default"
        />
      </el-form-item>
      <el-form-item v-show="axixSetting.splitLine.show" label="轴线类型">
        <el-radio-group v-model="axixSetting.splitLine.lineStyle.type">
          <el-radio-button v-for="item in LineType.getList()" :key="item.id" :value="item.id">{{
            item.name
          }}</el-radio-button>
        </el-radio-group>
      </el-form-item>
    </div>
    <el-divider v-if="axixSetting.show" />
    <!-- 标签 -->
    <div v-show="axixSetting.show">
      <el-form-item label="标签显示">
        <el-switch v-model="axixSetting.axisLabel.show" />
      </el-form-item>
      <el-form-item v-show="axixSetting.axisLabel.show" label="标签颜色">
        <el-color-picker v-model="axixSetting.axisLabel.color" />
      </el-form-item>
      <el-form-item v-show="axixSetting.axisLabel.show" label="标签角度">
        <el-slider v-model="axixSetting.axisLabel.rotate" :min="-90" :max="90" size="default" />
      </el-form-item>
      <el-form-item v-show="axixSetting.axisLabel.show" label="标签字号">
        <el-slider v-model="axixSetting.axisLabel.fontSize" :min="10" :max="40" size="default" />
      </el-form-item>
    </div>
    <el-divider v-if="axixSetting.show" />
    <el-form-item v-show="axixSetting.show" label="内容格式">
      <el-input v-model="axixSetting.axisLabel.formatter" type="textarea" :rows="4" />
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { InfoFilled } from '@element-plus/icons-vue';
import { DirectionType } from '@/common/staticDict/index';
import { ANY_OBJECT } from '@/types/generic';
import { LineType, VerticalAxisPosition, HorizontalAxisPostion } from '@/common/staticDict/report';

const props = withDefaults(defineProps<{ axisType?: number }>(), {
  axisType: DirectionType.HORIZONTAL,
});

const formConfig = inject('formConfig', () => {
  console.error('ChartAxisSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});

const widget = computed(() => {
  return formConfig().currentWidget;
});
const fieldName = computed(() => {
  return props.axisType === DirectionType.HORIZONTAL ? 'xAxisSetting' : 'yAxisSetting';
});
const axixSetting = computed(() => {
  return widget.value ? (widget.value.props || {})[fieldName.value] : undefined;
});
</script>
