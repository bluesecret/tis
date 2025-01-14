<template>
  <div class="dingflow-design" @mousedown.prevent="mousedown" @mousemove.prevent="mousemove">
    <div class="dingflow-design-header">
      <div class="img" @click="openFile" v-show="!readonly">
        <img src="@/assets/img/import.png" alt="" />
      </div>
      <div class="img" @click="downloadProcessAsXml" v-show="!readonly">
        <img src="@/assets/img/down.png" alt="" />
      </div>
      <div class="img" @click="previewProcessXML" v-show="!readonly">
        <img src="@/assets/img/preview.png" alt="" />
      </div>
      <span class="line" v-show="!readonly"></span>
      <div class="zoom">
        <div
          class="img"
          :class="'zoom-out' + (nowVal === 50 ? ' disabled' : '')"
          @click="zoomSize(1)"
        >
          <img src="@/assets/img/reduce.png" alt="" />
        </div>
        <span>{{ nowVal }}%</span>
        <div
          class="img"
          :class="'zoom-in' + (nowVal === 300 ? ' disabled' : '')"
          @click="zoomSize(2)"
        >
          <img src="@/assets/img/add.png" alt="" />
        </div>
      </div>
    </div>
    <div class="design-scroll" ref="scroll">
      <div
        v-if="nodeConfig && onload"
        class="box-scale"
        id="box-scale"
        :key="nowVal / 100"
        :style="'transform: scale(' + nowVal / 100 + '); transform-origin: 50% 0px 0px;'"
      >
        <nodeWrap
          v-model:nodeConfig="nodeConfig"
          :bpmnModeler="bpmnModeler"
          @elementClick="elementClick"
          :readonly="readonly"
        ></nodeWrap>
        <div class="end-node" :class="{ active: finishedTaskSet.indexOf(endId) > -1 }">
          <div class="end-node-circle"></div>
          <div class="end-node-text">流程结束</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { SysCommonBizController } from '@/api/system/';
import { ANY_OBJECT } from '@/types/generic';
import { FlowNodeType } from '@/common/staticDict/flow';
import { treeDataTranslate } from '@/common/utils';
import { useOtherStore } from '@/store';
import nodeWrap from './nodeWrap.vue';

const props = withDefaults(
  defineProps<{
    bpmnModeler?: ANY_OBJECT;
    finishedInfo?: ANY_OBJECT;
    readonly?: boolean;
    downloadProcessAsXml?: () => void;
    previewProcessXML?: () => void;
    openFile?: () => void;
  }>(),
  {
    readonly: false,
    downloadProcessAsXml: () => {
      return () => {
        console.warn('default empty function {downloadProcessAsXml}');
      };
    },
    previewProcessXML: () => {
      return () => {
        console.warn('default empty function {previewProcessXML}');
      };
    },
    openFile: () => {
      return () => {
        console.warn('default empty function {openFile}');
      };
    },
  },
);

const otherStore = useOtherStore();

// refs
const scroll = ref();
// data
const nodeConfig = ref<ANY_OBJECT>({});
const nowVal = ref(100);
const users = ref<ANY_OBJECT[]>([]);
const roleList = ref<ANY_OBJECT[]>([]);
const roleMap = new Map();
const groupList = ref<ANY_OBJECT[]>([]);
const groupMap = new Map();
const deptPostMap = new Map();
let deptPostList = ref<ANY_OBJECT[]>([]);
const postMap = new Map();
const postList = ref<ANY_OBJECT[]>([]);
const onload = ref(false);
const isDown = ref(false);
const top = ref(0);
const left = ref(0);
const dx = ref(0);
const dy = ref(0);
const hasElementIds = ref<string[]>([]);
const endId = ref('');
// computed
const finishedSequenceFlowSet = computed(() => {
  return props.finishedInfo ? props.finishedInfo.finishedSequenceFlowSet : [];
});
const finishedTaskSet = computed(() => {
  return props.finishedInfo ? props.finishedInfo.finishedTaskSet : [];
});
const unfinishedTaskSet = computed(() => {
  return props.finishedInfo ? props.finishedInfo.unfinishedTaskSet : [];
});
// methods
const zoomSize = (type: number) => {
  if (type === 1) {
    if (nowVal.value === 50) return;
    nowVal.value -= 10;
  } else {
    if (nowVal.value === 300) return;
    nowVal.value += 10;
  }
};
const getNodeUserList = (element: ANY_OBJECT, config: ANY_OBJECT) => {
  let formKey: ANY_OBJECT = {};
  try {
    console.log('ding getNodeUserList', element);
    formKey = element?.businessObject?.formKey
      ? JSON.parse(element?.businessObject?.formKey)
      : JSON.parse(element?.businessObject?.$attrs['flowable:formKey']);
    config.groupType = formKey.groupType;
  } catch (error) {
    console.warn(error);
  }
  if (formKey.groupType === 'ASSIGNEE') {
    return (
      (
        element?.businessObject?.assignee?.split(',') ||
        element?.businessObject?.$attrs['flowable:assignee']?.split(',') ||
        []
      ).map((row: ANY_OBJECT) => {
        users.value.indexOf(row) === -1 && users.value.push(row);
        return {
          id: row,
          name: row,
        };
      }) || []
    );
  } else if (formKey.groupType === 'USERS') {
    return (
      (
        element?.businessObject?.candidateUsers?.split(',') ||
        element?.businessObject?.$attrs['flowable:candidateUsers']?.split(',') ||
        []
      ).map((row: ANY_OBJECT) => {
        users.value.indexOf(row) === -1 && users.value.push(row);
        return {
          id: row,
          name: row,
        };
      }) || []
    );
  } else if (formKey.groupType === 'DEPT') {
    return (
      (
        element?.businessObject?.candidateGroups?.split(',') ||
        element?.businessObject?.$attrs['flowable:candidateGroups']?.split(',') ||
        []
      ).map((row: ANY_OBJECT) => {
        return {
          id: row,
          name: row,
        };
      }) || []
    );
  } else if (formKey.groupType === 'ROLE') {
    return (
      (
        element?.businessObject?.candidateGroups?.split(',') ||
        element?.businessObject?.$attrs['flowable:candidateGroups']?.split(',') ||
        []
      ).map((row: ANY_OBJECT) => {
        return {
          id: row,
          name: row,
        };
      }) || []
    );
  } else if (formKey.groupType === 'POST') {
    return (
      (
        element?.businessObject?.candidateGroups?.split(',') ||
        element?.businessObject?.$attrs['flowable:candidateGroups']?.split(',') ||
        []
      ).map((row: ANY_OBJECT) => {
        const arr = row.split('__');
        return {
          id: arr[1],
          name: arr[1],
          type: arr[0],
        };
      }) || []
    );
  } else if (formKey.groupType === 'DEPT_POST_LEADER') {
    return [{ name: '发起人部门领导', type: 'DEPT_POST_LEADER' }];
  } else if (formKey.groupType === 'UP_DEPT_POST_LEADER') {
    return [{ name: '发起人上级部门领导', type: 'DEPT_POST_LEADER' }];
  }
  return [];
};
// 查找分支条件的目标节点
const findConditionTargetElement = (childNode: ANY_OBJECT) => {
  const obj: ANY_OBJECT = {};
  const findEndElement = (element: ANY_OBJECT, arr: string[]) => {
    let el = element?.incoming?.length > 1 ? element : null;
    el && arr.indexOf(el.id) === -1 && arr.push(el.id);
    element &&
      element.outgoing.forEach((row: ANY_OBJECT) => {
        if (row.target) {
          findEndElement(row.target, arr);
        }
      });
    return arr;
  };
  childNode.conditionNodes.forEach((row: ANY_OBJECT) => {
    const arr = findEndElement(row.element.target, []);
    arr.forEach((key: string) => {
      obj[key] = Number.isInteger(obj[key]) ? obj[key] + 1 : 1;
    });
  });
  let elementKey = '';
  Object.keys(obj).forEach(key => {
    if (
      obj[key] === childNode.conditionNodes.length &&
      !elementKey &&
      hasElementIds.value.indexOf(key) === -1
    ) {
      elementKey = key;
    }
  });
  let element: ANY_OBJECT = props.bpmnModeler
    ?.get('elementRegistry')
    .find(
      (el: ANY_OBJECT) =>
        ['bpmn:UserTask', 'bpmn:ExclusiveGateway', 'bpmn:ParallelGateway'].indexOf(el.type) > -1 &&
        el.id === elementKey,
    );
  element && hasElementIds.value.push(element.id);
  return element;
};
// 设置分支条件数据
const setConditionData = (config: ANY_OBJECT, childElement: ANY_OBJECT | null = null) => {
  const childNode: ANY_OBJECT = {};
  const conditionElement =
    childElement?.target ||
    (config.type === FlowNodeType.CONNECTING_LINE
      ? config.element.target
      : config.element.outgoing[0].target);
  childNode.nodeName =
    conditionElement?.businessObject?.name ||
    (conditionElement.type === 'bpmn:ExclusiveGateway' ? '网关' : '并行网关');
  childNode.type =
    conditionElement.type === 'bpmn:ExclusiveGateway'
      ? FlowNodeType.CONDITIONAL_BRANCH
      : FlowNodeType.PARALLEL_BRANCH;
  childNode.element = markRaw(conditionElement);
  childNode.conditionNodes = [];
  childNode.childNode = null;
  conditionElement.outgoing.forEach((element: ANY_OBJECT, index: number) => {
    let extensionElements =
      element.businessObject.get('extensionElements') ||
      props.bpmnModeler?.get('moddle').create('bpmn:ExtensionElements', { values: [] });
    let customCondition = (extensionElements.values || []).filter(
      (ex: ANY_OBJECT) =>
        ex.$type === 'flowable:CustomCondition' || ex.$type === 'flowable:customCondition',
    )[0];
    let conditionStr = '';
    if (conditionElement.type === 'bpmn:ParallelGateway') {
      conditionStr = '并行任务（同时进行）';
    } else if (element.source.businessObject?.default?.id === element?.id) {
      conditionStr = '默认流转路径';
    } else if (customCondition && customCondition.type === 'operation') {
      conditionStr = '内置按钮';
    } else if (!element.businessObject.conditionExpression) {
      conditionStr = '普通流转路径';
    } else {
      conditionStr = '条件流转路径';
    }
    let priorityLevel = index;
    element?.businessObject?.extensionElements?.values &&
      element.businessObject.extensionElements.values.forEach((row: ANY_OBJECT) => {
        if (row.$type === 'flowable:Properties' || row.$type === 'flowable:properties') {
          (row.values || row.$children).forEach((item: ANY_OBJECT) => {
            if (item.name === 'priorityLevel') {
              priorityLevel = item.value;
            }
          });
        }
      });
    let obj = {
      nodeName: element?.businessObject?.name || '',
      type: FlowNodeType.CONNECTING_LINE,
      priorityLevel: priorityLevel,
      element: markRaw(element),
      conditionStr: conditionStr,
      extensionElements: element.businessObject.extensionElements,
      conditionExpression: element.businessObject.conditionExpression,
      childNode: null,
    };
    childNode.conditionNodes.push(obj);
  });

  childNode.conditionNodes.sort(
    (a: ANY_OBJECT, b: ANY_OBJECT) => a.priorityLevel * 1 - b.priorityLevel * 1,
  );

  config.childNode = childNode;
  // 查找条件分支下的目标节点
  const element = findConditionTargetElement(childNode);
  if (element) {
    if (
      (element.type === 'bpmn:ExclusiveGateway' || element.type === 'bpmn:ParallelGateway') &&
      element.outgoing.length === 1
    ) {
      childNode.endElement = markRaw(element);
      element.outgoing[0]?.target?.incoming.length === 1 &&
        setNodeConfig(childNode, element.outgoing[0]);
    } else {
      setNodeConfig(childNode, element.incoming[0]);
    }
  }

  childNode.conditionNodes.forEach((row: ANY_OBJECT) => {
    setNodeConfig(row);
  });

  return childNode;
};
// 设置节点数据
const setNodeConfig = (config: ANY_OBJECT, childElement: ANY_OBJECT | null = null) => {
  let childNode: ANY_OBJECT | null = {};
  const element =
    childElement ||
    (config.type === FlowNodeType.CONNECTING_LINE
      ? config.element
      : config.element.outgoing && config.element.outgoing[0]);
  if (
    element?.target.type === 'bpmn:UserTask' &&
    (element.target.incoming.length === 1 || childElement)
  ) {
    let formKey: ANY_OBJECT = {};
    try {
      formKey = JSON.parse(element?.target?.businessObject?.formKey);
    } catch (error) {
      console.warn(error);
    }
    childNode.nodeName = element.target?.businessObject?.name || '审核人';
    childNode.type = FlowNodeType.APPROVED_BY;
    childNode.groupType = formKey ? formKey.groupType : 'ASSIGNEE';
    childNode.element = markRaw(element.target);
    childNode.nodeUserList = getNodeUserList(element.target, childNode);
    setNodeConfig(childNode);
  } else if (element?.target.type === 'bpmn:ServiceTask') {
    childNode.nodeName = element.target?.businessObject?.name || '服务任务';
    childNode.type = FlowNodeType.SERVICE_TASK;
    childNode.element = markRaw(element.target);
    childNode.nodeUserList = [];
    childNode.childNode = null;
    setNodeConfig(childNode);
  } else if (element?.target.type === 'bpmn:ReceiveTask') {
    childNode.nodeName = element.target?.businessObject?.name || '接收任务';
    childNode.type = FlowNodeType.RECEIVE_TASK;
    childNode.element = markRaw(element.target);
    childNode.nodeUserList = [];
    childNode.childNode = null;
    setNodeConfig(childNode);
  } else if (
    (element?.target?.type === 'bpmn:ExclusiveGateway' ||
      element?.target?.type === 'bpmn:ParallelGateway') &&
    element.target.outgoing.length > 1 &&
    (element.target.incoming.length === 1 || childElement)
  ) {
    childNode = setConditionData(config, childElement);
  } else {
    childNode = null;
  }
  config.childNode = childNode;
};
const initData = () => {
  const StartEvent = props.bpmnModeler
    ?.get('elementRegistry')
    .find((el: ANY_OBJECT) => el.type === 'bpmn:StartEvent');
  const firstNode = StartEvent.outgoing[0]?.target;
  let nodeName, type;
  if (firstNode?.type === 'bpmn:UserTask') {
    nodeName = firstNode?.businessObject?.name || '发起人';
    type = FlowNodeType.ORIGINATOR;
  } else {
    nodeName = firstNode?.businessObject?.name || '接受任务';
    type = FlowNodeType.RECEIVE_TASK;
  }
  const config = {
    nodeName: nodeName,
    type: type,
    element: markRaw(firstNode),
    groupType: '',
    nodeUserList: [],
    childNode: null,
  };
  config.nodeUserList = getNodeUserList(firstNode, config);
  setNodeConfig(config);
  nodeConfig.value = config;
};

const loadDeptWidgetDropdownList = () => {
  return new Promise((resolve, reject) => {
    let params = {
      widgetType: 'upms_dept',
      filter: {},
    };
    SysCommonBizController.list(params, {
      showMask: false,
    })
      .then(res => {
        let list: ANY_OBJECT[] = [];
        res.data.dataList.forEach(item => {
          const obj = {
            id: String(item.deptId),
            name: item.deptName,
            parentId: String(item.parentId),
            ...item,
          };
          groupMap.set(obj.id, obj);
          list.push(obj);
        });
        groupList.value = treeDataTranslate(list);
        resolve(res);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const loadSysPostList = () => {
  return new Promise((resolve, reject) => {
    let params = {
      widgetType: 'upms_post',
      filter: {},
    };
    SysCommonBizController.list(params, {
      showMask: false,
    })
      .then(res => {
        res.data.dataList.forEach(item => {
          const obj = {
            id: String(item.postId),
            name: item.postName,
            ...item,
          };
          postMap.set(obj.id, obj);
        });
        postList.value = res.data.dataList;
        resolve(res);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const loadDeptPostList = () => {
  return new Promise((resolve, reject) => {
    let params = {
      widgetType: 'upms_dept_post',
      filter: {},
    };
    SysCommonBizController.list(params, {
      showMask: false,
    })
      .then(res => {
        res.data.dataList.forEach(item => {
          deptPostMap.set(item.deptPostId, item);
        });
        deptPostList.value = res.data.dataList;
        resolve(res);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const loadSysRoleList = () => {
  return new Promise((resolve, reject) => {
    let params = {
      widgetType: 'upms_role',
      filter: {},
    };
    SysCommonBizController.list(params, {
      showMask: false,
    })
      .then(res => {
        roleList.value = res.data.dataList.map(item => {
          let obj = {
            id: String(item.roleId),
            name: item.roleName,
            ...item,
          };
          roleMap.set(obj.id, obj);
          return obj;
        });
        resolve(res);
      })
      .catch(e => {
        reject(e);
      });
  });
};
const getUserShowName = () => {
  return new Promise(resolve => {
    let params = {
      widgetType: 'upms_user',
      fieldName: 'loginName',
      fieldValues: users.value.join(','),
    };

    // 去掉流程启动人和指定审批人
    params.fieldValues = params.fieldValues
      .split(',')
      .filter(row => {
        return ['${startUserName}', '${appointedAssignee}'].indexOf(row) === -1;
      })
      .join(',');

    if (params.fieldValues) {
      SysCommonBizController.viewByIds(params)
        .then(res => {
          console.log('ProcessDingflowDesigner.getUserShowName.vieByIds', params, res);
          let userShowNameData = { ...otherStore.getUserShowNameData };
          (res.data || []).forEach((item: ANY_OBJECT) => {
            userShowNameData[item.loginName] = item.showName;
          });
          otherStore.setUserShowNameData(userShowNameData);
          resolve(res);
        })
        .catch(e => {
          resolve(e);
        });
    } else {
      resolve(null);
    }
  });
};

const mousedown = (e: MouseEvent) => {
  top.value = scroll.value.scrollTop;
  left.value = scroll.value.scrollLeft;
  dx.value = e.clientX;
  dy.value = e.clientY;
  isDown.value = true;
};
const mousemove = (e: MouseEvent) => {
  if (isDown.value) {
    let x = dx.value - e.clientX;
    let y = dy.value - e.clientY;

    scroll.value.scrollTop = y + top.value;
    scroll.value.scrollLeft = x + left.value;
  }
};
const mouseup = () => {
  isDown.value = false;
};
onMounted(() => {
  endId.value = props.bpmnModeler
    ?.get('elementRegistry')
    .find((el: ANY_OBJECT) => el.type === 'bpmn:EndEvent').id;
  window.addEventListener('mouseup', mouseup);
  let httpCall = [
    loadDeptWidgetDropdownList(),
    loadDeptPostList(),
    loadSysPostList(),
    loadSysRoleList(),
  ];
  Promise.all(httpCall).then(() => {
    initData();
    getUserShowName().then(() => {
      onload.value = true;
    });
  });
});
onUnmounted(() => {
  window.removeEventListener('mouseup', mouseup);
});

// emits
const emit = defineEmits<{ elementClick: [ANY_OBJECT] }>();
const elementClick = (element: ANY_OBJECT) => {
  console.log('elementClick', element);
  emit('elementClick', element);
};

// expose method
const refresh = () => {
  console.log('ProcessDingflowDesigner.refresh');
  endId.value = props.bpmnModeler
    ?.get('elementRegistry')
    .find((el: ANY_OBJECT) => el.type === 'bpmn:EndEvent').id;
  initData();
  getUserShowName();
};
const getNodeConfig = () => {
  return nodeConfig.value;
};
defineExpose({
  refresh,
  getNodeConfig,
});

// povide
provide('bpmnModeler', props.bpmnModeler);
provide('groupMap', groupMap);
provide('deptPostMap', deptPostMap);
provide('postMap', postMap);
provide('roleMap', roleMap);
provide('finishedSequenceFlowSet', finishedSequenceFlowSet);
provide('finishedTaskSet', finishedTaskSet);
provide('unfinishedTaskSet', unfinishedTaskSet);

watch(nodeConfig, (node, old) => {
  console.log('nodeConfig changed', node, old);
});
</script>

<style scoped>
.dingflow-design {
  position: relative;
  width: 100%;
  height: 100%;
  background-color: #f5f5f7;
}
.design-scroll {
  position: relative;
  overflow: auto;
  width: 100%;
  height: 100%;
}
.dingflow-design-header {
  position: absolute;
  top: 24px;
  right: 26px;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 40px;
  padding-left: 10px;
  background: white;
}
.dingflow-design-header > div {
  padding: 0 6px;
}
.dingflow-design-header .img {
  cursor: pointer;
}

.dingflow-design-header .img img {
  width: 20px;
  height: 20px;
}

.dingflow-design-header .line {
  width: 1px;
  height: 26px;
  margin: 0 10px;
  background-color: #d9dbdd;
}
.zoom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 125px;
  height: 40px;
  -webkit-box-align: center;
  -ms-flex-align: center;
  -webkit-box-pack: justify;
  -ms-flex-pack: justify;
}
.zoom .zoom-in,
.zoom .zoom-out {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 30px;
  height: 30px;
  color: #c1c1cd;
  background: #fff;
  background-repeat: no-repeat;
  background-size: 100%;
  cursor: pointer;
}
.zoom .zoom-out.disabled {
  opacity: 0.5;
}
.zoom .zoom-in.disabled {
  opacity: 0.5;
}
.active.end-node .end-node-circle {
  background: #999;
}
</style>
