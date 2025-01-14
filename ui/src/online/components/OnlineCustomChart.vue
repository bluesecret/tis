<template>
  <el-card
    class="custom-chart"
    :body-style="getBodyStyle"
    :shadow="widget.widgetType === SysCustomWidgetType.Carousel ? 'never' : 'always'"
    :style="{ border: widget.widgetType === SysCustomWidgetType.Carousel ? 'none' : undefined }"
    @click="onClickWidget()"
  >
    <LineChart
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.LineChart"
      :value="value"
      @input="onValueChange"
      :style="{ height: '100%' }"
      :data="chartData"
      :options="getChartOptions"
      :categrayColumnList="categrayColumnList"
      :valueColumnList="valueColumnList"
      @click="onClickWidget"
      @dblclick="onDblClick"
    />
    <BarChart
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.BarChart"
      :value="value"
      @input="onValueChange"
      :style="{ height: '100%' }"
      :data="chartData"
      :options="getChartOptions"
      :categrayColumnList="categrayColumnList"
      :valueColumnList="valueColumnList"
      @click="onClickWidget()"
      @dblclick="onDblClick"
    />
    <PieChart
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.PieChart"
      :value="value"
      @input="onValueChange"
      :style="{ height: '100%' }"
      :data="chartData"
      :options="getChartOptions"
      :categrayColumnList="categrayColumnList"
      :valueColumnList="valueColumnList"
      @click="onClickWidget()"
      @dblclick="onDblClick"
    />
    <ScatterCharts
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.ScatterChart"
      :value="value"
      @input="onValueChange"
      :style="{ height: '100%' }"
      :data="chartData"
      :options="getChartOptions"
      :categrayColumnList="categrayColumnList"
      :valueColumnList="valueColumnList"
      @click="onClickWidget()"
      @dblclick="onDblClick"
    />
    <PivotTable
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.PivotTable"
      :data="chartData"
      :height="(widget || {}).props.basicInfo.height"
      :options="pivotTableOptions"
      :rowGroupColumnList="rowGroupColumnList"
      :columnGroupColumnList="columnGroupColumnList"
      :valueColumnList="valueColumnList"
      @click="onClickWidget()"
    />
    <DataViewTable
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.DataViewTable"
      :data="chartData"
      :height="(widget || {}).props.basicInfo.height"
      :options="dataViewTableOptions"
      :columnList="dataViewColumnList"
      @click="onClickWidget()"
      @refresh="refreshDataViewTable"
    />
    <CarouselChart
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.Carousel"
      :data="chartData"
      :height="(widget || {}).props.basicInfo.height"
      :options="dataViewTableOptions"
      :imagePath="imagePath"
      :imageName="imageName"
      @click="onClickWidget()"
    />
    <RichText
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.RichText"
      :data="chartData"
      :height="(widget || {}).props.basicInfo.height"
      :options="dataViewTableOptions"
      @click="onClickWidget()"
    />
    <GaugeChart
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.GaugeChart"
      :value="value"
      @input="onValueChange"
      :style="{ height: '100%' }"
      :data="chartData"
      :options="getChartOptions"
      :categrayColumnList="categrayColumnList"
      :valueColumnList="valueColumnList"
      @click="onClickWidget"
      @dblclick="onDblClick"
    />
    <FunnelChart
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.FunnelChart"
      :value="value"
      @input="onValueChange"
      :style="{ height: '100%' }"
      :data="chartData"
      :options="getChartOptions"
      :categrayColumnList="categrayColumnList"
      :valueColumnList="valueColumnList"
      @click="onClickWidget"
      @dblclick="onDblClick"
    />
    <RadarChart
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.RadarChart"
      :value="value"
      @input="onValueChange"
      :style="{ height: '100%' }"
      :data="chartData"
      :options="getChartOptions"
      :categrayColumnList="categrayColumnList"
      :valueColumnList="valueColumnList"
      @click="onClickWidget"
      @dblclick="onDblClick"
    />
    <ProgressBar
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.ProgressBar"
      :data="chartData"
      :height="(widget || {}).props.basicInfo.height"
      :options="dataViewTableOptions"
      @click="onClickWidget()"
      @dblclick="onDblClick"
    />
    <ProgressCircle
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.ProgressCircle"
      :data="chartData"
      :height="(widget || {}).props.basicInfo.height"
      :options="dataViewTableOptions"
      @click="onClickWidget()"
      @dblclick="onDblClick"
    />
    <DataCard
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.DataCard"
      :data="chartData"
      :height="(widget || {}).props.basicInfo.height"
      :options="dataViewTableOptions"
      @click="onClickWidget()"
      @dblclick="onDblClick"
    />
    <DataProgressCard
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.DataProgressCard"
      :data="chartData"
      :height="(widget || {}).props.basicInfo.height"
      :options="dataViewTableOptions"
      @click="onClickWidget()"
      @dblclick="onDblClick"
    />
    <CommonList
      ref="innerObj"
      v-if="widget.widgetType === SysCustomWidgetType.CommonList"
      :data="chartData"
      :height="(widget || {}).props.basicInfo.height"
      :options="dataViewTableOptions"
      @click="onClickWidget()"
      @dblclick="onDblClick"
    />
    <el-icon
      v-if="widget.widgetType !== SysCustomWidgetType.Carousel"
      class="refresh-btn"
      @click.stop="refresh"
      ><Refresh
    /></el-icon>
  </el-card>
</template>

<script setup lang="ts">
import { Refresh } from '@element-plus/icons-vue';
import { ANY_OBJECT } from '@/types/generic';
import { SysCustomWidgetType } from '@/common/staticDict';
import LineChart from '@/components/Charts/lineChart.vue';
import BarChart from '@/components/Charts/barChart.vue';
import CarouselChart from '@/components/Charts/carouselChart.vue';
import ScatterCharts from '@/components/Charts/scatterChart.vue';
import DataProgressCard from '@/components/Charts/dataProgressCard.vue';
import CommonList from '@/components/Charts/commonList.vue';
import DataCard from '@/components/Charts/dataCard.vue';
import ProgressCircle from '@/components/Charts/progressCircle.vue';
import { DatasetType, OrderType } from '@/common/staticDict/report';
import { findItemFromList, findTreeNodeObjectPath, treeDataTranslate } from '@/common/utils';
import { getValueColumnName } from '@/components/Charts/utils';
import PieChart from '@/components/Charts/pieChart.vue';
import PivotTable from '@/components/Charts/pivotTable.vue';
import DataViewTable from '@/components/Charts/dataViewTable.vue';
import RichText from '@/components/Charts/richText.vue';
import GaugeChart from '@/components/Charts/gaugeChart.vue';
import FunnelChart from '@/components/Charts/funnelChart.vue';
import RadarChart from '@/components/Charts/radarChart.vue';
import ProgressBar from '@/components/Charts/progressBar.vue';

const props = withDefaults(
  defineProps<{
    value?: number | ANY_OBJECT[] | string | Date | ANY_OBJECT;
    isEdit?: boolean;
    widget: ANY_OBJECT;
  }>(),
  {
    isEdit: false,
  },
);

const innerObj = ref();
const chartData = ref<ANY_OBJECT[]>([]);
let loadTimer: number | undefined | null = undefined;
const categrayColumnList = ref<ANY_OBJECT[]>([]);
const valueColumnList = ref<ANY_OBJECT[]>([]);
// // 透视表
const pivotTableOptions = ref<ANY_OBJECT>();
const dataViewTableOptions = ref<ANY_OBJECT>();
const rowGroupColumnList = ref<ANY_OBJECT[]>([]);
const columnGroupColumnList = ref<ANY_OBJECT[]>([]);
const dataViewColumnList = ref<ANY_OBJECT[]>([]);
const imagePath = ref<ANY_OBJECT[]>([]);
const imageName = ref<ANY_OBJECT[]>([]);
const textColumn = ref<ANY_OBJECT[]>([]);
let refreshTimer: number | undefined | null = undefined;

const form = inject('form', () => {
  console.error('OnlineCustomChart: form not injected');
  return { isEdit: false } as ANY_OBJECT;
});

const emit = defineEmits<{
  widgetClick: [ANY_OBJECT | null];
  'update:value': [number | ANY_OBJECT[] | string | Date | ANY_OBJECT];
}>();

const onClickWidget = () => {
  emit('widgetClick', props.widget);
};
// 构建读取列表数据的参数
const getLoadListDataParams = (pageParam: ANY_OBJECT | null) => {
  if (props.widget == null || props.widget.dataset == null) {
    return null;
  }
  let tempPageParams =
    pageParam ||
    (props.widget.props.pagerSetting == null || props.widget.props.pagerSetting.show
      ? props.widget.props.datasetInfo.pageParam
      : undefined);
  return {
    datasetId: props.widget.dataset.datasetId,
    orderDataList: props.widget.props.datasetInfo.orderInfoList,
    pageParam: pageParam,
    datasetFilterParams: (props.widget.props.datasetInfo.datasetFilterParams || [])
      .map((filter: ANY_OBJECT) => {
        return form().getQueryParam(filter);
      })
      .filter((item: ANY_OBJECT) => item.paramValue != null),
    filterParams: Array.isArray(props.widget.props.datasetInfo.filterList)
      ? props.widget.props.datasetInfo.filterList
          .map((filter: ANY_OBJECT) => {
            return form().getQueryParam(filter);
          })
          .filter((item: ANY_OBJECT) => item.paramValue != null)
      : [],
  };
};
// 读取列表数据
const loadListData = (pageParam: ANY_OBJECT | null = null) => {
  if (loadTimer) clearTimeout(loadTimer);
  loadTimer = setTimeout(() => {
    let params = getLoadListDataParams(pageParam);
    if (params != null && props.widget.dataset != null) {
      form()
        .listDataWithGroup(params)
        .then((res: ANY_OBJECT[]) => {
          chartData.value = res;
          loadTimer = null;
        })
        .catch((e: Error) => {
          console.warn(e);
          loadTimer = null;
        });
    } else {
      chartData.value = [];
    }
  }, 50);
};
const refreshDataViewTable = () => {
  nextTick(() => {
    loadListData(dataViewTableOptions.value?.datasetInfo.pageParam);
  });
};
const refresh = () => {
  if (refreshTimer != null) clearTimeout(refreshTimer);
  refreshTimer = setTimeout(() => {
    if (innerObj.value && typeof innerObj.value.resize === 'function') {
      innerObj.value.resize();
    }

    if (
      props.widget.widgetType !== SysCustomWidgetType.DataViewTable &&
      props.widget.widgetType !== SysCustomWidgetType.ProgressBar
    ) {
      nextTick(loadChartData);
    } else {
      nextTick(loadListData);
    }
    refreshTimer = null;
  }, 200);
};
const onDblClick = (data: ANY_OBJECT) => {
  form().onChartDblClick(props.widget, data);
};
const loadChartData = () => {
  if (loadTimer != null) clearTimeout(loadTimer);
  loadTimer = setTimeout(() => {
    let params = getLoadChartParams();
    if (params != null && props.widget.dataset != null) {
      form()
        .listDataWithGroup(params)
        .then((res: ANY_OBJECT) => {
          let data = res.dataList;
          let columnPath = null;
          if (props.widget.dataset.datasetType === DatasetType.API) {
            columnPath = getColumnPath(props.widget.props.datasetInfo.bindColumnId);
          }
          if (Array.isArray(columnPath) && columnPath.length > 0) {
            let rootColumn = columnPath[0];
            let rootData = rootColumn && rootColumn.fieldType === 'Object' ? data[0] : data;
            if (columnPath.length > 1) {
              for (let i = 1; i < columnPath.length; i++) {
                let tempColumn = columnPath[i];
                rootData = rootData[tempColumn.columnName];
                if (rootData == null) break;
              }
            }

            if (rootData != null) {
              if (props.widget.widgetType === SysCustomWidgetType.DataViewTable) {
                chartData.value = res as ANY_OBJECT[];
              } else {
                chartData.value = rootData;
              }
            }
          } else {
            chartData.value = data;
          }
          loadTimer = null;
        })
        .catch((e: Error) => {
          console.error(e);
          loadTimer = null;
        });
    } else {
      chartData.value = [];
    }
  }, 50);
};
const onValueChange = (value: ANY_OBJECT) => {
  emit('update:value', value);
};
const cloneObject = (obj: ANY_OBJECT | undefined) => {
  if (obj == null) return undefined;
  return JSON.parse(JSON.stringify(obj));
};
const getLoadChartParams = () => {
  if (
    // 不检查数值轴
    ![
      SysCustomWidgetType.DataViewTable,
      SysCustomWidgetType.Carousel,
      SysCustomWidgetType.RichText,
      SysCustomWidgetType.CommonList,
      SysCustomWidgetType.DataCard,
      SysCustomWidgetType.DataProgressCard,
      SysCustomWidgetType.ProgressBar,
      SysCustomWidgetType.ProgressCircle,
    ].find(x => x === props.widget.widgetType)
  ) {
    if (
      props.widget == null ||
      props.widget.dataset == null ||
      !Array.isArray(props.widget.props.datasetInfo.valueColumnList) ||
      props.widget.props.datasetInfo.valueColumnList.length === 0
    ) {
      return null;
    }
  }

  let categroyColumnList = props.widget.props.datasetInfo.categroyColumnList;
  if (props.widget.widgetType === SysCustomWidgetType.PivotTable) {
    if (
      !Array.isArray(props.widget.props.datasetInfo.rowGroupColumnList) ||
      props.widget.props.datasetInfo.rowGroupColumnList.length === 0
    ) {
      return null;
    }
    categroyColumnList = (props.widget.props.datasetInfo.rowGroupColumnList || []).concat(
      props.widget.props.datasetInfo.columnGroupColumnList || [],
    );
  }

  if (
    // 这三个组件不检查类别轴 / 维度
    ![
      SysCustomWidgetType.DataViewTable,
      SysCustomWidgetType.Carousel,
      SysCustomWidgetType.RichText,
      SysCustomWidgetType.CommonList,
      SysCustomWidgetType.DataCard,
      SysCustomWidgetType.DataProgressCard,
      SysCustomWidgetType.ProgressBar,
      SysCustomWidgetType.ProgressCircle,
    ].find(x => x === props.widget.widgetType)
  ) {
    if (!Array.isArray(categroyColumnList) || categroyColumnList.length === 0) {
      return null;
    }
  }

  let orderList: ANY_OBJECT[] = [];
  if (props.widget.widgetType === SysCustomWidgetType.PivotTable) {
    if (Array.isArray(props.widget.props.datasetInfo.rowGroupColumnList)) {
      props.widget.props.datasetInfo.rowGroupColumnList.forEach((item: ANY_OBJECT) => {
        if (item.columnId) {
          orderList.push({
            columnId: item.columnId,
            orderType: OrderType.ASC,
          });
        }
      });
    }
    if (Array.isArray(props.widget.props.datasetInfo.columnGroupColumnList)) {
      props.widget.props.datasetInfo.columnGroupColumnList.forEach((item: ANY_OBJECT) => {
        if (item.columnId) {
          orderList.push({
            columnId: item.columnId,
            orderType: OrderType.ASC,
          });
        }
      });
    }
  }
  if (
    props.widget.props.datasetInfo &&
    Array.isArray(props.widget.props.datasetInfo.orderInfoList)
  ) {
    props.widget.props.datasetInfo.orderInfoList.forEach((item: ANY_OBJECT) => {
      orderList.push({
        columnId: item.orderColumnId,
        calculateType: item.calculateType,
        orderType: item.orderType,
      });
    });
  }
  let isApi = (props.widget.dataset || {}).datasetType === DatasetType.API;
  return {
    datasetId: (props.widget.dataset || {}).datasetId,
    dimensionDataList: isApi
      ? []
      : (categroyColumnList || []).map((item: ANY_OBJECT) => {
          return {
            columnId: item.columnId,
          };
        }),
    indexDataList: isApi
      ? []
      : (props.widget.props.datasetInfo.valueColumnList || []).map((item: ANY_OBJECT) => {
          return {
            columnId: item.columnId,
            calculateType: item.calculateType,
            filterParams: Array.isArray(item.filterList)
              ? item.filterList
                  .map((filter: ANY_OBJECT) => {
                    return form().getQueryParam(filter);
                  })
                  .filter((item: ANY_OBJECT) => item.paramValue != null)
              : [],
          };
        }),
    orderDataList: isApi ? [] : orderList,
    filterParams: isApi
      ? []
      : Array.isArray(props.widget.props.datasetInfo.filterList)
      ? props.widget.props.datasetInfo.filterList
          .map((filter: ANY_OBJECT) => {
            return form().getQueryParam(filter);
          })
          .filter((item: ANY_OBJECT) => item.paramValue != null)
      : [],
    datasetFilterParams: Array.isArray(props.widget.props.datasetInfo.datasetFilterParams)
      ? props.widget.props.datasetInfo.datasetFilterParams
          .map((filter: ANY_OBJECT) => {
            return form().getQueryParam(filter);
          })
          .filter((item: ANY_OBJECT) => item.paramValue != null)
      : [],
  };
};
const getChartCategrayColumnList = () => {
  if (
    props.widget == null ||
    props.widget.dataset == null ||
    props.widget.props.datasetInfo.categroyColumnList == null ||
    props.widget.props.datasetInfo.categroyColumnList.length === 0
  ) {
    return [];
  }
  return props.widget.props.datasetInfo.categroyColumnList.map((item: ANY_OBJECT) => {
    let columnName = getColumnName(item.columnId);
    return {
      columnName: columnName != null ? columnName : undefined,
    };
  });
};
const getChartValueColumnList = () => {
  if (
    props.widget == null ||
    props.widget.dataset == null ||
    props.widget.props.datasetInfo.valueColumnList == null ||
    props.widget.props.datasetInfo.valueColumnList.length === 0
  ) {
    return [];
  }
  return props.widget.props.datasetInfo.valueColumnList
    .map((item: ANY_OBJECT) => {
      let calculateName = getColumnName(item.columnId, item.calculateType);
      if (calculateName == null) return null;
      return {
        columnName: calculateName,
        name: item.showName,
        columnWidth: item.columnWidth,
        fixed: item.fixed,
      };
    })
    .filter((item: ANY_OBJECT) => item != null);
};
const getRowGroupColumnList = () => {
  console.log('getRowGroupColumnList >>>', props);
  if (
    props.widget == null ||
    props.widget.dataset == null ||
    props.widget.props.datasetInfo.rowGroupColumnList == null ||
    props.widget.props.datasetInfo.rowGroupColumnList.length === 0
  ) {
    return rowGroupColumnList.value || [];
  }
  const list = props.widget.props.datasetInfo.rowGroupColumnList
    .map((item: ANY_OBJECT) => {
      let columnName = getColumnName(item.columnId);
      console.log('getRowGroupColumnList columnName >>>', columnName, item);
      if (columnName == null) return null;
      return {
        columnName: columnName,
        columnWidth: item.columnWidth,
        showName: item.showName,
      };
    })
    .filter((item: ANY_OBJECT) => item != null);
  console.log('getRowGroupColumnList rowGroupColumnList >>>', list);
  return list;
};
const getDataViewColumnList = () => {
  if (
    props.widget == null ||
    props.widget.dataset == null ||
    props.widget.props.datasetInfo.showColumnList == null ||
    props.widget.props.datasetInfo.showColumnList.length === 0
  ) {
    return [];
  }
  return props.widget.props.datasetInfo.showColumnList
    .map((item: ANY_OBJECT) => {
      let columnName = getColumnName(item.columnId);
      if (columnName == null) return null;
      return {
        ...item,
        columnName: columnName,
      };
    })
    .filter((item: ANY_OBJECT) => item != null);
};
const getColumnGroupColumnList = () => {
  if (
    props.widget == null ||
    props.widget.dataset == null ||
    props.widget.props.datasetInfo.columnGroupColumnList == null ||
    props.widget.props.datasetInfo.columnGroupColumnList.length === 0
  ) {
    return [];
  }
  return props.widget.props.datasetInfo.columnGroupColumnList
    .map((item: ANY_OBJECT) => {
      let columnName = getColumnName(item.columnId);
      if (columnName == null) return null;
      return {
        columnName: columnName,
        columnWidth: item.columnWidth,
        showName: item.showName,
      };
    })
    .filter((item: ANY_OBJECT) => item != null);
};
const getColumnPath = (columnId: string, bindColumnId: string | null = null) => {
  let columnList = props.widget.dataset.columnList || props.widget.columnList;
  if (props.widget == null || props.widget.dataset == null || !Array.isArray(columnList)) return [];
  let columnTree = treeDataTranslate(
    columnList.map((column: ANY_OBJECT) => {
      return {
        ...column,
        children: undefined,
      };
    }),
    'columnId',
    'parentId',
  );
  let tempPath = findTreeNodeObjectPath(columnTree, columnId, 'columnId');
  let columnPath: ANY_OBJECT[] = [];
  let bFindBindColumn = false;
  tempPath.forEach(column => {
    if (bFindBindColumn || bindColumnId == null) {
      columnPath.push(column);
    }
    if (!bFindBindColumn && column.columnId === bindColumnId) bFindBindColumn = true;
  });
  return columnPath;
};
const getColumnName = (columnId: string, calculateType: number | null = null) => {
  let columnList = props.widget.dataset.columnList || props.widget.columnList;
  if (props.widget == null || props.widget.dataset == null || !Array.isArray(columnList))
    return null;
  let isApi = props.widget.dataset.datasetType === DatasetType.API;
  if (isApi) {
    let columnPath = getColumnPath(columnId, props.widget.props.datasetInfo.bindColumnId);
    if (Array.isArray(columnPath)) {
      return columnPath.map(column => column.columnName);
    }
  } else {
    let column = findItemFromList(columnList, columnId, 'columnId');
    if (column != null)
      return calculateType == null
        ? column.columnName
        : getValueColumnName(calculateType, column.columnName);
  }
  return null;
};
const getPivotTableConfig = () => {
  if (props.widget.widgetType !== SysCustomWidgetType.PivotTable) return;

  pivotTableOptions.value = {
    // 基础设置
    borderColor: props.widget.props.basicInfo.borderColor,
    cellBackgroundColor: props.widget.props.basicInfo.cellBackgroundColor,
    cellFontColor: props.widget.props.basicInfo.cellFontColor,
    cellFontSize: props.widget.props.basicInfo.cellFontSize,
    cellAlign: props.widget.props.basicInfo.cellAlign,
    // 列分组设置
    headerCellBackgroundColor: props.widget.props.columnGroupSetting.backgroundColor,
    headerCellFontColor: props.widget.props.columnGroupSetting.fontColor,
    headerCellFontSize: props.widget.props.columnGroupSetting.fontSize,
    headerRowHeight: props.widget.props.columnGroupSetting.headerRowHeight,
    headerAlign: props.widget.props.columnGroupSetting.align,
    // 行分组设置
    rowGroupBackgroundColor: props.widget.props.rowGroupSetting.backgroundColor,
    rowGroupFontColor: props.widget.props.rowGroupSetting.fontColor,
    rowGroupFontSize: props.widget.props.rowGroupSetting.fontSize,
    rowGroupAlign: props.widget.props.rowGroupSetting.align,
    cellRowHeight: props.widget.props.basicInfo.cellRowHeight,
    // 总计设置
    totalSetting: {
      row: {
        show: props.widget.props.rowTotalSetting.show,
        showName: props.widget.props.rowTotalSetting.showName,
        position: props.widget.props.rowTotalSetting.position,
      },
      col: {
        show: props.widget.props.colTotalSetting.show,
        showName: props.widget.props.colTotalSetting.showName,
        position: props.widget.props.colTotalSetting.position,
      },
    },
    // 小计设置
    subTotalSetting: {
      row: {
        show: props.widget.props.rowSubTotalSetting.show,
        showName: props.widget.props.rowSubTotalSetting.showName,
        position: props.widget.props.rowSubTotalSetting.position,
      },
      col: {
        show: props.widget.props.colSubTotalSetting.show,
        showName: props.widget.props.colSubTotalSetting.showName,
        position: props.widget.props.colSubTotalSetting.position,
      },
    },
    // 标题设置
    title: JSON.parse(JSON.stringify(props.widget.props.titleSetting)),
    // 单元格规则设置
    cellRule: {
      // 单元格阈值规则
      cellThreshold: props.widget.props.cellRule,
    },
  };
};
const buildDatasetInfo = (datasetInfo: ANY_OBJECT) => {
  let cardDatasetColumnFieldNameList = [
    'mainTextColumn',
    'numTextColumn',
    'footTextColumn',
    'footNumTextColumn',
    'textColumn',
    'titleColumn',
    'timeColumn',
    'progressColumn',
  ];
  let temp = JSON.parse(JSON.stringify(datasetInfo));
  cardDatasetColumnFieldNameList.forEach(fieldName => {
    if (temp[fieldName] != null) {
      if (Array.isArray(temp[fieldName])) {
        temp[fieldName].forEach((item: ANY_OBJECT) => {
          // TODO 点击卡片之后，取不到columnName，暂时先用原来的值
          const columnName = getColumnName(item.columnId);
          if (Array.isArray(columnName) && columnName.length > 0) {
            item.columnName = getColumnName(item.columnId) || item.columnName;
          }
          //item.columnName = getColumnName(item.columnId);
        });
      } else {
        temp[fieldName].columnName = getColumnName(temp[fieldName].columnId);
      }
    }
  });
  return temp;
};
const getDataViewTableConfig = () => {
  const useDataViewTableConfig = [
    SysCustomWidgetType.DataViewTable,
    SysCustomWidgetType.Carousel,
    SysCustomWidgetType.RichText,
    SysCustomWidgetType.GaugeChart,
    SysCustomWidgetType.FunnelChart,
    SysCustomWidgetType.RadarChart,
    SysCustomWidgetType.ProgressBar,
    SysCustomWidgetType.ProgressCircle,
    SysCustomWidgetType.DataCard,
    SysCustomWidgetType.DataProgressCard,
    SysCustomWidgetType.CommonList,
  ];
  if (!useDataViewTableConfig.find(x => x === props.widget.widgetType)) return;

  dataViewTableOptions.value = {
    datasetInfo: buildDatasetInfo(props.widget.props.datasetInfo),
    basicInfo: props.widget.props.basicInfo,
    // 基础设置
    borderColor: props.widget.props.basicInfo.borderColor,
    cellAlign: props.widget.props.basicInfo.cellAlign,
    cellRowHeight: props.widget.props.basicInfo.cellRowHeight,
    rowSetting: props.widget.props.rowSetting,
    columnSetting: props.widget.props.columnSetting,
    pagerSetting: props.widget.props.pagerSetting,
    seriesSetting: props.widget.props.seriesSetting,
    routeSetting: props.widget.props.routeSetting,
    pageParam: props.widget.props.datasetInfo.pageParam,
    // 标题设置
    title: props.widget.props.titleSetting
      ? JSON.parse(JSON.stringify(props.widget.props.titleSetting))
      : {},
    // 单元格规则设置
    cellRule: {
      // 单元格阈值规则
      cellThreshold: props.widget.props.cellRule,
    },
  };
};
const buildColumnInfo = (columnList: ANY_OBJECT[]) => {
  if (Array.isArray(columnList)) {
    return columnList.map((item: ANY_OBJECT) => {
      return {
        ...item,
        columnName: getColumnName(item.columnId),
      };
    });
  } else {
    return [];
  }
};

const getChartOptions = computed(() => {
  if (props.widget == null) return undefined;
  return {
    basic: {
      color: cloneObject(props.widget.props.basicInfo.chartColors),
      grid: props.widget.props.basicInfo.grid
        ? cloneObject(props.widget.props.basicInfo.grid)
        : undefined,
      label: cloneObject(props.widget.props.labelSetting),
      tooltip: cloneObject(props.widget.props.tooltipSetting),
      xAxis: props.widget.props.xAxisSetting
        ? cloneObject(props.widget.props.xAxisSetting)
        : undefined,
      yAxis: props.widget.props.yAxisSetting
        ? {
            ...cloneObject(props.widget.props.yAxisSetting),
            min:
              props.widget.props.yAxisSetting == null ||
              props.widget.props.yAxisSetting.yAxisValueAuto
                ? undefined
                : props.widget.props.yAxisSetting.min,
            max:
              props.widget.props.yAxisSetting == null ||
              props.widget.props.yAxisSetting.yAxisValueAuto
                ? undefined
                : props.widget.props.yAxisSetting.max,
            interval:
              props.widget.props.yAxisSetting == null ||
              props.widget.props.yAxisSetting.yAxisValueAuto
                ? undefined
                : props.widget.props.yAxisSetting.interval,
          }
        : undefined,
      title: {
        ...cloneObject(props.widget.props.titleSetting),
        textStyle: {
          ...cloneObject(props.widget.props.titleSetting.textStyle),
          fontStyle: props.widget.props.titleSetting.italics ? 'italic' : 'normal',
          fontWeight: props.widget.props.titleSetting.bold ? 'bold' : 'normal',
        },
      },
      legend: cloneObject(props.widget.props.legendSetting),
    },
    series: {
      ...cloneObject(props.widget.props.seriesSetting),
      barWidth: props.widget.props.seriesSetting.autoWidth
        ? undefined
        : props.widget.props.seriesSetting.barWidth,
    },
    radar: props.widget.props.radarSetting ? cloneObject(props.widget.props.radarSetting) : null,
    datasetInfo: props.widget.props.datasetInfo,
  };
});
const getBodyStyle = computed(() => {
  if (!props.widget) return {};
  if (
    props.widget.widgetType !== SysCustomWidgetType.PivotTable &&
    props.widget.widgetType !== SysCustomWidgetType.DataViewTable
  ) {
    return {
      padding: props.widget.widgetType === SysCustomWidgetType.Carousel ? '0px' : '15px',
      height: (props.widget || {}).props.basicInfo.height,
    };
  }
  return undefined;
});

watch(
  () => props.widget.dataset,
  () => {
    valueColumnList.value = getChartValueColumnList();
    categrayColumnList.value = getChartCategrayColumnList();
    rowGroupColumnList.value = getRowGroupColumnList();
    columnGroupColumnList.value = getColumnGroupColumnList();
    dataViewColumnList.value = getDataViewColumnList();
    imagePath.value = buildColumnInfo(props.widget.props.datasetInfo.imagePath);
    imageName.value = buildColumnInfo(props.widget.props.datasetInfo.imageName);
    textColumn.value = buildColumnInfo(props.widget.props.datasetInfo.textColumn);
    getPivotTableConfig();
    getDataViewTableConfig();
    refresh();
  },
  {
    deep: true,
    immediate: true,
  },
);
watch(
  () => props.widget.props,
  () => {
    valueColumnList.value = getChartValueColumnList();
    categrayColumnList.value = getChartCategrayColumnList();
    rowGroupColumnList.value = getRowGroupColumnList();
    columnGroupColumnList.value = getColumnGroupColumnList();
    dataViewColumnList.value = getDataViewColumnList();
    imagePath.value = buildColumnInfo(props.widget.props.datasetInfo.imagePath);
    imageName.value = buildColumnInfo(props.widget.props.datasetInfo.imageName);
    textColumn.value = buildColumnInfo(props.widget.props.datasetInfo.textColumn);
    getPivotTableConfig();
    getDataViewTableConfig();
    refresh();
  },
  {
    deep: true,
    immediate: true,
  },
);

onUnmounted(() => {
  if (refreshTimer) clearTimeout(refreshTimer);
  refreshTimer = null;
});

defineExpose({
  refresh,
  loadListData,
  refreshDataViewTable,
});
</script>

<style scoped>
.custom-chart {
  position: relative;
}

.refresh-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  display: none;
  width: 30px;
  height: 30px;
  font-size: 20px;
  color: #383838;
  cursor: pointer;
}

.custom-chart:hover .refresh-btn {
  display: block;
}
</style>
