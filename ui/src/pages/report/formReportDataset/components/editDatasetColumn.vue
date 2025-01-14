<template>
  <div class="form-single-fragment edit-dataset-column" style="position: relative">
    <el-form
      label-width="0px"
      :size="formItemSize"
      label-position="left"
      :inline="true"
      @submit.prevent
    >
      <el-form-item label="">
        <el-radio-group v-model="filter.dimension">
          <el-radio-button :value="undefined">全部</el-radio-button>
          <el-radio-button :value="true">维度</el-radio-button>
          <el-radio-button :value="false">指标</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="">
        <el-input v-model="filter.columnName" clearable placeholder="字段名称" />
      </el-form-item>
      <el-button
        v-if="!supportAddColumn"
        style="float: right"
        @click="onSyncColumns"
        :size="formItemSize"
        >同步字段</el-button
      >
      <el-button
        v-if="supportAddColumn"
        style="float: right"
        type="primary"
        @click="onSaveAllColumn()"
        :size="formItemSize"
        >保存</el-button
      >
      <el-button
        v-if="supportAddColumn"
        style="float: right; margin-right: 10px"
        @click="onAddColumn()"
        :size="formItemSize"
        :disabled="columnList.length > 0"
      >
        添加字段
      </el-button>
    </el-form>
    <el-row>
      <vxe-table
        ref="columnTable"
        class="dataset-column-table"
        :row-config="{ keyField: 'columnId', isHover: true }"
        :data="validColumnList"
        :size="formItemSize"
        :height="height - 100 + 'px'"
        :tree-config="{
          rowField: 'columnId',
          parentField: 'parentId',
          expandRowKeys: defaultExpandKeys,
        }"
        @toggle-tree-expand="onExpandChange"
        header-cell-class-name="table-header-gray"
        :row-class-name="getRowClassName"
        style="width: 100%"
      >
        <vxe-column title="显示名" width="200px" tree-node>
          <template v-slot="scope">
            <el-input
              size="default"
              clearable
              v-model="scope.row.columnComment"
              style="width: 100%"
              @keydown.enter="updateDatasetColumn(scope.row)"
              @change="updateDatasetColumn(scope.row)"
            />
          </template>
        </vxe-column>
        <vxe-column v-if="!supportAddColumn" title="字典绑定" field="dictId" min-width="150px">
          <template v-slot="scope">
            <el-select
              size="default"
              v-model="scope.row.dictId"
              placeholder=""
              clearable
              :disabled="isObjectColumn(scope.row)"
              @change="updateDatasetColumn(scope.row)"
            >
              <el-option
                v-for="item in dictList"
                :key="item.id"
                :value="item.id"
                :label="item.name"
              />
            </el-select>
          </template>
        </vxe-column>
        <vxe-column title="字段名" field="columnName" min-width="200px">
          <template v-slot="scope">
            <el-input
              v-if="supportAddColumn"
              size="default"
              clearable
              v-model="scope.row.columnName"
              @keydown.enter="updateDatasetColumn(scope.row)"
              @change="updateDatasetColumn(scope.row)"
            />
            <span v-else>{{ scope.row.columnName }}</span>
          </template>
        </vxe-column>
        <vxe-column title="字段类型" field="fieldType" min-width="100px">
          <template v-slot="scope">
            <el-select
              v-if="supportAddColumn"
              size="default"
              v-model="scope.row.fieldType"
              placeholder=""
              clearable
              @change="updateDatasetColumn(scope.row)"
            >
              <el-option v-for="item in fieldTypeList" :key="item" :value="item" :label="item" />
            </el-select>
            <span v-else>{{ scope.row.fieldType }}</span>
          </template>
        </vxe-column>
        <vxe-column
          v-if="!supportAddColumn"
          title="原始类型"
          field="columnType"
          min-width="100px"
        />
        <vxe-column title="图片字段" min-width="80px">
          <template v-slot="scope">
            <el-switch
              v-model="scope.row.image"
              :disabled="isObjectColumn(scope.row)"
              @change="updateDatasetColumn(scope.row)"
            />
          </template>
        </vxe-column>
        <vxe-column v-if="!supportAddColumn" title="数据权限" min-width="150px">
          <template v-slot="scope">
            <el-select
              size="default"
              placeholder=""
              clearable
              :model-value="getDataPermValue(scope.row)"
              @change="(val:string) => { setDataPermValue(val, scope.row); updateDatasetColumn(scope.row, true)}"
            >
              <el-option label="用户过滤字段" value="userFilter" />
              <el-option label="部门过滤字段" value="deptFilter" />
            </el-select>
          </template>
        </vxe-column>
        <vxe-column v-if="!supportAddColumn" title="逻辑删除" min-width="80px">
          <template v-slot="scope">
            <el-switch v-model="scope.row.logicDelete" @change="updateDatasetColumn(scope.row)" />
          </template>
        </vxe-column>
        <vxe-column title="维度 / 指标" min-width="150px">
          <template v-slot="scope">
            <el-select
              v-model="scope.row.dimension"
              placeholder=""
              size="default"
              :disabled="isObjectColumn(scope.row)"
              @change="updateDatasetColumn(scope.row)"
            >
              <el-option label="维度" :value="true" />
              <el-option label="指标" :value="false" />
            </el-select>
          </template>
        </vxe-column>
        <vxe-column v-if="supportAddColumn" title="操作" width="150px">
          <template v-slot="scope">
            <el-button
              link
              type="primary"
              :size="formItemSize"
              :disabled="!isObjectColumn(scope.row)"
              @click="onAddColumn(scope.row)"
            >
              添加子字段
            </el-button>
            <el-button link type="danger" :size="formItemSize" @click="onDeleteColumn(scope.row)">
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
      </vxe-table>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { VxeTable, VxeColumn } from 'vxe-table';
import { ElMessage, ElMessageBox } from 'element-plus';
import { treeDataTranslate, findItemFromList } from '@/common/utils';
import { ReportDatasetColumnController } from '@/api/report';
import { DatasetType } from '@/common/staticDict/report';
import { ANY_OBJECT } from '@/types/generic';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

const emit = defineEmits<{ saveColumn: [ANY_OBJECT[]]; syncColumns: []; refreshColumns: [] }>();

const props = withDefaults(
  defineProps<{
    dataset?: ANY_OBJECT;
    height: number;
    datasetColumnList?: ANY_OBJECT[];
    dictList?: ANY_OBJECT[];
  }>(),
  {},
);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const columnTable = ref();
const filter = ref<ANY_OBJECT>({
  dimension: '',
  columnName: '',
});
const columnList = ref<ANY_OBJECT[]>([]);
const fieldTypeList = [
  'String',
  'Integer',
  'Long',
  'Double',
  'BigDecimal',
  'Date',
  'Boolean',
  'Object',
  'Array',
];
// 展开行
const defaultExpandKeys = ref<string[] | number[] | undefined>([]);
const treeExpandeds = ref<ANY_OBJECT[]>([]);

// 是否允许添加字段，目前只有api结果集支持添加字段
const supportAddColumn = computed(() => {
  return (props.dataset || {}).datasetType === DatasetType.API;
});
const validColumnList = computed(() => {
  // 获取过滤后的字段列表
  let filteredColumnList = (columnList.value || []).filter(item => {
    return (
      (!filter.value.columnName || item.columnName.indexOf(filter.value.columnName) !== -1) &&
      (filter.value.dimension === '' ? true : item.dimension === filter.value.dimension)
    );
  });
  let finalColumnList: ANY_OBJECT[] = [];
  filteredColumnList.forEach(column => {
    let parentColumn = findItemFromList(columnList.value, column.parentId, 'columnId');
    while (parentColumn) {
      // 判断父字段是否在列表中
      if (findItemFromList(finalColumnList, parentColumn.columnId, 'columnId') == null) {
        // 添加父字段
        parentColumn.children = undefined;
        finalColumnList.push(parentColumn);
        parentColumn = findItemFromList(columnList.value, parentColumn.parentId, 'columnId');
      } else {
        break;
      }
    }
    // 添加字段
    if (findItemFromList(finalColumnList, column.columnId, 'columnId') == null) {
      column.children = undefined;
      finalColumnList.push(column);
    }
  });
  const data = supportAddColumn.value
    ? treeDataTranslate(finalColumnList, 'columnId', 'parentId')
    : finalColumnList;
  nextTick(() => {
    treeExpandeds.value.forEach(row => {
      columnTable.value.setTreeExpand(row, true);
    });
  });
  return data;
});

const getDataPermValue = (row: ANY_OBJECT) => {
  if (row.userFilter) return 'userFilter';
  if (row.deptFilter) return 'deptFilter';
  return undefined;
};
const setDataPermValue = (val: string, row: ANY_OBJECT) => {
  row.userFilter = val === 'userFilter';
  row.deptFilter = val === 'deptFilter';
};
const onSyncColumns = () => {
  // columnNameFilter = undefined;
  // columnNameFilterCopy = undefined;
  // dimensionFilter = undefined;
  // dimensionFilterCopy = undefined;
  emit('syncColumns');
};
const updateDatasetColumn = (row: ANY_OBJECT, refresh = false) => {
  //console.log('column change', row == null || supportAddColumn.value, row);
  // api数据集字段信息存储在json里，不调用保存
  if (row == null || supportAddColumn.value) {
    columnList.value.forEach(item => {
      console.log(item == row, item, row.columnId === item.columnId);
      if (row.columnId === item.columnId) {
        item.parentId = row.parentId;
        item.columnComment = row.columnComment;
        item.dictId = row.dictId;
        item.columnName = row.columnName;
        item.fieldType = row.fieldType;
        item.image = row.image;
        item.dimension = row.dimension;
      }
    });
    console.log('columnList', columnList.value);
    return;
  }
  if (row.columnComment == null || row.columnComment === '') {
    ElMessage.error('字段显示名不能为空！');
    return;
  }

  let params = {
    reportDatasetColumnDto: {
      ...row,
    },
  };
  ReportDatasetColumnController.update(params)
    .then(() => {
      if (refresh) emit('refreshColumns');
    })
    .catch(e => {
      console.warn(e);
    });
};
const onAddColumn = (row: ANY_OBJECT | null = null) => {
  if (row) onExpandChange({ row, expanded: true });
  columnList.value.push({
    parentId: (row || {}).columnId,
    columnId: new Date().getTime(),
    columnComment: columnList.value.length > 0 ? undefined : 'data',
    dictId: undefined,
    columnName: columnList.value.length > 0 ? undefined : 'data',
    fieldType: undefined,
    image: false,
    dimension: true,
  });
};
const onDeleteColumn = (row: ANY_OBJECT) => {
  if (row.children && row.children.length > 0) {
    ElMessage.error('请删除子字段！');
    return;
  }
  ElMessageBox.confirm('是否删除此字段？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      columnList.value = columnList.value.filter(column => {
        return column.columnId !== row.columnId;
      });
    })
    .catch(e => {
      console.warn(e);
    });
};
const checkColumnItem = (column: ANY_OBJECT) => {
  if (column == null) return null;
  let warningMsg = null;
  if (column.fieldType == null || column.fieldType === '') warningMsg = '字段类型不能为空！';
  if (column.columnName == null || column.columnName === '') warningMsg = '字段名不能为空！';
  if (column.columnComment == null || column.columnComment === '')
    warningMsg = '字段显示名不能为空！';
  return warningMsg;
};
const checkColumn = () => {
  console.log('>>> checkColumn', columnList.value);
  if (Array.isArray(columnList.value) && columnList.value.length > 0) {
    let warningMsg = null;
    for (let i = 0; i < columnList.value.length; i++) {
      let column = columnList.value[i];
      warningMsg = checkColumnItem(column);
      if (warningMsg != null) break;
    }
    if (warningMsg != null) {
      ElMessage.error(warningMsg);
      return false;
    }
    return true;
  } else {
    ElMessage.error('请添加数据集字段！');
    return false;
  }
};
const onSaveAllColumn = () => {
  if (!checkColumn()) return;
  emit(
    'saveColumn',
    columnList.value.map(item => {
      return {
        ...item,
        children: undefined,
        _level: undefined,
        _parent: undefined,
      };
    }),
  );
};
const isObjectColumn = (row: ANY_OBJECT) => {
  return row.fieldType === 'Object' || row.fieldType === 'Array';
};
const onExpandChange = ({ row, expanded }: { row: ANY_OBJECT; expanded: boolean }) => {
  if (expanded) {
    if (defaultExpandKeys.value?.indexOf(row.columnId) === -1)
      defaultExpandKeys.value.push(row.columnId);
  } else {
    defaultExpandKeys.value = defaultExpandKeys.value?.filter(item => {
      return item !== row.columnId;
    }) as string[];
  }
  treeExpandeds.value = [...columnTable.value.getTreeExpandRecords()];
};
const getRowClassName = ({ row }: { row: ANY_OBJECT }) => {
  return checkColumnItem(row) ? 'error-row' : undefined;
};

onMounted(() => {
  if (Array.isArray(validColumnList.value) && validColumnList.value.length > 0) {
    defaultExpandKeys.value = [validColumnList.value[0].columnId];
    treeExpandeds.value =
      columnTable.value && columnTable.value.treeExpandeds
        ? [...columnTable.value.getTreeExpandRecords()]
        : [];
  }
});

watch(
  () => props.datasetColumnList,
  newValue => {
    columnList.value = (newValue || []).map(item => {
      return {
        ...item,
      };
    });
  },
  {
    immediate: true,
  },
);
</script>

<style scoped>
.edit-dataset-column {
  width: 90vw;
  height: 100%;
  padding: 20px;
  background: white;
}

.dataset-column-table :deep(.vxe__row.error-row) {
  background: #fef0f0 !important;
}
</style>
