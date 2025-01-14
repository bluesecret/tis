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
      <el-form-item label="目标表字段" prop="destColumnName">
        <el-select
          v-model="formData.destColumnName"
          clearable
          filterable
          :disabled="dialogParams.data != null"
          placeholder="请选择字段名"
        >
          <el-option
            v-for="item in columnList"
            :key="item.columnName"
            :label="item.columnName"
            :value="item.columnName"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="字段值类型">
        <el-select
          v-model="formData.type"
          clearable
          placeholder="请选择值类型"
          @change="onValueTypeChange"
        >
          <el-option
            :label="AutoTaskValueType.getValue(AutoTaskValueType.FIXED)"
            :value="AutoTaskValueType.FIXED"
          />
          <el-option
            v-if="dialogParams.srcDblinkId != null && dialogParams.srcTableName != null"
            :label="AutoTaskValueType.getValue(AutoTaskValueType.FIELD)"
            :value="AutoTaskValueType.FIELD"
          />
          <el-option
            :label="AutoTaskValueType.getValue(AutoTaskValueType.VARIABLE)"
            :value="AutoTaskValueType.VARIABLE"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="字段值" prop="srcValue">
        <!-- 固定值 -->
        <el-input
          v-if="formData.type === AutoTaskValueType.FIXED"
          v-model="formData.srcValue"
          clearable
          placeholder="请输入字段值"
        />
        <!-- 表字段名 -->
        <el-select
          v-else-if="formData.type === AutoTaskValueType.FIELD"
          v-model="formData.srcValue"
          clearable
          filterable
          placeholder="请选择字段名"
        >
          <el-option
            v-for="item in srcColumnList"
            :key="item.columnName"
            :label="item.columnName"
            :value="item.columnName"
          />
        </el-select>
        <!-- 流程变量 -->
        <el-cascader
          v-else-if="formData.type === AutoTaskValueType.VARIABLE"
          :options="dialogParams.flowVariableList"
          :props="{ value: 'id', label: 'name', children: 'children' }"
          v-model="formData.srcValue"
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
import { findTreeNodePath } from '@/common/utils';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
import { FlowDblinkController } from '@/api/flow';

const layoutStore = useLayoutStore();
interface IProps extends ThirdProps {
  dblinkId?: string;
  tableName?: string;
  srcDblinkId?: string;
  srcTableName?: string;
  data?: ANY_OBJECT;
  flowVariableList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
}
const props = withDefaults(defineProps<IProps>(), {
  dblinkId: undefined,
  tableName: undefined,
  srcDblinkId: undefined,
  srcTableName: undefined,
  data: undefined,
  flowVariableList: undefined,
});
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const form = ref();
type FormDataType = {
  type: number | string;
  destColumnName: string;
  srcValue: string | string[] | undefined;
};
const formData = ref<FormDataType>({
  type: AutoTaskValueType.FIXED,
  destColumnName: '',
  srcValue: '',
});
const columnList = ref<ANY_OBJECT[]>([]);
const srcColumnList = ref<ANY_OBJECT[]>([]);
const rules = ref({
  destColumnName: [{ required: true, message: '请输入字段名', trigger: 'blur' }],
  srcValue: [{ required: true, message: '请输入字段值', trigger: 'blur' }],
});
const dialogParams = computed(() => {
  return {
    dblinkId: props.dblinkId || thirdParams.value.dblinkId,
    tableName: props.tableName || thirdParams.value.tableName,
    srcDblinkId: props.srcDblinkId || thirdParams.value.srcDblinkId,
    srcTableName: props.srcTableName || thirdParams.value.srcTableName,
    data: props.data || thirdParams.value.data,
    flowVariableList: props.flowVariableList || thirdParams.value.flowVariableList || [],
  };
});

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

const getSrcColumnList = () => {
  if (
    dialogParams.value.srcDblinkId == null ||
    dialogParams.value.srcTableName == null ||
    dialogParams.value.srcDblinkId === '' ||
    dialogParams.value.srcTableName === ''
  ) {
    return;
  }
  FlowDblinkController.listDblinkTableColumns({
    dblinkId: dialogParams.value.srcDblinkId,
    tableName: dialogParams.value.srcTableName,
  }).then(res => {
    srcColumnList.value = res.data;
  });
};

const onValueTypeChange = () => {
  formData.value.srcValue = undefined;
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
      let srcValue: string | string[] | undefined;
      srcValue = formData.value.srcValue;
      if (formData.value.type === AutoTaskValueType.VARIABLE) {
        srcValue = Array.isArray(formData.value.srcValue)
          ? formData.value.srcValue[formData.value.srcValue.length - 1]
          : undefined;
      }
      let tempData = {
        ...formData.value,
        srcValue,
      };
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
  getSrcColumnList();
  if (dialogParams.value.data) {
    let srcValue = dialogParams.value.data.srcValue;
    if (dialogParams.value.data.type === AutoTaskValueType.VARIABLE) {
      srcValue = findTreeNodePath(dialogParams.value.flowVariableList, srcValue);
    }
    formData.value = {
      ...formData.value,
      ...dialogParams.value.data,
      srcValue,
    };
  }
});
</script>

<style></style>
