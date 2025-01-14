<template>
  <vue-office-pdf
    :src="fileUrl"
    style="height: 100%"
    @error="errorHandler"
    @rendered="renderedHandler"
  />
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue';
import VueOfficePdf from '@vue-office/pdf';

const props = defineProps<{
  fileUrl: string;
}>();
const emit = defineEmits(['error', 'success']);

const ready = ref(false);

const errorHandler = error => {
  emit('error', error);
};
const renderedHandler = () => {
  emit('success');
};

onMounted(() => {
  ready.value = true;
});
</script>

<style></style>
