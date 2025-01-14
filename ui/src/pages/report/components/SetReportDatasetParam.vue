<template>
  <el-row class="set-dataset-param third-party-dlg">
    <el-col :span="24" class="form-box">
      <el-table
        :data="getDatasetParamList"
        size="default"
        header-cell-class-name="table-header-gray"
        border
        height="460px"
      >
        <el-table-column type="index" width="55px" label="序号" />
        <el-table-column label="参数名称" prop="paramName" width="200px" />
        <el-table-column label="参数值类型" prop="filterValueType" width="200px">
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
        </el-table-column>
        <el-table-column label="参数值" prop="paramValue">
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
            />
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
        </el-table-column>
        <template v-slot:empty>
          <div class="table-empty unified-font">
            <img src="@/assets/img/empty.png" />
            <span>暂无数据</span>
          </div>
        </template>
      </el-table>
    </el-col>
    <el-row class="no-scroll menu-box" justify="end" style="margin-top: 15px">
      <el-button :size="itemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="itemSize" @click="onSubmit"> 保存 </el-button>
    </el-row>
  </el-row>
</template>

<script setup lang="ts">
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
  formParamList?: string[];
  validFilterValueType?: number[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = defineProps<IProps>();
const itemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const dialogParams = computed(() => {
  return {
    datasetParamList:
      props.datasetParamList || (thirdParams.value.datasetParamList as ANY_OBJECT[]) || [],
    datasetFilterParams: props.datasetFilterParams || thirdParams.value.datasetFilterParams || [],
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
  if (props.dialog) {
    props.dialog.submit(getDatasetParamList.value.filter(item => item.paramValue != null));
  } else {
    onCloseThirdDialog(
      true,
      getDatasetParamList.value.filter(item => item.paramValue != null),
    );
  }
};
const onFilterValueTypeChange = (row: ANY_OBJECT) => {
  row.paramValue = undefined;
};
</script>
