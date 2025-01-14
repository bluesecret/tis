<template>
  <el-row class="form-edit-widget-categroy-column third-party-dlg">
    <el-form
      ref="formEditWidgetCategroyColumn"
      :model="formData"
      class="full-width-input form-box"
      :rules="rules"
      style="width: 100%"
      label-width="100px"
      :size="formItemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="数据集字段" prop="columnId">
            <el-select
              v-if="!isTreeColumn"
              v-model="formData.columnId"
              placeholder=""
              clearable
              :disabled="dialogParams.rowData != null"
              @change="onDatasetColumnChange"
            >
              <el-option
                v-for="item in dialogParams.columnList"
                :key="item.columnId"
                :label="item.columnName"
                :value="item.columnId"
                :disabled="item.disabled"
              >
                <el-row type="flex" justify="space-between" align="middle">
                  <span>{{ item.columnName }}</span>
                  <el-tag size="default" :type="item.dimension ? 'success' : 'info'">
                    {{ item.dimension ? '维度字段' : '指标字段' }}
                  </el-tag>
                </el-row>
              </el-option>
            </el-select>
            <el-cascader
              v-else
              :options="columnTree"
              placeholder=""
              clearable
              v-model="columnPath"
              @update:modelValue="onTreeColumnInput"
              :disabled="dialogParams.rowData != null"
              :props="{ label: 'columnName', value: 'columnId' }"
              @change="onDatasetColumnChange"
            >
              <template v-slot="{ data }">
                <el-row type="flex" justify="space-between" align="middle">
                  <span>{{ data.columnName }}</span>
                  <el-tag
                    v-if="data.fieldType !== 'Object' && data.fieldType !== 'Array'"
                    size="default"
                    :type="data.dimension ? 'success' : 'info'"
                  >
                    {{ data.dimension ? '维度字段' : '指标字段' }}
                  </el-tag>
                </el-row>
              </template>
            </el-cascader>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="显示名称" prop="showName">
            <el-input v-model="formData.showName" placeholder="" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="24" v-if="dialogParams.supportColumnWidth">
          <el-form-item label="字段最小宽度">
            <el-input
              v-model="formData.columnWidth"
              placeholder="最小列宽度，会自动将剩余空间按比例分配"
              clearable
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-row class="no-scroll menu-box" type="flex" justify="end">
      <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="formItemSize" @click="onSubmit"> 保存 </el-button>
    </el-row>
  </el-row>
</template>

<script setup lang="ts">
import { CascaderValue } from 'element-plus';
import { treeDataTranslate, findTreeNodePath, findItemFromList } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { DatasetType } from '@/common/staticDict/report';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  rowData?: ANY_OBJECT;
  columnList: ANY_OBJECT[];
  dataset?: ANY_OBJECT;
  supportColumnWidth?: boolean;
  defaultFormItemSize: Ref<'' | 'default' | 'small' | 'large'>;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
}
const props = withDefaults(defineProps<IProps>(), {
  supportColumnWidth: false,
});
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const formEditWidgetCategroyColumn = ref();
const formData = ref<ANY_OBJECT>({
  id: undefined,
  columnId: undefined,
  showName: undefined,
  columnWidth: undefined,
});
const rules = {
  columnId: [{ required: true, message: '请选择数据集字段！', trigger: 'blur' }],
  showName: [{ required: true, message: '显示名称不能为空！', trigger: 'blur' }],
};

const isTreeColumn = computed(() => {
  return dialogParams.value.dataset.datasetType === DatasetType.API;
});
const columnTree = computed(() => {
  let tempList = dialogParams.value.columnList.map(item => {
    return {
      ...item,
    };
  });
  return treeDataTranslate(tempList, 'columnId');
});
const columnPath = computed(() => {
  if (formData.value.columnId == null || formData.value.columnId === '') return [];
  return findTreeNodePath(columnTree.value, formData.value.columnId, 'columnId');
});
const dialogParams = computed(() => {
  return {
    rowData: props.rowData || thirdParams.value.rowData,
    dataset: props.dataset || thirdParams.value.dataset || {},
    supportColumnWidth: thirdParams.value.supportColumnWidth
      ? thirdParams.value.supportColumnWidth
      : props.supportColumnWidth,
    columnList: (props.columnList || thirdParams.value.columnList || []).map(item => {
      return {
        ...item,
        children: undefined,
      } as ANY_OBJECT;
    }),
  };
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};
const onSubmit = () => {
  formEditWidgetCategroyColumn.value.validate((valid: boolean) => {
    if (!valid) return;
    if (props.dialog) {
      props.dialog.submit(formData.value);
    } else {
      onCloseThirdDialog(true, dialogParams.value.rowData, formData.value);
    }
  });
};
const onTreeColumnInput = (value: CascaderValue) => {
  formData.value.columnId = Array.isArray(value) ? value[value.length - 1] : undefined;
};
const onDatasetColumnChange = (value: CascaderValue) => {
  let temp = findItemFromList(dialogParams.value.columnList, value, 'columnId');
  formData.value.showName = temp ? temp.columnName : undefined;
};

onMounted(() => {
  if (dialogParams.value.rowData != null) {
    formData.value = {
      ...dialogParams.value.rowData,
    };
  }
});
</script>
