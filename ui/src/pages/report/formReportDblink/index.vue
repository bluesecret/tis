<template>
  <AdvanceQuery
    class="form-report-dataset"
    :height="mainContextHeight"
    :treePanel="dblinkGroupConfig"
    :tablePanel="dblinkConfig"
    @refreshTable="onRefresDblink"
    @addTableItem="onEditReportDblink(null)"
  >
    <template #table>
      <table-box
        :data="reportDblinkWidget.dataList"
        :size="layoutStore.defaultFormItemSize"
        header-cell-class-name="table-header-gray"
        height="auto"
        :hasExtend="false"
        :seq-config="{
          startIndex: (reportDblinkWidget.currentPage - 1) * reportDblinkWidget.pageSize,
        }"
        style="padding-bottom: 16px"
      >
        <vxe-column
          title="序号"
          type="seq"
          width="50px"
          :index="reportDblinkWidget.getTableIndex"
        />
        <vxe-column title="链接名称" field="dblinkName" />
        <vxe-column title="链接类型" field="dblinkTypeDictMap.name" />
        <vxe-column title="操作" width="100px">
          <template v-slot="scope">
            <el-button
              link
              type="primary"
              :size="layoutStore.defaultFormItemSize"
              @click="onEditReportDblink(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="danger"
              :size="layoutStore.defaultFormItemSize"
              @click="onDeleteDblink(scope.row)"
            >
              删除
            </el-button>
          </template>
        </vxe-column>
        <template #pagination>
          <el-row type="flex" justify="end" style="margin-top: 16px">
            <el-pagination
              :total="reportDblinkWidget.totalCount"
              :current-page="reportDblinkWidget.currentPage"
              :page-size="reportDblinkWidget.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, prev, pager, next, sizes"
              @current-change="reportDblinkWidget.onCurrentPageChange"
              @size-change="reportDblinkWidget.onPageSizeChange"
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
import { ReportDblinkController } from '@/api/report';
import AdvanceQuery from '@/components/AdvanceQuery/index.vue';
import { ANY_OBJECT } from '@/types/generic';
import { Dialog } from '@/components/Dialog';
import { useTable } from '@/common/hooks/useTable';
import { TableOptions } from '@/common/types/pagination';
import { DblinkType } from '@/common/staticDict';
import { useLayoutStore } from '@/store';
import EditReportDblink from './editReportDblink.vue';

const layoutStore = useLayoutStore();
const mainContextHeight = inject('mainContextHeight', 200);
const currentDblinkType = ref(DblinkType.MYSQL);

const loadDblinkGroupData = () => {
  return Promise.resolve(DblinkType.getList());
};
const dblinkGroupConfig = {
  title: '数据库链接类型',
  supportAdd: false,
  supportEdit: false,
  supportDelete: false,
  keyColumnName: 'id',
  nameColumnName: 'name',
  loadFunction: loadDblinkGroupData,
};
const dblinkConfig = {
  title: '数据库链接',
  supportAdd: true,
  addText: '新建链接',
};

const loadDblinkData = (params: ANY_OBJECT) => {
  return new Promise((resolve, reject) => {
    params.reportDblinkDtoFilter = {
      dblinkType: currentDblinkType.value,
    };
    ReportDblinkController.list(params)
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
const loadDblinkDataVerify = () => {
  return true;
};
const tableOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadDblinkData,
  verifyTableParameter: loadDblinkDataVerify,
  paged: true,
};
const reportDblinkWidget = reactive(useTable(tableOptions));

const refresDblink = (reload = false) => {
  if (reload) {
    reportDblinkWidget.refreshTable(true, 1);
  } else {
    reportDblinkWidget.refreshTable();
  }
};
const onRefresDblink = (dblinkType: ANY_OBJECT) => {
  currentDblinkType.value = dblinkType.id;
  refresDblink(true);
};
const onEditReportDblink = (row: ANY_OBJECT | null) => {
  Dialog.show(
    '编辑数据库链接',
    EditReportDblink,
    {
      area: ['100vw', '100vh'],
      skin: 'fullscreen-dialog',
    },
    {
      dblink: row,
      path: 'thirdEditReportDblink',
    },
    {
      fullscreen: true,
      pathName: '/thirdParty/thirdEditReportDblink',
    },
  )
    .then(() => {
      refresDblink(true);
    })
    .catch(e => {
      console.warn(e);
      refresDblink(true);
    });
};
const onDeleteDblink = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此数据链接？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      return ReportDblinkController.delete({
        dblinkId: row.dblinkId,
      });
    })
    .then(() => {
      ElMessage.success('删除成功');
      refresDblink(true);
    })
    .catch(e => {
      console.warn(e);
    });
};
</script>
