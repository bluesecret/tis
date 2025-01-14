<template>
  <MobileBaseFilter :label="label" :supportExpand="false">
    <van-search style="padding: 0; margin-top: 8px" :placeholder="placeholder" />
  </MobileBaseFilter>
</template>

<script setup lang="ts">
import MobileBaseFilter from './MobileBaseFilter.vue';

withDefaults(
  defineProps<{
    label: string;
    placeholder: string;
  }>(),
  {},
);
</script>
