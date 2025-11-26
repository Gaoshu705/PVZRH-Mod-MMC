<template>
  <el-card class="role-setting">
    <template #header>
      <div class="header">
        设置该角色对应的后台管理权限
        <el-button style="float: right" v-if="activeRoleId" type="primary" @click="saveChange" :disabled="!hasPerm('system:role:upd')">保存</el-button>
      </div>
    </template>
    <div class="role-container">
      <el-checkbox-group v-model="checkedData">
        <div class="role-list">
          <RecursiveTree :tree="tree" :index="0" v-if="hasPerm('system:role:get')"/>
        </div>
      </el-checkbox-group>
    </div>
  </el-card>
</template>

<script setup>
import {inject, ref, watch} from 'vue';

import _ from 'lodash';
import {useMenuSettingStore} from "@/stores/menu-setting.js";
import {roleMenuApi} from "@/api/role-menu-api.js";
import {usemodLoadingStore} from "@/stores/mod-loading.js";
import RecursiveTree from "@/views/system/role/components/recursive-tree.vue";
import {hasPerm} from "@/utils/permission.js";


let menuSettingStore = useMenuSettingStore();
let tree = ref();
let activeRoleId = inject('activeRoleId');

watch(activeRoleId, () => getRoleSelectedMenu(), {immediate: true});

async function getRoleSelectedMenu() {
  if (!activeRoleId.value) return;
  let res = await roleMenuApi.getSelectedMenu(activeRoleId.value);
  if (res.code === 0) {
    let data = res.data;
    if (_.isEmpty(menuSettingStore.treeMap)) {
      menuSettingStore.initTreeMap(data.menuTree || []);
    }
    menuSettingStore.initCheckedData(data.selectedMenuIds || []);
    tree.value = data.menuTree;
  }
}

async function saveChange() {
  let checkedData = menuSettingStore.checkedData;
  if (_.isEmpty(checkedData)) {
    ElMessage.error('还未选择任何权限');
    return;
  }
  let params = {
    roleId: activeRoleId.value,
    selectedMenuIds: checkedData,
  };
  usemodLoadingStore().show();
  try {
    await roleMenuApi.updateRoleTree(params);
    ElMessage.success('保存成功');
  } catch (error) {
    console.log(error)
  } finally {
    usemodLoadingStore().hide();
  }
}

// -----------------------------------------------------------------
const checkedData = ref([]);

watch(() => menuSettingStore.checkedData, (newCheckedData) => {
  checkedData.value = newCheckedData;
}, {deep: true});
</script>

<style scoped lang="scss">
.role-setting {
  height: 100%;
}

</style>
