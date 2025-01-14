<template>
  <el-row class="set-dataset-param third-party-dlg">
    <el-col class="form-box" :span="24">
      <vxe-table
        :data="getDatasetParamList"
        :size="formItemSize as SizeType"
        :row-config="{ isHover: true }"
        header-cell-class-name="table-header-gray"
        height="450px"
      >
        <vxe-column type="seq" width="55px" title="序号" />
        <vxe-column title="参数名称" field="paramName" width="200px" />
        <vxe-column title="参数值类型" field="filterValueType" width="200px">
          <template v-slot="scope">
            <el-select
              v-model="scope.row.filterValueType"
              placeholder=""
              size="default"
              @change="onFilterValueTypeChange(scope.row)"
            >
              <el-option
                v-for="item in dialogParams.validFilterValueType"
                :key="item"
                :label="FilterValueKind.getValue(item)"
                :value="item"
              />
            </el-select>
          </template>
        </vxe-column>
        <vxe-column title="参数值" field="paramValue">
          <template v-slot="scope">
            <!-- 自定义参数值 -->
            <el-input
              v-if="
                scope.row.paramType === 'String' &&
                scope.row.filterValueType === FilterValueKind.INPUT_DATA
              "
              v-model="scope.row.paramValue"
              size="default"
              style="width: 100%"
            />
            <el-input-number
              v-if="
                scope.row.paramType === 'Number' &&
                scope.row.filterValueType === FilterValueKind.INPUT_DATA
              "
              v-model="scope.row.paramValue"
              size="default"
              style="width: 100%"
            />
            <el-date-picker
              v-if="
                scope.row.paramType === 'Date' &&
                scope.row.filterValueType === FilterValueKind.INPUT_DATA
              "
              type="datetime"
              v-model="scope.row.paramValue"
              size="default"
              style="width: 100%"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
            <!-- 组件数据参数 -->
            <el-select
              v-if="scope.row.filterValueType === FilterValueKind.WIDGET_DATA"
              v-model="scope.row.formWidgetId"
              key="formWidgetId"
              style="width: 100%"
              size="default"
              clearable
              placeholder=""
            >
              <el-option
                v-for="widget in dialogParams.widgetList"
                :key="widget.widgetId"
                :label="widget.showName"
                :value="widget.widgetId"
              />
            </el-select>
            <!-- 打印模版参数 / 表单输入参数 -->
            <el-select
              v-if="
                scope.row.filterValueType === FilterValueKind.PRINT_INPUT_PARAM ||
                scope.row.filterValueType === FilterValueKind.FORM_PARAM
              "
              v-model="scope.row.paramValue"
              style="width: 100%"
              size="default"
              clearable
              placeholder=""
            >
              <el-option
                v-for="item in dialogParams.formParamList"
                :key="item"
                :label="item"
                :value="item"
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
    <el-row class="no-scroll menu-box" type="flex" justify="end" style="margin-top: 15px">
      <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="formItemSize" @click="onSubmit"> 保存 </el-button>
    </el-row>
  </el-row>
</template>

<script setup lang="ts">
import { VxeTable, VxeColumn, SizeType } from 'vxe-table';
import { findItemFromList } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { FilterValueKind } from '@/common/staticDict/report';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  datasetParamList?: ANY_OBJECT[];
  datasetFilterParams?: ANY_OBJECT[];
  formParamList?: (string | number)[];
  widgetList?: ANY_OBJECT[];
  validFilterValueType?: (string | number)[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = withDefaults(defineProps<IProps>(), {});
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const dialogParams = computed(() => {
  return {
    datasetParamList:
      props.datasetParamList || (thirdParams.value.datasetParamList as ANY_OBJECT[]) || [],
    widgetList: props.widgetList || thirdParams.value.widgetList || [],
    datasetFilterParams:
      props.datasetFilterParams || (thirdParams.value.datasetFilterParams as ANY_OBJECT[]) || [],
    formParamList: props.formParamList || thirdParams.value.formParamList || [],
    validFilterValueType:
      props.validFilterValueType || thirdParams.value.validFilterValueType || [],
  };
});
const getDatasetParamList = computed(() => {
  return dialogParams.value.datasetParamList.map(item => {
    let temp =
      findItemFromList(dialogParams.value.datasetFilterParams, item.paramName, 'paramName') || {};
    return {
      ...item,
      filterValueType: temp.filterValueType || FilterValueKind.INPUT_DATA,
      paramValue: temp.paramValue,
      formWidgetId: temp.formWidgetId,
    };
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
  let temp = getDatasetParamList.value.filter(item => {
    if (item.filterValueType !== FilterValueKind.WIDGET_DATA) {
      return item.paramValue != null;
    } else {
      return item.formWidgetId != null;
    }
  });
  if (props.dialog) {
    props.dialog.submit(temp);
  } else {
    onCloseThirdDialog(true, null, temp);
  }
};
const onFilterValueTypeChange = (row: ANY_OBJECT) => {
  row.paramValue = undefined;
  row.formWidgetId = undefined;
};
</script>
