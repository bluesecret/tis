<template>
  <div class="table-container-wrapper" :table-key="keyName">
    <div class="active-widget-drag">
      <el-icon><Rank /></el-icon>
    </div>
    <div
      ref="myTableWrapper"
      class="widget-table-wrapper"
      @contextmenu="showContextMenu"
      :style="{ background: widget.props.backgroundColor }"
    >
      <template v-if="index.length > 0">
        <table ref="myTable" class="widget-table" cellspacing="0" cellpadding="0" border="0">
          <tr v-for="(rV, rK) in table.render" :key="rK">
            <td
              v-for="(cV, cI) in rV"
              :key="cI"
              class="table-container-cell"
              :class="{
                'select-td-class':
                  cV.rowIndex === current.rowIndex && cV.colIndex === current.colIndex && isEdit,
              }"
              :style="{
                border: '1px solid ' + widget.props.borderColor,
                backgroundColor: cV.color,
              }"
              :title="cV.index"
              :row="cV.rowIndex"
              :col="cV.colIndex"
              :rowspan="cV.rowSpan"
              :colspan="cV.colSpan"
              :align="cV.align"
              :valign="cV.valign"
              :width="cV.width"
              @click="setSource(cV)"
            >
              <div class="widget-td-wrapper">
                <template v-if="bindWidgetList(cV.rowIndex, cV.colIndex)">
                  <OnlineCustomBlock
                    class="cell-custom-block"
                    :isEdit="isEdit"
                    @dragAdd="onDragAdd"
                    v-model:value="
                      widget.childWidgetList[cV.rowIndex].childWidgetList[cV.colIndex]
                        .childWidgetList
                    "
                    @widgetClick="onWidgetClick"
                  />
                </template>
              </div>
            </td>
          </tr>
        </table>
      </template>
    </div>
    <div ref="tableMenu" class="menu" @click="hideContextMenu">
      <div style="line-height: 30px; cursor: pointer" class="menu__item" @mousedown="insertRows">
        向下插入行
      </div>
      <div style="line-height: 30px; cursor: pointer" class="menu__item" @mousedown="insertColumns">
        向右插入列
      </div>
      <div
        style="line-height: 30px; cursor: pointer"
        class="menu__item"
        v-if="allows.deleteRow"
        @mousedown="deleteRows"
      >
        删除行
      </div>
      <div
        style="line-height: 30px; cursor: pointer"
        class="menu__item"
        v-if="allows.deleteColumn"
        @mousedown="() => deleteColumns()"
      >
        删除列
      </div>
      <div
        style="line-height: 30px; cursor: pointer"
        class="menu__item"
        v-if="allows.mergeRightColumn"
        @mousedown="mergeRightColumn"
      >
        向右合并
      </div>
      <div
        style="line-height: 30px; cursor: pointer"
        class="menu__item"
        v-if="allows.mergeDownRow"
        @mousedown="mergeDownRow"
      >
        向下合并
      </div>
      <div
        style="line-height: 30px; cursor: pointer"
        class="menu__item"
        v-if="allows.splitCells"
        @mousedown="splitCells"
      >
        拆分单元格
      </div>
      <div
        style="line-height: 30px; cursor: pointer"
        class="menu__item"
        v-if="allows.mergeCells"
        @mousedown="mergeCells"
      >
        合并单元格
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Rank } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { ANY_OBJECT } from '@/types/generic';
import OnlineCustomBlock from '@/online/components/OnlineCustomBlock.vue';
import tableContainerConfig from '@/online/config/tableContainer';
import { WidgetEmit, WidgetProps } from './types/widget';
import { useWidget } from './hooks/widget';

type MouseTdNoneNull = [number, number];
type MouseTd = null | MouseTdNoneNull;

const emit = defineEmits<WidgetEmit>();
const props = withDefaults(defineProps<WidgetProps>(), {
  isEdit: false,
});

const form = inject('form', () => {
  console.error('OnlineCustomTableContainer: form not injected');
  return { isEdit: false } as ANY_OBJECT;
});

const { propsWidget, childWidgetList, onWidgetClick } = useWidget(props, emit);

const myTableWrapper = ref();
const myTable = ref();
const tableMenu = ref();
const table = ref<ANY_OBJECT>(propsWidget.value.props.table);
const index = ref<ANY_OBJECT[]>([]);
const current = reactive({
  rowIndex: 0,
  colIndex: 0,
});
const allows = reactive({
  deleteRow: true,
  deleteColumn: true,
  mergeDownRow: true,
  mergeRightColumn: true,
  mergeCells: true,
  splitCells: true,
});
const mouse = reactive({
  mouseDownHandler: null as number | null,
  isDrag: false,
  startTd: null as MouseTd,
  endTd: null as MouseTd,
});
const isDrag = ref(false);
const keyName = ref('table-container-');

const showContextMenu = (evt: MouseEvent) => {
  evt.preventDefault();

  if (!props.isEdit) return;
  const menu = tableMenu.value;

  menu.style.left = evt.clientX + 'px';
  menu.style.top = evt.clientY + 'px';
  menu.classList.add('active');

  if (window.document.body.clientHeight < menu.clientHeight + evt.clientY) {
    menu.style.top = window.document.body.clientHeight - menu.clientHeight - 10 + 'px';
  }
};
const onDragAdd = (e: ANY_OBJECT) => {
  console.log('tableContainer: drag add', e);
  let dragEle = e.list[e.dragEvent.newDraggableIndex];
  if (dragEle) dragEle.props.span = 24;
};

const getCell = (i: number, j: number) => {
  if (index.value.length <= i || index.value[i].length <= j) return null;
  return index.value[i][j];
};
const getRange = (start: MouseTdNoneNull, end: MouseTdNoneNull) => {
  const [rowStart, colStart] = start;
  const [rowEnd, colEnd] = end;

  let rowList = [];
  let colList = [];

  let cellStart = getCell(rowStart, colStart);
  let cellEnd = getCell(rowEnd, colEnd);

  let minRow = Math.min(
    cellStart.row,
    cellStart.row + cellStart.rowSpan - 1,
    cellEnd.row,
    cellEnd.row + cellEnd.rowSpan - 1,
  );
  let maxRow = Math.max(
    cellStart.row,
    cellStart.row + cellStart.rowSpan - 1,
    cellEnd.row,
    cellEnd.row + cellEnd.rowSpan - 1,
  );

  let minCol = Math.min(
    cellStart.col,
    cellStart.col + cellStart.colSpan - 1,
    cellEnd.col,
    cellEnd.col + cellEnd.colSpan - 1,
  );
  let maxCol = Math.max(
    cellStart.col,
    cellStart.col + cellStart.colSpan - 1,
    cellEnd.col,
    cellEnd.col + cellEnd.colSpan - 1,
  );

  for (let i = minRow; i <= maxRow; i++) {
    for (let j = minCol; j <= maxCol; j++) {
      let cell = getCell(i, j);
      if (!cell) continue;

      let start, end;
      start = cell.base ? cell.base.row : cell.row;
      end = cell.base ? cell.base.row + cell.base.rowSpan : cell.row + cell.rowSpan;

      for (let i = Math.min(start, end); i <= Math.max(start, end); i++) {
        rowList.push(i);
      }

      start = cell.base ? cell.base.col : cell.col;
      end = cell.base ? cell.base.col + cell.base.colSpan : cell.col + cell.colSpan;

      for (let j = Math.min(start, end); j <= Math.max(start, end); j++) {
        colList.push(j);
      }
    }
  }

  let rowMin = Math.min(...rowList);
  let rowMax = Math.max(...rowList);

  let colMin = Math.min(...colList);
  let colMax = Math.max(...colList);

  return { rowMin, rowMax, colMin, colMax };
};
const getSelectedRange = () => {
  console.log('getSelectedRange mouse', mouse);
  if (!mouse.startTd || !mouse.endTd) {
    return {
      rowMin: 0,
      colMin: 0,
      rowMax: 0,
      colMax: 0,
    };
  }
  let [rowStart, colStart] = mouse.startTd;
  let [rowEnd, colEnd] = mouse.endTd;

  return getRange([rowStart, colStart], [rowEnd, colEnd]);
};
const changeAllow = () => {
  allows.deleteRow = index.value.length > 1;
  allows.deleteColumn = (() => {
    let minCol = 9999;
    Object.values<ANY_OBJECT[]>(table.value.render).forEach(row => {
      minCol = Math.min(minCol, row.length);
    });
    return minCol > 1;
  })();
  allows.mergeDownRow = (() => {
    let cell = getCell(current.rowIndex, current.colIndex);
    return cell ? cell.row + cell.rowSpan < index.value.length : false;
  })();
  allows.mergeRightColumn = (() => {
    let cell = getCell(current.rowIndex, current.colIndex);
    if (index.value.length > 0 && cell) {
      return cell.col + cell.colSpan < index.value[0].length;
    } else {
      return false;
    }
  })();
  allows.splitCells = (() => {
    let cell = getCell(current.rowIndex, current.colIndex);
    return cell ? cell.rowSpan > 1 || cell.colSpan > 1 : false;
  })();
  allows.mergeCells = (() => {
    const { rowMin, rowMax, colMin, colMax } = getSelectedRange();
    let cell = getCell(rowMin, colMin);
    return rowMax - rowMin - cell.rowSpan > 0 || colMax - colMin - cell.colSpan > 0;
  })();
};
const hideContextMenu = () => {
  tableMenu.value.classList.remove('active');
};
const buildTable = () => {
  let temp: ANY_OBJECT[] = [];

  for (let i = 0; i < index.value.length; i++) {
    temp[i] = [];

    for (let j = 0; j < index.value[i].length; j++) {
      // let cell = index.value[i][j]
      if (index.value[i][j].base) {
        if (index.value[i][j].base.row === i && index.value[i][j].base.col === j) {
          temp[i].push({
            rowIndex: i,
            colIndex: j,
            rowSpan: index.value[i][j].rowSpan,
            colSpan: index.value[i][j].colSpan,
            align: index.value[i][j].align,
            valign: index.value[i][j].valign,
            width: index.value[i][j].width,
          });
          j = j + index.value[i][j].colSpan - 1;
        }
      } else {
        temp[i].push({
          rowIndex: i,
          colIndex: j,
          rowSpan: 1,
          colSpan: 1,
          align: index.value[i][j].align,
          valign: index.value[i][j].valign,
          width: index.value[i][j].width,
        });
      }
    }
  }
  table.value.render = temp;
  changeAllow();
  hideContextMenu();
};
const createWidgetList = (rowIndex: number, colIndex: number | null = null) => {
  let temp = form().getWidgetObject(tableContainerConfig);
  temp.showName = `cell_${rowIndex}${colIndex != null ? '_' + colIndex : ''}`;
  temp.variableName = `cell_${rowIndex}${colIndex != null ? '_' + colIndex : ''}`;
  temp['relation'] = undefined;
  temp['datasource'] = undefined;
  temp['column'] = undefined;

  return temp;
};
const createRowWidgetList = (rowIndex: number, colNum: number) => {
  const rowWidgetList = [];
  for (let j = 0; j < colNum; j++) {
    rowWidgetList.push(createWidgetList(rowIndex, j));
    rowWidgetList[j].childWidgetList = [];
  }
  return rowWidgetList;
};
const bindWidgetList = (rowIndex: number, colIndex: number) => {
  if (
    propsWidget.value.childWidgetList &&
    propsWidget.value.childWidgetList[rowIndex] &&
    propsWidget.value.childWidgetList[rowIndex].childWidgetList[colIndex]
  ) {
    return true;
  } else {
    return false;
  }
};
const buildChildWidgetList = (index: ANY_OBJECT[]) => {
  if (!propsWidget.value.childWidgetList || propsWidget.value.childWidgetList.length === 0) {
    let widgetList = [];

    for (let i = 0; i < index.length; i++) {
      const rowWidgetList = createWidgetList(i);
      for (let j = 0; j < index[i].length; j++) {
        rowWidgetList.childWidgetList.push(createWidgetList(i, j));
        rowWidgetList.childWidgetList[j].childWidgetList = [];
      }
      widgetList.push(rowWidgetList);
    }

    childWidgetList.value = widgetList;
  }
};
const buildIndex = (defaultRow: number, defaultCol: number) => {
  let temp = [];
  for (let i = 0; i < defaultRow; i++) {
    let row: ANY_OBJECT[] = [];
    let rowSpan = 1;
    let colSpan = 1;
    for (let j = 0; j < defaultCol; j++) {
      row.push({
        row: i,
        col: j,
        rowSpan: rowSpan,
        colSpan: colSpan,
        // width: wrapperWidth ? (wrapperWidth / defaultCol).toFixed(0) : null,
        width: null,
        align: 'left',
        valign: 'middle',
      });
    }

    temp.push(row);
  }
  if (temp && temp.length > 0) {
    buildChildWidgetList(temp);
  }

  index.value = temp;
};

const setSource = (cell: ANY_OBJECT) => {
  if (cell.rowIndex >= 0) {
    current.colIndex = cell.colIndex;
    current.rowIndex = cell.rowIndex;
  }
};
const setColumn = (colIndex: number, columnInfo: ANY_OBJECT) => {
  if (columnInfo == null) return;
  index.value = index.value.map(row => {
    let cell = row[colIndex];
    if (cell) {
      cell.width = columnInfo.width;
      if (columnInfo.align != null) cell.align = columnInfo.align;
      if (columnInfo.valign != null) cell.valign = columnInfo.valign;
    }
    return row;
  });
  buildTable();
};
const listToMap = (list: string[]) => {
  if (!list) return {};
  for (var i = 0, ci, obj: ANY_OBJECT = {}; (ci = list[i++]); ) {
    obj[ci.toUpperCase()] = obj[ci] = 1;
  }
  return obj;
};
const isBody = (node: ANY_OBJECT) => {
  return node && node.nodeType === 1 && node.tagName.toLowerCase() === 'body';
};
const findParent = (
  node: ANY_OBJECT,
  filterFn: (node: ANY_OBJECT) => boolean,
  includeSelf: boolean,
) => {
  if (node && !isBody(node)) {
    node = includeSelf ? node : node.parentNode;
    while (node) {
      if (!filterFn || filterFn(node) || isBody(node)) {
        return filterFn && !filterFn(node) && isBody(node) ? null : node;
      }
      node = node.parentNode;
    }
  }
  return null;
};
const findParentByTagName = (
  node: ANY_OBJECT,
  tagNames: string[] | string,
  includeSelf: boolean,
  excludeFn: ((node: ANY_OBJECT) => boolean) | null = null,
) => {
  const tagNameMap = listToMap(Array.isArray(tagNames) ? tagNames : [tagNames]);
  return findParent(
    node,
    function (node) {
      return tagNameMap[node.tagName] && !(excludeFn && excludeFn(node));
    },
    includeSelf,
  );
};
const clearSelected = (node: ANY_OBJECT) => {
  if (!node) return;
  let table = findParentByTagName(node, 'table', true);
  if (!table) {
    console.log('clearSelected,未找到table', node);
    return;
  }
  let classTds: ANY_OBJECT[] = Array.from(table.getElementsByClassName('select-td-class'));

  classTds.forEach(td => {
    td.classList.remove('select-td-class');
  });
};
const forIndex = (handler: (cell: ANY_OBJECT) => void) => {
  for (let i = 0; i < index.value.length; i++) {
    for (let j = 0; j < index.value[i].length; j++) {
      let cell = index.value[i][j];
      handler(cell);
    }
  }
};
const fixCellSpans = () => {
  let cells: ANY_OBJECT[] = [];

  forIndex(cell => {
    cells.push(cell);
  });
  // 所有合并的单元格
  let mergedCells = cells.filter(cell => !!cell.base);

  // 所有合并区域的首个单元格
  let mergedBaseCells: string[] = [];
  mergedCells.forEach(cell => {
    let cellName = `${cell.base.row}_${cell.base.col}`;
    if (mergedBaseCells.indexOf(cellName) === -1) {
      mergedBaseCells.push(cellName);
    }
  });

  mergedBaseCells.forEach(cellName => {
    let [row, col] = cellName.split('_');
    // 合并区域的首个单元格
    let cell = getCell(parseInt(row), parseInt(col));
    // 通过合并区首个单元格找到 合并区的所有单元格
    let spans = mergedCells.filter(
      cell => cell.base && cell.base.row === +row && cell.base.col === +col,
    );

    let maxCol = 0;
    let minCol = 9999;
    let maxRow = 0;
    let minRow = 9999;
    spans.forEach(item => {
      maxCol = item.col > maxCol ? item.col : maxCol;
      minCol = item.col < minCol ? item.col : minCol;

      maxRow = item.row > maxRow ? item.row : maxRow;
      minRow = item.row < minRow ? item.row : minRow;
    });

    cell.colSpan = maxCol - minCol + 1;
    cell.rowSpan = maxRow - minRow + 1;

    // 如果合并区只有一个单元格，把合并去
    if (spans.length === 1) {
      delete cell.base;
    }
  });
};
const deleteColumns = (idx: number) => {
  let colIndex = idx || current.colIndex;

  if (index.value.length > 0 && index.value[0].length === 1) {
    ElMessage.warning('最后一列无法删除');
    return;
  }

  if (index.value.length > 0 && index.value[0].length > colIndex) {
    for (let i = 0; i < index.value.length; i++) {
      // 循环每一行 获取要删除的单元格
      let cell = getCell(i, colIndex);

      // 如果要删除的单元格是合并单元格的首个，更改base引用
      if (Object.is(cell, cell.base) && cell.base.colSpan > 1) {
        for (let x = cell.row; x < cell.row + cell.rowSpan; x++) {
          for (let y = cell.col + 1; y < cell.col + cell.colSpan; y++) {
            let c = getCell(x, y);
            c.base = index.value[i][colIndex + 1];
          }
        }
      }
    }
  }

  for (let i = 0; i < index.value.length; i++) {
    index.value[i].splice(colIndex, 1);
    propsWidget.value.childWidgetList[i].childWidgetList.splice(colIndex, 1);
  }

  for (let i = 0; i < index.value.length; i++) {
    for (let j = colIndex; j < index.value[i].length; j++) {
      let cell = index.value[i][j];
      cell.col--;
    }
  }

  //clearSelected();
  mouse.startTd = null;
  mouse.endTd = null;
  fixCellSpans();
  buildTable();
};
const findMergeAreas = () => {
  let cells: ANY_OBJECT[] = [];
  for (let i = 0; i < index.value.length; i++) {
    for (let j = 0; j < index.value[i].length; j++) {
      let cell = index.value[i][j];
      cells.push(cell);
    }
  }
  let spanCells = cells.filter(cell => cell.colSpan > 1 || cell.rowSpan > 1);
  for (let cell of spanCells) {
    cell.range = [
      [cell.row, cell.col],
      [cell.row + cell.rowSpan - 1, cell.col + cell.colSpan - 1],
    ];
  }
  return spanCells;
};
const cellInfo = (cell: ANY_OBJECT, mergedArea: ANY_OBJECT[]) => {
  var isInArea = false; // 在合并区域内
  var isColBorder = false; // 在合并区域的最右列
  var isRowBorder = false; // 在合并区域的最下行
  var isMergeAreaFirstRow = false; // 是合并区域的首行
  var mergeCell;

  mergedArea.forEach(areaItem => {
    if (!isInArea) {
      // 判断是否在域内
      if (
        areaItem.range[0][0] <= cell.row &&
        cell.row <= areaItem.range[1][0] &&
        areaItem.range[0][1] <= cell.col &&
        cell.col <= areaItem.range[1][1]
      ) {
        isInArea = true;

        if (cell.col === areaItem.range[1][1]) {
          isColBorder = true;
        }
        if (cell.row === areaItem.range[1][0]) {
          isRowBorder = true;
        }

        if (cell.row === areaItem.row) {
          isMergeAreaFirstRow = true;
        }
        mergeCell = areaItem;
      }
    }
  });
  return {
    isInArea,
    isColBorder,
    isRowBorder,
    isMergeAreaFirstRow,
    mergeCell,
  };
};
const moveCellRight = (colIndex: number) => {
  // 变插入点后面所有的 坐标，和Base
  for (let i = 0; i < index.value.length; i++) {
    let row = index.value[i];

    for (let j = colIndex; j < row.length; j++) {
      let cell = row[j];
      cell.col++;
    }
  }
};
const moveCellDown = (rowIndex: number) => {
  for (let i = rowIndex; i < index.value.length; i++) {
    let row = index.value[i];

    for (let j = 0; j < row.length; j++) {
      let cell = row[j];
      cell.row++;
    }
  }
};
const insertColumns = () => {
  let insertIndex = current.colIndex;

  let mergedArea = findMergeAreas();

  // 如果选中的是合并单元格，把插入点移动到合并单元格的最下方
  const clickCell = getCell(current.rowIndex, current.colIndex);
  const clickCellInfo = cellInfo(clickCell, mergedArea);
  if (clickCellInfo.isInArea) {
    insertIndex = clickCell.col + clickCell.colSpan - 1;
  }

  mergedArea = findMergeAreas();
  for (let i = 0; i < index.value.length; i++) {
    let row = index.value[i];
    let cell = row[insertIndex];

    let cloneCell = {
      row: cell.row,
      col: cell.col,
      rowSpan: cell.rowSpan,
      colSpan: cell.colSpan,
      base: cell.base,
      valign: 'middle',
    };

    const cInfo = cellInfo(cloneCell, mergedArea);
    if (cInfo.isInArea && cInfo.isColBorder) {
      delete cloneCell.base;
    }
    cloneCell.col++;
    cloneCell.colSpan = 1;
    cloneCell.rowSpan = 1;

    let colWidgetList = createWidgetList(i, cloneCell.col);
    colWidgetList.childWidgetList = [];

    propsWidget.value.childWidgetList[i].childWidgetList.splice(insertIndex + 1, 0, colWidgetList);
    row.splice(insertIndex + 1, 0, cloneCell);
  }

  moveCellRight(insertIndex + 2);

  fixCellSpans();
  buildTable();
};
const deleteRows = () => {
  let rowIndex = current.rowIndex;

  if (index.value.length === 1) {
    ElMessage.warning('最后一行无法删除');
    return;
  }
  // 如果删除的不是最后一行，重定向base引用
  if (index.value.length > rowIndex) {
    for (let j = 0; j < index.value[rowIndex].length; j++) {
      let cell = getCell(rowIndex, j);

      // 如果要删除的单元格是合并单元格的首个，更改base引用
      if (Object.is(cell, cell.base) && cell.base.rowSpan > 1) {
        for (let x = cell.row + 1; x < cell.row + cell.rowSpan; x++) {
          for (let y = cell.col; y < cell.col + cell.colSpan; y++) {
            let c = getCell(x, y);
            c.base = index.value[rowIndex + 1][j];
          }
        }
      }
    }
  }

  index.value.splice(rowIndex, 1);
  propsWidget.value.childWidgetList.splice(rowIndex, 1);
  for (let i = rowIndex; i < index.value.length; i++) {
    let row = index.value[i];

    for (let j = 0; j < row.length; j++) {
      let cell = row[j];
      cell.row--;
    }
  }
  //clearSelected();

  mouse.startTd = null;
  mouse.endTd = null;
  //mouse.rowIndex--;
  fixCellSpans();
  buildTable();
};
const insertRows = () => {
  let insertIndex = current.rowIndex;

  let mergedArea = findMergeAreas();

  // 如果选中的是合并单元格，把插入点移动到合并单元格的最下方
  const clickCell = getCell(current.rowIndex, current.colIndex);
  const clickCellInfo = cellInfo(clickCell, mergedArea);
  if (clickCellInfo.isInArea) {
    insertIndex = clickCell.row + clickCell.rowSpan - 1;
  }

  let cloneRow = [];

  for (let j = 0; j < index.value[insertIndex].length; j++) {
    let cell = index.value[insertIndex][j];

    cloneRow.push({
      row: cell.row,
      col: cell.col,
      rowSpan: cell.rowSpan,
      colSpan: cell.colSpan,
      base: cell.base,
    });
  }

  // 创建行的组件
  let rowWidget = createWidgetList(insertIndex + 1);
  rowWidget.childWidgetList = createRowWidgetList(insertIndex + 1, index.value[insertIndex].length);

  propsWidget.value.childWidgetList.splice(insertIndex + 1, 0, rowWidget);
  index.value.splice(insertIndex + 1, 0, cloneRow);

  for (let j = 0; j < cloneRow.length; j++) {
    const cInfo = cellInfo(cloneRow[j], mergedArea);
    if (cInfo.isInArea && cInfo.isRowBorder) {
      delete cloneRow[j].base;
    }
    cloneRow[j].row++;

    cloneRow[j].colSpan = 1;
    cloneRow[j].rowSpan = 1;
  }

  moveCellDown(insertIndex + 2);
  fixCellSpans();

  buildTable();
};
const mergeRightColumn = () => {
  let cell = getCell(current.rowIndex, current.colIndex);
  let cellRight = getCell(cell.row, cell.col + cell.colSpan);
  if (!cellRight) return;

  let allowMerge = true;
  if (
    !!cellRight.base &&
    !cell.base /* 目标单元格合并 源单元格未合并 */ &&
    cellRight.base.rowSpan > 1 /* 目标单元格的横向合并数大于1 */
  ) {
    allowMerge = false;
  } else if (
    !cellRight.base &&
    !!cell.base /* 目标单元格未合并 源单元格合并 */ &&
    cell.base.rowSpan > 1 /* 源单元格的横向合并数大于1 */
  ) {
    allowMerge = false;
  } else if (
    !!cellRight.base &&
    !!cell.base /* 目标单元格和源都合并了 */ &&
    cell.base.row + cell.base.rowSpan !==
      cellRight.base.row + cellRight.base.rowSpan /* 合并位置不同 */
  ) {
    allowMerge = false;
  }

  if (!allowMerge) {
    ElMessage.warning('目标单元格数量不同，无法合并');
    return;
  }

  cell.base = cell;
  let widgetList: ANY_OBJECT[] = [];

  const relinkRight = (cell: ANY_OBJECT) => {
    if (cellRight.colSpan > 1) {
      let meragedCellNum = cellRight.colSpan;
      for (let i = cellRight.col; i < cellRight.col + meragedCellNum; i++) {
        let cRight = getCell(cellRight.row, i);
        cRight.base = cell;
        cRight.colSpan = 1;

        widgetList.push(
          ...propsWidget.value.childWidgetList[cRight.row].childWidgetList[
            i
          ].childWidgetList.splice(0),
        );
      }
    } else {
      cellRight.base = cell;
      widgetList.push(
        ...propsWidget.value.childWidgetList[cellRight.row].childWidgetList[
          cellRight.col
        ].childWidgetList.splice(0),
      );
    }

    propsWidget.value.childWidgetList[cell.row].childWidgetList[cell.col].childWidgetList.push(
      ...widgetList,
    );
  };
  relinkRight(cell);

  fixCellSpans();
  buildTable();
};
const mergeDownRow = () => {
  let cell = getCell(current.rowIndex, current.colIndex);
  let cellDown = getCell(cell.row + cell.rowSpan, cell.col);

  if (!cellDown) return;

  let allowMerge = true;
  if (
    !!cellDown.base &&
    !cell.base /* 目标单元格合并 源单元格未合并 */ &&
    cellDown.base.colSpan > 1 /* 目标单元格的横向合并数大于1 */
  ) {
    allowMerge = false;
  } else if (
    !cellDown.base &&
    !!cell.base /* 目标单元格未合并 源单元格合并 */ &&
    cell.base.colSpan > 1 /* 源单元格的横向合并数大于1 */
  ) {
    allowMerge = false;
  } else if (
    !!cellDown.base &&
    !!cell.base /* 目标单元格和源都合并了 */ &&
    cell.base.col + cell.base.colSpan !==
      cellDown.base.col + cellDown.base.colSpan /* 合并位置不同 */
  ) {
    allowMerge = false;
  }

  if (!allowMerge) {
    ElMessage.warning('目标单元格数量不同，无法合并');
    return;
  }

  cell.base = cell;
  let widgetList: ANY_OBJECT[] = [];
  const relinkBottom = (cell: ANY_OBJECT) => {
    if (cellDown.rowSpan > 1 || cellDown.colSpan > 1) {
      let meragedCellRowNum = cellDown.rowSpan;
      let meragedCellColNum = cellDown.colSpan;
      for (let i = cellDown.row; i < cellDown.row + meragedCellRowNum; i++) {
        for (let j = cellDown.col; j < cellDown.col + meragedCellColNum; j++) {
          let cRight = getCell(i, j);
          cRight.base = cell;
          cRight.rowSpan = 1;
          cRight.colSpan = 1;

          widgetList.push(
            ...propsWidget.value.childWidgetList[i].childWidgetList[j].childWidgetList.splice(0),
          );
        }
      }
    } else {
      cellDown.base = cell;
      widgetList.push(
        ...propsWidget.value.childWidgetList[cellDown.row].childWidgetList[
          cellDown.col
        ].childWidgetList.splice(0),
      );
    }
    propsWidget.value.childWidgetList[cell.row].childWidgetList[cell.col].childWidgetList.push(
      ...widgetList,
    );
  };
  relinkBottom(cell);

  fixCellSpans();
  buildTable();
};
const splitCells = () => {
  let cell = getCell(current.rowIndex, current.colIndex);

  if (!cell.base) return;

  let rowFrom = cell.base.row;
  let rowTo = cell.base.row + cell.base.rowSpan;
  let colFrom = cell.base.col;
  let colTo = cell.base.col + cell.base.colSpan;
  for (let i = rowFrom; i < rowTo; i++) {
    for (let j = colFrom; j < colTo; j++) {
      let c = getCell(i, j);
      c.rowSpan = 1;
      c.colSpan = 1;
      delete c.base;
      delete c.color;
    }
  }
  cell.rowSpan = 1;
  cell.colSpan = 1;
  fixCellSpans();
  buildTable();
};
const mergeCells = (evt: ANY_OBJECT) => {
  if (!evt.target) {
    console.error('mergeCells: target is null', evt);
    return;
  }
  const { rowMin, rowMax, colMin, colMax } = getSelectedRange();
  let widgetList = [];

  for (let i = rowMin; i < rowMax; i++) {
    for (let j = colMin; j < colMax; j++) {
      let c = getCell(i, j);
      if (c.base) {
        delete c.base;
      }
    }
  }

  const baseCell = getCell(rowMin, colMin);
  for (let i = rowMin; i < rowMax; i++) {
    for (let j = colMin; j < colMax; j++) {
      let c = getCell(i, j);
      c.base = baseCell;
      widgetList.push(
        ...propsWidget.value.childWidgetList[i].childWidgetList[j].childWidgetList.splice(0),
      );
    }
  }
  propsWidget.value.childWidgetList[baseCell.row].childWidgetList[
    baseCell.col
  ].childWidgetList.push(...widgetList);

  clearSelected(evt.target.parentNode.previousElementSibling.firstElementChild);

  mouse.startTd = [rowMin, colMin];
  mouse.endTd = mouse.startTd;
  setSelected(evt.target.parentNode.previousElementSibling.firstElementChild);
  fixCellSpans();
  buildTable();
};
const mountedActions = () => {
  // 挂载操作到属性面板
  propsWidget.value.props.actions = {};
  propsWidget.value.props.actions.indexData = index.value;
  propsWidget.value.props.actions.setSource = setSource;
  propsWidget.value.props.actions.setColumn = setColumn;
  propsWidget.value.props.actions.deleteColumns = deleteColumns;
  propsWidget.value.props.actions.insertColumns = insertColumns;
  propsWidget.value.props.actions.deleteRows = deleteRows;
  propsWidget.value.props.actions.insertRows = insertRows;
  propsWidget.value.props.actions.mergeRightColumn = mergeRightColumn;
  propsWidget.value.props.actions.mergeDownRow = mergeDownRow;
  propsWidget.value.props.actions.splitCells = splitCells;

  propsWidget.value.props.actions.current = () => {
    return current;
  };
};
const reIndex = () => {
  const maxCol = () => {
    let max = 0;
    Object.values<ANY_OBJECT[]>(table.value.render).forEach(row => {
      if (row.length > 0) {
        const lastCol = row[row.length - 1];
        const colNum = lastCol.colIndex + lastCol.colSpan;
        max = Math.max(max, colNum);
      }
    });
    return max;
  };
  const maxRow = () => {
    let max = 0;
    Object.keys(table.value.render).forEach(rowKey => {
      let row = table.value.render[rowKey];
      for (let i = 0; i < row.length; i++) {
        let rowNum = +rowKey + row[i].rowSpan;
        max = Math.max(max, rowNum);
      }
    });
    return max;
  };

  const mergeRange = (
    cellRef: ANY_OBJECT,
    {
      rowMin,
      rowMax,
      colMin,
      colMax,
    }: { rowMin: number; rowMax: number; colMin: number; colMax: number },
  ) => {
    for (let i = rowMin; i < rowMax; i++) {
      for (let j = colMin; j < colMax; j++) {
        let cell = getCell(i, j);
        // cell.rowSpan=1;
        // cell.colSpan=1;
        cell.base = cellRef;
      }
    }
  };
  buildIndex(maxRow(), maxCol());

  Object.keys(table.value.render).forEach(rowNum => {
    let row: ANY_OBJECT[] = table.value.render[rowNum];
    row.forEach(cell => {
      let indexCell = index.value[cell.rowIndex][cell.colIndex];
      indexCell.width = cell.width;
      indexCell.align = cell.align;
      indexCell.valign = cell.valign;

      if (cell.colSpan > 1 || cell.rowSpan > 1) {
        let indexCell = getCell(cell.rowIndex, cell.colIndex);
        indexCell.colSpan = cell.colSpan;
        indexCell.rowSpan = cell.rowSpan;
      }
    });
  });

  for (let i = 0; i < index.value.length; i++) {
    for (let j = 0; j < index.value[i].length; j++) {
      let cell = getCell(i, j);

      if (cell.colSpan > 1 || cell.rowSpan > 1) {
        let range = getRange(
          [cell.row, cell.col],
          [cell.row + cell.rowSpan - 1, cell.col + cell.colSpan - 1],
        );
        mergeRange(cell, range);
      }
    }
  }
};

const checkCellInRange = (row: number, col: number) => {
  if (mouse.startTd != null && mouse.endTd != null) {
    let minX = Math.min(mouse.startTd[0], mouse.endTd[0]);
    let maxX = Math.max(mouse.startTd[0], mouse.endTd[0]);
    let minY = Math.min(mouse.startTd[1], mouse.endTd[1]);
    let maxY = Math.max(mouse.startTd[1], mouse.endTd[1]);
    return row >= minX && row <= maxX && col >= minY && col <= maxY;
  } else {
    return false;
  }
};
const mouseClickOnWidget = (evt: ANY_OBJECT) => {
  console.log('mouseClickOnWidget', evt);
  if (!evt.target) {
    console.warn('mouseClickOnWidget target is null');
    return;
  }
  let target = evt.target as HTMLElement;
  let targetClassList = Array.from(target.classList);
  if (targetClassList.indexOf('table-container-cell') !== -1) {
    // 点击到单元格
    return false;
  } else if (targetClassList.indexOf('custom-block-draggable') !== -1) {
    // 点击到单元格里的Block组件，判断这个Block组件是否是单元格默认的Block组件。
    for (let i = 0; i < (evt.path || []).length; i++) {
      let tempNode = evt.path[0];
      let tempClassList = Array.from(tempNode.classList);
      if (tempClassList.indexOf('online-custom-block') !== -1) {
        // 到了Block组件根节点
        return tempClassList.indexOf('cell-custom-block') === -1;
      }
    }
    return false;
  } else {
    return true;
  }
};
const setSelected = (node: ANY_OBJECT) => {
  const { rowMin, rowMax, colMin, colMax } = getSelectedRange();

  let table = findParentByTagName(node, 'table', true);
  if (!table) {
    console.log('not found table', node);
    return;
  }
  let tds = Array.from<HTMLElement>(table.getElementsByTagName('td'));

  for (let i = rowMin; i < rowMax; i++) {
    for (let j = colMin; j < colMax; j++) {
      let td = tds.find(x => {
        return x.getAttribute('row') === i.toString() && x.getAttribute('col') === j.toString();
      });

      if (td) td.classList.add('select-td-class');
    }
  }
};
const mouseOverEvent = (evt: MouseEvent) => {
  if (!evt.target) {
    console.error('target is null', evt);
    return;
  }
  if (isDrag.value) {
    const target = findParentByTagName(evt.target, 'td', true);
    if (!target) {
      console.warn('not found table', evt.target);
      return;
    }

    let row = parseInt(target.getAttribute('row'));
    let col = parseInt(target.getAttribute('col'));

    clearSelected(evt.target);
    mouse.endTd = [row, col];

    setSelected(evt.target);
  }
};
const mouseDownEvent = (evt: MouseEvent) => {
  console.log('mouseDown', evt.target);
  if (!props.isEdit || !evt.target) return;
  let target = findParentByTagName(evt.target, 'td', true);

  if (!target) {
    console.error('not found td', evt.target);
    return;
  }

  let row = parseInt(target.getAttribute('row'));
  let col = parseInt(target.getAttribute('col'));

  let setCell = evt.button === 0 || (evt.button === 2 && !checkCellInRange(row, col));
  if (setCell) {
    // 设置选中cell为点击的cell
    evt.target && clearSelected(evt.target);
    current.rowIndex = row;
    current.colIndex = col;
    mouse.startTd = null;
    mouse.endTd = null;
    // 点击左键时关闭右键菜单
    hideContextMenu();
    changeAllow();
  } else if (evt.button === 2) {
    let table = findParentByTagName(evt.target, 'table', true);
    if (!table) {
      console.error('not found table', evt.target);
      return;
    }
    let tds: HTMLElement[] = Array.from(table.getElementsByTagName('td'));
    if (tds) {
      let td = tds.find(x => {
        return x.classList && Array.from(x.classList || []).indexOf('select-td-class') > -1;
      });

      if (!td) {
        clearSelected(evt.target);
        target.classList.add('select-td-class');
      }
    }
  }

  if (!mouseClickOnWidget(evt)) {
    // 屏蔽组件外的其他事件
    evt.preventDefault();

    isDrag.value = false;

    if (evt.button === 0) {
      clearSelected(evt.target);
      target.classList.add('select-td-class');

      mouse.startTd = [row, col];
      mouse.endTd = [row, col];

      setSelected(evt.target);
      if (mouse.mouseDownHandler) {
        clearTimeout(mouse.mouseDownHandler);
        mouse.mouseDownHandler = null;
      }
      mouse.mouseDownHandler = setTimeout(() => {
        isDrag.value = true;
        myTable.value.addEventListener('mouseover', mouseOverEvent);
      }, 50);
    }
  }
};

const mouseUpEvent = (evt: MouseEvent) => {
  if (!props.isEdit) return;
  if (evt.button === 0) {
    // const { rowMin, colMin } = getSelectedRange();
    mouse.isDrag = false;
    changeAllow();

    if (mouse.mouseDownHandler) {
      clearTimeout(mouse.mouseDownHandler);
    }
    myTable.value.removeEventListener('mouseover', mouseOverEvent);
  }
};

onBeforeMount(() => {
  mountedActions();
});
onMounted(() => {
  keyName.value = keyName.value + new Date().getTime();
  if (Object.values(table.value.render).length > 0) {
    // 根据保存的表格反向生成索引
    reIndex();
  } else if (index.value.length === 0) {
    // myTableWrapper.value.offsetWidth
    buildIndex(2, 3);
    buildTable();
  }

  nextTick(() => {
    myTable.value.addEventListener('mousedown', mouseDownEvent);
    myTable.value.addEventListener('mouseup', mouseUpEvent);
  });
});

// destoryTable
// forRange
// getRandomColor
// childWidgets

watch(
  index,
  () => {
    if (propsWidget.value.props.actions) {
      const widget = { ...propsWidget.value };
      widget.props.actions.indexData = index.value;
      propsWidget.value = widget;
    }
  },
  {
    deep: true,
    immediate: true,
  },
);
watch(
  () => props.widget.props.actions,
  (val: ANY_OBJECT) => {
    if (Object.keys(val).length === 0) {
      // 点击保存按钮后，属性面板会销毁，需要重新绑定actions
      mountedActions();
    }
  },
  {
    deep: true,
    immediate: true,
  },
);
</script>

<style lang="scss" scoped>
.table-container-wrapper {
  width: 100%;
  outline: none;
}
.widget-table-wrapper {
  position: relative;
}
:deep(.select-td-class) {
  background-color: rgb(13 188 121 / 20%);
}
.widget-item {
  .active-widget-drag {
    position: absolute;
    right: 34px;
    bottom: -1px;
    z-index: 1000;
    height: 20px;
    padding: 0 5px;
    font-size: 12px;
    color: #fff;
    background: #f70;
    border-radius: 2px 0 0;
    visibility: hidden;
    line-height: 20px;
    cursor: pointer;
  }
  &.active {
    .active-widget-drag {
      visibility: visible;
    }
  }
}
.widget-table {
  width: 100%;
  table-layout: fixed;
  border-collapse: collapse;
  tr,
  td {
    position: relative;
    overflow: hidden;
    min-width: 100px;
    height: 50px;
    border: solid 1px #eaedf4;
    user-select: none;
  }

  td.empty {
    // padding: 10px 20px;
    height: 150px;
    text-align: center;
  }
}
.widget-td-wrapper {
  width: 100%;
}
.menu,
.submenu {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 5000;
  width: 100px;
  padding: 5px;
  font-size: 14px;
  background: #fff;
  border: 1px solid #dadce0;
  visibility: hidden;
  user-select: none;
  box-sizing: border-box;
  .menu__item {
    position: relative;
    height: 30px;
    padding: 0 5px;
    text-align: left;
    line-height: 30px;
    cursor: pointer;
    .icon {
      float: right;
    }

    &:hover {
      background-color: #dadce0;
      .submenu {
        visibility: visible;
      }
    }

    .submenu {
      left: 85px;
    }
  }
}
.active {
  visibility: visible;
}
.info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center; /* // position: absolute; */
  width: 100%;
  height: 100%;
  text-align: center;
  vertical-align: middle;
  color: #999;
}
.info div {
  width: 80px;
  height: 80px;
  font-size: 60px;
  text-align: center;
  border: 1px dashed #d9dbdd;
  border-radius: 6px;
  line-height: 100px;
}
.info span {
  margin-top: 10px;
  font-size: 16px;
}
</style>

<style>
.widget-td-wrapper .custom-block-draggable {
  min-height: 50px !important;
}
.widget-td-wrapper .custom-block-draggable .info.mover {
  display: none;
}
</style>

<style scoped>
.table-container-cell {
  padding: 0 10px 10px;
}

.widget-table-wrapper:has(.el-form-item.is-error) .table-container-cell {
  padding: 10px;
}

.table-container-cell :deep(.el-form-item) {
  margin-top: 10px;
  margin-bottom: 0;
}

.widget-table-wrapper:has(.el-form-item.is-error) :deep(.el-form-item) {
  margin-top: 0;
  margin-bottom: 18px;
}
</style>
