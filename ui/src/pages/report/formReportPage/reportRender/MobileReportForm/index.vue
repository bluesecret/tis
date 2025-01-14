<template>
  <div
    class="online-mobile-report-form"
    style="position: relative; min-height: 200px"
    :style="{ height: dialogParams.height }"
  >
    <!-- 筛选、排序区域 -->
    <el-row class="header" align="middle">
      <div class="order-box" />
      <div class="filter-btn" @click.stop="showFilterDlg = true">
        <span>筛选</span>
        <i class="online-icon icon-filter" />
      </div>
    </el-row>
    <!-- 表单内容 -->
    <el-row class="list-box" style="border-top: 2px solid #f6f6f6; padding: 16px">
      <van-form
        ref="form"
        class="full-width-input"
        style="width: 100%; height: 100%"
        :label-width="(dialogParams.formConfig.labelWidth || 100) + 'px'"
        @submit.prevent
      >
        <OnlineCustomBlock
          v-model:value="contentWidgetList"
          :isEdit="isEdit"
          @widgetClick="onWidgetClick"
        />
      </van-form>
    </el-row>
    <!-- 弹出筛选窗口 -->
    <div v-show="showFilterDlg" class="filter-dlg">
      <div class="filter">
        <div class="filter-dlg-header">
          <div style="font-size: 14px; color: #333">筛选</div>
          <i class="close el-icon-close" @click.stop="showFilterDlg = false" />
        </div>
        <el-scrollbar class="filter-content">
          <slot name="filter" />
          <OnlineCustomBlock
            v-model:value="filterWidgetList"
            :isEdit="isEdit"
            @widgetClick="onWidgetClick"
          />
        </el-scrollbar>
        <el-row class="filter-btn-box" type="flex" align="bottom">
          <el-button type="default" size="default" style="width: 100px">重置</el-button>
          <el-button type="primary" size="default" style="flex-grow: 1">确定</el-button>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import {
  ReportDatasetController,
  ReportOperationController,
  ReportDictController,
} from '@/api/report';
import OnlineCustomBlock from '@/online/components/OnlineCustomBlock.vue';
import { DictionaryController } from '@/api/system';
import { findItemFromList } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { OnlineFormEventType, SysCustomWidgetType, SysOnlineFormType } from '@/common/staticDict';
import { FilterValueKind, ReportWidgetType } from '@/common/staticDict/report';
import { SysOnlineParamValueType } from '@/common/staticDict/online';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { getAllWidgetList } from '../../utils';

interface IProps extends ThirdProps {
  formConfig?: ANY_OBJECT;
  height?: string;
  // 是否表单编辑模式
  isEdit?: boolean;
  // 当前选中组件
  currentWidget?: ANY_OBJECT;
  // 表单参数
  params?: string;
  mode?: string;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<boolean>;
}
const props = withDefaults(defineProps<IProps>(), {
  mode: 'mobile',
});
const { thirdParams } = useThirdParty(props);

const isReady = ref(false);
const formData = ref<ANY_OBJECT>({});
const showFilterDlg = ref(false);
const datasetMap = new Map();
const dictMap = new Map();
const contentWidgetList = ref<ANY_OBJECT[]>([]);
const filterWidgetList = ref<ANY_OBJECT[]>([]);

const dialogParams = computed<ANY_OBJECT>(() => {
  return {
    formConfig: props.formConfig || thirdParams.value.formConfig,
    isEdit: props.isEdit || thirdParams.value.isEdit || false,
    height: props.height || thirdParams.value.height,
  };
});
const getAllWidget = computed(() => {
  if (
    dialogParams.value.formConfig?.widgetList &&
    Array.isArray(dialogParams.value.formConfig?.widgetList)
  ) {
    return getAllWidgetList(dialogParams.value.formConfig?.widgetList, props.mode);
  }

  return [] as ANY_OBJECT[];
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
  // if (typeof widget.eventInfo[OnlineFormEventType.VISIBLE] === 'function') {
  //   return widget.eventInfo[OnlineFormEventType.VISIBLE]();
  // } else {
  //   return true;
  // }
};
const onValueChange = (widget: ANY_OBJECT, value: ANY_OBJECT) => {
  console.log('MobileReportForm.onValueChange', dialogParams.value.isEdit, widget, value);
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
        console.log(e);
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
const onChartDblClick = (widget: ANY_OBJECT, data: ANY_OBJECT) => {
  console.log('mobile report form do nothing', widget, data);
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
const onWidgetClick = (widget: ANY_OBJECT | null) => {
  emit('widgetClick', widget);
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
const initFormData = () => {
  return new Promise((resolve, reject) => {
    contentWidgetList.value = [];
    filterWidgetList.value = [];
    const mobileFilterWidget = [
      SysCustomWidgetType.MobileRadioFilter,
      SysCustomWidgetType.MobileCheckBoxFilter,
      SysCustomWidgetType.MobileInputFilter,
      SysCustomWidgetType.MobileSwitchFilter,
      SysCustomWidgetType.MobileDateRangeFilter,
      SysCustomWidgetType.MobileNumberRangeFilter,
    ];
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
        dialogParams.value.formConfig.widgetList.forEach((widget: ANY_OBJECT) => {
          if (mobileFilterWidget.indexOf(widget.widgetType) !== -1) {
            filterWidgetList.value.push(widget);
          } else {
            contentWidgetList.value.push(widget);
          }
        });
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
    initFormData()
      .then(() => {
        nextTick(() => {
          contentWidgetList.value.forEach(widget => {
            if (widget.widgetImpl) widget.widgetImpl.refresh();
          });
        });
        isReady.value = true;
      })
      .catch(e => {
        console.warn(e);
        ElMessage.error('页面初始化失败！');
      });
  },
  {
    immediate: true,
  },
);
watch(
  () => contentWidgetList.value,
  () => {
    if (isReady.value) {
      dialogParams.value.formConfig.widgetList = [
        ...contentWidgetList.value,
        ...filterWidgetList.value,
      ];
    }
  },
);
watch(
  () => filterWidgetList.value,
  () => {
    if (isReady.value) {
      dialogParams.value.formConfig.widgetList = [
        ...contentWidgetList.value,
        ...filterWidgetList.value,
      ];
    }
  },
);

const clearWidget = () => {
  contentWidgetList.value = [];
  filterWidgetList.value = [];
};
defineExpose({ clearWidget });
</script>

<style scoped>
.online-mobile-report-form {
  position: relative;
  display: flex;
  flex-direction: column;
}

.online-mobile-report-form .header {
  flex-shrink: 0;
  padding: 8px 15px;
  background: white;
  flex-grow: 0;
}

.online-mobile-report-form .header .order-box {
  flex-shrink: 1;
  overflow-x: auto;
  overflow-y: hidden;
  width: 200px;
  margin-right: 10px;
  white-space: nowrap;
  flex-grow: 1;
}

.online-mobile-report-form .header .filter-btn {
  height: 20px;
  line-height: 20px;
  font-size: 14px;
  cursor: pointer;
}

.online-mobile-report-form .list-box {
  flex-shrink: 1;
  overflow-y: auto;
  padding: 15px 0;
  background: #f6f6f6;
  flex-grow: 1;
}

.online-mobile-report-form .filter-dlg {
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1000;
  width: 100%;
  height: 100%;
  background: rgb(0 0 0 / 40%);
}

.online-mobile-report-form .filter-dlg .filter {
  position: absolute;
  bottom: 0;
  left: 0;
  display: flex;
  width: 100%;
  height: 70%;
  min-height: 300px;
  max-height: 100%;
  padding: 0 20px 20px;
  background: white;
  border-radius: 12px 12px 0 0;
  flex-direction: column;
}

.online-mobile-report-form .filter-dlg .filter .filter-content {
  flex-grow: 1;
  flex-shrink: 1;
  height: 200px;
}

.online-mobile-report-form .filter-dlg .filter .filter-btn-box {
  flex-grow: 0;
  flex-shrink: 0;
  height: 40px;
}

.filter-dlg-header {
  position: relative;
  height: 40px;
  text-align: center;
  line-height: 40px;
}

.filter-dlg-header .close {
  position: absolute;
  top: 12px;
  right: 2px;
  font-size: 16px;
  cursor: pointer;
}

.online-mobile-report-form .order-box .order-item {
  display: inline-block;
  height: 20px;
  line-height: 20px;
}

.order-item + .order-item {
  margin-left: 20px;
}

.filter-content :deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}
</style>
