<template>
  <div class="side-menu" :style="{width: !collapse ? sideMenuWidth : 'auto'}">
    <el-menu mode="vertical"
             :collapse="collapse"
             :default-openeds="openedMenuIds"
             :default-active="activeMenuId"
    >
      <div class="logo" @click="onGoHome">
        <img class="logo-img" src="/logo.png"/>
        <div class="title" style="color: black" v-show="!collapse">{{ websiteName }}</div>
      </div>
      <recursiveMenu
          v-for="item in menuData"
          :key="item.menuId"
          :menu-item="item"
      />
    </el-menu>
  </div>
</template>
<script setup>
import {ref, computed, watch} from 'vue'
import RecursiveMenu from "@/layout/side-menu/recursive-menu.vue";
import {useMenuStore} from "@/stores/menu.js";
import {HOME_PAGE} from "@/constants/index.js";
import {useRoute, useRouter} from "vue-router";
import {useAdminConfigStore} from "@/stores/admin-config.js";
import _ from 'lodash'

const props = defineProps({
  collapse: {
    type: Boolean,
    default: false
  }
})

const menuData = computed(() => useMenuStore().menuTree)
const websiteName = computed(() => useAdminConfigStore().websiteName)
//菜单宽度
const sideMenuWidth = computed(() => useAdminConfigStore().sideMenuWidth)
const router = useRouter()

function onGoHome() {
  router.push({name: HOME_PAGE})
}

// ------------------------------- 打开的菜单栏 ---------------------------------
const openedMenuIds = ref([])
const activeMenuId = ref('')
let currentRoute = useRoute()

function updateMenuOpened() {
  activeMenuId.value = currentRoute.meta.menuId

  let menuParentListMap = useMenuStore().menuParentListMap
  let parentList = menuParentListMap[currentRoute.meta.menuId] || []
  if(!props.collapse){
    let needOpenIds = _.map(parentList, 'menuId')
    openedMenuIds.value = _.union(openedMenuIds.value, needOpenIds)
  }
}

watch(
    currentRoute,
    () => {
      updateMenuOpened()
    },
    {
      immediate: true
    }
)


</script>
<style scoped lang="scss">
.side-menu {
  height: 100%;

  .logo {
    display: flex;
    align-items: center;
    cursor: pointer;

    .logo-img {
      height: 30px;
      width: 30px;
      margin: 5px 10px 0 10px;
    }
  }

  .el-menu {
    height: 100%;
  }
}
</style>