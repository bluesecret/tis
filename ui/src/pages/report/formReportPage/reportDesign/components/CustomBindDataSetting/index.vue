<template>
  <el-form label-position="top" size="default" @submit.prevent>
    <el-form-item label="组件类型">
      <el-input :value="SysCustomWidgetType.getValue(value.widgetType)" :disabled="true" />
    </el-form-item>
    <el-form-item label="组件名称">
      <el-input v-model="showName" placeholder="" clearable filterable />
    </el-form-item>
    <el-form-item label="组件标识">
      <el-input v-model="variableName" placeholder="" clearable filterable />
    </el-form-item>
    <!-- 系统变量 -->
    <el-form-item label="系统变量" v-if="value.widgetType === SysCustomWidgetType.Label">
      <el-select style="width: 100%" v-model="systemVariableType" placeholder="" filterable>
        <el-option
          v-for="item in OnlineSystemVariableType.getList()"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { OnlineSystemVariableType, SysCustomWidgetType } from '@/common/staticDict';
import { ANY_OBJECT } from '@/types/generic';

const emit = defineEmits<{ 'update:value': [ANY_OBJECT] }>();
const props = defineProps<{ value: ANY_OBJECT }>();

const showName = computed({
  get() {
    return props.value.showName;
  },
  set(val) {
    //const value = { ...props.value };
    const value = props.value;
    value.showName = val;
    console.log('..................', val, value);
    emit('update:value', value);
  },
});
const variableName = computed({
  get() {
    return props.value.variableName;
  },
  set(val) {
    //const value = { ...props.value };
    const value = props.value;
    value.variableName = val;
    emit('update:value', value);
  },
});
const systemVariableType = computed({
  get() {
    return props.value.bindData.systemVariableType;
  },
  set(val) {
    //const value = { ...props.value };
    const value = props.value;
    value.bindData.systemVariableType = val;
    emit('update:value', value);
  },
});
</script>
