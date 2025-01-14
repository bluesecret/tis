<template>
  <el-container class="edit-report-page">
    <el-header
      class="step-header"
      style="overflow: hidden; height: 72px; background: white; border-bottom: 1px solid #ebeef5"
    >
      <el-row type="flex" justify="space-between" align="middle" style="height: 100%">
        <div class="title" style="display: flex; width: 200px; height: 40px; line-height: 40px">
          <i class="header-logo logo online-icon icon-orange-icon" style="font-size: 40px" />
          <span style="font-size: 22px; color: #333; font-weight: bold">橙单统计表单</span>
        </div>
        <StepBar class="step" :value="activeStep">
          <StepBarItem icon="online-icon icon-basic-info" :name="OnlineReportStep.BASIC"
            >基础信息</StepBarItem
          >
          <StepBarItem icon="online-icon icon-data" :name="OnlineReportStep.DESIGN"
            >表单设计</StepBarItem
          >
        </StepBar>
        <el-row class="operation" type="flex" justify="end" style="width: 200px">
          <el-button
            v-if="activeStep === OnlineReportStep.BASIC"
            size="default"
            type="primary"
            @click="onNextClick"
          >
            下一步
          </el-button>
          <el-button size="default" type="primary" @click="onSaveClick"> 保存 </el-button>
          <el-button @click="onClose" size="default">
            {{ activeStep === OnlineReportStep.BASIC ? '退出' : '返回' }}
          </el-button>
        </el-row>
      </el-row>
    </el-header>
    <el-main :style="{ padding: activeStep === OnlineReportStep.DESIGN ? '0px' : '15px' }">
      <el-row justify="center" style="width: 100%; height: 100%">
        <!-- 基础信息 -->
        <div v-show="activeStep === OnlineReportStep.BASIC" class="main-box" style="width: 600px">
          <el-form
            ref="basicForm"
            :model="pageInfo"
            class="full-width-input"
            :rules="rules"
            label-width="80px"
            :size="formItemSize"
            @submit.prevent
          >
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="所属分组" prop="groupId">
                  <el-cascader
                    class="input-item"
                    v-model="pageInfo.groupPath"
                    :options="groupTree"
                    filterable
                    :clearable="true"
                    :show-all-levels="false"
                    placeholder="所属分组"
                    :props="{ value: 'groupId', label: 'groupName', checkStrictly: true }"
                    @change="onGroupIdValueChange"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="页面名称" prop="pageName">
                  <el-input
                    class="input-item"
                    v-model="pageInfo.pageName"
                    :clearable="true"
                    placeholder="页面名称"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="页面编码" prop="pageCode">
                  <el-input
                    class="input-item"
                    v-model="pageInfo.pageCode"
                    :clearable="true"
                    placeholder="页面编码"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
        <!-- 页面设计 -->
        <ReportDesign
          ref="reportDesign"
          v-if="activeStep === OnlineReportStep.DESIGN"
          style="width: 100%"
          :pageInfo="pageInfo"
          :dictList="dictList"
          :pageGroupList="groupTree"
          :height="clientHeight"
          @updateForm="updateFormInfo"
        />
      </el-row>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { CascaderValue, ElMessage } from 'element-plus';
import StepBar from '@/components/StepBar/index.vue';
import StepBarItem from '@/components/StepBar/stepItem.vue';
import { treeDataTranslate, findTreeNodePath } from '@/common/utils';
import { ReportPageController } from '@/api/report';
import { DictionaryController } from '@/api/system';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
import { getReportFormConfig } from '../reportFormConfig/index';
import ReportDesign from '../reportDesign/index.vue';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  pageId?: string;
  groupList: ANY_OBJECT[];
  clientHeight: ANY_OBJECT;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<boolean>;
}
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const basicForm = ref();
const reportDesign = ref();
const activeStep = ref('basicInfo');
const pageInfo = ref<ANY_OBJECT>({
  pageId: undefined,
  pageName: undefined,
  pageCode: undefined,
  groupId: undefined,
  groupPath: [],
  formConfig: {
    labelWidth: 100,
    labelPosition: 'right',
  },
});
const dictList = ref<ANY_OBJECT[]>([]);
const rules = {
  pageName: [{ required: true, message: '请输入表单名称', trigger: 'blur' }],
  pageCode: [{ required: true, message: '请输入表单编码', trigger: 'blur' }],
  groupId: [{ required: true, message: '请选择所属分组', trigger: 'blur' }],
};

const dialogParams = computed(() => {
  return {
    pageId: props.pageId || thirdParams.value.pageId,
    groupList: props.groupList || thirdParams.value.groupList,
  };
});
const groupTree = computed(() => {
  return treeDataTranslate(
    dialogParams.value.groupList.map(item => {
      return {
        ...item,
      };
    }),
    'groupId',
    'parentId',
  );
});
const OnlineReportStep = computed(() => {
  return {
    BASIC: 'basicInfo',
    DESIGN: 'design',
  };
});

const loadReportDictList = () => {
  dictList.value = [];
  DictionaryController.dictReportDict({})
    .then(res => {
      dictList.value = res.getList();
      dictList.value.forEach(item => {
        item.dictData = item.dictDataJson ? JSON.parse(item.dictDataJson) : undefined;
      });
    })
    .catch(e => {
      console.warn(e);
    });
};
const onPrevClick = () => {
  if (activeStep.value === OnlineReportStep.value.DESIGN) {
    activeStep.value = OnlineReportStep.value.BASIC;
  }
};
const onNextClick = () => {
  if (activeStep.value === OnlineReportStep.value.BASIC) {
    saveWidgetImpl()
      .then(res => {
        if (pageInfo.value.pageId == null) pageInfo.value.pageId = res.data;
        activeStep.value = OnlineReportStep.value.DESIGN;
      })
      .catch(e => {
        console.log(e);
      });
  }
};
const saveWidgetImpl = (formInfo: ANY_OBJECT | null = null) => {
  return new Promise<ANY_OBJECT>((resolve, reject) => {
    basicForm.value.validate((valid: boolean) => {
      if (!valid) {
        reject();
        return;
      }
      let isEdit = pageInfo.value.pageId != null;
      pageInfo.value.pageJson = isEdit
        ? formInfo == null
          ? pageInfo.value.pageJson
          : JSON.stringify(formInfo)
        : JSON.stringify(getReportFormConfig());
      let params = {
        reportPageDto: {
          pageId: pageInfo.value.pageId,
          pageName: pageInfo.value.pageName,
          pageCode: pageInfo.value.pageCode,
          groupId: pageInfo.value.groupId,
          pageJson: pageInfo.value.pageJson,
        },
      };
      let httpCall = isEdit
        ? ReportPageController.update(params)
        : ReportPageController.add(params);
      httpCall
        .then(res => {
          pageInfo.value = parsePageInfo(pageInfo.value);
          if (!isEdit) pageInfo.value.pageId = res.data;
          ElMessage.success('保存成功！');
          resolve(res);
        })
        .catch(() => {
          reject();
        });
    });
  });
};
const onSaveClick = () => {
  if (activeStep.value === OnlineReportStep.value.BASIC) {
    saveWidgetImpl().catch(e => {
      console.warn(e);
    });
  } else if (activeStep.value === OnlineReportStep.value.DESIGN) {
    if (reportDesign.value) reportDesign.value.saveFormInfo();
  }
};
const onClose = () => {
  if (activeStep.value === OnlineReportStep.value.BASIC) {
    if (props.dialog) {
      props.dialog.cancel();
    } else {
      onCloseThirdDialog(true);
    }
  } else {
    onPrevClick();
  }
};
const onGroupIdValueChange = (val: CascaderValue) => {
  if (Array.isArray(val)) {
    pageInfo.value.groupId = val[val.length - 1];
  } else {
    pageInfo.value.groupId = undefined;
  }
};
const parsePageInfo = (pageInfo: ANY_OBJECT) => {
  let defaultFormConfig = getReportFormConfig();
  let allAllowMode = ['pc', 'mobile'];
  let tempInfo = JSON.parse(pageInfo.pageJson);
  tempInfo = allAllowMode.reduce((temp: ANY_OBJECT, mode: string) => {
    temp[mode] = tempInfo[mode] || defaultFormConfig[mode];
    return temp;
  }, {});

  let temp = {
    pageId: pageInfo.pageId,
    pageName: pageInfo.pageName,
    pageCode: pageInfo.pageCode,
    groupId: pageInfo.groupId,
    groupPath: findTreeNodePath(dialogParams.value.groupList, pageInfo.groupId, 'groupId'),
    pageJson: JSON.stringify(tempInfo),
    formInfo: tempInfo || {},
  };

  temp.formInfo = Object.keys(temp.formInfo).reduce((retObj: ANY_OBJECT, modelKey: string) => {
    let formInfo = temp.formInfo[modelKey];
    retObj[modelKey] = {
      pageId: temp.pageId,
      pageName: temp.pageName,
      pageCode: temp.pageCode,
      gutter: modelKey === 'mobile' ? undefined : formInfo.gutter || 20,
      labelWidth: modelKey === 'mobile' ? undefined : formInfo.labelWidth || 100,
      labelPosition: modelKey === 'mobile' ? undefined : formInfo.labelPosition || 'left',
      customFieldList: formInfo.customFieldList || [],
      filterItemWidth: formInfo.filterItemWidth || 350,
      widgetList: formInfo.widgetList || [],
      paramList: formInfo.paramList || [],
    };
    return retObj;
  }, {});

  return temp;
};
const loadReportPage = (pageId: string | undefined | null) => {
  if (pageId) {
    ReportPageController.view({
      pageId: pageId,
    })
      .then(res => {
        pageInfo.value = parsePageInfo(res.data);
      })
      .catch(e => {
        console.log(e);
      });
  }
};
const buildSaveWidget = (widget: ANY_OBJECT) => {
  if (widget == null) return widget;
  let oldWidgetImpl = widget.widgetImpl;
  let childWidgetList = widget.childWidgetList;
  let oldColumnList = widget.columnList;
  widget.widgetImpl = undefined;
  widget.childWidgetList = [];
  widget.columnList = undefined;
  let temp = JSON.parse(JSON.stringify(widget));
  widget.widgetImpl = oldWidgetImpl;
  widget.childWidgetList = childWidgetList;
  widget.columnList = oldColumnList;
  temp.dataset = undefined;
  temp.bindData.dataset = undefined;
  temp.widgetImpl = undefined;
  if (temp.props && temp.props.dictInfo) temp.props.dictInfo.dict = undefined;
  if (temp.props && Array.isArray(temp.props.tableColumnList)) {
    temp.props.tableColumnList.forEach((tableColumn: ANY_OBJECT) => {
      tableColumn.table = undefined;
      tableColumn.column = undefined;
    });
  }
  if (Array.isArray(widget.childWidgetList)) {
    temp.childWidgetList = widget.childWidgetList.map(item => {
      return buildSaveWidget(item);
    });
  }
  return temp;
};
const updateFormInfo = (formInfo: ANY_OBJECT) => {
  if (formInfo != null) {
    let allowMode = ['pc', 'mobile'];
    let tempInfo = allowMode.reduce((formConfig: ANY_OBJECT, mode: string) => {
      let tempFormConfig = formInfo[mode];
      formConfig[mode] = {
        ...tempFormConfig,
        widgetList: tempFormConfig.widgetList.map((widget: ANY_OBJECT) => {
          return buildSaveWidget(widget);
        }),
      };
      return formConfig;
    }, {});
    saveWidgetImpl(tempInfo);
  }
};

onMounted(() => {
  loadReportPage(dialogParams.value.pageId);
  loadReportDictList();
});
</script>

<style scoped>
.header-logo {
  width: 40px;
  height: 40px;
  margin-right: 8px;
  font-size: 28px;
  text-align: center;
  background: rgb(255 119 0 / 10%);
  border-radius: 8px;
  line-height: 40px;
}

.edit-report-page {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 100;
  width: 100vw;
  height: 100vh;
  background: #f6f6f6 !important;
}

.main-box {
  height: 100%;
  padding: 20px;
  background: white;
}

.design-box {
  height: 100%;
  padding: 0;
  border-radius: 3px;
}
</style>
