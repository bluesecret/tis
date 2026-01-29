<template>
  <div class="page-box" style="position: relative">
    <el-form
      ref="formTisPatResultRef"
      :size="layoutStore.defaultFormItemSize"
      label-width="120px"
      label-position="right"
      @submit.prevent
    >
      <filter-box
        :item-width="350"
        @search="refreshFormTisDeviceInfo()"
        @reset="resetFormTisPatResult"
      >
        <el-form-item label="Device No.">
          <el-input
            class="filter-item"
            v-model="formFilter.serNo"
            type="text"
            placeholder=""
            :clearable="true"
            :show-word-limit="false"
            maxlength=""
          />
        </el-form-item>
        <el-form-item label="Device Name">
          <el-input
            class="filter-item"
            v-model="formFilter.deviceName"
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
      :row-config="{ isCurrent: false, isHover: true }"
      :seq-config="{
        startIndex:
          (formTisPatResultTableWidgetCurrentPage - 1) * formTisPatResultTableWidgetPageSize,
      }"
      :sort-config="{ remote: true }"
      :hasExtend="false"
      @sort-change="formTisPatResultTableWidget.onSortChange"
      @refresh="formTisPatResultTableWidget.refreshTable()"
    >
      <vxe-column
        title="Index"
        type="seq"
        :index="formTisPatResultTableWidget.getTableIndex"
        :width="80"
      />
      <template v-slot:operator>
        <el-button
          type="primary"
          :size="layoutStore.defaultFormItemSize"
          :icon="Plus"
          @click="onAddRow()"
          >Add</el-button
        >
        <el-button type="primary" :size="layoutStore.defaultFormItemSize" @click="onUploadRow()"
          >Import</el-button
        >
      </template>
      <vxe-column title="Device No." field="serNo" />
      <vxe-column title="Device Name" field="deviceName" />
      <template slot="empty">
        <div class="table-empty unified-font">
          <img src="@/assets/img/empty.png" />
          <span>No Data</span>
        </div>
      </template>
      <vxe-column title="Operation" width="250px" fixed="right">
        <template v-slot="scope">
          <el-button
            :size="formItemSize"
            type="primary"
            link
            @click="onEditDatasourceTable(scope.row)"
          >
            Edit
          </el-button>
          <el-button link type="danger" :size="formItemSize" @click="onDeleteRow(scope.row)">
            Delete
          </el-button>
        </template>
      </vxe-column>

      <!-- 分页 -->
      <template #pagination>
        <el-row type="flex" justify="end" style="margin-top: 10px">
          <el-pagination
            :total="formTisPatResultTableWidgetTotalCount"
            :current-page="formTisPatResultTableWidgetCurrentPage"
            :page-size="formTisPatResultTableWidgetPageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formTisPatResultTableWidget.onCurrentPageChange"
            @size-change="formTisPatResultTableWidget.onPageSizeChange"
          >
          </el-pagination>
        </el-row>
      </template>
    </table-box>
    <label v-if="subPage" class="page-close-box" @click="onCancel()">
      <img src="@/assets/img/back2.png" alt="" />
    </label>
  </div>
</template>

  <script lang="ts">
export default {
  name: 'formTisDeviceInfo',
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
import {
  treeDataTranslate,
  findItemFromList,
  findTreeNodePath,
  findTreeNode,
  stringCase,
} from '@/common/utils';
import { TisDeviceData } from '@/api/generated/tisDeviceInfoController';
import EditDeviceForm from './formEditDevice/index.vue';
import UploadDeviceForm from './formUploadDevice/index.vue';
import { TisDeviceInfoController } from '@/api/generated';

const router = useRouter();
const route = useRoute();
const layoutStore = useLayoutStore();
const { downloadFile } = useDownload();
const { getUploadHeaders, getUploadActionUrl, fileListToJson, parseUploadData, getPictureList } =
  useUpload();
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

const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const formFilter = reactive({
  // 检测项目
  deviceName: undefined,
  // 检测结果
  serNo: undefined,
});
const formFilterCopy = reactive({
  // 检测项目
  serNo: undefined,
  // 检测结果
  deviceName: undefined,
});

const onCancel = () => {
  router.go(-1);
  layoutStore.removeCachePage(route.fullPath as string);
  route.meta.refreshParentCachedPage = true;
};

const onResume = () => {
  refreshFormTisDeviceInfo();
};

/**
 * 表格组件数据获取函数，返回Promise
 */
const loadFormTisPatResultTableWidgetData = (params: ANY_OBJECT) => {
  if (params == null) params = {};
  params = {
    ...params,
    tisDeviceInfoDto: {
      serNo: formFilter.serNo,
      deviceName: formFilter.deviceName,
    },
  };
  return new Promise((resolve, reject) => {
    TisDeviceInfoController.list(params)
      .then(res => {
        resolve({
          dataList: res.data.dataList,
          totalCount: res.data.totalCount,
        });
      })
      .catch(e => {
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
const formTisPatResultTableOptions: TableOptions<TisDeviceInfo> = {
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
const refreshFormTisDeviceInfo = () => {
  // 刷新段落
  formTisPatResultTableWidget.refreshTable();
};
/**
 * 重置过滤值
 */
const resetFormTisPatResult = () => {
  formFilter.serNo = undefined;
  formFilterCopy.serNo = undefined;
  formFilter.deviceName = undefined;
  formFilterCopy.deviceName = undefined;
  refreshFormTisDeviceInfo();
};
/**
 * 重置所有过滤值
 */
const resetFilter = () => {
  resetFormTisPatResult();
};
const formInit = () => {
  refreshFormTisDeviceInfo();
};

const onAddRow = () => {
  Dialog.show('Add Device', EditDeviceForm, {
    area: '600px',
  })
    .then(() => {
      refreshFormTisDeviceInfo();
    })
    .catch(e => {
      console.warn(e);
    });
};

const onEditDatasourceTable = (row: any) => {
  Dialog.show(
    'Edit Device',
    EditDeviceForm,
    {
      area: '600px',
    },
    {
      rowData: row,
    },
  )
    .then(() => {
      refreshFormTisDeviceInfo();
    })
    .catch(e => {
      console.warn(e);
    });
};

const onDeleteRow = (row: any) => {
  let params = {
    id: row.id,
  };
  ElMessageBox.confirm(`Is sure delete device【${row.deviceName}】？`, '', {
    confirmButtonText: 'Yes',
    cancelButtonText: 'Cancel',
    type: 'warning',
  })
    .then(() => {
      TisDeviceInfoController.delete(params)
        .then(() => {
          refreshFormTisDeviceInfo();
        })
        .catch(e => {
          console.log(e);
        });
    })
    .catch(e => {
      console.log(e);
    });
};

const onUploadRow = () => {
  Dialog.show('File Import', UploadDeviceForm, {
    area: '600px',
  })
    .then(() => {
      refreshFormTisDeviceInfo();
    })
    .catch(e => {
      console.warn(e);
    });
};

onMounted(() => {
  formInit();
});

onActivated(() => {
  onResume();
});
</script>
