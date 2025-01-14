<template>
  <el-row type="flex" class="print-template-edit">
    <!-- 左侧菜单 -->
    <left-menu></left-menu>
    <left-panel
      :xs="xs"
      ref="refLeftPanel"
      :printInfo="printInfo"
      :datasetList="datasetList"
      :datasetGroupList="datasetGroupList"
      :datasetRelationList="datasetRelationList"
    ></left-panel>
    <div class="left-box" id="left-box-container">
      <div class="x-spread-sheet-container">
        <div id="x-spread-sheet" />
      </div>
      <div class="x-spread-sheet-footer">
        <el-button @click="addRow" size="default">添加</el-button>
        <el-input
          style="width: 60px; margin-left: 10px"
          size="default"
          v-model="rowNum"
          placeholder=""
        ></el-input>
        <span>行</span><span class="x-spread-sheet-footer-tip">(在底添加)</span>
      </div>
    </div>
    <el-row class="edit-box">
      <div class="attribute-panel">
        <el-tabs modelValue="cell">
          <el-tab-pane label="单元格" name="cell">
            <template #label>
              <span>单元格</span>
            </template>
            <el-col :span="24" style="padding: 0px 16px">
              <!-- 单元格设置 -->
              <el-form
                class="chart-attribute"
                label-position="top"
                label-width="70px"
                size="default"
                @submit.prevent
              >
                <el-scrollbar :style="{ height: 'calc(100vh - 132px)' }" class="custom-scroll">
                  <el-form-item
                    class="view-attribute-item mt-10"
                    label="所属段落"
                    style="padding-top: 16px"
                  >
                    <el-input :value="(cellFragment || {}).showName" readonly />
                  </el-form-item>
                  <el-form-item class="view-attribute-item" label="单元格类型">
                    <el-select
                      v-model="currentCell.cellType"
                      style="width: 100%"
                      @change="onCellTypeChange"
                    >
                      <el-option
                        v-for="item in PrintCellType.getList()"
                        :key="item.id"
                        :value="item.id"
                        :label="item.name"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item
                    v-if="currentCell.cellType === PrintCellType.IMAGE"
                    class="view-attribute-item"
                    label="图片来源"
                  >
                    <el-select
                      v-model="currentCell.imageSourceType"
                      placeholder=""
                      style="width: 100%"
                    >
                      <el-option
                        v-for="item in PrintCellImageSourceType.getList()"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item
                    v-if="currentCell.cellType === PrintCellType.BARCODE"
                    class="view-attribute-item"
                    label="条形码类型"
                  >
                    <el-select
                      v-model="currentCell.barCodeType"
                      placeholder=""
                      style="width: 100%"
                      @change="onCellValueChange(currentCell.columnName)"
                    >
                      <el-option
                        v-for="item in PrintBarCodeType.getList()"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item
                    v-if="currentCell.cellType === PrintCellType.BARCODE"
                    class="view-attribute-item"
                    label="显示文字"
                  >
                    <el-switch
                      v-model="currentCell.barCodeShowWords"
                      @change="onCellValueChange(currentCell.columnName)"
                    />
                  </el-form-item>
                  <el-form-item
                    v-if="currentCell.cellType === PrintCellType.QRCODE"
                    class="view-attribute-item"
                    label="二维码类型"
                  >
                    <el-select
                      v-model="currentCell.barCodeType"
                      placeholder=""
                      style="width: 100%"
                      @change="onCellValueChange(currentCell.columnName)"
                    >
                      <el-option
                        v-for="item in PrintQRCodeType.getList()"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item
                    v-if="
                      currentCell.cellType === PrintCellType.COLUMN ||
                      currentCell.cellType === PrintCellType.BARCODE ||
                      currentCell.cellType === PrintCellType.QRCODE ||
                      (currentCell.cellType === PrintCellType.IMAGE &&
                        currentCell.imageSourceType === PrintCellImageSourceType.COLUMN)
                    "
                    class="view-attribute-item"
                    label="单元格数据集"
                  >
                    <el-select
                      v-model="currentCell.bindDataset"
                      placeholder=""
                      style="width: 100%"
                      @change="onCellDatasetChange"
                    >
                      <el-option
                        v-for="item in cellBindDatasetList"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                      >
                        <el-row type="flex" justify="space-between" align="middle">
                          <span>{{ item.name }}</span>
                          <el-tag
                            size="default"
                            :type="item.id === cellFragment.datasetId ? 'success' : 'primary'"
                          >
                            {{
                              item.id === cellFragment.datasetId
                                ? '主数据集'
                                : ReportRelationType.getValue(item.relationType)
                            }}
                          </el-tag>
                        </el-row>
                      </el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item
                    v-if="
                      currentCell.cellType === PrintCellType.COLUMN ||
                      currentCell.cellType === PrintCellType.BARCODE ||
                      currentCell.cellType === PrintCellType.QRCODE ||
                      (currentCell.cellType === PrintCellType.IMAGE &&
                        currentCell.imageSourceType === PrintCellImageSourceType.COLUMN)
                    "
                    class="view-attribute-item"
                    label="单元格字段"
                  >
                    <el-select
                      v-if="(cellFragmentDataset || {}).datasetType !== DatasetType.API"
                      v-model="currentCell.columnName"
                      placeholder=""
                      style="width: 100%"
                      :popper-options="{ placement: 'bottom-end' }"
                      clearable
                      @change="onCellValueChange"
                    >
                      <el-option
                        v-for="item in cellBindColumnList"
                        :key="item.id"
                        :label="item.name"
                        :value="item.id"
                      >
                        <el-row type="flex" justify="space-between" align="middle">
                          <span style="flex-grow: 0; flex-shrink: 0; min-width: 100px">{{
                            item.name
                          }}</span>
                          <span
                            style="
                              overflow: hidden;
                              max-width: 300px;
                              margin-left: 10px;
                              text-overflow: ellipsis;
                              white-space: nowrap;
                            "
                            :title="item.columnComment"
                          >
                            {{ item.columnComment }}
                          </span>
                        </el-row>
                      </el-option>
                    </el-select>
                    <el-cascader
                      v-else
                      v-model="currentCell.columnName"
                      style="width: 100%"
                      clearable
                      placeholder=""
                      :options="getCellColumnTree"
                      :props="{
                        value: 'id',
                        label: 'name',
                      }"
                      @change="onCellValueChange"
                    />
                  </el-form-item>
                </el-scrollbar>
              </el-form>
            </el-col>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-row>
  </el-row>
</template>

<script setup lang="ts">
import { ReportDatasetGroupController } from '@/api/report';
import { treeDataTranslate, findItemFromList, findTreeNode } from '@/common/utils';
import { DictionaryController } from '@/api/system';
import { ANY_OBJECT } from '@/types/generic';
import {
  DatasetType,
  PrintBarCodeType,
  PrintCellImageSourceType,
  PrintCellType,
  PrintQRCodeType,
  ReportRelationType,
} from '@/common/staticDict/report';
import LeftMenu from './leftMenu.vue';
import leftPanel from './leftPanel.vue';
import SpreadSheet from './SpreadSheet';
import { buildColumnTree, loadDatasetColumnList } from './utils';

const FRAGMENT_COLOR = [
  'rgba(83,109,186,0.5)',
  'rgba(165,200,148,0.5)',
  'rgba(250,200,88,0.5)',
  'rgba(238,102,102,0.5)',
  'rgba(115,192,222,0.5)',
  'rgba(59,162,114,0.5)',
  'rgba(252,132,82,0.5)',
  'rgba(154,96,180,0.5)',
  'rgba(234,124,204,0.5)',
];
const props = defineProps<{ printInfo: ANY_OBJECT }>();

const refLeftPanel = ref();

const xs = ref<ANY_OBJECT>({});
// 当前选中的单元格
const currentCell = ref<ANY_OBJECT>({
  cellType: undefined,
  position: undefined,
  cell: undefined,
  value: undefined,
  datasetId: undefined,
  relationId: undefined,
  bindDataset: undefined,
  imageSourceType: PrintCellImageSourceType.COLUMN,
});

const datasetList = ref<ANY_OBJECT[]>([]);
const datasetGroupList = ref<ANY_OBJECT[]>([]);
const datasetRelationList = ref<ANY_OBJECT[]>([]);
const dictList = ref<ANY_OBJECT[]>([]);
const rowNum = ref(100);

let currentFragment: ANY_OBJECT | null = null;

/**
 * 根据选择的数据集分组，计算可用的数据集
 */
const getValidDatasetList = computed(() => {
  if (
    currentFragment == null ||
    !Array.isArray(currentFragment.bindGroupPath) ||
    currentFragment.bindGroupPath.length <= 0
  )
    return [];
  let selectGroupId = currentFragment.bindGroupPath[currentFragment.bindGroupPath.length - 1];
  return (datasetList.value || []).filter((item: ANY_OBJECT) => {
    return item.groupId === selectGroupId && item.datasetType !== DatasetType.API;
  });
});
/**
 * 获取当前段落可绑定的数据（非循环段落只能绑定到数据集，循环段落可以绑定到数据集以及一对多关联上）
 */
const getDatasetRelation = computed(() => {
  if (getValidDatasetList.value.length <= 0) return [];
  if (!currentFragment?.loop) {
    // 不循环迭代的段落，只能绑定数据集
    return getValidDatasetList.value;
  } else {
    // 循环迭代的段落，可以绑定数据集或者数据集下一对多关联
    let datasetIdSet = new Set();
    getValidDatasetList.value.forEach((item: ANY_OBJECT) => {
      datasetIdSet.add(item.datasetId);
    });
    return getValidDatasetList.value.concat(
      datasetRelationList.value.filter((item: ANY_OBJECT) => {
        item.parentId = item.masterDatasetId;
        return (
          datasetIdSet.has(item.masterDatasetId) &&
          item.relationType === ReportRelationType.ONE_TO_MANY
        );
      }),
    );
  }
});

/**
 * 通过行号查找行属于哪个段落
 */
const findFragment = (rowNum: number) => {
  let fragmentInfo = null;
  if (Array.isArray(props.printInfo.fragmentJson) && props.printInfo.fragmentJson.length > 0) {
    for (let i = 0; i < props.printInfo.fragmentJson.length; i++) {
      let fragment = props.printInfo.fragmentJson[i];
      if (
        fragment &&
        rowNum >= Number.parseInt(fragment.startRow) - 1 &&
        rowNum <= Number.parseInt(fragment.startRow) + Number.parseInt(fragment.rowSpan) - 2
      ) {
        fragmentInfo = {
          index: i,
          fragment: fragment,
        };
        break;
      }
    }
  }
  return fragmentInfo;
};
/**
 * 当前选中单元格所属段落
 */
const cellFragment = computed(() => {
  if (currentCell.value.position && Array.isArray(props.printInfo.fragmentJson)) {
    let fragmentInfo = findFragment(currentCell.value.position.r);
    return fragmentInfo ? fragmentInfo.fragment : null;
  }

  return null;
});
/**
 * 获取cell选中段落绑定的数据集
 */
const cellFragmentDataset = computed(() => {
  if (cellFragment.value == null) return null;
  return findItemFromList(datasetList.value, cellFragment.value.datasetId, 'datasetId');
});
/**
 * 获取当前选中单元格可以绑定的数据集
 */
const cellBindDatasetList = computed(() => {
  if (cellFragment.value == null) return null;
  let list: ANY_OBJECT[] = [];
  if (cellFragment.value.loop && cellFragment.value.relationId != null) {
    let relation = findItemFromList(datasetRelationList.value, cellFragment.value.relationId, 'id');
    if (relation != null) list.push(relation);
  } else {
    let dataset = findItemFromList(datasetList.value, cellFragment.value.datasetId, 'id');
    if (dataset != null) {
      // 添加主数据集
      list.push(dataset);
      // 添加一对一关联数据集
      list.push(
        ...datasetRelationList.value.filter((item: ANY_OBJECT) => {
          return (
            item.masterDatasetId === cellFragment.value.datasetId &&
            item.relationType === ReportRelationType.ONE_TO_ONE
          );
        }),
      );
    }
  }

  return list;
});
/**
 * 获取当前单元格可以绑定的字段列表
 */
const cellBindColumnNode = computed(() => {
  if (!currentCell.value || !cellFragment.value) return null;
  if (currentCell.value.bindDataset !== cellFragment.value.datasetId) {
    return findItemFromList(datasetRelationList.value, currentCell.value.bindDataset, 'id');
  } else {
    return findItemFromList(datasetList.value, currentCell.value.bindDataset, 'id');
  }
});
const cellBindColumnList = computed(() => {
  if (!cellFragmentDataset.value) return [];
  let columnList = (cellBindColumnNode.value || {}).columnList || [];
  if (cellFragmentDataset.value.datasetType === DatasetType.API) {
    // API数据集
    if (
      currentCell.value.cellType === PrintCellType.IMAGE &&
      currentCell.value.imageSourceType === PrintCellImageSourceType.COLUMN
    ) {
      // 仅仅返回图片类型字段
      return columnList.map((item: ANY_OBJECT) => {
        return {
          ...item,
          disabled: item.fieldType !== 'Array' && item.fieldType !== 'Object' && !item.image,
        };
      });
    } else {
      return columnList;
    }
  } else {
    if (
      currentCell.value.cellType === PrintCellType.IMAGE &&
      currentCell.value.imageSourceType === PrintCellImageSourceType.COLUMN
    ) {
      // 仅仅返回图片类型字段
      return (columnList || []).filter((item: ANY_OBJECT) => {
        return item.image;
      });
    } else {
      return columnList;
    }
  }
});
const getCellColumnTree = computed(() => {
  if (cellFragment.value == null) return [];
  let tree = treeDataTranslate(
    cellBindColumnList.value.map((item: ANY_OBJECT) => {
      return {
        ...item,
      };
    }),
    'columnId',
    'parentId',
  );
  if (cellFragment.value.loop) {
    let id = cellFragment.value.bindColumnPath[cellFragment.value.bindColumnPath.length - 1];
    let node = findTreeNode(tree, id, 'columnName', 'children');
    return node ? [node] : [];
  } else {
    let tempList = buildColumnTree(cellBindColumnList.value, 'Object', true);
    return tempList;
  }
});

const addRow = () => {
  xs.value.addRow(null, rowNum.value);
};
// 检测是否可以保存
const checkValidate = () => {
  return refLeftPanel.value.checkValidate();
};
const findCellBindColumnPath = (val: string[] | string) => {
  if (!Array.isArray(val)) {
    return findItemFromList(cellBindColumnList.value, val, 'id');
  } else {
    let tempColumnPath = [];
    let bFind = true;
    let tempList = getCellColumnTree.value;
    for (let i = 0; i < val.length; i++) {
      let id = val[i];
      let column = findItemFromList(tempList, id, 'id');
      if (column == null) {
        bFind = false;
        return;
      }
      tempColumnPath.push(column);
      tempList = column.children;
    }
    return bFind ? tempColumnPath : null;
  }
};
/**
 * 单元格绑定数据改变
 */
const onCellValueChange = (val: string | undefined = undefined) => {
  if (currentCell.value && currentCell.value.position && val) {
    let cellValue: string | undefined = val;
    let columnPath = findCellBindColumnPath(val);
    let column = Array.isArray(columnPath) ? columnPath[columnPath.length - 1] : columnPath;
    if (
      currentCell.value.cellType === PrintCellType.COLUMN ||
      currentCell.value.cellType === PrintCellType.BARCODE ||
      currentCell.value.cellType === PrintCellType.QRCODE
    ) {
      if (currentCell.value.columnName == null) {
        cellValue = undefined;
      } else {
        cellValue = JSON.stringify({
          cellType: currentCell.value.cellType,
          value: val,
          datasetId: (cellFragment.value || {}).datasetId,
          relationId:
            (cellFragmentDataset.value || {}).datasetType !== DatasetType.API
              ? currentCell.value.relationId
              : undefined,
          columnName: currentCell.value.columnName,
          fieldType: column ? column.fieldType : undefined,
          barCodeType:
            currentCell.value.cellType === PrintCellType.BARCODE ||
            currentCell.value.cellType === PrintCellType.QRCODE
              ? currentCell.value.barCodeType
              : undefined,
          barCodeShowWords:
            currentCell.value.cellType === PrintCellType.BARCODE
              ? currentCell.value.barCodeShowWords
              : undefined,
          datasetType: (cellFragmentDataset.value || {}).datasetType,
        });
      }
    } else if (currentCell.value.cellType === PrintCellType.IMAGE) {
      if (currentCell.value.columnName == null) {
        cellValue = undefined;
      } else {
        cellValue = JSON.stringify({
          cellType: currentCell.value.cellType,
          imageSourceType: currentCell.value.imageSourceType,
          value: val,
          datasetId: (cellFragment.value || {}).datasetId,
          relationId:
            (cellFragmentDataset.value || {}).datasetType !== DatasetType.API
              ? currentCell.value.relationId
              : undefined,
          columnName: currentCell.value.columnName,
          fieldType: column ? column.fieldType : undefined,
          barCodeType: undefined,
          barCodeShowWords: undefined,
          datasetType: (cellFragmentDataset.value || {}).datasetType,
        });
      }
    }
    console.log(val);
    let m;
    if (val == null || val === '') {
      m = '';
    } else {
      if (
        [
          PrintCellType.COLUMN,
          PrintCellType.BARCODE,
          PrintCellType.QRCODE,
          PrintCellType.IMAGE,
        ].indexOf(currentCell.value.cellType) !== -1
      ) {
        m = Array.isArray(val) ? val.join(' / ') : val;
        if (m !== '') m = `{${m}}`;
      }
    }
    console.log(val, m, ']]]]]');
    xs.value.setCellValue(currentCell.value.position.r, currentCell.value.position.c, {
      ...currentCell.value.cell,
      v: cellValue == null ? '' : cellValue,
      text: m,
    });
  }
};
/**
 * 单元格数据类型改变
 */
const onCellTypeChange = () => {
  if (cellFragment.value && currentCell.value) {
    const cell = { ...currentCell.value };
    currentCell.value.columnName = undefined;
    if (currentCell.value.cellType === PrintCellType.BARCODE) {
      cell.barCodeType = PrintQRCodeType.CODE_128;
    } else if (currentCell.value.cellType === PrintCellType.QRCODE) {
      cell.barCodeType = PrintQRCodeType.QR_CODE;
    } else {
      cell.barCodeType = undefined;
    }
    cell.barCodeShowWords = false;
    if (!cellFragment.value.loop && cell.value) {
      cell.relationId = undefined;
    }
    currentCell.value = cell;
  }

  onCellValueChange();
};
/**
 * 单元格绑定数据集改变
 */
const onCellDatasetChange = (val: string, isInit = false) => {
  if (cellFragment.value.loop) {
    currentCell.value.relationId = val;
  } else {
    if (val === cellFragment.value.datasetId) {
      currentCell.value.datasetId = val;
      currentCell.value.relationId = undefined;
    } else {
      currentCell.value.relationId = val;
    }
  }

  if (!isInit) {
    currentCell.value.columnName = undefined;
    onCellValueChange(currentCell.value.columnName);
  }
};
/**
 * 获取表格数据
 */
const getSheetData = () => {
  return xs.value.getData();
};
/**
 * 获取指定范围内的段落列表
 */
const getFragmentByRange = (startRow: number, endRow: number) => {
  console.log('getFragmentByRange', startRow, endRow, props.printInfo);
  let fragmentList: ANY_OBJECT = [];
  if (Array.isArray(props.printInfo.fragmentJson) && props.printInfo.fragmentJson.length > 0) {
    for (let i = 0; i < props.printInfo.fragmentJson.length; i++) {
      let fragment = props.printInfo.fragmentJson[i];
      let fragmentStartRow = Number.parseInt(fragment.startRow) - 1;
      let fragmentEndRow =
        Number.parseInt(fragment.startRow) + Number.parseInt(fragment.rowSpan) - 2;
      if (fragmentStartRow > endRow) break;
      if (fragmentStartRow <= endRow && fragmentEndRow >= startRow) {
        fragmentList.push({
          index: i,
          fragment: fragment,
          startRow: fragmentStartRow,
          endRow: fragmentEndRow,
        });
      }
    }
  }

  return fragmentList;
};
defineExpose({ getSheetData, getFragmentByRange, checkValidate, getDatasetRelation });

const loadReportDictDropdownList = () => {
  dictList.value = [];
  DictionaryController.dictReportDict({})
    .then(res => {
      dictList.value = res.getList();
    })
    .catch(e => {
      console.warn(e);
    });
};
/**
 * 获取数据集以及关联数据
 */
const loadAllDatasetInfo = () => {
  ReportDatasetGroupController.listAll({})
    .then(res => {
      datasetGroupList.value = (res.data.reportDatasetGroupList || []).map((item: ANY_OBJECT) => {
        return {
          ...item,
          id: item.groupId,
          name: item.groupName,
        };
      });
      datasetList.value = (res.data.reportDatasetList || []).map((item: ANY_OBJECT) => {
        try {
          let info = JSON.parse(item.datasetInfo);
          item.datasetParamList = info.paramList;
        } catch (e) {
          item.datasetParamList = [];
        }
        return {
          ...item,
          id: item.datasetId,
          name: item.datasetName,
          columnList: undefined,
        };
      });
      datasetRelationList.value = (res.data.reportDatasetRelationList || []).map(
        (item: ANY_OBJECT) => {
          return {
            ...item,
            id: item.relationId,
            name: item.variableName,
            columnList: undefined,
          };
        },
      );
      // if (Array.isArray(props.printInfo.fragmentJson) && props.printInfo.fragmentJson.length > 0) {
      //   this.currentFragmentId = props.printInfo.fragmentJson[0].fragmentId;
      //   console.log('props.printInfo.fragmentJson', props.printInfo.fragmentJson)
      // }
    })
    .catch(e => {
      console.warn(e);
    });
};

onBeforeUnmount(() => {
  console.log('before unmount', xs.value);
  // xs.value = null;
});
onMounted(() => {
  // 获取报表字典信息
  loadReportDictDropdownList();
  loadAllDatasetInfo();
  const leftBoxContainer = document.getElementById('left-box-container');
  const view = {
    height: () => (leftBoxContainer ? leftBoxContainer.clientHeight - 50 : undefined),
    width: () => leftBoxContainer?.clientWidth,
  };
  const leftFixHeaderRender = (rowNum: number) => {
    let fragmentInfo = findFragment(rowNum);
    if (fragmentInfo != null) {
      return { fillStyle: FRAGMENT_COLOR[fragmentInfo.index % FRAGMENT_COLOR.length] };
    }
    return null;
  };
  let spreadSheet = new SpreadSheet('#x-spread-sheet', {
    view,
    leftFixHeaderRender,
    row: {
      len: 30,
      height: 25,
    },
    showBottomBar: false,
  });
  xs.value = spreadSheet;
  if (props.printInfo.sheetDataJson && props.printInfo.sheetDataJson.length > 0) {
    xs.value.setData(props.printInfo.sheetDataJson);
  }
  xs.value.on('cell-selected', (cell: ANY_OBJECT, ri: number, ci: number) => {
    let value, temp;
    let columnObject: ANY_OBJECT = {
      columnName: undefined,
      fieldType: undefined,
      datasetId: undefined,
      relationId: undefined,
      bindDataset: undefined,
      imageSourceType: PrintCellImageSourceType.COLUMN,
      datasetType: undefined,
    };
    if (cell != null) {
      try {
        temp = JSON.parse(cell.v);
        if (temp != null && temp.value != null) {
          value = temp.value;
        } else {
          value = cell.v;
        }
        // 单元格数据类型为绑定字段
        if (
          temp != null &&
          (temp.cellType === PrintCellType.COLUMN ||
            temp.cellType === PrintCellType.BARCODE ||
            temp.cellType === PrintCellType.QRCODE ||
            temp.cellType === PrintCellType.IMAGE)
        ) {
          columnObject = {
            columnName: temp.columnName,
            fieldType: temp.fieldType,
            datasetId: temp.datasetId,
            relationId: temp.relationId,
            bindDataset: temp.relationId || temp.datasetId,
            imageSourceType: temp.imageSourceType || PrintCellImageSourceType.COLUMN,
            barCodeType:
              temp.barCodeType ||
              (temp.cellType === PrintCellType.QRCODE
                ? PrintQRCodeType.QR_CODE
                : PrintBarCodeType.CODE_128),
            barCodeShowWords: temp.barCodeShowWords || false,
            datasetType: temp.datasetType,
          };
        }
      } catch (e) {
        temp = null;
        value = cell.v;
      }
    }
    currentCell.value = {
      position: { r: ri, c: ci },
      cellType: (temp || {}).cellType || PrintCellType.COLUMN,
      cell: cell,
      value: value,
      ...columnObject,
    };
  });
  xs.value.on('cell-edited', (text: string, ri: number, ci: number) => {
    if (currentCell.value.position.r + '-' + currentCell.value.position.c === ri + '-' + ci) {
      // TODO 这里原使用的是this.columnName与`{${text}}`进行的比较
      if (
        `{${text}}` !== currentCell.value.columnName &&
        currentCell.value.columnName !== undefined
      ) {
        currentCell.value.columnName = undefined;
        // currentCell.value.cell = undefined
        currentCell.value.datasetType = undefined;
        currentCell.value.fieldType = undefined;
        currentCell.value.relationId = undefined;
        currentCell.value.value = undefined;
      }
      let tmp = { ...currentCell.value.cell, text: text };
      if (tmp.v) {
        delete tmp.v;
      }
      console.log(tmp);
      xs.value.setCellValue(currentCell.value.position.r, currentCell.value.position.c, tmp);
    }
    // console.log(text, ri, ci, 'text-edit', currentCell.value)
  });
  // spreadSheet.on('cell-edited', (text, ri, ci) => {
  //   console.log(text, ri, ci, 'text-edit')
  // })
});

watch(
  () => cellBindColumnNode.value,
  newValue => {
    if (newValue) {
      loadDatasetColumnList(newValue).catch(e => {
        console.warn(e);
      });
    }
  },
);
watch(
  () => cellBindDatasetList.value,
  () => {
    if (currentCell.value && currentCell.value.bindDataset == null && cellBindDatasetList.value) {
      currentCell.value.bindDataset = (cellBindDatasetList.value[0] || {}).id;
      onCellDatasetChange(currentCell.value.bindDataset, true);
    }
  },
);
</script>

<style lang="scss" scoped>
.print-template-edit {
  display: flex;
  height: 100%;
}

.attribute-panel {
  width: 288px;
  height: 100%;
  background: white;

  .attribute-item-title {
    height: 40px;
    padding: 0 16px;
    line-height: 40px;

    span {
      font-size: 12px;
      color: #333;
    }
  }
}

.x-spread-sheet-footer {
  display: flex;
  align-items: center;
  height: 50px;
  .x-spread-sheet-footer-tip {
    color: #9c9c9c;
  }
}
.left-box {
  display: flex;
  width: 0;
  height: 100%;
  border-left: 1px solid #e5e5e5;
  border-right: 1px solid #e5e5e5;
  flex: 1;
  flex-direction: column;
  .x-spread-sheet-container {
    flex: 1;
    height: 0;
  }
}

.edit-box {
  flex-shrink: 0;
  width: 288px;
  height: calc(100vh - 72px);
  background: white;
  border-radius: 10px;
  flex-grow: 0;
}

.column-box :deep(.el-alert) {
  padding: 0;
}

.column-box :deep(.el-alert__content) {
  width: 100%;
  padding: 5px 10px;
  font-size: 12px;
}

.column-box :deep(.el-alert__content .el-icon-close) {
  cursor: pointer;
  flex-shrink: 0;
  flex-grow: 0;
}

.column-item + .column-item {
  margin-top: 5px;
}

.column-item :deep(.el-alert__title, .column-item .el-link) {
  font-size: 12px !important;
}

.print-template-edit :deep(.el-divider.el-divider--horizontal) {
  margin: 20px 0 !important;
}

.print-template-edit :deep(.el-empty .el-empty__description) {
  display: none;
}

.print-template-edit :deep(.el-form-item__label) {
  font-weight: normal !important;
}

.form-item-label-btn {
  margin-left: 6px;
  font-size: 14px;
  cursor: pointer;
}

.attribute-panel :deep(.el-tabs__item) {
  height: 57px;
  padding-top: 10px;
  padding-right: 10px;
  padding-left: 10px;
  font-size: 14px;
}
.attribute-panel :deep(.el-tabs__header) {
  margin: 0;
}
.attribute-panel :deep(.el-tabs__nav) {
  margin-left: 16px;
}
.attribute-panel :deep(.el-form-item) {
  margin-bottom: 12px;
}
.attribute-panel :deep(.el-form-item__label) {
  padding-bottom: 5px;
  font-size: 14px;
  color: #333;
}
.attribute-panel :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}
.attribute-panel :deep(.el-radio__label) {
  padding-left: 8px;
}
.attribute-panel :deep(.el-radio) {
  margin-right: 24px;
}
.attribute-panel :deep(.el-radio:last-child) {
  margin-right: 0;
}
.attribute-panel :deep(.el-collapse-item__wrap) {
  background: #fafbfc;
  border: none;
}
.attribute-panel :deep(.el-collapse-item__content) {
  padding-bottom: 0;
  background: #fafbfc;
}
</style>

<style>
.luckysheet-input-box {
  z-index: 999;
}

.luckysheet {
  border-top: none;
  border-bottom: none;
}
</style>
