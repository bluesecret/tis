import { defineStore } from 'pinia';
import type { ComponentSize } from 'element-plus';
import type { MenuItem } from '@/types/upms/menu';
import { findItemFromList } from '@/common/utils';
import { ANY_OBJECT } from '@/types/generic';
import { processMenu, findMenuItem, findMenuItemById } from './utils';

export type TagItemType = {
  // 唯一id
  id: string;
  // 显示名称
  tagName: string;
  // 路由名称
  routerName?: string;
  // 组件名称
  compomentName?: string;
  // query参数
  query?: ANY_OBJECT;
  // 菜单项
  menuItem?: MenuItem;
  deleted?: boolean;
  // 组件
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  compoment?: any;
};

export default defineStore('layout', {
  state: () => {
    return {
      // 首页路由名称
      indexName: 'welcome',
      // 侧边栏是否折叠
      collapsed: false,
      // 是否多栏目
      supportColumn: false,
      // 是否多标签
      supportTags: true,
      // 标签列表
      tagList: new Array<TagItemType>(),
      // 当前标签
      currentTagId: '',
      // 菜单列表
      menuList: new Array<MenuItem>(),
      // 页面缓存列表(已弃用)
      // cachePages: new Array<string>(),
      // 当前菜单
      currentMenu: {} as MenuItem,
      // 当前栏目
      currentColumn: {} as MenuItem,
      // 当前formSize
      defaultFormItemSize: 'default' as ComponentSize,
      documentClientHeight: 200,
      mainContextHeight: 200,
    };
  },
  getters: {
    currentMenuPath(): Array<MenuItem> {
      const menuPath: Array<MenuItem> = [];
      this.menuList.forEach(menu => {
        findMenuItem(menu, this.currentMenu.menuId, menuPath);
      });
      return menuPath;
    },
    currentMenuId(): string {
      return this.currentMenu.menuId;
    },
    currentColumnId(): string {
      return this.currentColumn.menuId;
    },
    currentFormSize(): string {
      return this.defaultFormItemSize;
    },
  },
  actions: {
    setCollapsed(val: boolean) {
      this.collapsed = val;
    },
    toggleCollapsed() {
      this.collapsed = !this.collapsed;
    },
    setMenuList(menuList: Array<MenuItem>) {
      menuList.forEach(item => {
        processMenu(item);
      });
      this.menuList = menuList;
      if (this.supportColumn && menuList && menuList.length) {
        this.currentColumn = menuList[0];
      }
    },
    setCurrentMenu(menu?: MenuItem | null) {
      if (menu == null || menu.menuId == null) {
        this.currentMenu = {} as MenuItem;
        this.currentTagId = '';
      } else {
        this.currentMenu = menu;
        if (this.supportTags) {
          const tagItem = this.tagList.find(item => {
            return item.menuItem?.menuId === menu.menuId;
          });
          if (tagItem) this.currentTagId = tagItem.id;
        }
      }
    },
    removeTag(id: string) {
      const pos = this.tagList.findIndex(item => item.id === id);
      if (pos === -1) return;
      this.tagList[pos].deleted = true;
      if (id === this.currentTagId) {
        // 关闭当前标签页
        if (pos !== -1) {
          const menuItem = pos > 0 ? this.tagList[pos - 1].menuItem : null;
          this.setCurrentMenu(menuItem);
          setTimeout(() => {
            this.tagList.splice(pos, 1);
            this.currentTagId = pos > 0 ? this.tagList[pos - 1].id : '';
          }, 50);
        }
      } else {
        // 关闭非当前标签页
        this.tagList.splice(pos, 1);
      }
    },
    closeOtherTags(id: string) {
      // 关闭其它标签
      const pos = this.tagList.findIndex(item => item.id === id);
      if (pos !== -1) {
        const menuItem = this.tagList[pos].menuItem;
        this.setCurrentMenu(menuItem);
        setTimeout(() => {
          this.tagList = [this.tagList[pos]];
          this.currentTagId = id;
        }, 50);
      }
    },
    clearAllTags() {
      // 关闭所有标签
      this.setCurrentMenu(null);
      setTimeout(() => {
        this.tagList = [];
        this.currentTagId = '';
      }, 50);
    },
    addTag(tag: TagItemType) {
      const item = findItemFromList(this.tagList, tag.id, 'id');
      if (item == null) {
        setTimeout(() => {
          this.tagList.push(tag);
          this.currentTagId = tag.id;
        }, 50);
      } else {
        item.compoment = tag.compoment;
      }
    },
    setCurrentTagId(id: string) {
      this.currentTagId = id;
    },
    setCurrentColumn(column: MenuItem) {
      this.currentColumn = column;
    },
    removeCachePage(id: string) {
      this.removeTag(id);
    },
    setCurrentFormSize(size: ComponentSize) {
      this.defaultFormItemSize = size;
    },
  },
  persist: {
    // 开启持久存储
    enabled: true,
    // 指定哪些state的key需要进行持久存储
    // storage默认是 sessionStorage存储
    // paths需要持久存储的key
    strategies: [
      { key: 'tags', paths: ['tagList', 'currentTagId'] },
      { key: 'menu', paths: ['currentColumn', 'currentMenu', 'menuList'] },
    ],
  },
});
