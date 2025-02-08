<template>
  <div class="file-list" :style="getFlexDirection">
    <el-tooltip
      effect="dark"
      :content="file.name"
      placement="top"
      :disabled="type === 'text'"
      v-for="file in getFileList"
      :key="file.url"
    >
      <div
        class="file-item"
        :class="type"
        :style="{
          width: type === 'text' && direction === 'vertical' ? '100%' : undefined,
          border: type === 'card' && !readonly ? '1px dashed #999999' : 'none',
        }"
        @click.stop="onPreviewFile(file, fileList)"
      >
        <template v-if="type === 'card'">
          <el-image
            v-if="file.type === EnumFileType.IMAGE"
            :src="file.url"
            fit="cover"
            style="width: 100%; height: 100%"
          />
          <div v-else class="card-item-text">
            <el-icon style="font-size: 24px"><Document /></el-icon>
          </div>
          <div v-if="supportPreview" class="priview-btn">Preview</div>
          <el-icon v-if="!readonly" class="close-btn" @click.stop="onRemoveFile(file, fileList)">
            <CircleCloseFilled />
          </el-icon>
        </template>
        <template v-else>
          <span
            :style="{
              'flex-grow': direction === 'vertical' ? 1 : 0,
              width: direction === 'vertical' ? '100px' : undefined,
              'margin-right': direction === 'vertical' ? undefined : '8px',
            }"
            >{{ file.name }}</span
          >
          <el-icon
            v-if="!readonly"
            class="close-btn"
            style="margin-left: 6px"
            @click.stop="onRemoveFile(file, fileList)"
          >
            <CircleCloseFilled />
          </el-icon>
        </template>
      </div>
    </el-tooltip>
    <div class="file-item" :class="type" v-if="supportAdd && $slots.default" @click="onAddFile">
      <slot />
    </div>
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
import { Plus, CircleCloseFilled, Document } from '@element-plus/icons-vue';
import { defineProps, withDefaults, defineEmits } from 'vue';
import { useDownload } from '@/common/hooks/useDownload';
import { EnumFileType } from '@/common/staticDict/index';
import { getFileType } from '@/common/utils';
import PreviewDialog from '@/components/Preview/Dialog';

const { downloadFile } = useDownload();
const props = withDefaults(
  defineProps<{
    fileList: Array<{ url: string; name: string; type?: string | number }>;
    type: string;
    direction: string;
    supportPreview: boolean;
    supportDownload: boolean;
    readonly: boolean;
    supportAdd: boolean;
  }>(),
  {
    fileList: () => [],
    type: 'text',
    direction: 'vertical',
    supportPreview: true,
    supportDownload: true,
    readonly: false,
    supportAdd: true,
  },
);

const emit = defineEmits(['remove', 'add', 'preview']);

const getFileList = computed(() => {
  return props.fileList.map(file => {
    return {
      ...file,
      type: getFileTypeByName(file),
      error: false,
    };
  });
});

const getFlexDirection = computed(() => {
  return {
    'flex-direction': props.direction === 'vertical' && props.type !== 'card' ? 'column' : 'row',
  };
});

const showPreview = ref(false);
const fileIndex = ref(0);

const getFileTypeByName = file => {
  if (file && file.type) {
    return file.type;
  } else {
    return getFileType(file.name);
  }
};

const onRemoveFile = (file, fileList) => {
  const index = fileList.findIndex(item => item.url === file.url);
  fileList.splice(index, 1);
  emit('remove', file, fileList);
};

const onPreviewFile = (file, fileList) => {
  if (!props.supportPreview) return;
  if (props.supportPreview) {
    fileIndex.value = fileList.findIndex(item => item.url === file.url);
    nextTick(() => {
      showPreview.value = true;
      emit('preview', file, fileList);
    });
  } else if (props.supportDownload) {
    downloadFile(file.url, file.name);
  }
};

const onAddFile = () => {
  emit('add');
};
</script>

<style lang="scss" scoped>
.file-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  align-items: flex-start;
  flex-wrap: wrap;
}
.file-item.card {
  width: 65px;
  height: 65px;
  line-height: 65px;
  border-radius: 4px;
  color: #999999;
  position: relative;
  text-align: center;
  border: 1px dashed #999999;
  margin: 4px 8px 4px 0px;
  cursor: pointer;
}
.file-item.card .file-name {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 22px;
  line-height: 22px;
  text-align: center;
  color: #3d3d3d;
  font-size: 12px;
  padding: 0px 4px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  cursor: pointer;
}
.file-item.card .priview-btn {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 22px;
  line-height: 22px;
  text-align: center;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 12px;
  display: none;
  cursor: pointer;
}
.file-item.card:hover .priview-btn {
  display: block;
}
.file-item.card .close-btn {
  position: absolute;
  top: -5px;
  right: -7px;
  width: 16px;
  height: 16px;
  color: #222222;
  font-size: 16px;
  cursor: pointer;
  display: none;
}
.file-item.card:hover .close-btn {
  display: block;
}

.file-item.text {
  display: flex;
  align-items: center;
  height: 36px;
  line-height: 36px;
  color: #3d3d3d;
  width: 100%;
}
.file-item.text span {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  cursor: pointer;
}
.file-item.text .close-btn {
  color: #d8d8d8;
  cursor: pointer;
}
.file-item.text:hover,
.file-item.text:hover .close-btn {
  color: $color-primary;
}
</style>
