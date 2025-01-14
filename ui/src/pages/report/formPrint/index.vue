<template>
  <AdvanceQuery
    ref="printGroup"
    :height="mainContextHeight"
    :treePanel="printGroupConfig"
    :tablePanel="printTemplateConfig"
    @editGroupItem="onEditPrintGroup"
    @deleteGroupItem="onDeletePrintGroup"
    @refreshTable="onRefreshTemplate"
    @addTableItem="onEditPrintTemplate(null)"
  >
    <template #table>
      <table-box
        :data="printTemplateWidget.dataList"
        :size="layoutStore.defaultFormItemSize"
        height="auto"
        :hasExtend="false"
        :seq-config="{
          startIndex: (printTemplateWidget.currentPage - 1) * printTemplateWidget.pageSize,
        }"
        style="padding-bottom: 16px"
      >
        <vxe-column
          title="序号"
          type="seq"
          width="50px"
          :index="printTemplateWidget.getTableIndex"
        />
        <vxe-column title="模版名称" field="printName" />
        <vxe-column title="模版标识" field="printVariable" />
        <vxe-column title="模版类型">
          <template v-slot="scope">
            <el-tag size="default" type="primary">
              {{ PrintTemplateType.getValue(scope.row.templateType) }}
            </el-tag>
          </template>
        </vxe-column>
        <vxe-column title="操作" width="150px">
          <template v-slot="scope">
            <el-button
              link
              type="primary"
              :size="layoutStore.defaultFormItemSize"
              @click="onEditPrintTemplate(scope.row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="primary"
              :size="layoutStore.defaultFormItemSize"
              @click="onPreview(scope.row)"
            >
              预览
            </el-button>
            <el-button
              link
              type="danger"
              :size="layoutStore.defaultFormItemSize"
              @click="onDeletePrintTemplate(scope.row)"
            >
              删除
            </el-button>
          </template>
        </vxe-column>
        <template #pagination>
          <el-row type="flex" justify="end" style="margin-top: 16px">
            <el-pagination
              :total="printTemplateWidget.totalCount"
              :current-page="printTemplateWidget.currentPage"
              :page-size="printTemplateWidget.pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, prev, pager, next, sizes"
              @current-change="printTemplateWidget.onCurrentPageChange"
              @size-change="printTemplateWidget.onPageSizeChange"
            >
            </el-pagination>
          </el-row>
        </template>
      </table-box>
    </template>
  </AdvanceQuery>
</template>

<script setup lang="ts">
import { VxeColumn } from 'vxe-table';
import { ElMessageBox, ElMessage } from 'element-plus';
import { treeDataTranslate } from '@/common/utils';
import { PrintTemplateType } from '@/common/staticDict/report';
import { ReportPrintController, ReportPrintGroupController } from '@/api/report';
import AdvanceQuery from '@/components/AdvanceQuery/index.vue';
import { ANY_OBJECT } from '@/types/generic';
import { useTable } from '@/common/hooks/useTable';
import { TableOptions } from '@/common/types/pagination';
import { Dialog } from '@/components/Dialog';
import { useLayoutStore } from '@/store';
import EditPrintManageGroup from './editPrintManageGroup.vue';
import EditPrintTemplate from './editPrintTemplate.vue';
import PreviewPrint from './components/previewPrint.vue';
const layoutStore = useLayoutStore();

const mainContextHeight = inject('mainContextHeight', 200);

const printGroup = ref<ANY_OBJECT>();
// 当前选中分组
const currentGroup = ref<ANY_OBJECT>();
const printGroupList = ref<ANY_OBJECT[]>([]);
const groupTree = computed(() => {
  if (Array.isArray(printGroupList.value)) {
    return treeDataTranslate(
      printGroupList.value.map(item => {
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
// 获取打印模版分组数据
const loadPrintGroupData = (params: ANY_OBJECT) => {
  return new Promise((resolve, reject) => {
    ReportPrintGroupController.list(params)
      .then(res => {
        printGroupList.value = res.data;
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const printGroupConfig = {
  title: '打印模版分组',
  supportAdd: true,
  supportEdit: true,
  supportDelete: true,
  addText: '新建分组',
  keyColumnName: 'groupId',
  nameColumnName: 'groupName',
  loadFunction: loadPrintGroupData,
};
const printTemplateConfig = {
  title: '打印模版',
  supportAdd: true,
  addText: '新建模版',
};
// 获取分组下打印模版数据
const loadPrintTemplateData = (params: ANY_OBJECT) => {
  return new Promise((resolve, reject) => {
    if (currentGroup.value == null) {
      resolve({
        dataList: [],
        totalCount: 0,
      });
      return;
    }
    params.reportPrintDtoFilter = {
      groupId: (currentGroup.value || {}).groupId,
    };
    ReportPrintController.list(params)
      .then(res => {
        (res.data.dataList || []).forEach(item => {
          try {
            if (item.paramJson && item.paramJson !== '') {
              item.paramJson = JSON.parse(item.paramJson);
            } else {
              item.paramJson = [];
            }
            if (item.printJson && item.printJson !== '') {
              item.printJson = JSON.parse(item.printJson);
            } else {
              item.printJson = {};
            }
            item.templateType = item.printJson.templateType || PrintTemplateType.EXCEL;
          } catch (e) {
            console.error(e);
            item.paramJson = [];
          }
        });
        resolve({
          dataList: res.data.dataList || [],
          totalCount: res.data.totalCount || 0,
        });
      })
      .catch(e => {
        console.log(e);
        reject(e);
      });
  });
};
const loadPrintTemplateDataVerify = () => {
  return true;
};
const tableOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadPrintTemplateData,
  verifyTableParameter: loadPrintTemplateDataVerify,
  paged: true,
};
const printTemplateWidget = reactive(useTable(tableOptions));

const refreshTemplate = (reload = false) => {
  if (reload) {
    printTemplateWidget.refreshTable(true, 1);
  } else {
    printTemplateWidget.refreshTable();
  }
};
const onEditPrintGroup = (row: ANY_OBJECT | null) => {
  Dialog.show(
    row ? '编辑分组' : '新建分组',
    EditPrintManageGroup,
    {
      area: ['600px'],
    },
    {
      groupId: (row || {}).groupId,
      groupList: groupTree.value,
      path: 'thirdEditPrintGroup',
    },
    {
      width: '600px',
      height: '400px',
      pathName: '/thirdParty/thirdEditPrintGroup',
    },
  )
    .then(() => {
      printGroup.value?.refreshGroup();
    })
    .catch(e => {
      console.warn(e);
    });
};
const onDeletePrintGroup = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此打印分组？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      return ReportPrintGroupController.delete({
        groupId: row.groupId,
      });
    })
    .then(() => {
      ElMessage.success('删除成功！');
      printGroup.value?.refreshGroup();
    })
    .catch(e => {
      console.warn(e);
    });
};
const onEditPrintTemplate = (row: ANY_OBJECT | null) => {
  Dialog.show(
    row ? '编辑模版' : '新建模版',
    EditPrintTemplate,
    {
      area: ['100vw', '100vh'],
      skin: 'fullscreen-dialog',
    },
    {
      printId: (row || {}).printId,
      reportPrintGroupList: groupTree.value,
      path: 'thirdEditPrint',
    },
    {
      fullscreen: true,
      pathName: '/thirdParty/thirdEditPrint',
    },
  )
    .then(() => {
      refreshTemplate();
    })
    .catch(e => {
      console.warn(e);
      refreshTemplate();
    });
};
const onDeletePrintTemplate = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此打印模版？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      return ReportPrintController.delete({
        printId: row.printId,
      });
    })
    .then(() => {
      ElMessage.success('删除成功！');
      refreshTemplate();
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
const onPreview = (row: ANY_OBJECT) => {
  Dialog.show(
    '预览',
    PreviewPrint,
    {
      area: ['600px', '410px'],
      skin: 'print-preview',
    },
    {
      printId: row.printId,
      printName: row.printName,
      paramList: row.paramJson,
      templateType: row.templateType,
      path: 'thirdPrintPreview',
    },
    {
      width: '600px',
      height: '450px',
      pathName: '/thirdParty/thirdPrintPreview',
    },
  ).catch(e => {
    console.warn(e);
  });
};
const onRefreshTemplate = (group: ANY_OBJECT) => {
  currentGroup.value = group;
  refreshTemplate(true);
};
</script>

<style scoped>
.print-template {
  padding: 0;
  margin-left: 15px;
  background: white;
}

.form-print :deep(.el-tree-node__content) {
  height: 35px;
}

.form-print .tree-node-item {
  width: 100%;
  height: 35px;
  line-height: 35px;
}
</style>
