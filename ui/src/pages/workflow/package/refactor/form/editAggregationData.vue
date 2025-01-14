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
      <el-form-item label="聚合函数" prop="aggregationFunction">
        <el-select v-model="formData.aggregationFunction" placeholder="请选择聚合函数">
          <el-option
            v-for="item in AutoTaskAggregationType.getList()"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="聚合字段" prop="aggregationColumn">
        <el-select
          v-model="formData.aggregationColumn"
          clearable
          filterable
          placeholder="请选择聚合字段"
        >
          <el-option
            v-for="item in columnList"
            :key="item.columnName"
            :label="item.columnName"
            :value="item.columnName"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="字段别名" prop="alias">
        <el-input
          v-model="formData.alias"
          clearable
          placeholder="请输入聚合字段别名（只允许英文）"
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
import { AutoTaskAggregationType } from '@/common/staticDict/flow';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
import { FlowDblinkController } from '@/api/flow';

const layoutStore = useLayoutStore();
interface IProps extends ThirdProps {
  srcDblinkId?: string;
  srcTableName?: string;
  data?: ANY_OBJECT;
  flowVariableList?: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
}
const props = withDefaults(defineProps<IProps>(), {
  srcDblinkId: undefined,
  srcTableName: undefined,
  data: undefined,
  flowVariableList: undefined,
  dialog: undefined,
});
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const form = ref();

type FormDataType = {
  id: number | undefined;
  aggregationFunction: number;
  aggregationColumn: string;
  alias: string;
};

const formData = ref<FormDataType>({
  id: undefined,
  aggregationFunction: AutoTaskAggregationType.SUM,
  aggregationColumn: '',
  alias: '',
});

const rules = ref({
  aggregationFunction: [{ required: true, message: '请选择聚合函数', trigger: 'blur' }],
  aggregationColumn: [{ required: true, message: '请选择聚合字段', trigger: 'blur' }],
  alias: [
    { required: true, message: '请输入聚合字段别名', trigger: 'blur' },
    // 只允许英文字符
    {
      pattern: /^[a-zA-Z]+$/,
      message: '聚合字段别名只允许英文字符',
      trigger: 'blur',
    },
  ],
});

const columnList = ref<ANY_OBJECT[]>([]);
const dialogParams = computed(() => {
  return {
    srcDblinkId: props.srcDblinkId || thirdParams.value.srcDblinkId,
    srcTableName: props.srcTableName || thirdParams.value.srcTableName,
    data: props.data || thirdParams.value.data,
    flowVariableList: props.flowVariableList || thirdParams.value.flowVariableList,
  };
});

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
    columnList.value = res.data;
  });
};

const onSubmit = () => {
  form.value.validate(valid => {
    if (valid) {
      let temp = {
        ...formData.value,
      };
      if (temp.id == null) temp.id = new Date().getTime();
      if (props.dialog) {
        props.dialog.submit(temp);
      } else {
        onCloseThirdDialog(true, dialogParams.value.data, temp);
      }
    }
  });
};

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};

onMounted(() => {
  getSrcColumnList();
  if (dialogParams.value.data) {
    formData.value = {
      ...dialogParams.value.data,
    };
  }
});
</script>
