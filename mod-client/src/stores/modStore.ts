import { ref } from 'vue';
import { fetch } from '@tauri-apps/plugin-http';

const PUBLIC_API = 'https://mod.ehre.top/api/public';

// 模组接口类型定义
export interface ModItem {
  id: string;
  modName: string;
  englishName: string;
  authorName: string;
  modDescription: string;
  iconUrl: string;
  videoUrl: string;
  supportedVersions: string;
  isPreposition: boolean;
  isModpack: boolean;
  showDirectUrl: boolean;
  downloadDirectUrl: string;
  downloadCloudUrl: string;
  version: string;
  updatedAt: string;
  frameworkName: string;
  isFeatured: boolean; // 是否为推荐模组
  downloadCount: string | number;
  viewCount: string | number;
}

export interface ApiResponse {
  code: number;
  msg: string | null;
  data: ModItem[];
}

// 全局状态
const modList = ref<ModItem[]>([]);
const isLoading = ref(false);
const lastFetchTime = ref<number | null>(null);
const error = ref<string | null>(null);

// 获取模组列表数据
export async function fetchModList(forceRefresh = false) {
  // 如果不是强制刷新且已有数据，直接返回
  if (!forceRefresh && modList.value.length > 0) {
    return modList.value;
  }

  isLoading.value = true;
  error.value = null;

  try {
    const response = await fetch(`${PUBLIC_API}/mod`, {
      method: 'GET',
      headers: { 'User-Agent': 'Tauri-App' }
    });

    if (!response.ok) throw new Error(`HTTP ${response.status}`);
    const res: ApiResponse = await response.json();

    if (res.code === 0 && res.data) {
      modList.value = res.data;
      lastFetchTime.value = Date.now();
      return modList.value;
    } else {
      throw new Error(res.msg || '获取模组列表失败');
    }
  } catch (err) {
    error.value = err instanceof Error ? err.message : '未知错误';
    throw err;
  } finally {
    isLoading.value = false;
  }
}

function applyCount(id: string, downloadCount?: string | number, viewCount?: string | number) {
  const item = modList.value.find(mod => mod.id === id);
  if (!item) return;
  if (downloadCount !== undefined && downloadCount !== null) {
    item.downloadCount = downloadCount;
  }
  if (viewCount !== undefined && viewCount !== null) {
    item.viewCount = viewCount;
  }
}

export async function reportModView(id: string) {
  try {
    const response = await fetch(`${PUBLIC_API}/mod/${id}/view`, {
      method: 'POST',
      headers: { 'User-Agent': 'Tauri-App' }
    });
    if (!response.ok) return;
    const res = await response.json();
    if (res.code === 0 && res.data) {
      applyCount(id, res.data.downloadCount, res.data.viewCount);
    }
  } catch {
    // 统计失败不影响主流程
  }
}

export async function reportModDownload(id: string) {
  try {
    const response = await fetch(`${PUBLIC_API}/mod/${id}/download`, {
      method: 'POST',
      headers: { 'User-Agent': 'Tauri-App' }
    });
    if (!response.ok) return;
    const res = await response.json();
    if (res.code === 0 && res.data) {
      applyCount(id, res.data.downloadCount, res.data.viewCount);
    }
  } catch {
    // 统计失败不影响主流程
  }
}

// 清空模组列表
export function clearModList() {
  modList.value = [];
  lastFetchTime.value = null;
  error.value = null;
}

// 导出状态和函数
export function useModListStore() {
  return {
    modList,
    isLoading,
    lastFetchTime,
    error,
    fetchModList,
    clearModList,
    reportModView,
    reportModDownload
  };
}