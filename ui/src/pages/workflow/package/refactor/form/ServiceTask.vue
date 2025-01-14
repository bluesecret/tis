<template>
  <div>
    <el-form
      v-if="tabType === 'baseInfo'"
      ref="form"
      label-position="top"
      :size="layoutStore.defaultFormItemSize"
      :model="formData"
      @submit.prevent
    >
      <template
        v-if="flowEntry().value.flowType === FlowEntryType.NORMAL || props.type !== 'ReceiveTask'"
      >
        <el-form-item label="Java委托类">
          <el-input
            v-model="formData.class"
            clearable
            placeholder="输入包含包名的完整委托类名"
            @change="updateServiceTaskClass"
          />
        </el-form-item>
      </template>
      <template v-else>
        <el-form-item label="任务类型">
          <el-select
            v-model="formData.actionType"
            clearable
            placeholder="请选择任务类型"
            @change="onActionTypeChange"
          >
            <el-option-group label="数据操作">
              <el-option
                :label="AutoTaskActionType.getValue(AutoTaskActionType.INSERT)"
                :value="AutoTaskActionType.INSERT"
              />
              <el-option
                :label="AutoTaskActionType.getValue(AutoTaskActionType.UPDATE)"
                :value="AutoTaskActionType.UPDATE"
              />
              <el-option
                :label="AutoTaskActionType.getValue(AutoTaskActionType.DELETE)"
                :value="AutoTaskActionType.DELETE"
              />
            </el-option-group>
            <el-option-group label="查询计算">
              <el-option
                :label="AutoTaskActionType.getValue(AutoTaskActionType.QUERY_SINGLE_DATA)"
                :value="AutoTaskActionType.QUERY_SINGLE_DATA"
              />
              <el-option
                :label="AutoTaskActionType.getValue(AutoTaskActionType.AGGREGATE)"
                :value="AutoTaskActionType.AGGREGATE"
              />
              <el-option
                :label="AutoTaskActionType.getValue(AutoTaskActionType.FORMULA)"
                :value="AutoTaskActionType.FORMULA"
              />
            </el-option-group>
            <el-option-group label="开发者">
              <el-option
                :label="AutoTaskActionType.getValue(AutoTaskActionType.HTTP)"
                :value="AutoTaskActionType.HTTP"
              />
              <el-option
                :label="AutoTaskActionType.getValue(AutoTaskActionType.LOAD_CACHE)"
                :value="AutoTaskActionType.LOAD_CACHE"
              />
              <el-option
                :label="AutoTaskActionType.getValue(AutoTaskActionType.DELETE_CACHE)"
                :value="AutoTaskActionType.DELETE_CACHE"
              />
              <el-option
                :label="AutoTaskActionType.getValue(AutoTaskActionType.SEND_MQ)"
                :value="AutoTaskActionType.SEND_MQ"
              />
            </el-option-group>
          </el-select>
        </el-form-item>
        <AutoInsertTaskSetting
          v-if="formData.actionType === AutoTaskActionType.INSERT"
          v-model="taskData"
          :flowDblinkList="flowDblinkList"
          @change="updateServiceTaskClass"
        />
        <AutoQuerySingleDataSetting
          v-if="formData.actionType === AutoTaskActionType.QUERY_SINGLE_DATA"
          v-model="taskData"
          :flowDblinkList="flowDblinkList"
          @change="updateServiceTaskClass"
        />
        <AutoHttpTaskSetting
          v-if="formData.actionType === AutoTaskActionType.HTTP"
          v-model="taskData"
          @change="updateServiceTaskClass"
        />
        <AutoUpdateTaskSetting
          v-if="formData.actionType === AutoTaskActionType.UPDATE"
          v-model="taskData"
          :flowDblinkList="flowDblinkList"
          @change="updateServiceTaskClass"
        />
        <AutoDeleteTaskSetting
          v-if="formData.actionType === AutoTaskActionType.DELETE"
          v-model="taskData"
          :flowDblinkList="flowDblinkList"
          @change="updateServiceTaskClass"
        />
        <AutoAggregationTaskSetting
          v-if="formData.actionType === AutoTaskActionType.AGGREGATE"
          v-model="taskData"
          :flowDblinkList="flowDblinkList"
          @change="updateServiceTaskClass"
        />
        <AutoCalcTaskSetting
          v-if="formData.actionType === AutoTaskActionType.FORMULA"
          v-model="taskData"
          @change="updateServiceTaskClass"
        />
        <AutoLoadCache
          v-if="formData.actionType === AutoTaskActionType.LOAD_CACHE"
          v-model="taskData"
          :flowDblinkList="flowDblinkList"
          @change="updateServiceTaskClass"
        />
        <AutoDeleteCache
          v-if="formData.actionType === AutoTaskActionType.DELETE_CACHE"
          v-model="taskData"
          :flowDblinkList="flowDblinkList"
          @change="updateServiceTaskClass"
        />
        <AutoMqTaskSetting
          v-if="formData.actionType === AutoTaskActionType.SEND_MQ"
          v-model="taskData"
          :flowDblinkList="flowDblinkList"
          @change="updateServiceTaskClass"
        />
      </template>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { defineProps, ref, inject, watch, nextTick } from 'vue';
import { ANY_OBJECT } from '@/types/generic';
import { useLayoutStore } from '@/store';
import { FlowEntryType, AutoTaskActionType } from '@/common/staticDict/flow';
import { FlowDblinkController } from '@/api/flow';
import AutoInsertTaskSetting from './AutoInsertTaskSetting.vue';
import AutoQuerySingleDataSetting from './AutoQuerySingleDataSetting.vue';
import AutoHttpTaskSetting from './AutoHttpTaskSetting.vue';
import AutoUpdateTaskSetting from './AutoUpdateTaskSetting.vue';
import AutoDeleteTaskSetting from './AutoDeleteTaskSetting.vue';
import AutoAggregationTaskSetting from './AutoAggregationTaskSetting.vue';
import AutoCalcTaskSetting from './AutoCalcTaskSetting.vue';
import AutoLoadCache from './AutoLoadCache.vue';
import AutoDeleteCache from './AutoDeleteCache.vue';
import AutoMqTaskSetting from './AutoMqTaskSetting.vue';

const layoutStore = useLayoutStore();
const props = defineProps<{
  id: string;
  type: string;
  tabType: string;
}>();
const flowEntry = inject('flowEntry', () => {
  return {} as ANY_OBJECT;
});
const formList = inject('formList', () => {
  return {} as ANY_OBJECT;
});
const prefix = inject('prefix');
const flowVariableList = inject('flowVariableList', () => {
  return [];
});
const autoTaskVariableList = inject('autoTaskVariableList', () => {
  return [];
});
const win: ANY_OBJECT = window;

const getAllAutoVariableList = () => {
  let tempFlowVariableList = flowVariableList();
  let tempAutoTaskVariableList = autoTaskVariableList();
  return [...tempFlowVariableList, ...tempAutoTaskVariableList];
};

provide('getAllAutoVariableList', getAllAutoVariableList);

type FormDataType = {
  class: string;
  actionType: number;
};
const formData = ref<FormDataType>({
  class: '',
  actionType: AutoTaskActionType.INSERT,
});

const taskInfoElement = ref();
const taskData = ref();
const flowDblinkList = ref<ANY_OBJECT[]>([]);

const onActionTypeChange = () => {
  taskData.value = undefined;
  updateServiceTaskClass();
};

const loadFlowDblinkList = () => {
  FlowDblinkController.list({})
    .then(res => {
      flowDblinkList.value = (res.data.dataList || []).map(item => {
        return {
          id: item.dblinkId,
          name: item.dblinkName,
          type: item.dblinkType,
        };
      });
    })
    .catch(e => {
      console.error(e);
    });
};

const resetTaskInfo = () => {
  let bpmnELement = win.bpmnInstances.bpmnElement;
  let elExtensionElements =
    bpmnELement.businessObject.get('extensionElements') ||
    win.bpmnInstances.moddle.create('bpmn:ExtensionElements', { values: [] });
  taskInfoElement.value = elExtensionElements.values.filter(
    ex => ex.$type === `${prefix}:TaskInfo`,
  )[0];
  if (taskInfoElement.value == null) {
    taskInfoElement.value = win.bpmnInstances.moddle.create(`${prefix}:TaskInfo`, {
      data: '{}',
    });
  }
  taskData.value = taskInfoElement.value.data;
  let actionType = AutoTaskActionType.INSERT;
  if (taskData.value) {
    let tempTaskData = taskData.value ? JSON.parse(taskData.value) : {};
    console.log('tempTaskData', tempTaskData);
    actionType = tempTaskData.actionType || AutoTaskActionType.INSERT;
  }
  formData.value = {
    actionType,
    class: bpmnELement.businessObject.class,
  };
};
const updateServiceTaskClass = () => {
  let bpmnELement = win.bpmnInstances.bpmnElement;
  if (flowEntry().value.flowType === FlowEntryType.NORMAL) {
    win.bpmnInstances.modeling.updateProperties(bpmnELement, {
      class: formData.value.class,
    });
    win.bpmnInstances.modeling.updateProperties(bpmnELement, {
      extensionElements: null,
    });
    return;
  }
  let elExtensionElements =
    bpmnELement.businessObject.get('extensionElements') ||
    win.bpmnInstances.moddle.create('bpmn:ExtensionElements', { values: [] });
  let otherExtensions = elExtensionElements.values.filter(ex => ex.$type !== `${prefix}:TaskInfo`);
  taskInfoElement.value.data =
    taskData.value == null
      ? JSON.stringify({ actionType: formData.value.actionType })
      : taskData.value;
  const newElExtensionElements = win.bpmnInstances.moddle.create(`bpmn:ExtensionElements`, {
    // values: otherExtensions.concat(this.taskInfoElement)
    values: [taskInfoElement.value],
  });
  // 更新到元素上
  win.bpmnInstances.modeling.updateProperties(bpmnELement, {
    extensionElements: newElExtensionElements,
  });
  win.bpmnInstances.modeling.updateProperties(bpmnELement, {
    class: undefined,
  });
};

watch(
  () => props.id,
  val => {
    val && val.length && nextTick(() => resetTaskInfo());
  },
  {
    immediate: true,
  },
);

onMounted(() => {
  loadFlowDblinkList();
});
</script>

<style></style>
