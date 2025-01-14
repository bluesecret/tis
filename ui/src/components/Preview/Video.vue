<template>
  <video ref="video" controls :poster="posterUrl">
    <source :src="fileUrl" />
    Your browser does not support the video tag.
  </video>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, onMounted, onUnmounted } from 'vue';

const props = defineProps<{
  fileUrl: string;
  posterUrl: string;
}>();

const emit = defineEmits(['error', 'play', 'pause', 'ended', 'loadedmetadata']);
const video = ref<HTMLVideoElement | null>(null);

const errorHandler = () => {
  emit('error', ref);
};

const playHandler = () => {
  emit('play', ref);
};

const pauseHandler = () => {
  emit('pause', ref);
};

const endedHandler = () => {
  emit('ended', ref);
};

const loadedmetadataHandler = () => {
  emit('loadedmetadata', ref);
};

onMounted(() => {
  video.value?.addEventListener('error', errorHandler);
  video.value?.addEventListener('play', playHandler);
  video.value?.addEventListener('pause', pauseHandler);
  video.value?.addEventListener('ended', endedHandler);
  video.value?.addEventListener('loadedmetadata', loadedmetadataHandler);
});

onUnmounted(() => {
  video.value?.removeEventListener('error', errorHandler);
  video.value?.removeEventListener('play', playHandler);
  video.value?.removeEventListener('pause', pauseHandler);
  video.value?.removeEventListener('ended', endedHandler);
  video.value?.removeEventListener('loadedmetadata', loadedmetadataHandler);
});
</script>

<style></style>
