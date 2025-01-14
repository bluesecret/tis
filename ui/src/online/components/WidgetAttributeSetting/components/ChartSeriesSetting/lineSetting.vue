<template>
  <el-form-item label="堆叠">
    <el-switch v-model="formData.stack" @change="onChange" />
  </el-form-item>
  <el-form-item label="面积">
    <el-switch v-model="formData.areaStyle" @change="onChange" />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="阶梯折线">
    <el-select v-model="formData.step" @change="onStepChange">
      <el-option label="不支持" value="" />
      <el-option label="当前点转弯" value="start" />
      <el-option label="中间转弯" value="middle" />
      <el-option label="结束点转弯" value="end" />
    </el-select>
  </el-form-item>
  <el-form-item class="view-attribute-item" label="平滑折线">
    <el-switch v-model="formData.smooth" :disabled="!!formData.step" @change="onChange" />
  </el-form-item>
  <el-form-item class="view-attribute-item slider-item" label="线宽">
    <el-slider
      v-model="formData.lineStyle.width"
      style="width: 100%"
      :min="1"
      :max="10"
      @change="onChange"
    />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="线型">
    <el-radio-group v-model="formData.lineStyle.type" @change="onChange">
      <el-radio-button v-for="item in LineType.getList()" :key="item.id" :value="item.id">{{
        item.name
      }}</el-radio-button>
    </el-radio-group>
  </el-form-item>
  <el-form-item class="view-attribute-item" label="折点">
    <el-select v-model="formData.symbol" placeholder="" style="width: 100%" @change="onChange">
      <el-option
        v-for="item in ShapeType.getList()"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      />
    </el-select>
  </el-form-item>
  <el-form-item class="view-attribute-item slider-item" label="折点大小">
    <el-slider
      v-model="formData.symbolSize"
      style="width: 100%"
      :min="1"
      :max="20"
      @change="onChange"
    />
  </el-form-item>
</template>

<script setup lang="ts">
import { ShapeType, LineType } from '@/common/staticDict/report';
import { getDefaultSerieLine } from '@/components/Charts/chartOption';
import { ANY_OBJECT } from '@/types/generic';

const emit = defineEmits<{ 'update:value': [ANY_OBJECT] }>();
const props = defineProps<{ value: ANY_OBJECT }>();
const formData = ref<ANY_OBJECT>({
  ...getDefaultSerieLine(),
});

const onChange = () => {
  emit('update:value', formData.value);
};

const onStepChange = (val: string) => {
  if (val != null) formData.value.smooth = false;
  onChange();
};

watch(
  () => props.value,
  newValue => {
    formData.value = {
      ...getDefaultSerieLine(),
      ...newValue,
    };
  },
  {
    immediate: true,
  },
);
</script>
