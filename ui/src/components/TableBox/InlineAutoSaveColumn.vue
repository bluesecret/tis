<template>
  <vxe-column width="0px">
    <template v-slot="scope">
      <input class="auto-edit-next" @focus="onFocused(scope.row)" />
    </template>
  </vxe-column>
</template>

<script setup lang="ts">
import { VxeColumn } from 'vxe-table';

const emit = defineEmits(['save']);
const onFocused = row => {
  emit('save', row);
};
</script>

<style scoped>
.auto-edit-next {
  border: none;
  padding: 0px;
  width: 0px;
}
</style>
