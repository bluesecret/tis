<template>
  <el-row class="edit-dataset-param third-party-dlg" style="flex-direction: row">
    <el-col class="form-box" :span="24">
      <DatasetParamList :datasetParamList="datasetParamList" />
    </el-col>
    <el-col class="menu-box" :span="24" style="margin-top: 15px">
      <el-row class="no-scroll flex-box" type="flex" justify="end">
        <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
        <el-button type="primary" :size="formItemSize" @click="onSubmit()"> 保存 </el-button>
      </el-row>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
import DatasetParamList from './datasetParamList.vue';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  paramList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const datasetParamList = ref<ANY_OBJECT[]>([]);
const dialogParams = computed(() => {
  return {
    paramList: props.paramList || thirdParams.value.paramList || [],
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
  if (props.dialog) {
    props.dialog.submit(datasetParamList.value);
  } else {
    onCloseThirdDialog(true, undefined, datasetParamList.value);
  }
};

watch(
  () => dialogParams.value.paramList,
  newValue => {
    datasetParamList.value = (newValue || []).map((item: ANY_OBJECT) => {
      return {
        ...item,
      };
    });
  },
  {
    immediate: true,
  },
);
</script>
