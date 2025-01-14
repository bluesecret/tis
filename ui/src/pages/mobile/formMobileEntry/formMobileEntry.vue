<template>
  <div class="page-box" style="position: relative">
    <table-box
      class="page-table"
      :data="formMobileEntry.MobileEntry.impl.dataList"
      :size="layoutStore.defaultFormItemSize"
      :row-config="{ isCurrent: false, isHover: true }"
      :seq-config="{
        startIndex:
          (formMobileEntry.MobileEntry.impl.currentPage - 1) *
          formMobileEntry.MobileEntry.impl.pageSize,
      }"
      :sort-config="{ remote: true }"
      :hasExtend="true"
      :tree-config="{ rowField: 'entryId', parentField: 'parentId' }"
      @sort-change="formMobileEntry.MobileEntry.impl.onSortChange"
      @refresh="refreshFormMobileEntry(true)"
    >
      <template #operator>
        <!-- 表格操作 -->
        <el-button
          type="primary"
          :size="layoutStore.defaultFormItemSize"
          @click="onFormAddMobileEntryClick()"
        >
          {{ entryType === MobileEntryType.SUDOKU ? '新建分组' : '新建轮播图' }}
        </el-button>
      </template>
      <!-- 表格列 -->
      <vxe-column
        v-if="entryType !== MobileEntryType.SUDOKU"
        title="序号"
        type="seq"
        width="50px"
        :index="formMobileEntry.MobileEntry.impl.getTableIndex"
      />
      <vxe-column
        title="显示名称"
        field="entryName"
        :tree-node="entryType === MobileEntryType.SUDOKU"
      />
      <vxe-column title="绑定类型" field="">
        <template v-slot="scope">
          <span v-if="entryType !== MobileEntryType.GROUP">
            {{ SysMenuBindType.getValue(scope.row.bindType) }}
          </span>
        </template>
      </vxe-column>
      <vxe-column title="显示顺序" field="showOrder" />
      <vxe-column title="显示图片">
        <template v-slot="scope">
          <el-image
            v-if="scope.row.entryType !== MobileEntryType.GROUP"
            style="width: 44px; height: 44px; padding: 10px 0; box-sizing: content-box"
            :src="getImageData(scope.row)"
          />
        </template>
      </vxe-column>
      <vxe-column
        title="操作"
        fixed="right"
        :width="entryType === MobileEntryType.SUDOKU ? '200px' : '100px'"
      >
        <template v-slot="scope">
          <el-button
            v-if="entryType === MobileEntryType.SUDOKU"
            link
            type="primary"
            :size="layoutStore.defaultFormItemSize"
            :disabled="scope.row.entryType !== MobileEntryType.GROUP"
            @click.stop="onFormEditMobileEntryClick(null, scope.row.entryId)"
          >
            新建九宫格
          </el-button>
          <el-button
            @click.stop="onFormEditMobileEntryClick(scope.row)"
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
      <template v-slot:empty>
        <div class="table-empty unified-font">
          <img src="@/assets/img/empty.png" />
          <span>暂无数据</span>
        </div>
      </template>
      <!-- 分页 -->
      <template #pagination>
        <el-row type="flex" justify="end" style="margin-top: 10px">
          <el-pagination
            :total="formMobileEntry.MobileEntry.impl.totalCount"
            :current-page="formMobileEntry.MobileEntry.impl.currentPage"
            :page-size="formMobileEntry.MobileEntry.impl.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, prev, pager, next, sizes"
            @current-change="formMobileEntry.MobileEntry.impl.onCurrentPageChange"
            @size-change="formMobileEntry.MobileEntry.impl.onPageSizeChange"
          >
          </el-pagination>
        </el-row>
      </template>
    </table-box>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus';
import { treeDataTranslate } from '@/common/utils';
import { MobileEntryController } from '@/api/mobile';
import { Dialog } from '@/components/Dialog';
import { ANY_OBJECT } from '@/types/generic';
import { useTable } from '@/common/hooks/useTable';
import { TableOptions } from '@/common/types/pagination';
import { useUpload } from '@/common/hooks/useUpload';
import { MobileEntryType, SysMenuBindType } from '@/common/staticDict';
import { useLayoutStore } from '@/store';
import formEditMobileEntry from './formEditMobileEntry.vue';

const props = withDefaults(defineProps<{ entryType: number }>(), { entryType: 1 });

const layoutStore = useLayoutStore();
const groupList = ref<ANY_OBJECT[]>([]);

const loadMobileEntryData = (params: ANY_OBJECT, entryType: number) => {
  if (params == null) params = {};
  params = {
    mobileEntryDtoFilter: {
      entryType: entryType,
    },
    ...params,
  };
  return MobileEntryController.list(params);
};
// 读取轮播图列表
const loadBannerData = (params: ANY_OBJECT) => {
  return new Promise((resolve, reject) => {
    if (params == null) params = {};
    params = {
      mobileEntryDtoFilter: {
        entryType: MobileEntryType.BANNER,
      },
      ...params,
    };
    loadMobileEntryData(params, MobileEntryType.BANNER)
      .then(res => {
        groupList.value = [];
        let dataList = res.data.dataList.map(item => {
          let extraData;
          if (item.extraData != null && item.extraData !== '') {
            extraData = JSON.parse(item.extraData);
          }
          return {
            ...item,
            ...extraData,
          };
        });
        resolve({
          dataList: dataList,
          totalCount: res.data.totalCount,
        });
      })
      .catch(e => {
        reject(e);
      });
  });
};
// 读取九宫格数据
const loadSudokuData = (params: ANY_OBJECT) => {
  return new Promise((resolve, reject) => {
    // 去掉分页
    if (params.pageParam) params.pageParam = undefined;
    let htppCallList = [
      loadMobileEntryData(params, MobileEntryType.GROUP),
      loadMobileEntryData(params, MobileEntryType.SUDOKU),
    ];
    Promise.all(htppCallList)
      .then(res => {
        let list = res[0].data.dataList || [];
        let sudokuList = res[1].data.dataList || [];
        let allDataList = [...list, ...sudokuList];
        groupList.value = list.map(item => {
          return {
            id: item.entryId,
            name: item.entryName,
          };
        });
        allDataList = allDataList.map(item => {
          let extraData;
          if (item.extraData != null && item.extraData !== '') {
            extraData = JSON.parse(item.extraData);
          }
          return {
            ...item,
            ...extraData,
          };
        });
        console.log(groupList.value, allDataList);
        // 九宫格转换成树
        let dataList = treeDataTranslate(allDataList, 'entryId', 'parentId');
        resolve({
          dataList: dataList,
          totalCount: dataList.length,
        });
      })
      .catch(e => {
        reject(e);
      });
  });
};
/**
 * 移动端首页显示管理数据获取函数，返回Promise
 */
const loadMobileEntryWidgetData = (params: ANY_OBJECT) => {
  return props.entryType === MobileEntryType.BANNER
    ? loadBannerData(params)
    : loadSudokuData(params);
};
/**
 * 移动端首页显示管理数据获取检测函数，返回true正常获取数据，返回false停止获取数据
 */
const loadMobileEntryVerify = () => {
  return true;
};
const tableOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadMobileEntryWidgetData,
  verifyTableParameter: loadMobileEntryVerify,
  paged: true,
  orderFieldName: 'showOrder',
  ascending: true,
};
const formMobileEntry = reactive({
  MobileEntry: { impl: useTable(tableOptions) },
  isInit: false,
});

const { getUploadFileUrl } = useUpload();

const getImageData = (row: ANY_OBJECT): string | undefined => {
  if (row == null) return undefined;
  let imageData: { downloadUri: string; filename: string } | null = null;
  try {
    imageData = JSON.parse(row.imageData);
  } catch (e) {
    console.log(e);
    imageData = null;
  }
  console.log('formMobileEntry getImageData', row, imageData);
  return imageData
    ? getUploadFileUrl(imageData, { filename: imageData.filename }) || undefined
    : undefined;
};

/**
 * 更新首页配置
 */
const refreshFormMobileEntry = (reloadData = false) => {
  if (reloadData) {
    formMobileEntry.MobileEntry.impl.refreshTable(true, 1);
  } else {
    formMobileEntry.MobileEntry.impl.refreshTable();
  }
  // if (!formMobileEntry.isInit) {
  //   // 初始化下拉数据
  // }
  formMobileEntry.isInit = true;
};

/**
 * 新建
 */
const onFormAddMobileEntryClick = () => {
  let params = {
    // 九宫格只能新建分组类型
    entryType: props.entryType === MobileEntryType.SUDOKU ? MobileEntryType.GROUP : props.entryType,
    groupList: groupList.value,
  };

  Dialog.show(
    '新建',
    formEditMobileEntry,
    {
      area: '800px',
    },
    params,
  )
    .then(() => {
      refreshFormMobileEntry(true);
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 编辑
 */
const onFormEditMobileEntryClick = (
  row: ANY_OBJECT | null,
  parentId: string | undefined = undefined,
) => {
  // 判断是否新建九宫格
  let entryType = row != null ? row.entryType : MobileEntryType.SUDOKU;
  let params = {
    entryId: (row || {}).entryId,
    entryType: entryType,
    parentId: parentId,
    rowData: row,
    groupList: groupList.value,
  };

  Dialog.show(
    '编辑',
    formEditMobileEntry,
    {
      area: '800px',
    },
    params,
  )
    .then(() => {
      refreshFormMobileEntry();
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
    entryId: row.entryId,
  };

  ElMessageBox.confirm('是否删除？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      MobileEntryController.delete(params)
        .then(() => {
          ElMessage.success('删除成功');
          refreshFormMobileEntry();
        })
        .catch(e => {
          console.warn(e);
        });
    })
    .catch(e => {
      console.warn(e);
    });
};
const onResume = () => {
  refreshFormMobileEntry();
};
const formInit = () => {
  refreshFormMobileEntry();
};
onMounted(() => {
  // 初始化页面数据
  formInit();
});

defineExpose({ onResume });
</script>
