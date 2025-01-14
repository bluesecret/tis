<template>
  <div class="form-single-fragment third-party-dlg" style="position: relative">
    <el-form
      ref="form"
      :model="formData"
      class="full-width-input form-box"
      :rules="rules"
      style="width: 100%"
      label-width="120px"
      :size="formItemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="关联标识" prop="variableName">
            <el-input
              class="input-item"
              v-model="formData.variableName"
              :clearable="true"
              placeholder="关联标识"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="关联类型" prop="relationType">
            <el-select class="input-item" v-model="formData.relationType" placeholder="关联类型">
              <el-option
                v-for="item in ReportRelationType.getList()"
                :key="item.id"
                :value="item.id"
                :label="item.name"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="主数据集">
            <el-input class="input-item" :value="masterDatasetName" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="主数据集字段">
            <el-select
              class="input-item"
              v-model="formData.masterColumnId"
              :clearable="true"
              filterable
              placeholder="数据集关联字段"
              :loading="masterColumnIdWidget.loading"
              @visible-change="masterColumnIdWidget.onVisibleChange"
            >
              <el-option
                v-for="item in masterColumnIdWidget.dropdownList"
                :key="item.columnId"
                :value="item.columnId"
                :label="item.columnName"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="关联数据集">
            <el-select
              class="input-item"
              v-model="formData.slaveDatasetId"
              :clearable="true"
              filterable
              placeholder="关联数据集"
              :loading="slaveDatasetIdWidget.loading"
              @visible-change="slaveDatasetIdWidget.onVisibleChange"
              @change="onSlaveDatasetIdChange"
            >
              <el-option
                v-for="item in slaveDatasetIdWidget.dropdownList"
                :key="item.datasetId"
                :value="item.datasetId"
                :label="item.datasetName"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="关联数据集字段">
            <el-select
              class="input-item"
              v-model="formData.slaveColumnId"
              :clearable="true"
              filterable
              placeholder="数据集关联字段"
              :loading="slaveColumnIdWidget.loading"
              @visible-change="slaveColumnIdWidget.onVisibleChange"
            >
              <el-option
                v-for="item in slaveColumnIdWidget.dropdownList"
                :key="item.columnId"
                :value="item.columnId"
                :label="item.columnName"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-row class="menu-box">
      <el-col :span="24">
        <el-row class="no-scroll flex-box" type="flex" justify="end">
          <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
          <el-button type="primary" :size="formItemSize" @click="onUpdateClick()"> 保存 </el-button>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { ReportDatasetController, ReportDatasetRelationController } from '@/api/report';
import { DatasetType, ReportRelationType } from '@/common/staticDict/report';
import { useDropdown } from '@/common/hooks/useDropdown';
import { DropdownOptions, ListData } from '@/common/types/list';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  masterDataset?: ANY_OBJECT;
  rowData?: ANY_OBJECT;
  datasetList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<boolean>;
}
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const form = ref();
const formData = ref<ANY_OBJECT>({
  relationId: undefined,
  variableName: undefined,
  masterDatasetId: undefined,
  masterColumnId: undefined,
  slaveDatasetId: undefined,
  slaveColumnId: undefined,
  relationType: undefined,
});
const rules = {
  variableName: [{ required: true, message: '请输入关联标识', trigger: 'blur' }],
  relationType: [{ required: true, message: '请选择关联类型', trigger: 'blur' }],
  masterDatasetId: [{ required: true, message: '请选择主数据集', trigger: 'blur' }],
  masterColumnId: [{ required: true, message: '请选择主数据集关联字段', trigger: 'blur' }],
  slaveDatasetId: [{ required: true, message: '请选择关联数据集', trigger: 'blur' }],
  slaveColumnId: [{ required: true, message: '请选择关联数据集关联字段', trigger: 'blur' }],
};

/**
 * 获取主数据集字段下拉数据
 */
const loadMasterColumnIdDropdownList = (): Promise<ListData<ANY_OBJECT>> => {
  return new Promise((resolve, reject) => {
    let params = {
      datasetId: dialogParams.value.masterDataset.datasetId,
    };

    ReportDatasetController.view(params)
      .then(res => {
        resolve({ dataList: res.data.columnList });
      })
      .catch(e => {
        reject(e);
      });
  });
};
const dropdownMasterColumnIdOptions: DropdownOptions<ANY_OBJECT> = {
  loadData: loadMasterColumnIdDropdownList,
};
const masterColumnIdWidget = reactive(useDropdown(dropdownMasterColumnIdOptions));

/**
 * 获取关联数据集下拉数据
 */
const loadSlaveDatasetIdDropdownList = (): Promise<ListData<ANY_OBJECT>> => {
  return Promise.resolve({
    dataList: dialogParams.value.datasetList.filter(
      item =>
        item.datasetId !== dialogParams.value.masterDataset.datasetId &&
        item.datasetType !== DatasetType.SQL,
    ),
  });
};
const dropdownSlaveDatasetIdOptions: DropdownOptions<ANY_OBJECT> = {
  loadData: loadSlaveDatasetIdDropdownList,
};
const slaveDatasetIdWidget = reactive(useDropdown(dropdownSlaveDatasetIdOptions));

/**
 * 获取关联数据集字段下拉数据
 */
const loadSlaveColumnIdDropdownList = (): Promise<ListData<ANY_OBJECT>> => {
  return new Promise((resolve, reject) => {
    if (formData.value.slaveDatasetId == null || formData.value.slaveDatasetId === '') {
      resolve([]);
      return;
    }
    let params = {
      datasetId: formData.value.slaveDatasetId,
    };

    ReportDatasetController.view(params)
      .then(res => {
        resolve({ dataList: res.data.columnList });
      })
      .catch(e => {
        reject(e);
      });
  });
};
const dropdownSlaveColumnIdOptions: DropdownOptions<ANY_OBJECT> = {
  loadData: loadSlaveColumnIdDropdownList,
};
const slaveColumnIdWidget = reactive(useDropdown(dropdownSlaveColumnIdOptions));

const dialogParams = computed(() => {
  return {
    rowData: props.rowData || thirdParams.value.rowData,
    masterDataset: props.masterDataset || thirdParams.value.masterDataset || {},
    datasetList: props.datasetList || (thirdParams.value.datasetList as ANY_OBJECT[]) || [],
  };
});
const isEdit = computed(() => {
  return dialogParams.value.rowData != null;
});
const masterDatasetName = computed(() => {
  return dialogParams.value.masterDataset.datasetName;
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};
/**
 * 保存
 */
const onUpdateClick = () => {
  form.value.validate((valid: boolean) => {
    if (!valid) return;
    let params = {
      reportDatasetRelationDto: {
        ...formData.value,
        masterDatasetId: dialogParams.value.masterDataset.datasetId,
      },
    };
    let httpCall = isEdit.value
      ? ReportDatasetRelationController.update(params)
      : ReportDatasetRelationController.add(params);
    httpCall
      .then(() => {
        ElMessage.success('保存成功!');
        if (props.dialog) {
          props.dialog.submit(true);
        } else {
          onCloseThirdDialog(true, true);
        }
      })
      .catch(e => {
        console.warn(e);
      });
  });
};

const onSlaveDatasetIdChange = () => {
  slaveColumnIdWidget.refresh(false);
  formData.value.slaveColumnId = undefined;
};
const formInit = () => {
  if (dialogParams.value.rowData != null) {
    formData.value = {
      ...dialogParams.value.rowData,
    };
    masterColumnIdWidget.onVisibleChange(true);
    slaveDatasetIdWidget.onVisibleChange(true);
    slaveColumnIdWidget.onVisibleChange(true);
  }
};

onMounted(() => {
  formInit();
});
</script>
