<template>
  <div class="tab-dialog-box" style="position: relative; margin-top: -15px">
    <el-tabs v-model="activeFragmentId">
      <el-tab-pane label="权限资源" name="fragmentSysUserPerm" style="width: 100%">
        <el-form
          :label-width="formItemSize == 'default' ? '65px' : '75px'"
          :size="formItemSize"
          @submit.prevent
          label-position="left"
        >
          <filter-box
            :item-width="350"
            @search="refreshFragmentSysUserPerm(true)"
            :hasReset="false"
            style="padding: 0; margin: 0"
          >
            <el-form-item label="URL地址">
              <el-input
                class="filter-item"
                v-model="fragmentSysUserPerm.formFilter.url"
                clearable
                placeholder=""
              />
            </el-form-item>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <vxe-table
              :data="fragmentSysUserPerm.SysUserPerm.impl.dataList"
              :size="formItemSize"
              :height="getTableHeight + 'px'"
              :row-config="{ isHover: true }"
              @sort-change="fragmentSysUserPerm.SysUserPerm.impl.onSortChange"
              header-cell-class-name="table-header-gray"
            >
              <vxe-column
                title="序号"
                type="seq"
                width="55px"
                :index="fragmentSysUserPerm.SysUserPerm.impl.getTableIndex"
              />
              <vxe-column title="所属角色" field="roleName" width="150px" />
              <vxe-column title="菜单">
                <template v-slot="scope">
                  <span>{{
                    getMenuPathString(getMenuPathById(scope.row.menuId)) || scope.row.menuName
                  }}</span>
                </template>
              </vxe-column>
              <vxe-column title="所属权限字" field="permCode" />
              <vxe-column title="URL" field="url" />
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
      <el-tab-pane label="权限字名" name="fragmentSysUserPermCode" style="width: 100%">
        <el-form
          :label-width="formItemSize == 'default' ? '65px' : '75px'"
          :size="formItemSize"
          @submit.prevent
          label-position="left"
        >
          <filter-box
            :item-width="350"
            @search="refreshFragmentSysUserPermCode(true)"
            :hasReset="false"
            style="padding: 0; margin: 0"
          >
            <el-form-item label="权限字名">
              <el-input
                class="filter-item"
                v-model="fragmentSysUserPermCode.formFilter.permCode"
                clearable
                placeholder=""
              />
            </el-form-item>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <vxe-table
              :data="fragmentSysUserPermCode.SysUserPermCode.impl.dataList"
              :size="formItemSize"
              :height="getTableHeight + 'px'"
              :row-config="{ isHover: true }"
              @sort-change="fragmentSysUserPermCode.SysUserPermCode.impl.onSortChange"
              header-cell-class-name="table-header-gray"
            >
              <vxe-column
                title="序号"
                type="seq"
                width="55px"
                :index="fragmentSysUserPermCode.SysUserPermCode.impl.getTableIndex"
              />
              <vxe-column title="所属角色" field="roleName" width="150px" />
              <vxe-column title="菜单">
                <template v-slot="scope">
                  <span>{{
                    getMenuPathString(getMenuPathById(scope.row.menuId)) || scope.row.menuName
                  }}</span>
                </template>
              </vxe-column>
              <vxe-column title="权限字" field="permCode" />
              <vxe-column title="权限字类型" field="permCodeType">
                <template v-slot="scope">
                  <el-tag :size="formItemSize" :type="getPermCodeType(scope.row.permCodeType)">{{
                    SysPermCodeType.getValue(scope.row.permCodeType)
                  }}</el-tag>
                </template>
              </vxe-column>
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
      <el-tab-pane label="菜单权限" name="fragmentSysUserMenu" style="width: 100%">
        <el-form
          :label-width="formItemSize == 'default' ? '65px' : '75px'"
          :size="formItemSize"
          @submit.prevent
          label-position="left"
        >
          <filter-box
            :item-width="350"
            @search="refreshFragmentSysUserMenu(true)"
            :hasReset="false"
            style="padding: 0; margin: 0"
          >
            <el-form-item label="菜单名称">
              <el-input
                class="filter-item"
                v-model="fragmentSysUserMenu.formFilter.menuName"
                clearable
                placeholder=""
              />
            </el-form-item>
          </filter-box>
        </el-form>
        <el-row>
          <el-col :span="24">
            <vxe-table
              :data="fragmentSysUserMenu.SysUserMenu.impl.dataList"
              :size="formItemSize"
              :height="getTableHeight + 'px'"
              :row-config="{ isHover: true }"
              @sort-change="fragmentSysUserMenu.SysUserMenu.impl.onSortChange"
              header-cell-class-name="table-header-gray"
            >
              <vxe-column
                title="序号"
                type="seq"
                width="55px"
                :index="fragmentSysUserPermCode.SysUserPermCode.impl.getTableIndex"
              />
              <vxe-column title="所属角色" field="roleName" width="150px" />
              <vxe-column title="菜单">
                <template v-slot="scope">
                  <span>{{
                    getMenuPathString(getMenuPathById(scope.row.menuId)) || scope.row.menuName
                  }}</span>
                </template>
              </vxe-column>
              <vxe-column title="菜单类型" field="menuType">
                <template v-slot="scope">
                  <el-tag :size="formItemSize" :type="getMenuType(scope.row)">{{
                    SysMenuType.getValue(scope.row.menuType)
                  }}</el-tag>
                </template>
              </vxe-column>
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
import { TableOptions } from '@/common/types/pagination';
import { ANY_OBJECT } from '@/types/generic';
import { SystemMenuController, SystemUserController } from '@/api/system';
import { SysMenuType, SysPermCodeType } from '@/common/staticDict';
import { MenuItem } from '@/types/upms/menu';
import { useTable } from '@/common/hooks/useTable';
import { useLayoutStore } from '@/store';
import { useMenuTools } from '../formSysMenu/hooks';
const layoutStore = useLayoutStore();

const props = defineProps<{
  userId: string;
  mainContextHeight: Ref<number>;
}>();

const activeFragmentId = ref('fragmentSysUserPerm');
const formItemSize = computed(() => {
  return layoutStore.defaultFormItemSize;
});
const getTableHeight = computed(() => {
  return props.mainContextHeight.value - 150;
});
const { getMenuType } = useMenuTools();
const menuMap = new Map();

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
 * 获取用户权限资源列表函数，返回Promise
 */
const loadSysUserPermData = (params: ANY_OBJECT) => {
  params.userId = props.userId;
  params.url = fragmentSysUserPerm.formFilterCopy.url;
  return new Promise((resolve, reject) => {
    SystemUserController.listSysPermWithDetail(params)
      .then(res => {
        resolve({
          dataList: res.data.map((item: ANY_OBJECT) => {
            return { ...item };
          }),
          totalCount: res.data.length,
        });
      })
      .catch(e => {
        reject(e);
      });
  });
};
/**
 * 用户权限资源获取检测函数，返回true正常获取数据，返回false停止获取数据
 */
const loadSysUserPermVerify = () => {
  fragmentSysUserPerm.formFilterCopy.url = fragmentSysUserPerm.formFilter.url;
  return true;
};
/**
 * 更新用户权限资源
 */
const refreshFragmentSysUserPerm = (reloadData = false) => {
  // 重新获取数据组件的数据
  if (reloadData) {
    fragmentSysUserPerm.SysUserPerm.impl.refreshTable(true, 1);
  } else {
    fragmentSysUserPerm.SysUserPerm.impl.refreshTable();
  }
  fragmentSysUserPerm.isInit = true;
};
const tableUserPermOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadSysUserPermData,
  verifyTableParameter: loadSysUserPermVerify,
};
const fragmentSysUserPerm = reactive({
  formFilter: {
    url: undefined,
  },
  formFilterCopy: {
    url: undefined,
  },
  SysUserPerm: {
    impl: useTable(tableUserPermOptions),
  },
  isInit: false,
});

/**
 * 获取用户权限字列表函数，返回Promise
 */
const loadSysUserPermCodeData = (params: ANY_OBJECT) => {
  params.userId = props.userId;
  params.permCode = fragmentSysUserPermCode.formFilterCopy.permCode;
  return new Promise((resolve, reject) => {
    SystemUserController.listSysPermCodeWithDetail(params)
      .then(res => {
        resolve({
          dataList: res.data.map((item: ANY_OBJECT) => {
            return { ...item };
          }),
          totalCount: res.data.length,
        });
      })
      .catch(e => {
        reject(e);
      });
  });
};
/**
 * 用户权限资源获取检测函数，返回true正常获取数据，返回false停止获取数据
 */
const loadSysUserPermCodeVerify = () => {
  fragmentSysUserPermCode.formFilterCopy.permCode = fragmentSysUserPermCode.formFilter.permCode;
  return true;
};
/**
 * 更新用户权限资源
 */
const refreshFragmentSysUserPermCode = (reloadData = false) => {
  // 重新获取数据组件的数据
  if (reloadData) {
    fragmentSysUserPermCode.SysUserPermCode.impl.refreshTable(true, 1);
  } else {
    fragmentSysUserPermCode.SysUserPermCode.impl.refreshTable();
  }
};

const tableUserPermCodeOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadSysUserPermCodeData,
  verifyTableParameter: loadSysUserPermCodeVerify,
};
const fragmentSysUserPermCode = reactive({
  formFilter: {
    permCode: undefined,
  },
  formFilterCopy: {
    permCode: undefined,
  },
  SysUserPermCode: {
    impl: useTable(tableUserPermCodeOptions),
  },
});

/**
 * 获取用户权限资源列表函数，返回Promise
 */
const loadSysUserMenuData = (params: ANY_OBJECT) => {
  params.userId = props.userId;
  params.menuName = fragmentSysUserMenu.formFilterCopy.menuName;
  return new Promise((resolve, reject) => {
    SystemUserController.listSysMenuWithDetail(params)
      .then(res => {
        resolve({
          dataList: res.data.map((item: ANY_OBJECT) => {
            return { ...item };
          }),
          totalCount: res.data.length,
        });
      })
      .catch(e => {
        reject(e);
      });
  });
};
/**
 * 用户权限资源获取检测函数，返回true正常获取数据，返回false停止获取数据
 */
const loadSysUserMenuVerify = () => {
  fragmentSysUserMenu.formFilterCopy.menuName = fragmentSysUserMenu.formFilter.menuName;
  return true;
};
/**
 * 更新用户权限资源
 */
const refreshFragmentSysUserMenu = (reloadData = false) => {
  // 重新获取数据组件的数据
  if (reloadData) {
    fragmentSysUserMenu.SysUserMenu.impl.refreshTable(true, 1);
  } else {
    fragmentSysUserMenu.SysUserMenu.impl.refreshTable();
  }
};

const tableUserMenuOptions: TableOptions<ANY_OBJECT> = {
  loadTableData: loadSysUserMenuData,
  verifyTableParameter: loadSysUserMenuVerify,
};
const fragmentSysUserMenu = reactive({
  formFilter: {
    menuName: undefined,
  },
  formFilterCopy: {
    menuName: undefined,
  },
  SysUserMenu: {
    impl: useTable(tableUserMenuOptions),
  },
});

/**
 * 获取所有菜单项
 */
const loadSysMenuData = () => {
  SystemMenuController.getMenuPermList({})
    .then(res => {
      res.data.forEach(item => {
        menuMap.set(item.menuId, item);
      });
    })
    .catch(e => {
      console.warn(e);
    });
};
const getMenuPathById = (menuId: string | null) => {
  if (menuId == null || menuId === '') return null;
  let menuPath = [];
  do {
    let menuItem = menuMap.get(menuId);
    if (menuItem != null) {
      menuPath.unshift(menuItem);
      menuId = menuItem.parentId;
    } else {
      menuId = null;
    }
  } while (menuId != null);

  return menuPath;
};
const getMenuPathString = (menuPath: MenuItem[] | null) => {
  if (menuPath && menuPath.length > 0) {
    return menuPath.map(item => item.menuName).join(' / ');
  } else {
    return null;
  }
};
onMounted(() => {
  loadSysMenuData();
});
</script>
