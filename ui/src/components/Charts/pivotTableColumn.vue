<template>
  <vxe-column v-if="getComponent == VxeColumn" v-bind="getComponentProps">
    <template v-slot="scope">
      <span class="vxe-cell--label">{{ scope.row[getComponentProps.field] }} </span>
    </template>
  </vxe-column>
  <vxe-colgroup v-else v-bind="getComponentProps">
    <pivot-table-column
      v-for="item in (columnInfo || {}).children"
      :key="item.id"
      :columnInfo="item"
    />
  </vxe-colgroup>
</template>

<script setup lang="ts">
import { VxeColumn, VxeColgroup } from 'vxe-table';
import { ANY_OBJECT } from '@/types/generic';
import PivotTableColumn from './pivotTableColumn.vue';

const props = defineProps<{ columnInfo: ANY_OBJECT }>();

console.log('pivotTableColumn props', props);

const getComponent = computed(() => {
  return props.columnInfo && props.columnInfo.isGroup ? VxeColgroup : VxeColumn;
});
const getComponentProps = computed(() => {
  let temp = {
    ...props.columnInfo,
  };
  delete temp.children;

  console.log('pivotTable column props', temp);
  return temp;
});
</script>
