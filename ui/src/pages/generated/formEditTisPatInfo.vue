<template>
  <div class="dialog-box" style="position: relative">
    <el-scrollbar class="custom-scroll content-box">
      <el-form
        ref="formEditTisPatInfoRef"
        :model="formData"
        :size="layoutStore.defaultFormItemSize"
        :rules="rules"
        label-width="120px"
        label-position="right"
        @submit.prevent
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="TisPatInfo.patName">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.patName"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批次号" prop="TisPatInfo.batchNo">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.batchNo"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="TisPatInfo.age">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.age"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检测项目" prop="TisPatInfo.projectId">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.projectId"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="TisPatInfo.sex">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.sex"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="样本编号" prop="TisPatInfo.sampleNo">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.sampleNo"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="患者编号" prop="TisPatInfo.patNo">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.patNo"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="样本类型" prop="TisPatInfo.sampleType">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.sampleType"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作人员" prop="TisPatInfo.operator">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.operator"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="cotful值" prop="TisPatInfo.cutoffVal">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.cutoffVal"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="范围" prop="TisPatInfo.rangeVal">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.rangeVal"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="患者卡条" prop="TisPatInfo.picPath">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.picPath"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检测时间" prop="TisPatInfo.testTime">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.testTime"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检测单位" prop="TisPatInfo.testUnit">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.testUnit"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检测状态" prop="TisPatInfo.testStat">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.testStat"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="文件" prop="TisPatInfo.filePath">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.filePath"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备用字段1" prop="TisPatInfo.remark1">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.remark1"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备用字段2" prop="TisPatInfo.remark2">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.remark2"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备用字段3" prop="TisPatInfo.remark3">
              <el-input
                class="input-item"
                v-model="formData.TisPatInfo.remark3"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-scrollbar>
    <el-row class="footer-box" type="flex" justify="end" align="middle">
      <el-button :size="layoutStore.defaultFormItemSize" @click="onCancel()">取消</el-button>
      <el-button :size="layoutStore.defaultFormItemSize" type="primary" @click="onSubmitTisPatInfoClick()">保存</el-button>
    </el-row>
  </div>
</template>

<script lang="ts">
export default {
  name: 'formEditTisPatInfo',
};
</script>

<script setup lang="ts">
import { DialogProp } from '@/components/Dialog/types';
import * as validateRules from '@/common/utils/validate';
import { VxeColumn, VxeTable } from 'vxe-table';
import { ANY_OBJECT } from '@/types/generic';
import { DictData, DictionaryBase } from '@/common/staticDict/types';
import { ElMessage, ElMessageBox, UploadFile } from 'element-plus';
import { useRoute, useRouter } from 'vue-router';
import { useCommon } from '@/common/hooks/useCommon';
import { useLayoutStore, useStaticDictStore } from '@/store';
import { useDownload } from '@/common/hooks/useDownload';
import { useDropdown } from '@/common/hooks/useDropdown';
import { DropdownOptions, ListData } from '@/common/types/list';
import { useTable } from '@/common/hooks/useTable';
import { TableOptions } from '@/common/types/pagination';
import { useUpload } from '@/common/hooks/useUpload';
import { useUploadWidget } from '@/common/hooks/useUploadWidget';
import { DictionaryController } from '@/api/system';
import { treeDataTranslate, findItemFromList, findTreeNodePath, findTreeNode, stringCase } from '@/common/utils';
import { TisPatInfoData } from '@/api/generated/tisPatInfoController';
import { TisPatResultData } from '@/api/generated/tisPatResultController';
import { TisPatInfoController, TisPatResultController } from '@/api/generated';

const router = useRouter();
const route = useRoute();
const layoutStore = useLayoutStore();
const { downloadFile } = useDownload();
const { getUploadHeaders, getUploadActionUrl, fileListToJson, parseUploadData, getPictureList } = useUpload();
const { 
  Delete,
  Search,
  Edit,
  Plus,
  Refresh,
  Picture,
  Dialog,
  mainContextHeight,
  clientHeight,
  checkPermCodeExist,
  parseParams,
  parseArrayParams,
  formatDateByStatsType,
  getDateRangeFilter,
} = useCommon();
// 静态字典
const { staticDict: StaticDict } = useStaticDictStore();

const props = withDefaults(
  defineProps<{
    subPage?: number | string | boolean;
    id?: ANY_OBJECT;
    saveOnSubmit?: boolean;
    rowData?: ANY_OBJECT;
    // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
    dialog?: DialogProp<ANY_OBJECT[]>;
  }>(),
  {
    subPage: 0,
    id: undefined,
    saveOnSubmit: true,
    rowData: undefined,
    dialog: undefined,
  },
);

const formEditTisPatInfoRef = ref();
// 表单数据定义
type FormEditTisPatInfoData = {
  TisPatInfo: TisPatInfoData;
};
// 表单数据
const formData = reactive<FormEditTisPatInfoData>({
  TisPatInfo: {
    // 主键Id
    id: undefined,
    // 姓名
    patName: undefined,
    // 批次号
    batchNo: undefined,
    // 年龄
    age: undefined,
    // 检测项目
    projectId: undefined,
    // 性别
    sex: undefined,
    // 样本编号
    sampleNo: undefined,
    // 患者编号
    patNo: undefined,
    // 样本类型
    sampleType: undefined,
    // 操作人员
    operator: undefined,
    // cotful值
    cutoffVal: undefined,
    // 范围
    rangeVal: undefined,
    // 患者卡条图片路径
    picPath: undefined,
    // 检测时间
    testTime: undefined,
    // 检测单位
    testUnit: undefined,
    // 检测状态
    testStat: undefined,
    // 文件
    filePath: undefined,
    // 备用字段1
    remark1: undefined,
    // 备用字段2
    remark2: undefined,
    // 备用字段3
    remark3: undefined,
    // 患者检测结果数据
    tisPatResultList: [],
  },
},
);
// 表单验证规则
const rules = reactive({
  'TisPatInfo.operator': [
  ],
  'TisPatInfo.cutoffVal': [
  ],
  'TisPatInfo.rangeVal': [
  ],
  'TisPatInfo.patName': [
  ],
  'TisPatInfo.sex': [
  ],
  'TisPatInfo.filePath': [
  ],
  'TisPatInfo.sampleNo': [
  ],
  'TisPatInfo.remark1': [
  ],
  'TisPatInfo.patNo': [
  ],
  'TisPatInfo.remark2': [
  ],
  'TisPatInfo.sampleType': [
  ],
  'TisPatInfo.remark3': [
  ],
  'TisPatInfo.picPath': [
  ],
  'TisPatInfo.batchNo': [
  ],
  'TisPatInfo.testTime': [
  ],
  'TisPatInfo.age': [
  ],
  'TisPatInfo.testUnit': [
  ],
  'TisPatInfo.projectId': [
  ],
  'TisPatInfo.testStat': [
  ],
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  }
};

const isEdit = computed(() => {
  return props.saveOnSubmit ? props.id != null : props.rowData != null;
});

// 初始化页面数据
const loadTisPatInfoData = () => {
  return new Promise<void>((resolve, reject) => {
    if (!isEdit.value) {
      resolve();
      return;
    }
    if (!props.saveOnSubmit && props.rowData != null) {
      formData.TisPatInfo = JSON.parse(JSON.stringify(props.rowData));
      resolve();
      return;
    }
    let params: ANY_OBJECT = {
      id: props.id
    };
    TisPatInfoController.view(params).then(res => {
      formData.TisPatInfo = { ...res.data };
      resolve();
    }).catch(e => {
      reject(e);
    });
  });
};
const onUploadError = () => {
  ElMessage.error('文件上传失败');
};
const onUploadLimit = () => {
  ElMessage.error('已经超出最大上传个数限制');
};
const refreshFormEditTisPatInfo = () => {
  // 刷新段落
};
/**
 * 重置过滤值
 */
const resetFormEditTisPatInfo = () => {
  refreshFormEditTisPatInfo();
};
/**
 * 重置所有过滤值
 */
const resetFilter = () => {
  resetFormEditTisPatInfo();
};
/**
 * 保存
 */
const onSubmitTisPatInfoClick = () => {
  formEditTisPatInfoRef.value.validate((valid) => {
    if (!valid) return;
    // 级联操作
    if (!props.saveOnSubmit) {
      let retFormData = {
        ...formData.TisPatInfo
      };
  props.dialog?.submit(retFormData);
      return;
    }
    let params: ANY_OBJECT = {
      tisPatInfoDto: {
        id: formData.TisPatInfo.id,
        patName: formData.TisPatInfo.patName,
        batchNo: formData.TisPatInfo.batchNo,
        age: formData.TisPatInfo.age,
        projectId: formData.TisPatInfo.projectId,
        sex: formData.TisPatInfo.sex,
        sampleNo: formData.TisPatInfo.sampleNo,
        patNo: formData.TisPatInfo.patNo,
        sampleType: formData.TisPatInfo.sampleType,
        operator: formData.TisPatInfo.operator,
        cutoffVal: formData.TisPatInfo.cutoffVal,
        rangeVal: formData.TisPatInfo.rangeVal,
        picPath: formData.TisPatInfo.picPath,
        testTime: formData.TisPatInfo.testTime,
        testUnit: formData.TisPatInfo.testUnit,
        testStat: formData.TisPatInfo.testStat,
        filePath: formData.TisPatInfo.filePath,
        remark1: formData.TisPatInfo.remark1,
        remark2: formData.TisPatInfo.remark2,
        remark3: formData.TisPatInfo.remark3,
      }
    };

    let httpCall = isEdit.value ? TisPatInfoController.update : TisPatInfoController.add;
    httpCall(params).then(res => {
      ElMessage.success('保存成功');
      props.dialog?.submit();
    }).catch(e => {
      // TODO: 异常处理
      console.error(e);
    });
  });
};
const formInit = () => {
  loadTisPatInfoData().then(res => {
    if (isEdit.value) refreshFormEditTisPatInfo();
  }).catch(e => {
    // TODO: 异常处理
    console.error(e);
  });
};

onMounted(() => {
  formInit();
});
</script>
