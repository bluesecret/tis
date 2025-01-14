<template>
  <div class="custom-config">
    <el-table
      :data="tableColumnList"
      :size="defaultFormItemSize"
      header-cell-class-name="table-header-gray"
      height="400px"
    >
      <el-table-column type="index" label="序号" width="60px" />
      <el-table-column label="表格列名" prop="showName" />
      <el-table-column label="是否显示" prop="show">
        <template v-slot="scope">
          <el-switch v-model="scope.row.show" />
        </template>
      </el-table-column>
    </el-table>
    <div class="menu-box">
      <el-button :size="defaultFormItemSize" @click="onCancel">取消</el-button>
      <el-button type="primary" :size="defaultFormItemSize" @click="onSaveClick">保存</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';

defineOptions({
  name: 'CustomConfig',
});

const props = withDefaults(
  defineProps<{
    data: ANY_OBJECT[];
    // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
    dialog?: DialogProp<ANY_OBJECT | ANY_OBJECT[] | undefined>;
  }>(),
  {
    data: () => [],
  },
);

const tableColumnList = ref([]);

const onCancel = () => {
  if (props.dialog?.cancel) {
    props.dialog.cancel(false);
  }
};

const onSaveClick = () => {
  if (props.dialog) {
    props.dialog.submit(tableColumnList.value);
  }
};

onMounted(() => {
  tableColumnList.value = (props.data || []).map((item: ANY_OBJECT) => {
    return {
      ...item,
    };
  });
});
</script>

<style scoped>
.custom-config .menu-box {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
  margin-top: 15px;
}
</style>
