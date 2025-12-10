<template>
  <div class="mod-store-container">
    <!-- 头部区域 -->
    <div class="header-section">
      <t-row class="page-header">
        <div class="header-content">
          <h1>在线模组库</h1>
          <p>浏览并下载最新的游戏模组，发现更多乐趣</p>
        </div>

        <div class="header-actions">
          <t-space>
            <t-button theme="warning" variant="outline" @click="handleImport" :loading="importing"
              :disabled="downloadingId !== ''">
              <template #icon>
                <FileImportIcon />
              </template>
              手动安装
            </t-button>

            <t-button theme="primary" variant="outline" @click="fetchMods" :loading="isLoading">
              <template #icon>
                <RefreshIcon />
              </template>
              刷新列表
            </t-button>
          </t-space>
        </div>
      </t-row>

      <t-row class="search-container" justify="end">
        <t-space>
          <t-input v-model="searchText" placeholder="搜索模组或作者..." style="width: 200px" clearable>
            <template #prefix-icon>
              <SearchIcon />
            </template>
          </t-input>

          <t-select v-model="filterFramework" placeholder="全部框架" style="width: 140px">
            <t-option value="all" label="全部框架" />
            <t-option value="1" label="BepInEx" />
            <t-option value="2" label="MelonLoader" />
          </t-select>

          <t-select v-model="filterAuthor" placeholder="全部作者" style="width: 140px">
            <t-option value="all" label="全部作者" />
            <t-option v-for="author in uniqueAuthors" :key="author" :value="author" :label="author" />
          </t-select>

          <t-select v-model="filterUpdateTime" placeholder="更新时间" style="width: 140px">
            <t-option value="all" label="全部时间" />
            <t-option value="week" label="最近一周" />
            <t-option value="month" label="最近一月" />
            <t-option value="quarter" label="最近三月" />
            <t-option value="year" label="最近一年" />
          </t-select>
        </t-space>
      </t-row>
    </div>

    <!-- 内容滚动区域 -->
    <div class="content-scroll-area">
      <!-- 加载状态 -->
      <div v-if="isLoading" class="loading-state">
        <t-loading text="正在获取模组列表..." size="small"></t-loading>
      </div>

      <!-- 左右顺序布局 -->
      <div v-else-if="filteredModList.length > 0" class="mod-grid">
        <div v-for="mod in filteredModList" :key="mod.id" class="mod-card-wrapper">
          <t-card class="mod-card" :bordered="false" hover-shadow>

            <!-- 1. 卡片头部 -->
            <div class="mod-card-header">
              <div class="mod-info-left">
                <div class="mod-title" :title="mod.modName">{{ mod.modName }}</div>
                <div class="mod-version">v{{ mod.version }}</div>
              </div>
              <div class="mod-tags">
                <t-tag v-if="mod.isFeatured" theme="danger" variant="light" size="small" class="featured-tag">
                  推荐
                </t-tag>
                <t-tag :theme="mod.frameworkName === '1' ? 'primary' : 'warning'" variant="light" size="small"
                  v-if="mod.frameworkName">
                  {{ mod.frameworkName === '1' ? 'BepInEx' : 'MelonLoader' }}
                </t-tag>
              </div>
            </div>

            <!-- 2. 视频预览 (可选) -->
            <div v-if="mod.videoUrl" class="video-wrapper">
              <iframe :src="mod.videoUrl" scrolling="no" border="0" frameborder="no" framespacing="0"
                allowfullscreen="true"
                sandbox="allow-top-navigation allow-same-origin allow-forms allow-scripts"></iframe>
            </div>

            <!-- 3. 内容区域 -->
            <div class="mod-card-body">
              <p class="description" :title="mod.modDescription">
                {{ mod.modDescription || '暂无描述' }}
              </p>

              <div class="info-footer">
                <div class="meta-row">
                  <div class="meta-item author">
                    <UserIcon size="14px" />
                    <span>{{ mod.authorName }}</span>
                  </div>
                  <div class="meta-item date">
                    <TimeIcon size="14px" />
                    <span>{{ formatDate(mod.updatedAt) }}</span>
                  </div>
                </div>

                <div class="support-info">
                  <span class="label">游戏版本:</span>
                  <span class="value">{{ mod.supportedVersions }}</span>
                </div>
              </div>
            </div>

            <!-- 4. 底部操作栏 -->
            <template #footer>
              <div class="mod-card-actions">
                <!-- 下载进度 -->
                <div v-if="downloadingId === mod.id" class="download-progress-area">
                  <div class="progress-header">
                    <span class="status-text">
                      <t-loading size="small" v-if="installStatus === '下载中'"
                        style="margin-right:4px; transform: scale(0.8);" />
                      {{ installStatus }}
                    </span>
                    <span class="percentage">{{ downloadProgress }}%</span>
                  </div>
                  <t-progress theme="plump" :percentage="downloadProgress"
                    :status="downloadProgress === 100 ? 'success' : 'active'" size="small" />
                  <t-button theme="danger" variant="text" size="small" block style="margin-top: 8px"
                    @click="cancelDownload">
                    取消
                  </t-button>
                </div>

                <!-- 按钮组 -->
                <template v-else>
                  <t-button v-if="mod.showDirectUrl" theme="primary" block :disabled="downloadingId !== '' || importing"
                    @click="handleDownload(mod)">
                    <template #icon>
                      <DownloadIcon />
                    </template>
                    安装
                  </t-button>

                  <t-button v-if="mod.downloadCloudUrl" theme="default" variant="outline" block
                    :disabled="downloadingId !== '' || importing" @click="handleCloudLink(mod)">
                    <template #icon>
                      <CloudDownloadIcon />
                    </template>
                    网盘
                  </t-button>
                </template>
              </div>
            </template>
          </t-card>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <t-empty :description="modList.length > 0 ? '未找到匹配的模组' : '暂无在线模组'" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { MessagePlugin, NotifyPlugin } from 'tdesign-vue-next';
import { RefreshIcon, DownloadIcon, CloudDownloadIcon, UserIcon, TimeIcon, FileImportIcon, SearchIcon } from 'tdesign-icons-vue-next';
import { openUrl } from '@tauri-apps/plugin-opener';
import { load } from '@tauri-apps/plugin-store';
import { readFile } from '@tauri-apps/plugin-fs';
import { fetch } from '@tauri-apps/plugin-http';
import { open } from '@tauri-apps/plugin-dialog';
// 假设 utils 路径依然有效，请确保这两个函数存在
import { getModFramework, extractZipToGameDir } from '../utils/modUtils';
// 导入全局状态管理
import { useModListStore, type ModItem } from '../stores/modStore';

// --- 状态变量 ---
const importing = ref(false);
const searchText = ref('');
const filterFramework = ref('all');
const filterAuthor = ref('all');
const filterUpdateTime = ref('all');

// 使用全局状态管理
const { modList, isLoading, fetchModList } = useModListStore();

// 下载状态
const downloadingId = ref('');
const downloadProgress = ref(0);
const installStatus = ref('下载中');
const abortController = ref<AbortController | null>(null);

// --- 计算属性：筛选逻辑 ---
// 获取唯一作者列表
const uniqueAuthors = computed(() => {
  const authors = new Set(modList.value.map(mod => mod.authorName));
  return Array.from(authors).sort();
});

// 筛选后的模组列表
const filteredModList = computed(() => {
  // 先进行筛选
  const filtered = modList.value.filter(mod => {
    const matchFramework = filterFramework.value === 'all' || mod.frameworkName === filterFramework.value;
    const matchAuthor = filterAuthor.value === 'all' || mod.authorName === filterAuthor.value;
    
    const keyword = searchText.value.trim().toLowerCase();
    const matchText = !keyword ||
      mod.modName.toLowerCase().includes(keyword) ||
      mod.authorName.toLowerCase().includes(keyword);
    
    // 更新时间筛选
    let matchUpdateTime = true;
    if (filterUpdateTime.value !== 'all' && mod.updatedAt) {
      const modDate = new Date(mod.updatedAt);
      const now = new Date();
      const diffTime = now.getTime() - modDate.getTime();
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      switch (filterUpdateTime.value) {
        case 'week':
          matchUpdateTime = diffDays <= 7;
          break;
        case 'month':
          matchUpdateTime = diffDays <= 30;
          break;
        case 'quarter':
          matchUpdateTime = diffDays <= 90;
          break;
        case 'year':
          matchUpdateTime = diffDays <= 365;
          break;
      }
    }
    
    return matchFramework && matchAuthor && matchText && matchUpdateTime;
  });
  
  // 然后排序，推荐模组(isFeatured=true)排在最前面
  return filtered.sort((a, b) => {
    // 如果a是推荐模组而b不是，a排在前面
    if (a.isFeatured && !b.isFeatured) return -1;
    // 如果b是推荐模组而a不是，b排在前面
    if (!a.isFeatured && b.isFeatured) return 1;
    // 如果都是推荐模组或都不是推荐模组，保持原有顺序
    return 0;
  });
});

// --- 工具函数 ---
const formatDate = (dateStr: string) => dateStr ? dateStr.split(' ')[0] : '';

// --- 核心业务逻辑 ---

// 1. 获取列表
const fetchMods = async () => {
  try {
    await fetchModList(true); // 强制刷新
  } catch (error) {
    console.error(error);
    MessagePlugin.error('无法连接到模组服务器');
  }
};

// 2. 手动导入
const handleImport = async () => {
  try {
    const store = await load('store.json');
    const gamePath = await store.get<string>('gamePath');
    if (!gamePath) {
      MessagePlugin.error('请先在设置中配置游戏根目录');
      return;
    }

    const selectedPath = await open({
      multiple: false,
      directory: false,
      filters: [{ name: 'Mod压缩包', extensions: ['zip'] }]
    });

    if (!selectedPath) return;

    importing.value = true;
    const loadingMsg = MessagePlugin.loading('正在安装模组...', 0);

    try {
      const fileData = await readFile(selectedPath as string);
      await extractZipToGameDir(fileData, gamePath);
      MessagePlugin.close(loadingMsg);
      NotifyPlugin.success({ title: '手动安装成功', content: '模组已解压至游戏目录' });
    } catch (err: any) {
      MessagePlugin.close(loadingMsg);
      NotifyPlugin.error({ title: '安装失败', content: err.message });
    } finally {
      importing.value = false;
    }
  } catch (err) {
    console.error(err);
    importing.value = false;
  }
};

// 3. 在线下载与安装
const cancelDownload = () => {
  if (abortController.value) {
    abortController.value.abort();
    abortController.value = null;
  }
  downloadingId.value = '';
  MessagePlugin.info('已取消安装');
};

const handleDownload = async (mod: ModItem) => {
  if (!mod.downloadDirectUrl) return;

  const modFramework = await getModFramework();
  const fcode = modFramework === 'BepInEx' ? '1' : '2';

  if (mod.frameworkName !== fcode) {
    MessagePlugin.warning(`环境不匹配：当前是 ${modFramework}`);
    return;
  }

  downloadingId.value = mod.id;
  downloadProgress.value = 0;
  installStatus.value = '下载中';
  abortController.value = new AbortController();

  try {
    const store = await load('store.json');
    const gamePath = await store.get<string>('gamePath');
    if (!gamePath) throw new Error('未设置游戏路径');

    const response = await fetch(mod.downloadDirectUrl, {
      method: 'GET',
      signal: abortController.value.signal,
    });

    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    if (!response.body) throw new Error('无响应内容');

    const contentLength = response.headers.get('content-length');
    const totalLength = contentLength ? parseInt(contentLength, 10) : 0;
    const reader = response.body.getReader();
    const chunks: Uint8Array[] = [];
    let receivedLength = 0;

    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      chunks.push(value);
      receivedLength += value.length;
      if (totalLength > 0) {
        downloadProgress.value = Math.min(Math.round((receivedLength / totalLength) * 95), 99);
      }
    }

    installStatus.value = '解压中';
    const allChunks = new Uint8Array(receivedLength);
    let position = 0;
    for (const chunk of chunks) {
      allChunks.set(chunk, position);
      position += chunk.length;
    }

    await extractZipToGameDir(allChunks, gamePath);

    downloadProgress.value = 100;
    NotifyPlugin.success({ title: '安装成功', content: `${mod.modName} 已就绪` });

  } catch (err: any) {
    if (!err.message.includes('aborted') && !err.message.includes('canceled')) {
      NotifyPlugin.error({ title: '安装失败', content: err.message });
    }
  } finally {
    if (!abortController.value?.signal.aborted) {
      downloadingId.value = '';
    }
    abortController.value = null;
  }
};

const handleCloudLink = (mod: ModItem) => {
  if (mod.downloadCloudUrl) openUrl(mod.downloadCloudUrl);
};

// --- 生命周期 ---
onMounted(async () => {
  try {
    await fetchModList(); // 首次加载，不强制刷新
  } catch (error) {
    console.error('首次加载模组列表失败:', error);
  }
});

onUnmounted(() => {
  if (abortController.value) abortController.value.abort();
});
</script>

<style scoped>
.mod-store-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  /* background-color: var(--td-bg-color-container); */
}

.header-section {
  flex-shrink: 0;
  /* background-color: var(--td-bg-color-container); */
  z-index: 10;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 4px 10px 4px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: var(--td-text-color-primary);
  margin: 0 0 4px 0;
}

.page-header p {
  font-size: 14px;
  color: var(--td-text-color-secondary);
  margin: 0;
}

.search-container {
  padding: 0 4px 16px 4px;
}

.content-scroll-area {
  flex: 1;
  overflow-y: auto;
  padding: 4px 4px 20px 4px;
  /* 隐藏滚动条但保留功能 */
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.content-scroll-area::-webkit-scrollbar {
  display: none;
}

/* 左右顺序布局 */
.mod-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
  padding: 16px;
}

.mod-card-wrapper {
  /* 不需要特殊样式，网格布局会自动处理 */
}

/* 响应式调整 */
@media (max-width: 600px) {
  .mod-grid {
    grid-template-columns: 1fr;
    padding: 8px;
  }
}

@media (min-width: 601px) and (max-width: 900px) {
  .mod-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 901px) and (max-width: 1200px) {
  .mod-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1201px) {
  .mod-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

/* 卡片样式优化 */
.mod-card {
  border-radius: 8px;
  border: 1px solid var(--td-component-stroke);
  transition: all 0.25s ease;
  background: var(--td-bg-color-container);
}

.mod-card:hover {
  transform: translateY(-4px);
  border-color: var(--td-brand-color-focus);
}

.mod-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-bottom: 8px;
  gap: 8px;
}

.mod-tags {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: flex-end;
}

.featured-tag {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
  100% {
    opacity: 1;
  }
}

.mod-info-left {
  flex: 1;
  min-width: 0;
}

.mod-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--td-text-color-primary);
  margin-bottom: 2px;
  /* 多行截断 */
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  line-height: 1.4;
}

.mod-version {
  font-size: 12px;
  color: var(--td-text-color-placeholder);
  font-family: monospace;
}

/* 视频容器 */
.video-wrapper {
  position: relative;
  width: 100%;
  padding-bottom: 56.25%;
  background: #000;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 12px;
  border: 1px solid var(--td-component-stroke);
}

.video-wrapper iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.description {
  font-size: 13px;
  color: var(--td-text-color-secondary);
  line-height: 1.6;
  margin-bottom: 16px;
  /* 描述文本允许长一点，瀑布流可以容纳 */
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 4;
  overflow: hidden;
}

.info-footer {
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px dashed var(--td-component-stroke);
}

.meta-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--td-text-color-placeholder);
}

.support-info {
  background: var(--td-bg-color-secondarycontainer);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  display: flex;
  justify-content: space-between;
}

/* 覆盖 TDesign Card Padding */
:deep(.t-card__body) {
  padding: 16px;
  display: flex;
  flex-direction: column;
}

:deep(.t-card__footer) {
  padding: 12px 16px;
}

.mod-card-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

/* 下载进度条样式 */
.download-progress-area {
  grid-column: span 2;
  background: var(--td-brand-color-light);
  border-radius: 6px;
  padding: 10px;
  border: 1px solid var(--td-brand-color-focus);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  margin-bottom: 6px;
  color: var(--td-brand-color);
}

.percentage {
  font-weight: 700;
  font-family: monospace;
}

.loading-state,
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 100px 0;
  color: var(--td-text-color-secondary);
}
</style>