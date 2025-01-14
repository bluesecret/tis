<template>
  <router-view v-slot="{ Component }">
    <transition name="el-fade-in-linear" :appear="true">
      <keep-alive ref="keepAlive" :include="keepAliveInclude">
        <component :is="getRenderComponent(Component)" />
      </keep-alive>
    </transition>
  </router-view>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router';
import { useLayoutStore } from '@/store';
import { TagItemType } from '@/store/layout';

const route = useRoute();
const layoutStore = useLayoutStore();
const keepAlive = ref();

function createComponent(name, component) {
  return defineComponent({
    name: name,
    render() {
      return component ? h(component) : null;
    },
  });
}

const keepAliveInclude = computed(() => {
  let tempList = [];
  layoutStore.tagList.filter(item => {
    const compomentName = item.compomentName;
    if (compomentName != null && tempList.indexOf(compomentName) === -1) {
      tempList.push(compomentName);
    }
  });
  return tempList;
});

const getRenderComponent = component => {
  const { name, query, fullPath } = route;
  if (route.meta.keepalive == null || !route.meta.keepalive) {
    return component;
  }
  let cacheKey = fullPath;
  let menuItem = layoutStore.currentMenu;
  let showName = route.meta?.title || route.name;
  let currentMenu;
  // 判断当前路由是否是菜单路由，如果是菜单的路由使用菜单的名字，对于在线表单和报表formRouterName为空，其他的路由菜单则通过formRouterName来判断
  if (
    menuItem != null &&
    menuItem.menuId != null &&
    (menuItem.formRouterName == null || menuItem.formRouterName === route.name)
  ) {
    showName = menuItem.menuName;
    currentMenu = menuItem;
  }
  let tagItem: TagItemType = layoutStore.tagList.find(item => item.id === cacheKey);
  if (tagItem == null) {
    let tempComponent = createComponent(cacheKey, component);
    tagItem = {
      id: cacheKey,
      tagName: showName,
      routerName: route.name,
      compomentName: cacheKey,
      query: route.query,
      menuItem: currentMenu,
      component: tempComponent,
    };
  } else if (tagItem.component == null || tagItem.component.render == null) {
    tagItem.component = createComponent(cacheKey, component);
  }
  if (tagItem.deleted !== true) {
    layoutStore.addTag(tagItem);
  }
  return tagItem.component;
};
</script>
