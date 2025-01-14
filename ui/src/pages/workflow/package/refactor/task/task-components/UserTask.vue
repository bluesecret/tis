<template>
  <div>
    <el-form-item label="候选类型">
      <el-radio-group v-model="formData.groupType" @change="onGroupTypeChange">
        <el-radio label="ASSIGNEE" value="ASSIGNEE" :disabled="isCountersign">
          处理用户
        </el-radio>
        <el-radio label="USERS" value="USERS">候选用户组</el-radio>
        <el-radio label="ROLE" value="ROLE">角色</el-radio>
        <el-radio label="DEPT" value="DEPT">部门</el-radio>
        <el-radio label="POST" value="POST">岗位</el-radio>
        <el-radio label="DEPT_POST_LEADER" value="DEPT_POST_LEADER">发起人部门领导</el-radio>
        <el-radio label="UP_DEPT_POST_LEADER" value="UP_DEPT_POST_LEADER">发起人上级部门领导</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="" v-if="formData.groupType === 'ASSIGNEE'">
      <TaskMultipleSelect v-model:value="userName" style="width: 100%">
        <el-button
          :icon="Plus"
          :size="layoutStore.defaultFormItemSize"
          @click="onSelectAssignee"
          >添加处理用户</el-button
        >
      </TaskMultipleSelect>
    </el-form-item>
    <el-form-item label="" v-if="formData.groupType === 'USERS'">
      <TaskMultipleSelect v-model:value="userName" style="width: 100%">
        <el-button
          :icon="Plus"
          :size="layoutStore.defaultFormItemSize"
          @click="onSelectCandidateUsers"
          >添加候选用户</el-button
        >
      </TaskMultipleSelect>
    </el-form-item>
    <el-form-item v-if="formData.groupType === 'ROLE'" label="候选角色">
      <el-select
        v-model="candidateGroupIds"
        placeholder=""
        :multiple="true"
        @change="onSelectRoleChange"
      >
        <el-option v-for="role in roleList" :key="role.id" :label="role.name" :value="role.id" />
      </el-select>
    </el-form-item>
    <el-form-item v-if="formData.groupType == 'DEPT' || formData.groupType == 'POST'" label="">
      <TaskMultipleSelect v-model:value="candidateGroupIds" style="width: 100%">
        <el-button
          :icon="Plus"
          :size="layoutStore.defaultFormItemSize"
          @click="onSelectCandidatGroups"
          >添加{{ formData.groupType === 'DEPT' ? '候选部门' : '候选岗位' }}</el-button
        >
      </TaskMultipleSelect>
    </el-form-item>
    <!--
    <el-form-item label="到期时间">
      <el-input v-model="userTaskForm.dueDate" clearable @change="updateElementTask('dueDate')" />
    </el-form-item>
    <el-form-item label="跟踪时间">
      <el-input v-model="userTaskForm.followUpDate" clearable @change="updateElementTask('followUpDate')" />
    </el-form-item>
    <el-form-item label="优先级">
      <el-input v-model="userTaskForm.priority" clearable @change="updateElementTask('priority')" />
      <div class="number-box">
        <el-input-number v-model="userTaskForm.priority" :controls="false" @change="updateElementTask('priority')" :min="1" :max="999" style="width: 105px">
        </el-input-number>
        <div class="icon icon-plus-wrap" @click="userTaskForm.priority = userTaskForm.priority*1+1">
          <i class="el-icon-plus"></i>
        </div>
        <div class="icon icon-minus-wrap" @click="userTaskForm.priority = userTaskForm.priority*1-1">
          <i class="el-icon-minus"></i>
        </div>
      </div>
    </el-form-item>
    -->
  </div>
</template>

<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue';
import { ANY_OBJECT } from '@/types/generic';
import TaskUserSelect from '@/pages/workflow/components/TaskUserSelect.vue';
import TaskMultipleSelect from '@/pages/workflow/components/TaskMultipleSelect.vue';
import TaskGroupSelect from '@/pages/workflow/components/TaskGroupSelect.vue';
import TaskPostSelect from '@/pages/workflow/components/TaskPostSelect.vue';
import { findItemFromList, treeDataTranslate } from '@/common/utils';
import { SysPostController, DictionaryController, SysCommonBizController } from '@/api/system';
import { useOtherStore } from '@/store';
import { Dialog } from '@/components/Dialog';
import { SysFlowEntryBindFormType } from '@/common/staticDict/flow';

const props = defineProps<{ id: string; type: string; }>();
const flowEntry = inject('flowEntry', () => {
  console.warn('UserTask not inject flowEntry yet.');
  return {} as ANY_OBJECT;
});
const prefix = inject('prefix');
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

const isCountersign = ref(false);
const candidateGroupIds = ref<ANY_OBJECT[]>([]);
const sendMessageType = ref<string[]>([]);
const defaultTaskForm: ANY_OBJECT = {
  assignee: '',
  candidateUsers: '',
  candidateGroups: '',
  dueDate: '',
  followUpDate: '',
  priority: '',
  sendMessageType: '',
};
const formData = ref<ANY_OBJECT>({
  groupType: 'ASSIGNEE',
});
const roleList = ref<ANY_OBJECT[]>();
const groupList = ref<ANY_OBJECT[]>();
let groupMap: Map<string, ANY_OBJECT> | null = new Map();
const postList = ref<ANY_OBJECT[]>([]);
const deptPostList = ref<ANY_OBJECT[]>([]);
let postMap: Map<string, ANY_OBJECT> | null = new Map();
let deptPostMap: Map<string, ANY_OBJECT> | null = new Map();
let userTaskForm = ref<ANY_OBJECT>({
  assignee: '',
  candidateUsers: '',
  candidateGroups: '',
  dueDate: '',
  followUpDate: '',
  priority: '',
  sendMessageType: '',
});
const userName = ref<ANY_OBJECT[]>([]);
const isThird = true;

const otherStore = useOtherStore();
const win: ANY_OBJECT = window;

const calcUserName = (userInfo: ANY_OBJECT) => {
  if (userInfo == null || userInfo.loginName == null) return;
  if (userInfo.loginName === '${startUserName}') userInfo.showName = '流程发起人';
  if (userInfo.loginName === '${appointedAssignee}') userInfo.showName = '指定审批人';
  if (userInfo.loginName === '${assignee}') userInfo.showName = '会签人';
};
const updateAssignee = (res: ANY_OBJECT) => {
  calcUserName(res);
  userName.value = res
    ? [
        {
          id: res.loginName,
          name: res.showName || res.loginName,
        },
      ]
    : [];
};
const onSelectAssignee = () => {
  Dialog.show<ANY_OBJECT>(
    '选择用户',
    TaskUserSelect,
    {
      area: ['1000px', '650px'],
    },
    {
      multiple: false,
      path: 'thirdTaskUserSelect',
    },
    {
      width: '1000px',
      height: '650px',
      pathName: '/thirdParty/thirdTaskUserSelect',
    },
  )
    .then(res => {
      updateAssignee(res);
    })
    .catch(e => {
      console.error(e);
    });
};
const updateCandidateUsers = (res: ANY_OBJECT) => {
  let tempList = res;
  if (res && !Array.isArray(res)) {
    tempList = [res];
  }
  if (Array.isArray(tempList)) {
    let oldUserList = userName.value.map(item => item.id);
    tempList.forEach(item => {
      calcUserName(item);
      if (oldUserList.indexOf(item.loginName) === -1) {
        userName.value.push({
          id: item.loginName,
          name: item.showName || item.loginName,
        });
      }
    });
  }
};
const onSelectCandidateUsers = () => {
  let usedUserIdList =
    userTaskForm.value.candidateUsers == null || userTaskForm.value.candidateUsers === ''
      ? []
      : userTaskForm.value.candidateUsers.split(',');
  Dialog.show<ANY_OBJECT[]>(
    '选择候选用户',
    TaskUserSelect,
    {
      area: ['1000px', '650px'],
    },
    {
      multiple: true,
      usedUserIdList: usedUserIdList,
      path: 'thirdTaskUserSelectCandidateUsers',
    },
    {
      width: '1000px',
      height: '650px',
      pathName: '/thirdParty/thirdTaskUserSelect',
    },
  )
    .then(res => {
      updateCandidateUsers(res);
    })
    .catch(e => {
      console.warn(e);
    });
};

const loadSysRoleList = () => {
  return new Promise((resolve, reject) => {
    if (isThird) {
      let params = {
        widgetType: 'upms_role',
        filter: {},
      };
      SysCommonBizController.list(params, {
        showMask: false,
      })
        .then(res => {
          roleList.value = res.data.dataList.map(item => {
            return {
              id: String(item.roleId),
              name: item.roleName,
              ...item,
            };
          });
          resolve(res);
        })
        .catch(e => {
          reject(e);
        });
    } else {
      DictionaryController.dictSysRole({})
        .then(res => {
          roleList.value = res.getList();
          resolve(res);
        })
        .catch(e => {
          reject(e);
        });
    }
  });
};
const loadDeptWidgetDropdownList = () => {
  return new Promise((resolve, reject) => {
    if (isThird) {
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
            groupMap?.set(obj.id + '', obj);
            list.push(obj);
          });
          groupList.value = treeDataTranslate(list);
          resolve(res);
        })
        .catch(e => {
          reject(e);
        });
    } else {
      DictionaryController.dictSysDept({})
        .then(res => {
          res.getList().forEach(item => {
            groupMap?.set(item.id + '', item);
          });
          groupList.value = treeDataTranslate(res.getList());
          resolve(res);
        })
        .catch(e => {
          reject(e);
        });
    }
  });
};
const loadDeptPostList = () => {
  return new Promise((resolve, reject) => {
    if (isThird) {
      let params = {
        widgetType: 'upms_dept_post',
        filter: {},
      };
      SysCommonBizController.list(params, {
        showMask: false,
      })
        .then(res => {
          res.data.dataList.forEach(item => {
            deptPostMap?.set(item.deptPostId + '', item);
          });
          deptPostList.value = res.data.dataList;
          resolve(res);
        })
        .catch(e => {
          reject(e);
        });
    } else {
      DictionaryController.dictDeptPost({})
        .then(res => {
          res.forEach(item => {
            deptPostMap?.set(item.deptPostId + '', item);
          });
          deptPostList.value = res.sort((value1, value2) => {
            return value1.postLevel - value2.postLevel;
          });
          resolve(res);
        })
        .catch(e => {
          reject(e);
        });
    }
  });
};
const loadSysPostList = () => {
  postMap = new Map();
  return new Promise((resolve, reject) => {
    if (isThird) {
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
            postMap?.set(obj.id + '', obj);
          });
          postList.value = res.data.dataList;
          resolve(res);
        })
        .catch(e => {
          reject(e);
        });
    } else {
      SysPostController.list({})
        .then(res => {
          postList.value = res.data.dataList;
          postList.value.forEach(item => {
            postMap?.set(item.postId + '', item);
          });
          resolve(res);
        })
        .catch(e => {
          reject(e);
        });
    }
  });
};
const handlerDeptChange = (usedIdList: string[]) => {
  Dialog.show<ANY_OBJECT>(
    '选择部门',
    TaskGroupSelect,
    {
      area: ['600px', '600px'],
    },
    {
      allGroupList: groupList.value,
      usedIdList: usedIdList,
      path: 'thirdTaskGroupSelect',
    },
    {
      width: '600px',
      height: '630px',
      pathName: '/thirdParty/thirdTaskGroupSelect',
    },
  )
    .then(res => {
      updateDept(res);
    })
    .catch(e => {
      console.warn(e);
    });
};
const updateDept = (res: ANY_OBJECT | ANY_OBJECT[]) => {
  if (Array.isArray(res)) {
    if (!Array.isArray(candidateGroupIds.value)) candidateGroupIds.value = [];
    res.forEach(item => {
      if (findItemFromList(candidateGroupIds.value, item.id, 'id') == null) {
        candidateGroupIds.value.push(item);
      }
    });
  } else {
    candidateGroupIds.value.push({
      id: res.deptId,
      name: res.deptName,
    });
  }
  userTaskForm.value.candidateGroups = Array.isArray(candidateGroupIds.value)
    ? candidateGroupIds.value.map(item => item.id).join(',')
    : '';
};
const handlerPostChange = (usedIdList: string[]) => {
  Dialog.show<ANY_OBJECT>(
    '选择岗位',
    TaskPostSelect,
    {
      area: ['1000px', '615px'],
      skin: 'layer-advance-dialog',
    },
    {
      deptList: groupList.value,
      deptPostList: deptPostList.value,
      postList: postList.value,
      usedIdList: usedIdList,
      path: 'thirdTaskPostSelect',
    },
    {
      width: '1000px',
      height: '640px',
      pathName: '/thirdParty/thirdTaskPostSelect',
    },
  )
    .then(res => {
      updatePost(res);
    })
    .catch(e => {
      console.warn(e);
    });
};
const updateDeptPost = () => {
  // 岗位
  let elExtensionElements =
    win.bpmnInstances.bpmnElement.businessObject.get('extensionElements') ||
    win.bpmnInstances.moddle.create('bpmn:ExtensionElements', { values: [] });
  let otherExtensions = elExtensionElements.values.filter(
    (ex: ANY_OBJECT) => ex.$type !== `${prefix}:DeptPostList`,
  );
  let deptPostListElement: ANY_OBJECT[] | ANY_OBJECT | null = null;
  if (formData.value.groupType === 'POST') {
    deptPostListElement = win.bpmnInstances.moddle.create(`${prefix}:DeptPostList`, {
      deptPostList: [],
    });
    if (deptPostListElement && !Array.isArray(deptPostListElement)) {
      deptPostListElement.deptPostList = candidateGroupIds.value.map(item => {
        return win.bpmnInstances.moddle.create(`${prefix}:DeptPost`, {
          id: item.id,
          type: item.deptType,
          postId: item.postId,
          deptPostId: item.deptPostId,
        });
      });
    }
  } else {
    deptPostListElement = [];
  }
  const newElExtensionElements = win.bpmnInstances.moddle.create(`bpmn:ExtensionElements`, {
    values: otherExtensions.concat(deptPostListElement),
  });
  // 更新到元素上
  win.bpmnInstances.modeling.updateProperties(win.bpmnInstances.bpmnElement, {
    extensionElements: newElExtensionElements,
  });
};
const updatePost = (res: ANY_OBJECT) => {
  userTaskForm.value.candidateGroups = '';
  if (Array.isArray(res)) {
    if (!Array.isArray(candidateGroupIds.value)) candidateGroupIds.value = [];
    res.forEach(item => {
      let temp = getDeptPostItem(item);
      if (findItemFromList(candidateGroupIds.value, item.id, 'id') == null) {
        candidateGroupIds.value.push({
          ...item,
          name: temp.deptName ? `${temp.deptName} / ${temp.postName}` : temp.postName,
        });
      }
    });
    updateDeptPost();
  }
  userTaskForm.value.candidateGroups = Array.isArray(candidateGroupIds.value)
    ? candidateGroupIds.value.map(item => item.id).join(',')
    : '';
};
const getDeptPostItem = (item: ANY_OBJECT) => {
  let deptName;
  switch (item.deptType) {
    case 'allDeptPost':
      deptName = '全部';
      break;
    case 'selfDeptPost':
      deptName = '本部门';
      break;
    case 'siblingDeptPost':
      deptName = '同级部门';
      break;
    case 'upDeptPost':
      deptName = '上级部门';
      break;
    case 'deptPost':
      deptName = (deptPostMap?.get(item.deptPostId) || {}).deptName || '未知岗位';
      break;
  }
  let postName =
    item.deptType === 'deptPost'
      ? (deptPostMap?.get(item.deptPostId) || {}).postShowName || '未知岗位'
      : (postMap?.get(String(item.postId)) || {}).postName || '未知岗位';

  return {
    deptName,
    postName,
  };
};
const onSelectCandidatGroups = () => {
  let usedIdList = userTaskForm.value.candidateGroups
    ? userTaskForm.value.candidateGroups.split(',')
    : [];
  if (formData.value.groupType === 'DEPT') {
    handlerDeptChange(usedIdList);
  } else {
    handlerPostChange(usedIdList);
  }
};
const onSelectRoleChange = (value: string[] | string) => {
  nextTick(() => {
    userTaskForm.value.candidateGroups = Array.isArray(value) ? value.join(',') : '';
  });
};
// 获取审批人信息
const loadUserInfo = (params: ANY_OBJECT) => {
  return new Promise<ANY_OBJECT[]>((resolve, reject) => {
    if (params == null || params.fieldValues === '') {
      resolve([]);
    } else {
      SysCommonBizController.viewByIds(params)
        .then(res => {
          resolve(res.data || []);
        })
        .catch(e => {
          reject(e);
        });
    }
  });
};
let bpmnElement: ANY_OBJECT = {};
const resetTaskForm = () => {
  userTaskForm.value = {
    assignee: '',
    candidateUsers: '',
    candidateGroups: '',
    dueDate: '',
    followUpDate: '',
    priority: '',
    sendMessageType: '',
  };
  candidateGroupIds.value = [];
  sendMessageType.value = [];
  bpmnElement = win.bpmnInstances.bpmnElement;
  let formKey = bpmnElement.businessObject.formKey;
  let formObj = formKey ? JSON.parse(formKey) : undefined;
  if (formObj) {
    formData.value = {
      formId: formObj.formId,
      routerName: formObj.routerName,
      editable: !formObj.readOnly,
      formAuth: formObj.formAuth || {},
      groupType: formObj.groupType || (isCountersign.value ? 'USERS' : 'ASSIGNEE'),
    };
  } else {
    formData.value = {
      groupType: isCountersign.value ? 'USERS' : 'ASSIGNEE',
    };
  }
  if (isCountersign.value && formData.value.groupType === 'ASSIGNEE') {
    formData.value.groupType = 'USERS';
    userName.value = [];
  }
  for (let key in defaultTaskForm) {
    let value;
    if (key === 'sendMessageType') {
      value = (win.bpmnInstances.bpmnElement || {}).businessObject[key] || defaultTaskForm[key];
      sendMessageType.value = value != null && value !== '' ? value.split(',') : [];
    }
    if (key === 'candidateUsers' || key === 'candidateGroups') {
      value = (win.bpmnInstances.bpmnElement || {}).businessObject[key] || defaultTaskForm[key];
      if (key === 'candidateGroups' && value) {
        candidateGroupIds.value = value.split(',');
        if (Array.isArray(candidateGroupIds.value) && formData.value.groupType === 'DEPT') {
          candidateGroupIds.value = candidateGroupIds.value
            .map(item => {
              return groupMap?.get(item.toString());
            })
            .filter(item => item != null) as ANY_OBJECT[];
        }
      }
    } else {
      value = (win.bpmnInstances.bpmnElement || {}).businessObject[key] || defaultTaskForm[key];
    }
    userTaskForm.value[key] = value;
  }

  let params = {
    widgetType: 'upms_user',
    fieldName: 'loginName',
    fieldValues: '',
  };

  if (formData.value.groupType === 'ASSIGNEE') {
    params.fieldValues = userTaskForm.value.assignee;
  } else if (formData.value.groupType === 'USERS') {
    params.fieldValues = userTaskForm.value.candidateUsers;
  }
  let oldUser: string[] = params.fieldValues.split(',');
  // 去掉流程启动人和指定审批人
  params.fieldValues = params.fieldValues
    .split(',')
    .filter(row => {
      return ['${startUserName}', '${appointedAssignee}', '${assignee}'].indexOf(row) === -1;
    })
    .join(',');
  // 获取处理人用户信息
  loadUserInfo(params)
    .then(dataList => {
      userName.value = oldUser
        .map(item => {
          let tempData = null;
          if (
            item === '${startUserName}' ||
            item === '${appointedAssignee}' ||
            item === '${assignee}'
          ) {
            tempData = {
              loginName: item,
            };
          } else {
            tempData = findItemFromList(dataList, item, 'loginName');
          }
          if (tempData != null) {
            calcUserName(tempData);
            return {
              id: tempData.loginName,
              name: tempData.showName,
            } as ANY_OBJECT;
          } else {
            if (item != null && item !== '') {
              return {
                id: item,
                name: item,
              } as ANY_OBJECT;
            } else {
              return null;
            }
          }
        })
        .filter(item => item != null) as ANY_OBJECT[];
    })
    .catch(e => {
      console.log(e);
    });
  // 岗位
  if (formData.value.groupType === 'POST') {
    let elExtensionElements =
      win.bpmnInstances.bpmnElement.businessObject.get('extensionElements') ||
      win.bpmnInstances.moddle.create('bpmn:ExtensionElements', { values: [] });
    const deptPostListElement =
      elExtensionElements.values.filter(
        (ex: ANY_OBJECT) => ex.$type === `${prefix}:DeptPostList`,
      )?.[0] || win.bpmnInstances.moddle.create(`${prefix}:DeptPostList`, { deptPostList: [] });
    candidateGroupIds.value = deptPostListElement.deptPostList.map((item: ANY_OBJECT) => {
      item.deptType = item.type;
      item.type = undefined;
      let temp = getDeptPostItem({
        ...item,
      });
      if (temp) {
        return {
          ...item,
          name: temp.deptName ? `${temp.deptName} / ${temp.postName}` : temp.postName,
        };
      }
    });
  }
};
const updateFormKey = () => {
  if (formData.value == null) return;
  let formKeyString = JSON.stringify({
    formId:
      flowEntry().value.bindFormType === SysFlowEntryBindFormType.ONLINE_FORM
        ? formData.value.formId
        : undefined,
    routerName:
      flowEntry().value.bindFormType === SysFlowEntryBindFormType.ONLINE_FORM
        ? undefined
        : formData.value.routerName,
    readOnly: !formData.value.editable,
    groupType: formData.value.groupType || 'ASSIGNEE',
    formAuth: formData.value.formAuth,
  });
  win.bpmnInstances.modeling.updateProperties(win.bpmnInstances.bpmnElement, {
    formKey: formKeyString,
  });
};
const onGroupTypeChange = (val: string) => {
  userName.value = [];
  userTaskForm.value.assignee = undefined;
  userTaskForm.value.candidateUsers = undefined;
  candidateGroupIds.value = [];
  userTaskForm.value.candidateGroups = '';
  updateFormKey();
  updateDeptPost();
};
const updateUserCandidateGroups = (type: string, value: string) => {
  let elExtensionElements =
    win.bpmnInstances.bpmnElement.businessObject.get('extensionElements') ||
    win.bpmnInstances.moddle.create('bpmn:ExtensionElements', { values: [] });
  let otherExtensions = elExtensionElements.values.filter(
    (ex: ANY_OBJECT) => ex.$type !== `${prefix}:UserCandidateGroups`,
  );
  let userCandidateGroupsElement = win.bpmnInstances.moddle.create(
    `${prefix}:UserCandidateGroups`,
    {
      type: type,
      value: value,
    },
  );
  if (type !== 'POST' && value != null && value !== '')
    otherExtensions.push(userCandidateGroupsElement);
  const newElExtensionElements = win.bpmnInstances.moddle.create(`bpmn:ExtensionElements`, {
    values: otherExtensions,
  });
  // 更新到元素上
  win.bpmnInstances.modeling.updateProperties(win.bpmnInstances.bpmnElement, {
    extensionElements: newElExtensionElements,
  });
};
const updateElementTask = (key: string) => {
  let taskAttr = Object.create(null);
  if (key === 'candidateUsers' || key === 'candidateGroups') {
    taskAttr[key] = userTaskForm.value[key] || null;
    let type = key === 'candidateUsers' ? 'USERS' : formData.value.groupType;
    updateUserCandidateGroups(type, taskAttr[key]);
  } else if (key === 'sendMessageType') {
    taskAttr[key] = sendMessageType.value.join(',');
  } else {
    taskAttr[key] = userTaskForm.value[key] || null;
  }
  if (isCountersign.value) {
    // eslint-disable-next-line no-template-curly-in-string
    taskAttr.assignee = '${assignee}';
  }
  win.bpmnInstances.modeling.updateProperties(win.bpmnInstances.bpmnElement, taskAttr);
};

onBeforeUnmount(() => {
  groupMap = null;
  postMap = null;
  deptPostMap = null;
});

watch(
  () => props.id,
  () => {
    isCountersign.value = win.bpmnInstances.bpmnElement?.businessObject?.loopCharacteristics != null;
    if (roleList.value == null) {
      loadSysRoleList()
        .then(() => {
          let httpCall = [
            loadDeptWidgetDropdownList(),
            loadSysPostList(),
            loadDeptPostList()
          ];
          return Promise.all(httpCall);
        })
        .then(() => {
          nextTick(() => resetTaskForm());
        })
        .catch(() => {
          roleList.value = undefined;
        });
    } else {
      nextTick(() => resetTaskForm());
    }
  },
  {
    immediate: true,
  },
);
watch(
  () => isCountersign.value,
  isCountersign => {
    // 会签不能选择处理人
    if (isCountersign && formData.value.groupType === 'ASSIGNEE') {
      formData.value.groupType = 'USERS';
      userName.value = [];
    }
  },
);
watch(
  () => userTaskForm.value.assignee,
  () => {
    console.log('userTaskForm.assignee changed');
    updateElementTask('assignee');
  },
);
watch(
  () => userTaskForm.value.candidateUsers,
  () => {
    console.log('userTaskForm.candidateUsers changed');
    updateElementTask('candidateUsers');
  },
);
watch(
  () => userTaskForm.value.candidateGroups,
  () => {
    console.log('userTaskForm.candidateGroups changed');
    updateElementTask('candidateGroups');
  },
);
watch(
  userName,
  () => {
    let usedUserIdList = (userName.value || []).map(item => item.id);
    let userShowNameData = { ...otherStore.getUserShowNameData };
    otherStore.setUserShowNameData(userShowNameData);
    if (formData.value.groupType === 'ASSIGNEE') {
      userTaskForm.value.assignee = usedUserIdList.join(',');
    } else if (formData.value.groupType === 'USERS') {
      userTaskForm.value.candidateUsers = usedUserIdList.join(',');
    }
  },
  {
    deep: true,
  },
);
watch(candidateGroupIds, () => {
  if (formData.value.groupType === 'ROLE') {
    userTaskForm.value.candidateGroups = Array.isArray(candidateGroupIds.value)
      ? candidateGroupIds.value.join(',')
      : '';
  } else if (formData.value.groupType === 'POST') {
    userTaskForm.value.candidateGroups = Array.isArray(candidateGroupIds.value)
      ? candidateGroupIds.value.map(item => item.id).join(',')
      : '';
    updateDeptPost();
  } else {
    userTaskForm.value.candidateGroups = Array.isArray(candidateGroupIds.value)
      ? candidateGroupIds.value.map(item => item.id).join(',')
      : '';
  }
});
</script>

<style lang="scss" scoped>
.append-add {
  background: #f5f7fa;
  border: none;
  border-radius: 0;
  border-left: 1px solid #dcdfe6;
}
.el-radio-group {
  padding-top: 4px;
  :deep(.el-radio) {
    width: 90px;
    margin-bottom: 1px;
    &:last-child {
      margin-bottom: 0;
    }
    .el-radio__inner {
      width: 16px;
      height: 16px;
    }
    .el-radio__label {
      padding-left: 4px;
      font-size: 14px;
      color: #333;
      font-weight: normal;
    }
    &.is-checked .el-radio__label {
      color: $color-primary;
    }
    .el-radio__inner::after {
      width: 8px;
      height: 8px;
    }
  }
}
:deep(.el-input-number input) {
  padding-right: 32px !important;
}
.number-box {
  position: relative;
  display: inline-block;
  float: right;
  i {
    font-size: 14px;
    transform: scale(0.8);
  }
  & > .icon {
    position: absolute;
    right: 2px;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 16px;
    height: 16px;
    cursor: pointer;
    box-sizing: border-box;
    border-left: 1px solid #d4d6d9;
    &:active {
      background-color: rgb(0 0 0 / 10%);
    }
  }
  .icon-plus-wrap {
    top: 1px;
  }
  .icon-minus-wrap {
    bottom: 0;
    border-top: 1px solid #d4d6d9;
  }
}
</style>
