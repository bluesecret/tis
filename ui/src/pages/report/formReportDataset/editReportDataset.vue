<template>
  <el-container class="edit-report-dataset">
    <el-header
      class="step-header"
      style="overflow: hidden; height: 72px; background: white; border-bottom: 1px solid #ebeef5"
    >
      <el-row type="flex" justify="space-between" align="middle" style="height: 100%">
        <div class="title" style="display: flex; width: 200px; height: 40px; line-height: 40px">
          <i class="header-logo logo online-icon icon-orange-icon" style="font-size: 40px" />
          <span style="font-size: 22px; color: #333; font-weight: bold">数据集</span>
        </div>
        <StepBar class="step" :value="activeStep">
          <StepBarItem icon="online-icon icon-basic-info" :name="ReportEditDatasetStep.BASIC">{{
            ReportEditDatasetStep.getValue(ReportEditDatasetStep.BASIC)
          }}</StepBarItem>
          <StepBarItem
            v-if="(formData.dataset || {}).datasetType !== DatasetType.API"
            icon="online-icon icon-data"
            :name="ReportEditDatasetStep.PREVIEW"
            >{{ ReportEditDatasetStep.getValue(ReportEditDatasetStep.PREVIEW) }}</StepBarItem
          >
          <StepBarItem icon="online-icon icon-basic-info" :name="ReportEditDatasetStep.COLUMN">{{
            ReportEditDatasetStep.getValue(ReportEditDatasetStep.COLUMN)
          }}</StepBarItem>
          <StepBarItem
            v-if="(formData.dataset || {}).datasetType !== DatasetType.API"
            icon="online-icon icon-basic-info"
            :name="ReportEditDatasetStep.RELATION"
            >{{ ReportEditDatasetStep.getValue(ReportEditDatasetStep.RELATION) }}</StepBarItem
          >
        </StepBar>
        <el-row class="operation" style="width: 200px" type="flex" justify="end">
          <el-button
            :size="formItemSize"
            :disabled="activeStep === ReportEditDatasetStep.BASIC"
            @click="onPrevClick"
          >
            上一步
          </el-button>
          <el-button
            :disabled="
              activeStep === ReportEditDatasetStep.RELATION ||
              (activeStep === ReportEditDatasetStep.COLUMN &&
                formData.dataset.datasetType === DatasetType.API)
            "
            :size="formItemSize"
            type="primary"
            @click="onNextClick"
          >
            下一步
          </el-button>
          <el-button
            v-if="activeStep === ReportEditDatasetStep.BASIC"
            :size="formItemSize"
            type="primary"
            @click="onSaveClick"
          >
            保存
          </el-button>
          <el-button :size="formItemSize" @click="onCancel">退出</el-button>
        </el-row>
      </el-row>
    </el-header>
    <el-main style="padding: 15px; background-color: #f9f9f9">
      <el-row type="flex" justify="center" style="height: 100%">
        <!-- 基础信息 -->
        <div v-if="activeStep === ReportEditDatasetStep.BASIC">
          <EditDatasetBasicInfo
            ref="basicInfo"
            :dataset="formData.dataset"
            :defaultFormItemSize="layoutStore.defaultFormItemSize"
            :groupList="dialogParams.groupList"
          />
        </div>
        <!-- 数据预览 -->
        <div v-if="activeStep === ReportEditDatasetStep.PREVIEW">
          <DatasetPreview
            :datasetId="(formData.dataset || {}).datasetId"
            :defaultFormItemSize="layoutStore.defaultFormItemSize"
            :height="layoutStore.documentClientHeight - 100"
          />
        </div>
        <!-- 字段管理 -->
        <div v-if="activeStep === ReportEditDatasetStep.COLUMN">
          <EditDatasetColumn
            :dataset="formData.dataset"
            :height="layoutStore.documentClientHeight - 100"
            :datasetColumnList="formData.datasetColumnList"
            :dictList="dictList"
            :defaultFormItemSize="layoutStore.defaultFormItemSize"
            @refreshColumns="onRefreshColumns"
            @syncColumns="onSyncColumns"
            @saveColumn="onSaveDatasetColumn"
          />
        </div>
        <!-- 关联管理 -->
        <div v-if="activeStep === ReportEditDatasetStep.RELATION">
          <EditDatasetRelation
            :dataset="formData.dataset"
            :height="layoutStore.documentClientHeight - 100"
            :datasetList="dialogParams.datasetList"
            :defaultFormItemSize="layoutStore.defaultFormItemSize"
          />
        </div>
      </el-row>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import StepBar from '@/components/StepBar/index.vue';
import StepBarItem from '@/components/StepBar/stepItem.vue';
import { findTreeNodePath } from '@/common/utils';
import { DictionaryBase } from '@/common/staticDict/types';
import { DatasetType } from '@/common/staticDict/report';
import { DictionaryController } from '@/api/system';
import { ReportDatasetController } from '@/api/report';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
import EditDatasetBasicInfo from './components/editDatasetBasicInfo.vue';
import DatasetPreview from './components/datasetPreview.vue';
import EditDatasetColumn from './components/editDatasetColumn.vue';
import EditDatasetRelation from './components/editDatasetRelation.vue';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  datasetId?: string;
  groupList?: ANY_OBJECT[];
  datasetList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const basicInfo = ref();

const ReportEditDatasetStep = new DictionaryBase('报表数据集编辑步骤', [
  {
    id: 0,
    name: '基础信息',
    symbol: 'BASIC',
  },
  {
    id: 1,
    name: '数据预览',
    symbol: 'PREVIEW',
  },
  {
    id: 2,
    name: '字段管理',
    symbol: 'COLUMN',
  },
  {
    id: 3,
    name: '数据关联',
    symbol: 'RELATION',
  },
]);

const activeStep = ref(ReportEditDatasetStep.BASIC);
const formData = ref<ANY_OBJECT>({
  dataset: undefined,
  datasetColumnList: [],
});
const dictList = ref<ANY_OBJECT[]>([]);

const dialogParams = computed(() => {
  return {
    datasetId: props.datasetId || thirdParams.value.datasetId,
    groupList: props.groupList || thirdParams.value.groupList || [],
    datasetList: props.datasetList || thirdParams.value.datasetList || [],
  };
});

const reset = () => {
  formData.value.datasetColumnList = [];
  formData.value.dataset = undefined;
};
const loadReportDict = () => {
  dictList.value = [];
  DictionaryController.dictReportDict({})
    .then(res => {
      dictList.value = res.getList();
    })
    .catch(e => {
      console.warn(e);
    });
};
const loadDatasetDetailInfo = (datasetId: string | undefined) => {
  if (!datasetId) {
    reset();
    return;
  }
  let params: ANY_OBJECT = {
    datasetId: datasetId,
  };

  ReportDatasetController.view(params)
    .then(res => {
      let info = null;
      try {
        info = JSON.parse(res.data.datasetInfo);
      } catch (e) {
        info = {
          tableName: undefined,
          sql: undefined,
          paramList: [],
          variables: undefined,
          url: undefined,
          method: 'get',
          columnList: undefined,
        };
      }

      formData.value.dataset = {
        datasetId: res.data.datasetId,
        datasetName: res.data.datasetName,
        groupId: res.data.groupId,
        dblinkId: res.data.dblinkId,
        datasetType: res.data.datasetType,
        datasetInfo: info,
        tableName: res.data.tableName,
        sql: info.sql,
        paramList: info.paramList,
        variables: info.variables,
        groupPath: findTreeNodePath(dialogParams.value.groupList, res.data.groupId, 'groupId'),
      };
      if (res.data.datasetType === DatasetType.API) {
        formData.value.datasetColumnList = info.columnList;
      } else {
        formData.value.datasetColumnList = res.data.columnList;
      }
    })
    .catch(e => {
      console.log(e);
    });
};
const onPrevClick = () => {
  switch (activeStep.value) {
    case ReportEditDatasetStep.RELATION:
      activeStep.value = ReportEditDatasetStep.COLUMN;
      break;
    case ReportEditDatasetStep.COLUMN:
      if (formData.value.dataset.datasetType === DatasetType.API) {
        activeStep.value = ReportEditDatasetStep.BASIC;
      } else {
        activeStep.value = ReportEditDatasetStep.PREVIEW;
      }
      break;
    case ReportEditDatasetStep.PREVIEW:
      activeStep.value = ReportEditDatasetStep.BASIC;
      break;
  }
};
const onNextClick = () => {
  switch (activeStep.value) {
    case ReportEditDatasetStep.BASIC:
      onSaveClick()
        .then(() => {
          if (!formData.value.dataset) {
            console.warn('formData.value.dataset is undefined');
            return;
          }
          if (formData.value.dataset.datasetType === DatasetType.API) {
            activeStep.value = ReportEditDatasetStep.COLUMN;
          } else {
            activeStep.value = ReportEditDatasetStep.PREVIEW;
          }
        })
        .catch((e: Error) => {
          console.warn(e);
        });
      break;
    case ReportEditDatasetStep.PREVIEW:
      activeStep.value = ReportEditDatasetStep.COLUMN;
      break;
    case ReportEditDatasetStep.COLUMN:
      if (formData.value.dataset.datasetType !== DatasetType.API) {
        activeStep.value = ReportEditDatasetStep.RELATION;
      }
      break;
  }
};
const onRefreshColumns = () => {
  loadDatasetDetailInfo(formData.value.dataset.datasetId);
};
const onSyncColumns = () => {
  let params = {
    datasetId: formData.value.dataset.datasetId,
  };

  ReportDatasetController.syncColumns(params)
    .then(res => {
      ElMessage.success('同步成功！');
      formData.value.datasetColumnList = res.data;
    })
    .catch(e => {
      console.warn(e);
    });
};
const onSaveDatasetColumn = (columnList: ANY_OBJECT[]) => {
  if (formData.value.dataset == null) formData.value.dataset = {};
  if (formData.value.dataset.datasetInfo == null) formData.value.dataset.datasetInfo = {};
  formData.value.dataset.datasetInfo.columnList = columnList;
  saveImpl({
    datasetId: formData.value.dataset.datasetId,
    datasetName: formData.value.dataset.datasetName,
    groupId: formData.value.dataset.groupId,
    dblinkId: formData.value.dataset.dblinkId,
    datasetType: formData.value.dataset.datasetType,
    tableName: formData.value.dataset.tableName,
    sql: formData.value.dataset.sql,
    paramList: formData.value.dataset.paramList,
    variables: formData.value.dataset.variables,
    datasetInfo: {
      ...formData.value.dataset.datasetInfo,
    },
  })
    .then(() => {
      loadDatasetDetailInfo(formData.value.dataset.datasetId);
      ElMessage.success('保存成功');
    })
    .catch(e => {
      console.warn(e);
    });
};
const saveImpl = (data: ANY_OBJECT) => {
  if (data == null) return Promise.reject();
  return new Promise((resolve, reject) => {
    let params = null;
    if (data.datasetId == null) {
      params = {
        reportDatasetDto: {
          datasetName: data.datasetName,
          groupId: data.groupId,
          dblinkId: data.dblinkId,
          datasetType: data.datasetType,
          tableName: data.tableName,
          datasetInfo: JSON.stringify({
            tableName: data.tableName,
            sql: data.sql,
            paramList: data.paramList,
            variables: data.variables,
            url: (data.datasetInfo || {}).url,
            method: (data.datasetInfo || {}).method,
            columnList: (data.datasetInfo || {}).columnList,
          }),
        },
      };
    } else {
      params = {
        reportDatasetDto: {
          datasetId: data.datasetId,
          datasetName: data.datasetName,
          groupId: data.groupId,
          dblinkId: data.dblinkId,
          datasetType: data.datasetType,
          tableName: data.tableName,
          datasetInfo: JSON.stringify({
            tableName: data.tableName,
            sql: data.sql,
            paramList: data.paramList,
            variables: data.variables,
            url: (data.datasetInfo || {}).url,
            method: (data.datasetInfo || {}).method,
            columnList: (data.datasetInfo || {}).columnList,
          }),
        },
      };
    }

    let httpCall =
      data.datasetId == null
        ? ReportDatasetController.add(params)
        : ReportDatasetController.update(params);
    httpCall
      .then(res => {
        resolve(res.data || data.datasetId);
      })
      .catch(e => {
        console.log(e);
        reject(e);
      });
  });
};
const onSaveClick = () => {
  if (activeStep.value === ReportEditDatasetStep.BASIC) {
    return new Promise((resolve, reject) => {
      basicInfo.value
        .getBasicInfo()
        .then((data: ANY_OBJECT) => {
          return saveImpl(data);
        })
        .then((datasetId: string) => {
          loadDatasetDetailInfo(datasetId);
          ElMessage.success('保存成功');
          resolve(true);
        })
        .catch((e: Error) => {
          reject(e);
        });
    });
  } else {
    return Promise.resolve();
  }
};
const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(true);
  }
};

onMounted(() => {
  loadReportDict();
});

watch(
  () => dialogParams.value.datasetId,
  newValue => {
    loadDatasetDetailInfo(newValue);
  },
  {
    immediate: true,
  },
);

// watch(
//   () => formData.value.dataset,
//   () => {
//     if (!formData.value.dataset) {
//       console.warn('watch formData.value.dataset is undefined');
//       return;
//     }
//     if (!init) {
//       console.log('editReportDataset inited');
//       activeStep.value = ReportEditDatasetStep.BASIC;
//       init = true;
//       return;
//     }
//     if (formData.value.dataset.datasetType === DatasetType.API) {
//       activeStep.value = ReportEditDatasetStep.COLUMN;
//     } else {
//       activeStep.value = ReportEditDatasetStep.PREVIEW;
//     }
//   },
// );
</script>

<style scoped>
.edit-report-dataset {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 100;
  width: 100vw;
  height: 100vh;
  background: #f9f9f9;
}
</style>
