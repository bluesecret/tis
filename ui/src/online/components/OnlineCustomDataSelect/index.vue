<template>
  <div class="data-select">
    <el-select
      :model-value="selectedValue"
      style="width: 100%"
      :multiple="multiple"
      :disabled="disabled || (relativeTable || {}).relativeFormId == null"
      :size="size"
      :clearable="clearable"
      :placeholder="placeholder"
      :teleported="false"
      popper-class="data-select-popper"
      @visible-change="onVisibleChange"
      @clear="onClear"
    >
      <el-option :label="selectedItem.label" :value="selectedItem.value" />
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { getUUID } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import { Dialog } from '@/components/Dialog';
import { post } from '@/common/http/request';
import { TableData } from '@/common/types/table';
import { API_CONTEXT } from '@/api/config';
import DataSelectDlg from './DataSelectDlg.vue';

const emit = defineEmits<{
  'update:value': [string | number | Array<ANY_OBJECT>];
  change: [string | number | Array<ANY_OBJECT>, ANY_OBJECT | null];
  input: [string | number | Array<ANY_OBJECT>];
}>();

const props = withDefaults(
  defineProps<{
    value: string | number | Array<ANY_OBJECT>;
    relativeTable?: ANY_OBJECT;
    size?: '' | 'default' | 'small' | 'large';
    placeholder?: string;
    multiple?: boolean;
    disabled?: boolean;
    clearable?: boolean;
    collapseTags?: boolean;
    extraData?: ANY_OBJECT;
  }>(),
  {
    multiple: false,
    disabled: false,
    clearable: false,
    collapseTags: true,
  },
);

const widgetId = ref(getUUID());
const selectedItem = ref<{ label: string; value: string | number | Array<ANY_OBJECT> }>({
  label: '',
  value: '',
});
const selectedValue = ref<string | number | Array<ANY_OBJECT>>('');

const form = inject('form', () => {
  console.error('OnlineCustomDataSelect: form not injected');
  return { isEdit: false } as ANY_OBJECT;
});

const onClear = () => {
  selectedItem.value = { label: '', value: '' };
  selectedValue.value = '';
  emitChange(null);
};
const emitChange = (selectRow: ANY_OBJECT | null) => {
  emit('update:value', selectedValue.value);
  emit('change', selectedValue.value, selectRow);
  emit('input', selectedValue.value);
};
const handlerEditOperate = (value: ANY_OBJECT) => {
  selectedItem.value = {
    label: value[props.relativeTable?.displayField],
    value: value[props.relativeTable?.relativeColumn],
  };
  selectedValue.value = value[props.relativeTable?.relativeColumn];
  emitChange(value);
};

const loadTableData = () => {
  return new Promise((resolve, reject) => {
    let params: ANY_OBJECT = {};
    let httpCall = null;
    params.datasourceId = props.relativeTable?.datasourceId;
    params.filterDtoList = [];
    params.filterDtoList.push({
      columnName: props.relativeTable?.relativeColumn,
      columnValue: selectedValue.value,
      filterType: 1,
      tableName: props.relativeTable?.relativeTableName,
    });
    params.relationId = props.relativeTable?.relationId;

    httpCall = post<TableData<ANY_OBJECT>>(
      API_CONTEXT +
        `/online/onlineOperation/listByOneToManyRelationId/${props.relativeTable?.variableName}`,
      params,
    );

    httpCall
      .then(res => {
        if (res.data.dataList.length > 0) {
          selectedItem.value = {
            label: res.data.dataList[0][props.relativeTable?.displayField],
            value: selectedValue.value,
          };
        }
        resolve({
          dataList: res.data.dataList,
          totalCount: res.data.totalCount,
        });
      })
      .catch(e => {
        reject(e);
      });
  });
};

const loadOnlineFormConfig = (formId: string) => {
  return new Promise((resolve, reject) => {
    form()
      .loadOnlineFormConfig(formId)
      .then((formConfig: ANY_OBJECT) => {
        resolve(formConfig);
      })
      .catch((e: Error) => {
        console.log(e);
        reject(e);
      });
  });
};
const onVisibleChange = (visible: boolean) => {
  if (visible) {
    loadOnlineFormConfig(props.relativeTable?.relativeFormId)
      .then(formConfig => {
        Dialog.show<ANY_OBJECT>(
          '关联数据选择',
          DataSelectDlg,
          {
            area: ['900px', '650px'],
            offset: '100px',
            skin: 'one_to_one_query',
          },
          {
            value: selectedValue.value,
            relativeTable: props.relativeTable,
            formConfig: formConfig,
            extraData: props.extraData,
            path: 'thirdSelectData/' + widgetId.value,
          },
          {
            width: '900px',
            height: '650px',
            pathName: '/thirdParty/thirdSelectData',
          },
        ).then(res => {
          handlerEditOperate(res);
        });
      })
      .catch(e => {
        console.log(e);
        ElMessage.error('获取弹窗信息失败！');
      });
  }
};

onMounted(() => {
  selectedValue.value = props.value;
  if (selectedValue.value) loadTableData();
});

watch(
  () => props.value,
  () => {
    selectedValue.value = props.value;
    if (selectedValue.value) loadTableData();
  },
  {
    deep: true,
    immediate: true,
  },
);
</script>

<style scoped>
.data-select :deep(.data-select-popper) {
  display: none;
}
.data-select :deep(.el-dialog__header) {
  height: 42px;
  line-height: 42px;
  padding: 0 20px;
  background-color: #f8f8f8;
}
.data-select :deep(.el-dialog__title) {
  font-size: 14px;
  color: #333;
}
.data-select :deep(.el-dialog__headerbtn) {
  top: 12px;
}
.data-select :deep(.el-dialog__body) {
  padding: 25px;
}
</style>
