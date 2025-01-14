<template>
  <el-container class="edit-print-template">
    <el-header
      class="step-header"
      style="overflow: hidden; height: 72px; background: white; border-bottom: 1px solid #ebeef5"
    >
      <el-row type="flex" justify="space-between" align="middle" style="height: 100%">
        <div class="title" style="display: flex; width: 200px; height: 40px; line-height: 40px">
          <i class="header-logo logo online-icon icon-orange-icon" style="font-size: 40px" />
          <span style="font-size: 22px; color: #333; font-weight: bold">橙单打印设计</span>
        </div>
        <StepBar class="step" :value="activeStep">
          <StepBarItem icon="online-icon icon-basic-info" :name="PrintTemplateEditStep.BASIC"
            >基础信息</StepBarItem
          >
          <StepBarItem icon="online-icon icon-data" :name="PrintTemplateEditStep.TEMPLATE"
            >编辑模版</StepBarItem
          >
        </StepBar>
        <el-row class="operation" type="flex" justify="end">
          <el-button
            size="default"
            :disabled="activeStep === PrintTemplateEditStep.BASIC"
            @click="onPrevClick"
          >
            上一步
          </el-button>
          <el-button
            v-if="activeStep !== PrintTemplateEditStep.TEMPLATE"
            size="default"
            type="primary"
            @click="onNextClick"
          >
            下一步
          </el-button>
          <el-button
            v-if="activeStep === PrintTemplateEditStep.TEMPLATE"
            size="default"
            type="primary"
            @click="onSaveClick"
          >
            保存
          </el-button>
          <el-button size="default" @click="onCancelClick">退出</el-button>
        </el-row>
      </el-row>
    </el-header>
    <el-main style="background: #f8f8f8">
      <el-row
        type="flex"
        justify="center"
        style="height: 100%"
        :style="{ padding: activeStep === PrintTemplateEditStep.BASIC ? '15px' : '0px' }"
      >
        <!-- 基础信息 -->
        <div v-if="activeStep === PrintTemplateEditStep.BASIC && isReady" class="basic-step">
          <EditTemplateBasicInfo
            ref="basic"
            :templateInfo="formData"
            :reportPrintGroupList="dialogParams.reportPrintGroupList"
          />
        </div>
        <!-- 编辑模版 -->
        <div
          v-if="activeStep === PrintTemplateEditStep.TEMPLATE && isReady"
          class="template-edit-step"
        >
          <EditTemplateTable
            v-if="formData.templateType === PrintTemplateType.EXCEL"
            ref="templateSheet"
            :printInfo="formData"
          />
          <EditTemplateWord
            v-if="formData.templateType === PrintTemplateType.WORD"
            ref="templateWord"
            :printInfo="formData"
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
import { ReportPrintController } from '@/api/report';
import { PrintTemplateType, PrintTemplateEditStep } from '@/common/staticDict/report';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import EditTemplateBasicInfo from './components/editTemplateBasicInfo.vue';
import EditTemplateTable from './components/editTemplateTable.vue';
import EditTemplateWord from './components/editTemplateWord.vue';

interface IProps extends ThirdProps {
  printId?: string;
  reportPrintGroupList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<boolean>;
}
const props = defineProps<IProps>();

const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const basic = ref();
const templateSheet = ref();
const isReady = ref(false);
const activeStep = ref(PrintTemplateEditStep.BASIC);
const formData = ref<ANY_OBJECT>({
  printId: undefined,
  groupId: undefined,
  printName: undefined,
  printVariable: undefined,
  printJson: undefined,
  fragmentJson: [],
  sheetDataJson: undefined,
  datasetJson: undefined,
  templateType: PrintTemplateType.EXCEL,
  paramJson: [],
});
const dialogParams = computed(() => {
  return {
    printId: props.printId || thirdParams.value.printId,
    reportPrintGroupList:
      props.reportPrintGroupList || thirdParams.value.reportPrintGroupList || [],
  };
});

const onPrevClick = () => {
  switch (activeStep.value) {
    case PrintTemplateEditStep.TEMPLATE:
      if (templateSheet.value) templateSheet.value.getSheetData();
      activeStep.value = PrintTemplateEditStep.BASIC;
      break;
    default:
      break;
  }
};
const onNextClick = () => {
  switch (activeStep.value) {
    case PrintTemplateEditStep.BASIC:
      // 获取基础信息
      basic.value
        .getBasicInfo()
        .then((basicInfo: ANY_OBJECT) => {
          formData.value.printName = basicInfo.printName;
          formData.value.printVariable = basicInfo.printVariable;
          formData.value.groupId = basicInfo.groupId;
          formData.value.templateType = basicInfo.templateType;
          formData.value.fragmentJson = basicInfo.fragmentJson;
          formData.value.printJson = {
            landscape: basicInfo.landscape,
            paperType: basicInfo.paperType,
            customSize: basicInfo.paperType === 'custom',
            customHeight: basicInfo.customHeight,
            customWidth: basicInfo.customWidth,
            topMargin: basicInfo.topMargin,
            bottomMargin: basicInfo.bottomMargin,
            leftMargin: basicInfo.leftMargin,
            rightMargin: basicInfo.rightMargin,
            templateType: basicInfo.templateType,
          };
          formData.value.paramJson = basicInfo.paramList.map((item: ANY_OBJECT) => {
            return {
              variableName: item,
            };
          });
          return onSaveImpl();
        })
        .then(() => {
          ElMessage.success('保存成功！');
          activeStep.value = PrintTemplateEditStep.TEMPLATE;
        })
        .catch((e: Error) => {
          console.log(e);
        });
      break;
    default:
      break;
  }
};
const onSaveImpl = () => {
  return new Promise((resolve, reject) => {
    let params = {
      reportPrintDto: {
        ...formData.value,
      },
    };
    if (templateSheet.value && activeStep.value === PrintTemplateEditStep.TEMPLATE) {
      let sheetData = templateSheet.value.getSheetData();
      params.reportPrintDto.sheetDataJson = sheetData;
    } else {
      params.reportPrintDto.sheetDataJson = undefined;
    }

    if (Array.isArray(params.reportPrintDto.sheetDataJson)) {
      params.reportPrintDto.sheetDataJson = JSON.stringify(params.reportPrintDto.sheetDataJson);
    }

    params.reportPrintDto.printType =
      params.reportPrintDto.printJson.templateType === PrintTemplateType.EXCEL ? 1 : 2;

    let httpCall =
      formData.value.printId == null
        ? ReportPrintController.add(params)
        : ReportPrintController.update(params);
    httpCall
      .then(res => {
        if (formData.value.printId == null) formData.value.printId = res.data;
        resolve(true);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const onSaveClick = () => {
  // 验证打印模版是否可以保存
  if (
    formData.value.templateType === PrintTemplateType.EXCEL &&
    (templateSheet.value == null || !templateSheet.value.checkValidate())
  ) {
    return;
  }
  onSaveImpl()
    .then(() => {
      ElMessage.success('保存成功！');
    })
    .catch(e => {
      console.warn(e);
    });
};
const onCancelClick = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(true);
  }
};
const initFormData = (printId: string | undefined) => {
  if (!printId) {
    isReady.value = true;
    return;
  }
  ReportPrintController.view({
    printId: printId,
  })
    .then(res => {
      if (res.data.printJson) res.data.printJson = JSON.parse(res.data.printJson);
      if (res.data.fragmentJson) {
        res.data.fragmentJson = JSON.parse(res.data.fragmentJson);
      } else {
        res.data.fragmentJson = [];
      }
      if (res.data.datasetJson) res.data.datasetJson = JSON.parse(res.data.datasetJson);
      if (res.data.paramJson) res.data.paramJson = JSON.parse(res.data.paramJson);
      if (res.data.sheetDataJson) res.data.sheetDataJson = JSON.parse(res.data.sheetDataJson);
      formData.value = {
        ...res.data,
        templateType: (res.data.printJson || {}).templateType || PrintTemplateType.EXCEL,
      };
      isReady.value = true;
    })
    .catch(() => {
      isReady.value = true;
    });
};

watch(
  () => dialogParams.value.printId,
  newValue => {
    initFormData(newValue);
  },
  {
    immediate: true,
  },
);
</script>

<style scoped>
.edit-print-template {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 100;
  width: 100vw;
  height: 100vh;
  background: #f9f9f9;
}

.edit-print-template .basic-step {
  width: 700px;
  padding: 20px;
  background: white;
  border-radius: 5px;
}

.edit-print-template .dataset-step {
  width: 800px;
  padding: 20px;
  background: white;
  border-radius: 5px;
}

.edit-print-template .template-edit-step {
  width: 100vw;
}
</style>
