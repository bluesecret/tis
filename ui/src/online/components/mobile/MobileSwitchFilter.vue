<template>
  <MobileBaseFilter :label="label" :supportExpand="false">
    <template v-slot:right>
      <van-switch size="22px" :active-color="activeColor" :inactive-color="inactiveColor" />
    </template>
  </MobileBaseFilter>
</template>

<script setup lang="ts">
import MobileBaseFilter from './MobileBaseFilter.vue';

withDefaults(
  defineProps<{
    label: string;
    activeColor: string;
    inactiveColor: string;
  }>(),
  {},
);
</script>
