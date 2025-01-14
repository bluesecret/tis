<template>
  <el-row class="form-edit-widget-order third-party-dlg">
    <el-form
      ref="formEditWidgetOrder"
      :model="formData"
      class="full-width-input form-box"
      :rules="rules"
      style="width: 100%"
      label-width="80px"
      :size="formItemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="排序方式" prop="orderType">
            <el-radio-group v-model="formData.orderType">
              <el-radio-button
                v-for="item in OrderType.getList()"
                :key="item.id"
                :label="item.id"
                >{{ item.name }}</el-radio-button
              >
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="排序字段" prop="id">
            <el-select v-model="formData.id" placeholder="" clearable @change="onColumnChange">
              <el-option
                v-for="item in dialogParams.columnList"
                :key="item.id"
                :label="item.showName"
                :value="item.id"
                :disabled="item.disabled"
              >
                <el-row type="flex" justify="space-between" align="middle">
                  <span>{{ item.showName }}</span>
                  <el-tag
                    size="default"
                    v-if="item.isIndex !== undefined"
                    :type="item.isIndex ? 'primary' : 'success'"
                    style="margin-left: 100px"
                  >
                    {{ item.isIndex ? '值轴' : '分类轴' }}
                  </el-tag>
                </el-row>
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-row class="no-scroll menu-box" type="flex" justify="end">
      <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="formItemSize" @click="onSubmit"> 保存 </el-button>
    </el-row>
  </el-row>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { findItemFromList } from '@/common/utils';
import { OrderType } from '@/common/staticDict/report';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  rowData?: ANY_OBJECT;
  columnList: ANY_OBJECT[];
  defaultFormItemSize: Ref<'' | 'default' | 'small' | 'large'>;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
}
const props = withDefaults(defineProps<IProps>(), {});
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const { thirdParams, onCloseThirdDialog } = useThirdParty(props);

const formEditWidgetOrder = ref();
const formData = ref<ANY_OBJECT>({
  id: undefined,
  orderColumnId: undefined,
  calculateType: undefined,
  orderType: OrderType.ASC,
});
const rules = {
  id: [{ required: true, message: '请选择排序字段！', trigger: 'blur' }],
  orderType: [{ required: true, message: '请选择排序类型！', trigger: 'blur' }],
};
const dialogParams = computed(() => {
  return {
    rowData: props.rowData || thirdParams.value.rowData,
    columnList: props.columnList || thirdParams.value.columnList || [],
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
  formEditWidgetOrder.value.validate((valid: boolean) => {
    if (!valid) return;
    if (props.dialog) {
      props.dialog.submit(formData.value);
    } else {
      onCloseThirdDialog(true, dialogParams.value.rowData, formData.value);
    }
  });
};
const onColumnChange = (val: string) => {
  let column: ANY_OBJECT | null = findItemFromList(dialogParams.value.columnList, val, 'id');
  formData.value.calculateType = (column || {}).calculateType;
  formData.value.orderColumnId = column?.columnId;
};

onMounted(() => {
  if (dialogParams.value.rowData != null) {
    formData.value = {
      ...dialogParams.value.rowData,
    };
  } else {
    formData.value.orderType = OrderType.ASC;
  }
});
</script>
