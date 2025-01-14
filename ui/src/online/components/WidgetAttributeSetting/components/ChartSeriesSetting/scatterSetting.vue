<template>
  <el-form-item class="view-attribute-item" label="气泡类型">
    <el-select v-model="formData.symbolType" placeholder="" @change="onChange">
      <el-option
        v-for="item in ScatterSymbolType.getList()"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      />
    </el-select>
  </el-form-item>
  <el-form-item class="view-attribute-item slider-item" label="气泡大小">
    <el-slider
      v-model="formData.itemSize"
      :min="1"
      :max="50"
      @change="onChange"
      style="width: 100% !important"
    />
  </el-form-item>
</template>

<script setup lang="ts">
import { ScatterSymbolType } from '@/common/staticDict';
import { ANY_OBJECT } from '@/types/generic';

const defaultScatterChartSetting = {
  shape: 1,
  size: 20,
};

const emit = defineEmits<{ 'update:value': [ANY_OBJECT] }>();
const props = defineProps<{ value: ANY_OBJECT }>();
const formData = ref<ANY_OBJECT>({
  ...defaultScatterChartSetting,
});

const onChange = () => {
  emit('update:value', formData.value);
};

watch(
  () => props.value,
  newValue => {
    formData.value = {
      ...defaultScatterChartSetting,
      ...newValue,
    };
  },
  {
    immediate: true,
  },
);
</script>
