<template>
  <el-form-item class="view-attribute-item" label="南丁格尔图">
    <el-switch v-model="formData.isRose" @change="onChange" />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="中心横坐标">
    <el-input placeholder="" v-model="formData.centerX" @input="onCenterXChange" @change="onChange">
      <template v-slot:append>%</template>
    </el-input>
  </el-form-item>
  <el-form-item class="view-attribute-item" label="中心纵坐标">
    <el-input placeholder="" v-model="formData.centerY" @input="onCenterYChange" @change="onChange">
      <template v-slot:append>%</template>
    </el-input>
  </el-form-item>
  <el-form-item class="view-attribute-item" label="内径">
    <el-input placeholder="" v-model="formData.radiusInner" @change="onChange" />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="外径">
    <el-input placeholder="" v-model="formData.radiusOuter" @change="onChange" />
  </el-form-item>
</template>

<script setup lang="ts">
import { getDefaultSeriePie } from '@/components/Charts/chartOption';
import { ANY_OBJECT } from '@/types/generic';

const defaultPieChartSetting = {
  ...getDefaultSeriePie(),
};

const emit = defineEmits<{ 'update:value': [ANY_OBJECT] }>();
const props = defineProps<{ value: ANY_OBJECT }>();
const formData = ref<ANY_OBJECT>({
  ...defaultPieChartSetting,
});

const onChange = () => {
  emit('update:value', formData.value);
};

const onCenterXChange = (val: string) => {
  const data: ANY_OBJECT = { ...formData.value };
  data.centerX = val.replace(/[^\d]/g, '');
  formData.value = data;
};
const onCenterYChange = (val: string) => {
  const data: ANY_OBJECT = { ...formData.value };
  data.centerY = val.replace(/[^\d]/g, '');
  formData.value = data;
};

watch(
  () => props.value,
  newValue => {
    formData.value = {
      ...defaultPieChartSetting,
      ...newValue,
    };
  },
  {
    immediate: true,
  },
);
</script>
