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
      <el-form-item label="父字段">
        <el-cascader
          v-model="parentPath"
          :options="dialogParams.usedFieldList"
          :props="{
            value: 'id',
            label: 'fieldName',
            children: 'children',
            checkStrictly: true,
            disabled: 'disabled',
          }"
          clearable
          filterable
          :disabled="dialogParams.data != null"
          placeholder="请选择父字段"
          @change="onParentChange"
        />
      </el-form-item>
      <el-form-item label="字段名" prop="fieldName">
        <el-input v-model="formData.fieldName" clearable :disabled="dialogParams.data != null" />
      </el-form-item>
      <el-form-item label="字段类型" prop="fieldType">
        <el-select v-model="formData.fieldType" clearable placeholder="请选择字段类型">
          <el-option v-for="item in ValidFieldTypes" :key="item" :label="item" :value="item" />
        </el-select>
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
import { ElMessage } from 'element-plus';
import { ANY_OBJECT } from '@/types/generic';
import { AutoTaskValueType } from '@/common/staticDict/flow';
import { findTreeNodePath, findTreeNodeObjectPath } from '@/common/utils';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const ValidFieldTypes = ref(['String', 'Number', 'Boolean', 'Object', 'Array']);
const layoutStore = useLayoutStore();
interface IProps extends ThirdProps {
  data?: ANY_OBJECT;
  usedFieldList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
}
const props = withDefaults(defineProps<IProps>(), {
  data: undefined,
  usedFieldList: undefined,
});
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const form = ref();
type FormDataType = {
  id: number | string | undefined;
  parentId: number | string | undefined;
  fieldName: string;
  fieldType: string;
};
const formData = ref<FormDataType>({
  id: undefined,
  parentId: undefined,
  fieldName: '',
  fieldType: 'String',
});
const parentPath = ref<Array<string | number>>([]);
const rules = {
  fieldName: [{ required: true, message: '请输入字段名', trigger: 'blur' }],
  fieldType: [{ required: true, message: '请输入字段类型', trigger: 'blur' }],
};
const dialogParams = computed(() => {
  return {
    data: props.data || thirdParams.value.data,
    usedFieldList: props.usedFieldList || thirdParams.value.usedFieldList || [],
  };
});
const parentNode = computed(() => {
  let parentNodePath = findTreeNodeObjectPath(
    dialogParams.value.usedFieldList,
    formData.value.parentId,
  );
  return parentNodePath ? parentNodePath[parentNodePath.length - 1] : null;
});

const onParentChange = (value: Array<string | number>) => {
  formData.value.parentId = parentPath.value[parentPath.value.length - 1];
};
const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};
const onSubmit = () => {
  let childList = parentNode.value ? parentNode.value.children : dialogParams.value.usedFieldList;
  if ((childList || []).some(item => item.fieldName === formData.value.fieldName)) {
    ElMessage.error('字段名已存在');
    return;
  }
  form.value.validate(valid => {
    if (valid) {
      formData.value.parentId = parentNode.value ? parentNode.value.id : undefined;
      formData.value.id = parentNode.value
        ? parentNode.value.id + '.' + formData.value.fieldName
        : formData.value.fieldName;
      if (props.dialog) {
        props.dialog.submit(formData.value);
      } else {
        onCloseThirdDialog(true, dialogParams.value.data, formData.value);
      }
    }
  });
};

onMounted(() => {
  if (dialogParams.value.data) {
    formData.value = {
      ...formData.value,
      ...dialogParams.value.data,
    };
    parentPath.value = findTreeNodePath(
      dialogParams.value.usedFieldList,
      dialogParams.value.data.parentId,
    );
  }
});
</script>

<style scoped></style>
