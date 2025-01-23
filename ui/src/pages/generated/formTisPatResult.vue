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
        <el-form-item label="检测项目">
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
        <el-form-item label="检测结果">
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
      :hasExtend="true"
      @sort-change="formTisPatResultTableWidget.onSortChange"
      @refresh="formTisPatResultTableWidget.refreshTable()"
    >
      <template #operator>
        <el-button
          type="primary"
          :size="layoutStore.defaultFormItemSize"
          @click="onAddTisPatResultClick()"
          >
          新建
        </el-button>
        <el-button
          type="primary"
          :size="layoutStore.defaultFormItemSize"
          @click="onExportTisPatResultClick()"
          >
          导出
        </el-button>
        <el-upload
          class="btn-import"
          :auto-upload="false"
          action=""
          :show-file-list="false"
          accept=".xls,.xlsx"
          style="display: inline-block;"
          :on-change="onImportTisPatResultClick"
        >
          <template #trigger>
            <el-button
              type="primary"
              :size="layoutStore.defaultFormItemSize"
            >
              导入
            </el-button>
          </template>
        </el-upload>
      </template>
      <vxe-column title="序号" type="seq" :index="formTisPatResultTableWidget.getTableIndex" :width="80" />
      <vxe-column title="检测项目" field="projectName" />
      <vxe-column title="检测结果" field="result" />
      <vxe-column title="备用字段1" field="remark1" />
      <vxe-column title="备用字段2" field="remark2" />
      <vxe-column title="备用字段3" field="remark3" />
      <vxe-column title="操作" fixed="right">
        <template v-slot="scope">
          <el-button
            link
            type="primary"
            :size="layoutStore.defaultFormItemSize"
            @click.stop="onEditTisPatResultClick(scope.row)"
          >
            编辑
          </el-button>
          <el-button
            link
            type="primary"
            :size="layoutStore.defaultFormItemSize"
            @click.stop="onDeleteTisPatResultClick(scope.row)"
          >
            删除
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
import FormEditTisPatResult from '@/pages/generated/formEditTisPatResult.vue';

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
/**
 * 新建
 */
const onAddTisPatResultClick = (row?: TisPatResultData) => {
  let params: ANY_OBJECT = {
  };

  Dialog
    .show('新建', FormEditTisPatResult, { area: '900px' }, { ...params, subPage: true })
    .then(res => {
      formTisPatResultTableWidget.refreshTable();
    }).catch(e => {
      // TODO: 异常处理
      console.error(e);
    });
};
/**
 * 编辑
 */
const onEditTisPatResultClick = (row?: TisPatResultData) => {
  let params: ANY_OBJECT = {
    id: row?.id,
  };

  Dialog
    .show('编辑', FormEditTisPatResult, { area: '900px' }, { ...params, subPage: true })
    .then(res => {
      formTisPatResultTableWidget.refreshTable();
    }).catch(e => {
      // TODO: 异常处理
      console.error(e);
    });
};
/**
 * 导出
 */
const onExportTisPatResultClick = (row?: TisPatResultData) => {
  let params: ANY_OBJECT = {
  };

  TisPatResultController.export(params, '表格组件.xlsx').then(res => {
    ElMessage.success('导出成功');
  }).catch(e => {
    ElMessage.error(e.errorMessage);
  });
};
/**
 * 导入
 */
const onImportTisPatResultClick = (file) => {
  let params: ANY_OBJECT = {
    importFile: file.raw,
    // 是否忽略表头
    skipHeader: false
  };

  TisPatResultController.import(params).then(res => {
    ElMessage.success('导入成功');
    formTisPatResultTableWidget.refreshTable();
  }).catch(e => {
    // TODO: 异常处理
    console.error(e);
  });
};
/**
 * 删除
 */
const onDeleteTisPatResultClick = (row?: TisPatResultData) => {
  let params: ANY_OBJECT = {
    id: row?.id,
  };

  ElMessageBox.confirm('是否删除此记录？').then(res => {
    TisPatResultController.delete(params).then(res => {
      ElMessage.success('删除成功');
      formTisPatResultTableWidget.refreshTable(false, 1);
    }).catch(e => {
      // TODO: 异常处理
      console.error(e);
    });
  }).catch(e => {
    // TODO: 异常处理
    console.error(e);
  });
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