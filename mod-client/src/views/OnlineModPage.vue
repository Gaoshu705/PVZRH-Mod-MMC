<template>
  <div class="mod-store-container">
    <div class="page-header">
      <div class="header-content">
        <h1>在线模组库</h1>
        <p>浏览并下载最新的游戏模组，发现更多乐趣</p>
      </div>
      <div class="header-actions">
        <t-button theme="primary" variant="outline" @click="fetchMods" :loading="loading">
          <template #icon><RefreshIcon /></template>
          刷新列表
        </t-button>
      </div>
    </div>

    <div v-if="loading" class="loading-state">
      <t-loading text="正在获取模组列表..." size="small"></t-loading>
    </div>

    <div v-else class="mod-grid">
      <t-card 
        v-for="mod in modList" 
        :key="mod.id" 
        class="mod-card"
        :bordered="false"
        hover-shadow
      >
        <div class="mod-card-header">
          <div class="mod-title" :title="mod.modName">{{ mod.modName }}</div>
          <t-space size="small">
            <t-tag theme="warning" variant="light" size="small" v-if="mod.frameworkName">{{ mod.frameworkName === '1' ? 'BepInEx' : 'MelonLoader' }}</t-tag>
            <t-tag theme="primary" variant="light" size="small">{{ mod.version }}</t-tag>
          </t-space>
        </div>
        
        <div class="mod-card-body">
          <p class="description" :title="mod.modDescription">
            {{ mod.modDescription || '暂无描述' }}
          </p>
          <div class="meta-info">
            <div class="meta-item">
              <UserIcon size="14px" />
              <span>{{ mod.authorName }}</span>
            </div>
            <div class="meta-item">
              <TimeIcon size="14px" />
              <span>{{ formatDate(mod.updatedAt) }}</span>
            </div>
          </div>
          <div class="support-info">
            <span class="label">支持版本：</span>
            <span class="value">{{ mod.supportedVersions }}</span>
          </div>
        </div>

        <template #footer>
          <!-- 下载区域 -->
          <div class="mod-card-actions">
            
            <!-- 如果当前模组正在下载，显示进度条和取消按钮 -->
            <div v-if="downloadingId === mod.id" class="download-progress-area">
              <div class="progress-info">
                <span class="status-text">{{ installStatus }}</span>
                <!-- 仅在下载阶段显示百分比 -->
                <span v-if="installStatus === '下载中'" class="percentage">{{ downloadProgress }}%</span>
              </div>
              <t-progress 
                theme="plump" 
                :percentage="downloadProgress" 
                :status="downloadProgress === 100 ? 'success' : 'active'"
                size="small"
              />
              <t-button 
                theme="danger" 
                variant="text" 
                size="small" 
                block 
                style="margin-top: 8px"
                @click="cancelDownload"
              >
                取消安装
              </t-button>
            </div>

            <!-- 正常状态显示操作按钮 -->
            <template v-else>
              <t-button 
                theme="primary" 
                block 
                :disabled="downloadingId !== ''"
                @click="handleDownload(mod)"
              >
                <template #icon><DownloadIcon /></template>
                下载并安装
              </t-button>
              
              <t-button 
                v-if="mod.downloadCloudUrl" 
                theme="default" 
                variant="dashed" 
                block 
                :disabled="downloadingId !== ''"
                @click="handleCloudLink(mod)"
              >
                <template #icon><CloudDownloadIcon /></template>
                网盘链接
              </t-button>
            </template>

          </div>
        </template>
      </t-card>
    </div>
    
    <div v-if="!loading && modList.length === 0" class="empty-state">
      <t-empty description="暂无在线模组" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { MessagePlugin, NotifyPlugin } from 'tdesign-vue-next';
import { RefreshIcon, DownloadIcon, CloudDownloadIcon, UserIcon, TimeIcon } from 'tdesign-icons-vue-next';
import { openUrl } from '@tauri-apps/plugin-opener';
import { getModFramework } from '../utils/modUtils';
import JSZip from 'jszip';
import { load } from '@tauri-apps/plugin-store';
import { writeFile, mkdir } from '@tauri-apps/plugin-fs';
import { join, dirname } from '@tauri-apps/api/path';
import { fetch } from '@tauri-apps/plugin-http';

// 接口定义
interface ModItem {
  id: string;
  modName: string;
  authorName: string;
  modDescription: string;
  supportedVersions: string;
  downloadDirectUrl: string; 
  downloadCloudUrl: string; 
  version: string;
  updatedAt: string;
  frameworkName: string;
}

interface ApiResponse {
  code: number;
  msg: string | null;
  data: ModItem[];
}

const loading = ref(false);
const modList = ref<ModItem[]>([]);

// 下载相关状态
const downloadingId = ref('');
const downloadProgress = ref(0);
const installStatus = ref('下载中'); // '下载中' | '解压中'
const abortController = ref<AbortController | null>(null);

// 获取模组列表
const fetchMods = async () => {
  loading.value = true;
  try {
    const response = await fetch('https://mod.ehre.top/api/public/mod', {
      method: 'GET',
      headers: { 'User-Agent': 'Tauri-App' }
    });

    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    const res: ApiResponse = await response.json();
    
    if (res.code === 0 && res.data) {
      modList.value = res.data;
    } else {
      MessagePlugin.error(res.msg || '获取模组列表失败');
    }
  } catch (error) {
    console.error(error);
    MessagePlugin.error('网络请求失败，请检查网络');
  } finally {
    loading.value = false;
  }
};

const formatDate = (dateStr: string) => dateStr ? dateStr.split(' ')[0] : '';

// 取消下载
const cancelDownload = () => {
  if (abortController.value) {
    abortController.value.abort();
    abortController.value = null;
  }
  downloadingId.value = '';
  downloadProgress.value = 0;
  installStatus.value = '';
  MessagePlugin.info('已取消安装');
};

// 下载并安装
const handleDownload = async (mod: ModItem) => {
  if (!mod.downloadDirectUrl) {
    MessagePlugin.warning('该模组没有直链下载地址');
    return;
  }

  // 1. 检查框架兼容性
  const modFramework = await getModFramework();
  const fcode = modFramework === 'BepInEx' ? '1' : '2';
  // const needFramework = fcode === '1' ? 'MelonLoader' : 'BepInEx';
  // 注意：这里逻辑似乎反了，如果 fcode是1(BepInEx)，需要安装BepInEx的mod(frameworkName=1)。
  // 如果 mod.frameworkName != fcode，说明不匹配。
  // 假设 1=BepInEx, 2=MelonLoader。当前环境是 BepInEx(1)，但Mod是 MelonLoader(2)，则报错。
  if (mod.frameworkName !== fcode) {
     // 优化提示文案
     const currentEnv = modFramework;
     const targetEnv = mod.frameworkName === '1' ? 'BepInEx' : 'MelonLoader';
     MessagePlugin.warning(`环境不匹配：当前是 ${currentEnv}，该模组需要 ${targetEnv}`);
     return;
  }

  // 2. 初始化下载状态
  downloadingId.value = mod.id;
  downloadProgress.value = 0;
  installStatus.value = '下载中';
  abortController.value = new AbortController();

  try {
    const store = await load('store.json');
    const gamePath = await store.get<string>('gamePath');
    if (!gamePath) throw new Error('未设置游戏路径');

    // 3. 发起请求 (带 signal 用于取消)
    const response = await fetch(mod.downloadDirectUrl, {
      method: 'GET',
      signal: abortController.value.signal,
      connectTimeout: 30000,
    });

    if (!response.ok) throw new Error(`HTTP ${response.status}`);

    // 4. 获取总大小
    const contentLength = response.headers.get('content-length');
    const totalLength = contentLength ? parseInt(contentLength, 10) : 0;

    // 5. 读取流并计算进度
    if (!response.body) throw new Error('无法读取响应流');
    
    const reader = response.body.getReader();
    const chunks: Uint8Array[] = [];
    let receivedLength = 0;

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;

      chunks.push(value);
      receivedLength += value.length;

      // 更新进度条
      if (totalLength > 0) {
        // 预留 5% 给解压过程
        const progress = Math.round((receivedLength / totalLength) * 95);
        downloadProgress.value = progress;
      }
    }

    // 下载完成，开始解压
    installStatus.value = '解压中';
    downloadProgress.value = 95;

    // 6. 组合二进制数据
    const allChunks = new Uint8Array(receivedLength);
    let position = 0;
    for (const chunk of chunks) {
      allChunks.set(chunk, position);
      position += chunk.length;
    }

    // 7. 解压逻辑
    const zip = new JSZip();
    const loadedZip = await zip.loadAsync(allChunks.buffer);

    for (const [relativePath, fileEntry] of Object.entries(loadedZip.files)) {
      const targetPath = await join(gamePath, relativePath);

      if (fileEntry.dir) {
        await mkdir(targetPath, { recursive: true });
      } else {
        const parentDir = await dirname(targetPath);
        await mkdir(parentDir, { recursive: true });
        const content = await fileEntry.async('uint8array');
        await writeFile(targetPath, content);
      }
    }

    // 8. 完成
    downloadProgress.value = 100;
    NotifyPlugin.success({ title: '安装成功', content: `${mod.modName} 已就绪`, duration: 3000 });

  } catch (err: any) {
    // 如果是用户主动取消，不报错
    if (err.name === 'AbortError' || err.toString().includes('aborted') || err.message.includes('canceled')) {
      console.log('用户取消下载');
    } else {
      console.error(err);
      NotifyPlugin.error({ title: '安装失败', content: err.message || '未知错误' });
    }
  } finally {
    // 只有在非取消状态下（或者出错时）重置，避免UI闪烁
    if (!abortController.value?.signal.aborted) {
        downloadingId.value = '';
    }
    abortController.value = null;
  }
};

const handleCloudLink = (mod: ModItem) => {
  if (mod.downloadCloudUrl) openUrl(mod.downloadCloudUrl);
};

onMounted(() => {
  fetchMods();
});

onUnmounted(() => {
  // 组件销毁时取消正在进行的下载
  if (abortController.value) {
    abortController.value.abort();
  }
});
</script>

<style scoped>
/* 保持原有布局样式 */
.mod-store-container { height: 100%; display: flex; flex-direction: column; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.page-header h1 { font-size: 24px; font-weight: 600; color: #1d2129; margin: 0 0 4px 0; }
.page-header p { font-size: 14px; color: #86909c; margin: 0; }
.mod-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; padding-bottom: 20px; }
.mod-card { border-radius: 8px; transition: transform 0.2s; display: flex; flex-direction: column; }
.mod-card:hover { transform: translateY(-2px); }
.mod-card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.mod-title { font-size: 16px; font-weight: 600; color: #1d2129; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 70%; }
.mod-card-body { flex: 1; }
.description { color: #4e5969; font-size: 14px; line-height: 1.5; height: 42px; margin-bottom: 16px; display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden; }
.meta-info { display: flex; gap: 16px; font-size: 12px; color: #86909c; margin-bottom: 8px; }
.meta-item { display: flex; align-items: center; gap: 4px; }
.support-info { font-size: 12px; background: #f7f8fa; padding: 4px 8px; border-radius: 4px; display: inline-block; color: #4e5969; }
.support-info .label { color: #86909c; }
.mod-card-actions { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; padding-top: 8px; }
.loading-state, .empty-state { display: flex; justify-content: center; align-items: center; padding: 60px 0; }

/* 新增：下载进度区域样式 */
.download-progress-area {
  grid-column: 1 / -1; /* 占满整行 */
  background: #f7f8fa;
  padding: 12px;
  border-radius: 6px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #4e5969;
  margin-bottom: 4px;
}

.percentage {
  font-weight: 600;
  color: #0052D9;
}
</style>