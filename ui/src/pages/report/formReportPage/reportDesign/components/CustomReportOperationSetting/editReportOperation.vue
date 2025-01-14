<template>
  <div class="form-edit-report-operation third-party-dlg" style="position: relative">
    <div class="form-box">
      <el-form
        ref="form"
        :model="formData"
        class="full-width-input"
        :rules="rules"
        style="width: 100%"
        label-width="80px"
        :inline="true"
        :size="formItemSize"
        @submit.prevent
      >
        <el-form-item label="所属分组">
          <el-cascader
            v-model="formData.groupId"
            :options="dialogParams.groupList"
            :props="{ label: 'groupName', value: 'groupId' }"
            @change="onPageGroupChange"
          />
        </el-form-item>
        <el-form-item label="下钻页面" prop="pageId">
          <el-select v-model="formData.pageId" clearable placeholder="" style="width: 250px">
            <el-option
              v-for="page in pageList"
              :key="page.pageId"
              :label="page.pageName"
              :value="page.pageId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用">
          <el-switch v-model="formData.enabled" />
        </el-form-item>
      </el-form>
      <el-col :span="24" style="margin-bottom: 18px">
        <vxe-table
          :data="pageFilterList"
          :size="formItemSize"
          header-cell-class-name="table-header-gray"
          height="400px"
          :row-config="{ isHover: true }"
        >
          <vxe-column title="参数名" field="paramName" width="150px" />
          <vxe-column title="参数值类型" field="filterValueType" width="150px">
            <template v-slot="scope">
              <el-select
                v-model="scope.row.filterValueType"
                size="default"
                placeholder=""
                @change="onFilterValueTypeChange(scope.row)"
              >
                <el-option
                  v-for="item in getValidFilteValueType"
                  :key="item"
                  :label="FilterValueKind.getValue(item)"
                  :value="item"
                />
              </el-select>
            </template>
          </vxe-column>
          <vxe-column title="参数值" field="paramValue">
            <template v-slot="scope">
              <!-- 表单输入参数 -->
              <el-select
                v-if="scope.row.filterValueType === FilterValueKind.FORM_PARAM"
                style="width: 200px"
                v-model="scope.row.paramValue"
                size="default"
                clearable
                placeholder=""
              >
                <el-option
                  v-for="name in dialogParams.formParamList"
                  :key="name"
                  :label="name"
                  :value="name"
                />
              </el-select>
              <!-- 组件数据参数 -->
              <el-select
                v-if="scope.row.filterValueType === FilterValueKind.WIDGET_DATA"
                style="width: 200px"
                v-model="scope.row.formWidgetId"
                key="formWidgetId"
                size="default"
                clearable
                placeholder=""
                @change="scope.row.formWidgetColumn = undefined"
              >
                <el-option
                  v-for="widget in dialogParams.widgetList"
                  :key="widget.id"
                  :label="widget.showName"
                  :value="widget.id"
                />
              </el-select>
              <el-select
                v-if="
                  scope.row.filterValueType === FilterValueKind.WIDGET_DATA &&
                  Array.isArray((getFilterWidget(scope.row) || {}).children) &&
                  (getFilterWidget(scope.row) || { children: [] }).children.length > 0
                "
                v-model="scope.row.formWidgetColumn"
                key="formWidgetColumn"
                style="width: 200px; margin-left: 15px"
                size="default"
                clearable
                placeholder=""
              >
                <el-option
                  v-for="column in (getFilterWidget(scope.row) || {}).children"
                  :key="column.id"
                  :label="column.showName"
                  :value="column.id"
                />
              </el-select>
              <!-- 字典参数 -->
              <el-select
                v-if="scope.row.filterValueType === FilterValueKind.DICT_DATA"
                style="width: 200px"
                placeholder="请选择参数值字典"
                v-model="scope.row.paramDictId"
                size="default"
                clearable
                @change="onFilterDictChange(scope.row)"
              >
                <el-option
                  v-for="dict in getValidDictList"
                  :key="dict.id"
                  :label="dict.name"
                  :value="dict.id"
                  :disabled="dict.disabled"
                />
              </el-select>
              <el-select
                v-if="scope.row.filterValueType === FilterValueKind.DICT_DATA"
                style="width: 200px; margin-left: 15px"
                v-model="scope.row.paramValue"
                size="default"
                clearable
                placeholder="请选择参数值"
              >
                <el-option
                  v-for="data in scope.row.paramDictDataList"
                  :key="data.id"
                  :label="data.name"
                  :value="data.id"
                />
              </el-select>
              <!-- 内置变量 -->
              <el-select
                v-if="scope.row.filterValueType === FilterValueKind.INNER_VARIABLE"
                style="width: 200px"
                v-model="scope.row.paramValue"
                size="default"
                clearable
                placeholder=""
              >
                <el-option
                  v-for="item in CustomDateValueType.getList()"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
              <!-- 自定义参数 -->
              <el-input
                v-if="scope.row.filterValueType === FilterValueKind.INPUT_DATA"
                style="width: 200px"
                v-model="scope.row.paramValue"
                size="default"
                placeholder=""
              />
              <!-- 下钻数据 -->
              <el-select
                :key="FilterValueKind.DRILL_DATA"
                v-if="scope.row.filterValueType === FilterValueKind.DRILL_DATA"
                style="width: 200px"
                v-model="scope.row.paramValue"
                size="default"
                placeholder=""
              >
                <el-option
                  v-for="item in dialogParams.widgetDrillColumnList"
                  :key="item.columnId"
                  :label="item.showName"
                  :value="item.columnId"
                />
              </el-select>
            </template>
          </vxe-column>
          <template v-slot:empty>
            <div class="table-empty unified-font">
              <img src="@/assets/img/empty.png" />
              <span>暂无数据</span>
            </div>
          </template>
        </vxe-table>
      </el-col>
    </div>
    <el-row class="no-scroll menu-box" type="flex" justify="end">
      <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="formItemSize" @click="onSubmit"> 保存 </el-button>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { VxeTable, VxeColumn } from 'vxe-table';
import { findItemFromList, findTreeNodePath } from '@/common/utils';
import { ReportDictController, ReportPageController } from '@/api/report';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { FilterValueKind, CustomDateValueType } from '@/common/staticDict/report';
import { SysCustomWidgetOperationType } from '@/common/staticDict';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  rowData?: ANY_OBJECT;
  activeMode?: string;
  groupList?: ANY_OBJECT[];
  formParamList?: string[];
  reportDictList?: ANY_OBJECT[];
  widgetList?: ANY_OBJECT[];
  widgetDrillColumnList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = withDefaults(defineProps<IProps>(), {
  groupList: () => [],
  formParamList: () => [],
  reportDictList: () => [],
  widgetList: () => [],
});
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const form = ref();
const formData = ref<ANY_OBJECT>({
  id: undefined,
  name: undefined,
  type: undefined,
  groupId: undefined,
  pageId: undefined,
  enabled: false,
});
const rules = {
  pageId: [{ required: true, message: '请选择下钻页面！', trigger: 'change' }],
};
const oldFilterList = ref<ANY_OBJECT[] | null>(null);
const pageList = ref<ANY_OBJECT[]>([]);
const pageFilterList = ref<ANY_OBJECT[]>([]);

const dialogParams = computed(() => {
  return {
    rowData: props.rowData || thirdParams.value.rowData,
    activeMode: props.activeMode || thirdParams.value.activeMode || '',
    groupList: props.groupList || thirdParams.value.groupList,
    formParamList: props.formParamList || thirdParams.value.formParamList,
    reportDictList: props.reportDictList || thirdParams.value.reportDictList,
    widgetList: props.widgetList || thirdParams.value.widgetList,
    widgetDrillColumnList: props.widgetDrillColumnList || thirdParams.value.widgetDrillColumnList,
  };
});
const currentPage = computed(() => {
  return findItemFromList(pageList.value, formData.value.pageId, 'pageId');
});
const getValidFilteValueType = computed(() => {
  return [
    FilterValueKind.FORM_PARAM,
    FilterValueKind.WIDGET_DATA,
    FilterValueKind.DRILL_DATA,
    FilterValueKind.DICT_DATA,
    FilterValueKind.INNER_VARIABLE,
    FilterValueKind.INPUT_DATA,
  ];
});
const getValidDictList = computed(() => {
  return dialogParams.value.reportDictList.map(item => {
    return {
      ...item,
      dictDataList: item.dictDataList,
    } as ANY_OBJECT;
  });
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};
const onSubmit = () => {
  form.value.validate((valid: boolean) => {
    if (!valid) return;
    const data = {
      ...formData.value,
      groupId: Array.isArray(formData.value.groupId)
        ? formData.value.groupId[formData.value.groupId.length - 1]
        : undefined,
      pageId: formData.value.pageId,
      pageName: currentPage.value?.pageName,
      filterList: pageFilterList.value.map(item => {
        return {
          ...item,
          paramDictDataList: undefined,
        };
      }),
    };
    if (props.dialog) {
      props.dialog.submit(data);
    } else {
      onCloseThirdDialog(true, dialogParams.value.rowData, data);
    }
  });
};

const parsePageInfo = (pageInfo: ANY_OBJECT) => {
  let temp = {
    pageId: pageInfo.pageId,
    pageName: pageInfo.pageName,
    pageCode: pageInfo.pageCode,
    groupId: pageInfo.groupId,
    pageJson: pageInfo.pageJson,
    formInfo: pageInfo.pageJson ? JSON.parse(pageInfo.pageJson) : {},
  };

  temp.formInfo = Object.keys(temp.formInfo).reduce((retObj: ANY_OBJECT, modelKey: string) => {
    let formInfo = temp.formInfo[modelKey];
    retObj[modelKey] = {
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
    return retObj;
  }, {});

  return temp;
};
const loadReportPage = () => {
  pageList.value = [];
  if (formData.value.groupId == null || formData.value.groupId === '') {
    return;
  }
  ReportPageController.list({
    reportPageDtoFilter: {
      groupId: Array.isArray(formData.value.groupId)
        ? formData.value.groupId[formData.value.groupId.length - 1]
        : undefined,
    },
  })
    .then(res => {
      pageList.value = (res.data.dataList || []).map(pageInfo => {
        return parsePageInfo(pageInfo);
      });
    })
    .catch(e => {
      console.warn(e);
    });
};
const onPageGroupChange = () => {
  formData.value.pageId = undefined;
  loadReportPage();
};
const onFilterValueTypeChange = (row: ANY_OBJECT) => {
  row.paramDictId = undefined;
  row.innerVeriable = undefined;
  row.paramValue = undefined;
  row.paramDictDataList = [];
};
const getFilterValueDict = (row: ANY_OBJECT) => {
  for (let i = 0; i < getValidDictList.value.length; i++) {
    if (row.paramDictId === getValidDictList.value[i].id) return getValidDictList.value[i];
  }

  return null;
};
const getFilterWidget = (row: ANY_OBJECT) => {
  return findItemFromList(dialogParams.value.widgetList, row.formWidgetId, 'widgetId');
};
const onFilterDictChange = (row: ANY_OBJECT) => {
  loadReportDictDataList(row, getFilterValueDict(row));
  row.paramValue = undefined;
};
const loadReportDictDataList = (row: ANY_OBJECT, node: ANY_OBJECT | null) => {
  if (node == null || Array.isArray(node.dictDataList)) {
    row.paramDictDataList = (node || {}).dictDataList;
    return;
  }
  ReportDictController.listDictData({
    dictId: node.id,
  })
    .then(res => {
      row.paramDictDataList = res.data;
      node.dictDataList = res.data;
    })
    .catch(e => {
      console.warn(e);
    });
};

watch(
  () => currentPage.value,
  () => {
    if (currentPage.value == null) {
      pageFilterList.value = [];
    } else {
      let tempFilterList: ANY_OBJECT[] = [];
      ReportPageController.view({
        pageId: currentPage.value.pageId,
      })
        .then(res => {
          if (currentPage.value == null) {
            console.error('currentPage is null');
            return;
          }
          currentPage.value.formInfo = res.data.pageJson ? JSON.parse(res.data.pageJson) : {};
          if (
            currentPage.value.formInfo[dialogParams.value.activeMode] &&
            Array.isArray(currentPage.value.formInfo[dialogParams.value.activeMode].paramList)
          ) {
            tempFilterList = currentPage.value.formInfo[dialogParams.value.activeMode].paramList;
          }
          pageFilterList.value = tempFilterList
            .map(item => {
              if (item.variableName != null && item.variableName !== '') {
                let oldFilter: ANY_OBJECT | null = null;
                if (oldFilterList.value != null) {
                  oldFilter = findItemFromList(oldFilterList.value, item.variableName, 'paramName');
                }

                let temp: ANY_OBJECT = {
                  paramName: item.variableName,
                  filterValueType: oldFilter
                    ? oldFilter.filterValueType
                    : FilterValueKind.INPUT_DATA,
                  // 字典过滤
                  paramDictId: oldFilter ? oldFilter.paramDictId : undefined,
                  // 表单组件参数
                  formWidgetId: oldFilter ? oldFilter.formWidgetId : undefined,
                  formWidgetColumn: oldFilter ? oldFilter.formWidgetColumn : undefined,
                  // 参数值
                  paramValue: oldFilter ? oldFilter.paramValue : undefined,
                  paramDictDataList: [],
                };
                loadReportDictDataList(temp, getFilterValueDict(temp));
                return temp;
              } else {
                return null;
              }
            })
            .filter(item => item != null) as ANY_OBJECT[];
          oldFilterList.value = null;
        })
        .catch(e => {
          console.warn(e);
        });
    }
  },
);

onMounted(() => {
  if (dialogParams.value.rowData != null) {
    let groupPath = findTreeNodePath(
      dialogParams.value.groupList,
      dialogParams.value.rowData.groupId,
      'groupId',
    );
    formData.value = {
      id: dialogParams.value.rowData.id || new Date().getTime(),
      name:
        dialogParams.value.rowData.name ||
        SysCustomWidgetOperationType.getValue(dialogParams.value.rowData.type),
      type: dialogParams.value.rowData.type,
      groupId: groupPath,
      pageId: dialogParams.value.rowData.pageId,
      enabled: dialogParams.value.rowData.enabled,
      builtin: dialogParams.value.rowData.builtin,
    };
    oldFilterList.value = dialogParams.value.rowData.filterList;
    loadReportPage();
  }
});
</script>
