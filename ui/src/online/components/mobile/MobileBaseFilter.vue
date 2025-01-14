<template>
  <div class="mobile-base-filter" style="margin-top: 16px">
    <el-row type="flex" justify="space-between" align="middle">
      <span class="label">{{ label }}</span>
      <div class="right">
        <span class="select" v-if="select != null && select !== ''">{{ select }}</span>
        <slot name="right" />
        <span v-if="supportExpand" class="expand-text">{{ expandText }}</span>
        <el-icon class="expand-icon" v-if="supportExpand"><ArrowDown /></el-icon>
      </div>
    </el-row>
    <slot />
  </div>
</template>

<script setup lang="ts">
import { ArrowDown } from '@element-plus/icons-vue';

withDefaults(
  defineProps<{
    label: string;
    expandText?: string;
    supportExpand?: boolean;
    select?: string;
  }>(),
  {
    expandText: '展开',
    supportExpand: true,
  },
);
</script>

<style lang="scss" scoped>
.mobile-base-filter .label {
  flex-shrink: 1;
  overflow: hidden;
  width: 200px;
  font-size: 14px;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #333;
  flex-grow: 1;
}

.mobile-base-filter .right {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-left: 10px;
  font-size: 12px;
  color: #999;
}

.mobile-base-filter .right .select {
  flex-shrink: 1;
  overflow: hidden;
  width: 200px;
  margin-right: 8px;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: $color-primary;
  flex-grow: 1;
}

.mobile-base-filter .right .expand-text {
  margin-right: 8px;
}

.mobile-base-filter .right .expand-icon {
  font-size: 14px;
}
</style>
