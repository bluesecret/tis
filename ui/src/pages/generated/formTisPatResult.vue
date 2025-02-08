<template>
  <div class="page-box" style="position: relative;">
    <el-form
      ref="formTisPatResultRef"
      :size="layoutStore.defaultFormItemSize"
      label-width="120px"
      label-position="right"
      @submit.prevent
    >
      <filter-box :item-width="350" @search="refreshFormTisPatResult()" @reset="resetFormTisPatResult">
        <el-form-item label="Check Project">
          <el-input
            class="filter-item"
            v-model="formFilter.projectNameFilter"
            type="text"
            placeholder=""
            :clearable="true"
            :show-word-limit="false"
            maxlength=""
          />
        </el-form-item>
        <el-form-item label="Check Result">
          <el-input
            class="filter-item"
            v-model="formFilter.resultFilter"
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
      ref="formTisPatResultTable"
      class="page-table"
      :data="formTisPatResultTableWidgetDataList"
      :size="layoutStore.defaultFormItemSize"
      :row-config="{isCurrent: false, isHover: true}"
      :seq-config="{startIndex: ((formTisPatResultTableWidgetCurrentPage - 1) * formTisPatResultTableWidgetPageSize)}"
      :sort-config="{remote: true}"
      :hasExtend="false"
      @sort-change="formTisPatResultTableWidget.onSortChange"
      @refresh="formTisPatResultTableWidget.refreshTable()"
    >
      <vxe-column title="序号" type="seq" :index="formTisPatResultTableWidget.getTableIndex" :width="80" />
      <vxe-column title="检测项目" field="projectName" />
      <vxe-column title="检测结果" field="result" />
      <template slot="empty">
        <div class="table-empty unified-font">
          <img src="@/assets/img/empty.png">
          <span>No Data</span>
        </div>
      </template>
      <!-- 分页 -->
      <template #pagination>
        <el-row type="flex" justify="end" style="margin-top: 10px;">
          <el-pagination
            :total="formTisPatResultTableWidgetTotalCount"
            :current-page="formTisPatResultTableWidgetCurrentPage"
            :page-size="formTisPatResultTableWidgetPageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formTisPatResultTableWidget.onCurrentPageChange"
            @size-change="formTisPatResultTableWidget.onPageSizeChange">
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
  name: 'formTisPatResult',
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
  }>(),
  {
    subPage: 0,
    id: undefined,
  },
);

const formFilter = reactive({
  // 检测项目
  projectNameFilter: undefined,
  // 检测结果
  resultFilter: undefined,
});
const formFilterCopy = reactive({
  // 检测项目
  projectNameFilter: undefined,
  // 检测结果
  resultFilter: undefined,
});

const onCancel = () => {
  router.go(-1);
  layoutStore.removeCachePage(route.fullPath as string);
  route.meta.refreshParentCachedPage = true;
};

const onResume = () => {
  refreshFormTisPatResult();
};

/**
 * 表格组件数据获取函数，返回Promise
 */
const loadFormTisPatResultTableWidgetData = (params: ANY_OBJECT) => {
  if (params == null) params = {};
  params = {
    ...params,
    tisPatResultDtoFilter: {
      projectName: formFilter.projectNameFilter,
      result: formFilter.resultFilter,
    }
  };
  return new Promise((resolve, reject) => {
    TisPatResultController.list(params).then(res => {
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
const loadFormTisPatResultTableVerify = () => {
  formFilterCopy.projectNameFilter = formFilter.projectNameFilter;
  formFilterCopy.resultFilter = formFilter.resultFilter;
  return true;
};
// 表格组件表格组件参数
const formTisPatResultTableOptions: TableOptions<TisPatResultData> = {
  loadTableData: loadFormTisPatResultTableWidgetData,
  verifyTableParameter: loadFormTisPatResultTableVerify,
  paged: true,
  rowSelection: false,
  orderFieldName: undefined,
  ascending: true,
};
// 表格组件表格组件
const formTisPatResultTable = ref();
const formTisPatResultTableWidget = useTable(formTisPatResultTableOptions);
const {
  dataList: formTisPatResultTableWidgetDataList,
  currentPage: formTisPatResultTableWidgetCurrentPage,
  pageSize: formTisPatResultTableWidgetPageSize,
  totalCount: formTisPatResultTableWidgetTotalCount,
} = formTisPatResultTableWidget;
const refreshFormTisPatResult = () => {
  // 刷新段落
  formTisPatResultTableWidget.refreshTable();
};
/**
 * 重置过滤值
 */
const resetFormTisPatResult = () => {
  formFilter.projectNameFilter = undefined;
  formFilterCopy.projectNameFilter = undefined;
  formFilter.resultFilter = undefined;
  formFilterCopy.resultFilter = undefined;
  refreshFormTisPatResult();
};
/**
 * 重置所有过滤值
 */
const resetFilter = () => {
  resetFormTisPatResult();
};
const formInit = () => {
  refreshFormTisPatResult();
};

onMounted(() => {
  formInit();
});

onActivated(() => {
  onResume();
});
</script>
