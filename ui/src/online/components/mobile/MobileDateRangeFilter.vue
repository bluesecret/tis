<template>
  <MobileBaseFilter :label="label" :supportExpand="false">
    <div class="date-range">
      <span>开始日期</span>
      <div style="width: 12px; height: 1px; background: #999" />
      <span>结束日期</span>
      <i class="icon el-icon-date" />
    </div>
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

<style scoped>
.date-range {
  display: flex;
  align-items: center;
  height: 36px;
  margin-top: 8px;
  font-size: 14px;
  color: #999;
  background: #f6f7f9;
  border-radius: 4px;
}

.date-range span {
  flex-grow: 1;
  flex-shrink: 1;
  width: 100px;
  text-align: center;
}

.date-range .icon {
  margin-right: 10px;
}
</style>
