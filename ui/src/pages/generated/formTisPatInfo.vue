<template>
  <div class="page-box" style="position: relative;">
    <el-form
      ref="formTisPatInfoRef"
      :size="layoutStore.defaultFormItemSize"
      label-width="120px"
      label-position="right"
      @submit.prevent
    >
      <filter-box :item-width="350" @search="refreshFormTisPatInfo()" @reset="resetFormTisPatInfo">
        <el-form-item label="姓名">
          <el-input
            class="filter-item"
            v-model="formFilter.patNameFilter"
            type="text"
            placeholder=""
            :clearable="true"
            :show-word-limit="false"
            maxlength=""
          />
        </el-form-item>
        <el-form-item label="检测项目">
          <el-input
            class="filter-item"
            v-model="formFilter.projectIdFilter"
            type="text"
            placeholder=""
            :clearable="true"
            :show-word-limit="false"
            maxlength=""
          />
        </el-form-item>
        <el-form-item label="样本编号">
          <el-input
            class="filter-item"
            v-model="formFilter.sampleNoFilter"
            type="text"
            placeholder=""
            :clearable="true"
            :show-word-limit="false"
            maxlength=""
          />
        </el-form-item>
        <el-form-item label="患者编号">
          <el-input
            class="filter-item"
            v-model="formFilter.patNoFilter"
            type="text"
            placeholder=""
            :clearable="true"
            :show-word-limit="false"
            maxlength=""
          />
        </el-form-item>
        <el-form-item label="操作人员">
          <el-input
            class="filter-item"
            v-model="formFilter.operatorFilter"
            type="text"
            placeholder=""
            :clearable="true"
            :show-word-limit="false"
            maxlength=""
          />
        </el-form-item>
        <el-form-item label="检测状态">
          <el-input
            class="filter-item"
            v-model="formFilter.testStatFilter"
            type="text"
            placeholder=""
            :clearable="true"
            :show-word-limit="false"
            maxlength=""
          />
        </el-form-item>
      </filter-box>
    </el-form>
    <table-box
      ref="formTisPatInfoTable"
      class="page-table"
      :data="formTisPatInfoTableWidgetDataList"
      :size="layoutStore.defaultFormItemSize"
      :row-config="{isCurrent: false, isHover: true}"
      :seq-config="{startIndex: ((formTisPatInfoTableWidgetCurrentPage - 1) * formTisPatInfoTableWidgetPageSize)}"
      :sort-config="{remote: true}"
      :hasExtend="false"
      @sort-change="formTisPatInfoTableWidget.onSortChange"
      @refresh="formTisPatInfoTableWidget.refreshTable()"
    >
      <vxe-column title="序号" type="seq" :index="formTisPatInfoTableWidget.getTableIndex" :width="80" />
      <vxe-column title="姓名" field="patName" />
      <vxe-column title="年龄" field="age" />
      <vxe-column title="检测项目" field="projectId" />
      <vxe-column title="性别" field="sex" />
      <vxe-column title="患者编号" field="patNo" />
      <vxe-column title="操作人员" field="operator" />
      <vxe-column title="cotful值" field="cutoffVal" />
      <vxe-column title="患者卡条">
        <template v-slot="scope">
          <upload-file-list
            :file-list="
              parseUploadData(scope.row.picPath, {
                id: scope.row.id,
                fieldName: 'picPath',
                asImage: true
              })
            "
            type="card"
            direction="horizontal"
            :readonly="true"
          />
        </template>
      </vxe-column>
      <vxe-column title="检测时间" field="testTime" />
      <vxe-column title="检测状态" field="testStat">
        <template v-slot="scope">
          <el-tag size="default" type="primary">{{ scope.row.testStat }}</el-tag>
        </template>
      </vxe-column>
      <vxe-column title="附件">
        <template v-slot="scope">
          <upload-file-list
            :file-list="
              parseUploadData(scope.row.filePath, {
                id: scope.row.id,
                fieldName: 'filePath',
                asImage: false
              })
            "
            type="text"
            direction="horizontal"
            :readonly="true"
          />
        </template>
      </vxe-column>
      <vxe-column title="操作" fixed="right">
        <template v-slot="scope">
          <el-button
            link
            type="primary"
            :size="layoutStore.defaultFormItemSize"
            @click.stop="onEditTisPatInfoClick(scope.row)"
            :disabled="!checkPermCodeExist('formTisPatInfo:formTisPatInfo:editTisPatInfo')"
          >
            检查结果
          </el-button>
        </template>
      </vxe-column>
      <template slot="empty">
        <div class="table-empty unified-font">
          <img src="@/assets/img/empty.png">
          <span>暂无数据</span>
        </div>
      </template>
      <!-- 分页 -->
      <template #pagination>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formTisPatInfoTableWidgetTotalCount"
            :current-page="formTisPatInfoTableWidgetCurrentPage"
            :page-size="formTisPatInfoTableWidgetPageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formTisPatInfoTableWidget.onCurrentPageChange"
            @size-change="formTisPatInfoTableWidget.onPageSizeChange">
          </el-pagination>
        </el-row>
      </template>
    </table-box>
    <label v-if="subPage" class="page-close-box" @click="onCancel()">
      <img src="@/assets/img/back2.png" alt="">
    </label>
  </div>
</template>

<script lang="ts">
export default {
  name: 'formTisPatInfo',
};
</script>

<script setup lang="ts">
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
import FormEditTisPatInfo from '@/pages/generated/formEditTisPatInfo.vue';

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
  }>(),
  {
    subPage: 0,
  },
);

const formFilter = reactive({
  // 姓名
  patNameFilter: undefined,
  // 检测项目
  projectIdFilter: undefined,
  // 样本编号
  sampleNoFilter: undefined,
  // 患者编号
  patNoFilter: undefined,
  // 操作人员
  operatorFilter: undefined,
  // 检测状态
  testStatFilter: undefined,
});
const formFilterCopy = reactive({
  // 姓名
  patNameFilter: undefined,
  // 检测项目
  projectIdFilter: undefined,
  // 样本编号
  sampleNoFilter: undefined,
  // 患者编号
  patNoFilter: undefined,
  // 操作人员
  operatorFilter: undefined,
  // 检测状态
  testStatFilter: undefined,
});

const onCancel = () => {
  router.go(-1);
  layoutStore.removeCachePage(route.fullPath as string);
  route.meta.refreshParentCachedPage = true;
};

const onResume = () => {
  refreshFormTisPatInfo();
};

/**
 * 表格组件数据获取函数，返回Promise
 */
const loadFormTisPatInfoTableWidgetData = (params: ANY_OBJECT) => {
  if (params == null) params = {};
  params = {
    ...params,
    tisPatInfoDtoFilter: {
      patName: formFilter.patNameFilter,
      projectId: formFilter.projectIdFilter,
      sampleNo: formFilter.sampleNoFilter,
      patNo: formFilter.patNoFilter,
      operator: formFilter.operatorFilter,
      testStat: formFilter.testStatFilter,
    }
  };
  return new Promise((resolve, reject) => {
    TisPatInfoController.list(params).then(res => {
      resolve({
        dataList: res.data.dataList,
        totalCount: res.data.totalCount
      });
    }).catch(e => {
      reject(e);
    });
  });
};
/**
 * 表格组件数据获取检测函数，返回true正常获取数据，返回false停止获取数据
 */
const loadFormTisPatInfoTableVerify = () => {
  formFilterCopy.patNameFilter = formFilter.patNameFilter;
  formFilterCopy.projectIdFilter = formFilter.projectIdFilter;
  formFilterCopy.sampleNoFilter = formFilter.sampleNoFilter;
  formFilterCopy.patNoFilter = formFilter.patNoFilter;
  formFilterCopy.operatorFilter = formFilter.operatorFilter;
  formFilterCopy.testStatFilter = formFilter.testStatFilter;
  return true;
};
/**
 * 检查结果
 */
const onEditTisPatInfoClick = (row?: TisPatInfoData) => {
  let params: ANY_OBJECT = {
    id: row?.id,
  };

  Dialog
    .show('检查结果', FormEditTisPatInfo, { area: '900px' }, { ...params, subPage: true })
    .then(res => {
      formTisPatInfoTableWidget.refreshTable();
    }).catch(e => {
      // TODO: 异常处理
      console.error(e);
    });
};
// 表格组件表格组件参数
const formTisPatInfoTableOptions: TableOptions<TisPatInfoData> = {
  loadTableData: loadFormTisPatInfoTableWidgetData,
  verifyTableParameter: loadFormTisPatInfoTableVerify,
  paged: true,
  rowSelection: false,
  orderFieldName: undefined,
  ascending: true,
};
// 表格组件表格组件
const formTisPatInfoTable = ref();
const formTisPatInfoTableWidget = useTable(formTisPatInfoTableOptions);
const {
  dataList: formTisPatInfoTableWidgetDataList,
  currentPage: formTisPatInfoTableWidgetCurrentPage,
  pageSize: formTisPatInfoTableWidgetPageSize,
  totalCount: formTisPatInfoTableWidgetTotalCount,
} = formTisPatInfoTableWidget;
const refreshFormTisPatInfo = () => {
  // 刷新段落
  formTisPatInfoTableWidget.refreshTable();
};
/**
 * 重置过滤值
 */
const resetFormTisPatInfo = () => {
  formFilter.patNameFilter = undefined;
  formFilterCopy.patNameFilter = undefined;
  formFilter.projectIdFilter = undefined;
  formFilterCopy.projectIdFilter = undefined;
  formFilter.sampleNoFilter = undefined;
  formFilterCopy.sampleNoFilter = undefined;
  formFilter.patNoFilter = undefined;
  formFilterCopy.patNoFilter = undefined;
  formFilter.operatorFilter = undefined;
  formFilterCopy.operatorFilter = undefined;
  formFilter.testStatFilter = undefined;
  formFilterCopy.testStatFilter = undefined;
  refreshFormTisPatInfo();
};
/**
 * 重置所有过滤值
 */
const resetFilter = () => {
  resetFormTisPatInfo();
};
const formInit = () => {
  refreshFormTisPatInfo();
};

onMounted(() => {
  formInit();
});

onActivated(() => {
  onResume();
});
</script>