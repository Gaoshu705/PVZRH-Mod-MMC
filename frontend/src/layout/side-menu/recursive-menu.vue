<template>
  <el-sub-menu v-if="menuItem.children && menuItem.children.length > 0"
               :index="menuItem.menuId"
  >
    <template #title>
      <component v-if="menuItem.icon" :is="menuItem.icon" class="icon"/>
      <span class="title">{{ menuItem.menuName }}</span>
    </template>
    <recursive-menu
        v-for="child in menuItem.children"
        :key="child.menuId"
        :menu-item="child"
    />
  </el-sub-menu>
  <el-menu-item v-else :index="menuItem.menuId" @click="turnToPage(menuItem)">
    <template #default>
      <component v-if="menuItem.icon" :is="menuItem.icon" class="icon"/>
      <span>{{ menuItem.menuName }}</span>
    </template>
  </el-menu-item>
</template>
<script setup>
import {router} from "@/router/index.js";

const props = defineProps({
  menuItem: {
    type: Object,
    required: true,
  }
})

// 页面跳转
function turnToPage(menu) {
  router.push({path: menu.uri})
}
</script>

<style lang="scss" scoped>
.icon {
  width: 20px;
  height: 20px;
  margin-right: 10px;
}
</style>