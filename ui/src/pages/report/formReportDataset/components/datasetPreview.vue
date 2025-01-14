<template>
  <div class="form-single-fragment edit-dataset-preview" style="position: relative">
    <el-form label-width="80px" :size="formItemSize" label-position="left" @submit.prevent>
      <el-form-item label="显示行数">
        <el-input v-model="lineCount" placeholder="" style="width: 200px">
          <template #append>
            <el-button :icon="Search" @click="loadPreviewData"></el-button>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
    <el-row>
      <vxe-table
        :data="preview.dataList"
        :size="formItemSize"
        header-cell-class-name="table-header-gray"
        :height="height ? height - 100 + 'px' : ''"
        :row-config="{ isHover: true }"
        style="width: 100%"
      >
        <vxe-column
          v-for="item in preview.columnMetaList"
          :key="item.columnName"
          :title="item.columnName"
          :field="item.columnName"
          show-overflow="tooltip"
          min-width="120px"
        >
          <template v-slot="scope">
            <span class="table-cell" :title="scope.row[item.columnName]">{{
              scope.row[item.columnName]
            }}</span>
          </template>
        </vxe-column>
        <template v-slot:empty>
          <div class="table-empty unified-font">
            <img src="@/assets/img/empty.png" />
            <span>暂无数据</span>
          </div>
        </template>
      </vxe-table>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { VxeTable, VxeColumn } from 'vxe-table';
import { Search } from '@element-plus/icons-vue';
import { ReportDatasetController } from '@/api/report';
import { ANY_OBJECT } from '@/types/generic';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

const props = withDefaults(
  defineProps<{
    datasetId?: string;
    height?: number;
  }>(),
  {},
);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const lineCount = ref();
const preview = ref<ANY_OBJECT>({
  dataList: [],
  columnMetaList: [],
});

const clear = () => {
  preview.value = {
    dataList: [],
    columnMetaList: [],
  };
};
const loadPreviewData = () => {
  if (props.datasetId == null || props.datasetId === '') {
    clear();
    return;
  }

  ReportDatasetController.previewDataset({
    datasetId: props.datasetId,
    pageParam: {
      pageNum: 1,
      pageSize: lineCount.value || 1000,
    },
  })
    .then(res => {
      const data = res.data as ANY_OBJECT;
      data.dataList =
        data.dataList?.map((item: ANY_OBJECT) => {
          return { ...item };
        }) || [];
      data.columnMetaList =
        data.columnMetaList?.map((item: ANY_OBJECT) => {
          return { ...item };
        }) || [];
      preview.value = data;
    })
    .catch(e => {
      console.warn(e);
      clear();
    });
};

watch(
  () => props.datasetId,
  () => {
    loadPreviewData();
  },
  {
    immediate: true,
  },
);
</script>

<style scoped>
.edit-dataset-preview {
  width: 90vw;
  height: 100%;
  padding: 20px;
  background: white;
}

.table-cell {
  overflow: hidden;
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
