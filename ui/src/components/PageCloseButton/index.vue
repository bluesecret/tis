<template>
  <label class="page-close-box" @click="onCancel()">
    <img src="@/assets/img/back2.png" alt="" />
  </label>
</template>

<script setup lang="ts">
const emit = defineEmits<{ close: [] }>();

const onCancel = () => {
  emit('close');
};
</script>
