<template>
  <div style="position: relative">
    <el-form
      ref="form"
      :model="formReportDict"
      :size="layoutStore.defaultFormItemSize"
      label-width="80px"
      label-position="right"
      @submit.prevent
    >
      <filter-box
        :item-width="330"
        @search="refreshFormReportDict(true)"
        @reset="onResetFormReportDict"
      >
        <el-form-item label="字典类型" prop="formFilter.dictType">
          <el-select
            class="filter-item"
            v-model="formReportDict.formFilter.dictType"
            :clearable="true"
            filterable
            placeholder="字典类型"
          >
            <el-option
              v-for="item in ReportDictType.getList()"
              :key="item.id"
              :value="item.id"
              :label="item.name"
            />
          </el-select>
        </el-form-item>
      </filter-box>
    </el-form>
    <table-box
      ref="reportDict"
      class="page-table"
      :data="formReportDict.ReportDict.impl.dataList"
      :size="layoutStore.defaultFormItemSize"
      @sort-change="formReportDict.ReportDict.impl.onSortChange"
      @refresh="refreshFormReportDict(true)"
      :seq-config="{
        startIndex:
          (formReportDict.ReportDict.impl.currentPage - 1) *
          formReportDict.ReportDict.impl.pageSize,
      }"
    >
      <template #operator>
        <el-button
          type="primary"
          :icon="Plus"
          :size="layoutStore.defaultFormItemSize"
          @click="onFormCreateReportDictClick()"
          >新建</el-button
        >
      </template>
      <vxe-column
        title="序号"
        type="seq"
        width="55px"
        :index="formReportDict.ReportDict.impl.getTableIndex"
      />
      <vxe-column title="字典名称" field="dictName"> </vxe-column>
      <vxe-column title="字典类型" field="dictTypeDictMap.name"> </vxe-column>
      <vxe-column title="树形标记" field="treeFlag">
        <template v-slot="scope">
          <el-tag size="default" :type="scope.row.treeFlag ? 'success' : 'primary'">{{
            scope.row.treeFlag ? '是' : '否'
          }}</el-tag>
        </template>
      </vxe-column>
      <vxe-column title="操作" fixed="right" width="100px">
        <template v-slot="scope">
          <el-button
            @click.stop="onFormEditReportDictClick(scope.row)"
            link
            type="primary"
            :size="layoutStore.defaultFormItemSize"
          >
            编辑
          </el-button>
          <el-button
            @click.stop="onDeleteClick(scope.row)"
            link
            type="danger"
            :size="layoutStore.defaultFormItemSize"
          >
            删除
          </el-button>
        </template>
      </vxe-column>
      <template #pagination>
        <el-row type="flex" justify="end" style="margin-top: 16px">
          <el-pagination
            :total="formReportDict.ReportDict.impl.totalCount"
            :current-page="formReportDict.ReportDict.impl.currentPage"
            :page-size="formReportDict.ReportDict.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formReportDict.ReportDict.impl.onCurrentPageChange"
            @size-change="formReportDict.ReportDict.impl.onPageSizeChange"
          >
          </el-pagination>
        </el-row>
      </template>
    </table-box>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus';
import { useCommon } from '@/common/hooks/useCommon';
import { ReportDictController } from '@/api/report';
import { ANY_OBJECT } from '@/types/generic';
import { Dialog } from '@/components/Dialog';
import { useTable } from '@/common/hooks/useTable';
import { TableOptions } from '@/common/types/pagination';
import { ReportDictType } from '@/common/staticDict/report';
import { useLayoutStore } from '@/store';
import formEditReportDict from './editReportDict.vue';

const { Plus } = useCommon();
const layoutStore = useLayoutStore();
const form = ref();

/**
 * 报表字典数据获取函数，返回Promise
 */
const loadReportDictWidgetData = (params: ANY_OBJECT) => {
  if (params == null) params = {};
  params = {
    ...params,
    reportDictDtoFilter: {
      dictType: formReportDict.formFilterCopy.dictType,
    },
  };
  return new Promise((resolve, reject) => {
    ReportDictController.list(params)
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
 * 报表字典数据获取检测函数，返回true正常获取数据，返回false停止获取数据
 */
const loadReportDictVerify = () => {
  formReportDict.formFilterCopy.dictType = formReportDict.formFilter.dictType;
  return true;
};
const tableOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadReportDictWidgetData,
  verifyTableParameter: loadReportDictVerify,
  paged: true,
};
const formReportDict = reactive({
  formFilter: {
    dictType: undefined,
  },
  formFilterCopy: {
    dictType: undefined,
  },
  ReportDict: {
    impl: useTable(tableOptions),
  },
  isInit: false,
});

/**
 * 更新报表字典
 */
const refreshFormReportDict = (reloadData = false) => {
  if (reloadData) {
    formReportDict.ReportDict.impl.refreshTable(true, 1);
  } else {
    formReportDict.ReportDict.impl.refreshTable();
  }
  // if (!formReportDict.isInit) {
  //   // 初始化下拉数据
  // }
  formReportDict.isInit = true;
};
const onResetFormReportDict = () => {
  form.value.resetFields();
  refreshFormReportDict(true);
};
/**
 * 新建
 */
const onFormCreateReportDictClick = () => {
  let params = {
    path: 'thirdEditReportDict',
  };

  Dialog.show(
    '新建',
    formEditReportDict,
    {
      area: '800px',
    },
    params,
    {
      width: '800px',
      height: '600px',
      pathName: '/thirdParty/thirdEditReportDict',
    },
  )
    .then(() => {
      refreshFormReportDict();
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 编辑
 */
const onFormEditReportDictClick = (row: ANY_OBJECT | null = null) => {
  let params = {
    rowData: row,
    path: 'thirdEditReportDict',
  };

  Dialog.show(
    '编辑',
    formEditReportDict,
    {
      area: '600px',
    },
    params,
    {
      width: '800px',
      height: '600px',
      pathName: '/thirdParty/thirdEditReportDict',
    },
  )
    .then(() => {
      refreshFormReportDict();
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 删除
 */
const onDeleteClick = (row: ANY_OBJECT) => {
  let params = {
    dictId: row.dictId,
  };

  ElMessageBox.confirm('是否删除此报表字典？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      ReportDictController.delete(params)
        .then(() => {
          ElMessage.success('删除成功');
          refreshFormReportDict();
        })
        .catch(e => {
          console.warn(e);
        });
    })
    .catch(e => {
      console.warn(e);
    });
};
const formInit = () => {
  refreshFormReportDict();
};

onMounted(() => {
  // 初始化页面数据
  formInit();
});

const onResume = () => {
  refreshFormReportDict();
};
defineExpose({ onResume });
</script>
