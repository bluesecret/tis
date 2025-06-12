<template>
  <el-form
    ref="form"
    :model="formData"
    :rules="rules"
    label-width="120px"
    :size="formItemSize"
    label-position="right"
    @submit.prevent
  >
    <el-row :gutter="20" class="full-width-input">
      <el-col :span="24">
        <el-form-item label="Device No." prop="serNo">
          <el-input v-model="formData.serNo" placeholder="Device No." clearable maxlength="30" />
        </el-form-item>
        <el-form-item label="Device Name" prop="deviceName">
          <el-input v-model="formData.deviceName" placeholder="Device Name" clearable maxlength="30" />
        </el-form-item>
      </el-col>
    </el-row>
    <!-- 弹窗下发按钮栏，必须设置class为dialog-btn-layer -->
    <el-row type="flex" justify="end" class="dialog-btn-layer">
      <el-button :plain="true" @click="onCancel"> Cancel </el-button>
      <el-button type="primary" @click="onSubmit"> OK </el-button>
    </el-row>
  </el-form>
</template>

<script setup lang="ts">
import { Ref, computed, inject, onMounted, reactive, ref } from 'vue';
import { CascaderValue, ElCascader, ElMessage } from 'element-plus';
import { SysUserStatus, SysUserType } from '@/common/staticDict/index';
import { findTreeNodePath } from '@/common/utils';
import { DialogProp } from '@/components/Dialog/types';
import { usePermissions } from '@/common/hooks/usePermission';
import { TisDeviceInfoController } from '@/api/generated';
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
const formData: Ref<any> = ref({
  serNo: '',
  deviceName: '',
});
const validatePasswordRepeat = (
  rule: ANY_OBJECT,
  value: string,
  callback: (error?: Error) => void,
) => {
  //console.log(rule, value, callback);
  if (!value) {
    callback(new Error('重输密码不能为空'));
  } else if (value != formData.value.password) {
    callback(new Error('两次密码输入不一致，请重新输入'));
  } else {
    callback();
  }
};
const rules = ref({
  serNo: [{ required: true, message: 'The device number cannot be empty', trigger: 'change' }],
  deviceName: [{ required: true, message: 'The device number cannot be empty', trigger: 'change' }],
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
        tisDeviceInfoDto: {
          serNo: formData.value.serNo,
          deviceName: formData.value.deviceName,
        },
      };

      let operation: Promise<ANY_OBJECT>;
      if (formData.value.id != null) {
        params.tisDeviceInfoDto.id = formData.value.id;
        operation = TisDeviceInfoController.update(params);
      } else {
        operation = TisDeviceInfoController.add(params);
      }

      operation
        .then(res => {
          ElMessage.success(formData.value.id != null ? '编辑成功' : '添加成功');
          props.dialog.submit(res);
        })
        .catch(e => {
          console.warn(e);
        });
    }
  });
};

onMounted(() => {
  if (props.rowData != null) {
    console.log(props);
    formData.value = Object.assign({}, props.rowData);
  }
});
</script>
