<template>
  <div class="autotask-calc-settting">
    <el-form-item label="表达式" prop="calculateFormula">
      <template #label>
        <el-row type="flex" justify="space-between">
          <span>表达式</span>
          <el-button
            size="small"
            link
            type="primary"
            @click="onEditFormulaData(formData.formulaConfig)"
          >
            编辑
          </el-button>
        </el-row>
      </template>
      <el-input
        v-model="formData.calculateFormula"
        type="textarea"
        placeholder="请输入表达式"
        clearable
        rows="7"
        readonly
        @change="onChange"
      />
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';
import EditFormulaData from './editFormulaData.vue';
import { FormulaItemKind } from '@/common/staticDict/index';
import { AutoTaskActionType, AutoTaskValueType } from '@/common/staticDict/flow';
import { Dialog } from '@/components/Dialog';
import { ANY_OBJECT } from '@/types/generic';

const emit = defineEmits(['update:modelValue', 'change']);
const props = defineProps<{
  modelValue: string;
}>();
type FormDataType = {
  actionType?: number;
  calculateFormula: string;
  formulaConfig?: string;
};
const formData = ref<FormDataType>({
  actionType: AutoTaskActionType.FORMULA,
  calculateFormula: '',
  formulaConfig: '',
});
const getAllAutoVariableList = inject('getAllAutoVariableList', () => {
  return [];
});
const rules = {
  calculateFormula: [{ required: true, message: '请输入表达式', trigger: 'blur' }],
};

const onEditFormulaData = (formulaConfig: string) => {
  Dialog.show(
    '编辑表达式',
    EditFormulaData,
    {
      area: ['1000px', '700px'],
    },
    {
      data: formulaConfig,
      flowVariableList: getAllAutoVariableList(),
    },
    {
      width: '1000px',
      height: '700px',
      pathName: '/thirdParty/thirdEditFormulaData',
    },
  )
    .then((res: string) => {
      formData.value.formulaConfig = res;
      onFumulaChange(res);
    })
    .catch(() => {
      console.log('取消');
    });
};

const onFumulaChange = (val: string) => {
  formData.value.formulaConfig = val;
  formData.value.calculateFormula = '';
  let formulaList = [];
  if (val != null && val !== '') formulaList = JSON.parse(val);

  if (Array.isArray(formulaList) && formulaList.length > 0) {
    formData.value.calculateFormula = formulaList.reduce((retObj, item) => {
      let temp = '';
      if (item.itemKind === FormulaItemKind.OPERATOR) {
        // 运算符
        temp = item.itemCode;
      } else if (item.itemKind === FormulaItemKind.NUMBER) {
        // 数字
        temp = item.itemName;
      } else if (item.itemKind === FormulaItemKind.STRING) {
        // 字符串
        temp = "'" + item.itemName + "'";
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
  onChange();
};

const onChange = () => {
  let tempData = {
    ...formData.value,
  };
  emit('update:modelValue', JSON.stringify(tempData));
  emit('change', JSON.stringify(tempData));
};

watch(
  () => props.modelValue,
  val => {
    if (val) {
      let taskInfo = val && val !== '' ? JSON.parse(val) : {};
      formData.value = {
        actionType: AutoTaskActionType.FORMULA,
        calculateFormula: taskInfo.calculateFormula || '',
        formulaConfig: taskInfo.formulaConfig || '',
      };
    }
  },
  { immediate: true },
);
</script>

<style></style>
