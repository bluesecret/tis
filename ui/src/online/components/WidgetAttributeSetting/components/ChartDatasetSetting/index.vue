<template>
  <el-form label-position="top" size="default" v-if="datasetInfo" style="padding: 10px 16px 0">
    <el-form-item label="数据集">
      <el-cascader
        style="width: 100%"
        v-model="datasetPath"
        @change="onDatasetChange"
        :options="datasetTree"
        :props="{ label: 'name', value: 'id' }"
      />
    </el-form-item>
    <!-- 针对API结果集，选择绑定字段 -->
    <el-form-item label="绑定字段" v-if="(bindDataset || {}).datasetType === DatasetType.API">
      <el-cascader
        style="width: 100%"
        v-model="datasetInfo.bindColumnId"
        :options="datasetArrayColumn"
        :props="{ label: 'columnName', value: 'columnId', emitPath: false }"
        @change="onBindColumnChange"
      />
    </el-form-item>
    <!-- 数据集参数 -->
    <MultiItemList
      v-if="
        (bindDataset || {}).datasetType === DatasetType.SQL ||
        (bindDataset || {}).datasetType === DatasetType.API
      "
      v-model:data="datasetInfo.datasetFilterParams"
      :disabled="bindDataset == null"
      label="数据集参数"
      @add="onSetDatasetParamList()"
      @edit="onSetDatasetParamList()"
      @delete="onRemoveDatasetParam"
      :prop="{
        label: 'paramName',
        value: 'paramName',
      }"
    >
      <template v-slot="scope">
        <template v-if="scope.data != null">
          <span>{{ scope.data.paramName }}</span>
          <span style="margin: 0 10px">等于</span>
          <span>
            {{ getFilterShowName(scope.data) }}
          </span>
        </template>
      </template>
    </MultiItemList>
    <!-- 类别轴 / 维度 -->
    <MultiItemList
      v-if="isShowCategoryColumnList"
      v-model:data="datasetInfo.categroyColumnList"
      :maxCount="categoryColumnMaxCount"
      :disabled="bindDataset == null"
      label="类别轴 / 维度"
      @add="onEditCategroyColumn()"
      @edit="onEditCategroyColumn"
      @delete="onRemoveCategroyColumn"
      :prop="{
        label: 'showName',
        value: 'id',
      }"
    />
    <!-- 行分组 / 维度 -->
    <MultiItemList
      v-if="widget.widgetType === SysCustomWidgetType.PivotTable"
      v-model:data="datasetInfo.rowGroupColumnList"
      :disabled="bindDataset == null"
      label="行分组 / 维度"
      @add="onEditRowGroupColumn()"
      @edit="onEditRowGroupColumn"
      @delete="onRemoveRowGroupColumn"
      :supportSort="true"
      dragGroup="rowGroup"
      :prop="{
        label: 'showName',
        value: 'id',
      }"
    />
    <!-- 列分组 / 维度 -->
    <MultiItemList
      v-if="widget.widgetType === SysCustomWidgetType.PivotTable"
      v-model:data="datasetInfo.columnGroupColumnList"
      :disabled="bindDataset == null"
      label="列分组 / 维度"
      @add="onEditColumnGroupColumn()"
      @edit="onEditColumnGroupColumn"
      @delete="onRemoveColumnGroupColumn"
      :supportSort="true"
      dragGroup="columnGroup"
      :prop="{
        label: 'showName',
        value: 'id',
      }"
    />
    <!-- 值轴 / 指标 -->
    <MultiItemList
      v-if="isShowValueColumnList"
      :maxCount="widget.widgetType === SysCustomWidgetType.FunnelChart ? 1 : undefined"
      v-model:data="datasetInfo.valueColumnList"
      label="值轴 / 指标"
      :disabled="bindDataset == null"
      @add="onEditIndexColumn()"
      @edit="onEditIndexColumn"
      @delete="onRemoveIndexColumn"
      :supportSort="true"
      dragGroup="valueGroup"
      :prop="{
        label: 'showName',
        value: 'id',
      }"
    >
      <template v-slot:right="scope">
        <span
          v-if="bindDataset?.datasetType !== DatasetType.API"
          style="margin-right: 5px; font-size: 12px; color: #878d9f"
        >
          {{ CalculateType.getValue((scope.data || {}).calculateType) }}
        </span>
      </template>
    </MultiItemList>

    <!-- 显示字段 -->
    <MultiItemList
      v-if="isShowShowColumnList"
      v-model:data="datasetInfo.showColumnList"
      :disabled="bindDataset == null"
      :supportSort="true"
      label="显示字段"
      @add="onShowViewTableColumnList()"
      @edit="onShowViewTableColumnList"
      @delete="
        row => {
          onRemoveSelectedColumn(row, datasetInfo.showColumnList);
        }
      "
      :prop="{
        label: 'showName',
        value: 'columnId',
      }"
    />

    <!-- 图片路径 -->
    <MultiItemList
      v-if="isShowImageColumn"
      v-model:data="datasetInfo.imagePath"
      :disabled="bindDataset == null"
      :supportSort="true"
      :maxCount="1"
      label="图片路径字段"
      @add="onEditCarouselPictureUrlColumn()"
      @edit="onEditCarouselPictureUrlColumn"
      @delete="
        row => {
          onRemoveSelectedColumn(row, datasetInfo.imagePath);
        }
      "
      :prop="{
        label: function (item:ANY_OBJECT) {
          if (Array.isArray(item.columnName)) {
            return item.columnName.join(' / ');
          } else {
            return item.columnName;
          }
        },
        value: 'columnId',
      }"
    />

    <!-- 图片名称 -->
    <MultiItemList
      v-if="isShowImageColumn"
      v-model:data="datasetInfo.imageName"
      :disabled="bindDataset == null"
      :supportSort="true"
      :maxCount="1"
      label="图片名称字段"
      @add="onEditCarouselPictureNameColumn()"
      @edit="onEditCarouselPictureNameColumn"
      @delete="
        row => {
          onRemoveSelectedColumn(row, datasetInfo.imageName);
        }
      "
      :prop="{
        label: function (item:ANY_OBJECT) {
          if (Array.isArray(item.columnName)) {
            return item.columnName.join(' / ');
          } else {
            return item.columnName;
          }
        },
        value: 'columnId',
      }"
    />

    <!-- 富文本显示字段 -->
    <MultiItemList
      v-if="widget.widgetType === SysCustomWidgetType.RichText"
      v-model:data="datasetInfo.textColumn"
      :disabled="bindDataset == null"
      :supportSort="true"
      :maxCount="1"
      label="内容字段"
      @add="onEditRichTextColumn()"
      @edit="onEditRichTextColumn"
      @delete="
        row => {
          onRemoveSelectedColumn(row, datasetInfo.textColumn);
        }
      "
      :prop="{
        label: function (item:ANY_OBJECT) {
          if (Array.isArray(item.columnName)) {
            return item.columnName.join(' / ');
          } else {
            return item.columnName;
          }
        },
        value: 'columnId',
      }"
    />
    <template v-if="widget.widgetType === SysCustomWidgetType.DataCard">
      <!-- 通用卡片 字段绑定 -->
      <EditPropertyFieldBind
        fieldKey="dataCardTitle"
        v-model:value="datasetInfo.mainTextColumn"
        :validColumnList="getValidColumnList"
        :dataset="bindDataset"
        label="卡片标题"
      />
      <EditPropertyFieldBind
        fieldKey="dataCardContent"
        v-model:value="datasetInfo.numTextColumn"
        :validColumnList="getValidColumnList"
        :dataset="bindDataset"
        label="卡片内容"
      />
      <EditPropertyFieldBind
        fieldKey="dataCardLeftText"
        v-model:value="datasetInfo.footTextColumn"
        :validColumnList="getValidColumnList"
        :dataset="bindDataset"
        label="左下文字"
      />
      <!--
      <EditPropertyFieldBind
        v-model:value="datasetInfo.footNumIconColumn"
        :validColumnList="getValidColumnList"
        :dataset="bindDataset"
        label='底部数字图标'
      />
      -->
      <EditPropertyFieldBind
        fieldKey="dataCardRightText"
        v-model:value="datasetInfo.footNumTextColumn"
        :validColumnList="getValidColumnList"
        :dataset="bindDataset"
        label="右下文字"
      />
    </template>
    <template v-if="widget.widgetType === SysCustomWidgetType.DataProgressCard">
      <!-- 通用卡片 字段绑定 -->
      <EditPropertyFieldBind
        fieldKey="progressCardTitle"
        v-model:value="datasetInfo.mainTextColumn"
        :validColumnList="getValidColumnList"
        :dataset="bindDataset"
        label="卡片标题"
      />
      <EditPropertyFieldBind
        fieldKey="progressCardContent"
        v-model:value="datasetInfo.numTextColumn"
        :validColumnList="getValidColumnList"
        :dataset="bindDataset"
        label="卡片内容"
      />
    </template>
    <template v-if="widget.widgetType === SysCustomWidgetType.ProgressBar">
      <EditPropertyFieldBind
        fieldKey="progressCardDesc"
        v-model:value="datasetInfo.textColumn"
        :validColumnList="getValidColumnList"
        :dataset="bindDataset"
        label="说明字段"
      />
    </template>
    <template
      v-if="
        widget.widgetType === SysCustomWidgetType.ProgressCircle ||
        widget.widgetType === SysCustomWidgetType.DataProgressCard ||
        widget.widgetType === SysCustomWidgetType.ProgressBar
      "
    >
      <!-- 通用列表 字段绑定 -->
      <EditPropertyFieldBind
        fieldKey="progressColumn"
        v-model:value="datasetInfo.progressColumn"
        :validColumnList="getValidColumnList"
        :dataset="bindDataset"
        label="进度字段"
      />
    </template>
    <template v-if="widget.widgetType === SysCustomWidgetType.CommonList">
      <!-- 通用列表 字段绑定 -->
      <EditPropertyFieldBind
        fieldKey="commonListTitle"
        v-model:value="datasetInfo.titleColumn"
        :validColumnList="getValidColumnList"
        :dataset="bindDataset"
        label="内容标题字段"
      />
      <EditPropertyFieldBind
        fieldKey="commonListContent"
        v-model:value="datasetInfo.textColumn"
        :validColumnList="getValidColumnList"
        :dataset="bindDataset"
        label="内容文字字段"
      />
      <EditPropertyFieldBind
        fieldKey="commonListRight"
        v-model:value="datasetInfo.timeColumn"
        :validColumnList="getValidColumnList"
        :dataset="bindDataset"
        label="内容时间字段"
      />
    </template>
    <!-- 排序 -->
    <MultiItemList
      v-if="
        (bindDataset || {}).datasetType !== DatasetType.API &&
        [
          SysCustomWidgetType.ProgressBar,
          SysCustomWidgetType.ProgressCircle,
          SysCustomWidgetType.DataCard,
          SysCustomWidgetType.DataProgressCard,
          SysCustomWidgetType.CommonList,
          SysCustomWidgetType.DataViewTable,
        ].indexOf(widget.widgetType) === -1
      "
      v-model:data="datasetInfo.orderInfoList"
      :disabled="bindDataset == null"
      label="排序"
      :maxCount="1"
      @add="onEditWidgetOrder()"
      @edit="onEditWidgetOrder"
      @delete="onRemoveWidgetOrder"
      :prop="{
        label: 'showName',
        value: 'id',
      }"
    >
      <template v-slot="scope">
        <span>{{ getOrderColumnName((scope.data || {}).id) }}</span>
      </template>
      <template v-slot:right="scope">
        <i
          v-if="(scope.data || {}).orderType === OrderType.ASC"
          class="el-icon-sort-up"
          style="margin-right: 5px; font-size: 12px; color: #878d9f"
        >
          {{ OrderType.getValue((scope.data || {}).orderType) }}
        </i>
        <i
          v-if="(scope.data || {}).orderType === OrderType.DESC"
          class="el-icon-sort-down"
          style="margin-right: 5px; font-size: 12px; color: #878d9f"
        >
          {{ OrderType.getValue((scope.data || {}).orderType) }}
        </i>
      </template>
    </MultiItemList>
    <!-- 过滤器 -->
    <MultiItemList
      v-if="(bindDataset || {}).datasetType !== DatasetType.API"
      v-model:data="datasetInfo.filterList"
      :disabled="bindDataset == null"
      label="过滤器"
      @add="onEditFilterColumn()"
      @edit="onEditFilterColumn"
      @delete="onRemoveFilterColumn"
      :prop="{
        label: 'paramName',
        value: 'paramId',
      }"
    >
      <template v-slot="scope">
        <template v-if="scope.data != null">
          <span>{{ scope.data.paramName }}</span>
          <span style="margin: 0 10px">{{
            CriteriaFilterType.getValue(scope.data.filterType)
          }}</span>
          <span>
            {{ getFilterShowName(scope.data) }}
          </span>
        </template>
      </template>
    </MultiItemList>
  </el-form>
</template>

<script setup lang="ts">
import { CascaderValue, ElMessageBox } from 'element-plus';
import { Dialog } from '@/components/Dialog';
import MultiItemList from '@/components/MultiItemList/index.vue';
import { findItemFromList, findTreeNodePath, treeDataTranslate } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import {
  CalculateType,
  CriteriaFilterType,
  CustomDateValueType,
  DatasetType,
  FilterValueKind,
  OrderType,
  ReportWidgetType,
} from '@/common/staticDict/report';
import { SysCustomWidgetType } from '@/common/staticDict';
import { useLayoutStore } from '@/store';
import SetReportDatasetParam from '../../../SetReportDatasetParam/index.vue';
import EditWidgetCategoryColumn from './editWidgetCategoryColumn.vue';
import EditWidgetIndexColumn from './editWidgetIndexColumn.vue';
import EditWidgetOrderInfo from './editWidgetOrderInfo.vue';
import EditReportColumnFilter from './editReportColumnFilter.vue';
import EditColumnBind from './editColumnBind.vue';
import EditDataViewTableColumn from './editDataViewTableColumn.vue';
import EditPropertyFieldBind from './editPropertyFieldBind.vue';

const layoutStore = useLayoutStore();

const formConfig = inject('formConfig', () => {
  console.error('ChartDatasetSetting: formConfig not injected');
  return {} as ANY_OBJECT;
});
//console.log('formConfig()', formConfig());
const bindDataset = ref<ANY_OBJECT>();

const widget = computed(() => {
  return formConfig().currentWidget;
});
const datasetInfo = computed(() => {
  return widget.value ? (widget.value.props || {}).datasetInfo : undefined;
});
// TODO 响应性
const datasetPath = computed(() => {
  // console.log(
  //   ' >>>>>>>datasetPath>>>>>>>>> ',
  //   datasetInfo.value,
  //   datasetTree.value,
  //   findTreeNodePath(datasetTree.value, datasetInfo.value.datasetId),
  // );
  if (!datasetInfo.value || !datasetInfo.value.datasetId) return undefined;
  return findTreeNodePath(datasetTree.value, datasetInfo.value.datasetId);
});
const datasetTree = computed(() => {
  return formConfig().reportDatasetTree;
});
const getOrderColumnList = computed(() => {
  if (widget.value == null) return [];
  let columnList = (datasetInfo.value.categroyColumnList || [])
    .map((item: ANY_OBJECT) => {
      return {
        id: item.columnId,
        columnId: item.columnId,
        showName: item.showName,
        isIndex: false,
      };
    })
    .concat(
      (datasetInfo.value.valueColumnList || []).map((item: ANY_OBJECT) => {
        return {
          id: item.id,
          showName: item.showName,
          columnId: item.columnId,
          calculateType: item.calculateType,
          isIndex: true,
        };
      }),
    );
  return columnList;
});
const datasetArrayColumn = computed(() => {
  if (!bindDataset.value || !Array.isArray(bindDataset.value?.columnList)) return [];
  let onlyArray =
    [
      SysCustomWidgetType.PivotTable,
      SysCustomWidgetType.LineChart,
      SysCustomWidgetType.BarChart,
      SysCustomWidgetType.PieChart,
      SysCustomWidgetType.ScatterChart,
      SysCustomWidgetType.DataViewTable,
      SysCustomWidgetType.Carousel,
      SysCustomWidgetType.FunnelChart,
      SysCustomWidgetType.CommonList,
    ].indexOf(widget.value.widgetType) !== -1;
  // 所有数组类型字段
  let arrayColumnList: ANY_OBJECT[] = [];
  // 忽略字段，如果字段父在此字段中，则字段不加入到arrayColumnList
  let ignorParentColumn: ANY_OBJECT[] = [];
  bindDataset.value?.columnList.forEach(column => {
    if (column.fieldType === 'Object' || column.fieldType === 'Array') {
      if (ignorParentColumn.indexOf(column.parentId) === -1) {
        if (!onlyArray || column.fieldType === 'Array') {
          arrayColumnList.push(column);
          ignorParentColumn.push(column.columnId);
        }
      } else {
        ignorParentColumn.push(column.columnId);
      }
    }
  });
  // 所有可选字段ID
  let allColumnIds: string[] = [];
  // 添加所有可用数组字段的父字段
  let tempList: ANY_OBJECT[] = [];
  arrayColumnList.forEach(column => {
    let tempColumn: ANY_OBJECT | null = column;
    do {
      if (allColumnIds.indexOf(tempColumn.columnId) === -1) {
        tempList.push({
          ...tempColumn,
          children: undefined,
        });
        allColumnIds.push(tempColumn.columnId);
      }
      tempColumn = findItemFromList(bindDataset.value?.columnList, tempColumn.parentId, 'columnId');
    } while (tempColumn != null);
  });
  return treeDataTranslate(tempList, 'columnId');
});
const getValidColumnList = computed(() => {
  //console.log('================== getValidColumnList 1', bindDataset.value);
  if (!bindDataset.value) return [];
  if (bindDataset.value?.datasetType !== DatasetType.API) {
    return bindDataset.value?.columnList;
  } else {
    if (datasetInfo.value.bindColumnId == null || datasetInfo.value.bindColumnId === '') return [];
    // 临时Object字段列表，只有有子字段的Object字段，才会加入到最终的显示
    let tempObjectFieldMap = new Map();
    // 父字段列表，只有父字段在此列表中的字段，才会显示
    let parentColumnIdList: string[] = [];
    // 最终显示字段列表
    let tempList: ANY_OBJECT[] = [];
    (bindDataset.value?.columnList || []).forEach((column: ANY_OBJECT) => {
      // 只有绑定字段下的非数组字段可以显示
      if (column.fieldType !== 'Array' || column.columnId === datasetInfo.value.bindColumnId) {
        if (
          parentColumnIdList.indexOf(column.parentId) !== -1 ||
          column.columnId === datasetInfo.value.bindColumnId
        ) {
          parentColumnIdList.push(column.columnId);
          if (column.fieldType !== 'Object') {
            tempList.push(column);
          } else {
            if (column.parentId === datasetInfo.value.bindColumnId) {
              // 父字段是绑定字段，放入显示列表
              tempList.push(column);
            } else {
              // 放入临时Object字段缓存，用于判断是否这个Object下有子字段
              tempObjectFieldMap.set(column.columnId, column);
            }
          }
          // 把父字段添加到显示列表
          let parentColumn = column;
          do {
            parentColumn = tempObjectFieldMap.get(parentColumn.parentId);
            if (parentColumn != null && tempList.indexOf(parentColumn) === -1)
              tempList.push(parentColumn);
          } while (parentColumn != null);
        }
      }
    });

    //console.log('================== getValidColumnList 2', tempList);
    // 如果只有绑定字段，那么返回空
    return tempList.length > 1 ? tempList : [];
  }
});
const categoryColumnMaxCount = computed(() => {
  if (widget.value.widgetType === SysCustomWidgetType.RadarChart) {
    return 1;
  } else {
    return undefined;
  }
});
// 类别轴 / 维度 的显示判断
const isShowCategoryColumnList = computed(() => {
  return (
    widget.value.widgetType !== SysCustomWidgetType.PivotTable &&
    widget.value.widgetType !== SysCustomWidgetType.DataViewTable &&
    widget.value.widgetType !== SysCustomWidgetType.Carousel &&
    widget.value.widgetType !== SysCustomWidgetType.RichText &&
    widget.value.widgetType !== SysCustomWidgetType.CommonList &&
    widget.value.widgetType !== SysCustomWidgetType.ProgressBar &&
    widget.value.widgetType !== SysCustomWidgetType.ProgressCircle &&
    widget.value.widgetType !== SysCustomWidgetType.DataCard &&
    widget.value.widgetType !== SysCustomWidgetType.DataProgressCard
  );
});
// 值轴 / 指标 的显示判断
const isShowValueColumnList = computed(() => {
  return (
    widget.value.widgetType !== SysCustomWidgetType.DataViewTable &&
    widget.value.widgetType !== SysCustomWidgetType.Carousel &&
    widget.value.widgetType !== SysCustomWidgetType.RichText &&
    widget.value.widgetType !== SysCustomWidgetType.CommonList &&
    widget.value.widgetType !== SysCustomWidgetType.ProgressBar &&
    widget.value.widgetType !== SysCustomWidgetType.ProgressCircle &&
    widget.value.widgetType !== SysCustomWidgetType.DataCard &&
    widget.value.widgetType !== SysCustomWidgetType.DataProgressCard
  );
});
// 显示字段选择组件
const isShowShowColumnList = computed(() => {
  return widget.value.widgetType === SysCustomWidgetType.DataViewTable;
});
// 显示轮播图url和label选择组件
const isShowImageColumn = computed(() => {
  return widget.value.widgetType === SysCustomWidgetType.Carousel;
});
// 显示富文本内容选择组件
// const isShowTextColumn = computed(() => {
//   return widget.value.widgetType === SysCustomWidgetType.RichText;
// });

const buildColumnList = (
  columnList: ANY_OBJECT[],
  disableColumnItemHandler: ((item: ANY_OBJECT) => boolean) | null = null,
) => {
  return getValidColumnList.value
    .map((item: ANY_OBJECT) => {
      let disabled = item.fieldType !== 'Object' && item.fieldType !== 'Array';
      disabled =
        disabled &&
        columnList.map((item: ANY_OBJECT) => item.columnId).indexOf(item.columnId) !== -1;
      if (disableColumnItemHandler) disabled = disabled && disableColumnItemHandler(item);
      return {
        ...item,
        disabled: disabled,
      };
    })
    .sort((val1: ANY_OBJECT, val2: ANY_OBJECT) => {
      return val1.disabled ? (val2.disabled ? 0 : 1) : -1;
    });
};
// 编辑类别轴
const handlerCategroyColumn = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  if (row == null) {
    datasetInfo.value.categroyColumnList.push(res);
  } else {
    datasetInfo.value.categroyColumnList = datasetInfo.value.categroyColumnList.map(
      (item: ANY_OBJECT) => {
        if (item.columnId === res.columnId) {
          return {
            ...res,
          };
        } else {
          return {
            ...item,
          };
        }
      },
    );
  }
};
const onEditCategroyColumn = (row: ANY_OBJECT | null = null) => {
  if (!bindDataset.value || !Array.isArray(getValidColumnList.value)) return;
  Dialog.show<ANY_OBJECT>(
    '类别轴',
    EditWidgetCategoryColumn,
    {
      area: ['600px'],
    },
    {
      dataset: {
        datasetType: bindDataset.value?.datasetType,
      },
      rowData: row,
      columnList: buildColumnList(datasetInfo.value.categroyColumnList, (item: ANY_OBJECT) => {
        return !item.dimension;
      }),
      path: 'thirdEditWidgetCategroyColumn',
    },
    {
      width: '600px',
      height: '500px',
      pathName: '/thirdParty/thirdEditWidgetCategroyColumn',
    },
  )
    .then(res => {
      handlerCategroyColumn(row, res);
    })
    .catch((e: Error) => {
      console.log(e);
    });
};
// 编辑透视表行分组字段
const handlerRowGroupColumn = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  if (row == null) {
    datasetInfo.value.rowGroupColumnList.push(res);
  } else {
    datasetInfo.value.rowGroupColumnList = datasetInfo.value.rowGroupColumnList.map(
      (item: ANY_OBJECT) => {
        if (item.columnId === res.columnId) {
          return {
            ...res,
          };
        } else {
          return {
            ...item,
          };
        }
      },
    );
  }
};
const onEditRowGroupColumn = (row: ANY_OBJECT | null = null) => {
  if (
    widget.value == null ||
    widget.value.props == null ||
    !Array.isArray(getValidColumnList.value)
  )
    return;
  Dialog.show<ANY_OBJECT>(
    '行分组',
    EditWidgetCategoryColumn,
    {
      area: ['600px'],
    },
    {
      rowData: row,
      dataset: {
        datasetType: bindDataset.value?.datasetType,
      },
      columnList: getValidColumnList.value
        .map((item: ANY_OBJECT) => {
          let disabled =
            !item.dimension ||
            datasetInfo.value.columnGroupColumnList
              .map((item: ANY_OBJECT) => item.columnId)
              .indexOf(item.columnId) !== -1 ||
            datasetInfo.value.rowGroupColumnList
              .map((item: ANY_OBJECT) => item.columnId)
              .indexOf(item.columnId) !== -1;
          return {
            ...item,
            disabled: disabled && item.fieldType !== 'Object' && item.fieldType !== 'Array',
          };
        })
        .sort((val1, val2) => {
          return val1.disabled ? (val2.disabled ? 0 : 1) : -1;
        }),
      supportColumnWidth: true,
      path: 'thirdEditWidgetCategroyColumn/rowGroup',
    },
    {
      width: '600px',
      height: '500px',
      pathName: '/thirdParty/thirdEditWidgetCategroyColumn',
    },
  )
    .then(res => {
      handlerRowGroupColumn(row, res);
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
// 编辑透视表列分组字段
const handlerColumnGroupColumn = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  if (row == null) {
    datasetInfo.value.columnGroupColumnList.push(res);
  } else {
    datasetInfo.value.columnGroupColumnList = datasetInfo.value.columnGroupColumnList.map(
      (item: ANY_OBJECT) => {
        if (item.columnId === res.columnId) {
          return {
            ...res,
          };
        } else {
          return {
            ...item,
          };
        }
      },
    );
  }
};
const onEditColumnGroupColumn = (row: ANY_OBJECT | null = null) => {
  if (
    widget.value == null ||
    widget.value.props == null ||
    !Array.isArray(getValidColumnList.value)
  )
    return;
  Dialog.show<ANY_OBJECT>(
    '列分组',
    EditWidgetCategoryColumn,
    {
      area: ['600px'],
    },
    {
      rowData: row,
      dataset: {
        datasetType: bindDataset.value?.datasetType,
      },
      columnList: getValidColumnList.value
        .map((item: ANY_OBJECT) => {
          let disabled =
            !item.dimension ||
            datasetInfo.value.columnGroupColumnList
              .map((item: ANY_OBJECT) => item.columnId)
              .indexOf(item.columnId) !== -1 ||
            datasetInfo.value.rowGroupColumnList
              .map((item: ANY_OBJECT) => item.columnId)
              .indexOf(item.columnId) !== -1;
          return {
            ...item,
            disabled: disabled && item.fieldType !== 'Object' && item.fieldType !== 'Array',
          };
        })
        .sort((val1, val2) => {
          return val1.disabled ? (val2.disabled ? 0 : 1) : -1;
        }),
      path: 'thirdEditWidgetCategroyColumn/colGroup',
    },
    {
      width: '600px',
      height: '500px',
      pathName: '/thirdParty/thirdEditWidgetCategroyColumn',
    },
  )
    .then(res => {
      handlerColumnGroupColumn(row, res);
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
// 编辑数值轴
const handlerIndexColumn = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  if (row == null) {
    datasetInfo.value.valueColumnList.push({
      ...res,
      id: res.columnId + '__' + res.calculateType,
    });
  } else {
    datasetInfo.value.valueColumnList = datasetInfo.value.valueColumnList.map(
      (item: ANY_OBJECT) => {
        if (item.id === res.id) {
          return {
            ...res,
          };
        } else {
          return {
            ...item,
          };
        }
      },
    );
  }
};
const onEditIndexColumn = (row: ANY_OBJECT | null = null) => {
  if (!bindDataset.value || !Array.isArray(getValidColumnList.value)) return;
  let area = ['1200px', '700px'];
  if (bindDataset.value?.datasetType === DatasetType.API) {
    area = ['600px'];
  }
  Dialog.show<ANY_OBJECT>(
    '数值轴',
    EditWidgetIndexColumn,
    {
      area: area,
    },
    {
      dataset: {
        datasetType: bindDataset.value?.datasetType,
      },
      rowData: row,
      datasetId: datasetInfo.value.datasetId,
      columnList: (getValidColumnList.value || [])
        .map((item: ANY_OBJECT) => {
          let disabled =
            item.fieldType !== 'Object' &&
            item.fieldType !== 'Array' &&
            (item.dimension ||
              datasetInfo.value.valueColumnList
                .map((item: ANY_OBJECT) => item.columnId)
                .indexOf(item.columnId) !== -1);
          return {
            ...item,
            disabled: disabled,
          };
        })
        .sort((val1, val2) => {
          return val1.disabled ? (val2.disabled ? 0 : 1) : -1;
        }),
      supportColumnWidth: widget.value.widgetType === ReportWidgetType.PivotTable,
      widgetList: (formConfig().allWidgetList || []).map((widget: ANY_OBJECT) => {
        return {
          widgetId: widget.widgetId,
          showName: widget.showName,
          widgetType: widget.widgetType,
          children: (widget.children || []).map((item: ANY_OBJECT) => {
            return {
              id: item.id,
              showName: item.showName,
            };
          }),
        };
      }),
      formParamList: (formConfig().form.paramList || []).map(
        (item: ANY_OBJECT) => item.variableName,
      ),
      reportDictList: formConfig().dictList,
      path: 'thirdEditWidgetIndexColumn',
    },
    {
      width: '1200px',
      height: '750px',
      pathName: '/thirdParty/thirdEditWidgetIndexColumn',
    },
  )
    .then(res => {
      handlerIndexColumn(row, res);
    })
    .catch((e: Error) => {
      console.log(e);
    });
};
// 显示字段
const handlerColumnSelected = (
  columnList: ANY_OBJECT[],
  row: ANY_OBJECT | null,
  res: ANY_OBJECT,
) => {
  if (row == null) {
    columnList.push(res);
    //$forceUpdate();
  } else {
    columnList.forEach((item: ANY_OBJECT) => {
      if (item.columnId === res.columnId) {
        Object.keys(item).forEach(key => {
          item[key] = res[key];
        });
      }
    });
  }
};
const handlerEditDataViewColumn = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  handlerColumnSelected(datasetInfo.value.showColumnList, row, res);
};
const onShowViewTableColumnList = (row: ANY_OBJECT | null = null) => {
  if (!bindDataset.value || !Array.isArray(getValidColumnList.value)) return;
  Dialog.show<ANY_OBJECT>(
    '显示字段',
    EditDataViewTableColumn,
    {
      area: ['600px'],
    },
    {
      dataset: {
        datasetType: bindDataset.value?.datasetType,
      },
      rowData: row,
      columnList: buildColumnList(datasetInfo.value.showColumnList),
      path: 'thirdEditDataViewColumn',
    },
    {
      width: '600px',
      height: '500px',
      pathName: '/thirdParty/thirdEditDataViewColumn',
    },
  )
    .then(res => {
      handlerEditDataViewColumn(row, res);
    })
    .catch((e: Error) => {
      console.log(e);
    });
};
const handlerEditCarouselImage = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  datasetInfo.value.imagePath = [res];
};
const onEditCarouselPictureUrlColumn = (row: ANY_OBJECT | null = null) => {
  if (!bindDataset.value || !Array.isArray(getValidColumnList.value)) return;
  Dialog.show<ANY_OBJECT>(
    '图片路径字段',
    EditColumnBind,
    {
      area: ['600px'],
    },
    {
      dataset: {
        datasetType: bindDataset.value?.datasetType,
      },
      rowData: row,
      columnList: buildColumnList(datasetInfo.value.imagePath),
      path: 'thirdEditWidgetColumnBind/carouselPicture',
    },
    {
      width: '600px',
      height: '500px',
      pathName: '/thirdParty/thirdEditWidgetColumnBind',
    },
  )
    .then(res => {
      handlerEditCarouselImage(row, res);
    })
    .catch((e: Error) => {
      console.log(e);
    });
};
const handlerEditCarouselName = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  datasetInfo.value.imageName = [res];
};
const onEditCarouselPictureNameColumn = (row: ANY_OBJECT | null = null) => {
  if (!bindDataset.value || !Array.isArray(getValidColumnList.value)) return;
  Dialog.show<ANY_OBJECT>(
    '显示字段',
    EditColumnBind,
    {
      area: ['600px'],
    },
    {
      dataset: {
        datasetType: bindDataset.value?.datasetType,
      },
      rowData: row,
      columnList: buildColumnList(datasetInfo.value.imageName),
      path: 'thirdEditWidgetColumnBind/carouselName',
    },
    {
      width: '600px',
      height: '500px',
      pathName: '/thirdParty/thirdEditWidgetColumnBind',
    },
  )
    .then(res => {
      handlerEditCarouselName(row, res);
    })
    .catch((e: Error) => {
      console.log(e);
    });
};
const handlerEditRichTextHtml = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  datasetInfo.value.textColumn = [res];
};
const onEditRichTextColumn = (row: ANY_OBJECT | null = null) => {
  if (!bindDataset.value || !Array.isArray(getValidColumnList.value)) return;
  Dialog.show<ANY_OBJECT>(
    '内容字段',
    EditColumnBind,
    {
      area: ['600px'],
    },
    {
      dataset: {
        datasetType: bindDataset.value?.datasetType,
      },
      rowData: row,
      columnList: buildColumnList(datasetInfo.value.textColumn),
      path: 'thirdEditWidgetColumnBind/richHtml',
    },
    {
      width: '600px',
      height: '500px',
      pathName: '/thirdParty/thirdEditWidgetColumnBind',
    },
  )
    .then(res => {
      handlerEditRichTextHtml(row, res);
    })
    .catch((e: Error) => {
      console.log(e);
    });
};
// 编辑排序字段
const handlerWidgetOrder = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  if (row == null) {
    datasetInfo.value.orderInfoList.push({
      ...res,
      columnId: res.orderColumnId || res.columnId,
    });
  } else {
    datasetInfo.value.orderInfoList = datasetInfo.value.orderInfoList.map((item: ANY_OBJECT) => {
      if (item.id === res.id) {
        return res;
      } else {
        return item;
      }
    });
  }
};
const onEditWidgetOrder = (row: ANY_OBJECT | null = null) => {
  Dialog.show<ANY_OBJECT>(
    '排序',
    EditWidgetOrderInfo,
    {
      area: ['400px'],
    },
    {
      rowData: row,
      columnList: getOrderColumnList.value,
      path: 'thirdEditWidgetOrderInfo',
    },
    {
      width: '400px',
      height: '400px',
      pathName: '/thirdParty/thirdEditWidgetOrderInfo',
    },
  )
    .then(res => {
      handlerWidgetOrder(row, res);
    })
    .catch((e: Error) => {
      console.log(e);
    });
};
// 编辑过滤字段
const handlerFilterColumn = (row: ANY_OBJECT | null, res: ANY_OBJECT) => {
  if (row == null) {
    // 新增过滤
    datasetInfo.value.filterList.push({
      paramId: res.paramId,
      required: res.required,
      paramName: res.paramName,
      filterType: res.filterType,
      filterValueType: res.filterValueType,
      paramDictId: res.paramDictId,
      paramValue: res.paramValue,
      formWidgetId: res.formWidgetId,
      formWidgetColumn: res.formWidgetColumn,
      relationId: res.relationId,
    });
  } else {
    datasetInfo.value.filterList = datasetInfo.value.filterList.map((item: ANY_OBJECT) => {
      if (item.paramId === res.paramId) {
        return {
          paramId: res.paramId,
          required: res.required,
          paramName: res.paramName,
          filterType: res.filterType,
          filterValueType: res.filterValueType,
          paramDictId: res.paramDictId,
          paramValue: res.paramValue,
          formWidgetId: res.formWidgetId,
          formWidgetColumn: res.formWidgetColumn,
          relationId: res.relationId,
        };
      } else {
        return item;
      }
    });
  }
};
const onEditFilterColumn = (row: ANY_OBJECT | null = null) => {
  if (!bindDataset.value || !Array.isArray(getValidColumnList.value)) return;
  let temp = getValidColumnList.value
    .map((item: ANY_OBJECT) => {
      return {
        ...item,
        id: item.columnName,
        name: item.columnName,
        disabled:
          datasetInfo.value.filterList
            .map((item: ANY_OBJECT) => item.columnId)
            .indexOf(item.columnId) !== -1,
      };
    })
    .sort((val1, val2) => {
      return val1.disabled ? (val2.disabled ? 0 : 1) : -1;
    });
  Dialog.show<ANY_OBJECT>(
    '过滤器',
    EditReportColumnFilter,
    {
      area: ['600px'],
    },
    {
      rowData: row,
      columnList: temp,
      widgetList: formConfig()
        .allWidgetList.filter((item: ANY_OBJECT) => {
          return item.widgetId !== widget.value.widgetId;
        })
        .map((widget: ANY_OBJECT) => {
          return {
            widgetId: widget.widgetId,
            showName: widget.showName,
            widgetType: widget.widgetType,
            children: (widget.children || []).map((item: ANY_OBJECT) => {
              return {
                id: item.id,
                showName: item.showName,
              };
            }),
          };
        }),
      formParamList: (formConfig().form.paramList || []).map(
        (item: ANY_OBJECT) => item.variableName,
      ),
      reportDictList: formConfig().dictList,
      validFilterValueType: [
        FilterValueKind.FORM_PARAM,
        FilterValueKind.WIDGET_DATA,
        FilterValueKind.DICT_DATA,
        FilterValueKind.COLUMN_DATA,
        FilterValueKind.INNER_VARIABLE,
        FilterValueKind.INPUT_DATA,
      ],
      path: 'thirdEditReportColumnFilter',
    },
    {
      width: '600px',
      height: '450px',
      pathName: '/thirdParty/thirdEditReportColumnFilter',
    },
  )
    .then(res => {
      handlerFilterColumn(row, res);
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
const handlerDatasetParamList = (res: ANY_OBJECT) => {
  datasetInfo.value.datasetFilterParams = (res || []).map((item: ANY_OBJECT) => {
    return {
      paramName: item.paramName,
      filterValueType: item.filterValueType,
      paramValue: item.paramValue,
      formWidgetId: item.formWidgetId,
    };
  });
};
const onSetDatasetParamList = () => {
  Dialog.show<ANY_OBJECT>(
    '数据集参数',
    SetReportDatasetParam,
    {
      area: ['900px', '605px'],
    },
    {
      datasetParamList: (bindDataset.value || {}).datasetParamList,
      datasetFilterParams: datasetInfo.value.datasetFilterParams,
      formParamList: (formConfig().form.paramList || []).map(
        (item: ANY_OBJECT) => item.variableName,
      ),
      widgetList: (formConfig().allWidgetList || []).map((widget: ANY_OBJECT) => {
        return {
          widgetId: widget.widgetId,
          showName: widget.showName,
          widgetType: widget.widgetType,
        };
      }),
      validFilterValueType: [
        FilterValueKind.FORM_PARAM,
        FilterValueKind.WIDGET_DATA,
        FilterValueKind.INPUT_DATA,
      ],
      path: 'thirdSetDatasetPaam',
    },
    {
      width: '900px',
      height: '650px',
      pathName: '/thirdParty/thirdSetDatasetPaam',
    },
  )
    .then(res => {
      handlerDatasetParamList(res);
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
const onRemoveDatasetParam = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除此数据集参数？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      datasetInfo.value.datasetFilterParams = datasetInfo.value.datasetFilterParams.filter(
        (item: ANY_OBJECT) => {
          return item.paramName !== row.paramName;
        },
      );
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
const onRemoveSelectedColumn = (row: ANY_OBJECT, columnList: ANY_OBJECT[]) => {
  ElMessageBox.confirm('是否删除显示列？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      let delIndex = columnList.findIndex(x => x.columnId === row.columnId);
      columnList.splice(delIndex, 1);
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
// 删除类别轴字段
const onRemoveCategroyColumn = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除类别轴字段？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      datasetInfo.value.categroyColumnList = datasetInfo.value.categroyColumnList.filter(
        (item: ANY_OBJECT) => {
          return item.columnId !== row.columnId;
        },
      );
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
// 移除数值轴字段
const onRemoveIndexColumn = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除值轴字段？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      datasetInfo.value.valueColumnList = datasetInfo.value.valueColumnList.filter(
        (item: ANY_OBJECT) => {
          return item.columnId !== row.columnId || item.calculateType !== row.calculateType;
        },
      );
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};

// 移除排序字段
const onRemoveWidgetOrder = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除排序字段？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      datasetInfo.value.orderInfoList = datasetInfo.value.orderInfoList.filter(
        (item: ANY_OBJECT) => {
          return item.id !== row.id;
        },
      );
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
const getOrderColumnName = (columnId: string) => {
  let temp = findItemFromList(getOrderColumnList.value, columnId, 'id');
  return temp ? temp.showName : undefined;
};

// 移除过滤字段
const onRemoveFilterColumn = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除过滤字段？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      datasetInfo.value.filterList = datasetInfo.value.filterList.filter((item: ANY_OBJECT) => {
        return item.paramId !== row.paramId;
      });
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
// 删除行分组字段
const onRemoveRowGroupColumn = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除行分组字段？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      datasetInfo.value.rowGroupColumnList = datasetInfo.value.rowGroupColumnList.filter(
        (item: ANY_OBJECT) => {
          return item.columnId !== row.columnId;
        },
      );
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
// 删除列分组字段
const onRemoveColumnGroupColumn = (row: ANY_OBJECT) => {
  ElMessageBox.confirm('是否删除列分组字段？', '', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      datasetInfo.value.columnGroupColumnList = datasetInfo.value.columnGroupColumnList.filter(
        (item: ANY_OBJECT) => {
          return item.columnId !== row.columnId;
        },
      );
    })
    .catch((e: Error) => {
      console.warn(e);
    });
};
const getFilterShowName = (filterItem: ANY_OBJECT) => {
  if (filterItem.filterValueType === FilterValueKind.INNER_VARIABLE) {
    return CustomDateValueType.getValue(filterItem.paramValue);
  } else if (filterItem.filterValueType === FilterValueKind.WIDGET_DATA) {
    let widget = findItemFromList(formConfig().allWidgetList, filterItem.formWidgetId, 'widgetId');
    if (widget != null) {
      let column = null;
      if (Array.isArray(widget.children) && widget.children.length > 0) {
        column = findItemFromList(widget.children, filterItem.formWidgetColumn, 'id');
      }

      return widget.showName + (column == null ? '' : ` / ${column.showName}`);
    }
  } else {
    return filterItem.paramValue;
  }
};

const onBindColumnChange = () => {
  datasetInfo.value.datasetFilterParams = [];
  datasetInfo.value.categroyColumnList = [];
  datasetInfo.value.valueColumnList = [];
  datasetInfo.value.filterList = [];
  datasetInfo.value.orderInfoList = [];
  datasetInfo.value.rowGroupColumnList = [];
  datasetInfo.value.columnGroupColumnList = [];

  if (datasetInfo.value.showColumnList) datasetInfo.value.showColumnList = [];
  if (datasetInfo.value.imagePath) datasetInfo.value.imagePath = [];
  if (datasetInfo.value.imageName) datasetInfo.value.imageName = [];
  if (datasetInfo.value.textColumn) datasetInfo.value.textColumn = [];
  if (datasetInfo.value.mainTextColumn) datasetInfo.value.mainTextColumn = [];
  if (datasetInfo.value.numTextColumn) datasetInfo.value.numTextColumn = [];
  if (datasetInfo.value.footTextColumn) datasetInfo.value.footTextColumn = [];
  if (datasetInfo.value.footNumTextColumn) datasetInfo.value.footNumTextColumn = [];
  if (datasetInfo.value.progressColumn) datasetInfo.value.progressColumn = [];
  if (datasetInfo.value.titleColumn) datasetInfo.value.titleColumn = [];
  if (datasetInfo.value.timeColumn) datasetInfo.value.timeColumn = [];
};
const onDatasetChange = (value: CascaderValue) => {
  console.log('----- 数据集变化 ---------', value);
  datasetInfo.value.datasetId = Array.isArray(value) ? value[value.length - 1] : undefined;
  datasetInfo.value.bindColumnId = undefined;
  onBindColumnChange();
};

const loadReportDatasetColumnList = (datasetId: string | null) => {
  return new Promise((resolve, reject) => {
    if (!datasetId) {
      reject();
      return;
    }
    if (!bindDataset.value || bindDataset.value?.columnList != null) {
      reject();
      return;
    }
    formConfig()
      .loadDatasetInfo(datasetId)
      .then((datasetInfo: ANY_OBJECT) => {
        const val = { ...bindDataset.value };
        val.columnList = datasetInfo.columnList;
        val.datasetParamList = datasetInfo.datasetParamList;
        bindDataset.value = val;
        resolve(datasetInfo);
      })
      .catch((e: Error) => {
        reject(e);
      });
  });
};

watch(
  () => datasetInfo.value,
  () => {
    //console.log(' ---------datasetInfo change--------------- ');
    let dataset: ANY_OBJECT | undefined = undefined;
    if (datasetInfo.value && datasetInfo.value.datasetId) {
      dataset =
        findItemFromList(formConfig().datasetList, datasetInfo.value.datasetId, 'datasetId') ||
        undefined;
    }
    bindDataset.value = dataset;
    widget.value && (widget.value.dataset = dataset);
    loadReportDatasetColumnList((dataset || {}).datasetId).catch(e => {
      console.warn(e);
    });
  },
  {
    deep: true,
    immediate: true,
  },
);

const setPropertyFieldValue = (fieldName: string, value: ANY_OBJECT | null) => {
  if (!fieldName && !value) {
    if (Array.isArray(datasetInfo.value[fieldName])) {
      datasetInfo.value[fieldName] = [value];
    } else {
      datasetInfo.value[fieldName] = value;
    }
  }
};
const handlerPropertyFieldBind = (data: ANY_OBJECT) => {
  let key = data.path.split('/')[1];
  let value = data.data
    ? {
        columnId: data.data.columnId,
        columnName: data.data.columnName,
      }
    : null;
  if (key === 'dataCardTitle') {
    setPropertyFieldValue('mainTextColumn', value);
  } else if (key === 'dataCardContent') {
    setPropertyFieldValue('numTextColumn', value);
  } else if (key === 'dataCardLeftText') {
    setPropertyFieldValue('footTextColumn', value);
  } else if (key === 'dataCardRightText') {
    setPropertyFieldValue('footNumTextColumn', value);
  } else if (key === 'progressCardTitle') {
    setPropertyFieldValue('mainTextColumn', value);
  } else if (key === 'progressCardContent') {
    setPropertyFieldValue('numTextColumn', value);
  } else if (key === 'progressCardDesc') {
    setPropertyFieldValue('textColumn', value);
  } else if (key === 'commonListTitle') {
    setPropertyFieldValue('titleColumn', value);
  } else if (key === 'commonListContent') {
    setPropertyFieldValue('textColumn', value);
  } else if (key === 'commonListRight') {
    setPropertyFieldValue('timeColumn', value);
  } else if (key === 'progressColumn') {
    setPropertyFieldValue('progressColumn', value);
  }
};
</script>
