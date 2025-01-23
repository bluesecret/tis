<template>
  <div class="dialog-box" style="position: relative">
    <el-scrollbar class="custom-scroll content-box">
      <el-form
        ref="formEditTisPatResultRef"
        :model="formData"
        :size="layoutStore.defaultFormItemSize"
        :rules="rules"
        label-width="120px"
        label-position="right"
        @submit.prevent
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="检测项目" prop="TisPatResult.projectName">
              <el-input
                class="input-item"
                v-model="formData.TisPatResult.projectName"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="检测结果" prop="TisPatResult.result">
              <el-input
                class="input-item"
                v-model="formData.TisPatResult.result"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备用字段1" prop="TisPatResult.remark1">
              <el-input
                class="input-item"
                v-model="formData.TisPatResult.remark1"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备用字段2" prop="TisPatResult.remark2">
              <el-input
                class="input-item"
                v-model="formData.TisPatResult.remark2"
                type="text"
                placeholder=""
                :clearable="true"
                :show-word-limit="false"
                maxlength=""
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备用字段3" prop="TisPatResult.remark3">
              <el-input
                class="input-item"
                v-model="formData.TisPatResult.remark3"
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
      <el-button :size="layoutStore.defaultFormItemSize" type="primary" @click="onSubmitTisPatResultClick()">保存</el-button>
    </el-row>
  </div>
</template>

<script lang="ts">
export default {
  name: 'formEditTisPatResult',
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
import { TisPatResultData } from '@/api/generated/tisPatResultController';
import { TisPatResultController } from '@/api/generated';

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

const formEditTisPatResultRef = ref();
// 表单数据定义
type FormEditTisPatResultData = {
  TisPatResult: TisPatResultData;
};
// 表单数据
const formData = reactive<FormEditTisPatResultData>({
  TisPatResult: {
    // 主键Id
    id: undefined,
    // 患者ID
    patId: undefined,
    // 检测项目
    projectName: undefined,
    // 检测结果
    result: undefined,
    // 备用字段1
    remark1: undefined,
    // 备用字段2
    remark2: undefined,
    // 备用字段3
    remark3: undefined,
    // 创建时间
    createTime: undefined,
    // 创建用户
    createUserId: undefined,
    // 修改时间
    updateTime: undefined,
    // 修改用户
    updateUserId: undefined,
  },
},
);
// 表单验证规则
const rules = reactive({
  'TisPatResult.projectName': [
  ],
  'TisPatResult.result': [
  ],
  'TisPatResult.remark3': [
  ],
  'TisPatResult.remark1': [
  ],
  'TisPatResult.remark2': [
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
const loadTisPatResultData = () => {
  return new Promise<void>((resolve, reject) => {
    if (!isEdit.value) {
      resolve();
      return;
    }
    if (!props.saveOnSubmit && props.rowData != null) {
      formData.TisPatResult = JSON.parse(JSON.stringify(props.rowData));
      resolve();
      return;
    }
    let params: ANY_OBJECT = {
      id: props.id
    };
    TisPatResultController.view(params).then(res => {
      formData.TisPatResult = { ...res.data };
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
const refreshFormEditTisPatResult = () => {
  // 刷新段落
};
/**
 * 重置过滤值
 */
const resetFormEditTisPatResult = () => {
  refreshFormEditTisPatResult();
};
/**
 * 重置所有过滤值
 */
const resetFilter = () => {
  resetFormEditTisPatResult();
};
/**
 * 保存
 */
const onSubmitTisPatResultClick = () => {
  formEditTisPatResultRef.value.validate((valid) => {
    if (!valid) return;
    // 级联操作
    if (!props.saveOnSubmit) {
      let retFormData = {
        ...formData.TisPatResult
      };
  props.dialog?.submit(retFormData);
      return;
    }
    let params: ANY_OBJECT = {
      tisPatResultDto: {
        id: formData.TisPatResult.id,
        patId: formData.TisPatResult.patId,
        projectName: formData.TisPatResult.projectName,
        result: formData.TisPatResult.result,
        remark1: formData.TisPatResult.remark1,
        remark2: formData.TisPatResult.remark2,
        remark3: formData.TisPatResult.remark3,
        createTime: formData.TisPatResult.createTime,
        createUserId: formData.TisPatResult.createUserId,
        updateTime: formData.TisPatResult.updateTime,
        updateUserId: formData.TisPatResult.updateUserId,
      }
    };

    let httpCall = isEdit.value ? TisPatResultController.update : TisPatResultController.add;
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
  loadTisPatResultData().then(res => {
    if (isEdit.value) refreshFormEditTisPatResult();
  }).catch(e => {
    // TODO: 异常处理
    console.error(e);
  });
};

onMounted(() => {
  formInit();
});
</script>
