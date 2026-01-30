<template>
  <el-form
    ref="form"
    :model="formData"
    :rules="rules"
    label-width="110px"
    :size="formItemSize"
    label-position="right"
    @submit.prevent
  >
    <el-row :gutter="20" class="full-width-input">
      <el-col :span="24">
        <el-form-item label="loginName" prop="loginName">
          <el-input
            v-model="formData.loginName"
            placeholder="loginName"
            clearable
            :disabled="isEdit"
            maxlength="30"
          />
        </el-form-item>
        <el-form-item label="password" v-if="!isEdit" prop="password">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="password"
            clearable
            maxlength="64"
          />
        </el-form-item>
        <el-form-item label="passwordRepeat" v-if="!isEdit" prop="passwordRepeat">
          <el-input
            v-model="formData.passwordRepeat"
            type="password"
            placeholder="passwordRepeat"
            clearable
            maxlength="64"
          />
        </el-form-item>
        <el-form-item label="showName" prop="showName">
          <el-input v-model="formData.showName" placeholder="showName" clearable maxlength="30" />
        </el-form-item>
        <el-form-item label="userType" prop="userType">
          <el-select v-model="formData.userType">
            <el-option
              v-for="item in SysUserType_pri.getList()"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="userStatus" prop="userStatus" v-if="isEdit">
          <el-radio-group v-model="formData.userStatus">
            <el-radio v-for="item in SysUserStatus.getList()" :key="item.id" :value="item.id">{{
              item.name
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="deptId" prop="deptId">
          <el-cascader
            class="input-item"
            v-model="deptIdPath"
            :clearable="true"
            placeholder="deptId"
            :loading="deptId.impl.loading"
            :props="{ value: 'deptId', label: 'deptName', checkStrictly: true }"
            @visible-change="onDeptIdVisibleChange"
            :options="deptId.impl.dropdownList"
            @change="onDeptIdValueChange"
          >
          </el-cascader>
        </el-form-item>
        <!--
        <el-form-item label="deptPostId" prop="deptPostIdList">
          <el-select v-model="formData.deptPostIdList" multiple placeholder="deptPostId">
            <el-option
              v-for="deptPost in deptPostList"
              :key="deptPost.deptPostId"
              :label="deptPost.postShowName"
              :value="deptPost.deptPostId"
            />
          </el-select>
        </el-form-item>
        -->
        <el-form-item label="roleId" prop="roleIdList">
          <el-select v-model="formData.roleIdList" multiple placeholder="roleId">
            <el-option
              v-for="role in roleList"
              :key="role.roleId"
              :label="role.roleName"
              :value="role.roleId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="dataPermId" prop="dataPermIdList">
          <el-select v-model="formData.dataPermIdList" multiple placeholder="dataPermId">
            <el-option
              v-for="dataPerm in dataPermList"
              :key="dataPerm.dataPermId"
              :label="dataPerm.dataPermName"
              :value="dataPerm.dataPermId"
            />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <!-- 弹窗下发按钮栏，必须设置class为dialog-btn-layer -->
    <el-row type="flex" justify="end" class="dialog-btn-layer">
      <el-button :plain="true" @click="onCancel">CANCEL</el-button>
      <el-button
        type="primary"
        @click="onSubmit"
        :disabled="
          !(
            checkPermCodeExist('formSysUser:fragmentSysUser:update') ||
            checkPermCodeExist('formSysUser:fragmentSysUser:add')
          )
        "
      >
        YES
      </el-button>
    </el-row>
  </el-form>
</template>

<script setup lang="ts">
import { Ref, computed, inject, onMounted, reactive, ref } from 'vue';
import { CascaderValue, ElCascader, ElMessage } from 'element-plus';
import { SysUserStatus, SysUserType, SysUserType_pri } from '@/common/staticDict/index';
import { findTreeNodePath } from '@/common/utils';
import { DialogProp } from '@/components/Dialog/types';
import { usePermissions } from '@/common/hooks/usePermission';
import {
  DictionaryController,
  SysDeptController,
  SystemUserController,
  SysDataPermController,
  SystemRoleController,
} from '@/api/system';
import { ANY_OBJECT } from '@/types/generic';
import { User } from '@/types/upms/user';
import { useDropdown } from '@/common/hooks/useDropdown';
import { DropdownOptions, ListData } from '@/common/types/list';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

const props = defineProps<{
  rowData?: User;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}>();
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});
const { checkPermCodeExist } = usePermissions();

const form = ref();
const formData: Ref<User> = ref({
  userType: 2,
  userStatus: 0,
  dataPermIdList: [],
  deptPostIdList: [],
  roleIdList: [],
});
const validatePasswordRepeat = (
  rule: ANY_OBJECT,
  value: string,
  callback: (error?: Error) => void,
) => {
  //console.log(rule, value, callback);
  if (!value) {
    callback(new Error('The retyped password cannot be empty!'));
  } else if (value != formData.value.password) {
    callback(new Error('The two passwords entered do not match. Please enter them again!'));
  } else {
    callback();
  }
};
const rules = ref({
  loginName: [{ required: true, message: 'Username cannot be empty', trigger: 'blur' }],
  password: [{ required: true, message: 'Password cannot be empty', trigger: 'blur' }],
  passwordRepeat: [
    {
      required: true,
      validator: validatePasswordRepeat,
      trigger: 'blur',
    },
  ],
  showName: [{ required: true, message: 'ShowName cannot be empty', trigger: 'blur' }],
  userType: [{ required: true, message: 'UserType cannot be empty', trigger: 'change' }],
  deptId: [{ required: true, message: 'DeptId cannot be empty', trigger: 'change' }],
  dataPermIdList: [{ required: true, message: 'DataPermId cannot be empty', trigger: 'change' }],
  /*
  deptPostIdList: [{ required: true, message: 'DeptPostId cannot be empty', trigger: 'change' }],
  */
  roleIdList: [{ required: true, message: 'RoleId cannot be empty', trigger: 'change' }],
});
const deptIdPath = ref<CascaderValue | undefined>([]);
const dataPermList = ref<ANY_OBJECT>([]);
const deptPostList = ref<ANY_OBJECT[]>();
const roleList = ref<ANY_OBJECT>([]);

const isEdit = computed(() => {
  return formData.value.userId != null;
});
/**
 * 所属部门下拉数据获取函数
 */
const loadDeptmentDropdownList = (): Promise<ListData<ANY_OBJECT>> => {
  return new Promise((resolve, reject) => {
    let params = {};
    SysDeptController.list(params)
      .then(res => {
        resolve({
          dataList: res.data.dataList,
        });
      })
      .catch(e => {
        reject(e);
      });
  });
};

const dropdownOptions: DropdownOptions<ANY_OBJECT> = {
  loadData: loadDeptmentDropdownList,
  idKey: 'deptId',
  isTree: true,
};

const deptId = reactive({
  impl: useDropdown(dropdownOptions),
  value: [],
});
/**
 * 所属部门下拉框显隐
 */
const onDeptIdVisibleChange = (show: boolean) => {
  deptId.impl.onVisibleChange(show).catch(e => {
    console.warn(e);
  });
};
/**
 * 所属部门选中值改变
 */
const onDeptIdValueChange = (value: CascaderValue) => {
  formData.value.deptId = Array.isArray(value) ? value[value.length - 1].toString() : undefined;
  formData.value.deptPostIdList = undefined;
  loadDeptPost();
};
/**
 * 获取部门岗位列表
 */
const loadDeptPost = () => {
  if (formData.value.deptId == null || formData.value.deptId === '') {
    deptPostList.value = [];
    return;
  }
  DictionaryController.dictDeptPost({
    deptId: formData.value.deptId,
  })
    .then(res => {
      console.log('dictDeptPost', res);
      deptPostList.value = res;
    })
    .catch(e => {
      console.warn(e);
    });
};
const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  }
};
const onSubmit = () => {
  form.value.validate((valid: boolean) => {
    if (valid) {
      let params = {
        sysUserDto: {
          userId: formData.value.userId,
          loginName: formData.value.loginName,
          password: formData.value.password,
          showName: formData.value.showName,
          userType: formData.value.userType,
          deptId: formData.value.deptId,
          userStatus: formData.value.userStatus,
        },
        dataPermIdListString: Array.isArray(formData.value.dataPermIdList)
          ? formData.value.dataPermIdList.join(',')
          : undefined,
        deptPostIdListString: Array.isArray(formData.value.deptPostIdList)
          ? formData.value.deptPostIdList.join(',')
          : undefined,
        roleIdListString: Array.isArray(formData.value.roleIdList)
          ? formData.value.roleIdList.join(',')
          : undefined,
      };

      let operation: Promise<ANY_OBJECT>;
      if (formData.value.userId != null) {
        operation = SystemUserController.updateUser(params);
      } else {
        operation = SystemUserController.addUser(params);
      }

      operation
        .then(res => {
          ElMessage.success(formData.value.userId != null ? '编辑成功' : '添加成功');
          props.dialog.submit(res);
        })
        .catch(e => {
          console.warn(e);
        });
    }
  });
};
const loadRole = () => {
  SystemRoleController.getRoleList({})
    .then(res => {
      roleList.value = res.data.dataList;
    })
    .catch(e => {
      console.warn(e);
    });
};
const loadDataPerm = () => {
  SysDataPermController.list({})
    .then(res => {
      dataPermList.value = res.data.dataList;
    })
    .catch(e => {
      console.warn(e);
    });
};
onMounted(() => {
  if (props.rowData != null) {
    formData.value = {
      ...props.rowData,
      dataPermIdList: [],
      deptPostIdList: [],
      roleIdList: [],
    };
    if (Array.isArray(formData.value.sysDataPermUserList)) {
      formData.value.dataPermIdList = formData.value.sysDataPermUserList.map(
        item => item.dataPermId,
      );
    }
    if (Array.isArray(formData.value.sysUserPostList)) {
      formData.value.deptPostIdList = formData.value.sysUserPostList.map(item => item.deptPostId);
    }
    if (Array.isArray(formData.value.sysUserRoleList)) {
      formData.value.roleIdList = formData.value.sysUserRoleList.map(item => item.roleId);
    }
  }
  deptId.impl.onVisibleChange(true).then(() => {
    deptIdPath.value = formData.value.deptId
      ? findTreeNodePath(deptId.impl.dropdownList, formData.value.deptId, 'deptId')
      : [];
  });
  loadRole();
  loadDataPerm();
  loadDeptPost();
});
</script>
