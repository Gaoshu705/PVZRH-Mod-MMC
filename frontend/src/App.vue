<template>
  <el-config-provider :locale="locale">
    <div v-loading="loading"
         element-loading-text="加载中..."
         :element-loading-spinner="svg"
         element-loading-svg-view-box="-10, -10, 50, 50">
      <RouterView/>
    </div>
  </el-config-provider>
</template>
<script setup>
import {computed, watch} from 'vue'
import {usemodLoadingStore} from "@/stores/mod-loading.js";
import {useAdminConfigStore} from "@/stores/admin-config.js";
import {en, zhCn} from "element-plus/es/locale/index";

const locale = computed(() => (useAdminConfigStore().language === 'zh-cn' ? zhCn : en))
const loading = computed(() => usemodLoadingStore().loading)
const themeColor = computed(() => useAdminConfigStore().themeColor)
const svg = `
        <path class="path" d="
          M 30 15
          L 28 17
          M 25.61 25.61
          A 15 15, 0, 0, 1, 15 30
          A 15 15, 0, 1, 1, 27.99 7.5
          L 15 15
        " style="stroke-width: 4px; fill: rgba(0, 0, 0, 0)"/>`
// 监听 themeColor 的变化并更新 CSS 变量
watch(themeColor, () => {
  document.documentElement.style.setProperty('--el-color-primary', themeColor.value);
}, {immediate: true});
</script>
<style lang="scss">
body {
  background-color: #f0f0f0;
  margin: 0;
}

.drawer-footer {
  position: relative;
  margin: -10px -20px;
  padding: 10px 16px 0 16px;
  border-top: 1px solid #e9e9e9;
  text-align: left;
  box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.05);
}

/* 全局滚动条的样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.3);
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.5);
}
</style>
