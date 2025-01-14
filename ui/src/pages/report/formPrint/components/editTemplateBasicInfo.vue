<template>
  <div class="form-single-fragment" style="position: relative">
    <el-form
      ref="form"
      :model="formData"
      class="full-width-input"
      :rules="rules"
      style="width: 100%"
      label-width="92px"
      :size="layoutStore.defaultFormItemSize"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="模版名称" prop="printName">
            <el-input
              class="input-item"
              v-model="formData.printName"
              :clearable="true"
              placeholder="模版名称"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="模版标识" prop="printVariable">
            <el-input
              class="input-item"
              v-model="formData.printVariable"
              :clearable="true"
              placeholder="只能输入英文字符和数字"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="所属分组" prop="groupId">
            <el-cascader
              class="input-item"
              v-model="formEditPrintTemplate.groupId.value"
              :options="formEditPrintTemplate.groupId.impl.dropdownList"
              filterable
              :clearable="true"
              :show-all-levels="false"
              placeholder="所属分组"
              :props="{ value: 'groupId', label: 'groupName', checkStrictly: true }"
              @visible-change="formEditPrintTemplate.groupId.impl.onVisibleChange"
              @change="onGroupIdValueChange"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="模版类型" prop="templateType">
            <el-radio-group
              v-model="formData.templateType"
              :disabled="isEdit"
              @change="formData.fragmentJson = []"
            >
              <el-radio-button :label="0">Excel模版</el-radio-button>
              <el-radio-button :label="1">Word模版</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <!--
        <el-col :span="12">
          <el-form-item label="打印方向" prop="landscape">
            <el-radio-group v-model="formData.landscape">
              <el-radio-button :value="true">横向</el-radio-button>
              <el-radio-button :value="false">纵向</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="纸张大小" prop="paperType">
            <el-radio-group v-model="formData.paperType">
              <el-radio-button value="A4">A4</el-radio-button>
              <el-radio-button value="A5">A5</el-radio-button>
              <el-radio-button value="custom">自定义</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="formData.paperType === 'custom'">
          <el-form-item label="自定义宽度" prop="customWidth">
            <el-input-number
              v-model="formData.customWidth"
              :min="1"
              size="default"
              aria-label="自定义宽度"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="formData.paperType === 'custom'">
          <el-form-item label="自定义高度" prop="customHeight">
            <el-input-number
              v-model="formData.customHeight"
              :min="1"
              size="default"
              aria-label="自定义高度"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="上边距" prop="topMargin">
            <el-input-number
              v-model="formData.topMargin"
              :min="0"
              :max="100"
              size="default"
              aria-label="上边距"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="下边距" prop="bottomMargin">
            <el-input-number
              v-model="formData.bottomMargin"
              :min="0"
              :max="100"
              size="default"
              aria-label="下边距"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="左边距" prop="leftMargin">
            <el-input-number
              v-model="formData.leftMargin"
              :min="0"
              :max="100"
              size="default"
              aria-label="左边距"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="右边距" prop="rightMargin">
            <el-input-number
              v-model="formData.rightMargin"
              :min="0"
              :max="100"
              size="default"
              aria-label="右边距"
            />
          </el-form-item>
        </el-col>
        -->
        <el-col :span="24">
          <el-form-item label="输入参数">
            <el-select
              v-model="formData.paramList"
              multiple
              filterable
              allow-create
              default-first-option
              placeholder=""
            >
              <el-option
                v-for="item in formData.paramList"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { CascaderValue } from 'element-plus';
import { PrintPaperType, PrintTemplateType } from '@/common/staticDict/report';
import { findTreeNodePath } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import { useDropdown } from '@/common/hooks/useDropdown';
import { DropdownOptions, ListData } from '@/common/types/list';

import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

const props = withDefaults(
  defineProps<{
    templateInfo?: ANY_OBJECT;
    reportPrintGroupList?: ANY_OBJECT[];
  }>(),
  { reportPrintGroupList: () => [] },
);

const isEdit = computed(() => props.templateInfo && props.templateInfo.printId != null);
const form = ref();
const formData = ref<ANY_OBJECT>({
  printId: undefined,
  printName: undefined,
  printVariable: '',
  printType: 1,
  groupId: undefined,
  landscape: false,
  paperType: PrintPaperType.A4,
  customHeight: undefined,
  customWidth: undefined,
  topMargin: 5,
  bottomMargin: 5,
  leftMargin: 5,
  rightMargin: 5,
  templateType: PrintTemplateType.EXCEL,
  paramList: [],
  fragmentJson: [],
});
const rules: ANY_OBJECT = {
  printName: [{ required: true, message: '请输入模版名称', trigger: 'blur' }],
  groupId: [{ required: true, message: '请选择所属分组', trigger: 'blur' }],
  printVariable: [
    { required: true, message: '请输入模版标识', trigger: 'blur' },
    {
      type: 'string',
      pattern: /^[A-Za-z0-9]+$/,
      message: '模版标识只允许输入字母和数字',
      trigger: 'blur',
    },
    {
      type: 'string',
      pattern: /^[A-Za-z][A-Za-z0-9]+$/,
      message: '模版标识不能以数字开头',
      trigger: 'blur',
    },
  ],
  landscape: [{ required: true, message: '请选择打印方向', trigger: 'blur' }],
  paperType: [{ required: true, message: '请选择纸张大小', trigger: 'blur' }],
  customWidth: [{ required: true, message: '自定义宽度不能为空', trigger: 'blur' }],
  customHeight: [{ required: true, message: '自定义高度不能为空', trigger: 'blur' }],
  topMargin: [{ required: true, message: '请输入上边距', trigger: 'blur' }],
  bottomMargin: [{ required: true, message: '请输入下边距', trigger: 'blur' }],
  leftMargin: [{ required: true, message: '请输入左边距', trigger: 'blur' }],
  rightMargin: [{ required: true, message: '请输入右边距', trigger: 'blur' }],
};
/**
 * 所属分组下拉数据获取函数
 */
const loadGroupIdDropdownList = (): Promise<ListData<ANY_OBJECT>> => {
  return Promise.resolve({ dataList: props.reportPrintGroupList });
};
const dropdownOptions: DropdownOptions<ANY_OBJECT> = {
  loadData: loadGroupIdDropdownList,
  isTree: true,
};
const formEditPrintTemplate = reactive({
  groupId: {
    impl: useDropdown(dropdownOptions),
    value: [] as CascaderValue | undefined,
  },
});
/**
 * 所属分组选中值改变
 */
const onGroupIdValueChange = (value: CascaderValue) => {
  formData.value.groupId = Array.isArray(value) ? value[value.length - 1] : undefined;
};
/**
 * 获得打印模版基础信息
 */
const getBasicInfo = () => {
  return new Promise((resolve, reject) => {
    form.value.validate((valid: boolean) => {
      if (!valid) {
        reject();
        return;
      }
      resolve(formData.value);
    });
  });
};
defineExpose({ getBasicInfo });

const formInit = (templateInfo: ANY_OBJECT | undefined) => {
  if (templateInfo && templateInfo.printId != null) {
    formData.value.printName = templateInfo.printName;
    formData.value.groupId = templateInfo.groupId;
    formData.value.printVariable = templateInfo.printVariable;
    formData.value.printType = templateInfo.printType;
    formData.value.landscape = (templateInfo.printJson || {}).landscape;
    formData.value.paperType = (templateInfo.printJson || {}).paperType;
    formData.value.customWidth = (templateInfo.printJson || {}).customWidth;
    formData.value.customHeight = (templateInfo.printJson || {}).customHeight;
    formData.value.topMargin = (templateInfo.printJson || {}).topMargin;
    formData.value.bottomMargin = (templateInfo.printJson || {}).bottomMargin;
    formData.value.leftMargin = (templateInfo.printJson || {}).leftMargin;
    formData.value.rightMargin = (templateInfo.printJson || {}).rightMargin;
    formData.value.paramList = (templateInfo.paramJson || []).map(
      (item: ANY_OBJECT) => item.variableName,
    );
    formData.value.templateType =
      (templateInfo.printJson || {}).templateType || PrintTemplateType.EXCEL;
    formData.value.fragmentJson = templateInfo.fragmentJson || [];
  } else {
    formData.value = {
      printId: undefined,
      printName: undefined,
      printVariable: '',
      printType: 1,
      groupId: undefined,
      landscape: false,
      paperType: PrintPaperType.A4,
      customWidth: undefined,
      customHeight: undefined,
      topMargin: 5,
      bottomMargin: 5,
      leftMargin: 5,
      rightMargin: 5,
      templateType: PrintTemplateType.EXCEL,
      paramList: [],
      fragmentJson: [],
    };
  }
  // 初始化下拉数据
  formEditPrintTemplate.groupId.value = [];
  formEditPrintTemplate.groupId.impl
    .onVisibleChange(true)
    .then(res => {
      formEditPrintTemplate.groupId.value = findTreeNodePath(
        res,
        formData.value.groupId,
        'groupId',
      );
    })
    .catch(e => {
      console.warn(e);
    });
};

watch(
  () => props.templateInfo,
  newValue => {
    formInit(newValue);
  },
  {
    immediate: true,
  },
);
</script>
