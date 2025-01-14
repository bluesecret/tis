<template>
  <div class="form-single-fragment third-party-dlg" style="position: relative">
    <el-form
      ref="form"
      :model="formData"
      class="full-width-input form-box"
      :rules="rules"
      style="width: 100%"
      label-width="80px"
      :size="itemSize"
      label-position="right"
      @submit.prevent
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="目标任务" prop="taskList">
            <el-select v-model="formData.taskList" multiple clearable placeholder="请选择目标任务">
              <el-option
                v-for="item in taskList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <!-- 备注 -->
        <el-col :span="24">
          <el-form-item label="操作备注" prop="taskComment">
            <el-input v-model="formData.taskComment" placeholder="请输入操作备注" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-row class="no-scroll flex-box menu-box" type="flex" justify="end">
      <el-button :size="itemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="itemSize" @click="onSubmit"> 提交 </el-button>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { FlowOperationController } from '@/api/flow';
import { DialogProp } from '@/components/Dialog/types';
import { ANY_OBJECT } from '@/types/generic';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  processInstanceId?: string;
  processDefinitionId?: string;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<boolean>;
}
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const itemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const form = ref();
const formData = ref<ANY_OBJECT>({
  taskList: [],
  taskComment: undefined,
});
const rules = {
  taskList: [
    {
      required: true,
      message: '请选择复活任务',
      trigger: 'blur',
    },
  ],
  taskComment: [
    {
      required: true,
      message: '请输入备注',
      trigger: 'blur',
    },
  ],
};
const taskList = ref<ANY_OBJECT[]>([]);
const dialogParams = computed(() => {
  return {
    processInstanceId: props.processInstanceId || thirdParams.value.processInstanceId,
    processDefinitionId: props.processDefinitionId || thirdParams.value.processDefinitionId,
  };
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  } else {
    onCloseThirdDialog(false);
  }
};
const onSubmit = () => {
  form.value.validate((valid: boolean) => {
    if (!valid) return;
    FlowOperationController.restart({
      processInstanceId: dialogParams.value.processInstanceId,
      taskKeys: formData.value.taskList,
      taskComment: formData.value.taskComment,
    })
      .then(() => {
        if (props.dialog) {
          props.dialog.submit(true);
        } else {
          onCloseThirdDialog(true);
        }
      })
      .catch(e => {
        console.log(e);
      });
  });
};
const loadAllUserTask = () => {
  FlowOperationController.listAllUserTask({
    processDefinitionId: dialogParams.value.processDefinitionId,
  })
    .then(res => {
      taskList.value = res.data;
    })
    .catch(e => {
      console.log(e);
    });
};

onMounted(() => {
  loadAllUserTask();
});
</script>
