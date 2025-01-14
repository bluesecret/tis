<template>
  <div class="form-single-fragment third-party-dlg" style="position: relative; height: 100%">
    <el-form
      ref="form"
      :model="formData"
      class="full-width-input form-box"
      :rules="rules"
      style="width: 100%"
      label-width="100px"
      :size="layoutStore.defaultFormItemSize"
      label-position="right"
      @submit.prevent
    >
      <el-form-item label="过滤字段" prop="filterColumnName">
        <el-select
          v-model="formData.filterColumnName"
          clearable
          filterable
          :disabled="dialogParams.data != null"
          placeholder="请选择过滤字段"
        >
          <el-option
            v-for="item in columnList"
            :key="item.columnName"
            :label="item.columnName"
            :value="item.columnName"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="过滤类型">
        <el-select v-model="formData.filterType" placeholder="请选择过滤类型">
          <el-option
            v-for="item in CriteriaFilterType.getList()"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="字段值类型">
        <el-select
          v-model="formData.valueType"
          placeholder="请选择过滤值类型"
          @change="onValueTypeChange"
        >
          <el-option
            :label="AutoTaskValueType.getValue(AutoTaskValueType.FIXED)"
            :value="AutoTaskValueType.FIXED"
          />
          <el-option
            :label="AutoTaskValueType.getValue(AutoTaskValueType.VARIABLE)"
            :value="AutoTaskValueType.VARIABLE"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="过滤值" prop="filterValue">
        <!-- 固定值 -->
        <el-input
          v-if="formData.valueType === AutoTaskValueType.FIXED"
          v-model="formData.filterValue"
          clearable
          placeholder="请输入过滤值"
        />
        <!-- 流程变量 -->
        <el-cascader
          v-else-if="formData.valueType === AutoTaskValueType.VARIABLE"
          :options="dialogParams.flowVariableList"
          :props="{ value: 'id', label: 'name', children: 'children' }"
          v-model="formData.filterValue"
          clearable
          filterable
          placeholder="请选择流程变量"
        />
      </el-form-item>
    </el-form>
    <el-row class="no-scroll flex-box menu-box" type="flex" justify="end" style="padding-top: 10px">
      <el-button :size="layoutStore.defaultFormItemSize" :plain="true" @click="onCancel()">
        取消
      </el-button>
      <el-button type="primary" :size="layoutStore.defaultFormItemSize" @click="onSubmit()">
        保存
      </el-button>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { AutoTaskValueType } from '@/common/staticDict/flow';
import { FlowDblinkController, FlowEntryVariableController } from '@/api/flow';
import { CriteriaFilterType } from '@/common/staticDict/index';
import { findTreeNodePath } from '@/common/utils';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';

const layoutStore = useLayoutStore();
interface IProps extends ThirdProps {
  dblinkId?: string;
  tableName?: string;
  data?: ANY_OBJECT;
  flowVariableList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
}
const props = withDefaults(defineProps<IProps>(), {
  dblinkId: undefined,
  tableName: undefined,
  data: undefined,
  flowVariableList: undefined,
});
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const form = ref();
type FormDataType = {
  id: number | undefined;
  filterColumnName: string;
  filterType: number;
  valueType: number;
  filterValue?: string | string[];
};
const formData = ref<FormDataType>({
  id: undefined,
  filterColumnName: '',
  filterType: 0,
  valueType: AutoTaskValueType.FIXED,
  filterValue: '',
});
const columnList = ref<ANY_OBJECT[]>([]);
const rules = {
  filterColumnName: [{ required: true, message: '请选择过滤字段', trigger: 'blur' }],
  filterType: [{ required: true, message: '请选择过滤类型', trigger: 'blur' }],
  filterValue: [{ required: true, message: '请输入过滤值', trigger: 'blur' }],
};
const dialogParams = computed(() => {
  return {
    dblinkId: props.dblinkId || thirdParams.value.dblinkId,
    tableName: props.tableName || thirdParams.value.tableName,
    data: props.data || thirdParams.value.data,
    flowVariableList: props.flowVariableList || thirdParams.value.flowVariableList,
  };
});
const onValueTypeChange = () => {
  formData.value.filterValue = undefined;
};
const getColumnList = () => {
  if (dialogParams.value.dblinkId == null || dialogParams.value.tableName == null) {
    return;
  }
  FlowDblinkController.listDblinkTableColumns({
    dblinkId: dialogParams.value.dblinkId,
    tableName: dialogParams.value.tableName,
  }).then(res => {
    columnList.value = res.data;
  });
};

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};

const onSubmit = () => {
  form.value.validate(valid => {
    if (valid) {
      let tempFilterValue;
      if (formData.value.valueType === AutoTaskValueType.VARIABLE) {
        tempFilterValue = Array.isArray(formData.value.filterValue)
          ? formData.value.filterValue[formData.value.filterValue.length - 1]
          : undefined;
      }
      let tempData = {
        ...formData.value,
        filterValue: tempFilterValue || formData.value.filterValue,
      };
      if (tempData.id == null) tempData.id = new Date().getTime();
      if (props.dialog) {
        props.dialog.submit(tempData);
      } else {
        onCloseThirdDialog(true, dialogParams.value.data, tempData);
      }
    }
  });
};

onMounted(() => {
  getColumnList();
  if (dialogParams.value.data) {
    let filterValue = dialogParams.value.data.filterValue;
    if (dialogParams.value.data.valueType === AutoTaskValueType.VARIABLE) {
      filterValue = findTreeNodePath(dialogParams.value.flowVariableList, filterValue);
    }
    formData.value = {
      ...dialogParams.value.data,
      valueType: dialogParams.value.data.valueType || 0,
      filterValue,
    };
  }
});
</script>

<style></style>
