<template>
  <div class="tab-dialog-box" style="position: relative; margin-top: -15px">
    <el-tabs v-model="activeFragmentId">
      <el-tab-pane label="权限资源" name="fragmentSysMenuPerm" style="width: 100%">
        <el-form label-width="80px" :size="formItemSize" @submit.prevent label-position="left">
          <filter-box
            :item-width="350"
            @search="refreshfragmentSysMenuPerm(true)"
            :hasReset="false"
            style="padding: 0; margin: 0"
          >
            <el-form-item label="URL地址">
              <el-input
                class="filter-item"
                v-model="fragmentSysMenuPerm.formFilter.url"
                clearable
                placeholder=""
              />
            </el-form-item>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <vxe-table
              :data="fragmentSysMenuPerm.SysMenuPerm.impl.dataList"
              :size="formItemSize"
              :row-config="{ isHover: true }"
              :height="getTableHeight + 'px'"
              @sort-change="fragmentSysMenuPerm.SysMenuPerm.impl.onSortChange"
              header-cell-class-name="table-header-gray"
            >
              <vxe-column
                title="序号"
                type="seq"
                width="55px"
                :index="fragmentSysMenuPerm.SysMenuPerm.impl.getTableIndex"
              />
              <vxe-column title="权限字名称" field="showName" width="150px"> </vxe-column>
              <vxe-column title="权限字类型" width="100px">
                <template v-slot="scope">
                  <el-tag :size="formItemSize" :type="getPermCodeType(scope.row.permCodeType)">
                    {{ SysPermCodeType.getValue(scope.row.permCodeType) }}
                  </el-tag>
                </template>
              </vxe-column>
              <vxe-column title="权限字标识" field="permCode" width="300px" />
              <vxe-column title="权限名称" field="permName" width="150px" />
              <vxe-column title="关联URL" field="url" min-width="300px"> </vxe-column>
              <template v-slot:empty>
                <div class="table-empty unified-font">
                  <img src="@/assets/img/empty.png" />
                  <span>暂无数据</span>
                </div>
              </template>
            </vxe-table>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="用户查询" name="fragmentSysMenuUser" style="width: 100%">
        <el-form label-width="80px" :size="formItemSize" @submit.prevent label-position="left">
          <filter-box
            :item-width="350"
            @search="refreshFragmentSysMenuUser(true)"
            :hasReset="false"
            style="padding: 0; margin: 0"
          >
            <el-form-item label="用户名称">
              <el-input
                class="filter-item"
                v-model="fragmentSysMenuUser.formFilter.loginName"
                clearable
                placeholder=""
              />
            </el-form-item>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <vxe-table
              :data="fragmentSysMenuUser.SysMenuUser.impl.dataList"
              :size="formItemSize"
              :row-config="{ isHover: true }"
              :height="getTableHeight + 'px'"
              @sort-change="fragmentSysMenuUser.SysMenuUser.impl.onSortChange"
              header-cell-class-name="table-header-gray"
            >
              <vxe-column
                title="序号"
                header-align="center"
                align="center"
                type="seq"
                width="55px"
                :index="fragmentSysMenuUser.SysMenuUser.impl.getTableIndex"
              />
              <vxe-column title="用户名" field="loginName" />
              <vxe-column title="用户昵称" field="showName" />
              <vxe-column title="用户角色" field="roleName" />
              <template v-slot:empty>
                <div class="table-empty unified-font">
                  <img src="@/assets/img/empty.png" />
                  <span>暂无数据</span>
                </div>
              </template>
            </vxe-table>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { VxeTable, VxeColumn } from 'vxe-table';
import { SysPermCodeType } from '@/common/staticDict';
import { ANY_OBJECT } from '@/types/generic';
import { SystemMenuController } from '@/api/system';
import { treeDataTranslate } from '@/common/utils';
import { TableOptions } from '@/common/types/pagination';
import { useTable } from '@/common/hooks/useTable';
import { TableData } from '@/common/types/table';
import { useLayoutStore } from '@/store';
const layoutStore = useLayoutStore();

const props = defineProps<{
  menuId: string;
  mainContextHeight: Ref<number>;
}>();

const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});

const getTableHeight = computed(() => {
  return props.mainContextHeight.value - 150;
});

const activeFragmentId = ref('fragmentSysMenuPerm');

const loadSysMenuPermData = (params: ANY_OBJECT): Promise<TableData<ANY_OBJECT[]>> => {
  params.menuId = props.menuId;
  params.url = fragmentSysMenuPerm.formFilterCopy.url;
  return new Promise((resolve, reject) => {
    SystemMenuController.listSysPermByMenuIdWithDetail(params)
      .then(res => {
        resolve({
          dataList: treeDataTranslate(res.data),
          totalCount: res.data.length,
        });
      })
      .catch(e => {
        reject(e);
      });
  });
};
/**
 * 菜单权限资源获取检测函数，返回true正常获取数据，返回false停止获取数据
 */
const loadSysMenuPermVerify = () => {
  fragmentSysMenuPerm.formFilterCopy.url = fragmentSysMenuPerm.formFilter.url;
  return true;
};
const getPermCodeType = (permCodeType: number) => {
  switch (permCodeType) {
    case SysPermCodeType.FORM:
      return 'primary';
    case SysPermCodeType.FRAGMENT:
      return 'warning';
    case SysPermCodeType.OPERATION:
      return 'success';
    default:
      return 'info';
  }
};
/**
 * 更新菜单权限资源
 */
const refreshfragmentSysMenuPerm = (reloadData = false) => {
  // 重新获取数据组件的数据
  if (reloadData) {
    fragmentSysMenuPerm.SysMenuPerm.impl.refreshTable(true, 1);
  } else {
    fragmentSysMenuPerm.SysMenuPerm.impl.refreshTable();
  }
};
/**
 * 获取菜单用户函数，返回Promise
 */
const loadSysMenuUserData = (params: ANY_OBJECT): Promise<TableData<ANY_OBJECT[]>> => {
  params.menuId = props.menuId;
  params.loginName = fragmentSysMenuUser.formFilterCopy.loginName;
  return new Promise((resolve, reject) => {
    SystemMenuController.listSysUserByMenuIdWithDetail(params)
      .then(res => {
        resolve({
          dataList: treeDataTranslate(res.data),
          totalCount: res.data.length,
        });
      })
      .catch(e => {
        reject(e);
      });
  });
};
/**
 * 菜单用户获取检测函数，返回true正常获取数据，返回false停止获取数据
 */
const loadSysMenuUserVerify = () => {
  // if (!fragmentSysMenuUser.formFilter.loginName) {
  //   //this.$message.error('请输入用户名！');
  //   return false;
  // }
  fragmentSysMenuUser.formFilterCopy.loginName = fragmentSysMenuUser.formFilter.loginName;
  return true;
};
/**
 * 更新菜单用户
 */
const refreshFragmentSysMenuUser = (reloadData = false) => {
  // 重新获取数据组件的数据
  if (reloadData) {
    fragmentSysMenuUser.SysMenuUser.impl.refreshTable(true, 1);
  } else {
    fragmentSysMenuUser.SysMenuUser.impl.refreshTable();
  }
};

const tableMenuPermOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadSysMenuPermData,
  verifyTableParameter: loadSysMenuPermVerify,
  paged: false,
};

const fragmentSysMenuPerm = reactive({
  formFilter: {
    url: undefined,
  },
  formFilterCopy: {
    url: undefined,
  },
  SysMenuPerm: {
    impl: useTable(tableMenuPermOptions),
  },
});

const tableMenuUserOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadSysMenuUserData,
  verifyTableParameter: loadSysMenuUserVerify,
  paged: false,
};

const fragmentSysMenuUser = reactive({
  formFilter: {
    loginName: undefined,
  },
  formFilterCopy: {
    loginName: undefined,
  },
  SysMenuUser: {
    impl: useTable(tableMenuUserOptions),
  },
});

onMounted(() => {
  refreshfragmentSysMenuPerm();
  refreshFragmentSysMenuUser();
});
</script>
