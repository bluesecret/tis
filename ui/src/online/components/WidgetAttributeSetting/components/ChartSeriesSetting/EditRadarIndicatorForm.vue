<template>
  <el-row class="form-edit-widget-categroy-column third-party-dlg">
    <el-form
      ref="form"
      :model="formData"
      class="full-width-input form-box"
      style="width: 100%"
      label-width="100px"
      :size="itemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="最大值" prop="max">
            <el-input-number
              v-model="formData.max"
              controls-position="right"
              placeholder=""
              clearable
            />
          </el-form-item>
        </el-col>
        <el-col :span="24" prop="min">
          <el-form-item label="最小值">
            <el-input-number
              v-model="formData.min"
              controls-position="right"
              placeholder=""
              clearable
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-row class="no-scroll menu-box" type="flex" justify="end">
      <el-button :size="itemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="itemSize" @click="onSubmit"> 保存 </el-button>
    </el-row>
  </el-row>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

const props = defineProps<{
  rowData: ANY_OBJECT;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
}>();
const itemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const form = ref();
const formData = ref<ANY_OBJECT>({
  columnId: undefined,
  min: 0,
  max: 10000,
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  }
};
const onSubmit = () => {
  form.value.validate((valid: boolean) => {
    if (!valid) return;
    if (props.dialog) {
      props.dialog.submit(formData.value);
    }
  });
};

onMounted(() => {
  formData.value.text = props.rowData.text;
  formData.value.min = props.rowData.min;
  formData.value.max = props.rowData.max;
  formData.value.columnId = props.rowData.columnId;
});
</script>
