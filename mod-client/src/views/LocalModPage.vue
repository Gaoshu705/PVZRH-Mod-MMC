<template>
  <div class="page-container">
    <!-- 头部区域 -->
    <div class="page-header">
      <div class="header-content">
        <h1>本地模组管理</h1>
        <p>管理游戏目录下的插件文件 (BepInEx/plugins)</p>
      </div>
      <div class="header-actions">
        <t-button theme="default" variant="outline" @click="initData" :loading="loading">
          <template #icon><RefreshIcon /></template>
          刷新
        </t-button>
        <t-button theme="primary" variant="outline" @click="openFolder">
          <template #icon><FolderOpenIcon /></template>
          打开文件夹
        </t-button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="page-content">
      <t-card :bordered="false" class="main-card">
        <t-table
          :data="modList"
          :columns="columns"
          :loading="loading"
          row-key="name"
          stripe
          :hover="true"
          height="100%" 
          :header-affixed-top="true"
          table-layout="auto"
        >
          <!-- 状态列 -->
          <template #status="{ row }">
            <t-tag v-if="row.isEnabled" theme="success" variant="light" shape="round">
              <template #icon><CheckCircleIcon /></template>已开启
            </t-tag>
            <t-tag v-else theme="default" variant="light" shape="round">
              <template #icon><StopCircleIcon /></template>已禁用
            </t-tag>
          </template>

          <!-- 名字列 (优化显示结构) -->
          <template #name="{ row }">
            <div class="mod-info-cell">
              <!-- 图标状态区分 -->
              <div class="icon-wrapper" :class="{ 'disabled': !row.isEnabled }">
                <AppIcon v-if="row.isEnabled" />
                <StopCircleIcon v-else />
              </div>
              
              <!-- 名称显示逻辑 -->
              <div class="text-wrapper">
                <!-- 显示匹配到的中文名，如果没有则显示文件名主体 -->
                <div class="mod-title" :class="{ 'text-disabled': !row.isEnabled }">
                  {{ getMatchInfo(row.name) || formatFileName(row.name) }}
                </div>
                <!-- 如果有中文名，则在下方显示原始文件名 -->
                <div class="mod-filename" v-if="getMatchInfo(row.name)">
                  {{ row.name }}
                </div>
              </div>
            </div>
          </template>

          <!-- 操作列 -->
          <template #action="{ row }">
            <t-switch 
              v-model="row.isEnabled" 
              :loading="row.processing" 
              :custom-value="[true, false]"
              @change="handleToggle(row)" 
            />
          </template>

          <template #empty>
            <div class="empty-container">
              <t-empty description="暂无本地模组或路径未配置" />
              <t-button size="small" variant="text" @click="openFolder" style="margin-top: 10px">
                检查文件夹
              </t-button>
            </div>
          </template>
        </t-table>
      </t-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { MessagePlugin } from 'tdesign-vue-next';
import { 
  RefreshIcon, 
  FolderOpenIcon, 
  CheckCircleIcon, 
  StopCircleIcon,
  AppIcon
} from 'tdesign-icons-vue-next';
import { rename } from '@tauri-apps/plugin-fs';
import { join } from '@tauri-apps/api/path';
import { openPath } from '@tauri-apps/plugin-opener';
import { fetch } from '@tauri-apps/plugin-http';
import { scanLocalMods, getPluginsDirectory, type LocalModInfo } from '../utils/modUtils';

// 1. 类型定义优化
interface UIModItem extends LocalModInfo {
  processing: boolean;
}

interface OnlineModItem {
  englishName: string;
  modName: string;
  [key: string]: any;
}

const loading = ref(false);
const modList = ref<UIModItem[]>([]);
// 使用 Map 存储在线模组信息，Key为小写的英文名，Value为中文名
const onlineModMap = ref<Map<string, string>>(new Map());

const columns = [
  { colKey: 'name', title: '模组信息', width: '60%', ellipsis: true },
  { colKey: 'status', title: '状态', width: '15%' },
  { colKey: 'action', title: '操作', width: '25%', align: 'right' },
];

// 2. 纯净文件名提取工具 (去除后缀)
const formatFileName = (fullName: string): string => {
  return fullName.replace(/\.dll(-close)?$/i, '');
};

// 3. 获取在线模组数据 (构建 Map)
const fetchOnlineData = async () => {
  try {
    const response = await fetch('https://mod.ehre.top/api/public/mod', {
      method: 'GET',
      headers: { 'User-Agent': 'Tauri-App' }
    });
    if (!response.ok) return; // 失败静默处理，不影响本地功能

    const json = await response.json();
    const list: OnlineModItem[] = json.data || [];
    
    // 清空并重新构建 Map
    onlineModMap.value.clear();
    list.forEach(item => {
      if (item.englishName && item.modName) {
        //以此 EnglishName 的小写作为 Key
        onlineModMap.value.set(item.englishName.toLowerCase(), item.modName);
      }
    });
  } catch (error) {
    console.warn('在线模组信息获取失败，将仅显示文件名', error);
  }
};

// 4. 获取匹配的中文名
const getMatchInfo = (fileName: string): string | undefined => {
  const cleanName = formatFileName(fileName).toLowerCase();
  return onlineModMap.value.get(cleanName);
};

// 5. 初始化/刷新数据 (并行处理)
const initData = async () => {
  loading.value = true;
  try {
    // 并行执行：扫描本地 和 获取在线信息
    const [localMods, _] = await Promise.all([
      scanLocalMods(),
      fetchOnlineData()
    ]);

    // 合并数据
    modList.value = localMods
      .map(m => ({ ...m, processing: false }))
      .sort((a, b) => a.name.localeCompare(b.name));
      
  } catch (err) {
    console.error(err);
    MessagePlugin.error('加载模组列表失败');
  } finally {
    loading.value = false;
  }
};

// 6. 开关逻辑
const handleToggle = async (row: UIModItem) => {
  row.processing = true;
  try {
    const pluginsDir = await getPluginsDirectory();
    if (!pluginsDir) throw new Error('路径丢失');

    const targetEnable = row.isEnabled;
    // 逻辑优化：根据当前文件名直接判断，更加稳健
    const newFileName = targetEnable 
      ? row.fileName.replace(/-close$/, '') // 开启：去后缀
      : row.fileName + '-close';           // 关闭：加后缀
      
    // 防止重复后缀 (虽然正则已处理，但在文件名已经是正确格式时做保护)
    if (!targetEnable && row.fileName.endsWith('-close')) {
        throw new Error('文件状态异常');
    }

    const newPath = await join(pluginsDir, newFileName);
    await rename(row.fullPath, newPath);
    
    // 更新本地状态
    row.fileName = newFileName;
    row.fullPath = newPath;
    row.name = newFileName;
    
    MessagePlugin.success({ content: targetEnable ? '已启用' : '已禁用', duration: 1000 });
  } catch (error) {
    console.error(error);
    MessagePlugin.error('操作失败，可能文件被占用');
    row.isEnabled = !row.isEnabled; // 失败回滚
  } finally {
    row.processing = false;
  }
};

const openFolder = async () => {
  const dir = await getPluginsDirectory();
  if (dir) {
    try { await openPath(dir); } 
    catch (e) { MessagePlugin.warning('无法打开文件夹'); }
  } else {
    MessagePlugin.warning('请先设置游戏路径');
  }
};

onMounted(() => {
  initData();
});
</script>

<style scoped>
.page-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-shrink: 0;
}
.page-header h1 { font-size: 24px; font-weight: 600; color: #1d2129; margin: 0 0 4px 0; }
.page-header p { font-size: 14px; color: #86909c; margin: 0; }
.header-actions { display: flex; gap: 12px; }

/* 内容区域自适应 */
.page-content {
  flex: 1;
  min-height: 0; /* 允许内部滚动 */
  display: flex;
  flex-direction: column;
}

.main-card {
  height: 100%; /* 关键：撑满父容器 */
  display: flex;
  flex-direction: column;
  border-radius: 8px;
}

:deep(.t-card__body) {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 模组信息单元格样式优化 */
.mod-info-cell {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 0;
}

.icon-wrapper {
  font-size: 20px;
  color: #00a870; /* 启用色 */
  display: flex;
  align-items: center;
}
.icon-wrapper.disabled {
  color: #c5c8ce; /* 禁用色 */
}

.text-wrapper {
  display: flex;
  flex-direction: column;
  line-height: 1.4;
  overflow: hidden;
}

.mod-title {
  font-weight: 500;
  color: #1d2129;
  font-size: 14px;
}
.mod-title.text-disabled {
  color: #86909c;
  text-decoration: line-through;
}

.mod-filename {
  font-size: 12px;
  color: #86909c;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.empty-container {
  padding: 40px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>