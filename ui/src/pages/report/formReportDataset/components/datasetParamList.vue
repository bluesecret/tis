<template>
  <vxe-table
    :data="datasetParamList"
    size="default"
    header-cell-class-name="table-header-gray"
    :height="height"
    :row-config="{ isHover: true }"
    style="width: 100%"
  >
    <vxe-column v-if="!supportDelete" type="seq" width="55px" title="序号" />
    <vxe-column title="参数名称" field="paramName" max-width="200px">
      <template v-slot="scope">
        <el-input
          v-if="scope.row.isEdit"
          v-model="scope.row.paramName"
          size="default"
          style="width: 100%"
        />
        <span v-else>{{ scope.row.paramName }}</span>
      </template>
    </vxe-column>
    <vxe-column title="参数类型" field="paramType" width="150px">
      <template v-slot="scope">
        <el-select v-model="scope.row.paramType" placeholder="" size="default">
          <el-option title="字符串" value="String" />
          <el-option title="数字" value="Number" />
          <el-option title="日期" value="Date" />
        </el-select>
      </template>
    </vxe-column>
    <vxe-column title="默认值" field="defaultValue">
      <template v-slot="scope">
        <el-input
          v-if="scope.row.paramType === 'String'"
          v-model="scope.row.defaultValue"
          size="default"
          style="width: 100%"
        />
        <el-input-number
          v-if="scope.row.paramType === 'Number'"
          v-model="scope.row.defaultValue"
          size="default"
          style="width: 100%"
        />
        <el-date-picker
          v-if="scope.row.paramType === 'Date'"
          type="datetime"
          v-model="scope.row.defaultValue"
          size="default"
          style="width: 100%"
        />
      </template>
    </vxe-column>
    <vxe-column v-if="supportDelete" width="55px" title="操作">
      <template v-slot="scope">
        <el-button link type="danger" size="default" @click="onDeleteParams(scope.row)">
          删除
        </el-button>
      </template>
    </vxe-column>
    <template v-slot:empty>
      <div class="table-empty unified-font">
        <img src="@/assets/img/empty.png" />
        <span>暂无数据</span>
      </div>
    </template>
  </vxe-table>
</template>

<script setup lang="ts">
import { VxeTable, VxeColumn } from 'vxe-table';
import { ANY_OBJECT } from '@/types/generic';

withDefaults(
  defineProps<{ height?: string; datasetParamList: ANY_OBJECT[]; supportDelete?: boolean }>(),
  {
    height: '450px',
    supportDelete: false,
  },
);

const emit = defineEmits<{ delete: [ANY_OBJECT] }>();
const onDeleteParams = (row: ANY_OBJECT) => {
  emit('delete', row);
};
</script>
