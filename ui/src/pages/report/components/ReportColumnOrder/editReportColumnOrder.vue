<template>
  <div class="edit-report-template-order third-party-dlg">
    <el-form
      class="form-box"
      ref="form"
      :model="formData"
      :rules="rules"
      label-width="80px"
      :size="itemSize"
      @submit.prevent
    >
      <el-row>
        <el-col :span="24">
          <el-row class="full-width-input">
            <el-col :span="24">
              <el-form-item label="排序字段" prop="fieldName">
                <el-select v-model="formData.fieldName" placeholder="" clearable>
                  <el-option
                    v-for="item in dialogParams.columnList"
                    :key="item.columnName"
                    :label="item.columnName"
                    :value="item.columnName"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="排序类型" prop="asc" required>
                <el-radio-group v-model="formData.asc">
                  <el-radio-button :value="true">升序</el-radio-button>
                  <el-radio-button :value="false">降序</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="顺序" prop="showOrder">
                <el-input-number v-model="formData.showOrder" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
    </el-form>
    <el-row type="flex" class="menu-box" style="margin-top: 15px" justify="end">
      <el-button :size="itemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="itemSize" @click="onSubmit"> 确定 </el-button>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  rowData?: ANY_OBJECT;
  columnList?: ANY_OBJECT[];
  usedColumnList?: ANY_OBJECT[];
  maxOrder?: number;
  defaultFormItemSize: Ref<'' | 'default' | 'small' | 'large'>;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = defineProps<IProps>();
const itemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const form = ref();
const formData = ref<ANY_OBJECT>({
  fieldName: undefined,
  asc: true,
  showOrder: 0,
});
const rules: ANY_OBJECT = {
  fieldName: [{ required: true, message: '排序字段不能为空', trigger: 'blur' }],
  showOrder: [{ required: true, message: '排序顺序不能为空', trigger: 'blur' }],
};

const dialogParams = computed(() => {
  return {
    rowData: props.rowData || thirdParams.value.rowData,
    columnList: props.columnList || thirdParams.value.columnList || [],
    usedColumnList: props.usedColumnList || [], // thirdParams.value.usedColumnList || [],
    maxOrder: props.maxOrder, // || thirdParams.value.maxOrder,
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
    if (
      dialogParams.value.usedColumnList != null &&
      dialogParams.value.usedColumnList.indexOf(formData.value.fieldName) !== -1
    ) {
      ElMessage.error('此字段已被使用，不能重复添加！');
      return;
    }
    if (props.dialog) {
      props.dialog.submit(formData.value);
    } else {
      onCloseThirdDialog(true, dialogParams.value.rowData, formData.value);
    }
  });
};

onMounted(() => {
  formData.value.showOrder =
    (dialogParams.value && dialogParams.value.maxOrder ? dialogParams.value.maxOrder : 0) + 1;
  if (dialogParams.value.rowData != null) {
    formData.value = {
      ...formData.value,
      ...dialogParams.value.rowData,
    };
  }
});
</script>
