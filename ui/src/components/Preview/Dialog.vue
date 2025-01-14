<template>
  <el-dialog
    width="80vw"
    :modelValue="visible"
    top="15vh"
    :append-to-body="true"
    class="dialog-preview"
    :show-close="false"
    :destroy-on-close="true"
  >
    <div class="preview-dialog">
      <div class="preview-content">
        <el-carousel
          ref="carousel"
          :initial-index="defaultIndex"
          height="100%"
          :autoplay="false"
          :arrow="getFileList.length > 1 ? 'hover' : 'never'"
          style="display: flex; height: 100%"
          :style="{ width: ready ? '100%' : '0px' }"
          indicator-position="none"
        >
          <el-carousel-item v-for="(file, index) in getFileList" :key="file.url">
            <div
              style="
                height: 100%;
                display: flex;
                justify-content: center;
                align-items: center;
                flex-direction: column;
                padding: 0px 150px;
              "
            >
              <div class="file-item">
                <div class="file-header">
                  <span>{{ file.name }}</span>
                  <el-icon style="cursor: pointer" @click="closeDialog">
                    <Close />
                  </el-icon>
                </div>
                <div class="preview-box">
                  <template v-if="file.error">
                    <div
                      class="error-box"
                      style="
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        flex-direction: column;
                        height: 100%;
                        width: 100%;
                      "
                    >
                      <img :src="fileErrorImg" alt="error" />
                      <span>加载失败</span>
                    </div>
                  </template>
                  <template v-else>
                    <div style="width: 100%; height: 100%">
                      <el-image
                        :src="file.url"
                        style="width: 100%; height: 100%"
                        fit="contain"
                        v-if="file.type === EnumFileType.IMAGE"
                      >
                        <template #error>
                          <div
                            class="error-box"
                            style="
                              display: flex;
                              justify-content: center;
                              align-items: center;
                              flex-direction: column;
                              height: 100%;
                              width: 100%;
                            "
                          >
                            <img :src="fileErrorImg" alt="error" />
                            <span>加载失败</span>
                          </div>
                        </template>
                      </el-image>
                      <template v-else-if="getComponent(file) != null">
                        <component
                          style="width: 100%; height: 100%"
                          :is="getComponent(file)"
                          :file-url="file.url"
                          :time="index === defaultIndex ? 10 : 10"
                          @error="err => onPreviewError(file, err)"
                        />
                      </template>
                      <div
                        v-else
                        class="error-box"
                        style="
                          display: flex;
                          justify-content: center;
                          align-items: center;
                          flex-direction: column;
                          height: 100%;
                          width: 100%;
                        "
                      >
                        <img :src="fileErrorImg" alt="error" />
                        <span>暂不支持预览</span>
                      </div>
                    </div>
                  </template>
                </div>
              </div>
              <div class="menu-box" v-if="!readonly">
                <!--
                <el-button
                  type="default"
                  size="small"
                  style="width: 80px"
                  @click="onDeleteFile(file)"
                >
                  删除
                </el-button>
                -->
                <slot />
                <el-button
                  type="primary"
                  size="default"
                  style="width: 80px"
                  @click="onDownloadFile(file)"
                >
                  下载
                </el-button>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { Plus, Close } from '@element-plus/icons-vue';
import { defineProps, defineEmits, withDefaults } from 'vue';
import PdfPreview from './Pdf.vue';
import WordPreview from './Word.vue';
import ExcelPreview from './Excel.vue';
import VideoPreview from './Video.vue';
import { EnumFileType } from '@/common/staticDict/index';
import fileErrorImg from '@/assets/img/file-error.png';
import { useDownload } from '@/common/hooks/useDownload';
import { getFileType } from '@/common/utils';

const props = withDefaults(
  defineProps<{
    visible: boolean;
    fileList: Array<{ url: string; name: string; type: string }>;
    defaultIndex: number;
    readonly: boolean;
  }>(),
  {
    visible: false,
    fileList: () => [],
    defaultIndex: 0,
    readonly: false,
  },
);

const emit = defineEmits(['close', 'delete', 'download']);
const { downloadFile } = useDownload();
const ready = ref(false);
const getFileList = ref([]);

const onPreviewError = (file, err) => {
  file.error = true;
  getFileList.value = [...getFileList.value];
};

const closeDialog = () => {
  emit('close');
};

const getFileTypeByName = file => {
  if (file && file.type) {
    return file.type;
  } else {
    return getFileType(file.name);
  }
};

const getComponent = file => {
  switch (file.type) {
    case EnumFileType.PDF:
      return PdfPreview;
    case EnumFileType.WORD:
      return WordPreview;
    case EnumFileType.EXCEL:
      return ExcelPreview;
    case EnumFileType.VIDEO:
      return VideoPreview;
    default:
      return null;
  }
};

const onDeleteFile = file => {
  let temp = props.fileList.filter(item => item.url !== file.url);
  emit('delete', file, temp);
};

const onDownloadFile = file => {
  downloadFile(file.url, file.name);
  emit('download', file);
};

watch(
  () => props.fileList,
  val => {
    getFileList.value = val.map(file => {
      return {
        ...file,
        type: getFileTypeByName(file),
        error: false,
      };
    });
  },
  { immediate: true },
);

onMounted(() => {
  // 如果没有延迟element的走马灯组件会有布局错乱问题
  setTimeout(() => {
    ready.value = true;
  }, 400);
});
</script>

<style scoped>
.preview-dialog {
  width: 100%;
  height: 70vh;
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.error-box img {
  width: 159px;
  height: 91px;
  padding: 5px;
  border: 1px dashed #d5d3d3;
}
.error-box span {
  color: #3d3d3d;
  font-size: 14px;
  margin-top: 30px;
}

.preview-content {
  width: 100%;
  height: 100%;
}

.preview-dialog ::v-deep .el-carousel__container {
  height: 100%;
  width: 100%;
}

.file-item {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 6px;
  box-shadow: 0px 1px 6px 0px rgba(0, 0, 0, 0.2);
}

.file-header {
  display: flex;
  flex-shrink: 0;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
  height: 44px;
  font-weight: 500;
  border-bottom: 1px solid #eeeff1;
}
.file-header span {
  color: #3d3d3d;
  font-size: 14px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  margin-right: 10px;
}
.preview-box {
  flex-grow: 1;
  height: 100px;
  padding: 10px;
  overflow: hidden;
}
.menu-box {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-shrink: 0;
  margin-top: 20px;
}
</style>

<style>
.el-dialog.dialog-preview {
  background: transparent;
  box-shadow: none !important;
}
.el-dialog.dialog-preview .el-dialog__header {
  display: none;
}
</style>
