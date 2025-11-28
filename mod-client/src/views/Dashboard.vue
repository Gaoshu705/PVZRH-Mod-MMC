<template>
  <div class="dashboard-container">
    <div class="page-header">
      <div class="header-left">
        <h1>仪表盘</h1>
        <p>欢迎回来，这里是您的 MOD 管理中心</p>
      </div>
      <div class="header-right">
        <t-button theme="primary" variant="outline" @click="refreshData" :loading="loading">
          <template #icon>
            <RefreshIcon />
          </template>
          刷新数据
        </t-button>
      </div>
    </div>

    <div class="stat-grid">
      <!-- 已安装 -->
      <div class="stat-card">
        <div class="stat-icon-wrapper blue-bg">
          <AppIcon size="24px" />
        </div>
        <div class="stat-info">
          <div class="stat-label">已安装模组</div>
          <div class="stat-value">{{ stats.installed }}</div>
        </div>
      </div>

      <!-- 已启用 -->
      <div class="stat-card">
        <div class="stat-icon-wrapper green-bg">
          <CheckCircleIcon size="24px" />
        </div>
        <div class="stat-info">
          <div class="stat-label">已启用模组</div>
          <div class="stat-value">{{ stats.enabled }}</div>
        </div>
      </div>

      <!-- 在线库 -->
      <div class="stat-card">
        <div class="stat-icon-wrapper purple-bg">
          <CloudIcon size="24px" />
        </div>
        <div class="stat-info">
          <div class="stat-label">在线模组库</div>
          <div class="stat-value">{{ stats.online }}</div>
        </div>
      </div>
    </div>

    <div class="content-grid">
      <t-card title="模组状态概览" :bordered="false" class="dashboard-card">
        <div class="status-chart-container">
          <div class="chart-row">
            <span>启用率</span>
            <span class="chart-value">{{ enableRate }}%</span>
          </div>
          <t-progress theme="plump" :percentage="enableRate" :color="{ from: '#0052D9', to: '#00A870' }"
            track-color="#E7E7E7" />
          <p class="chart-desc">
            本地检测到 {{ stats.installed }} 个文件，其中 {{ stats.enabled }} 个已启用。
          </p>
        </div>
      </t-card>

      <t-card title="系统信息" :bordered="false" class="dashboard-card">
        <div class="system-info">
          <div class="info-item">
            <span class="label">游戏路径：</span>
            <span class="value text-ellipsis" :title="gamePath" @click="openGamePath">{{ gamePath || '设置游戏路径' }}</span>
          </div>
          <div class="info-item">
            <span class="label">最后更新：</span>
            <span class="value">{{ lastUpdateTime }}</span>
          </div>
          <div class="info-item">
            <span class="label">Mod框架</span>
            <span class="value">{{ modFramework }}</span>
          </div>
        </div>
      </t-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { AppIcon, CheckCircleIcon, CloudIcon, RefreshIcon } from 'tdesign-icons-vue-next';
import { MessagePlugin } from 'tdesign-vue-next';
// 引入公共方法
import { getGamePath, scanLocalMods, getModFramework } from '../utils/modUtils';
import { openPath } from '@tauri-apps/plugin-opener';
import { useRouter } from "vue-router";
const router = useRouter();


const loading = ref(false);
const gamePath = ref('');
const lastUpdateTime = ref('');
const modFramework = ref('');

const stats = ref({
  installed: 0,
  enabled: 0,
  online: 0
});

const enableRate = computed(() => {
  if (stats.value.installed === 0) return 0;
  return Math.round((stats.value.enabled / stats.value.installed) * 100);
});

const openGamePath = async () => {
  if (!gamePath.value) {
    router.push('/setting');
    MessagePlugin.warning('未设置游戏路径');
    return;
  }
  await openPath(gamePath.value);
};

// 获取所有数据
const fetchData = async () => {
  loading.value = true;
  try {
    // 1. 获取游戏路径
    const path = await getGamePath();
    gamePath.value = path || '';

    // 2. 获取本地模组统计
    const localMods = await scanLocalMods();
    stats.value.installed = localMods.length;
    stats.value.enabled = localMods.filter(mod => mod.isEnabled).length;

    // 3. 获取Mod框架
    modFramework.value = await getModFramework() || '未安装任何框架';

    // 4. 获取在线模组数量 (调用你的API)
    // 注意：这里为了速度不阻塞UI，可以不 await 或者单独处理错误
    try {
      const response = await fetch('https://mod.ehre.top/api/public/mod');
      const res = await response.json();
      if (res.code === 0 && res.data) {
        stats.value.online = res.data.length;
      }
    } catch (apiErr) {
      console.error('获取在线数量失败', apiErr);
    }

    lastUpdateTime.value = new Date().toLocaleString();
    // MessagePlugin.success('数据已更新');
  } catch (err) {
    console.error(err);
    MessagePlugin.error('获取数据失败');
  } finally {
    loading.value = false;
  }
};

const refreshData = () => {
  fetchData();
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
/* 保持原有样式不变 */
.dashboard-container {
  padding-bottom: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 700;
  color: #1d2129;
  margin: 0 0 4px 0;
}

.page-header p {
  font-size: 14px;
  color: #86909c;
  margin: 0;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: #fff;
}

.blue-bg {
  background: linear-gradient(135deg, #2b5aed 0%, #3d7fff 100%);
}

.green-bg {
  background: linear-gradient(135deg, #00a870 0%, #4cd2a2 100%);
}

.purple-bg {
  background: linear-gradient(135deg, #7b61ff 0%, #a48eff 100%);
}

.stat-info .stat-label {
  font-size: 14px;
  color: #86909c;
  margin-bottom: 4px;
}

.stat-info .stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1d2129;
  line-height: 1.2;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.dashboard-card {
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
}

.status-chart-container {
  padding: 10px 0;
}

.chart-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-weight: 500;
  color: #4e5969;
}

.chart-desc {
  margin-top: 16px;
  font-size: 13px;
  color: #86909c;
}

.system-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.info-item .label {
  color: #86909c;
  white-space: nowrap;
}

.info-item .value {
  color: #1d2129;
  font-weight: 500;
}

.text-ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 150px;
  cursor: pointer;
  color: #3070f0 !important;
  text-decoration: underline;
}
</style>