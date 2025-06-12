<template>
  <el-form
    ref="form"
    :model="formData"
    :rules="rules"
    label-width="80px"
    :size="formItemSize"
    label-position="right"
    @submit.prevent
  >
    <el-row :gutter="20">
      <el-col :span="24">
        <!-- <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="角色名称" clearable maxlength="30" />
        </el-form-item> -->
        <!-- <el-form-item v-if="false" label="角色类型" :required="true">
          <el-radio-group v-model="formData.adminRole">
            <el-radio :value="1">管理员</el-radio>
            <el-radio :value="0">其他</el-radio>
          </el-radio-group>
        </el-form-item> -->
        <el-card shadow="never">
          <template v-slot:header>
            <div class="card-header">
              <span>设备编号</span>
              <el-input
                :size="formItemSize"
                v-model="menuNameFilter"
                placeholder="输入设备编号过滤"
                style="width: 250px"
                clearable
                :suffix-icon="Search"
              />
            </div>
          </template>
          <el-scrollbar style="height: 230px" wrap-class="scrollbar_dropdown__wrap">
            <el-tree
              ref="authTree"
              :data="authData"
              :props="treeProps"
              :default-expand-all="true"
              :check-strictly="false"
              show-checkbox
              node-key="serNo"
              :filter-node-method="
                (value, data) => {
                  return filterMenuNode(value, data as MenuItem);
                }
              "
            >
            </el-tree>
          </el-scrollbar>
        </el-card>
      </el-col>
    </el-row>
    <!-- 弹窗下发按钮栏，必须设置class为dialog-btn-layer -->
    <el-row type="flex" justify="end" class="dialog-btn-layer" style="margin-top: 20px">
      <el-button :plain="true" @click="onCancel">取消</el-button>
      <el-button type="primary" @click="onSubmit"> 确定 </el-button>
    </el-row>
  </el-form>
</template>

<script setup lang="ts">
import { Search } from '@element-plus/icons-vue';
import { ElTree, ElMessage } from 'element-plus';
import { DialogProp } from '@/components/Dialog/types';
import { usePermissions } from '@/common/hooks/usePermission';
import { ANY_OBJECT } from '@/types/generic';
import { SystemRoleController, SystemMenuController } from '@/api/system';
import { findTreeNode, treeDataTranslate } from '@/common/utils';
import { MenuItem } from '@/types/upms/menu';
import { Role } from '@/types/upms/role';
import { useLayoutStore } from '@/store';
import { TisDeviceInfoController } from '@/api/generated';
const layoutStore = useLayoutStore();

const props = defineProps<{
  rowData?: Role;
  // 当使用Dialog.show弹出组件时，须定义该prop属性，以便对dialog进行回调
  dialog?: DialogProp<ANY_OBJECT>;
}>();
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});
const { checkPermCodeExist } = usePermissions();
const treeProps = {
  label: 'serNo',
};
const form = ref();
const authTree = ref<InstanceType<typeof ElTree>>();
const formData = ref({
  roleId: '',
  roleName: '',
  adminRole: false,
  menuIdListString: '',
  tisUserDeviceList: [],
  menuIdList: [] as string[],
  sysRoleMenuList: [] as MenuItem[],
});
const rules = {
  roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }],
};
const menuNameFilter = ref();
const authData: Ref<MenuItem[]> = ref([]);
let allowParentList: string[] = [];
const isEdit = computed(() => {
  return !!props.rowData?.roleId;
});

const onCancel = () => {
  if (props.dialog) {
    props.dialog.cancel();
  }
};
// eslint-disable-next-line @typescript-eslint/no-unused-vars
const filterMenuNode = (value: string, data: MenuItem) => {
  if (!value) return true;
  if (data.serNo.indexOf(value) !== -1) {
    allowParentList.push(data.serNo);
    return true;
  } else if (data.id) {
    return allowParentList.indexOf(data.id) !== -1;
  }
  return false;
};
const onSubmit = () => {
  form.value.validate((valid: boolean) => {
    if (valid) {
      let selectMenu = authTree.value?.getHalfCheckedKeys() || [];
      selectMenu = selectMenu.concat(authTree.value?.getCheckedKeys() || []);

      if (selectMenu.length <= 0) {
        ElMessage.error({
          message: '请选择需要绑定的设备',
          showClose: true,
        });
        return;
      }
      let params = {
        sysUserDto: { ...formData.value },
        deviceIdListString: '',
      };
      params.deviceIdListString = selectMenu.join(',');
      TisDeviceInfoController.userBind(params)
        .then(res => {
          ElMessage.success('添加成功');
          if (props.dialog) {
            props.dialog.submit(res);
          }
        })
        .catch(e => {
          console.warn(e);
        });
    }
  });
};
const loadAuthData = () => {
  TisDeviceInfoController.list({})
    .then(res => {
      let list: Array<any> = [];
      let obj = {
        serNo: '',
        deviceName: '所有设备',
        children: res.data.dataList,
      };
      list.push(obj);
      authData.value = treeDataTranslate(list, 'serNo', 'id');
      if (Array.isArray(formData.value.menuIdList)) {
        let tempList: string[] = [];
        formData.value.menuIdList.forEach(menuId => {
          let tempMenu = findTreeNode(authData.value, menuId, 'serNo');
          // 判断是否为叶子节点
          if (tempMenu != null && (!tempMenu.children || tempMenu.children.length <= 0)) {
            tempList.push(menuId);
          }
        });
        console.log(tempList, 222);
        authTree.value?.setCheckedKeys(tempList, true);
      }
      console.log(authData);
    })
    .catch(e => {
      console.warn(e);
    });
};

watch(menuNameFilter, val => {
  allowParentList = [];
  authTree.value?.filter(val);
});

onMounted(() => {
  if (props.rowData != null) {
    formData.value = {
      ...props.rowData,
    };
    if (formData.value.tisUserDeviceList) {
      formData.value.menuIdList = formData.value.tisUserDeviceList.map(item => item.deviceNo);
    }
  }

  loadAuthData();
});
</script>
