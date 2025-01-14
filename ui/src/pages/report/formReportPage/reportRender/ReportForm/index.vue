<template>
  <div
    class="online-report-form"
    style="
      position: relative;
      height: 100%;
      min-height: 200px;
      padding: 24px;
      background-color: white;
      box-sizing: border-box;
    "
  >
    <template v-if="dialogParams.fullscreen">
      <el-container>
        <el-header style="height: 72px; background: white">
          <el-row
            type="flex"
            align="middle"
            style="justify-content: space-between; width: 100%; height: 100%"
          >
            <div style="display: flex; height: 40px; line-height: 40px">
              <i
                class="header-logo online-icon icon-orange-icon"
                style="font-size: 40px; color: #fda834; background: rgb(255 119 0 / 10%)"
              />
              <span style="margin-left: 15px; font-size: 22px; color: #333; font-weight: bold"
                >橙单</span
              >
              <el-divider class="divider" direction="vertical" />
              <span style="font-size: 18px; color: #333; font-weight: bold">{{
                dialogParams.formConfig.pageName
              }}</span>
            </div>
            <el-button
              link
              size="default"
              :icon="Close"
              style="font-size: 24px; color: #909399"
              @click="onCancel"
            />
          </el-row>
        </el-header>
        <el-main style="width: 100%; background: #f9f9f9">
          <el-row
            type="flex"
            justify="center"
            style="height: calc(100vh - 122px); padding: 25px; margin: 25px; background: white"
          >
            <el-scrollbar
              style="width: 100%; height: 100%; background: white"
              class="custom-scroll"
              wrap-class="scrollbar_dropdown__wrap"
            >
              <el-form
                v-if="isReady"
                ref="form"
                :model="formData"
                class="full-width-input"
                :label-width="(dialogParams.formConfig.labelWidth || 100) + 'px'"
                :label-position="dialogParams.formConfig.labelPosition || 'right'"
                :size="dialogParams.formConfig.formSize || formItemSize"
                @submit.prevent
              >
                <OnlineCustomBlock
                  ref="root"
                  v-model:value="dialogParams.formConfig.widgetList"
                  height="calc(100vh - 180px)"
                  :isEdit="dialogParams.isEdit"
                  :showBorder="false"
                  @widgetClick="onWidgetClick"
                />
              </el-form>
            </el-scrollbar>
          </el-row>
        </el-main>
      </el-container>
    </template>
    <div
      v-else
      class="form-box"
      :style="{ height: dialogParams.isEdit ? dialogParams.height : undefined }"
    >
      <el-scrollbar
        style="height: 100%"
        class="custom-scroll"
        wrap-class="scrollbar_dropdown__wrap"
      >
        <el-form
          v-if="isReady"
          ref="form"
          :model="formData"
          class="full-width-input"
          :label-width="(dialogParams.formConfig.labelWidth || 100) + 'px'"
          :label-position="dialogParams.formConfig.labelPosition || 'right'"
          :size="dialogParams.formConfig.formSize || formItemSize"
          @submit.prevent
        >
          <OnlineCustomBlock
            ref="root"
            v-model:value="dialogParams.formConfig.widgetList"
            :height="dialogParams.height"
            :isEdit="dialogParams.isEdit"
            :showBorder="false"
            @widgetClick="onWidgetClick"
          />
        </el-form>
      </el-scrollbar>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { Close } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { findItemFromList, findTreeNode } from '@/common/utils';
import OnlineCustomBlock from '@/online/components/OnlineCustomBlock.vue';
import {
  ReportDatasetController,
  ReportOperationController,
  ReportDictController,
  ReportPageController,
} from '@/api/report';
import { DictionaryController } from '@/api/system';
import { ANY_OBJECT } from '@/types/generic';
import {
  OnlineFormEventType,
  SysCustomWidgetOperationType,
  SysOnlineFormType,
} from '@/common/staticDict';
import { DialogProp } from '@/components/Dialog/types';
import { DatasetType, FilterValueKind, ReportWidgetType } from '@/common/staticDict/report';
import { SysOnlineParamValueType } from '@/common/staticDict/online';
import { Dialog } from '@/components/Dialog';
import { useLayoutStore } from '@/store';
import { MenuItem } from '@/types/upms/menu';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { getAllWidgetList, isChart } from '../../utils';
import OnlineReportForm from './index.vue';

interface IProps extends ThirdProps {
  formConfig?: ANY_OBJECT;
  height?: string;
  // 是否表单编辑模式
  isEdit?: boolean;
  fullscreen?: boolean;
  // 当前选中组件
  currentWidget?: ANY_OBJECT;
  // 表单参数
  params?: string;
  mode?: string;
  defaultFormItemSize: Ref<'' | 'default' | 'small' | 'large'>;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<boolean>;
}
const props = withDefaults(defineProps<IProps>(), {
  mode: 'pc',
});
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const isReady = ref(false);
const formData = ref<ANY_OBJECT>({});
const datasetMap = new Map();
const dictMap = new Map();

const router = useRouter();
const layoutStore = useLayoutStore();

const dialogParams = computed(() => {
  return {
    formConfig: props.formConfig || thirdParams.value.formConfig || {},
    isEdit: props.isEdit || thirdParams.value.isEdit || false,
    fullscreen: props.fullscreen || thirdParams.value.fullscreen || false,
    height: props.height || thirdParams.value.height,
    params: props.params || thirdParams.value.params,
  };
});
const getAllWidget = computed(() => {
  if (
    dialogParams.value.formConfig?.widgetList &&
    Array.isArray(dialogParams.value.formConfig?.widgetList)
  ) {
    return getAllWidgetList(dialogParams.value.formConfig?.widgetList, props.mode);
  }

  return [];
});
const formParams = computed(() => {
  let params: ANY_OBJECT = {};
  try {
    if (dialogParams.value.params) params = JSON.parse(dialogParams.value.params);
  } catch (e) {
    console.log(e);
  }
  return params;
});

const getScriptFunction = (eventInfo: ANY_OBJECT, eventType: string) => {
  if (eventInfo && typeof eventInfo[eventType] === 'function') {
    return eventInfo[eventType];
  } else {
    return null;
  }
};
const getWidgetValue = (widget: ANY_OBJECT) => {
  if (dialogParams.value.isEdit) return;
  return formData.value[widget.variableName];
};
const getWidgetVisible = (widget: ANY_OBJECT) => {
  if (dialogParams.value.isEdit || widget == null || widget.eventInfo == null) return true;
  if (typeof widget.eventInfo[OnlineFormEventType.VISIBLE] === 'function') {
    return widget.eventInfo[OnlineFormEventType.VISIBLE]();
  } else {
    return true;
  }
};
const onValueChange = (widget: ANY_OBJECT, value: ANY_OBJECT) => {
  console.log('ReportForm.onValueChange', dialogParams.value.isEdit, widget, value);
  if (dialogParams.value.isEdit) return;
  formData.value[widget.variableName] = value;
};
const onWidgetValueChange = (widget: ANY_OBJECT, value: ANY_OBJECT) => {
  if (dialogParams.value.isEdit || widget == null || widget.eventInfo == null) return;
  let fun = getScriptFunction(widget.eventInfo, OnlineFormEventType.CHANGE);
  fun && fun(value);
};
const listDictData = (dictId: string, params: ANY_OBJECT) => {
  if (params == null) {
    return Promise.reject();
  }
  let filterDtoList = Object.keys(params).map(key => {
    return {
      columnName: key,
      columnValue: params[key],
    };
  });
  return new Promise((resolve, reject) => {
    ReportDictController.listDictData({
      dictId: dictId,
      filterDtoList,
    })
      .then(res => {
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const getDictDataList = (dictInfo: ANY_OBJECT, params: ANY_OBJECT) => {
  if (dictInfo == null || dictInfo.id == null) return Promise.resolve([]);
  return listDictData(dictInfo.id, params);
};
const getQueryParam = (filter: ANY_OBJECT) => {
  if (filter.filterValueType === FilterValueKind.FORM_PARAM) {
    let formParam = formParams.value[filter.paramValue];
    if (formParam != null) {
      if (formParam.filterValueType === FilterValueKind.INNER_VARIABLE) {
        // 内置变量参数
        return {
          ...filter,
          filterValueType: FilterValueKind.INNER_VARIABLE,
          paramValue: formParam,
        };
      } else {
        return {
          ...filter,
          paramValue: formParam,
        };
      }
    } else {
      return {
        ...filter,
        paramValue: undefined,
      };
    }
  } else if (filter.filterValueType === FilterValueKind.WIDGET_DATA) {
    let filterWidget = findItemFromList(getAllWidget.value, filter.formWidgetId, 'widgetId');
    let paramValue, dateRange;
    if (filterWidget != null) {
      paramValue = formData.value[filterWidget.variableName];
      if (filterWidget.widgetType === ReportWidgetType.Cascader) {
        if (Array.isArray(paramValue)) {
          paramValue = paramValue[paramValue.length - 1];
        } else {
          paramValue = undefined;
        }
      }
      if (paramValue === '') paramValue = undefined;
      if (
        filterWidget.widgetType === ReportWidgetType.DateRange ||
        filterWidget.widgetType === ReportWidgetType.Date
      ) {
        switch (filterWidget.props.type) {
          case 'daterange':
            dateRange = 'date';
            break;
          case 'monthrange':
            dateRange = 'month';
            break;
          case 'datetimerange':
          case 'datetime':
            dateRange = undefined;
            break;
          default:
            dateRange = filterWidget.props.type;
        }
      }
    }
    return {
      ...filter,
      dateRange,
      paramValue,
    };
  } else {
    return {
      ...filter,
    };
  }
};
const listDataWithGroup = (params: ANY_OBJECT) => {
  return new Promise((resolve, reject) => {
    let finalParams = {
      ...params,
      pageCode: dialogParams.value.formConfig.pageCode,
    };
    let httpCall = dialogParams.value.isEdit
      ? ReportOperationController.previewDataWithGroup(finalParams)
      : ReportOperationController.listDataWithGroup(finalParams);
    httpCall
      .then(res => {
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
        console.warn(e);
      });
  });
};
const listColumnData = (datasetId: string, columnId: string) => {
  return new Promise((resolve, reject) => {
    ReportDatasetController.listDataWithColumn({
      datasetId: datasetId,
      columnId: columnId,
    })
      .then(res => {
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const loadDatasetInfo = (datasetId: string) => {
  return new Promise((resolve, reject) => {
    let params = {
      datasetId: datasetId,
    };
    ReportDatasetController.view(params)
      .then(res => {
        try {
          let info = JSON.parse(res.data.datasetInfo);
          res.data.datasetParamList = info.paramList;
          res.data.columnList = info.columnList || res.data.columnList;
        } catch (e) {
          res.data.datasetParamList = [];
        }
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const getParamValue = (valueType: number, valueData: string | number) => {
  switch (valueType) {
    case SysOnlineParamValueType.TABLE_COLUMN: {
      let widget = findItemFromList(getAllWidget.value, valueData, 'widgetId');
      return widget ? formData.value[widget.variableName] : undefined;
    }
    case SysOnlineParamValueType.STATIC_DICT:
      return Array.isArray(valueData) ? valueData[1] : undefined;
    case SysOnlineParamValueType.INPUT_VALUE:
      return valueData;
  }
};
const getDropdownParams = (widget: ANY_OBJECT) => {
  if (Array.isArray(widget.props.dictInfo.paramList)) {
    let params: ANY_OBJECT = {};
    for (let i = 0; i < widget.props.dictInfo.paramList.length; i++) {
      let dictParam = widget.props.dictInfo.paramList[i];
      if (dictParam.dictValue == null || dictParam.dictValueType == null) continue;
      let widgetData = getParamValue(dictParam.dictValueType, dictParam.dictValue);
      if (widgetData == null) return null;
      params[dictParam.dictParamName] = widgetData;
    }
    return params;
  } else {
    return {};
  }
};
const loadReportPage = (pageId: string) => {
  return new Promise<ANY_OBJECT | null>((resolve, reject) => {
    ReportPageController.view({
      pageId: pageId,
    })
      .then(res => {
        let temp = {
          pageId: res.data.pageId,
          pageName: res.data.pageName,
          pageCode: res.data.pageCode,
          groupId: res.data.groupId,
          pageJson: res.data.pageJson,
          formInfo: res.data.pageJson ? JSON.parse(res.data.pageJson) : {},
        };
        let formInfo = temp.formInfo.pc;
        let formConfig: ANY_OBJECT | null = null;
        if (formInfo != null) {
          formConfig = {
            pageId: temp.pageId,
            pageName: temp.pageName,
            pageCode: temp.pageCode,
            gutter: formInfo.gutter || 20,
            labelWidth: formInfo.labelWidth || 100,
            labelPosition: formInfo.labelPosition || 'left',
            customFieldList: formInfo.customFieldList || [],
            filterItemWidth: formInfo.filterItemWidth || 350,
            widgetList: formInfo.widgetList || [],
            paramList: formInfo.paramList || [],
          };
        }

        resolve(formConfig);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const gotoPage = (pageId: string, params: ANY_OBJECT) => {
  loadReportPage(pageId)
    .then(formConfig => {
      if (!formConfig) {
        console.warn('loadReportPage formConfig is null');
        return;
      }
      return Dialog.show(
        formConfig?.pageName,
        OnlineReportForm,
        {
          area: ['100vw', '100vh'],
          skin: 'fullscreen-dialog',
        },
        {
          formConfig: formConfig,
          height: 'calc(100vh - 60px)',
          isEdit: dialogParams.value.isEdit,
          fullscreen: true,
          params: JSON.stringify(params || {}),
          path: 'thirdReportForm',
        },
        {
          fullscreen: true,
          pathName: '/thirdParty/thirdReportForm',
        },
      );
    })
    .catch(e => {
      console.error(e);
    });
};
const gotoRouter = (routerName: string, routerParams: ANY_OBJECT) => {
  let menuItem: MenuItem | null = findTreeNode(
    layoutStore.menuList,
    routerName,
    'formRouterName',
  ) as MenuItem;
  if (menuItem != null) {
    // 查找要跳转的是不是菜单，如果是执行菜单选中操作
    layoutStore.setCurrentMenu(menuItem);
  } else {
    // 如果跳转的路由不是菜单，跳转过去
    router.push({
      name: routerName,
      query: routerParams.query || routerParams.param,
    });
  }
};
// 根据配置的字段取字符串数据
const getDataString = (columnName: string | string[], data: ANY_OBJECT) => {
  if (Array.isArray(columnName)) {
    let dataValue = columnName.length > 0 ? data : undefined;

    for (let i = 0; i < columnName.length; i++) {
      let name = columnName[i];
      if (name == null || dataValue == null) {
        dataValue = undefined;
        break;
      }
      dataValue = dataValue[name];
    }
    return dataValue;
  } else {
    return data[columnName];
  }
};
const onChartDblClick = (widget: ANY_OBJECT, data: ANY_OBJECT) => {
  if (widget != null && Array.isArray(widget.operationList)) {
    // 下钻事件
    let drillOperation = findItemFromList(
      widget.operationList,
      SysCustomWidgetOperationType.DRILL,
      'type',
    );
    if (drillOperation != null && drillOperation.enabled) {
      let pageId = drillOperation.pageId;
      let filterList = drillOperation.filterList;
      let params = {};
      if (Array.isArray(filterList)) {
        let dataset = datasetMap.get(widget.props.datasetInfo.datasetId);
        params = filterList.reduce((retObj, item) => {
          if (item.filterValueType === FilterValueKind.DRILL_DATA && dataset) {
            if (widget.props && widget.props.datasetInfo) {
              let columnId = item.paramValue;
              let column: ANY_OBJECT =
                findItemFromList(dataset.columnList, columnId, 'columnId') || {};
              // 维度字段
              if (Array.isArray(widget.props.datasetInfo.categroyColumnList)) {
                let category = findItemFromList(
                  widget.props.datasetInfo.categroyColumnList,
                  columnId,
                  'columnId',
                );
                if (category != null) {
                  retObj[item.paramName] = data[column.columnName];
                }
              }
              // 指标字段
            }
          } else {
            retObj[item.paramName] = (getQueryParam(item) || {}).paramValue;
          }
          return retObj;
        }, {});
      }
      gotoPage(pageId, params);
      return;
    }
    // 路由跳转操作
    let routeOperation = findItemFromList(
      widget.operationList,
      SysCustomWidgetOperationType.ROUTE,
      'type',
    );
    if (routeOperation != null && !props.isEdit && routeOperation.enabled) {
      let queryParams = (routeOperation.routeParams || []).reduce(
        (retObj: ANY_OBJECT, item: ANY_OBJECT) => {
          if (item.valueType === 'fixed') {
            // 固定值
            retObj[item.paramName] = item.paramValue;
          } else if (item.valueType === 'bind') {
            // 绑定到字段
            let columnName;
            if (widget.dataset) {
              let column: ANY_OBJECT | null = findItemFromList(
                widget.dataset.columnList,
                item.paramValue,
                'columnId',
              );
              if (widget.dataset.datasetType === DatasetType.API) {
                columnName = [];
                do {
                  columnName.push(column?.columnName);
                  column = findItemFromList(
                    widget.dataset.columnList,
                    column?.parentId,
                    'columnId',
                  );
                } while (column == null);
                columnName = columnName.reverse();
              } else {
                columnName = (column || {}).columnName;
              }
            }
            let value = getDataString(columnName, data);
            if (value) retObj[item.paramName] = value;
          }
          return retObj;
        },
        {},
      );
      gotoRouter(routeOperation.routeName, {
        query: queryParams,
      });
    }
  }
};

provide('form', () => {
  return {
    ...dialogParams.value.formConfig,
    mode: props.mode,
    isEdit: dialogParams.value.isEdit,
    formType: SysOnlineFormType.REPORT,
    readOnly: false,
    getWidgetValue: getWidgetValue,
    getWidgetVisible: getWidgetVisible,
    onValueChange: onValueChange,
    onWidgetValueChange: onWidgetValueChange,
    getDictDataList: getDictDataList,
    getQueryParam: getQueryParam,
    listDataWithGroup: listDataWithGroup,
    listDictData: listDictData,
    listColumnData: listColumnData,
    loadDatasetInfo: loadDatasetInfo,
    getDropdownParams: getDropdownParams,
    onChartDblClick: onChartDblClick,
    isActive: (widget: ANY_OBJECT) => {
      return props.currentWidget === widget;
    },
  };
});

const emit = defineEmits<{ widgetClick: [ANY_OBJECT | null] }>();
const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};

const onWidgetClick = (widget: ANY_OBJECT | null) => {
  console.log('ReportForm.formConfig widgetClick', widget);
  emit('widgetClick', widget);
};
const initFormWidget = (widget: ANY_OBJECT | null) => {
  if (widget == null) return;
  if (widget.props && widget.props.dictInfo) {
    let dict = dictMap.get(widget.props.dictInfo.dictId);
    widget.props.dictInfo.dict = dict;
  }
  if (widget.eventInfo == null) {
    widget.eventInfo = {};
  }
  if (Array.isArray(widget.childWidgetList)) {
    widget.childWidgetList = widget.childWidgetList.map(subWidget => {
      return initFormWidget(subWidget);
    });
  }
  if (widget && widget.props && widget.props.datasetInfo && widget.props.datasetInfo.datasetId) {
    let dataset = datasetMap.get(widget.props.datasetInfo.datasetId);
    return {
      ...widget,
      dataset: dataset,
      columnList: (dataset || {}).columnList,
    };
  } else {
    return {
      ...widget,
    };
  }
};
const getAllDatasetIdsByWidgetList = (widgetList: ANY_OBJECT[], datasetIdList: string[] = []) => {
  if (Array.isArray(widgetList)) {
    widgetList.forEach(widget => {
      if (
        widget &&
        widget.props &&
        widget.props.datasetInfo &&
        widget.props.datasetInfo.datasetId
      ) {
        if (datasetIdList.indexOf(widget.props.datasetInfo.datasetId.toString()) === -1) {
          datasetIdList.push(widget.props.datasetInfo.datasetId);
        }
      }
      getAllDatasetIdsByWidgetList(widget.childWidgetList, datasetIdList);
    });
  }
};
const loadDataset = (datasetId: string) => {
  return new Promise((resolve, reject) => {
    let params = {
      datasetId: datasetId,
    };
    ReportDatasetController.view(params)
      .then(res => {
        try {
          let info = JSON.parse(res.data.datasetInfo);
          res.data.datasetParamList = info.paramList;
        } catch (e) {
          res.data.datasetParamList = [];
        }
        datasetMap.set(datasetId, res.data);
        resolve(res.data);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const loadAllDataset = (widgetList: ANY_OBJECT[]) => {
  let datasetIdList: string[] = [];
  getAllDatasetIdsByWidgetList(widgetList, datasetIdList);
  return Promise.all(
    datasetIdList.map(datasetId => {
      return loadDataset(datasetId);
    }),
  );
};
const loadReportDictList = () => {
  dictMap.clear();
  return new Promise((resolve, reject) => {
    DictionaryController.dictReportDict({})
      .then(res => {
        res.getList().forEach(item => {
          item.dictData = item.dictDataJson ? JSON.parse(item.dictDataJson as string) : undefined;
          dictMap.set(item.id, item);
        });
        resolve(true);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const buildWatch = (widget: ANY_OBJECT, filter: ANY_OBJECT) => {
  if (filter.filterValueType === FilterValueKind.FORM_PARAM) {
    // 报表参数
    watch(
      () => formParams.value[filter.paramValue],
      () => {
        if (widget.widgetImpl) widget.widgetImpl.refresh();
      },
    );
  } else if (filter.filterValueType === FilterValueKind.WIDGET_DATA) {
    // 过滤组件
    let filterWidget: ANY_OBJECT | null = findItemFromList(
      getAllWidget.value,
      filter.formWidgetId,
      'widgetId',
    );
    if (filterWidget != null) {
      const variableName: string = filterWidget.variableName;
      watch(
        () => formData.value[variableName],
        () => {
          if (widget.widgetImpl) {
            let refresh = widget.widgetImpl.refresh;
            if (refresh && typeof refresh === 'function') {
              refresh();
            }
          }
        },
      );
    }
  }
};
const buildDropdownWatch = (widget: ANY_OBJECT) => {
  if (widget && Array.isArray((widget.props.dictInfo || {}).paramList)) {
    widget.props.dictInfo.paramList.forEach((param: ANY_OBJECT) => {
      if (param.dictValueType === SysOnlineParamValueType.TABLE_COLUMN && param.dictValue != null) {
        let filterWidget = findItemFromList(getAllWidget.value, param.dictValue, 'widgetId');
        if (filterWidget != null) {
          const variableName: string = filterWidget.variableName;
          watch(
            () => formData.value[variableName],
            () => {
              formData.value[widget.variableName] = undefined;
              if (widget && widget.widgetImpl && typeof widget.widgetImpl.reset === 'function') {
                widget.widgetImpl.reset();
              }
            },
          );
        }
      }
    });
  }
};
const initPageWatch = () => {
  getAllWidget.value.forEach(widget => {
    if (isChart(widget.widgetType) && widget.props.datasetInfo) {
      // 组件过滤
      if (Array.isArray(widget.props.datasetInfo.filterList)) {
        widget.props.datasetInfo.filterList.forEach((filter: ANY_OBJECT) => {
          buildWatch(widget, filter);
        });
      }
      // 数据集过滤
      if (Array.isArray(widget.props.datasetInfo.datasetFilterParams)) {
        widget.props.datasetInfo.datasetFilterParams.forEach((filter: ANY_OBJECT) => {
          buildWatch(widget, filter);
        });
      }
      // 指标过滤
      if (Array.isArray(widget.props.datasetInfo.valueColumnList)) {
        widget.props.datasetInfo.valueColumnList.forEach((valueColumn: ANY_OBJECT) => {
          if (valueColumn && Array.isArray(valueColumn.filterList)) {
            valueColumn.filterList.forEach((filter: ANY_OBJECT) => {
              buildWatch(widget, filter);
            });
          }
        });
      }
    }
    // 下拉数据过滤
    buildDropdownWatch(widget);
  });
};
const initFormData = () => {
  return new Promise((resolve, reject) => {
    getAllWidget.value.forEach(widget => {
      const data = { ...formData.value };
      data[widget.variableName] = undefined;
      formData.value = data;
    });
    loadAllDataset(dialogParams.value.formConfig.widgetList)
      .then(() => {
        return loadReportDictList();
      })
      .then(() => {
        dialogParams.value.formConfig.widgetList = dialogParams.value.formConfig.widgetList.map(
          (widget: ANY_OBJECT) => {
            return initFormWidget(widget);
          },
        );
        initPageWatch();
        resolve(true);
      })
      .catch(e => {
        console.log(e);
        reject(e);
      });
  });
};

watch(
  () => dialogParams.value.formConfig,
  () => {
    if (dialogParams.value.formConfig == null) return;
    isReady.value = false;
    datasetMap.clear();

    setTimeout(() => {
      initFormData()
        .then(() => {
          isReady.value = true;
        })
        .catch(() => {
          ElMessage.error('页面初始化失败！');
        });
    }, 200);
  },
  {
    immediate: true,
  },
);
</script>

<style scoped>
.divider {
  height: 26px;
  margin: 7px 8px;
}
</style>
