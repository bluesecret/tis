<template>
  <div class="autotask-mq-settting">
    <el-form-item label="消息主题">
      <template #label>
        <span>消息主题 </span>
        <el-tooltip class="item" effect="dark" content="该选项为必填项，同时要确保消息队列中存在该 Topic。" placement="right-start">
          <el-icon><Warning /></el-icon>
        </el-tooltip>
      </template>
      <el-input v-model="formData.messageTopic" @change="onChange" placeholder="必填项，同时要确保消息队列中存在该 Topic。"/>
    </el-form-item>
    <el-form-item label="消息类型">
      <el-input v-model="formData.messageType" @change="onChange" placeholder="该选项不是必填项"/>
    </el-form-item>
    <el-form-item label="消息命令">
      <el-input v-model="formData.messageCommand" @change="onChange" placeholder="该选项不是必填项"/>
    </el-form-item>
    <el-form-item label="消息数据">
      <template #label>
        <span>消息数据 </span>
        <el-tooltip class="item" effect="dark" content="必填项，可直接输入数据值，也可使用变量，并用 ${} 包含" placement="right-start">
          <el-icon><Warning /></el-icon>
        </el-tooltip>
      </template>
      <el-input
        v-model="formData.messageData"
        type="textarea"
        rows="5"
        clearable
        placeholder="必填项，可直接输入数据值，也可使用变量，并用 ${} 包含"
        @change="onChange"
      />
    </el-form-item>
    <el-form-item label="是否等待消费应答">
      <template #label>
        <span>是否等待消费应答 </span>
        <el-tooltip class="item" effect="dark" content="如不等待，当前消息发送后将自动流转，否则等待消费者应答后继续流转" placement="right-start">
          <el-icon><Warning /></el-icon>
        </el-tooltip>
      </template>
      <el-switch v-model="waitResponse" @change="onChange"/>
    </el-form-item>
    <el-form-item label="响应数据" v-if="waitResponse">
      <el-table
        :show-header="false"
        :data="responseDataTree"
        size="default"
        row-key="id"
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="fieldName" label="字段名" />
        <el-table-column prop="fieldType" label="字段类型" width="100px" />
        <el-table-column label="操作" width="120px">
          <template v-slot="scope">
            <el-button type="primary" link size="small" @click="onEditResponseData(scope.row)">
              编辑
            </el-button>
            <el-button type="primary" link size="small" @click="onDeleteResponseData(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-button
        type="primary"
        plain
        size="small"
        icon="el-icon-plus"
        style="width: 100%"
        @click="onEditResponseData()"
      >
        添加响应字段
      </el-button>
    </el-form-item>
  </div>
</template>

<script setup lang="ts">
import { Warning } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { defineProps, defineEmits } from 'vue';
import EditResponseData from './editResponseData.vue';
import { treeDataTranslate } from '@/common/utils/index';
import { Dialog } from '@/components/Dialog';
import { AutoTaskActionType } from '@/common/staticDict/flow';
import { ANY_OBJECT } from '@/types/generic';

const emit = defineEmits(['update:modelValue', 'change']);
const props = defineProps<{
  modelValue: string;
}>();
const flowEntry = inject('flowEntry', () => {
  return {} as ANY_OBJECT;
});

type FormDataType = {
  actionType?: number;
  messageTopic?: string;
  messageType?: string;
  messageCommand?: string;
  messageData?: string;
  autoTrigger: boolean;
  messageResponnseBody?: ANY_OBJECT[];
};
const formData = ref<FormDataType>({
  actionType: AutoTaskActionType.SEND_MQ,
  autoTrigger: true,
});


const responseDataTree = computed(() => {
  const dataList = formData.value.messageResponnseBody?.map(item => {
    return {
      ...item,
      disabled: item.fieldType !== 'Object' && item.fieldType !== 'Array',
    };
  }) || [];
  return treeDataTranslate(dataList, 'id', 'parentId');
});

const waitResponse = computed({
  get() {
    return !formData.value.autoTrigger;
  },
  set(val) {
    formData.value.autoTrigger = !val;
  }
});

const onChange = () => {
  let tempData = {
    ...formData.value,
  };
  emit('update:modelValue', JSON.stringify(tempData));
  emit('change', JSON.stringify(tempData));
};

const updateResponseData = (row, res) => {
  function buildChildId(node, parentId) {
    if (parentId == null || parentId === '') {
      node.id = parentId + '.' + node.fieldName;
    } else {
      node.id = node.fieldName;
    }
    if (Array.isArray(node.children)) {
      node.children.forEach(child => {
        buildChildId(child, node.id);
      });
    }
  }
  if (formData.value.messageResponnseBody == null) {
    formData.value.messageResponnseBody = [];
  }
  if (row == null) {
    // 新建
    formData.value.messageResponnseBody.push(res);
  } else {
    // 编辑
    formData.value.messageResponnseBody =
      formData.value.messageResponnseBody.map(item => {
        if (item.id === res.oldIId) {
          buildChildId(res, res.parentId);
          return res;
        } else {
          return item;
        }
      });
  }
  onChange();
};

const onEditResponseData = (row?: ANY_OBJECT) => {
  Dialog.show(
    '编辑响应字段',
    EditResponseData,
    {
      area: ['500px', '400px'],
    },
    {
      entryId: flowEntry().entryId,
      data: row,
      usedFieldList: responseDataTree.value,
      path: 'thirdEditResponseData',
    },
    {
      width: '500px',
      height: '400px',
      pathName: '/thirdParty/thirdEditResponseData',
    },
  )
    .then(res => {
      updateResponseData(row, res);
    })
    .catch(e => {
      console.log(e);
    });
};

const onDeleteResponseData = row => {
  ElMessageBox.confirm('是否删除此响应字段？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(res => {
      formData.value.messageResponnseBody = formData.value.messageResponnseBody?.filter(item => item.id !== row.id);
      onChange();
    })
    .catch(e => {
      console.log(e);
    });
};

watch(
  () => props.modelValue,
  val => {
    let taskInfo = val && val !== '' ? JSON.parse(val) : {};
    formData.value = {
      actionType: AutoTaskActionType.SEND_MQ,
      messageTopic: taskInfo.messageTopic,
      messageType: taskInfo.messageType,
      messageCommand: taskInfo.messageCommand,
      messageData: taskInfo.messageData,
      autoTrigger: taskInfo.autoTrigger !== undefined ? taskInfo.autoTrigger : true,
      messageResponnseBody: taskInfo.messageResponnseBody || []
    };
  },
  { immediate: true },
);
</script>

<style scoped></style>
