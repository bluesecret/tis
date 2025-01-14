<template>
  <el-popover trigger="click" width="300px" @show="onInit" :visible="isShow">
    <el-form
      ref="dictData"
      :model="formData"
      class="full-width-input"
      :rules="rules"
      style="width: 100%"
      label-width="100px"
      :size="formItemSize"
      label-position="right"
      @submit.prevent
    >
      <el-form-item label="字典键类型">
        <el-radio-group v-model="formData.type" :disabled="value != null">
          <el-radio-button value="Integer">整数</el-radio-button>
          <el-radio-button value="String">字符串</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="formData.type === 'String'" label="字典键数据" prop="id">
        <el-input v-model="formData.id" :disabled="props.value != null" />
      </el-form-item>
      <el-form-item v-if="formData.type === 'Integer'" label="字典键数据" prop="id">
        <el-input-number v-model="formData.id" :disabled="props.value != null" />
      </el-form-item>
      <el-form-item label="字典值数据" prop="id">
        <el-input v-model="formData.name" />
      </el-form-item>
    </el-form>
    <el-row type="flex" justify="end">
      <el-button :size="formItemSize" :plain="true" @click="onCancel">取消</el-button>
      <el-button :size="formItemSize" type="primary" @click="onSubmit">保存</el-button>
    </el-row>
    <template #reference>
      <el-button
        :class="btnClass"
        :size="formItemSize"
        link
        type="primary"
        @click="isShow = true"
        >{{ btnText }}</el-button
      >
    </template>
  </el-popover>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { ANY_OBJECT } from '@/types/generic';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

const emit = defineEmits<{ save: [ANY_OBJECT, ANY_OBJECT | undefined] }>();
const props = withDefaults(
  defineProps<{
    btnText: string;
    width?: string;
    value?: ANY_OBJECT;
    btnClass?: string;
    dictList?: Array<ANY_OBJECT>;
  }>(),
  { width: '200px' },
);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const dictData = ref();
const isShow = ref(false);
const formData = ref<ANY_OBJECT>({
  type: 'Integer',
  id: undefined,
  name: undefined,
});
const rules = {
  id: [{ required: true, message: '字典键数据不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '字典值数据不能为空', trigger: 'blur' }],
};

const usedDictList = computed(() => {
  let tempList = props.dictList?.filter(item => {
    if (props.value == null) return true;
    return item.id !== props.value.id;
  });

  return tempList;
});

const onCancel = () => {
  isShow.value = false;
};
const onSubmit = () => {
  dictData.value.validate((valid: boolean) => {
    if (!valid) return;
    if (usedDictList.value?.find(item => item.id === formData.value.id)) {
      ElMessage.error('字典键数据已存在');
      return;
    }
    if (formData.value.type === 'Integer') formData.value.id = Number.parseInt(formData.value.id);
    if (formData.value.type === 'String') formData.value.id = formData.value.id.toString();
    emit('save', formData.value, props.value);
    isShow.value = false;
  });
};
const onInit = () => {
  formData.value = { type: 'Integer' };
  if (props.value) {
    formData.value = { ...props.value };
  }
};
</script>
