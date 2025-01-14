<template>
  <el-form
    class="panel-go-back"
    :size="layoutStore.defaultFormItemSize"
    @submit.prevent
    label-position="top"
  >
    <el-form-item label="退回设置">
      <el-radio-group v-model="rejectType" :disabled="isCountersign">
        <el-radio value="0">重新审批</el-radio>
        <el-radio value="1"
          >从当前节点审批
          <el-tooltip
            class="item"
            effect="dark"
            content="若流程为A->B->C,C退回至A，则C->A->C"
            placement="top"
          >
            <i class="el-icon-warning-outline"></i>
          </el-tooltip>
        </el-radio>
      </el-radio-group>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';

import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();
const props = defineProps<{ id: string; isCountersign: boolean }>();
const rejectType = ref('0');

const win: ANY_OBJECT = window;

const resetFormVariable = () => {
  let businessObject = (win.bpmnInstances.bpmnElement || {}).businessObject;
  rejectType.value = props.isCountersign ? '' : businessObject.rejectType || '0';
};
const updateElementExtensions = () => {
  let taskAttr = Object.create(null);
  taskAttr.rejectType = rejectType.value;
  win.bpmnInstances.modeling.updateProperties(win.bpmnInstances.bpmnElement, taskAttr);
};

watch(
  () => props.id,
  val => {
    val && val.length && nextTick(() => resetFormVariable());
  },
  {
    immediate: true,
  },
);
watch(
  () => props.isCountersign,
  () => {
    if (props.isCountersign) {
      rejectType.value = '';
    }
  },
  {
    deep: true,
  },
);
watch(rejectType, () => {
  updateElementExtensions();
});
</script>

<style lang="scss" scoped>
.panel-go-back {
  padding-bottom: 20px;
  margin-top: 8px;
}
.el-radio-group {
  display: inline-block;
  width: 100%;
  padding-top: 4px;
  :deep(.el-radio) {
    margin-bottom: 8px;
    .el-radio__label {
      vertical-align: middle;
    }
  }
}
</style>
