<template>
  <div @click="selectProcess">
    <div
      class="node-wrap"
      :class="{
        gateway: nodeConfig.type === FlowNodeType.CONDITIONAL_BRANCH,
        current: unfinishedTaskSet.indexOf(nodeConfig?.element?.id) > -1,
        success:
          finishedTaskSet.indexOf(nodeConfig?.element?.id) > -1 &&
          unfinishedTaskSet.indexOf(nodeConfig?.element?.id) === -1,
        start: nodeConfig.type === FlowNodeType.ORIGINATOR,
      }"
      v-if="
        [FlowNodeType.CONDITIONAL_BRANCH, FlowNodeType.PARALLEL_BRANCH].indexOf(nodeConfig.type) ===
        -1
      "
    >
      <div
        class="node-wrap-box"
        :class="
          (nodeConfig.type === FlowNodeType.ORIGINATOR ? 'start-node ' : '') +
          (isActiveId === nodeConfig.element.id ? 'active ' : '') +
          (nodeConfig.nodeUserList.length === 0 &&
          nodeConfig.type === FlowNodeType.APPROVED_BY &&
          !readonly
            ? 'error'
            : '')
        "
      >
        <div @click.stop="setPerson()">
          <div class="title" :key="nodeConfig.type" :style="`background: rgb(${bgColor()});`">
            <span v-if="nodeConfig.type === FlowNodeType.ORIGINATOR">{{
              nodeConfig.nodeName
            }}</span>
            <template v-else>
              <img
                v-if="nodeConfig.type === FlowNodeType.APPROVED_BY"
                class="iconfont"
                src="@/assets/img/sp1.png"
                alt=""
              />
              <el-icon v-if="nodeConfig.type === FlowNodeType.SERVICE_TASK" class="iconfont">
                <Setting />
              </el-icon>
              <el-icon v-if="nodeConfig.type === FlowNodeType.RECEIVE_TASK" class="iconfont">
                <Message />
              </el-icon>
              <span class="editable-title">{{ nodeConfig.nodeName }}</span>
              <el-icon v-if="!readonly" class="anticon anticon-close close" @click.stop="delNode">
                <Close />
              </el-icon>
            </template>
          </div>
          <div class="content">
            <div class="text">
              <span class="placeholder" v-if="!showText">{{ defaultText }}</span>
              <span v-else>{{ showText }}</span>
            </div>
            <!-- <i class="anticon anticon-right arrow el-icon-arrow-right"></i> -->
          </div>
          <div class="error_tip" v-if="isTried && nodeConfig.error">
            <i class="anticon anticon-exclamation-circle"></i>
          </div>
        </div>
      </div>
      <addNode
        :class="{ active: finishedTaskSet.indexOf(nodeConfig?.element?.id) > -1 }"
        v-model:childNodeP="childNode"
        :key="'a' + nodeConfig.type + nodeConfig?.element?.id"
        :isGateway="
          [FlowNodeType.CONDITIONAL_BRANCH, FlowNodeType.PARALLEL_BRANCH].indexOf(nodeConfig.type) >
          -1
        "
        v-model:nodeConfig="nodeConfigWritable"
      ></addNode>
    </div>
    <div
      class="branch-wrap"
      v-if="
        [FlowNodeType.CONDITIONAL_BRANCH, FlowNodeType.PARALLEL_BRANCH].indexOf(nodeConfig.type) >
        -1
      "
    >
      <div class="branch-box-wrap">
        <div class="branch-box">
          <button
            class="add-branch"
            @click.stop="addTerm"
            ref="addBranch"
            :class="{ readonly: readonly }"
          >
            {{ nodeConfig.type === FlowNodeType.CONDITIONAL_BRANCH ? '添加条件' : '添加分支' }}
          </button>
          <div
            class="col-box"
            ref="colBoxes"
            v-for="(item, index) in conditionNodes"
            :key="nodeConfig?.element?.id + item?.element?.id + index"
            :class="{
              active: finishedSequenceFlowSet.indexOf(item?.element?.id) > -1,
              done: isDone(item),
            }"
          >
            <div
              class="top-line"
              :key="'top-line' + nodeConfig?.element?.id + item?.element?.id + index"
              :style="'width:' + widths[index] + 'px'"
              :class="getLineConditionNodesClass(index)"
            ></div>
            <div
              class="end-line"
              v-if="isDone(item)"
              :key="'end-line' + nodeConfig?.element?.id + item?.element?.id + index"
              :style="'width:' + widths[index] + 'px'"
              :class="getLineConditionNodesClass(index)"
            ></div>
            <div class="condition-node">
              <div class="condition-node-box">
                <div
                  @click.stop="setPerson(item.priorityLevel)"
                  class="auto-judge"
                  :class="{
                    success: finishedSequenceFlowSet.indexOf(item?.element?.id) > -1,
                    active: isActiveId === item.element.id,
                  }"
                >
                  <div class="title-wrapper">
                    <span
                      class="editable-title"
                      v-if="nodeConfig.type === FlowNodeType.CONDITIONAL_BRANCH"
                      >{{ item.nodeName || '满足条件' }}</span
                    >
                    <span
                      class="editable-title"
                      v-if="nodeConfig.type === FlowNodeType.PARALLEL_BRANCH"
                      >{{ item.nodeName || '并行分支' }}</span
                    >
                    <div>
                      <span class="priority-title"
                        >{{ nodeConfig.type === FlowNodeType.CONDITIONAL_BRANCH ? '优先级' : '分支'
                        }}{{ index + 1 }}</span
                      >
                      <el-icon
                        v-if="!readonly"
                        class="anticon anticon-close close"
                        @click.stop="delTerm(index)"
                        ><Close
                      /></el-icon>
                    </div>
                  </div>
                  <div class="content">{{ conditionStr(nodeConfig, index) }}</div>
                  <div class="error_tip" v-if="isTried && item.error">
                    <i class="anticon anticon-exclamation-circle"></i>
                  </div>
                </div>
                <addNode
                  :key="'a' + item.type + item?.element?.id"
                  v-model:childNodeP="item.childNode"
                  :priorityLevel="item.priorityLevel"
                  v-model:nodeConfig="nodeConfigWritable"
                ></addNode>
              </div>
            </div>
            <nodeWrap
              :key="'c' + item?.element?.id + (item?.childNode?.id || '')"
              v-if="item.childNode"
              v-model:nodeConfig="item.childNode"
              :readonly="readonly"
              :parent="item"
              @elementClick="elementClick"
            ></nodeWrap>
            <template v-if="!index">
              <div class="top-left-cover-line"></div>
              <div class="bottom-left-cover-line"></div>
            </template>
            <template v-if="index === conditionNodes.length - 1">
              <div class="top-right-cover-line"></div>
              <div class="bottom-right-cover-line"></div>
            </template>
          </div>
        </div>
        <addNode
          :class="{ active: finishedTaskSet.indexOf(nodeConfig?.childNode?.element?.id) > -1 }"
          v-model:childNodeP="childNode"
          :key="'a' + nodeConfig.type + nodeConfig?.element?.id"
          :isEnd="true"
          v-model:nodeConfig="nodeConfigWritable"
        ></addNode>
      </div>
    </div>
    <nodeWrap
      v-if="nodeConfig.childNode"
      :key="'b' + nodeConfig?.element?.id + (nodeConfig?.childNode?.id || '')"
      v-model:nodeConfig="childNode"
      @elementClick="elementClick"
      :readonly="readonly"
      :parent="nodeConfig"
    ></nodeWrap>
  </div>
</template>

<script setup lang="ts">
import { Close, Setting, Message } from '@element-plus/icons-vue';
import { ref, provide, inject, computed, onMounted } from 'vue';
import addNode from './addNode.vue';
import nodeWrap from './nodeWrap.vue';
import { ANY_OBJECT } from '@/types/generic';
import { FlowNodeType, AutoTaskActionType } from '@/common/staticDict/flow';
import { useOtherStore } from '@/store';

const emit = defineEmits<{ elementClick: [ANY_OBJECT]; 'update:nodeConfig': [ANY_OBJECT] }>();

const props = withDefaults(
  defineProps<{ nodeConfig: ANY_OBJECT; parent?: ANY_OBJECT; readonly?: boolean }>(),
  { readonly: false },
);
console.log('nodeWrap props', props);
provide('nodeConfig', props.nodeConfig);
provide('readonly', props.readonly);
provide('parent', props.parent);

const otherStore = useOtherStore();

// refs
const colBoxes = ref();
const addBranch = ref();
// data
const placeholderList = ['发起人', '审核人', '抄送人'];
const isTried = ref(false);
const showText = ref('');
const endId = ref('');
const widths = ref<number[]>([]);
const isActiveId = ref('');
// inject
const bpmnModeler = inject('bpmnModeler', {} as ANY_OBJECT);
const groupMap = inject('groupMap', new Map());
const deptPostMap = inject('deptPostMap', new Map());
const postMap = inject('postMap', new Map());
const roleMap = inject('roleMap', new Map());
const finishedTaskSet = inject('finishedTaskSet', ref<ANY_OBJECT[]>([]));
const finishedSequenceFlowSet = inject('finishedSequenceFlowSet', ref<ANY_OBJECT[]>([]));
const unfinishedTaskSet = inject('unfinishedTaskSet', ref<ANY_OBJECT[]>([]));
// computed
const defaultText = computed(() => {
  if (
    [FlowNodeType.ORIGINATOR, FlowNodeType.APPROVED_BY, FlowNodeType.CC_TO].indexOf(
      props.nodeConfig.type,
    ) > -1
  ) {
    return '设置' + placeholderList[props.nodeConfig.type];
  } else {
    return '';
  }
});
const bgColor = () => {
  if (
    props.nodeConfig.type === FlowNodeType.SERVICE_TASK ||
    props.nodeConfig.type === FlowNodeType.RECEIVE_TASK
  ) {
    let elExtensionElements = props.nodeConfig.element.businessObject.get('extensionElements');
    let taskInfo;
    if (elExtensionElements) {
      let taskInfoElement = elExtensionElements.values.filter(
        ex => ex.$type === `flowable:TaskInfo` || ex.$type === `flowable:taskInfo`,
      )[0];
      if (taskInfoElement) {
        taskInfo = taskInfoElement.data;
        taskInfo = taskInfo && taskInfo !== '' ? JSON.parse(taskInfo) : {};
      }
    }
    if (taskInfo) {
      switch (taskInfo.actionType) {
        case AutoTaskActionType.INSERT:
        case AutoTaskActionType.UPDATE:
        case AutoTaskActionType.DELETE:
          return '255, 164, 85';
        case AutoTaskActionType.QUERY_SINGLE_DATA:
        case AutoTaskActionType.AGGREGATE:
        case AutoTaskActionType.FORMULA:
          return '67, 92, 216';
        case AutoTaskActionType.HTTP:
          return '126, 91, 191';
        default:
          return ['144, 145, 150', '98, 168, 252', '255, 164, 85'][props.nodeConfig.type];
      }
    } else {
      return ['144, 145, 150', '98, 168, 252', '255, 164, 85'][props.nodeConfig.type];
    }
  } else {
    return ['144, 145, 150', '98, 168, 252', '255, 164, 85'][props.nodeConfig.type];
  }
};
const nodeConfigWritable = computed({
  get() {
    return props.nodeConfig;
  },
  set(val) {
    emit('update:nodeConfig', val);
  },
});
const childNode = computed({
  get() {
    //console.log('nodeWrap nodeConfig', props.nodeConfig);
    //return props.nodeConfig.childNode ? { ...props.nodeConfig.childNode } : null;
    return props.nodeConfig.childNode;
  },
  set(val) {
    console.log('nodeConfig.childNode change', val, props.parent);
    const node = { ...props.nodeConfig };
    node.childNode = val;
    // if (val == null || val.type == 1) {
    //   node.childNode = val;
    // } else {
    //   console.log('网关或条件', node.conditionNodes);
    //   if (!node.conditionNodes) {
    //     node.conditionNodes = [];
    //   }
    //   node.conditionNodes.push(val);
    // }
    emit('update:nodeConfig', node);
  },
});
const conditionNodes = computed({
  get() {
    return props.nodeConfig.conditionNodes;
  },
  set(val) {
    console.log('nodeConfig.conditionNodes change', val);
  },
});
// methods
const elementClick = (element: ANY_OBJECT) => {
  emit('elementClick', element);
};
const setPerson = (priorityLevel: number | null = null) => {
  const { type, element, conditionNodes } = props.nodeConfig;
  if (
    (type === FlowNodeType.CONDITIONAL_BRANCH || type === FlowNodeType.PARALLEL_BRANCH) &&
    priorityLevel != null
  ) {
    bpmnModeler.get('selection').select(conditionNodes[priorityLevel].element);
    elementClick(conditionNodes[priorityLevel].element);
  } else {
    bpmnModeler.get('selection').select(element);
    elementClick(element);
  }
};
// 更新连接线数据
const updateConnectData = (row: ANY_OBJECT) => {
  console.log('updateConnectData', row);
  if (row.conditionStr === '默认流转路径') {
    bpmnModeler.get('modeling').updateProperties(row.element.source, {
      default: row.element,
    });
  }
  let Properties: ANY_OBJECT | null = null;
  if (row.extensionElements) {
    let hasPriorityLevel = false;
    if (row.extensionElements.values) {
      row.extensionElements.values.forEach((extension: ANY_OBJECT) => {
        if (
          extension.$type === 'flowable:Properties' ||
          extension.$type === 'flowable:properties'
        ) {
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
    }
    const values = (Properties || ({} as ANY_OBJECT)).values;
    if (!hasPriorityLevel && values) {
      bpmnModeler.get('modeling').updateModdleProperties(row.extensionElements, Properties, {
        values: [
          ...values,
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
const conditionStr = (nodeConfig: ANY_OBJECT, index: number) => {
  return nodeConfig.conditionNodes[index].conditionStr;
};
const resetConditionNodesErr = () => {
  const conditionNodes = props.nodeConfig.conditionNodes;
  for (let i = 0; i < props.nodeConfig.conditionNodes.length; i++) {
    conditionNodes[i].error =
      !conditionStr(props.nodeConfig, i) && i !== props.nodeConfig.conditionNodes.length - 1;
  }
};
const reData = (data: ANY_OBJECT, addData: ANY_OBJECT) => {
  !data.childNode ? (data.childNode = addData) : reData(data.childNode, addData);
};
const selectProcess = () => {
  const process = bpmnModeler
    .get('elementRegistry')
    .find((el: ANY_OBJECT) => el.type === 'bpmn:Process');
  bpmnModeler.get('selection').select(process);
};
// 查找所有子节点
const findNextChild = (nodeConfig: ANY_OBJECT, childNodes: ANY_OBJECT) => {
  if (nodeConfig) {
    nodeConfig.element && childNodes.push(nodeConfig.element);
    nodeConfig.endElement && childNodes.push(nodeConfig.endElement);
    if (nodeConfig.childNode) {
      findNextChild(nodeConfig.childNode, childNodes);
    }
    if (nodeConfig.conditionNodes) {
      nodeConfig.conditionNodes.forEach((row: ANY_OBJECT) => {
        findNextChild(row.childNode, childNodes);
      });
    }
  }
};
// 删除任务节点
const delNode = () => {
  //console.log('删除任务节点', props.nodeConfig, props.parent);
  const parentNode = props.parent;
  if (parentNode) {
    parentNode.childNode = props.nodeConfig.childNode;
  }
  bpmnModeler.get('modeling').removeElements([props.nodeConfig.element]);
  //console.log('删除任务节点, 子节点', props.nodeConfig.childNode);
  emit('update:nodeConfig', props.nodeConfig.childNode);
};
// 添加分支
const addTerm = () => {
  if (props.readonly) return;
  let len = props.nodeConfig.conditionNodes.length;
  let obj = {
    nodeName: '',
    type: FlowNodeType.CONNECTING_LINE,
    priorityLevel: len,
    extensionElements: undefined,
    conditionExpression: undefined,
    conditionStr:
      props.nodeConfig.type === FlowNodeType.CONDITIONAL_BRANCH
        ? '普通流转路径'
        : '并行任务（同时进行）',
    nodeUserList: [],
    element: markRaw(
      bpmnModeler.get('modeling').connect(props.nodeConfig.element, props.nodeConfig.endElement),
    ),
    childNode: undefined,
  };
  const nodeConfig = { ...props.nodeConfig };
  const conditionNodes = nodeConfig.conditionNodes;
  conditionNodes.push(obj);
  updateConnectData(obj);
  resetConditionNodesErr();
  emit('update:nodeConfig', nodeConfig);
};
// 删除分支
const delTerm = (index: number) => {
  console.log('删除分支', props);
  // 需要删除节点数组组
  const delNodes: ANY_OBJECT[] = [];
  const nodeConfig = { ...props.nodeConfig };
  const conditionNodes = nodeConfig.conditionNodes;
  // 如果只有两个分支删除一个则需要删除分支节点
  if (conditionNodes.length === 2) {
    delNodes.push(nodeConfig.element);
    nodeConfig.endElement && delNodes.push(nodeConfig.endElement);
  } else {
    delNodes.push(conditionNodes[index]?.element);
  }
  // 获取当前分支条件线下的所有节点
  findNextChild(conditionNodes[index], delNodes);
  console.log('要删除的节点', delNodes);
  // 删除当前条件节点和支条件节点下的所有节点
  bpmnModeler.get('modeling').removeElements(delNodes);
  conditionNodes.splice(index, 1);
  conditionNodes.length > 1 &&
    conditionNodes.forEach((item: ANY_OBJECT, index: number) => {
      item.priorityLevel = index;
      updateConnectData(item);
    });
  resetConditionNodesErr();
  console.log('delTerm nodeConfig >>>', { ...nodeConfig });
  emit('update:nodeConfig', nodeConfig);
  if (conditionNodes.length === 1) {
    if (nodeConfig.childNode) {
      if (conditionNodes[0].childNode) {
        reData(conditionNodes[0].childNode, nodeConfig.childNode);
      } else {
        conditionNodes[0].childNode = nodeConfig.childNode;
      }
    }
    emit('update:nodeConfig', conditionNodes[0].childNode || nodeConfig.childNode);
  }
};
const setApproverStr = (nodeConfig: ANY_OBJECT) => {
  console.log('nodeConfig.nodeUserList', nodeConfig.nodeUserList);
  if (!nodeConfig.nodeUserList) return '';
  if (nodeConfig.groupType === 'ASSIGNEE' || nodeConfig.groupType === 'USERS') {
    return nodeConfig.nodeUserList
      .map((item: ANY_OBJECT) => otherStore.getUserShowNameData[item.id] || item.name)
      .toString();
  } else if (nodeConfig.groupType === 'DEPT') {
    return nodeConfig.nodeUserList
      .map((item: ANY_OBJECT) => groupMap?.get(item.id)?.name || item.name)
      .toString();
  } else if (nodeConfig.groupType === 'ROLE') {
    return nodeConfig.nodeUserList
      .map((item: ANY_OBJECT) => roleMap?.get(item.id)?.name || item.name)
      .toString();
  } else if (nodeConfig.groupType === 'POST') {
    return nodeConfig.nodeUserList
      .map((item: ANY_OBJECT) => {
        if (item.type === 'allDeptPost') {
          return '全部' + ' / ' + (postMap?.get(item.id)?.name || item.name);
        } else if (item.type === 'selfDeptPost') {
          return '本部门' + ' / ' + (postMap?.get(item.id)?.name || item.name);
        } else if (item.type === 'siblingDeptPost') {
          return '同级部门' + ' / ' + (postMap?.get(item.id)?.name || item.name);
        } else if (item.type === 'upDeptPost') {
          return '上级部门' + ' / ' + (postMap?.get(item.id)?.name || item.name);
        } else if (item.type === 'deptPost') {
          return (
            deptPostMap?.get(item.id)?.deptName +
            ' / ' +
            (deptPostMap?.get(item.id)?.postShowName || item.name)
          );
        } else {
          return item.name;
        }
      })
      .toString();
  } else if (nodeConfig.groupType === 'DEPT_POST_LEADER') {
    return '发起人部门领导';
  } else if (nodeConfig.groupType === 'UP_DEPT_POST_LEADER') {
    return '发起人上级部门领导';
  }
};
const getNodeUserList = (element: ANY_OBJECT) => {
  let formKey: ANY_OBJECT = {};
  const nodeConfig = props.nodeConfig;
  try {
    //console.log('formKey', { ...element }, element);
    formKey = JSON.parse(element?.businessObject?.formKey);
    nodeConfig.groupType = formKey.groupType;
  } catch (error) {
    console.warn('parse formKey error', element?.businessObject?.formKey);
  }
  if (formKey.groupType === 'ASSIGNEE') {
    return (element?.businessObject?.assignee?.split(',') || []).map((row: ANY_OBJECT) => {
      return {
        id: row,
        name: row,
      };
    });
  } else if (formKey.groupType === 'USERS') {
    return (element?.businessObject?.candidateUsers?.split(',') || []).map((row: ANY_OBJECT) => {
      return {
        id: row,
        name: row,
      };
    });
  } else if (formKey.groupType === 'DEPT') {
    return (element?.businessObject?.candidateGroups?.split(',') || []).map((row: ANY_OBJECT) => {
      return {
        id: row,
        name: row,
      };
    });
  } else if (formKey.groupType === 'ROLE') {
    return (element?.businessObject?.candidateGroups?.split(',') || []).map((row: ANY_OBJECT) => {
      return {
        id: row,
        name: row,
      };
    });
  } else if (formKey.groupType === 'POST') {
    return (element?.businessObject?.candidateGroups?.split(',') || []).map((row: ANY_OBJECT) => {
      const arr = row.split('__');
      return {
        id: arr[1],
        name: arr[1],
        type: arr[0],
      };
    });
  } else if (formKey.groupType === 'DEPT_POST_LEADER') {
    return [{ name: '发起人部门领导', type: 'DEPT_POST_LEADER' }];
  } else if (formKey.groupType === 'UP_DEPT_POST_LEADER') {
    return [{ name: '发起人上级部门领导', type: 'DEPT_POST_LEADER' }];
  }
  return [];
};
const getLineConditionNodesClass = (index: number) => {
  const colBox = colBoxes.value ? colBoxes.value[index] : null;
  let colBoxLeft = 0;
  if (colBox) {
    colBoxLeft = colBox.offsetLeft + colBox.querySelector('.condition-node-box').offsetLeft;
  }
  let addBranchLeft = addBranch.value?.offsetLeft || 0;
  return colBoxLeft - addBranchLeft < 0 ? 'left' : 'right';
};
const getConditionNodesLineWidth = () => {
  const w: number[] = [...widths.value];
  props.nodeConfig.conditionNodes &&
    props.nodeConfig.conditionNodes.forEach((row: ANY_OBJECT, index: number) => {
      const colBox = colBoxes.value ? colBoxes.value[index] : null;
      let colBoxLeft = 0;
      if (colBox) {
        colBoxLeft = colBox.offsetLeft + colBox.querySelector('.condition-node-box').offsetLeft;
      }
      let addBranchLeft = addBranch.value?.offsetLeft || 0;
      let num = colBoxLeft - addBranchLeft;
      w[index] = num >= 0 ? colBoxLeft - addBranchLeft + 170 : addBranchLeft - (colBoxLeft + 170);
    });
  widths.value = w;
};
const isDone = (item: ANY_OBJECT) => {
  let childId = props.nodeConfig.endElement
    ? props.nodeConfig.endElement.id
    : props.nodeConfig.element.id;
  const nextChild = (nodeConfig: ANY_OBJECT) => {
    //console.log('nextChild', nodeConfig);
    if (nodeConfig.childNode) {
      childId = nodeConfig.childNode.endElement
        ? nodeConfig.childNode.endElement.id
        : nodeConfig.childNode.element.id;
      nextChild(nodeConfig.childNode);
    }
  };
  nextChild(item);
  return (
    finishedTaskSet.value.indexOf(childId) > -1 && unfinishedTaskSet.value.indexOf(childId) === -1
  );
};

onMounted(() => {
  const nodeConfig = props.nodeConfig;
  //console.log('nodeWrap nodeConfig', nodeConfig);
  endId.value = bpmnModeler
    .get('elementRegistry')
    .find((el: ANY_OBJECT) => el.type === 'bpmn:EndEvent').id;
  // 监听选择事件，修改当前激活的元素以及表单
  bpmnModeler.on('selection.changed', ({ newSelection }: { newSelection: ANY_OBJECT[] }) => {
    isActiveId.value = newSelection[0]?.id;
  });
  bpmnModeler.on('element.changed', ({ element }: { element: ANY_OBJECT }) => {
    //console.log('bpmnModeler element.changed', { ...element }, element, nodeConfig);
    if (nodeConfig.type === FlowNodeType.CONDITIONAL_BRANCH) {
      console.log('bpmnModeler element.changed 1');
      nodeConfig.conditionNodes.forEach((row: ANY_OBJECT) => {
        if (element.id === row?.element?.id) {
          let extensionElements =
            row.element.businessObject.get('extensionElements') ||
            bpmnModeler.get('moddle').create('bpmn:ExtensionElements', { values: [] });
          let conditionExpression =
            row.element.businessObject.get('conditionExpression') ||
            bpmnModeler.get('moddle').create('bpmn:FormalExpression');
          let customCondition = (extensionElements.values || []).filter(
            (ex: ANY_OBJECT) => ex.$type === 'flowable:CustomCondition',
          )[0];
          if (
            row.element.source &&
            row.element.source.businessObject.default?.id === row.element.id
          ) {
            row.conditionStr = '默认流转路径';
          } else if (!row.element.businessObject.conditionExpression) {
            row.conditionStr = '普通流转路径';
          } else if (customCondition && customCondition.type === 'operation') {
            row.conditionStr = '内置按钮';
          } else {
            row.conditionStr = '条件流转路径';
          }
          row.extensionElements = extensionElements;
          row.conditionExpression = conditionExpression;
          row.nodeName = element.businessObject.name || '';
        }
      });
      if (element.id === nodeConfig?.element?.id) {
        nodeConfig.nodeName = element.businessObject.name || '网关';
      }
    } else if (nodeConfig.type === FlowNodeType.PARALLEL_BRANCH) {
      console.log('bpmnModeler element.changed 2');
      nodeConfig.conditionNodes.forEach((row: ANY_OBJECT) => {
        if (element.id === row?.element?.id) {
          row.nodeName = element.businessObject.name || '';
        }
      });
    } else {
      console.log('bpmnModeler element.changed 3', { ...element });
      if (element.id === nodeConfig?.element?.id) {
        let name = '';
        if (nodeConfig.type === FlowNodeType.ORIGINATOR) {
          name = '发起人';
        } else if (nodeConfig.type === FlowNodeType.APPROVED_BY) {
          name = '审批人';
        }
        nodeConfig.nodeName = element.businessObject.name || name;
        nodeConfig.nodeUserList = getNodeUserList(element);
        showText.value = setApproverStr(props.nodeConfig);
      }
    }
  });
  if (nodeConfig.type === FlowNodeType.APPROVED_BY) {
    nodeConfig.error = !setApproverStr(props.nodeConfig);
  } else if (nodeConfig.type === FlowNodeType.CONDITIONAL_BRANCH) {
    resetConditionNodesErr();
  }
  // showText.value = setApproverStr(props.nodeConfig);
  getConditionNodesLineWidth();
});

watch(
  () => props.nodeConfig,
  () => {
    if (
      props.nodeConfig.type === FlowNodeType.SERVICE_TASK ||
      props.nodeConfig.type === FlowNodeType.RECEIVE_TASK
    ) {
      let elExtensionElements = props.nodeConfig.element.businessObject.get('extensionElements');
      let taskInfo;
      if (elExtensionElements) {
        let taskInfoElement = elExtensionElements.values.filter(
          ex => ex.$type === `flowable:TaskInfo` || ex.$type === `flowable:taskInfo`,
        )[0];
        if (taskInfoElement) {
          taskInfo = taskInfoElement.data;
          taskInfo = taskInfo && taskInfo !== '' ? JSON.parse(taskInfo) : {};
        }
      }
      showText.value = taskInfo
        ? AutoTaskActionType.getValue(taskInfo.actionType)
        : AutoTaskActionType.getValue(AutoTaskActionType.INSERT);
    } else {
      if (props.nodeConfig.nodeUserList) {
        showText.value = setApproverStr(props.nodeConfig);
      }
    }
  },
  {
    immediate: true,
    deep: true,
  },
);
watch(
  () => props.nodeConfig.conditionNodes,
  () => {
    nextTick(getConditionNodesLineWidth);
  },
  {
    deep: true,
  },
);
</script>

<style lang="scss">
.node-wrap {
  position: relative;
  justify-content: flex-start;
  align-items: center;
  padding: 0 50px;
  flex-direction: column;
  -webkit-box-pack: start;
  -ms-flex-pack: start;
  -webkit-box-align: center;
  -ms-flex-align: center;
  // flex-wrap: wrap;
  -webkit-box-flex: 1;
  -ms-flex-positive: 1;
  vertical-align: middle;
}

.node-wrap-box.error {
  .title {
    background-color: #f56c6c !important;
  }

  .content {
    .placeholder {
      color: #f56c6c;
    }

    background-color: #fef0f0;
  }
}

.node-wrap.success {
  .node-wrap-box {
    border: 0;

    .content {
      background-color: #f8fff8 !important;
      border: 1px solid #52b852;
    }
  }

  &.gateway .node-wrap-box {
    background-color: #fff;
    border: 0;

    .content {
      background-color: #f8fff8 !important;
      border: 1px solid #52b852;
    }
  }
}

.node-wrap.current {
  .node-wrap-box {
    border: 0;

    .content {
      background-color: #f1f5fe !important;
      border: 1px solid #62a8fc;
    }
  }

  &.gateway .node-wrap-box {
    background-color: #fff;
    border: 0;

    .content {
      background-color: #f1f5fe !important;
      border: 1px solid #62a8fc;
    }
  }
}

.node-wrap.start .node-wrap-box .content {
  background-color: #fff;
  border: none;
}

.node-wrap.start .node-wrap-box.error .content {
  background-color: #fef0f0;
}

.auto-judge.success {
  background-color: #f8fff8 !important;
  border: 1px solid #52b852;
}

.auto-judge.current {
  background-color: #f1f5fe !important;
  border: 1px solid #62a8fc;
}

.branch-wrap,
.node-wrap {
  display: inline-flex;
  width: 100%;
}

.branch-box-wrap {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  width: 100%;
  min-height: 270px;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  flex-direction: column;
  // flex-wrap: wrap;
  -webkit-box-align: center;
  -ms-flex-align: center;
  -ms-flex-negative: 0;
}

.error_tip {
  position: absolute;
  top: 0;
  right: 0;
  transform: translate(150%, 0);
  font-size: 24px;
}

.promoter_person .el-dialog__body {
  padding: 10px 20px 14px;
}

.selected_list {
  margin-bottom: 20px;
  line-height: 30px;
}

.selected_list span {
  padding: 3px 6px 3px 9px;
  margin-right: 10px;
  white-space: nowrap;
  border: 1px solid rgb(220 220 220 / 100%);
  border-radius: 2px;
  line-height: 12px;
}

.selected_list img {
  width: 7px;
  height: 7px;
  margin-left: 5px;
  cursor: pointer;
}

.auto-judge:hover .editable-title.editing,
.node-wrap-box:hover .editable-title.editing {
  text-decoration: none;
  border: 1px solid #d9d9d9;
}

.auto-judge:hover .editable-title {
  border-color: #15bc83;
}

.priority-title {
  padding-right: 10px;
  font-size: 12px;
  color: #999;
}

.node-wrap-box {
  position: relative;
  display: inline-box;
  display: inline-flexbox;
  display: inline-flex;
  flex-shrink: 0;
  width: 220px;
  min-height: 72px;
  background: #fff;
  border-radius: 4px;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  flex-direction: column;
  -ms-flex-negative: 0;
  cursor: pointer;
}

.node-wrap-box::after {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 2;
  border: 1px solid transparent;
  border-radius: 4px;
  box-shadow: 0 2px 5px 0 rgb(0 0 0 / 10%);
  transition: all 0.1s cubic-bezier(0.645, 0.045, 0.355, 1);
  content: '';
  pointer-events: none;
}

.node-wrap-box.active::after,
.node-wrap-box:active::after,
.node-wrap-box:hover::after {
  border: 1px solid #3296fa;
  box-shadow: 0 0 6px 0 rgb(50 150 250 / 30%);
}

.node-wrap-box.active .close,
.node-wrap-box:active .close,
.node-wrap-box:hover .close {
  display: block;
}

.node-wrap-box .title {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  height: 30px;
  padding-right: 30px;
  padding-left: 16px;
  font-size: 12px;
  text-align: left;
  color: #fff;
  background: #ffa455;
  border-radius: 4px 4px 0 0;
  line-height: 30px;
}

.node-wrap-box .title .iconfont {
  margin-right: 5px;
  font-size: 12px;
}

.node-wrap-box .placeholder {
  color: #333;
}

.node-wrap-box .close {
  position: absolute;
  top: 0;
  right: 10px;
  display: none;
  // transform: translateY(-50%);
  width: 20px;
  height: 20px;
  font-size: 14px;
  text-align: center;
  color: #fff;
  border-radius: 50%;
  line-height: 30px;
}

.node-wrap-box .content {
  position: relative;
  z-index: 2;
  padding: 16px 30px 16px 16px;
  font-size: 14px;
}

.node-wrap-box .content .text {
  display: -webkit-box;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #333;
  word-wrap: break-word;
  line-clamp: 3;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.node-wrap-box .content .arrow {
  position: absolute;
  top: 50%;
  right: 10px;
  width: 20px;
  height: 14px;
  font-size: 14px;
  color: #979797;
  transform: translateY(-50%);
}

.start-node.node-wrap-box .content .text {
  display: block;
  white-space: nowrap;
  color: #333;
}

.node-wrap-box::before {
  position: absolute;
  top: -12px;
  left: 50%;
  width: 0;
  height: 4px;
  background: #f5f5f7;
  border-style: solid;
  border-width: 8px 6px 4px;
  border-color: #d9dbdd transparent transparent;
  content: '';
  transform: translateX(-50%);
}

.success .node-wrap-box::before,
.current .node-wrap-box::before {
  border-color: #999 transparent transparent;
}

.node-wrap-box.start-node::before {
  content: none;
}

.branch-box {
  position: relative;
  display: flex;
  overflow: visible;
  height: auto;
  min-height: 180px;
  border-bottom: 2px solid #d9dbdd;
  border-top: 2px solid #d9dbdd;

  /* margin-top: 15px; */
}

.branch-box .col-box {
  position: relative;
  background: #f5f5f7;
}

.top-line,
.end-line {
  position: absolute;
  display: none;
  height: 2px;
  background-color: #999;
}

.active > .top-line {
  top: -2px;
  display: block;
}

.left {
  &.top-line,
  &.end-line {
    left: 50%;
  }
}

.right {
  &.top-line,
  &.end-line {
    right: 50%;
  }
}

.active > .end-line {
  bottom: -2px;
}

.branch-box .col-box::before {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 0;
  width: 2px;
  height: 100%;
  margin: auto;
  background-color: #d9dbdd;
  content: '';
}

.branch-box .done.active.col-box::before {
  background-color: #999;
}
.branch-box .done .end-line {
  display: block;
}

.box-scale {
  position: relative;
  display: inline-block;
  justify-content: center;
  align-items: flex-start;
  width: 100%;
  min-width: min-content;
  padding: 54.5px 0;
  background-color: #f5f5f7;
  transform: scale(1);
  -webkit-box-align: start;
  -ms-flex-align: start;
  -webkit-box-pack: center;
  -ms-flex-pack: center;
  flex-wrap: wrap;
  transform-origin: 50% 0 0;
}

.add-branch {
  position: absolute;
  top: -16px;
  left: 50%;
  z-index: 1;
  display: inline-flex;
  justify-content: center;
  align-items: center;
  height: 30px;
  padding: 0 10px;
  font-size: 12px;
  color: #333;
  background: #fff;
  border: none;
  border-radius: 15px;
  outline: none;
  box-shadow: 0 2px 4px 0 rgb(0 0 0 / 10%);
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  user-select: none;
  line-height: 30px;
  transform: translateX(-50%);
  transform-origin: center center;
  cursor: pointer;
}

.add-branch.readonly {
  // display: none;
  opacity: 0;
}

.add-branch:hover {
  transform: translateX(-50%) scale(1.1);
  box-shadow: 0 8px 16px 0 rgb(0 0 0 / 10%);
}

.add-branch:active {
  transform: translateX(-50%);
  box-shadow: none;
}

.col-box {
  position: relative;
  display: inline-flex;
  align-items: center;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  flex-direction: column;
  -webkit-box-align: center;
}

.condition-node {
  min-height: 220px;
}

.condition-node,
.condition-node-box {
  display: inline-flex;
  -webkit-box-orient: vertical;
  -webkit-box-direction: normal;
  flex-direction: column;
  -webkit-box-flex: 1;
}

.condition-node-box {
  position: relative;
  justify-content: center;
  align-items: center;
  padding-top: 30px;
  padding-right: 50px;
  padding-left: 50px;
  -webkit-box-pack: center;
  -webkit-box-align: center;
  flex-grow: 1;
}

.condition-node-box::before {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  width: 2px;
  height: 100%;
  margin: auto;
  background-color: #d9dbdd;
  content: '';
}

.active > .condition-node > .condition-node-box::before {
  background-color: #999;
}

.auto-judge {
  position: relative;
  width: 220px;
  min-height: 72px;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
}

.auto-judge::after {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 2;
  border: 1px solid transparent;
  border-radius: 4px;
  box-shadow: 0 2px 5px 0 rgb(0 0 0 / 10%);
  transition: all 0.1s cubic-bezier(0.645, 0.045, 0.355, 1);
  content: '';
  pointer-events: none;
}

.auto-judge.active::after,
.auto-judge:active::after,
.auto-judge:hover::after {
  border: 1px solid #3296fa;
  box-shadow: 0 0 6px 0 rgb(50 150 250 / 30%);
}

.auto-judge.active .close,
.auto-judge:active .close,
.auto-judge:hover .close {
  display: block;
}

.auto-judge.error::after {
  border: 1px solid #f25643;
  box-shadow: 0 2px 5px 0 rgb(0 0 0 / 10%);
}

.auto-judge .title-wrapper {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 30px;
  padding: 0 19px;
  font-size: 12px;
  color: #15bc83;
  border-bottom: 1px solid #e8e8e8;
  box-sizing: content-box;
}

.auto-judge .title-wrapper .editable-title {
  display: inline-block;
  overflow: hidden;
  max-width: 120px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.auto-judge .placeholder {
  color: #bfbfbf;
}

.auto-judge .close {
  position: absolute;
  top: 0;
  right: 8px;
  z-index: 2;
  display: none;
  width: 20px;
  height: 30px;
  font-size: 14px;
  text-align: center;
  color: #999;
  border-radius: 50%;
  line-height: 30px;
}

.auto-judge .content {
  position: relative;
  z-index: 2;
  display: -webkit-box;
  overflow: hidden;
  padding: 14px 19px 20px;
  font-size: 14px;
  text-align: left;
  text-overflow: ellipsis;
  color: #333;
  -webkit-box-orient: vertical;
  line-clamp: 3;
  -webkit-line-clamp: 3;
}

.auto-judge .sort-left,
.auto-judge .sort-right {
  position: absolute;
  top: 0;
  bottom: 0;
  z-index: 1;
  display: none;
}

.auto-judge .sort-left {
  left: 0;
  border-right: 1px solid #f6f6f6;
}

.auto-judge .sort-right {
  right: 0;
  border-left: 1px solid #f6f6f6;
}

.auto-judge:hover .sort-left,
.auto-judge:hover .sort-right {
  display: flex;
  align-items: center;
}

.auto-judge .sort-left:hover,
.auto-judge .sort-right:hover {
  background: #efefef;
}

.end-node {
  font-size: 14px;
  text-align: left;
  color: #999;
  border-radius: 50%;
}

.end-node .end-node-circle {
  width: 10px;
  height: 10px;
  margin: auto;
  background: #dbdcdc;
  border-radius: 50%;
}

.end-node .end-node-text {
  margin-top: 5px;
  text-align: center;
}

.editable-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 30px;
  // border-bottom: 1px dashed transparent;
}

.editable-title::before {
  position: absolute;
  top: 0;
  right: 40px;
  bottom: 0;
  left: 0;
  content: '';
}

.editable-title-input {
  z-index: 1;
  height: 18px;
  padding-left: 4px;
  font-size: 12px;
  text-indent: 0;
  flex: none;
  line-height: 18px;
}

.editable-title-input:hover {
  text-decoration: none;
}

.ant-btn {
  position: relative;
}

.top-left-cover-line {
  left: -1px;
}

.top-left-cover-line,
.top-right-cover-line {
  position: absolute;
  top: -4px;
  width: 50%;
  height: 8px;
  background-color: #f5f5f7;
}

.top-right-cover-line {
  right: -1px;
}

.bottom-left-cover-line {
  left: -1px;
}

.bottom-left-cover-line,
.bottom-right-cover-line {
  position: absolute;
  bottom: -4px;
  width: 50%;
  height: 8px;
  background-color: #f5f5f7;
}

.bottom-right-cover-line {
  right: -1px;
}
</style>
