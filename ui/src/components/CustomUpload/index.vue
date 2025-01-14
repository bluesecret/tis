<template>
  <div class="custom-upload">
    <template v-if="type === 'dropdown'">
      <el-dropdown
        :size="size"
        split-button
        type="default"
        trigger="click"
        :class="{ 'hide-list': fileList.length <= 0 }"
      >
        <el-upload
          :name="name"
          style="width: 100%"
          :headers="headers"
          :action="action"
          :data="data"
          :limit="limit"
          :disabled="disabled"
          :multiple="multiple"
          :accept="accept"
          :show-file-list="false"
          :file-list="fileList"
          :before-upload="beforeUpload"
          :before-remove="beforeRemove"
          :on-exceed="onUploadLimit"
          :on-success="onUploadSuccess"
          :on-remove="onRemoveFile"
          :on-error="onUploadError"
        >
          <el-row type="flex" align="middle">
            <span class="upload-btn">
              上传文件
              <div class="file-count" v-if="uploadWidget.fileList.length > 0">
                {{ fileList.length }}
              </div>
            </span>
          </el-row>
        </el-upload>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-for="(item, index) in fileList" :key="item.url">
              <el-row
                class="dropdown-file-item"
                type="flex"
                justify="space-between"
                align="middle"
                style="height: 36px"
              >
                <span @click="onPreviewClick(item, fileList)" style="flex-grow: 1">
                  {{ item.name }}
                </span>
                <el-icon
                  v-if="!disabled"
                  style="margin-left: 8px"
                  @click.stop="onRemoveFile(item, uploadWidget.fileList.value)"
                >
                  <CircleCloseFilled />
                </el-icon>
              </el-row>
              <div v-if="index !== fileList.length - 1" style="height: 1px; background: #eeeff1" />
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </template>
    <el-upload
      v-else-if="type === 'expand'"
      class="expand-upload"
      :class="itemType"
      :name="name"
      style="width: 100%"
      :headers="headers"
      :action="action"
      :data="data"
      :limit="limit"
      :disabled="disabled || fileList.length >= limit"
      :multiple="multiple"
      :accept="accept"
      :show-file-list="false"
      :file-list="fileList"
      :before-upload="beforeUpload"
      :before-remove="beforeRemove"
      :on-exceed="onUploadLimit"
      :on-success="onUploadSuccess"
      :on-remove="onRemoveFile"
      :on-error="onUploadError"
    >
      <el-row v-if="itemType === 'card'" type="flex" style="flex-wrap: wrap">
        <div ref="uploadBtn" style="width: 0px; height: 0px; border: none" />
      </el-row>
      <div v-else class="upload-file-list">
        <!-- 文本样式 -->
        <el-button :size="size" :disabled="disabled || fileList.length >= limit" type="primary">
          点击上传
        </el-button>
      </div>
    </el-upload>
    <el-row v-if="type === 'expand' && itemType === 'card'" type="flex" style="flex-wrap: wrap">
      <!-- 卡片样式 -->
      <UploadFileList
        :supportPreview="supportPreview"
        :supportDownload="supportDownload"
        :file-list="fileList"
        :type="itemType"
        :direction="direction"
        :supportAdd="showCardAddBtn"
        @remove="onDeleteFile"
        @add="onAddFile"
      >
        <div v-if="showCardAddBtn">
          <el-icon style="font-size: 18px">
            <Plus />
          </el-icon>
        </div>
      </UploadFileList>
    </el-row>
    <UploadFileList
      v-if="type === 'expand' && itemType === 'text'"
      :supportPreview="supportPreview"
      :supportDownload="supportDownload"
      :file-list="fileList"
      :type="itemType"
      :direction="direction"
      style="margin-top: 8px"
      @remove="onDeleteFile"
    />
    <PreviewDialog
      v-if="showPreview"
      :visible="showPreview"
      :file-list="fileList"
      :defaultIndex="fileIndex"
      :readonly="!supportDownload"
      @close="showPreview = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { defineProps, withDefaults, defineEmits } from 'vue';
import { Plus, CircleCloseFilled } from '@element-plus/icons-vue';
import { ANY_OBJECT } from '@/types/generic';
import { useDownload } from '@/common/hooks/useDownload';
import { useUpload } from '@/common/hooks/useUpload';
import { useUploadWidget } from '@/common/hooks/useUploadWidget';
import UploadFileList from '@/components/UploadFileList/index.vue';
import PreviewDialog from '@/components/Preview/Dialog.vue';
import { getFileType } from '@/common/utils';

type UploadFiles = UploadFile[];
type UploadUserFile = Omit<UploadFile, 'status' | 'uid'> &
  Partial<Pick<UploadFile, 'status' | 'uid'>>;
type UploadStatus = 'ready' | 'uploading' | 'success' | 'fail';
type Awaitable<T> = Promise<T> | T;
type Mutable<T> = { -readonly [P in keyof T]: T[P] };
interface UploadRawFile extends File {
  uid: number;
}

interface UploadFile {
  name: string;
  percentage?: number;
  status: UploadStatus;
  size?: number;
  response?: unknown;
  uid: number;
  url?: string;
  raw?: UploadRawFile;
}

const { downloadFile } = useDownload();
const { getUploadHeaders, getUploadActionUrl, parseUploadData, fileListToJson } = useUpload();

const props = withDefaults(
  defineProps<{
    modelValue?: Array<{ url: string; name: string; type: string }>;
    size: string;
    type: string;
    itemType: string;
    direction: string;
    supportPreview: boolean;
    supportDownload: boolean;
    limit: number;
    disabled: boolean;
    action: string;
    headers: ANY_OBJECT;
    multiple: boolean;
    data: ANY_OBJECT;
    name: string;
    accept?: string;
    beforeUpload?: (
      rawFile: UploadRawFile,
    ) => Awaitable<void | undefined | null | boolean | File | Blob>;
    beforeRemove?: (uploadFile: UploadFile, uploadFiles: UploadFiles) => Awaitable<boolean>;
    onExceed?: (files: File[], uploadFiles: UploadUserFile[]) => void;
  }>(),
  {
    modelValue: () => [],
    size: 'default',
    type: 'dropdown',
    itemType: 'card',
    direction: 'vertical',
    supportPreview: true,
    supportDownload: true,
    limit: 0,
    disabled: false,
    action: '',
    headers: {},
    multiple: false,
    data: {},
    name: 'file',
  },
);

const showPopover = ref(false);
const showPreview = ref(false);
const fileIndex = ref(0);
const uploadWidget = useUploadWidget(props.limit);
const fileList = computed(() => {
  return (props.modelValue || []).map(item => {
    return {
      ...item,
      type: getFileType(item.name),
    };
  });
});

const showCardAddBtn = computed(() => {
  let temp =
    props.itemType === 'card' &&
    (props.limit === 0 || props.limit == null || fileList.value.length < props.limit);
  console.log('temp', temp);
  return temp;
});

const emit = defineEmits(['update:modelValue', 'error', 'success', 'remove', 'change']);

const onPreviewClick = (file, fileList) => {
  if (props.supportPreview) {
    fileIndex.value = fileList.findIndex(item => item.url === file.url);
    nextTick(() => {
      showPreview.value = true;
    });
  } else if (props.supportDownload) {
    downloadFile(file.url, file.name);
  }
};

const onUploadSuccess = (response, file, fileList) => {
  if (response.success) {
    file.downloadUri = response.data.downloadUri;
    file.filename = response.data.filename;
    file.uploadPath = response.data.uploadPath;
    file.url = URL.createObjectURL(file.raw);
    uploadWidget.onFileChange(file, fileList);
    console.log('uploadWidget.fileList', uploadWidget.fileList.value);
    onChange(uploadWidget.fileList.value);
    emit('success', response, file, fileList);
  } else {
    ElMessage.error(response.message);
    emit('error', response, file, fileList);
  }
};

const onRemoveFile = (file, fileList) => {
  if (file != null && fileList != null) {
    fileList = fileList.filter(item => item.url !== file.url);
  }

  uploadWidget.onFileChange(file, fileList);
  onChange(uploadWidget.fileList.value);
  emit('remove', file, fileList);
};

const onUploadError = (e, file, fileList) => {
  ElMessage.error('文件上传失败');
};

const onUploadLimit = (file, fileList) => {
  if (props.onExceed && typeof props.onExceed === 'function') {
    props.onExceed(file, fileList);
  } else {
    ElMessage.error('已经超出最大上传个数限制');
  }
};

const onDeleteFile = (file, fileList) => {
  uploadWidget.onFileChange(file, fileList);
  onChange(uploadWidget.fileList.value);
  emit('remove', file, fileList);
};

const uploadBtn = ref(null);
const onAddFile = () => {
  if (uploadBtn.value) {
    uploadBtn.value.click();
  }
};

const onChange = value => {
  emit('update:modelValue', value);
  emit('change', value);
};

watch(
  () => props.modelValue,
  val => {
    uploadWidget.fileList.value = val;
  },
  { immediate: true },
);
</script>

<style lang="scss" scoped>
.custom-upload .el-dropdown ::v-deep .el-button-group .el-button:first-child {
  padding: 0px;
}

.upload-btn {
  display: flex;
  align-items: center;
  padding: 9px 16px;
}
.upload-btn .file-count {
  height: 12px;
  line-height: 12px;
  min-width: 12px;
  color: white;
  font-size: 12px;
  background: #f56c6c;
  border-radius: 6px;
  padding: 0px 3px;
  margin-left: 5px;
}
.hide-list ::v-deep .el-dropdown__caret-button {
  display: none;
}
.dropdown-file-item {
  width: 100%;
  min-width: 200px;
  height: 36px;
  line-height: 36px;
}

.dropdown-file-item span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.expand-upload.card {
  height: 0px !important;
}

::v-deep .el-dropdown-menu__item {
  padding: 0px 12px;
}
::v-deep .el-dropdown-menu__item:not(.is-disabled):hover,
::v-deep .el-dropdown-menu__item:not(.is-disabled):hover i,
::v-deep .el-dropdown-menu__item:focus,
::v-deep .el-dropdown-menu__item:focus i {
  background-color: white;
  color: $color-primary;
}
::v-deep .el-dropdown-menu__item i {
  color: #d8d8d8;
}
</style>
