<template>
  <el-form-item v-show="formData.icon != undefined" :label="textName + '上传'">
    <div style="display: flex; align-items: center">
      <el-upload
        class="upload-demo"
        action=""
        :on-remove="handleRemove"
        :before-upload="beforeUpload"
        :limit="1"
        :show-file-list="false"
      >
        <el-button size="default" type="primary">点击上传</el-button>
      </el-upload>
      <img
        v-if="formData.icon"
        :src="formData.icon"
        style="width: 50px; height: 50px; margin-left: 20px; border-radius: 50%"
      />
    </div>
  </el-form-item>
  <el-form-item class="view-attribute-item" label="图标大小">
    <el-input-number
      controls-position="right"
      clearable
      :min="0"
      style="width: 100%"
      v-model="formData.iconSize"
    />
  </el-form-item>
  <el-form-item class="view-attribute-item" label="圆角">
    <el-input-number
      controls-position="right"
      clearable
      :min="0"
      :max="formData.iconSize / 2"
      style="width: 100%"
      v-model="formData.iconRadius"
    />
  </el-form-item>
</template>

<script setup lang="ts">
import { UploadRawFile } from 'element-plus/es/components/upload';
import { fileToBase64 } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';

const emit = defineEmits<{ 'update:value': [ANY_OBJECT] }>();
const props = defineProps<{ value: ANY_OBJECT; textName: string }>();
const formData = ref<ANY_OBJECT>({});

const beforeUpload = (file: UploadRawFile) => {
  return new Promise<void | undefined | null | boolean | File | Blob>((resolve, reject) => {
    fileToBase64(file)
      .then(base64 => {
        formData.value.icon = base64;
        resolve(true);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const handleRemove = () => {
  formData.value = {};
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
