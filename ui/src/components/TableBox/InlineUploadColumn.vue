<template>
  <vxe-column v-bind="$attrs">
    <template v-slot="scope">
      <el-form-item v-if="isEdit(scope.row)" label="" :prop="$attrs.field" style="width: 100%">
        <custom-upload
          v-model="uploadWidgetFileList"
          ref="inputWidget"
          name="uploadFile"
          :action="getUploadActionUrl(action)"
          :headers="getUploadHeaders"
          :data="data"
          :limit="limit"
          size="default"
          :type="fileListType"
          @change="onFileChange"
        />
      </el-form-item>
      <!-- 判断是否有default的slot -->
      <slot v-else-if="$slots.default" :row="scope.row" />
      <!-- 默认显示内容 -->
      <span v-else>{{ getRowValue(scope.row) }}</span>
    </template>
  </vxe-column>
</template>

<script setup lang="ts">
import { VxeColumn } from 'vxe-table';
import { ElMessage, ElMessageBox, UploadFile } from 'element-plus';
import { Close, CircleClose, Plus } from '@element-plus/icons-vue';
import { ANY_OBJECT } from '@/types/generic';
import { useUpload } from '@/common/hooks/useUpload';
import { useUploadWidget } from '@/common/hooks/useUploadWidget';
import useTableInlineColumn from './useTableInlineColumn';

const { getUploadHeaders, getUploadActionUrl, fileListToJson, parseUploadData, getPictureList } =
  useUpload();
type InlineInputColumnProps = {
  /**
   * 上传文件类型，image/file
   * image: 图片
   * file: 文件
   */
  type: string;
  /**
   * 文件列表类型，dropdown/expand
   * dropdown: 列表在下拉框内
   * expand: 列表展开
   */
  fileListType: string;
  /**
   * 上传的地址
   */
  action?: string;
  /**
   * 上传时附带的额外参数
   */
  data?: ANY_OBJECT;
  /**
   * 上传文件个数限制
   */
  limit?: number;
  /**
   * 主键字段名
   */
  rowKey?: string;
  parseUploadDataFunction?: (value: ANY_OBJECT, row: ANY_OBJECT) => UploadFile[];
};
const props = withDefaults(defineProps<InlineInputColumnProps>(), {
  type: 'file',
  fileListType: 'dropdown',
  action: '',
  data: {},
  limit: undefined,
  rowKey: undefined,
  parseUploadDataFunction: undefined,
});

const emit = defineEmits(['change']);
const uploadWidget = useUploadWidget(props.limit);
const { fileList: uploadWidgetFileList, maxCount: uploadWidgetMaxCount } = uploadWidget;

const getFieldValue = (value?: ANY_OBJECT) => {
  return fileListToJson(value);
};

const onFileChange = val => {
  onChange(val);
};

const inputWidget = ref();
const { isEdit, getRowData, getRowValue, onChange, reset, editConfig } = useTableInlineColumn(
  false,
  emit,
  inputWidget,
  getFieldValue,
);

watch(
  () => getRowData.value,
  value => {
    let row = (editConfig.value || {}).rowCopy;
    let downloadParams = {
      ...props.data,
    };
    if (row && row[props.rowKey]) downloadParams[props.rowKey] = row[props.rowKey];
    uploadWidgetFileList.value = props.parseUploadDataFunction
      ? props.parseUploadDataFunction(value, row)
      : parseUploadData(value, downloadParams);
  },
  {
    immediate: true,
  },
);

defineExpose({
  reset,
  focus: () => {
    if (inputWidget.value && inputWidget.value.focus) inputWidget.value.focus();
  },
});
</script>

<style scoped>
.upload-image-item {
  height: 65px;
  width: 65px;
  line-height: 65px;
  text-align: center;
  font-size: 28px !important;
  border-radius: 4px;
  border: 1px dashed #d9d9d9;
}
.ellipsis-item {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}
.hide-list /deep/ .el-dropdown__caret-button {
  display: none;
}
.upload-file-list {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 10px 0px;
}
.file-list-item {
  display: flex;
  align-items: center;
  margin-top: 5px;
  width: 100%;
  text-align: left;
}
.table-inline-upload /deep/ .el-upload {
  width: 100%;
  justify-content: flex-start;
}
.table-inline-upload /deep/ .el-upload--picture-card {
  width: 65px;
  height: 65px;
}
.table-inline-upload /deep/ .el-upload-list__item {
  width: 65px;
  height: 65px;
  position: relative;
}
.close-btn {
  padding: 1px;
  font-size: 10px;
  width: 16px;
  height: 16px;
}
</style>
