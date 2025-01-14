<template>
  <el-form-item label="堆叠">
    <el-switch v-model="formData.stack" @change="onChange" />
  </el-form-item>
  <el-form-item label="条形显示">
    <el-switch v-model="formData.lateral" @change="onChange" />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="自适应">
    <el-switch v-model="formData.autoWidth" @change="onChange" />
  </el-form-item>
  <el-form-item class="view-attribute-item slider-item" label="柱宽">
    <el-slider
      v-model="formData.barWidth"
      style="width: 100%"
      :disabled="formData.autoWidth"
      :min="1"
      :max="50"
      @change="onChange"
    />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="柱背景色">
    <el-row style="width: 100%" justify="space-between" align="middle">
      <el-switch v-model="formData.showBackground" @change="onChange" />
      <el-color-picker
        :disabled="!formData.showBackground"
        v-model="formData.backgroundStyle.color"
        @change="onChange"
      />
    </el-row>
  </el-form-item>
  <el-form-item class="view-attribute-item slider-item" label="背景色透明度">
    <el-slider
      v-model="formData.backgroundStyle.opacity"
      :disabled="!formData.showBackground"
      style="width: 100%"
      :min="0"
      :max="1"
      :step="0.05"
      @change="onChange"
    />
  </el-form-item>
</template>

<script setup lang="ts">
import { getDefaultSerieBar } from '@/components/Charts/chartOption';
import { ANY_OBJECT } from '@/types/generic';

const emit = defineEmits<{ 'update:value': [ANY_OBJECT] }>();
const props = defineProps<{ value: ANY_OBJECT }>();
const formData = ref<ANY_OBJECT>({
  ...getDefaultSerieBar(),
});

const onChange = () => {
  emit('update:value', formData.value);
};

watch(
  () => props.value,
  newValue => {
    formData.value = {
      ...getDefaultSerieBar(),
      ...newValue,
    };
  },
  {
    immediate: true,
  },
);
</script>
