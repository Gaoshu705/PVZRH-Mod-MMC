<template>
  <div class="tree-main">
    <el-row :gutter="10">
      <el-col :span="6">
        <RoleList ref="roleRef"/>
      </el-col>
      <el-col :span="18" class="role-setting">
        <RoleSetting/>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {computed, provide, ref} from 'vue';
import RoleList from './components/role-list.vue'
import RoleSetting from "./components/role-setting.vue";

const roleRef = ref(null);
const activeRoleId = computed(() => {
  if (!roleRef.value) return null
  return roleRef.value.activeRoleId
})
provide('activeRoleId', activeRoleId)
</script>

<style lang="scss" scoped>
.tree-main {
  height: 100%;
}
</style>
