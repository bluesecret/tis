<template>
  <el-form-item v-show="formData.text" :label="textName + '文字'">
    <el-input v-model="formData.text" clearable />
  </el-form-item>
  <el-form-item v-if="formData.fontSize != undefined" :label="textName + '字体大小'">
    <el-slider v-model="formData.fontSize" :min="10" :max="40" size="default" />
  </el-form-item>
  <el-form-item v-show="formData.color" :label="textName + '字体颜色'">
    <el-color-picker v-model="formData.color" />
  </el-form-item>
  <el-form-item v-show="formData.textAlign" :label="textName + '水平位置'">
    <el-select v-model="formData.textAlign" placeholder="">
      <el-option label="左" value="left" />
      <el-option label="中" value="center" />
      <el-option label="右" value="right" />
    </el-select>
  </el-form-item>
  <el-form-item
    v-show="formData.bold != undefined && formData.italics != undefined"
    :label="textName + '字体样式'"
  >
    <el-checkbox v-show="formData.bold != undefined" v-model="formData.bold">粗体</el-checkbox>
    <el-checkbox v-show="formData.italics != undefined" v-model="formData.italics"
      >斜体</el-checkbox
    >
  </el-form-item>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';

const props = defineProps<{ textName: string; value: ANY_OBJECT }>();
const formData = ref<ANY_OBJECT>({});

watch(
  () => props.value,
  () => {
    console.log('props.value change');
    formData.value = props.value;
  },
  {
    immediate: true,
  },
);
</script>
