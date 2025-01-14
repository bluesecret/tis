<template>
  <el-form-item label="端点样式">
    <el-select v-model="formData.symbol" placeholder="" @change="onChange">
      <el-option
        v-for="item in ShapeType.getList()"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      />
    </el-select>
  </el-form-item>
  <el-form-item label="端点大小">
    <el-slider v-model="formData.symbolSize" :min="0" :max="10" @change="onChange" />
  </el-form-item>
  <el-form-item label="端点描边颜色">
    <el-color-picker v-model="formData.itemStyle.borderColor" @change="onChange" />
  </el-form-item>
  <el-form-item label="端点描边宽度">
    <el-input-number
      controls-position="right"
      clearable
      style="width: 100%"
      :min="0"
      v-model="formData.itemStyle.borderWidth"
      @change="onChange"
    />
  </el-form-item>
  <el-form-item label="高亮线条宽度">
    <el-input-number
      controls-position="right"
      clearable
      style="width: 100%"
      :min="0"
      v-model="formData.emphasis.lineStyle.width"
      @change="onChange"
    />
  </el-form-item>
  <el-form-item label="填充区域透明度">
    <el-slider v-model="formData.areaStyleOpacity" :min="0" :max="10" @change="onChange" />
  </el-form-item>
</template>

<script setup lang="ts">
import { getDefaultSerieRadar } from '@/components/Charts/chartOption';
import { ANY_OBJECT } from '@/types/generic';
import { ShapeType } from '@/common/staticDict/report';

const defaultRadarSetting = {
  ...getDefaultSerieRadar(),
};

const emit = defineEmits<{ 'update:value': [ANY_OBJECT] }>();
const props = defineProps<{ value: ANY_OBJECT }>();
const formData = ref<ANY_OBJECT>({
  ...defaultRadarSetting,
});

const onChange = () => {
  emit('update:value', formData.value);
};

watch(
  () => props.value,
  newValue => {
    formData.value = {
      ...defaultRadarSetting,
      ...newValue,
    };
  },
  {
    immediate: true,
  },
);
</script>
