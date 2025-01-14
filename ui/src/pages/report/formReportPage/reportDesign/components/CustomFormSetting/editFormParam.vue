<template>
  <el-row third-party-dlg>
    <el-form
      ref="form"
      :model="formData"
      class="full-width-input form-box"
      :rules="rules"
      style="width: 100%"
      label-width="100px"
      :size="formItemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="参数名称" prop="variableName">
            <el-input v-model="formData.variableName" clearable />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-col :span="24" class="menu-box">
      <el-row class="no-scroll flex-box" type="flex" justify="end">
        <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
        <el-button type="primary" :size="formItemSize" @click="onSubmit"> 保存 </el-button>
      </el-row>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { DialogProp } from '@/components/Dialog/types';
import { ANY_OBJECT } from '@/types/generic';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  rowData: ANY_OBJECT;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const form = ref();
const formData = ref<ANY_OBJECT>({
  variableName: undefined,
});
const rules = {
  variableName: [
    {
      required: true,
      message: '参数名称不能为空！',
      trigger: 'blur',
    },
    {
      pattern: /^[a-z][A-Za-z]+$/,
      message: '参数名称只支持小驼峰规则英文字母',
      trigger: 'blur',
    },
  ],
};

const dialogParams = computed(() => {
  return {
    rowData: props.rowData || thirdParams.value.rowData,
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
  form.value.validate((valid: boolean) => {
    if (!valid) return;
    if (props.dialog) {
      props.dialog.submit(formData.value.variableName);
    } else {
      onCloseThirdDialog(true, dialogParams.value.rowData, formData.value.variableName);
    }
  });
};

onMounted(() => {
  if (dialogParams.value.rowData) {
    formData.value.variableName = dialogParams.value.rowData.variableName;
  }
});
</script>
