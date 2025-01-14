<template>
  <el-row class="process-data-compensation third-party-dlg" style="position: relative">
    <el-col :span="24" class="form-box">
      <el-radio-group v-model="selectTransId" style="width: 100%" @change="onSelectChange">
        <el-table
          :data="finalDataList"
          :size="itemSize"
          header-cell-class-name="table-header-gray"
          height="600px"
          style="width: 100%"
        >
          <el-table-column type="expand">
            <template v-slot="props">
              <div class="sql-list">
                <div
                  class="sql-item"
                  v-for="(sqlData, index) in props.row.sqlDataList"
                  :key="index"
                >
                  <span>{{ sqlData.sql }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="任务名称" prop="taskName" width="150px">
            <template v-slot="scope">
              <el-radio :value="scope.row.transId">{{ scope.row.taskName }}</el-radio>
            </template>
          </el-table-column>
          <el-table-column label="创建人" prop="createUsername" width="150px" />
          <el-table-column label="创建时间" prop="createTime" width="200px" />
          <el-table-column label="错误原因" prop="errorReason" />
          <template v-slot:empty>
            <div class="table-empty unified-font">
              <img src="@/assets/img/empty.png" />
              <span>暂无数据</span>
            </div>
          </template>
        </el-table>
      </el-radio-group>
    </el-col>
    <el-col :span="24" style="margin-top: 20px" class="menu-box">
      <el-row type="flex" justify="end">
        <el-button :size="itemSize" :plain="true" @click="onCancel"> 取消 </el-button>
        <el-button
          type="primary"
          :size="itemSize"
          :disabled="selectTransId == null"
          @click="onSubmit"
        >
          补偿数据
        </el-button>
      </el-row>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ElMessage } from 'element-plus';
import { findItemFromList } from '@/common/utils';
import { FlowOperationController } from '@/api/flow';
import { ANY_OBJECT } from '@/types/generic';
import { DialogProp } from '@/components/Dialog/types';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  dataList: ANY_OBJECT[];
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<boolean>;
}
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const itemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const selectTransId = ref<string>();
const processInstanceId = ref<string>();

const dialogParams = computed(() => {
  return {
    dataList: props.dataList || thirdParams.value.dataList,
  };
});
const finalDataList = computed(() => {
  return dialogParams.value.dataList.map(item => {
    return {
      ...item,
      sqlDataList: JSON.parse(item.sqlData),
    };
  });
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};
const onSubmit = () => {
  if (selectTransId.value == null) {
    ElMessage.warning('请选择要补偿的任务！');
    return;
  }
  let params = {
    transId: selectTransId.value,
    processInstanceId: processInstanceId.value,
  };

  FlowOperationController.fixBusinessData(params)
    .then(() => {
      ElMessage.success('补偿成功！');
      if (props.dialog) {
        props.dialog.submit(true);
      } else {
        onCloseThirdDialog(true);
      }
    })
    .catch(e => {
      console.warn(e);
    });
};
const onSelectChange = (val: string | number | boolean) => {
  let node = findItemFromList(finalDataList.value, val as string, 'taskId');
  if (node != null) {
    selectTransId.value = node.transId;
    processInstanceId.value = node.processInstanceId;
  }
};
</script>

<style scoped>
.process-data-compensation :deep(.el-table__expanded-cell) {
  padding: 5px 0;
}

.sql-list {
  overflow-y: auto;
  max-height: 400px;
}

.sql-item span {
  display: inline-block;
  padding: 5px;
  font-size: 14px;
  word-break: break-all;
  line-height: 1.5;
}

.el-table :deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #606266;
}

/*
  .sql-item:nth-child(odd) {
    background: #F3F9F2;
  }
  .sql-item:nth-child(even) {
    background: #FDF5E6;
  }
  */
</style>
