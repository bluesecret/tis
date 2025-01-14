<template>
  <el-row class="form-edit-widget-categroy-column third-party-dlg">
    <el-form
      ref="formEditWidgetCategroyColumn"
      :model="formData"
      class="full-width-input form-box"
      :rules="rules"
      style="width: 100%"
      label-width="80px"
      :size="formItemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="字段名称" prop="columnId">
            <el-select
              v-if="!isTreeColumn"
              v-model="formData.columnId"
              placeholder=""
              clearable
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
              :props="{ label: 'columnName', value: 'columnId' }"
              @change="onDatasetColumnChange"
            >
              <template v-slot="{ data }">
                <el-row type="flex" justify="space-between" align="middle">
                  <span>{{ data.columnName }}</span>
                </el-row>
              </template>
            </el-cascader>
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
  dataset?: ANY_OBJECT;
  supportColumnWidth?: boolean;
  columnList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = withDefaults(defineProps<IProps>(), {
  supportColumnWidth: false,
});
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const formEditWidgetCategroyColumn = ref();
const formData = ref<ANY_OBJECT>({
  columnId: undefined,
  columnName: undefined,
});
const rules: ANY_OBJECT = {
  columnId: [{ required: true, message: '字段名称不能为空', trigger: 'blur' }],
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
    supportColumnWidth:
      thirdParams.value && thirdParams.value.supportColumnWidth != null
        ? thirdParams.value.supportColumnWidth
        : props.supportColumnWidth,
    columnList: (props.columnList || (thirdParams.value.columnList as ANY_OBJECT[]) || []).map(
      item => {
        return {
          ...item,
          children: undefined,
        } as ANY_OBJECT;
      },
    ),
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
      onCloseThirdDialog(false, dialogParams.value.rowData, formData.value);
    }
  });
};
const onTreeColumnInput = (value: ANY_OBJECT[]) => {
  formData.value.columnId = Array.isArray(value) ? value[value.length - 1] : undefined;
};
const onDatasetColumnChange = (value: CascaderValue) => {
  const val = (Array.isArray(value) ? value[value.length - 1] : value) as string;
  let findItemResult = findItemFromList(dialogParams.value.columnList, val, 'columnId');

  let columnNames: ANY_OBJECT[] = [];
  if (Array.isArray(value)) {
    value.forEach((columnId, index) => {
      if (index > 0) {
        let columnItem = dialogParams.value.columnList.find(column => column.columnId === columnId);
        columnItem && columnNames.push(columnItem);
      }
    });
  } else {
    let columnItem = dialogParams.value.columnList.find(column => column.columnId === value);
    columnItem && columnNames.push(columnItem);
  }

  if (columnNames) {
    formData.value = {
      ...findItemResult,
      columnName: columnNames.map(x => x.columnName),
      showName: columnNames[columnNames.length - 1].columnComment,
    };
  }
};

onMounted(() => {
  if (dialogParams.value.rowData) {
    formData.value = {
      ...dialogParams.value.rowData,
    };
  }
});
</script>
