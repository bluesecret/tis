<template>
  <div class="add-node-btn-box" :key="id">
    <div class="add-node-btn">
      <el-popover
        placement="bottom"
        v-model:visible="visible"
        v-if="!readonly"
        trigger="click"
        width="auto"
      >
        <div class="add-node-popover-body">
          <a
            v-if="flowEntry().value.flowType === FlowEntryType.NORMAL"
            class="add-node-popover-item approver"
            @click="addType(FlowNodeType.APPROVED_BY)"
          >
            <div class="item-wrapper">
              <img class="iconfont" src="@/assets/img/spjd.png" alt="" />
              <img class="iconfont" src="@/assets/img/spjd2.png" alt="" />
            </div>
            <p>审批人</p>
          </a>
          <a
            v-if="flowEntry().value.flowType === FlowEntryType.NORMAL"
            class="add-node-popover-item approver"
            @click="addType(FlowNodeType.SERVICE_TASK)"
          >
            <div class="item-wrapper">
              <el-icon class="iconfont"><Setting /></el-icon>
              <el-icon class="iconfont"><Setting /></el-icon>
            </div>
            <p>服务任务</p>
          </a>
          <a class="add-node-popover-item approver" @click="addType(FlowNodeType.RECEIVE_TASK)">
            <div class="item-wrapper">
              <el-icon class="iconfont"><Message /></el-icon>
              <el-icon class="iconfont"><Message /></el-icon>
            </div>
            <p>
              {{ flowEntry().value.flowType === FlowEntryType.NORMAL ? '接收任务' : '自动化任务' }}
            </p>
          </a>
          <a
            class="add-node-popover-item condition"
            @click="addType(FlowNodeType.CONDITIONAL_BRANCH)"
          >
            <div class="item-wrapper">
              <img class="iconfont" src="@/assets/img/tj.png" alt="" />
              <img class="iconfont" src="@/assets/img/tj2.png" alt="" />
            </div>
            <p>条件分支</p>
          </a>
          <a class="add-node-popover-item condition" @click="addType(FlowNodeType.PARALLEL_BRANCH)">
            <div class="item-wrapper">
              <img class="iconfont" src="@/assets/img/wg.png" alt="" />
              <img class="iconfont" src="@/assets/img/wg2.png" alt="" />
            </div>
            <p>并行分支</p>
          </a>
        </div>
        <template #reference>
          <div class="btn" type="button" v-if="!isGateway">
            <el-icon><Plus /></el-icon>
          </div>
        </template>
      </el-popover>
    </div>
  </div>
</template>

<script setup lang="ts">
import { inject, ref } from 'vue';
import { Plus, Setting, Message } from '@element-plus/icons-vue';
import { ANY_OBJECT } from '@/types/generic';
import { FlowNodeType, FlowEntryType } from '@/common/staticDict/flow';

const emit = defineEmits<{ 'update:childNodeP': [ANY_OBJECT] }>();

const props = defineProps<{
  childNodeP?: ANY_OBJECT;
  nodeConfig: ANY_OBJECT;
  priorityLevel?: string | number;
  isEnd?: boolean;
  isGateway?: boolean;
}>();

const visible = ref(false);
const id = ref(Date.now());
const bpmnModeler = inject('bpmnModeler', {} as ANY_OBJECT);
const readonly = inject('readonly', false);
const flowEntry = inject('flowEntry', () => {
  return {} as ANY_OBJECT;
});

const addType = (type: number) => {
  //console.log('addType bpmnModeler', bpmnModeler);
  visible.value = false;
  const StartEvent = bpmnModeler
    .get('elementRegistry')
    .find((el: ANY_OBJECT) => el.type === 'bpmn:StartEvent');
  //console.log('addType StartEvent', StartEvent);
  // 添加审批人
  if (type === FlowNodeType.APPROVED_BY) {
    // 创建用户任务节点
    const UserTask = bpmnModeler.get('elementFactory').createShape({ type: 'bpmn:UserTask' });
    // 插入新建的用户任务节点
    bpmnModeler.get('autoPlace').append(StartEvent, UserTask);
    let data = {
      nodeName: '审核人',
      error: true,
      type: FlowNodeType.APPROVED_BY,
      element: markRaw(UserTask),
      childNode: props.childNodeP,
      nodeUserList: [],
    };
    console.log('addNode add 1', data, props.childNodeP);
    emit('update:childNodeP', data);
  } else if (type === FlowNodeType.SERVICE_TASK) {
    // 新建服务任务节点
    const ServiceTask = bpmnModeler.get('elementFactory').createShape({ type: 'bpmn:ServiceTask' });
    // 插入新建的服务任务
    bpmnModeler.get('autoPlace').append(StartEvent, ServiceTask);
    let data = {
      nodeName: '服务任务',
      error: true,
      type: FlowNodeType.SERVICE_TASK,
      element: markRaw(ServiceTask),
      childNode: props.childNodeP,
      nodeUserList: [],
    };
    emit('update:childNodeP', data);
  } else if (type === FlowNodeType.RECEIVE_TASK) {
    // 新建接收任务节点
    const ReceiveTask = bpmnModeler.get('elementFactory').createShape({ type: 'bpmn:ReceiveTask' });
    // 插入新建的接收任务
    bpmnModeler.get('autoPlace').append(StartEvent, ReceiveTask);
    let data = {
      nodeName: '接收任务',
      error: true,
      type: FlowNodeType.RECEIVE_TASK,
      element: markRaw(ReceiveTask),
      childNode: props.childNodeP,
      nodeUserList: [],
    };
    emit('update:childNodeP', data);
  } else {
    // 添加分支
    let data: ANY_OBJECT = {
      nodeName: type === FlowNodeType.CONDITIONAL_BRANCH ? '网关' : '并行网关',
      type: type,
      childNode: props.childNodeP ? props.childNodeP : null,
      element: null,
      conditionNodes: [
        {
          nodeName: '',
          type: FlowNodeType.CONNECTING_LINE,
          element: null,
          priorityLevel: 0,
          conditionStr:
            type === FlowNodeType.CONDITIONAL_BRANCH ? '普通流转路径' : '并行任务（同时进行）',
          nodeUserList: [],
          childNode: null,
        },
        {
          nodeName: '',
          type: FlowNodeType.CONNECTING_LINE,
          element: null,
          priorityLevel: 1,
          conditionStr:
            type === FlowNodeType.CONDITIONAL_BRANCH ? '普通流转路径' : '并行任务（同时进行）',
          nodeUserList: [],
          childNode: null,
        },
      ],
    };
    const element = bpmnModeler.get('elementFactory').createShape({
      type:
        type === FlowNodeType.CONDITIONAL_BRANCH ? 'bpmn:ExclusiveGateway' : 'bpmn:ParallelGateway',
    });
    // 创建分支节点
    data.element = markRaw(element);
    const endElement = bpmnModeler.get('elementFactory').createShape({
      type:
        type === FlowNodeType.CONDITIONAL_BRANCH ? 'bpmn:ExclusiveGateway' : 'bpmn:ParallelGateway',
    });
    data.endElement = markRaw(endElement);

    // 插入新建的用户任务节点
    bpmnModeler.get('autoPlace').append(StartEvent, element);
    bpmnModeler.get('autoPlace').append(element, endElement);
    bpmnModeler.get('modeling').removeElements(element.outgoing);

    data.conditionNodes.forEach((row: ANY_OBJECT) => {
      row.element = markRaw(bpmnModeler.get('modeling').connect(element, endElement));
      updateConnectData(row);
    });
    console.log('addNode add 2', data);
    emit('update:childNodeP', data);
  }
};
// 更新连接线数据
const updateConnectData = (row: ANY_OBJECT) => {
  console.log('addNode updateConnectData', row);
  if (row.conditionStr === '默认流转路径') {
    bpmnModeler.get('modeling').updateProperties(row.element.source, {
      default: row.element,
    });
  }

  if (row.extensionElements) {
    let hasPriorityLevel = false;
    let Properties: ANY_OBJECT | null = null;
    row.extensionElements.values.forEach((extension: ANY_OBJECT) => {
      if (extension.$type === 'flowable:Properties' || extension.$type === 'flowable:properties') {
        Properties = extension;
        extension.values.forEach((item: ANY_OBJECT) => {
          if (item.name === 'priorityLevel') {
            hasPriorityLevel = true;
            bpmnModeler.get('modeling').updateModdleProperties(extension, item, {
              value: row.priorityLevel,
            });
          }
        });
      }
    });
    if (!hasPriorityLevel) {
      bpmnModeler.get('modeling').updateModdleProperties(row.extensionElements, Properties, {
        values: [
          ...(Properties || ({} as ANY_OBJECT)).values,
          bpmnModeler.get('moddle').create('flowable:Property', {
            name: 'priorityLevel',
            value: row.priorityLevel,
          }),
        ],
      });
    }
  } else {
    row.extensionElements = bpmnModeler.get('moddle').create('bpmn:ExtensionElements', {
      values: [
        bpmnModeler.get('moddle').create('flowable:Properties', {
          values: [
            bpmnModeler.get('moddle').create('flowable:Property', {
              name: 'priorityLevel',
              value: row.priorityLevel,
            }),
          ],
        }),
      ],
    });
  }
  bpmnModeler.get('modeling').updateProperties(row.element, {
    extensionElements: row.extensionElements,
    conditionExpression: row.conditionExpression,
  });
};
</script>

<style scoped lang="scss">
.add-node-btn-box {
  position: relative;
  display: inline-box;
  display: inline-flexbox;
  display: inline-flex;
  flex-shrink: 0;
  width: 240px;
  -ms-flex-negative: 0;
  -webkit-box-flex: 1;
  -ms-flex-positive: 1;

  &::before {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: -1;
    width: 2px;
    height: 100%;
    margin: auto;
    background-color: #d9dbdd;
    content: '';
  }

  .add-node-btn {
    display: flex;
    justify-content: center;
    flex-shrink: 0;
    width: 240px;
    padding: 20px 0 32px;
    user-select: none;
    -webkit-box-pack: center;
    -webkit-box-flex: 1;
    flex-grow: 1;

    .btn {
      position: relative;
      display: flex;
      justify-content: center;
      align-items: center;
      width: 28px;
      height: 28px;
      background: white;
      border: 1px solid #d9dbdd;
      border-radius: 50%;
      box-shadow: 0 2px 4px 0 rgb(0 0 0 / 10%);
      transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);

      .el-icon-plus {
        width: 16px;
        font-size: 16px;
        color: #333;
        font-weight: bold;
      }

      &:hover {
        transform: scale(1.3);
        box-shadow: 0 13px 27px 0 rgb(0 0 0 / 10%);
      }

      &:active {
        transform: none;
        background: white;
        box-shadow: 0 2px 4px 0 rgb(0 0 0 / 10%);
      }
    }
  }
}

.success .add-node-btn-box,
.add-node-btn-box.active {
  &::before {
    z-index: 0;
    background-color: #999;
  }
}
.current .add-node-btn-box {
  &::before {
    z-index: -1;
    background-color: #cacaca;
  }
}
</style>
<style lang="scss">
.add-node-popover-body {
  display: flex;

  .add-node-popover-item {
    margin: 0 8px;
    cursor: pointer;
    text-align: center;
    flex: 1;
    color: #333 !important;

    .item-wrapper {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 80px;
      height: 80px;
      margin-bottom: 5px;
      background: #fff;
      border: 1px solid #e2e2e2;
      border-radius: 50%;
      user-select: none;
      // transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);

      .iconfont {
        width: 35px;
        font-size: 35px;

        &:nth-child(2) {
          display: none;
        }
      }
    }

    p {
      margin-bottom: 0;
    }

    &.approver {
      .item-wrapper {
        color: #ff943e;
      }
    }

    &.notifier {
      .item-wrapper {
        color: #3296fa;
      }
    }

    &.condition {
      .item-wrapper {
        color: #15bc83;
      }
    }

    &:hover {
      .item-wrapper {
        background: $color-primary;
        box-shadow: 0 2px 8px 1px rgb(255 119 0 / 30%);

        .iconfont {
          &:nth-child(1) {
            display: none;
          }

          &:nth-child(2) {
            display: block;
          }
        }
      }

      .iconfont {
        color: #fff;
      }
    }

    &:active {
      .item-wrapper {
        background: #d9dbdd;
        box-shadow: none;
      }

      .iconfont {
        color: inherit;
      }
    }
  }
}
</style>
