<template>
  <div class="form-single-fragment edit-dataset-relation" style="position: relative">
    <el-row>
      <el-col :span="24">
        <el-table
          :data="relationList"
          :size="formItemSize"
          header-cell-class-name="table-header-gray"
        >
          <el-table-column label="序号" type="index" width="60px" />
          <el-table-column label="关联标识" prop="variableName" />
          <el-table-column label="主数据集" prop="masterDatasetIdDictMap.name" />
          <el-table-column label="主数据集字段" prop="masterColumnIdDictMap.name" />
          <el-table-column label="关联类型" prop="relationTypeDictMap.name" />
          <el-table-column label="关联数据集" prop="slaveDatasetIdDictMap.name" />
          <el-table-column label="关联数据集字段" prop="slaveColumnIdDictMap.name" />
          <el-table-column label="操作" width="120px">
            <template v-slot="scope">
              <el-button type="primary" link size="default" @click="onEditRelation(scope.row)"
                >编辑</el-button
              >
              <el-button type="danger" link size="default" @click="onDeleteRelation(scope.row)"
                >删除</el-button
              >
            </template>
          </el-table-column>
          <template v-slot:empty>
            <div class="table-empty unified-font">
              <img src="@/assets/img/empty.png" />
              <span>暂无数据</span>
            </div>
          </template>
        </el-table>
        <el-button
          class="full-button"
          :size="formItemSize"
          icon="el-icon-plus"
          @click="onEditRelation()"
          >新增数据表</el-button
        >
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ElMessageBox } from 'element-plus';
import { ReportDatasetRelationController } from '@/api/report';
import { ANY_OBJECT } from '@/types/generic';
import { Dialog } from '@/components/Dialog';
import { useLayoutStore } from '@/store';
import EditReportRelation from './editReportRelation.vue';
const layoutStore = useLayoutStore();

const props = withDefaults(
  defineProps<{
    dataset?: ANY_OBJECT;
    datasetList?: ANY_OBJECT[];
  }>(),
  {},
);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const relationList = ref<ANY_OBJECT[]>([]);

const onDeleteRelation = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此关联？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      return ReportDatasetRelationController.delete({
        relationId: row.relationId,
      });
    })
    .then(() => {
      loadDatasetRelation();
    })
    .catch(e => {
      console.warn(e);
    });
};
const onEditRelation = (row: ANY_OBJECT) => {
  Dialog.show(
    '编辑关联',
    EditReportRelation,
    {
      area: ['600px'],
    },
    {
      masterDataset: props.dataset,
      rowData: row,
      datasetList: props.datasetList,
      path: 'thirdEditDatasetRelation',
    },
    {
      width: '600px',
      height: '500px',
      pathName: '/thirdParty/thirdEditDatasetRelation',
    },
  )
    .then(() => {
      loadDatasetRelation();
    })
    .catch(e => {
      console.warn(e);
    });
};
const loadDatasetRelation = () => {
  relationList.value = [];
  let params = {
    reportDatasetRelationDtoFilter: {
      masterDatasetId: (props.dataset || {}).datasetId,
    },
  };

  ReportDatasetRelationController.list(params)
    .then(res => {
      relationList.value = res.data.dataList;
    })
    .catch(e => {
      console.warn(e);
    });
};

watch(
  () => props.dataset,
  () => {
    loadDatasetRelation();
  },
  {
    immediate: true,
  },
);
</script>

<style scoped>
.edit-dataset-relation {
  width: 90vw;
  height: 100%;
  padding: 20px;
  background: white;
}

.full-button {
  width: 100%;
  margin-top: 10px;
  border: 1px dashed #ebeef5;
}
</style>
