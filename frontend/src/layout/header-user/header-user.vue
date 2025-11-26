<template>
  <el-space :size="10">
    <div class="setting">
      <el-icon @click="showSetting" class="operate-icon">
        <Setting/>
      </el-icon>
    </div>
    <div class="user-space-item">
      <HeaderAvatar></HeaderAvatar>
    </div>

    <HeaderSetting ref="doShow"/>
  </el-space>
</template>

<script setup>
import {ref} from 'vue'
import HeaderSetting from "@/layout/header-user/header-setting.vue";
import HeaderAvatar from "@/layout/header-user/header-avatar.vue";

// 设置
const doShow = ref(null)

function showSetting() {
  if (doShow.value) {
    doShow.value.show()
  }
}


</script>

<style lang="scss" scoped>
.setting {
  // display: flex;
  // align-items: center;
  cursor: pointer;
}
</style>
