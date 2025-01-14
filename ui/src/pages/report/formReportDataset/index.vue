<template>
  <AdvanceQuery
    class="form-report-dataset"
    ref="datasetForm"
    :height="mainContextHeight"
    :treePanel="datasetGroupConfig"
    :tablePanel="datasetConfig"
    @editGroupItem="onEditReportDatasetGroup"
    @deleteGroupItem="onDeleteReportDatasetGroup"
    @refreshTable="onRefreshDataset"
    @addTableItem="onEditReportDataset(null)"
  >
    <template #table>
      <table-box
        :data="reportDatasetWidget.dataList"
        :size="layoutStore.defaultFormItemSize"
        header-cell-class-name="table-header-gray"
        height="auto"
        :hasExtend="false"
        :seq-config="{
          startIndex: (reportDatasetWidget.currentPage - 1) * reportDatasetWidget.pageSize,
        }"
        style="padding-bottom: 16px"
      >
        <vxe-column
          title="序号"
          type="seq"
          width="50px"
          :index="reportDatasetWidget.getTableIndex"
        />
        <vxe-column title="数据集名称" field="datasetName" />
        <vxe-column title="数据集类型" field="datasetTypeDictMap.name" />
        <vxe-column title="数据库链接" field="dblinkIdDictMap.name" />
        <vxe-column title="操作" width="100px">
          <template v-slot="scope">
            <el-button
              link
              type="primary"
              :size="layoutStore.defaultFormItemSize"
              @click="onEditReportDataset(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="danger"
              :size="layoutStore.defaultFormItemSize"
              @click="onDeleteDataset(scope.row)"
            >
              删除
            </el-button>
          </template>
        </vxe-column>
        <template #pagination>
          <el-row type="flex" justify="end" style="margin-top: 16px">
            <el-pagination
              :total="reportDatasetWidget.totalCount"
              :current-page="reportDatasetWidget.currentPage"
              :page-size="reportDatasetWidget.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, prev, pager, next, sizes"
              @current-change="reportDatasetWidget.onCurrentPageChange"
              @size-change="reportDatasetWidget.onPageSizeChange"
            >
            </el-pagination>
          </el-row>
        </template>
      </table-box>
    </template>
  </AdvanceQuery>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus';
import { treeDataTranslate } from '@/common/utils';
import AdvanceQuery from '@/components/AdvanceQuery/index.vue';
import { ReportDatasetController, ReportDatasetGroupController } from '@/api/report';
import { ANY_OBJECT } from '@/types/generic';
import { Dialog } from '@/components/Dialog';
import { useTable } from '@/common/hooks/useTable';
import { TableOptions } from '@/common/types/pagination';
import { useLayoutStore } from '@/store';
import EditReportDataset from './editReportDataset.vue';
import EditReportDatasetGroup from './editReportDatasetGroup.vue';

const layoutStore = useLayoutStore();
const mainContextHeight = inject('mainContextHeight', 200);
const clientHeight = inject('documentClientHeight', 200);

const datasetForm = ref();
const datasetConfig = {
  title: '数据集',
  supportAdd: true,
  addText: '新建数据集',
};
const allDatasetList = ref<ANY_OBJECT[]>([]);
const datasetGroupList = ref<ANY_OBJECT[]>([]);
const pageParam = ref({
  pageNum: 1,
  pageSize: 10,
});
const currentGroup = ref();
/**
 * 获取数据集分组数据
 */
const loadDatasetGroupData = () => {
  return new Promise((resolve, reject) => {
    ReportDatasetGroupController.list({})
      .then(res => {
        datasetGroupList.value = res.data;
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const datasetGroupConfig = {
  title: '数据集分组',
  supportAdd: true,
  supportEdit: true,
  supportDelete: true,
  addText: '新建分组',
  keyColumnName: 'groupId',
  nameColumnName: 'groupName',
  loadFunction: loadDatasetGroupData,
};

const groupTree = computed(() => {
  if (Array.isArray(datasetGroupList.value)) {
    return treeDataTranslate(
      datasetGroupList.value.map(item => {
        return {
          ...item,
        };
      }),
      'groupId',
    );
  } else {
    return [];
  }
});
/**
 * 通过分组过滤后的数据集列表
 */
const filteredDatasetList = computed(() => {
  return currentGroup.value == null
    ? []
    : allDatasetList.value.filter(item => {
        return item.groupId === currentGroup.value.groupId;
      });
});
/**
 * 分页后的数据集列表
 */
const pagedDatasetList = computed(() => {
  let startPos = (pageParam.value.pageNum - 1) * pageParam.value.pageSize;
  return filteredDatasetList.value.slice(startPos, startPos + pageParam.value.pageSize);
});
const loadDatasetData = (params: ANY_OBJECT) => {
  pageParam.value = {
    ...params.pageParam,
  };
  return Promise.resolve({
    dataList: pagedDatasetList.value,
    totalCount: filteredDatasetList.value.length,
  });
};
const loadDatasetDataVerify = () => {
  return true;
};
const tableOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadDatasetData,
  verifyTableParameter: loadDatasetDataVerify,
  paged: true,
};
const reportDatasetWidget = reactive(useTable(tableOptions));

/**
 * 获取所有数据集
 */
const loadAllDatasetData = () => {
  ReportDatasetController.list({})
    .then(res => {
      allDatasetList.value = res.data.dataList;
      reportDatasetWidget.refreshTable();
    })
    .catch(e => {
      console.warn(e);
    });
};

onMounted(() => {
  loadAllDatasetData();
});

const onEditReportDatasetGroup = (row: ANY_OBJECT | null) => {
  Dialog.show(
    row ? '编辑分组' : '新建分组',
    EditReportDatasetGroup,
    {
      area: ['600px'],
    },
    {
      groupId: (row || {}).groupId,
      groupList: groupTree.value,
      path: 'thirdEditReportDatasetGroup',
    },
    {
      width: '600px',
      height: '300px',
      pathName: '/thirdParty/thirdEditReportDatasetGroup',
    },
  )
    .then(() => {
      datasetForm.value.refreshGroup();
    })
    .catch(e => {
      console.warn(e);
    });
};
const onDeleteReportDatasetGroup = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此分组？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      return ReportDatasetGroupController.delete({
        groupId: row.groupId,
      });
    })
    .then(() => {
      ElMessage.success('删除成功！');
      datasetForm.value.refreshGroup();
    })
    .catch(e => {
      console.warn(e);
    });
};
const onEditReportDataset = (row: ANY_OBJECT | null = null) => {
  Dialog.show(
    row ? '新建数据集' : '编辑数据集',
    EditReportDataset,
    {
      area: ['100vw', '100vh'],
      skin: 'fullscreen-dialog',
    },
    {
      datasetId: (row || {}).datasetId,
      groupList: groupTree.value,
      datasetList: allDatasetList.value,
      path: 'thirdEditReportDataset',
    },
    {
      fullscreen: true,
      pathName: '/thirdParty/thirdEditReportDataset',
    },
  )
    .then(() => {
      loadAllDatasetData();
    })
    .catch(e => {
      console.warn(e);
      loadAllDatasetData();
    });
};
const onDeleteDataset = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此数据集？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      return ReportDatasetController.delete({
        datasetId: row.datasetId,
      });
    })
    .then(() => {
      ElMessage.success('删除成功！');
      loadAllDatasetData();
    })
    .catch(e => {
      console.warn(e);
    });
};
const onRefreshDataset = (group: ANY_OBJECT) => {
  currentGroup.value = group;
  loadAllDatasetData();
  reportDatasetWidget.refreshTable(true, 1);
};
</script>
