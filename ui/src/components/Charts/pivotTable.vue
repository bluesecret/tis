<template>
  <el-row class="pivot-table" @click.stop="onTableClick" style="background: white">
    <el-col
      ref="title"
      class="title"
      v-if="title != null && title !== ''"
      :span="24"
      :style="getTitleStyle"
    >
      {{ title }}
    </el-col>
    <el-col class="table" :span="24" v-show="ready">
      <vxe-table
        ref="table"
        class="online-table"
        :data="tableDataList"
        :style="getTableStyle"
        border
        show-overflow="title"
        show-header-overflow="title"
        :row-config="rowConfig"
        :header-cell-style="getHeaderCellStyle"
        :cell-style="getCellStyle"
        :footer-cell-style="getCellStyle"
        :show-footer="showFooter"
        :max-height="tableHeight"
        :footer-method="getFooterDataList"
      >
        <pivot-table-column
          v-for="column in tableGroupInfo"
          :key="column.id"
          :columnInfo="column"
        />
        <template v-slot:empty>
          <div class="table-empty unified-font">
            <img src="@/assets/img/empty.png" />
            <span>暂无数据</span>
          </div>
        </template>
      </vxe-table>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { VxeTable } from 'vxe-table';
import { ANY_OBJECT } from '@/types/generic';
import PivotTableColumn from './pivotTableColumn.vue';
import { getColumnName, getRowDataByColumnName } from './utils';

const props = withDefaults(
  defineProps<{
    // 透视表数据
    data?: ANY_OBJECT[];
    // 透视表设置
    options?: ANY_OBJECT;
    // 透视表高度
    height?: number | string;
    // 行分组
    rowGroupColumnList?: ANY_OBJECT[];
    // 列分组
    columnGroupColumnList?: ANY_OBJECT[];
    // 指标列表
    valueColumnList?: ANY_OBJECT[];
  }>(),
  {
    data: () => [],
    rowGroupColumnList: () => [],
    columnGroupColumnList: () => [],
    valueColumnList: () => [],
  },
);

const table = ref();
const ready = ref(false);
let buildDataTimer: number | null = null;

// 透视表表头分组信息
const tableGroupInfo = ref<ANY_OBJECT[]>([]);
// 透视表表格数据
const tableDataList = ref<ANY_OBJECT[]>([]);
// 是否显示表尾行（支持行总计时显示）
const showFooter = ref(false);
// 行总计数据
const footerDataList = ref<(string | number | null)[]>([]);
// 表格高度
const tableHeight = ref<number | string>();
// 指标字段map
let valueColumnMap = new Map();

const title = computed(() => {
  if (!props.options || !props.options?.title || !props.options?.title.show) return undefined;
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
    'border-bottom': 'none',
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

const getFooterDataList = () => {
  return [footerDataList.value];
};
// 表头样式
const getHeaderCellStyle = ({ $columnIndex }: { $columnIndex: number }) => {
  return {
    height: props.options?.headerRowHeight + 'px',
    'border-bottom': '1px solid ' + props.options?.borderColor,
    'border-left': $columnIndex > 0 ? '1px solid ' + props.options?.borderColor : 'none',
    background: props.options?.headerCellBackgroundColor,
    color: props.options?.headerCellFontColor,
    'font-size': props.options?.headerCellFontSize + 'px',
    'text-align': props.options?.headerAlign,
  };
};
// 单元格样式（包括总计、行分组）
const getCellStyle = ({ $columnIndex }: { $columnIndex: number }) => {
  let style: ANY_OBJECT = {
    height: props.options?.cellRowHeight + 'px',
    padding: '0px',
    'border-bottom': '1px solid ' + props.options?.borderColor,
    'border-left': $columnIndex > 0 ? '1px solid ' + props.options?.borderColor : 'none',
    background:
      $columnIndex < props.rowGroupColumnList.length
        ? props.options?.rowGroupBackgroundColor
        : props.options?.cellBackgroundColor,
    color:
      $columnIndex < props.rowGroupColumnList.length
        ? props.options?.rowGroupFontColor
        : props.options?.cellFontColor,
    'font-size':
      ($columnIndex < props.rowGroupColumnList.length
        ? props.options?.rowGroupFontSize
        : props.options?.cellFontSize) + 'px',
    'text-align':
      $columnIndex < props.rowGroupColumnList.length
        ? props.options?.rowGroupAlign
        : props.options?.cellAlign,
    'font-weight': $columnIndex < props.rowGroupColumnList.length ? 700 : undefined,
  };
  return style;
};

const emit = defineEmits<{
  click: [];
}>();

const onTableClick = () => {
  emit('click');
};
/**
 * 计算行分组
 */
const buildRowGroupInfo = () => {
  let rowColumnGroup: ANY_OBJECT | null = null;
  let columnGroup: ANY_OBJECT | null = null;
  let tempTime = new Date().getTime();
  console.log('pivotTable buildRowGroupInfo', props.columnGroupColumnList);
  for (const item of props.columnGroupColumnList || []) {
    let temp: ANY_OBJECT = {
      id: 'rowGroup' + tempTime++,
      title: item.showName,
      isGroup: true,
      fixed: 'left',
    };
    if (rowColumnGroup == null) {
      rowColumnGroup = temp;
    } else if (columnGroup) {
      if (!Array.isArray(columnGroup.children)) columnGroup.children = [];
      columnGroup.children.push(temp);
    }
    columnGroup = temp;
  }

  let tempColumnList = props.rowGroupColumnList.map(item => {
    return {
      id: 'rowColumn' + tempTime++,
      title: item.showName,
      field: getColumnName(item.columnName),
      fixed: 'left',
      'min-width': item.columnWidth,
    };
  });

  if (columnGroup != null) {
    columnGroup.children = tempColumnList;
    rowColumnGroup && tableGroupInfo.value.push(rowColumnGroup);
  } else {
    tableGroupInfo.value = tempColumnList;
  }
  console.log('pivotTable buildRowGroupInfo tableGroupInfo', tableGroupInfo.value);
};
/**
 * 计算表格行数据唯一ID
 */
const buildRowDataID = (data: ANY_OBJECT): string | null => {
  let id: string | null = null;
  props.rowGroupColumnList.forEach(item => {
    let value = getRowDataByColumnName(data, item.columnName);
    if (value == null) return null;
    if (id == null) {
      id = value;
    } else {
      id = id + '__' + value;
    }
  });
  return id;
};
/**
 * 计算列分组唯一ID
 */
const buildColumnGroupID = (data: ANY_OBJECT): string | null => {
  let id: string | null = null;
  props.columnGroupColumnList.forEach(item => {
    let value = getRowDataByColumnName(data, item.columnName);
    if (value == null) return null;
    if (id == null) {
      id = value;
    } else {
      id = id + '__' + value;
    }
  });
  return id;
};
/**
 * 添加列分组
 */
const addColumnGroup = (data: ANY_OBJECT, id: string) => {
  let tempTime = new Date().getTime();
  // 父分组节点
  let parentNode: ANY_OBJECT = {
    children: tableGroupInfo.value,
  };
  for (const item of props.columnGroupColumnList || []) {
    // 当前分组节点
    let currentNode = null;
    if (!Array.isArray(parentNode.children)) parentNode.children = [];
    for (let i = 0; i < parentNode.children.length; i++) {
      if (parentNode.children[i].dataValue === getRowDataByColumnName(data, item.columnName)) {
        currentNode = parentNode.children[i];
        break;
      }
    }
    // 当前分组节点不存在，创建列分组节点
    if (currentNode == null) {
      currentNode = {
        id: id + 'columnGroup' + tempTime++,
        dataValue: getRowDataByColumnName(data, item.columnName),
        title: (getRowDataByColumnName(data, item.columnName) || '').toString(),
        children: [],
        isGroup: true,
      };

      parentNode.children.push(currentNode);
    }
    parentNode = currentNode;
  }
  // 列分组指标数据
  let tempColumnList = props.valueColumnList.map(item => {
    return {
      id: id + 'column' + tempTime++,
      title: item.name,
      field: id + '__' + getColumnName(item.columnName),
      'min-width': item.columnWidth,
    };
  });
  if (!Array.isArray(parentNode.children)) parentNode.children = [];
  parentNode.children.push(...tempColumnList);
};
/**
 * 计算列分组
 */
const buildColumnGroupInfo = () => {
  // 表格所有列名称
  let tableColumnNameList: string[] = [];
  // 总计数据
  let footerData: ANY_OBJECT = {};
  if (Array.isArray(props.data)) {
    // 所有表格行数据Map
    let allTableDataMap = new Map();
    // 列分组唯一值Set
    let columnGroupSet = new Set();

    if (props.options?.totalSetting.row.show) {
      // 支持行总计，添加总计数据以及列名称
      props.rowGroupColumnList.forEach(item => {
        let columnName = getColumnName(item.columnName);
        tableColumnNameList.push(columnName);
        footerData[columnName] = props.options?.totalSetting.row.showName;
      });
    }
    /**
     * 编辑所有数据，计算列分组同时计算透视表显示数据
     */
    props.data.forEach(data => {
      // 透视表表格行数据唯一ID
      let dataID = buildRowDataID(data);
      // 透视表列分组唯一ID
      let columnGroupId = buildColumnGroupID(data);
      if (dataID != null) {
        let rowData = allTableDataMap.get(dataID);
        if (rowData == null) {
          // 透视表行显示数据不存在，创建行显示数据
          // 添加行分组列数据
          rowData = props.rowGroupColumnList.reduce((retObj, item) => {
            let columnName = getColumnName(item.columnName);
            retObj[columnName] = getRowDataByColumnName(data, item.columnName);
            return retObj;
          }, {});
          allTableDataMap.set(dataID, rowData);
          tableDataList.value.push(rowData);
        }
        // 列分组数据不存在，创建列分组数据
        if (columnGroupId && !columnGroupSet.has(columnGroupId)) {
          addColumnGroup(data, columnGroupId);
          columnGroupSet.add(columnGroupId);
        }
        // 添加列指标数据
        (props.valueColumnList || []).forEach(item => {
          let value = getRowDataByColumnName(data, item.columnName);
          let columnName = columnGroupId + '__' + getColumnName(item.columnName);
          valueColumnMap.set(columnName, item);
          rowData[columnName] = value;

          if (props.options?.totalSetting.row.show) {
            // 显示行总计的情况下，添加总计列名，并初始化数据为0
            if (tableColumnNameList.indexOf(columnName) === -1) {
              tableColumnNameList.push(columnName);
              footerData[columnName] = 0;
            }
            // 累计行总计数据
            if (value != null) footerData[columnName] += value;
          }

          if (props.options?.totalSetting.col.show && value != null) {
            /**
             * 支持列总计，那么需要给行总计添加列总计的统计信息和数据
             */
            let totalColumnName = '__total__data__' + getColumnName(item.columnName);
            if (props.options?.totalSetting.row.show) {
              if (footerData[totalColumnName] == null) {
                footerData[totalColumnName] = 0;
              }
              if (value != null) footerData[totalColumnName] += value;
            }
            // 列总计数据统计
            if (rowData[totalColumnName] == null) {
              rowData[totalColumnName] = value;
            } else {
              if (value != null) rowData[totalColumnName] += value;
            }
          }
        });
      }
    });
    // 支持列总计，添加列总计字段
    if (props.options?.totalSetting.col.show) {
      (props.valueColumnList || []).forEach(item => {
        let totalColumnName = '__total__data__' + getColumnName(item.columnName);
        valueColumnMap.set(totalColumnName, item);
        tableColumnNameList.push(totalColumnName);
      });
    }

    allTableDataMap.clear();
    columnGroupSet.clear();
  }
  // 计算出最终列总计数据（表尾数据）
  tableColumnNameList.forEach(keyName => {
    footerDataList.value.push(footerData[keyName]);
  });
};
/**
 * 合并单元格
 */
const buildMergeItems = () => {
  // 每一列的分组数据
  let rowGroupData: ANY_OBJECT | null = null;
  // 每一列分组开始位置
  let startPos: number[] = [];
  // 最终合并单元格信息
  let mergeCells: ANY_OBJECT[] = [];
  // 所有需要合并单元格的列名
  let rowColumnNameList: string[] = props.rowGroupColumnList.map(item => {
    return item.columnName;
  });
  /**
   * 遍历所有透视表数据，计算合并单元格信息
   */
  if (Array.isArray(tableDataList.value)) {
    for (let index = 0; index < tableDataList.value.length; index++) {
      let data = tableDataList.value[index];
      if (rowGroupData == null) {
        // 初始化分组数据和起始位置
        rowGroupData = [];
        startPos = [];
        for (const columnName of rowColumnNameList) {
          rowGroupData.push(getRowDataByColumnName(data, columnName));
          startPos.push(0);
        }
      } else {
        // 这一行数据是否合并过
        let merged = false;
        for (let columnIndex = 0; columnIndex < rowColumnNameList.length; columnIndex++) {
          let columnName = rowColumnNameList[columnIndex];
          // 合并起始行坐标
          let pos = 0;
          // 合并行数
          let rowspan = 0;

          if (rowGroupData[columnIndex] !== getRowDataByColumnName(data, columnName)) {
            /**
             * 行数据改变，需要合并单元格
             */
            // 取出起始位置坐标
            pos = startPos[columnIndex];
            // 计算合并行数
            rowspan = index - pos;
            // 重新赋值起始坐标和行数据，并且设置行合并标记为true
            rowGroupData[columnIndex] = getRowDataByColumnName(data, columnName);
            startPos[columnIndex] = index;
            merged = true;
          } else if (merged) {
            // 这一行之前的列合并过，所以后面的列也需要合并
            pos = startPos[columnIndex];
            rowspan = index - pos;
            rowGroupData[columnIndex] = getRowDataByColumnName(data, columnName);
            startPos[columnIndex] = index;
          } else if (index === tableDataList.value.length - 1) {
            // 到达表格最后一行数据，合并单元格
            pos = startPos[columnIndex];
            rowspan = index - pos + 1;
            rowGroupData[columnIndex] = getRowDataByColumnName(data, columnName);
            startPos[columnIndex] = index;
          }
          // 只有合并行数大于1的才需要合并
          if (rowspan > 1) {
            mergeCells.push({
              row: pos,
              col: columnIndex,
              rowspan: rowspan,
              colspan: 1,
            });
          }
        }
      }
    }
  }
  /**
   * 设置合并信息
   * 注意：必须放到nextTick里，不然会不生效
   */
  nextTick(() => {
    if (table.value) {
      table.value.setMergeCells(mergeCells);
    }
  });
};
/**
 * 总计数据设置
 */
const buildTotalInfo = () => {
  // 支持列总计，添加列总计分组信息
  if (props.options?.totalSetting.col.show) {
    tableGroupInfo.value.push({
      id: '__total__data__',
      title: props.options?.totalSetting.col.showName,
      fixed: 'right',
      children: props.valueColumnList.map(item => {
        let totalColumnName = '__total__data__' + getColumnName(item.columnName);
        return {
          id: totalColumnName,
          title: item.name,
          field: totalColumnName,
          fixed: 'right',
          'min-width': item.columnWidth,
        };
      }),
      isGroup: true,
    });
  }
  // 设置行总计单元格合并
  if (props.options?.totalSetting.row.show) {
    nextTick(() => {
      if (table.value) {
        table.value.setMergeFooterItems([
          {
            row: 0,
            col: 0,
            rowspan: 1,
            colspan: props.rowGroupColumnList.length,
          },
        ]);
      }
    });
  }
};
// 计算表格高度
const calcTableHeight = () => {
  tableHeight.value = props.height;
};
// 格式化指标显示
const formatValueCell = () => {
  let columnNameList: string[] = [];
  function buildColumnName(groupInfo: ANY_OBJECT) {
    if (!groupInfo.isGroup && groupInfo.field) columnNameList.push(groupInfo.field);
    if (groupInfo.isGroup && Array.isArray(groupInfo.children)) {
      groupInfo.children.forEach(subGroupInfo => {
        buildColumnName(subGroupInfo);
      });
    }
  }
  if (Array.isArray(tableGroupInfo.value)) {
    tableGroupInfo.value.forEach(groupInfo => {
      buildColumnName(groupInfo);
    });
  }
  columnNameList.forEach((columnName, index) => {
    let temp = valueColumnMap.get(columnName);
    if (temp && temp.fixed != null) {
      tableDataList.value.forEach(data => {
        data[columnName] = data[columnName].toFixed(temp.fixed);
      });
      if (showFooter.value && Array.isArray(footerDataList.value)) {
        //footerDataList.value[index] = footerDataList.value[index].toFixed(temp.fixed);
        let val = footerDataList.value[index];
        footerDataList.value[index] =
          typeof val == 'string' ? val : !val ? val : val.toFixed(temp.fixed);
      }
    }
  });

  console.log('pivotTable data', props.data, tableDataList.value);
};
// 构建透视表渲染信息
const buildTableInfo = () => {
  console.log('pivotTable buildTableInfo >>>');
  if (buildDataTimer != null) clearTimeout(buildDataTimer);
  ready.value = false;
  buildDataTimer = setTimeout(() => {
    tableGroupInfo.value = [];
    tableDataList.value = [];
    footerDataList.value = [];
    valueColumnMap = new Map();
    showFooter.value = false;
    buildRowGroupInfo();
    buildColumnGroupInfo();
    buildTotalInfo();
    buildMergeItems();
    buildDataTimer = null;
    showFooter.value = props.options?.totalSetting.row.show;
    formatValueCell();
    calcTableHeight();
    ready.value = true;
    valueColumnMap.clear();

    console.log('pivotTable tableDataList', tableDataList.value);
    console.log('pivotTable tableGroupInfo', tableGroupInfo.value);
  }, 50);
};

watch(
  () => props.rowGroupColumnList,
  () => {
    buildTableInfo();
  },
  { immediate: true },
);
watch(
  () => props.columnGroupColumnList,
  () => {
    buildTableInfo();
  },
  { immediate: true },
);
watch(
  () => props.valueColumnList,
  () => {
    buildTableInfo();
  },
  { immediate: true },
);
watch(
  () => props.data,
  () => {
    buildTableInfo();
  },
  { immediate: true },
);
watch(
  () => props.options,
  () => {
    buildTableInfo();
  },
  { deep: true, immediate: true },
);
watch(
  () => props.height,
  () => {
    calcTableHeight();
  },
  { immediate: true },
);
</script>

<style scoped>
/*
  .pivot-table {
    display: flex;
    flex-direction: column;
  }
  */
.pivot-table .title {
  flex-shrink: 0;
  padding: 10px 2px;
  flex-grow: 0;
}

.pivot-table .table {
  flex-grow: 1;
  min-height: 100px;
}

.pivot-table :deep(.vxe-table--header-border-line) {
  display: none;
}

.pivot-table :deep(.vxe-cell--title) {
  width: 100%;
}

.pivot-table :deep(.vxe-cell--label) {
  width: 100%;
}

.pivot-table :deep(.vxe-table--render-default .vxe-table--footer-wrapper) {
  border: none;
}

.pivot-table :deep(.vxe-table--render-default .vxe-body--x-space) {
  margin-top: 1px;
}

.pivot-table :deep(.vxe-table--border-line) {
  border: none;
}
</style>
