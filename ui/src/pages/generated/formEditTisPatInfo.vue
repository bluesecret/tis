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
                :readonly="true"
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
                :readonly="true"
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
                :readonly="true"
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
                :readonly="true"
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
                :readonly="true"
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
                :readonly="true"
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
                :readonly="true"
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
                :readonly="true"
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
                :readonly="true"
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
                :readonly="true"
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
                :readonly="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="患者卡条" prop="TisPatInfo.picPath">
              <custom-upload
                v-model="picPathWidgetFileList"
                name="uploadFile"
                :size="layoutStore.defaultFormItemSize"
                type="expand"
                :headers="getUploadHeaders"
                :action="getUploadActionUrl('/admin/app/tisPatInfo/upload')"
                :data="{fieldName: 'picPath', asImage: true}"
                :limit="picPathWidgetMaxCount"
                @change="onPicPathChange"
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
                :readonly="true"
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
                :readonly="true"
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
                :readonly="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="附件" prop="TisPatInfo.filePath">
              <custom-upload
                v-model="filePathWidgetFileList"
                name="uploadFile"
                :size="layoutStore.defaultFormItemSize"
                type="dropdown"
                :headers="getUploadHeaders"
                :action="getUploadActionUrl('/admin/app/tisPatInfo/upload')"
                :data="{fieldName: 'filePath', asImage: false}"
                :limit="filePathWidgetMaxCount"
                @change="onFilePathChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <table-box
              ref="tisPatResult"
              :data="tisPatResultWidgetDataList"
              style="height: 300px"
              :size="layoutStore.defaultFormItemSize"
              :row-config="{isCurrent: true, isHover: true}"
              :seq-config="{startIndex: ((tisPatResultWidgetCurrentPage - 1) * tisPatResultWidgetPageSize)}"
              :sort-config="{remote: false}"
              :hasExtend="false"
              @refresh="tisPatResultWidget.refreshTable()"
              @radio-select-change="onTisPatResultRadioSelectChange"
            >
              <vxe-column title="" type="radio" align="center" :width="80" />
              <vxe-column title="检测项目" field="projectName" />
              <vxe-column title="检测结果" field="result" />
              <template slot="empty">
                <div class="table-empty unified-font">
                  <img src="@/assets/img/empty.png">
                  <span>暂无数据</span>
                </div>
              </template>
            </table-box>
          </el-col>
        </el-row>
      </el-form>
    </el-scrollbar>
    <el-row class="footer-box" type="flex" justify="end" align="middle">
      <el-button :size="layoutStore.defaultFormItemSize" @click="onCancel()">取消</el-button>
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
    // txt文件路径
    filePath: undefined,
    // 备用字段1
    remark1: undefined,
    // 备用字段2
    remark2: undefined,
    // 备用字段3
    remark3: undefined,
    // 创建时间
    createTime: undefined,
    // 创建用户
    createdUserId: undefined,
    // 修改时间
    updateTime: undefined,
    // 修改用户
    updateUserId: undefined,
    // 检查结果数据
    tisPatResultList: [],
  },
},
);
// 表单验证规则
const rules = reactive({
  'TisPatInfo.rangeVal': [
  ],
  'TisPatInfo.projectId': [
  ],
  'TisPatInfo.testStat': [
  ],
  'TisPatInfo.age': [
  ],
  'TisPatInfo.testUnit': [
  ],
  'TisPatInfo.batchNo': [
  ],
  'TisPatInfo.testTime': [
  ],
  'TisPatInfo.patName': [
  ],
  'TisPatInfo.picPath': [
  ],
  'TisPatInfo.sampleType': [
  ],
  'TisPatInfo.patNo': [
  ],
  'TisPatInfo.sampleNo': [
  ],
  'TisPatInfo.sex': [
  ],
  'TisPatInfo.filePath': [
  ],
  'TisPatInfo.cutoffVal': [
  ],
  'TisPatInfo.operator': [
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
      tisPatResultWidget.refreshTable();
      resolve();
    }).catch(e => {
      reject(e);
    });
  });
};
/**
 * 患者卡条上传文件改变
 */
const onPicPathChange = val => {
  formData.TisPatInfo.picPath = fileListToJson(val);
};
// 患者卡条上传文件组件
const picPathWidget = useUploadWidget(1);
const { fileList: picPathWidgetFileList, maxCount: picPathWidgetMaxCount } = picPathWidget;
/**
 * 附件上传文件改变
 */
const onFilePathChange = val => {
  formData.TisPatInfo.filePath = fileListToJson(val);
};
// 附件上传文件组件
const filePathWidget = useUploadWidget(1);
const { fileList: filePathWidgetFileList, maxCount: filePathWidgetMaxCount } = filePathWidget;
/**
 * 检查结果数据获取函数，返回Promise
 */
const loadTisPatResultWidgetData = (params: ANY_OBJECT) => {
  if (params == null) params = {};
  params = {
    ...params,
    tisPatResultDtoFilter: {
      patId: props.id ||formData.TisPatInfo.id
    }
  };
  return new Promise((resolve, reject) => {
    TisPatResultController.list(params).then(res => {
      // 级联更新设置临时唯一id
      let tempTime = new Date().getTime();
      nextTick(() => {
        formData.TisPatInfo.tisPatResultList = tisPatResultWidgetDataList.value;
      });
      resolve({
        dataList: res.data.dataList.map((item, index) => {
          return {
            __cascade_add_temp_id__: tempTime + index,
            ...item
          }
        }),
        totalCount: res.data.totalCount
      });
      // 恢复当选择行
      if (tisPatResultSelectRow.value != null) {
        nextTick(() => {
          let currentRow = findItemFromList(tisPatResultWidgetDataList.value, tisPatResultSelectRow.value.id, 'id');
          tisPatResult.value.getTableImpl().setRadioRow(currentRow);
        });
      }
    }).catch(e => {
      reject(e);
    });
  });
};
/**
 * 检查结果数据获取检测函数，返回true正常获取数据，返回false停止获取数据
 */
const loadTisPatResultVerify = () => {
  return true;
};
/**
 * 检查结果表格选中行改变
 */
const onTisPatResultRadioSelectChange = (row?: TisPatResultData) => {
  tisPatResultSelectRow.value = row;
};
// 检查结果表格组件参数
const tisPatResultOptions: TableOptions<TisPatResultData> = {
  loadTableData: loadTisPatResultWidgetData,
  verifyTableParameter: loadTisPatResultVerify,
  paged: false,
  rowSelection: true,
  orderFieldName: undefined,
  ascending: true,
};
// 检查结果选中数据
const tisPatResultSelectRow = ref<TisPatResultData>();
// 检查结果表格组件
const tisPatResult = ref();
const tisPatResultWidget = useTable(tisPatResultOptions);
const {
  dataList: tisPatResultWidgetDataList,
  currentPage: tisPatResultWidgetCurrentPage,
  pageSize: tisPatResultWidgetPageSize,
  totalCount: tisPatResultWidgetTotalCount,
} = tisPatResultWidget;
const onUploadError = () => {
  ElMessage.error('文件上传失败');
};
const onUploadLimit = () => {
  ElMessage.error('已经超出最大上传个数限制');
};
const refreshFormEditTisPatInfo = () => {
  // 刷新段落
  tisPatResultWidget.refreshTable();
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
const formInit = () => {
  loadTisPatInfoData().then(res => {
    let picPathDownloadParams = {
      id: formData.TisPatInfo.id,
      fieldName: 'picPath',
      asImage: true
    };
    picPathWidgetFileList.value = parseUploadData(formData.TisPatInfo.picPath, picPathDownloadParams);
    let filePathDownloadParams = {
      id: formData.TisPatInfo.id,
      fieldName: 'filePath',
      asImage: false
    };
    filePathWidgetFileList.value = parseUploadData(formData.TisPatInfo.filePath, filePathDownloadParams);
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
