<template>
  <el-form label-position="top" size="default" @submit.prevent>
    <el-form-item label="最小值">
      <el-input
        type="number"
        v-model="formData.min"
        @input="val => onInput('min', val)"
        @change="onChange"
      ></el-input>
    </el-form-item>
    <el-form-item label="最大值">
      <el-input
        type="number"
        v-model="formData.max"
        @input="val => onInput('max', val)"
        @change="onChange"
      ></el-input>
    </el-form-item>
    <el-form-item label="起始角度">
      <el-slider v-model="formData.startAngle" :min="-360" :max="360" @change="onChange" />
    </el-form-item>
    <el-form-item label="结束角度">
      <el-slider v-model="formData.endAngle" :min="-360" :max="360" @change="onChange" />
    </el-form-item>
    <el-form-item label="缩放 %">
      <el-slider v-model="radius" :min="20" :max="100" @change="onChange" />
    </el-form-item>
    <el-form-item label="显示位置(X)">
      <el-slider v-model="formData.centerX" :min="0" :max="100" @change="onChange" />
    </el-form-item>
    <el-form-item label="显示位置(Y)">
      <el-slider v-model="formData.centerY" :min="0" :max="100" @change="onChange" />
    </el-form-item>
    <el-form-item label="分割段数">
      <el-slider v-model="formData.splitNumber" :min="0" :max="100" @change="onChange" />
    </el-form-item>
    <el-form-item label="仪表颜色配色">
      <el-color-picker v-model="lineStyleColor" @change="onChange" />
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { getDefaultSerieGauge } from '@/components/Charts/chartOption';
import { ANY_OBJECT } from '@/types/generic';

const defaultGaugeSetting = {
  ...getDefaultSerieGauge(),
};

const emit = defineEmits<{ 'update:value': [ANY_OBJECT] }>();
const props = defineProps<{ value: ANY_OBJECT }>();

const formData = ref<ANY_OBJECT>({
  ...defaultGaugeSetting,
  ...props.value,
  centerX: 50,
  centerY: 50,
});
const radius = computed({
  get: function () {
    return Number(formData.value.radius.replace('%', ''));
  },
  set: function (val) {
    formData.value.radius = val + '%';
  },
});
const lineStyleColor = computed({
  get: function () {
    return formData.value.axisLine.lineStyle.color[0][1];
  },
  set: function (val) {
    formData.value.axisLine.lineStyle.color[0][1] = val;
  },
});

const onInput = (name: string, val: string) => {
  let reg = /^[0-9]*$/;
  if (reg.test(val)) {
    formData.value[name] = val;
  }
};
const onChange = () => {
  formData.value.center = [formData.value.centerX + '%', formData.value.centerY + '%'];

  emit('update:value', formData.value);
};

watch(
  () => props.value,
  newValue => {
    formData.value = {
      ...defaultGaugeSetting,
      ...newValue,
    };
  },
  {
    immediate: true,
  },
);
</script>
