<template>
  <MobileBaseFilter :label="label" :supportExpand="false">
    <div class="number-range">
      <span>{{ startPlaceholder }}</span>
      <div style="width: 12px; height: 1px; margin: 0 10px; background: #999" />
      <span>{{ endPlaceholder }}</span>
    </div>
    <el-row :gutter="8">
      <el-col class="select-item" :span="8" v-for="item in quickSelect" :key="item.id">
        <div class="text">{{ item.name }}</div>
      </el-col>
    </el-row>
  </MobileBaseFilter>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import MobileBaseFilter from './MobileBaseFilter.vue';

withDefaults(
  defineProps<{
    label: string;
    quickSelect: ANY_OBJECT[];
    startPlaceholder?: string;
    endPlaceholder?: string;
  }>(),
  {
    startPlaceholder: '最低价格',
    endPlaceholder: '最高价格',
  },
);
</script>

<style scoped>
.number-range {
  display: flex;
  align-items: center;
  margin-top: 8px;
  font-size: 14px;
  color: #999;
}
.number-range span {
  flex-grow: 1;
  flex-shrink: 1;
  width: 100px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  background: #f6f7f9;
  border-radius: 4px;
}
.select-item {
  margin-top: 8px;
}
.select-item .text {
  overflow: hidden;
  height: 36px;
  padding: 0 10px;
  font-size: 14px;
  text-align: center;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #333;
  background: #f6f7f9;
  border-radius: 4px;
  line-height: 36px;
}
.select-item .text.active {
  color: #f70;
  background: #fdf2e6;
  border: 1px solid #f70;
}
</style>
