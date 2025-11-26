<script setup lang="ts">
import { ref, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import { getCurrentWindow } from '@tauri-apps/api/window';
// import { MessagePlugin } from 'tdesign-vue-next';

const router = useRouter();
const route = useRoute();
const collapsed = ref(true);
const iconUrl = ref('https://mod.ehre.top/api/default/avatar.png');
const appWindow = getCurrentWindow();

const minimize = () => {
  appWindow.minimize();
}

const close = () => {
  appWindow.close();
}

const changeCollapsed = () => {
  collapsed.value = !collapsed.value;
  // iconUrl.value = collapsed.value
  //   ? 'https://oteam-tdesign-1258344706.cos.ap-guangzhou.myqcloud.com/site/logo%402x.png'
  //   : 'https://tdesign.gtimg.com/site/baseLogo-light.png';
};

// 菜单点击处理，实现路由跳转
const changeHandler = (active: string) => {
  console.log('change', active);
  // 根据菜单项的value匹配对应的路由
  const routeMap: Record<string, string> = {
    'dashboard': '/dashboard',
    '2-1-1': '/list/basic',
    '2-1-2': '/list/card',
    '2-1-3': '/list/filter',
    '2-1-4': '/list/tree-filter',
    '2-2': '/form',
    '2-3': '/detail',
    '2-4': '/result',
    'item3': '/user',
    'item4': '/login',
    'online-mod': '/online-mod',
    'local-mod': '/local-mod'
  };
  
  if (routeMap[active]) {
    router.push(routeMap[active]);
  }
};

// 监听路由变化，更新当前激活的菜单项
const currentActive = ref('item1');
watch(() => route.path, (newPath) => {
  const pathMap: Record<string, string> = {
    '/dashboard': 'dashboard',
    '/list/basic': '2-1-1',
    '/list/card': '2-1-2',
    '/list/filter': '2-1-3',
    '/list/tree-filter': '2-1-4',
    '/form': '2-2',
    '/detail': '2-3',
    '/result': '2-4',
    '/user': 'item3',
    '/login': 'item4',
    '/online-mod': 'online-mod',
    '/local-mod': 'local-mod'
  };
  
  currentActive.value = pathMap[newPath] || 'item1';
}, { immediate: true });

// 跳转到设置页面
const goToSetting = () => {
  router.push('/setting');
};
</script>

<template>
  
  <div class="app-container">
    <!-- 侧边菜单 -->
    <t-menu 
      theme="light" 
      v-model:value="currentActive" 
      :collapsed="collapsed" 
      @change="changeHandler"
      width="240px"
    >
      <template #logo>
        <img :width="collapsed ? 35 : 35" :src="iconUrl" alt="logo" />
      </template>
      <t-menu-group title="主导航">
        <t-menu-item value="dashboard">
          <template #icon>
            <t-icon name="app" />
          </template>
          仪表盘
        </t-menu-item>
      </t-menu-group>
      <t-menu-group title="功能">
        <!-- <t-submenu title="列表项" value="2-1">
          <template #icon>
            <t-icon name="server" />
          </template>
          <t-menu-item value="2-1-1">基础列表项</t-menu-item>
          <t-menu-item value="2-1-2">卡片列表项</t-menu-item>
          <t-menu-item value="2-1-3">筛选列表项</t-menu-item>
          <t-menu-item value="2-1-4">树状筛选列表项</t-menu-item>
        </t-submenu>
        <t-menu-item value="2-2">
          <template #icon>
            <t-icon name="edit-1" />
          </template>
          表单项
        </t-menu-item>
        <t-menu-item value="2-3">
          <template #icon>
            <t-icon name="root-list" />
          </template>
          详情页
        </t-menu-item>
        <t-menu-item value="2-4">
          <template #icon>
            <t-icon name="check" />
          </template>
          结果页
        </t-menu-item> -->
      <t-menu-item value="online-mod">
          <template #icon>
            <t-icon name="cloud" />
          </template>
          在线模组
        </t-menu-item>
        <t-menu-item value="local-mod">
          <template #icon>
            <t-icon name="folder-open" />
          </template>
          本地模组
        </t-menu-item>
      </t-menu-group>
      <!-- <t-menu-group title="更多">
        <t-menu-item value="item3">
          <template #icon>
            <t-icon name="user" />
          </template>
          个人页
        </t-menu-item>
        <t-menu-item value="item4">
          <template #icon>
            <t-icon name="login" />
          </template>
          登录页
        </t-menu-item>
      </t-menu-group> -->
      <template #operations>
        <div class="menu-operations">
          <t-button class="t-demo-collapse-btn" variant="text" shape="square" @click="changeCollapsed">
            <template #icon><t-icon name="view-list" /></template>
          </t-button>
          <t-button variant="text" shape="square" @click="goToSetting">
            <template #icon><t-icon name="setting" /></template>
          </t-button>
        </div>
      </template>
    </t-menu>
    
    <!-- 右侧内容区域 -->
    <div class="right-content">
      <!-- Mac风格标题栏 -->
      <div class="mac-title-bar">
        <!-- 左侧控制按钮 -->
        <!-- <div class="mac-control-buttons">
          <t-button variant="text" class="mac-btn close-btn" shape="circle" size="small" @click="close">
            <template #icon><t-icon name="close" /></template>
          </t-button>
          <t-button variant="text" class="mac-btn minimize-btn" shape="circle" size="small" @click="minimize">
            <template #icon><t-icon name="minus" /></template>
          </t-button>
        </div> -->
        
        <!-- 中间标题 -->
        <div class="mac-title">模组管理器</div>
        
        <!-- 右侧操作按钮 -->
        <div class="mac-actions">
          <t-button variant="text" class="mac-btn minimize-btn" shape="circle" size="small" @click="minimize">
            <template #icon><t-icon name="minus" /></template>
          </t-button>
          <t-button variant="text" class="mac-btn close-btn" shape="circle" size="small" @click="close">
            <template #icon><t-icon name="close" /></template>
          </t-button>
          <!-- <t-button variant="text" shape="square" size="small" @click="MessagePlugin.info('这是一个提示信息')">
            <template #icon><t-icon name="question-circle" size="1em" /></template>
          </t-button>
          <t-button variant="text" shape="square" size="small">
            <template #icon><t-icon name="info-circle" /></template>
          </t-button>
          <t-button variant="text" shape="square" size="small">
            <template #icon><t-icon name="ellipsis" /></template>
          </t-button> -->
        </div>
      </div>
      
      <!-- 主内容区域 -->
      <div class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<style>
:root {
  font-family: Inter, Avenir, Helvetica, Arial, sans-serif;
  font-size: 16px;
  line-height: 24px;
  font-weight: 400;

  color: #0f0f0f;
  background-color: #f6f6f6;

  font-synthesis: none;
  text-rendering: optimizeLegibility;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  -webkit-text-size-adjust: 100%;
  user-select: none;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  height: 100%;
  width: 100%;
}

.app-container {
  display: flex;
  height: 100vh;
  width: 100%;
  overflow: hidden;
  background-color: #ffffff;
}

.right-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

/* Mac风格标题栏 */
.mac-title-bar {
  display: flex;
  align-items: center;
  height: 36px;
  background-color: #ffffff;
  border-bottom: 1px solid #ebeef5;
  padding: 0 16px;
  -webkit-app-region: drag;
  user-select: none;
}

/* 左侧控制按钮 */
.mac-control-buttons {
  display: flex;
  gap: 8px;
  margin-right: 16px;
}

.mac-btn {
  width: 10% !important;
  height: 10% !important;
  border-radius: 50%;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  -webkit-app-region: no-drag;
  border: none !important;
  box-shadow: none !important;
}

.mac-btn t-icon {
  font-size: 8px;
  color: white !important;
}

/* 关闭按钮颜色 */
.close-btn {
  background-color: #ff5f57 !important;
}

.close-btn:hover {
  background-color: #ff3b30 !important;
}

/* 最小化按钮颜色 */
.minimize-btn {
  background-color: #ffbd2e !important;
}

.minimize-btn:hover {
  background-color: #ff9500 !important;
}

/* 最大化按钮颜色 */
.maximize-btn {
  background-color: #28ca42 !important;
}

.maximize-btn:hover {
  background-color: #30d158 !important;
}

/* 中间标题 */
.mac-title {
  flex: 1;
  font-size: 13px;
  color: #333333;
  font-weight: 500;
  text-align: center;
}

/* 右侧操作按钮 */
.mac-actions {
  width: 30px;
  display: flex;
  gap: 8px;
  -webkit-app-region: no-drag;
}

.mac-actions t-button {
  color: #666666;
}

.mac-actions t-button:hover {
  color: #333333;
  background-color: rgba(0, 0, 0, 0.05);
}

/* 主内容区域 */
.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f5f7fa;
}

.menu-operations {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 8px;
}

/* 路由切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    padding: 10px;
  }
}
</style>