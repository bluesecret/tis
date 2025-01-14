<template>
  <AdvanceQuery
    class="form-report-page"
    ref="pageForm"
    :height="mainContextHeight"
    :treePanel="pageGroupConfig"
    :tablePanel="pageConfig"
    @editGroupItem="onEditReportPageGroup"
    @deleteGroupItem="onDeleteReportPagetGroup"
    @refreshTable="onRefreshPage"
    @addTableItem="onEditReportPage()"
  >
    <template #table>
      <table-box
        style="padding-bottom: 16px"
        :data="reportPageWidget.dataList"
        :size="layoutStore.defaultFormItemSize"
        header-cell-class-name="table-header-gray"
        height="auto"
        :hasExtend="false"
        :seq-config="{ startIndex: (reportPageWidget.currentPage - 1) * reportPageWidget.pageSize }"
      >
        <vxe-column title="序号" type="seq" width="50px" :index="reportPageWidget.getTableIndex" />
        <vxe-column title="页面名称" field="pageName" />
        <vxe-column title="页面编码" field="pageCode" />
        <vxe-column title="操作" width="100px">
          <template v-slot="scope">
            <el-button
              link
              type="primary"
              :size="layoutStore.defaultFormItemSize"
              @click="onEditReportPage(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="danger"
              :size="layoutStore.defaultFormItemSize"
              @click="onDeleteReportPage(scope.row)"
            >
              删除
            </el-button>
          </template>
        </vxe-column>
        <template #pagination>
          <el-row type="flex" justify="end" style="margin-top: 16px">
            <el-pagination
              :total="reportPageWidget.totalCount"
              :current-page="reportPageWidget.currentPage"
              :page-size="reportPageWidget.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, prev, pager, next, sizes"
              @current-change="reportPageWidget.onCurrentPageChange"
              @size-change="reportPageWidget.onPageSizeChange"
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
import { ReportPageGroupController, ReportPageController } from '@/api/report';
import { ANY_OBJECT } from '@/types/generic';
import { Dialog } from '@/components/Dialog';
import { useTable } from '@/common/hooks/useTable';
import { TableOptions } from '@/common/types/pagination';
import { useLayoutStore } from '@/store';
import EditReportPageGroup from './editReportPageGroup.vue';
import EditReportPage from './editReportPage/index.vue';

const layoutStore = useLayoutStore();
const mainContextHeight = inject('mainContextHeight', 200);
const clientHeight = inject('documentClientHeight', 200);

const pageForm = ref();
const reportPageGroupList = ref<ANY_OBJECT[]>([]);
const loadReportPageGroup = () => {
  return new Promise((resolve, reject) => {
    ReportPageGroupController.list({})
      .then(res => {
        reportPageGroupList.value = res.data;
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const pageGroupConfig = {
  title: '报表分组',
  supportAdd: true,
  supportEdit: true,
  supportDelete: true,
  addText: '新建分组',
  keyColumnName: 'groupId',
  nameColumnName: 'groupName',
  loadFunction: loadReportPageGroup,
};
const pageConfig = {
  title: '报表',
  supportAdd: true,
  addText: '新建页面',
};
const currentGroup = ref();
const currentPageId = ref();
const showEditPage = ref(false);

const loadReportPageData = (params: ANY_OBJECT) => {
  return new Promise((resolve, reject) => {
    if (currentGroup.value == null) {
      resolve({
        dataList: [],
        totalCount: 0,
      });
      return;
    }
    params.reportPageDtoFilter = {
      groupId: currentGroup.value.groupId,
    };
    ReportPageController.list(params)
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
const loadReportPageVerify = () => {
  return true;
};
const tableOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadReportPageData,
  verifyTableParameter: loadReportPageVerify,
  paged: true,
};
const reportPageWidget = reactive(useTable(tableOptions));

const groupTree = computed(() => {
  if (Array.isArray(reportPageGroupList.value)) {
    return treeDataTranslate(
      reportPageGroupList.value.map(item => {
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

const onEditReportPageGroup = (row: ANY_OBJECT | null) => {
  Dialog.show(
    row ? '编辑分组' : '新建分组',
    EditReportPageGroup,
    {
      area: ['600px'],
    },
    {
      groupId: (row || {}).groupId,
      groupList: groupTree.value,
      path: 'thirdEditReportPageGroup',
    },
    {
      width: '600px',
      height: '400px',
      pathName: '/thirdParty/thirdEditReportPageGroup',
    },
  )
    .then(() => {
      pageForm.value.refreshGroup();
    })
    .catch(e => {
      console.warn(e);
    });
};
const onDeleteReportPagetGroup = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此分组？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      return ReportPageGroupController.delete({
        groupId: row.groupId,
      });
    })
    .then(() => {
      ElMessage.success('删除成功！');
      pageForm.value.refreshGroup();
    })
    .catch(e => {
      console.warn(e);
    });
};
const onRefreshPage = (group: ANY_OBJECT) => {
  currentGroup.value = group;
  refreshPage(true);
};
const refreshPage = (reload = false) => {
  if (reload) {
    reportPageWidget.refreshTable(true, 1);
  } else {
    reportPageWidget.refreshTable();
  }
};
const onEditReportPage = (row: ANY_OBJECT | null = null) => {
  Dialog.show(
    '编辑报表页面',
    EditReportPage,
    {
      area: ['100vw', '100vh'],
      skin: 'fullscreen-dialog',
    },
    {
      pageId: (row || {}).pageId,
      groupList: groupTree.value,
      path: 'thirdEditReportPage',
      clientHeight,
    },
    {
      fullscreen: true,
      pathName: '/thirdParty/thirdEditReportPage',
    },
  )
    .then(() => {
      refreshPage(true);
    })
    .catch(e => {
      console.warn(e);
    });
};
const onDeleteReportPage = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此报表？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      return ReportPageController.delete({
        pageId: row.pageId,
      });
    })
    .then(() => {
      ElMessage.success('删除成功！');
      refreshPage(true);
    })
    .catch(e => {
      console.warn(e);
    });
};
const onCloseEdit = () => {
  showEditPage.value = false;
  currentPageId.value = undefined;
  refreshPage(true);
};
defineExpose({ onCloseEdit });
</script>

<style scoped>
.page-table {
  padding: 0 16px 16px !important;
}
</style>
