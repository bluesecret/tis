<template>
  <div class="form-single-fragment third-party-dlg" style="position: relative">
    <el-form
      ref="form"
      :model="formData"
      class="full-width-input form-box"
      style="width: 100%"
      label-width="100px"
      :size="formItemSize"
      label-position="right"
      @submit.prevent
      :rules="rules"
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="跳转节点" prop="targetTaskKey">
            <UserTaskSelect
              v-model:value="formData.targetTaskKey"
              :xml="taskProcessXml"
              placeholder="请选择跳转任务节点"
              :disabled="taskProcessXml == null"
              :finishedInfo="finishedInfo"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="处理用户" prop="delegateAssignee">
            <TagSelect v-model:value="userName">
              <template #append>
                <el-button
                  class="append-add"
                  type="default"
                  :icon="Plus"
                  @click="onSelectAssignee"
                />
              </template>
            </TagSelect>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="干预原因" prop="taskComment">
            <el-input v-model="formData.taskComment" clearable placeholder="请输入干预原因" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-row class="no-scroll flex-box menu-box" type="flex" justify="end">
      <el-button :size="formItemSize" :plain="true" @click="onCancel"> 取消 </el-button>
      <el-button type="primary" :size="formItemSize" @click="onSubmit"> 提交 </el-button>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue';
import TagSelect from '@/pages/workflow/components/TagSelect.vue';
import TaskUserSelect from '@/pages/workflow/components/TaskUserSelect.vue';
import UserTaskSelect from '@/pages/workflow/components/UserTaskSelect/index.vue';
import { FlowOperationController } from '@/api/flow';
import { Dialog } from '@/components/Dialog';
import { DialogProp } from '@/components/Dialog/types';
import { ANY_OBJECT } from '@/types/generic';
import { useThirdParty } from '@/components/thirdParty/hooks';
import { ThirdProps } from '@/components/thirdParty/types';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

interface IProps extends ThirdProps {
  processInstanceId?: string;
  processDefinitionId?: string;
  taskId?: string;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}
const props = defineProps<IProps>();
const { thirdParams, onCloseThirdDialog } = useThirdParty(props);
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize || thirdParams.value.defaultFormItemSize?.value;
});

const form = ref();
const formData = ref<ANY_OBJECT>({
  taskComment: undefined,
  delegateAssignee: undefined,
  targetTaskKey: undefined,
});
const rules = {
  taskComment: [{ required: true, message: '请输入干预原因', trigger: 'blur' }],
};
const taskProcessXml = ref();
const userName = ref<ANY_OBJECT[]>([]);
const finishedInfo = ref();

const dialogParams = computed(() => {
  return {
    taskId: props.taskId || thirdParams.value.taskId,
    processDefinitionId: props.processDefinitionId || thirdParams.value.processDefinitionId,
    processInstanceId: props.processInstanceId || thirdParams.value.processInstanceId,
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
    if (valid) {
      let params = {
        processInstanceId: dialogParams.value.processInstanceId,
        taskId: dialogParams.value.taskId,
        delegateAssignee: formData.value.delegateAssignee,
        targetTaskKey: formData.value.targetTaskKey,
        taskComment: formData.value.taskComment,
      };

      FlowOperationController.interveneRuntimeTask(params)
        .then(res => {
          if (props.dialog) {
            props.dialog.submit(res);
          } else {
            onCloseThirdDialog(true);
          }
        })
        .catch(e => {
          console.warn(e);
        });
    }
  });
};
const updateSelectAssignee = (res: ANY_OBJECT[]) => {
  if (Array.isArray(res)) {
    let oldUserList = userName.value.map(item => item.id);
    const list: ANY_OBJECT[] = [];
    res.forEach(item => {
      if (oldUserList.indexOf(item.loginName) === -1) {
        list.push({
          id: item.loginName,
          name: item.showName || item.loginName,
        });
      }
    });
    userName.value = userName.value ? userName.value.concat(list) : list;
  }
};
const onSelectAssignee = () => {
  let usedUserIdList =
    formData.value.delegateAssignee == null || formData.value.delegateAssignee === ''
      ? []
      : formData.value.delegateAssignee.split(',');
  Dialog.show<ANY_OBJECT[]>(
    '选择用户',
    TaskUserSelect,
    {
      area: ['1000px', '600px'],
    },
    {
      showAssignee: false,
      showStartUser: false,
      multiple: true,
      usedUserIdList: usedUserIdList,
      path: 'thirdTaskUserSelect',
    },
    {
      width: '1000px',
      height: '640px',
      pathName: '/thirdParty/thirdTaskUserSelect',
    },
  )
    .then(res => {
      updateSelectAssignee(res);
    })
    .catch(e => {
      console.warn(e);
    });
};
const getTaskProcessXml = () => {
  let params = {
    processDefinitionId: dialogParams.value.processDefinitionId,
  };
  FlowOperationController.viewProcessBpmn(params)
    .then(res => {
      // 当前流程实例xml
      taskProcessXml.value = res.data;
    })
    .catch(e => {
      console.warn(e);
    });
};
const getTaskHighlightData = () => {
  if (dialogParams.value.processInstanceId == null || dialogParams.value.processInstanceId === '') {
    return;
  }
  let params = {
    processInstanceId: dialogParams.value.processInstanceId,
  };

  FlowOperationController.viewHighlightFlowData(params)
    .then(res => {
      // 已完成节点
      finishedInfo.value = res.data;
    })
    .catch(e => {
      console.warn(e);
    });
};

watch(
  userName,
  newValue => {
    if (newValue) {
      let usedUserIdList = newValue.map(item => item.id);
      formData.value.delegateAssignee = usedUserIdList.join(',');
    }
  },
  {
    deep: true,
  },
);

onMounted(() => {
  getTaskProcessXml();
  getTaskHighlightData();
});
</script>

<style scoped>
.append-add {
  background: #f5f7fa;
  border: none;
  border-radius: 0;
  border-left: 1px solid #dcdfe6;
}
</style>
