import { load } from '@tauri-apps/plugin-store';
import { readDir,exists } from '@tauri-apps/plugin-fs';
import { join } from '@tauri-apps/api/path';

export interface LocalModInfo {
  name: string;      // 显示名称
  fileName: string;  // 文件名
  fullPath: string;  // 绝对路径
  isEnabled: boolean; // 是否启用
}

/**
 * 获取配置中的游戏路径
 */
export const getGamePath = async (): Promise<string | null> => {
  try {
    const store = await load('store.json');
    return await store.get<string>('gamePath') ?? null;
  } catch (err) {
    console.error('无法读取配置文件:', err);
    return null;
  }
};

/**
 * 获取并保存游戏使用的mod框架
 * 优先检测 BepInEx，其次检测 MelonLoader
 */
export const getModFramework = async (): Promise<string | null> => {
  try {
    const gamePath = await getGamePath();
    if (!gamePath) return null;

    let frameworkName: string | null = null;

    // 1. 拼接可能的路径
    const bepInExPath = await join(gamePath, 'BepInEx');
    const melonLoaderPath = await join(gamePath, 'MelonLoader');

    // 2. 检测是否存在
    // 注意：exists 需要在 import 中引入
    const hasBepInEx = await exists(bepInExPath);
    
    if (hasBepInEx) {
      frameworkName = 'BepInEx';
    } else {
      // 只有 BepInEx 不存在时才去检查 MelonLoader
      const hasMelon = await exists(melonLoaderPath);
      if (hasMelon) {
        frameworkName = 'MelonLoader';
      }
    }

    // 3. 如果找到了框架，保存到 Store
    if (frameworkName) {
      const store = await load('store.json');
      await store.set('modFramework', frameworkName);
      await store.save(); // 必须调用 save 才能持久化
      // console.log('框架已更新为:', frameworkName);
    }

    return frameworkName;

  } catch (err) {
    console.error('检测 Mod 框架失败:', err);
    return null;
  }
};



/**
 * 获取 BepInEx/plugins 完整路径
 */
export const getPluginsDirectory = async (): Promise<string | null> => {
  const gamePath = await getGamePath();
  if (!gamePath) return null;
  // 根据不同的mod框架返回不同的路径
  const modFramework = await getModFramework();
  if (modFramework === 'MelonLoader') {
    return await join(gamePath, 'Mods');
  }
  // 确保跨平台路径正确拼接
  return await join(gamePath, 'BepInEx', 'plugins');
};

/**
 * 获取MelonLoader/plugins 完整路径
 */
export const getMelonLoaderPluginsDirectory = async (): Promise<string | null> => {
  const gamePath = await getGamePath();
  if (!gamePath) return null;
  // 确保跨平台路径正确拼接
  return await join(gamePath, 'Mods');
};

/**
 * 扫描本地模组列表
 * @returns 模组列表数组，如果扫描失败返回空数组
 */
export const scanLocalMods = async (): Promise<LocalModInfo[]> => {
  const pluginsDir = await getPluginsDirectory();
  if (!pluginsDir) return [];

  try {
    const entries = await readDir(pluginsDir);
    const mods: LocalModInfo[] = [];

    for (const entry of entries) {
      if (entry.isDirectory) continue;

      const name = entry.name;
      const isDll = name.endsWith('.dll');
      const isDisabled = name.endsWith('.dll-close');

      if (isDll || isDisabled) {
        mods.push({
          name: name,
          fileName: name,
          fullPath: await join(pluginsDir, name),
          isEnabled: isDll,
        });
      }
    }
    return mods;
  } catch (err) {
    console.error('扫描插件目录失败:', err);
    return [];
  }
};