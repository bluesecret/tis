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
      label-position="top"
      @submit.prevent
    >
      <el-scrollbar>
        <el-form-item label="" label-width="0px" prop="formulaConfig">
          <FormulaEditor v-model="formData.formulaConfig" :options="formulatOptions" />
        </el-form-item>
      </el-scrollbar>
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
import { FormulaItemKind } from '@/common/staticDict/index';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import FormulaEditor from '@/components/FormulaEditor/index.vue';
import { useLayoutStore } from '@/store';

const layoutStore = useLayoutStore();
interface IProps extends ThirdProps {
  data?: string;
  flowVariableList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
}

const props = withDefaults(defineProps<IProps>(), {
  data: undefined,
  flowVariableList: undefined,
});
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const form = ref();
type FormDataType = {
  formulaConfig?: string;
};
const formData = ref<FormDataType>({
  formulaConfig: '',
});
const rules = {
  formulaConfig: [{ required: true, message: '请输入表达式', trigger: 'blur' }],
};
const dialogParams = computed(() => {
  return {
    data: props.data || thirdParams.value.data,
    flowVariableList: props.flowVariableList || thirdParams.value.flowVariableList,
  };
});
const formulaVariableList = computed(() => {
  let variableTree = dialogParams.value.flowVariableList;
  // 遍历树节点，获取所有叶子节点的变量
  let variableList = [];
  const getLeafVariable = (root: ANY_OBJECT, treeNode: ANY_OBJECT) => {
    if (treeNode.children && treeNode.children.length > 0) {
      treeNode.children.forEach((item: ANY_OBJECT) => {
        getLeafVariable(root, item);
      });
    } else {
      variableList.push({
        itemName: root.name + '.' + treeNode.id,
        itemCode: treeNode.id,
        itemKind: FormulaItemKind.VARABLE,
      });
    }
  };
  variableTree.forEach((item: ANY_OBJECT) => {
    getLeafVariable(item, item);
  });
  return variableList;
});

const formulatOptions = computed(() => {
  return {
    variableList: formulaVariableList.value,
  };
});
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
      if (props.dialog) {
        props.dialog.submit(formData.value.formulaConfig);
      } else {
        onCloseThirdDialog(true, dialogParams.value.data, formData.value.formulaConfig);
      }
    }
  });
};

onMounted(() => {
  if (dialogParams.value.data) {
    formData.value.formulaConfig = dialogParams.value.data;
  }
});
</script>

<style scoped></style>
