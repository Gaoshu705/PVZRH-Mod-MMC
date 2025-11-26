<template>
  <el-breadcrumb separator=">" class="breadcrumb">
    <el-breadcrumb-item v-for="(item, index) in parentMenuList" :key="index">{{ item.menuName }}</el-breadcrumb-item>
    <el-breadcrumb-item>{{ currentRoute.meta.title }}</el-breadcrumb-item>
  </el-breadcrumb>
</template>
<script setup>
import {useRoute} from 'vue-router'
import {computed} from 'vue'
import {useMenuStore} from "@/stores/menu.js";

let currentRoute = useRoute()

const parentMenuList = computed(() => {
  let menuId = currentRoute.meta.menuId
  if (!menuId || typeof menuId !== 'string') {
    return []
  }
  let menuParentListMap = useMenuStore().menuParentListMap
  return menuParentListMap[menuId] || []
})
</script>
<style scoped lang="scss">

</style>