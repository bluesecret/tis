<template>
  <el-row class="dataview-table" @click.stop="onTableClick" style="background: white">
    <el-col
      ref="title"
      class="title"
      v-if="title != null && title !== ''"
      :span="24"
      :style="getTitleStyle"
    >
      {{ title }}
    </el-col>
    <el-col class="table" :span="24" v-if="props.data.dataList && props.data.dataList.length > 0">
      <vxe-table
        class="online-table"
        ref="table"
        :max-height="props.options?.basicInfo.height"
        :data="tableDataList"
        :style="getTableStyle"
        border
        show-overflow="title"
        show-header-overflow="title"
        :row-config="rowConfig"
        :header-cell-style="getHeaderCellStyle"
        :cell-style="getCellStyle"
        :footer-cell-style="getCellStyle"
        :sort-config="{ trigger: 'cell', orders: ['desc', 'asc', null] }"
        @sort-change="sortChangeHandler"
      >
        <!-- 其他类型 -->
        <vxe-column
          v-for="(tableColumn, index) in props.columnList"
          :key="index"
          :title="tableColumn.showName || tableColumn.columnComment"
          :width="index == props.columnList.length - 1 ? undefined : tableColumn.columnWidth"
          :sortable="tableColumn.sortable || undefined"
        >
          <template v-slot="scope">
            {{ getColumnValue(scope.row, tableColumn.columnName) }}
          </template>
        </vxe-column>
        <template v-slot:empty>
          <div class="table-empty unified-font">
            <img src="@/assets/img/empty.png" />
            <span>暂无数据</span>
          </div>
        </template>
      </vxe-table>
      <div
        class="pagination"
        :class="'display-' + props.options?.pagerSetting.align"
        style="width: 100%"
      >
        <el-pagination
          v-if="props.options?.pagerSetting.show"
          :total="data.totalCount"
          :page-size="props.options.datasetInfo.pageParam.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :background="props.options.pagerSetting.background == '1' ? true : undefined"
          :small="props.options.pagerSetting.size == 'small' ? true : undefined"
          layout="total, prev, pager, next, sizes"
          :current-page="props.options.datasetInfo.pageParam.pageNum"
          @current-change="handlePageChange"
          @size-change="handlePageSizeChange"
          class="dataViewTable"
        />
      </div>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { VxeTable, VxeColumn, VxeColgroup } from 'vxe-table';
import { ANY_OBJECT } from '@/types/generic';
import { getColumnValue } from './utils';

const props = withDefaults(
  defineProps<{
    // 透视表数据
    data?: ANY_OBJECT;
    // 透视表设置
    options?: ANY_OBJECT;
    columnList?: ANY_OBJECT[];
  }>(),
  {
    data: () => [],
    columnList: () => [],
  },
);

const ready = ref(true);
let buildDataTimer: number | null = null;
// 透视表表头分组信息
// const tableGroupInfo = ref<ANY_OBJECT[]>([]);
// 透视表表格数据
// const tableDataList = ref<ANY_OBJECT[]>([]);
// 是否显示表尾行（支持行总计时显示）
// const showFooter = ref(false);
// 行总计数据
// const footerDataList = ref<ANY_OBJECT[]>([]);

const title = computed(() => {
  if (props.options == null || props.options?.title == null || !props.options?.title.show) {
    return undefined;
  }
  return props.options ? props.options?.title.text : undefined;
});
const rowConfig = computed(() => {
  return {
    height: props.options?.cellRowHeight,
    isHover: true,
  };
});
const getTableStyle = computed(() => {
  return {
    border: '1px solid ' + props.options?.borderColor,
    borderBottom:
      props.data.dataList && props.data.dataList.length > 0
        ? 'none'
        : '1px solid ' + props.options?.borderColor,
  };
});
const getTitleStyle = computed(() => {
  if (props.options == null || props.options?.title == null) return undefined;
  return {
    color: props.options?.title.textStyle.color,
    'font-size': props.options?.title.textStyle.fontSize + 'px',
    'font-weight': props.options?.title.bold ? 600 : undefined,
    'text-align': props.options?.title.left,
    'font-style': props.options?.title.italics ? 'italic' : undefined,
  };
});

// const getFooterDataList = () => {
//   return [footerDataList.value];
// };
// 表头样式
const getHeaderCellStyle = ({ $columnIndex }: { $columnIndex: number }) => {
  return {
    height: props.options?.columnSetting.headerRowHeight + 'px',
    'border-bottom': '1px solid ' + props.options?.borderColor,
    'border-left': $columnIndex > 0 ? '1px solid ' + props.options?.borderColor : 'none',
    background: props.options?.columnSetting.backgroundColor,
    color: props.options?.columnSetting.fontColor,
    'font-size': props.options?.columnSetting.fontSize + 'px',
    'text-align': props.options?.columnSetting.align,
  };
};
// 单元格样式（包括总计、行分组）
const getCellStyle = () => {
  return {
    height: props.options?.cellRowHeight + 'px',
    padding: '0px',
    'border-bottom': '1px solid ' + props.options?.borderColor,
    'border-left': '1px solid ' + props.options?.borderColor,
    background: props.options?.rowSetting.backgroundColor,
    color: props.options?.rowSetting.fontColor,
    'font-size': props.options?.rowSetting.fontSize + 'px',
    'text-align': props.options?.rowSetting.align,
  };
};

const emit = defineEmits<{
  click: [];
  refresh: [ANY_OBJECT | null];
}>();

const onTableClick = () => {
  emit('click');
};

const tableDataList = computed(() => {
  return props.data.dataList.map((item: ANY_OBJECT) => {
    return {
      ...item,
    };
  });
});

// 构建透视表渲染信息
const buildTableInfo = () => {
  if (buildDataTimer != null) clearTimeout(buildDataTimer);
  ready.value = false;
  buildDataTimer = setTimeout(() => {
    //   tableGroupInfo.value = [];
    //   tableDataList.value = [];
    //   footerDataList.value = [];
    //   this.valueColumnMap = new Map();
    //   showFooter.value = false;
    //   this.buildRowGroupInfo();
    //   this.buildColumnGroupInfo();
    //   this.buildTotalInfo();
    //   this.buildMergeItems();
    buildDataTimer = null;
    //   showFooter.value = props.options?.totalSetting.row.show;
    //   this.formatValueCell();
    ready.value = true;
    //   this.valueColumnMap = null;
  }, 50);
};
const refresh = () => {
  emit(
    'refresh',
    props.options?.pagerSetting.show ? props.options?.datasetInfo.pageParam : undefined,
  );
};
const handlePageChange = (currentPage: number) => {
  if (props.options) {
    const options = props.options;
    options.datasetInfo.pageParam.pageNum = currentPage;
  }
  refresh();
};
const handlePageSizeChange = (pageSize: number) => {
  if (props.options) {
    const options = props.options;
    options.datasetInfo.pageParam.pageSize = pageSize;
  }
  refresh();
};
const sortChangeHandler = (sortEntity: ANY_OBJECT) => {
  let sortField = props.options?.datasetInfo.showColumnList.find(
    (x: ANY_OBJECT) => x.columnName === sortEntity.field,
  );
  if (props.options) {
    const options = props.options;
    options.datasetInfo.orderInfoList = [
      {
        id: sortField.columnId,
        columnId: sortField.columnId,
        showName: sortField.columnComment,
        orderType: sortEntity.order === 'desc' ? 2 : 1,
      },
    ];
  }
};

watch(
  () => props.data,
  () => {
    // buildTableInfo();
  },
  {
    immediate: true,
  },
);
watch(
  () => props.options,
  () => {
    // buildTableInfo();
  },
  {
    deep: true,
    immediate: true,
  },
);
</script>

<style scoped>
/*
  .dataview-table {
    display: flex;
    flex-direction: column;
  }
  */
.dataview-table .title {
  flex-shrink: 0;
  padding: 10px 2px;
  flex-grow: 0;
}

.dataview-table .table {
  flex-grow: 1;
  min-height: 100px;
}

.dataview-table :deep(.vxe-table--header-border-line) {
  display: none;
}

.dataview-table :deep(.vxe-cell--title) {
  width: 100%;
}

.dataview-table :deep(.vxe-cell--label) {
  width: 100%;
}

.dataview-table :deep(.vxe-table--render-default .vxe-table--footer-wrapper) {
  border: none;
}

.dataview-table :deep(.vxe-table--border-line) {
  border: none;
}

.dataview-table :deep(.el-pagination__sizes) {
  width: 150px;
}
.dataview-table :deep(.pagination) {
  margin-top: 10px;
}

.dataview-table :deep(.display-left) {
  text-align: left;
}

.dataview-table :deep(.display-center) {
  text-align: center;
}

.dataview-table :deep(.display-right) {
  text-align: right;
}

.dataview-table :deep(.vxe-body--row td:first-of-type) {
  border-left: none !important;
}

.dataview-table :deep(.vxe-table .vxe-header--row, .vxe-table th, .el-table th) {
  color: none !important;
}
</style>
