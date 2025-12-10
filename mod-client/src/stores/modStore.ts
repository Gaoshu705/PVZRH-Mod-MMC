import { ref } from 'vue';
import { fetch } from '@tauri-apps/plugin-http';

// 模组接口类型定义
export interface ModItem {
  id: string;
  modName: string;
  englishName: string;
  authorName: string;
  modDescription: string;
  videoUrl: string;
  supportedVersions: string;
  showDirectUrl: boolean;
  downloadDirectUrl: string;
  downloadCloudUrl: string;
  version: string;
  updatedAt: string;
  frameworkName: string;
  isFeatured: boolean; // 是否为推荐模组
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
    const response = await fetch('https://mod.ehre.top/api/public/mod', {
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
    clearModList
  };
}