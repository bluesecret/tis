<template>
  <div class="process-design" style="display: flex; height: 100%">
    <ProcessDesigner
      v-bind="controlForm"
      :key="`designer-${reloadIndex}`"
      v-model:value="xmlString"
      keyboard
      ref="processDesigner"
      :processId="flowEntryInfo.processDefinitionKey"
      :processName="flowEntryInfo.processDefinitionName"
      :flowEntryInfo="flowEntryInfo"
      :events="[
        'element.click',
        'connection.added',
        'connection.removed',
        'connection.changed',
        'element.changed',
        'element.updateId',
        'shape.removed',
      ]"
      @element-click="elementClick"
      @element-changed="elementChanged"
      @element-updateId="elementUpdateId"
      @shape-removed="onShapeRemoved"
      @init-finished="initModeler"
      @event="handlerEvent"
      @save="onSaveProcess"
    />
    <PropertiesPanel
      v-if="modeler"
      :key="`penal-${reloadIndex}`"
      :bpmn-modeler="modeler"
      :prefix="controlForm.prefix"
      :width="600"
      :autoTaskVariableList="autoTaskVariableList"
      :flowVariableList="flowVariableList"
      :templateVariableList="messageVariableList"
      class="process-panel"
    />
  </div>
</template>

<script setup lang="ts">
import hljs from 'highlight.js';
//import javascript from 'highlight.js/lib/languages/javascript';
import xml from 'highlight.js/lib/languages/xml';
import json from 'highlight.js/lib/languages/json';
import { FlowEntryVariableController, FlowDblinkController } from '@/api/flow';
import { SysFlowVariableType, AutoTaskActionType, FlowEntryType } from '@/common/staticDict/flow';
import { treeDataTranslate } from '@/common/utils';
import 'highlight.js/styles/atom-one-dark-reasonable.css';
import { ANY_OBJECT } from '@/types/generic';
import ProcessDesigner from '../package/process-designer/ProcessDesigner.vue';
import PropertiesPanel from '../package/refactor/PropertiesPanel.vue';
// 自定义元素选中时的弹出菜单（修改 默认任务 为 用户任务）
import CustomContentPadProvider from '../package/process-designer/plugins/content-pad/index.js';
// 自定义左侧菜单（修改 默认任务 为 用户任务）
import CustomPaletteProvider from '../package/process-designer/plugins/palette/index.js';

// Then register the languages you need
//hljs.registerLanguage('javascript', javascript);
hljs.registerLanguage('xml', xml);
hljs.registerLanguage('json', json);

const emit = defineEmits<{ save: [string] }>();
const props = defineProps<{ flowEntryInfo: ANY_OBJECT }>();

const processDesigner = ref();
let reloadIndex = 0;
const xmlString = ref<string>(props.flowEntryInfo.bpmnXml);
const modeler = ref();
const controlForm = ref<ANY_OBJECT>({
  processId: '',
  processName: '',
  simulation: false,
  labelEditing: false,
  labelVisible: false,
  prefix: 'flowable',
  headerButtonSize: 'default',
  additionalModel: [CustomContentPadProvider, CustomPaletteProvider],
});

const autoTaskVariableList = ref<ANY_OBJECT[]>([]);
const flowVariableList = ref<ANY_OBJECT[]>([]);
const messageVariableList = ref<ANY_OBJECT[]>([]);

const elementClick = (element: ANY_OBJECT) => {
  console.log('ProcessDesigner main', element);
};
const initModeler = (m: ANY_OBJECT) => {
  setTimeout(() => {
    modeler.value = m;
  }, 10);
};
const handlerEvent = (eventName: string, element: ANY_OBJECT | null, some: ANY_OBJECT) => {
  console.log('ProcessDesigner main', eventName, element, some);
};
const onSaveProcess = (saveData: string) => {
  emit('save', saveData);
};

const getFlowVariableList = () => {
  return new Promise((resolve, reject) => {
    let params = {
      entryId: props.flowEntryInfo.entryId,
    };
    FlowEntryVariableController.listAutoFlowVariables(params)
      .then(res => {
        flowVariableList.value = (res.data || []).reduce((retObj, item) => {
          let temp = retObj.find(v => v.id === item.variableType);
          if (temp == null) {
            temp = {
              id: item.variableType,
              name: SysFlowVariableType.getValue(item.variableType),
              children: [
                {
                  id: item.variableName,
                  name: item.showName,
                },
              ],
            };
            retObj.push(temp);
          } else {
            temp.children.push({
              id: item.variableName,
              name: item.showName,
            });
          }
          return retObj;
        }, []);
        resolve(flowVariableList.value);
      })
      .catch(err => {
        reject(err);
      });
  });
};

const getMessageVariableList = () => {
  let params = {
    entryId: props.flowEntryInfo.entryId,
  };
  FlowEntryVariableController.listMessageFlowVariables(params).then(res => {
    const result = res.data || [];
    const variableList = [];
    for (const item of result) {
      variableList.push({
        id: item.variableName,
        name: item.showName,
      });
    }
    messageVariableList.value = variableList;
  });
};

const buildVariableTree = (
  actionType: number,
  outputVariableName: string,
  fieldList: ANY_OBJECT[] | string[],
) => {
  let dataTree;
  if (actionType === AutoTaskActionType.HTTP || actionType === AutoTaskActionType.SEND_MQ) {
    if (fieldList == null) return [];
    let dataList = fieldList.map(item => {
      return {
        id: outputVariableName + '.' + item.id,
        name: item.fieldName,
        parentId: item.parentId ? outputVariableName + '.' + item.parentId : undefined,
        fieldName: item.fieldName,
        fieldType: item.fieldType,
      };
    });
    dataTree = treeDataTranslate(dataList, 'id', 'parentId');
  } else if (actionType === AutoTaskActionType.QUERY_SINGLE_DATA) {
    if (fieldList == null) return [];
    dataTree = fieldList.map(item => {
      return {
        id: outputVariableName + '.' + item,
        name: item,
      };
    });
  } else if (actionType === AutoTaskActionType.AGGREGATE) {
    if (fieldList == null) return [];
    dataTree = fieldList.map(item => {
      return {
        id: outputVariableName + '.' + item,
        name: item,
      };
    });
  } else if (actionType === AutoTaskActionType.FORMULA) {
    if (fieldList == null) return [];
    dataTree = fieldList.map(item => {
      return {
        id: outputVariableName + '.' + item,
        name: item,
      };
    });
  }
  return dataTree;
};
const updateAutoTaskVariable = (variableName, showName, children) => {
  let currentTaskVariable = autoTaskVariableList.value.find(
    (item: ANY_OBJECT) => item.id === variableName,
  );
  if (children && children.length > 0) {
    if (currentTaskVariable == null) {
      autoTaskVariableList.value.push({
        id: variableName,
        name: showName || variableName,
        children,
      });
    } else {
      currentTaskVariable.name = showName || variableName;
      currentTaskVariable.children = children;
    }
  } else {
    if (currentTaskVariable != null) {
      autoTaskVariableList.value = autoTaskVariableList.value.filter(
        item => item.id !== variableName,
      );
    }
  }
};
const elementUpdateId = (element, eventObj) => {
  console.log('elementUpdateId', element, eventObj);
  function updateItemId(item, newId, oldId) {
    item.id = newId + '.' + item.id.split('.').pop();
    if (item.children && item.children.length > 0) {
      item.children.forEach(child => {
        updateItemId(child, newId, oldId);
      });
    }
  }
  let oldId = element.id;
  let newId = eventObj.newId;
  autoTaskVariableList.value.forEach(item => {
    if (item.id === oldId) {
      item.id = newId;
      item.children.forEach(child => {
        updateItemId(child, newId, oldId);
      });
    }
  });
};
const elementChanged = async (element, eventObj) => {
  if (
    props.flowEntryInfo.flowType === FlowEntryType.AUTO_TASK &&
    element &&
    element.businessObject
  ) {
    let outputVariableName = element.businessObject.id;
    let outputVariableShowName = element.businessObject.name;
    let elExtensionElements = element.businessObject.get('extensionElements');
    if (elExtensionElements != null && Array.isArray(elExtensionElements.values)) {
      let taskInfoElement = elExtensionElements.values.filter(
        ex => ex.$type === `:TaskInfo`,
      )[0];
      if (taskInfoElement != null) {
        let taskData = taskInfoElement.data;
        if (taskData != null && taskData !== '') {
          let taskInfo = JSON.parse(taskData);
          let dataTree;
          if (taskInfo.actionType === AutoTaskActionType.INSERT) {
            //
          } else if (taskInfo.actionType === AutoTaskActionType.QUERY_SINGLE_DATA) {
            // 查询任务变量获取
            if (outputVariableName != null && outputVariableName !== '') {
              let selectFieldList = taskInfo.selectFieldList;
              if (
                (selectFieldList == null || selectFieldList.length === 0) &&
                taskInfo.srcDblinkId &&
                taskInfo.srcTableName
              ) {
                let res = await FlowDblinkController.listDblinkTableColumns({
                  dblinkId: taskInfo.srcDblinkId,
                  tableName: taskInfo.srcTableName,
                });
                selectFieldList = res.data.map(item => item.columnName);
              }
              dataTree = buildVariableTree(
                taskInfo.actionType,
                outputVariableName,
                selectFieldList,
              );
            }
          } else if (taskInfo.actionType === AutoTaskActionType.HTTP) {
            // HTTP任务变量获取
            if (
              outputVariableName != null &&
              outputVariableName !== '' &&
              taskInfo.httpResponnseData &&
              taskInfo.httpResponnseData.httpResponseBody
            ) {
              dataTree = buildVariableTree(
                taskInfo.actionType,
                outputVariableName,
                taskInfo.httpResponnseData.httpResponseBody,
              );
            }
          } else if (taskInfo.actionType === AutoTaskActionType.AGGREGATE) {
            // 聚合任务变量获取
            if (outputVariableName != null && outputVariableName !== '') {
              let selectFieldList = (taskInfo.aggregationDataList || []).map(item => item.alias);
              dataTree = buildVariableTree(
                taskInfo.actionType,
                outputVariableName,
                selectFieldList,
              );
            }
          } else if (taskInfo.actionType === AutoTaskActionType.FORMULA) {
            // 计算任变量获取
            if (outputVariableName != null && outputVariableName !== '') {
              let fieldList = ['dataResult'];
              dataTree = buildVariableTree(taskInfo.actionType, outputVariableName, fieldList);
            }
          }
          if (dataTree) {
            updateAutoTaskVariable(outputVariableName, outputVariableShowName, dataTree);
          }
        }
      }
    }
  }
};

const onShapeRemoved = (element, eventObj) => {
  setTimeout(() => {
    if (element && element.businessObject) {
      let outputVariableName = element.businessObject.id;
      let showName = element.businessObject.name;
      if (outputVariableName) {
        updateAutoTaskVariable(outputVariableName, showName, null);
      }
    }
  });
};

watch(
  () => props.flowEntryInfo.autoTaskVariableList,
  () => {
    if (props.flowEntryInfo.autoTaskVariableList != null) {
      autoTaskVariableList.value = props.flowEntryInfo.autoTaskVariableList
        .map(item => {
          let tempFieldList = item.fieldList || item.httpResponseBody || item.messageResponseBody;
          if (item.actionType === AutoTaskActionType.HTTP || item.actionType === AutoTaskActionType.SEND_MQ) {
            tempFieldList = JSON.parse(tempFieldList);
          }
          let children = buildVariableTree(item.actionType, item.taskKey, tempFieldList);
          if (children && children.length > 0) {
            return {
              taskId: item.taskId,
              id: item.taskKey,
              name: item.taskName || item.taskKey,
              actionType: item.actionType,
              children,
            };
          } else {
            return null;
          }
        })
        .filter(item => item != null);
    } else {
      autoTaskVariableList.value = [];
    }
  },
  {
    deep: true,
    immediate: true,
  },
);

onMounted(() => {
  getFlowVariableList();
  getMessageVariableList();
});

defineExpose({
  processDesigner,
});
</script>

<style lang="scss">
@import url('../package/theme/index.scss');
.djs-palette {
  background: var(--palette-background-color);
  border: solid 1px var(--palette-border-color) !important;
  border-radius: 2px;
}

.my-process-designer {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  .my-process-designer__header {
    width: 100%;
    min-height: 36px;
    .el-button {
      text-align: center;
    }
    .el-button-group {
      margin: 4px;
    }
    .el-tooltip__popper {
      .el-button {
        width: 100%;
        text-align: left;
        padding-left: 8px;
        padding-right: 8px;
      }
      .el-button:hover {
        background: rgba(64, 158, 255, 0.8);
        color: #ffffff;
      }
    }
    .align {
      position: relative;
      i {
        &:after {
          content: '|';
          position: absolute;
          left: 15px;
          transform: rotate(90deg) translate(200%, 60%);
        }
      }
    }
    .align.align-left i {
      transform: rotate(90deg);
    }
    .align.align-right i {
      transform: rotate(-90deg);
    }
    .align.align-top i {
      transform: rotate(180deg);
    }
    .align.align-bottom i {
      transform: rotate(0deg);
    }
    .align.align-center i {
      transform: rotate(90deg);
      &:after {
        transform: rotate(90deg) translate(0, 60%);
      }
    }
    .align.align-middle i {
      transform: rotate(0deg);
      &:after {
        transform: rotate(90deg) translate(0, 60%);
      }
    }
  }
  .my-process-designer__container {
    display: inline-flex;
    width: 100%;
    flex: 1;
    background-color: #f6f7f9;
    .my-process-designer__canvas {
      flex: 1;
      height: 100%;
      position: relative;
      background: url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PHBhdHRlcm4gaWQ9ImEiIHdpZHRoPSI0MCIgaGVpZ2h0PSI0MCIgcGF0dGVyblVuaXRzPSJ1c2VyU3BhY2VPblVzZSI+PHBhdGggZD0iTTAgMTBoNDBNMTAgMHY0ME0wIDIwaDQwTTIwIDB2NDBNMCAzMGg0ME0zMCAwdjQwIiBmaWxsPSJub25lIiBzdHJva2U9IiNlMGUwZTAiIG9wYWNpdHk9Ii4yIi8+PHBhdGggZD0iTTQwIDBIMHY0MCIgZmlsbD0ibm9uZSIgc3Ryb2tlPSIjZTBlMGUwIi8+PC9wYXR0ZXJuPjwvZGVmcz48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSJ1cmwoI2EpIi8+PC9zdmc+')
        repeat !important;
      div.toggle-mode {
        display: none;
      }
    }
    .my-process-designer__property-panel {
      height: 100%;
      overflow: scroll;
      overflow-y: auto;
      z-index: 10;
      * {
        box-sizing: border-box;
      }
    }
    svg {
      width: 100%;
      height: 100%;
      min-height: 100%;
      overflow: hidden;
    }
  }
}
.djs-palette.open {
  .djs-palette-entries {
    div[class^='bpmn-icon-']:before,
    div[class*='bpmn-icon-']:before {
      line-height: unset;
    }
    div.entry {
      position: relative;
    }
    div.entry:hover {
      &::after {
        width: max-content;
        content: attr(title);
        vertical-align: text-bottom;
        position: absolute;
        right: -10px;
        top: 0;
        bottom: 0;
        overflow: hidden;
        transform: translateX(100%);
        font-size: 0.5em;
        display: inline-block;
        text-decoration: inherit;
        font-variant: normal;
        text-transform: none;
        background: #fafafa;
        box-shadow: 0 0 6px #eeeeee;
        border: 1px solid #cccccc;
        box-sizing: border-box;
        padding: 0 16px;
        border-radius: 4px;
        z-index: 100;
      }
    }
  }
}
pre {
  margin: 0;
  height: 100%;
  overflow: hidden;
  max-height: calc(80vh - 32px);
  overflow-y: auto;
}
.hljs {
  word-break: break-word;
  white-space: pre-wrap;
}
.hljs * {
  font-family: Consolas, Monaco, monospace;
}
.djs-container {
  .djs-visual {
    rect,
    polygon,
    circle {
      stroke: #c1c2c4 !important;
      stroke-width: 1px !important;
    }
    circle[style*='stroke-width: 4px'] {
      stroke: #333333 !important;
    }
    path[style='fill: black; stroke-width: 1px; stroke: black;'] {
      fill: #333333 !important;
      stroke-width: 1px;
      stroke: #333333 !important;
    }
  }
  .djs-visual path {
    stroke: #333333 !important;
  }
  [id^='sequenceflow-end-white-black'] path {
    fill: #333333 !important;
    stroke: #333333 !important;
  }
  [id^='conditional-flow-marker-white-black'] path {
    stroke: #333333 !important;
  }
}
</style>
