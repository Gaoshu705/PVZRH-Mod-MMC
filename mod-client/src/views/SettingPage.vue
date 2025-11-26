<template>
  <div class="setting-container">
    <div class="page-header">
      <h1>设置</h1>
    </div>
    
    <div class="setting-content">
      <!-- 1. 系统设置卡片 -->
      <t-card title="系统设置" :bordered="false" class="setting-card">
        <div class="card-content">
          <div class="setting-item">
            <span class="label">游戏路径：</span>
            <t-input-group>
              <t-input 
                v-model="gamePath" 
                placeholder="请选择游戏路径" 
                @change="onInputChange" 
                style="width: 350px;"
              />
              <t-button theme="primary" @click="selectGamePath">浏览</t-button>
            </t-input-group>
          </div>
          <div class="path-hint" v-if="gamePath">
            当前路径：{{ gamePath }}
          </div>
        </div>
      </t-card>

      <!-- 2. 关于信息卡片 -->
      <t-card title="关于软件" :bordered="false" class="setting-card">
        <div class="about-content">
          <div class="app-info">
            <div class="app-logo">
              <!-- 这里可以用 img 标签放你的 logo，暂时用图标代替 -->
              <t-icon name="app" size="48px" style="color: #0052D9;" />
            </div>
            <div class="app-text">
              <h3>MOD 应用管理系统</h3>
              <p>版本：v{{ appVersion }}</p>
            </div>
          </div>

          <t-divider dashed style="margin: 16px 0" />

          <t-list :split="false">
            <t-list-item>
              <t-list-item-meta title="开发者" description="LibraHp_0928" />
              <template #action>
                <t-button theme="primary" variant="outline" size="small" @click="openLink('https://space.bilibili.com/1117414477')">
                  访问主页
                </t-button>
              </template>
            </t-list-item>
            
            <t-list-item>
              <t-list-item-meta title="开源地址" description="GitHub Repository" />
              <template #action>
                <t-button theme="primary" variant="outline" size="small" @click="openLink('https://github.com/Gaoshu705/PVZRH-Mod-MMC')">
                  去 Star
                </t-button>
              </template>
            </t-list-item>

            <t-list-item>
              <t-list-item-meta title="检查更新" description="当前已是最新版本" />
              <template #action>
                <t-button theme="primary" variant="outline" size="small" @click="checkUpdate">
                  检查
                </t-button>
              </template>
            </t-list-item>
          </t-list>

          <div class="copyright">
            Copyright © 2025 LibraHp_0928. All Rights Reserved.
          </div>
        </div>
      </t-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { open } from '@tauri-apps/plugin-dialog';
import { load } from '@tauri-apps/plugin-store';
import { getVersion } from '@tauri-apps/api/app'; // 获取应用版本
import { openUrl } from '@tauri-apps/plugin-opener'; // 打开外部链接
import { MessagePlugin } from 'tdesign-vue-next';
// import { AppIcon } from 'tdesign-icons-vue-next'; // 引入图标组件

const gamePath = ref('');
const appVersion = ref('1.0.0');

// 通用保存函数
const saveSetting = async (key: string, value: string) => {
  try {
    const store = await load('store.json');
    await store.set(key, value);
    await store.save();
  } catch (err) {
    console.error('保存失败:', err);
    MessagePlugin.error('设置保存失败');
  }
};

// 选择路径
const selectGamePath = async () => {
  try {
    const file = await open({
      multiple: false,
      directory: true,
    });
    if (file) {
      gamePath.value = file;
      await saveSetting('gamePath', file);
      MessagePlugin.success('游戏路径已更新');
    }
  } catch (err) {
    console.error(err);
  }
}

// 输入框变更
const onInputChange = async (newValue: string) => {
  if (newValue) {
    await saveSetting('gamePath', newValue);
  }
};

// 打开外部链接
const openLink = async (url: string) => {
  try {
    await openUrl(url);
  } catch (e) {
    MessagePlugin.error('无法打开链接');
  }
};

// 模拟检查更新
const checkUpdate = () => {
  MessagePlugin.loading('正在检查更新...');
  setTimeout(() => {
    MessagePlugin.success('当前已是最新版本');
  }, 1000);
};

onMounted(async () => {
  // 1. 加载配置
  try {
    const store = await load('store.json');
    const savedPath = await store.get<string>('gamePath');
    if (savedPath) {
      gamePath.value = savedPath;
    }
  } catch (err) {
    console.error('加载配置失败:', err);
  }

  // 2. 获取版本号
  try {
    appVersion.value = await getVersion();
  } catch (e) {
    console.warn('获取版本号失败，使用默认值');
  }
});
</script>

<style scoped>
.setting-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1d2129;
}

.setting-content {
  display: grid;
  gap: 20px;
  /* 限制最大宽度，防止在大屏上太宽难看 */
  max-width: 800px; 
}

.setting-card {
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.setting-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.label {
  width: 80px;
  color: #1d2129;
  font-weight: 500;
}

.path-hint {
  margin-top: 8px;
  margin-left: 96px; /* label width + gap */
  font-size: 12px;
  color: #86909c;
  word-break: break-all;
}

/* 关于卡片样式 */
.app-info {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px 0;
}

.app-text h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
  color: #1d2129;
}

.app-text p {
  margin: 0;
  color: #86909c;
  font-size: 14px;
}

.copyright {
  text-align: center;
  margin-top: 24px;
  font-size: 12px;
  color: #c5c8ce;
}
</style>