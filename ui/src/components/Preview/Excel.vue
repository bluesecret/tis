<template>
  <div style="width: 100%; height: 100%">
    <vue-office-excel
      style="width: 100%; height: 100%"
      :src="fileUrl"
      @error="errorHandler"
      @rendered="renderedHandler"
    />
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';
import VueOfficeExcel from '@vue-office/excel';
import '@vue-office/excel/lib/index.css';

const props = defineProps<{
  fileUrl: string;
}>();
const emit = defineEmits(['error', 'success']);

const errorHandler = error => {
  emit('error', error);
};
const renderedHandler = () => {
  emit('success');
};
</script>

<style></style>
