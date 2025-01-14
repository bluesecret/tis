<template>
  <MobileBaseFilter :label="label">
    <el-row :gutter="8">
      <el-col class="select-item" :span="8" v-for="(item, index) in dictDataList" :key="item.id">
        <div class="text" :class="{ active: index === 0 }">{{ item.name }}</div>
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
    dictDataList?: ANY_OBJECT[];
  }>(),
  {
    dictDataList: () => [],
  },
);
</script>

<style scoped>
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
