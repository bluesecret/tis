<template>
  <div class="form-single-fragment" style="position: relative">
    <el-form
      ref="formModifyPassword"
      class="full-width-input"
      style="width: 100%"
      label-width="80px"
      :size="defaultFormItemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="上传文件">
            <el-upload
              class="upload-demo"
              name="uploadFile"
              drag
              :show-file-list="false"
              :action="headImageUploadUrl"
              accept=".xls,.xlsx"
              style="width: 100%"
              :headers="getUploadHeaders"
              :before-upload="beforeUoload"
              :http-request="uploadHttpRequest"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">Drop file here or <em>click to upload</em></div>
              <template #tip>
                <div class="el-upload__tip">请上传.xls,.xlsx类型文件</div>
              </template>
            </el-upload>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="">
            <div v-for="(item, index) in fileList" :key="index">
              <div>
                <span>{{ item.name }}</span>
                <span style="color: #f56c6c; cursor: pointer; margin-left: 10px" @click="toDelRow"
                  >删除</span
                >
              </div>
            </div>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" justify="end" class="dialog-btn-layer">
        <el-button :plain="true" @click="onCancel">取消</el-button>
        <el-button type="primary" @click="onSubmit"> 确定 </el-button>
      </el-row>
    </el-form>
  </div>
</template>
  
  <script setup lang="ts">
import { ElMessage, UploadFile } from 'element-plus';
import { ANY_OBJECT } from '@/types/generic';
import { useUpload } from '@/common/hooks/useUpload';
import { useLoginStore } from '@/store';
import LoginController from '@/api/system/LoginController';
import { UploadFilled } from '@element-plus/icons-vue';
import { TisDeviceInfoController } from '@/api/generated';
const { getUploadFileUrl, getUploadActionUrl, getUploadHeaders } = useUpload();
const loginStore = useLoginStore();
const userInfo = loginStore.userInfo;
const fileList: Array<any> = reactive([]);
import { DialogProp } from '@/components/Dialog/types';
const props = defineProps<{
  rowData?: any;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}>();

const onHeadImageUploadSuccess = (
  response: ANY_OBJECT,
  file: UploadFile,
  fileList: UploadFile[],
) => {
  console.log(file);
};

const beforeUoload = (file: any) => {
  console.log(file);

  var fileType = file.name.substring(file.name.lastIndexOf('.') + 1);
  const isFile = fileType == 'xls' || fileType == 'xlsx';
  console.log(isFile);

  if (!isFile) {
    ElMessage.warning('文件上传格式错误');
    return false;
  }
  fileList.push(file);

  console.log(fileList);
};

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  }
};

const onSubmit = () => {
  if (fileList.length == 0) {
    ElMessage.warning('请先上传文件');
    return;
  }
  let params = {
    uploadFile: fileList[0],
    asImage: false,
    fieldName: '',
  };
  let operation: Promise<ANY_OBJECT>;
  operation = TisDeviceInfoController.changeHeadImageUrl(params);
  operation
    .then(res => {
      ElMessage.success('上传成功');
      props.dialog.submit(res);
    })
    .catch(e => {
      console.warn(e);
    });
};

const toDelRow = () => {
  fileList.splice(0, 1);
};

const uploadHttpRequest = () => {};

const headImageUploadUrl = computed(() => {
  return '';
});
</script>
  
  <style></style>
  