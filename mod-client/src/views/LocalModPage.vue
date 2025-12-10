<template>
  <div class="page-container">
    <!-- 头部区域 -->
    <div class="page-header">
      <div class="header-content">
        <h1>本地模组管理</h1>
        <p>管理游戏目录下的插件文件</p>
      </div>
      <div class="header-actions">
        <!-- 新增：批量操作按钮组 -->
        <div class="batch-actions" v-if="selectedRowKeys.length > 0">
          <t-button theme="success" variant="outline" @click="handleBatch(true)" :loading="batchLoading">
            <template #icon>
              <CheckCircleIcon />
            </template>
            开启
          </t-button>
          <t-button theme="danger" variant="outline" @click="handleBatch(false)" :loading="batchLoading">
            <template #icon>
              <StopCircleIcon />
            </template>
            关闭
          </t-button>
          <t-divider layout="vertical" />
        </div>

        <t-button theme="default" variant="outline" @click="initData" :loading="loading">
          <template #icon>
            <RefreshIcon />
          </template>
          刷新
        </t-button>
        <t-button theme="primary" variant="outline" @click="openFolder">
          <template #icon>
            <FolderOpenIcon />
          </template>
          打开文件夹
        </t-button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="page-content">
      <t-card :bordered="false" class="main-card">
        <!-- 修改：添加 selected-row-keys 和 columns绑定 -->
        <t-table :data="modList" :columns="columns" :loading="loading" row-key="name" stripe :hover="true" height="100%"
          :header-affixed-top="true" table-layout="auto" v-model:selected-row-keys="selectedRowKeys">
          <!-- 状态列 -->
          <template #status="{ row }">
            <t-tag v-if="row.isEnabled" theme="success" variant="light" shape="round">
              <template #icon>
                <CheckCircleIcon />
              </template>已开启
            </t-tag>
            <t-tag v-else theme="default" variant="light" shape="round">
              <template #icon>
                <StopCircleIcon />
              </template>已禁用
            </t-tag>
          </template>

          <!-- 名字列 -->
          <template #name="{ row }">
            <div class="mod-info-cell">
              <div class="icon-wrapper" :class="{ 'disabled': !row.isEnabled }">
                <AppIcon v-if="row.isEnabled" />
                <StopCircleIcon v-else />
              </div>
              <div class="text-wrapper">
                <div class="mod-title" :class="{ 'text-disabled': !row.isEnabled }">
                  {{ getMatchInfo(row.name) || formatFileName(row.name) }}
                </div>
                <div class="mod-filename" v-if="getMatchInfo(row.name)">
                  {{ row.name }}
                </div>
              </div>
            </div>
          </template>

          <!-- 操作列 -->
          <template #action="{ row }">
            <t-switch v-model="row.isEnabled" :loading="row.processing" :custom-value="[true, false]"
              @change="handleToggle(row)" />
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

interface UIModItem extends LocalModInfo {
  processing: boolean;
}

interface OnlineModItem {
  englishName: string;
  modName: string;
  [key: string]: any;
}

const loading = ref(false);
const batchLoading = ref(false); // 批量操作loading
const modList = ref<UIModItem[]>([]);
const onlineModMap = ref<Map<string, string>>(new Map());

// 新增：选中行的 Keys
const selectedRowKeys = ref<string[]>([]);

// 修改：添加多选列配置
const columns = [
  { colKey: 'row-select', type: 'multiple', width: '8%', align: 'center' }, // 新增多选列
  { colKey: 'name', title: '模组信息', width: '57%', ellipsis: true },
  { colKey: 'status', title: '状态', width: '10%' },
  { colKey: 'action', title: '操作', width: '25%', align: 'right' },
];

const formatFileName = (fullName: string): string => {
  return fullName.replace(/\.dll(-close)?$/i, '');
};

const fetchOnlineData = async () => {
  try {
    const response = await fetch('https://mod.ehre.top/api/public/mod', {
      method: 'GET',
      headers: { 'User-Agent': 'Tauri-App' }
    });
    if (!response.ok) return;
    const json = await response.json();
    const list: OnlineModItem[] = json.data || [];
    onlineModMap.value.clear();
    list.forEach(item => {
      if (item.englishName && item.modName) {
        onlineModMap.value.set(item.englishName.toLowerCase(), item.modName);
      }
    });
  } catch (error) {
    console.warn('在线模组信息获取失败', error);
  }
};

const getMatchInfo = (fileName: string): string | undefined => {
  const cleanName = formatFileName(fileName).toLowerCase();
  return onlineModMap.value.get(cleanName);
};

const initData = async () => {
  loading.value = true;
  selectedRowKeys.value = []; // 刷新时清空选中
  try {
    const [localMods, _] = await Promise.all([
      scanLocalMods(),
      fetchOnlineData()
    ]);
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

// --- 新增：核心重命名逻辑抽离 ---
// 返回值: boolean (是否成功)
const executeModStateChange = async (row: UIModItem, targetEnable: boolean): Promise<boolean> => {
  try {
    const pluginsDir = await getPluginsDirectory();
    if (!pluginsDir) throw new Error('路径丢失');

    // 如果状态已经是目标状态，直接跳过（视为成功）
    if (row.isEnabled === targetEnable) return true;

    const newFileName = targetEnable
      ? row.fileName.replace(/-close$/, '')
      : row.fileName + '-close';

    // 安全检查
    if (!targetEnable && row.fileName.endsWith('-close')) return true;

    const newPath = await join(pluginsDir, newFileName);
    await rename(row.fullPath, newPath);

    // 更新内存对象状态 (UI更新依赖)
    row.fileName = newFileName;
    row.fullPath = newPath;
    row.name = newFileName;
    row.isEnabled = targetEnable;

    return true;
  } catch (error) {
    console.error(`Failed to toggle ${row.name}:`, error);
    return false;
  }
};

// 修改：单条切换逻辑使用通用函数
const handleToggle = async (row: UIModItem) => {
  row.processing = true;
  // 注意：Switch 组件已经修改了 v-model (row.isEnabled)，这里 target 是当前值
  const targetState = row.isEnabled;

  // 先把状态回滚，因为 executeModStateChange 会负责设置状态
  // 或者为了 UI 响应更快，我们在失败时回滚。
  // 这里的逻辑稍微调整：因为 Switch 点击瞬间值已经变了
  // 如果我们想要精确控制，最好手动处理 rename

  // 恢复 Switch 的变动，由 execute 函数全权接管
  row.isEnabled = !targetState;

  const success = await executeModStateChange(row, targetState);

  if (success) {
    MessagePlugin.success({ content: targetState ? '已启用' : '已禁用', duration: 1000 });
  } else {
    MessagePlugin.error('操作失败');
    // 状态已经在 executeModStateChange 内部通过引用修改了，
    // 或者失败时没修改，所以这里不需要额外回滚，只需处理加载状态
  }
  row.processing = false;
};

// --- 新增：批量处理逻辑 ---
const handleBatch = async (enable: boolean) => {
  if (selectedRowKeys.value.length === 0) return;

  batchLoading.value = true;
  let successCount = 0;
  let failCount = 0;

  try {
    // 筛选出需要被操作的行（即选中 且 状态与目标不一致的）
    const targets = modList.value.filter(item =>
      selectedRowKeys.value.includes(item.name) && item.isEnabled !== enable
    );

    if (targets.length === 0) {
      MessagePlugin.info('选中项已处于目标状态');
      batchLoading.value = false;
      return;
    }

    // 并行执行所有重命名操作
    const results = await Promise.all(
      targets.map(row => {
        row.processing = true; // 开启单行 loading
        return executeModStateChange(row, enable).then(res => {
          row.processing = false;
          return res;
        });
      })
    );

    successCount = results.filter(r => r).length;
    failCount = results.filter(r => !r).length;

    // 清空选中状态，因为文件名变了，key 也会变，必须重置
    selectedRowKeys.value = [];

    // 重新排序或刷新列表以确保一致性 (因为名字变了，排序可能乱了)
    // 简单起见，直接重新获取一次列表最稳妥
    await initData();

    if (failCount === 0) {
      MessagePlugin.success(`批量操作成功 (${successCount} 个)`);
    } else {
      MessagePlugin.warning(`操作完成：成功 ${successCount} 个，失败 ${failCount} 个`);
    }

  } catch (e) {
    MessagePlugin.error('批量操作发生错误');
  } finally {
    batchLoading.value = false;
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

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  color: #1d2129;
  margin: 0 0 4px 0;
}

.page-header p {
  font-size: 14px;
  color: #86909c;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* 内容区域自适应 */
.page-content {
  flex: 1;
  min-height: 0;
  /* 允许内部滚动 */
  display: flex;
  flex-direction: column;
}

.main-card {
  height: 100%;
  /* 关键：撑满父容器 */
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
  color: #00a870;
  /* 启用色 */
  display: flex;
  align-items: center;
}

.icon-wrapper.disabled {
  color: #c5c8ce;
  /* 禁用色 */
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

/* 简单的布局样式补充 */
.header-actions {
  display: flex;
  gap: 12px;
}

.batch-actions {
  display: flex;
  gap: 8px;
}
</style>