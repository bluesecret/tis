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
        <el-form-item label="" label-width="0px" prop="templateConfig">
          <MessageTemplateEditor v-model="formData.templateConfig" :options="templateOptions" />
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
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import MessageTemplateEditor from '@/components/MessageTemplateEditor/index.vue';
import { FormulaItemKind } from '@/common/staticDict/index';
import { useLayoutStore } from '@/store';

const layoutStore = useLayoutStore();

defineOptions({
  name: 'EditMessageTemplate',
});

interface IProps extends ThirdProps {
  data?: string;
  flowVariableList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
}

const props = withDefaults(defineProps<IProps>(), {});

const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const form = ref();
const formData = ref({
  templateConfig: '',
});

const rules = {
  templateConfig: [{ required: true, message: '请输入模版内容', trigger: 'blur' }],
};

const dialogParams = computed(() => {
  return {
    data: props.data || thirdParams.value.data || '',
    flowVariableList: props.flowVariableList || thirdParams.value.flowVariableList || [],
  };
});

const templateVariableList = computed(() => {
  const variableList = [];
  for (const item of dialogParams.value.flowVariableList) {
    variableList.push({
      itemName: item.name,
      itemCode: item.id,
      itemKind: FormulaItemKind.VARABLE,
    });
  }
  return variableList;
});

const templateOptions = computed(() => {
  return {
    variableList: templateVariableList.value,
  };
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog();
  }
};

const onSubmit = () => {
  form.value.validate(valid => {
    if (!valid) return;
    const val = formData.value.templateConfig;
    let templateList = [];
    if (val != null && val !== '') templateList = JSON.parse(val);
    let templateStr = undefined;
    if (Array.isArray(templateList) && templateList.length > 0) {
      templateStr = templateList.reduce((retObj, item) => {
        let temp = '';
        if (item.itemKind === FormulaItemKind.STRING) {
          // 字符串
          temp = item.itemName;
        } else if (item.itemKind === FormulaItemKind.VARABLE) {
          // 变量
          temp = '${' + item.itemCode + '}';
        }
        if (temp !== '' && retObj !== '') {
          retObj += ' ';
        }
        return retObj + temp;
      }, '');
    }
    if (props.dialog) {
      props.dialog.submit({
        templateConfig: formData.value.templateConfig,
        message: templateStr,
      });
    } else {
      onCloseThirdDialog(true, dialogParams.value.data, {
        templateConfig: formData.value.templateConfig,
        message: templateStr,
      });
    }
  });
};

onMounted(() => {
  if (dialogParams.value.data) {
    formData.value = {
      templateConfig: dialogParams.value.data,
    };
  }
});
</script>

<style></style>
