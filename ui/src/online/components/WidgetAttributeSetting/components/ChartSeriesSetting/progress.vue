<template>
  <el-form-item class="view-attribute-item" label="进度条最大值">
    <el-input-number
      controls-position="right"
      clearable
      :min="0"
      style="width: 100%"
      v-model="formData.maxValue"
      @change="onChange"
    />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="进度条颜色">
    <el-color-picker v-model="formData.color" @change="onChange" />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="进度条底色">
    <el-color-picker v-model="formData.defineBackColor" @change="onChange" />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="进度条宽度">
    <el-slider
      style="width: 95%; margin-left: 5px"
      v-model="formData.strokeWidth"
      :min="0"
      :max="50"
      size="default"
      @change="onChange"
    />
  </el-form-item>
  <el-form-item v-if="formData.width" class="view-attribute-item" label="进度条直径">
    <el-input-number
      controls-position="right"
      clearable
      :min="5"
      style="width: 100%"
      v-model="formData.width"
      @change="onChange"
    />
  </el-form-item>
  <el-form-item v-if="formData.strokeLinecap" class="view-attribute-item" label="进度条两端形状">
    <el-radio-group v-model="formData.strokeLinecap" @change="onChange">
      <el-radio-button value="round">圆形</el-radio-button>
      <el-radio-button value="square">方形</el-radio-button>
    </el-radio-group>
  </el-form-item>
  <el-form-item
    v-show="formData.showText != undefined"
    class="view-attribute-item"
    label="显示文字"
  >
    <el-switch v-model="formData.showText" @change="onChange" />
  </el-form-item>
  <template v-if="formData.showText">
    <el-form-item
      v-show="formData.textInside != undefined"
      class="view-attribute-item"
      label="文字位置"
    >
      <el-radio-group v-model="formData.textInside" @change="onChange">
        <el-radio-button :value="true">内部</el-radio-button>
        <el-radio-button :value="false">外部</el-radio-button>
      </el-radio-group>
    </el-form-item>
  </template>
  <el-form-item v-if="formData.textStyle.width" class="view-attribute-item" label="文字宽度">
    <el-input-number
      controls-position="right"
      clearable
      :min="5"
      style="width: 100%"
      v-model="formData.textStyle.width"
      @change="onChange"
    />
  </el-form-item>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';

const emit = defineEmits<{ 'update:value': [ANY_OBJECT] }>();
const props = defineProps<{ value: ANY_OBJECT }>();
const formData = ref<ANY_OBJECT>({});

const onChange = () => {
  emit('update:value', formData.value);
};

watch(
  () => props.value,
  () => {
    formData.value = props.value;
  },
  {
    immediate: true,
  },
);
</script>
