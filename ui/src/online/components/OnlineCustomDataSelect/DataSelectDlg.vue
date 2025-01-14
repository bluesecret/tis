<template>
  <div ref="formWrapper" class="form-wrapper">
    <el-row>
      <OnlineOneToOneForm
        ref="onlineForm"
        :height="tableHeight"
        :formConfig="dialogParams.formConfig"
        :selectedColumn="dialogParams.relativeTable.relativeColumn"
        :selectedValue="dialogParams.value"
        :extraData="dialogParams.extraData"
        @radioSelectChanged="onRadioChange"
        @submit="onSubmit"
      >
      </OnlineOneToOneForm>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import OnlineOneToOneForm from '@/pages/online/OnlinePageRender/OnlineOneToOneForm/index.vue';

interface IProps extends ThirdProps {
  value: string;
  relativeTable: ANY_OBJECT;
  formConfig: ANY_OBJECT;
  extraData?: ANY_OBJECT;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = defineProps<IProps>();

const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const formWrapper = ref();
const dialogSelectItems = ref<ANY_OBJECT>({});
const tableHeight = ref('');

const dialogParams = computed(() => {
  return {
    value: props.value || thirdParams.value.value,
    formConfig: props.formConfig || thirdParams.value.formConfig,
    relativeTable: props.relativeTable || thirdParams.value.relativeTable || {},
    extraData: props.extraData || thirdParams.value.extraData || {},
  };
});

const onSubmit = () => {
  if (props.dialog) {
    props.dialog.submit(dialogSelectItems.value);
  } else {
    onCloseThirdDialog(true, dialogParams.value.value, dialogSelectItems.value);
  }
};
const onRadioChange = (data: ANY_OBJECT) => {
  dialogSelectItems.value = data;
};

onMounted(() => {
  nextTick(() => {
    tableHeight.value = formWrapper.value.offsetHeight + 'px';
  });
});
</script>

<style lang="scss" scoped>
.form-wrapper {
  position: relative;
  height: 100%;
}
.form-footer {
  position: absolute;
  bottom: 0;
  width: 100%;
  text-align: right;

  .btn-confirm {
    display: inline-block;
    height: 28px;
  }
}
</style>
