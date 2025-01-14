<template>
  <div class="online-custom-upload">
    <custom-upload
      v-if="!readOnly"
      :type="isImage ? 'expand' : 'dropdown'"
      :name="widget.props.fileFieldName"
      :headers="getUploadHeaders"
      :action="getActionUrl"
      :data="uploadData"
      :modelValue="getUploadFileList"
      :limit="maxFileCount"
      :accept="getAccept"
      :disabled="getDisabledStatus()"
      @change="onValueChange"
      @input.stop
    />
    <upload-file-list
      v-else
      :supportPreview="true"
      :supportDownload="true"
      :file-list="getUploadFileList"
      type="card"
      :supportAdd="false"
    />
  </div>
</template>

<script setup lang="ts">
import { UploadFile, ElMessage } from 'element-plus';
import { Plus, Close } from '@element-plus/icons-vue';
import { useDownload } from '@/common/hooks/useDownload';
import { useUpload } from '@/common/hooks/useUpload';
import { useUploadWidget } from '@/common/hooks/useUploadWidget';
import { OnlineFormEventType, EnumFileType } from '@/common/staticDict';
import { ANY_OBJECT } from '@/types/generic';
import { SysOnlineFieldKind } from '@/common/staticDict/online';
import { API_CONTEXT } from '@/api/config';

const emit = defineEmits<{
  'update:value': [string | undefined];
  'update:modelValue': [string | undefined];
  change: [string | undefined];
}>();

const props = withDefaults(
  defineProps<{ value?: string; widget: ANY_OBJECT; readOnly?: boolean }>(),
  {
    readOnly: false,
  },
);

const form = inject('form', () => {
  return { isEdit: false } as ANY_OBJECT;
});
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

const { downloadFile } = useDownload();
const { getUploadHeaders, getUploadActionUrl, parseUploadData, fileListToJson } = useUpload();

const maxFileCount = computed(() => {
  return props.widget.column ? props.widget.column.maxFileCount : 1;
});
const buildFlowParam = computed(() => {
  let flowParam: ANY_OBJECT = {};
  let flowData = form().flowData;
  if (flowData) {
    if (flowData.processDefinitionKey)
      flowParam.processDefinitionKey = flowData.processDefinitionKey;
    if (flowData.processInstanceId) flowParam.processInstanceId = flowData.processInstanceId;
    if (flowData.taskId) flowParam.taskId = flowData.taskId;
  }

  return flowParam;
});
const getActionUrl = computed(() => {
  if (props.widget.props.actionUrl == null || props.widget.props.actionUrl === '') {
    if (props.widget.relation) {
      return getUploadActionUrl(
        API_CONTEXT +
          '/online/onlineOperation/uploadOneToManyRelation/' +
          (props.widget.datasource || {}).variableName,
      );
    } else {
      return getUploadActionUrl(
        API_CONTEXT +
          '/online/onlineOperation/uploadDatasource/' +
          (props.widget.datasource || {}).variableName,
      );
    }
  } else {
    return getUploadActionUrl(props.widget.props.actionUrl);
  }
});
const getDownloadUrl = computed(() => {
  if (props.widget.props.downloadUrl == null || props.widget.props.downloadUrl === '') {
    if (props.widget.relation) {
      return (
        API_CONTEXT +
        '/online/onlineOperation/downloadOneToManyRelation/' +
        (props.widget.datasource || {}).variableName
      );
    } else {
      return (
        API_CONTEXT +
        '/online/onlineOperation/downloadDatasource/' +
        (props.widget.datasource || {}).variableName
      );
    }
  } else {
    return props.widget.props.downloadUrl;
  }
});

const uploadData = ref<ANY_OBJECT>({});
const getUploadFileList = computed(() => {
  return uploadWidgetImpl ? uploadWidgetImpl.fileList : [];
});

const getAccept = computed(() => {
  if (props.widget && props.widget.column) {
    if (props.widget.column.fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE) {
      return '.png,.jpg,.jpeg,.gif,.bmp';
    } else {
      let fileType = props.widget.column.fileType;
      if (fileType != null) {
        fileType = fileType.split(',').map(item => Number.parseInt(item));
      }
      if (fileType == null || fileType.length === 0) {
        return undefined;
      } else {
        // eslint-disable-next-line array-callback-return
        let temp = fileType.reduce((acc, cur) => {
          if (acc !== '') acc += ',';
          switch (cur) {
            case EnumFileType.IMAGE:
              return acc + '.png,.jpg,.jpeg,.gif,.bmp';
            case EnumFileType.AUDIO:
              return acc + '.mp3';
            case EnumFileType.VIDEO:
              return acc + '.mp4';
            case EnumFileType.PDF:
              return acc + '.pdf';
            case EnumFileType.WORD:
              return acc + '.docx';
            case EnumFileType.EXCEL:
              return acc + '.xlsx';
          }
        }, '');
        return temp;
      }
    }
  } else {
    return undefined;
  }
});

const isImage = ref(false);
const uploadWidgetImpl = reactive(
  useUploadWidget(props.widget.column ? props.widget.column.maxFileCount : 0),
);

const getDisabledStatus = () => {
  if (form().isEdit) return true;
  const formWidgetAuth: ANY_OBJECT | null =
    form().formAuth && form().formAuth() && form().formAuth().pc
      ? form().formAuth().pc[props.widget.variableName]
      : null;
  if (formWidgetAuth && formWidgetAuth.disabled) return true;
  if (props.widget.eventInfo && props.widget.eventInfo[OnlineFormEventType.DISABLE]) {
    return props.widget.eventInfo[OnlineFormEventType.DISABLE](form().instanceData());
  } else {
    return props.widget.props.disabled;
  }
};

const onValueChange = val => {
  // TODO 没找到widgetConfig的定义
  uploadWidgetImpl.fileList = val;
  const json = fileListToJson(val);
  emit('update:value', json);
  emit('update:modelValue', json);
  emit('change', json);
};

watch(
  () => props.widget,
  () => {
    setTimeout(() => {
      let column = props.widget.bindData?.column || props.widget.column;
      isImage.value = column ? column.fieldKind === SysOnlineFieldKind.UPLOAD_IMAGE : false;

      let temp: ANY_OBJECT = {
        ...buildFlowParam.value,
        datasourceId: (props.widget.datasource || {}).datasourceId,
        asImage: isImage.value,
        fieldName: (props.widget.column || {}).columnName,
      };
      if ((props.widget.relation || {}).relationId)
        temp.relationId = (props.widget.relation || {}).relationId;
      let flowData = form().flowData;
      if (flowData && flowData.processDefinitionKey)
        temp.processDefinitionKey = flowData.processDefinitionKey;
      uploadData.value = temp;
    }, 30);
  },
  {
    deep: true,
    immediate: true,
  },
);

watch(
  () => props.value,
  newValue => {
    uploadWidgetImpl.fileList = [];
    setTimeout(() => {
      if (newValue != null && newValue !== '') {
        let downloadParams: ANY_OBJECT = {
          ...buildFlowParam.value,
          datasourceId: (props.widget.datasource || {}).datasourceId,
          fieldName: props.widget.column.columnName,
          asImage: isImage.value,
          dataId: form().getPrimaryData(props.widget) || '',
        };
        if (props.widget.relation) downloadParams.relationId = props.widget.relation.relationId;
        let temp = JSON.parse(newValue);
        temp = Array.isArray(temp)
          ? temp.map(item => {
              return {
                ...item,
                downloadUri: getDownloadUrl.value,
              };
            })
          : [];
        uploadWidgetImpl.fileList = parseUploadData(JSON.stringify(temp), downloadParams);
      } else {
        uploadWidgetImpl.fileList = [];
      }
    }, 30);
  },
  {
    deep: true,
    immediate: true,
  },
);
</script>
