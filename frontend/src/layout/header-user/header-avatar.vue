<template>
  <el-dropdown class="header-trigger">
    <div class="wrapper">
      <img class="avatar-image" :src="avatar" v-if="avatar"/>
      <el-avatar v-else :size="20" class="avatar">
        user
      </el-avatar>
      <span class="name">{{ nickname }}</span>
    </div>
    <template #dropdown>
      <el-dropdown-menu class="avatar-menu">
        <el-dropdown-item @click="toAccount">
          <span>个人中心</span>
        </el-dropdown-item>
        <el-dropdown-item @click="onLogout">
          <span>退出登录</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>
<script setup>
import {ref, reactive, computed} from 'vue'
import {loginApi} from "@/api/login-api.js";
import {useUserStore} from "@/stores/user.js";
import {useMenuStore} from "@/stores/menu.js";
import {useRouter} from "vue-router";

const userStore = useUserStore()
const nickname = computed(() => userStore.nickname)
const avatar = computed(() => userStore.avatar)

async function onLogout() {
  try {
    let res = await loginApi.logout()
    if(res.code === 0){
      ElMessage.success('退出登录成功')
    }
  } catch (e) {
    console.log(e)
  } finally {
    useUserStore().reset()
    useMenuStore().reset()
    location.reload()
  }
}

// ------------------------ 个人中心 ------------------------
const router = useRouter()

function toAccount(menuId) {
  router.push({
    path: '/account',
    // query: {menuId}
  })
}
</script>

<style scoped lang="scss">
.wrapper {
  cursor: pointer;
  display: flex;
  align-items: center;

  .avatar-image {
    width: 20px;
    height: 20px;
    object-fit: cover;
    border-radius: 50%;
    margin-right: 10px;
  }
}
</style>