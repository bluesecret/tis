<template>
  <div class="panel-tab__content">
    <el-select
      class="input-item"
      v-model="latestApprovalStatus"
      @change="onLatestApprovalStatusChange"
    >
      <el-option label="不更新" value="none_undefined" />
      <el-option
        v-for="item in validStatusList"
        :key="item.id"
        :label="item.name"
        :value="item.id"
      />
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';

const props = defineProps<{ id: string; type?: string }>();
const flowEntry = inject('flowEntry', () => {
  return {} as ANY_OBJECT;
});
const prefix = inject('prefix');

const latestApprovalStatus = ref<number | string>();
const elementPropertyList = ref<ANY_OBJECT[]>([]);

const validStatusList = computed(() => {
  if (
    flowEntry().value.extensionData != null &&
    Array.isArray(flowEntry().value.extensionData.approvalStatusDict)
  ) {
    return flowEntry().value.extensionData.approvalStatusDict.map(item => {
      return {
        id: item.id.toString(),
        name: item.name,
      };
    });
  } else {
    return [];
  }
});

let bpmnElement: ANY_OBJECT = {};
let bpmnElementPropertyList: ANY_OBJECT[] = [];
const win: ANY_OBJECT = window;

const resetAttributesList = () => {
  latestApprovalStatus.value = 'none_undefined';
  bpmnElement = win.bpmnInstances.bpmnElement;
  const bpmnElementProperties =
    bpmnElement.businessObject?.extensionElements?.values?.filter((ex: ANY_OBJECT) => {
      return ex.$type === `${prefix}:Properties`;
    }) ?? [];

  // 保存所有的 扩展属性字段
  bpmnElementPropertyList = bpmnElementProperties.reduce(
    (pre: ANY_OBJECT[], current: ANY_OBJECT) => pre.concat(current.values),
    [],
  );

  // 复制 显示
  elementPropertyList.value = JSON.parse(JSON.stringify(bpmnElementPropertyList ?? []));
  elementPropertyList.value.forEach(item => {
    if (item.name === 'latestApprovalStatus') {
      latestApprovalStatus.value = item.value === 'none_undefined' ? undefined : item.value;
    }
  });
};
const updateElementExtensions = (properties: ANY_OBJECT[]) => {
  const otherExtensionList =
    bpmnElement.businessObject?.extensionElements?.values?.filter((ex: ANY_OBJECT) => {
      return ex.$type !== `${prefix}:Properties`;
    }) ?? [];
  const extensions = win.bpmnInstances.moddle.create('bpmn:ExtensionElements', {
    values: otherExtensionList.concat(properties),
  });
  win.bpmnInstances.modeling.updateProperties(bpmnElement, {
    extensionElements: extensions,
  });
};
const onLatestApprovalStatusChange = (val: string) => {
  if (val == null || val === '' || val === 'none_undefined') {
    updateElementExtensions([]);
  } else {
    const newPropertyObject = win.bpmnInstances.moddle.create(`${prefix}:Property`, {
      name: 'latestApprovalStatus',
      value: val,
    });
    const propertiesObject = win.bpmnInstances.moddle.create(`${prefix}:Properties`, {
      values: [newPropertyObject],
    });
    updateElementExtensions([propertiesObject]);
    resetAttributesList();
  }
};

watch(
  () => props.id,
  val => {
    val && val.length && resetAttributesList();
  },
  {
    immediate: true,
  },
);
</script>
