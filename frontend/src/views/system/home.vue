<template>
  <div class="home-container">
    <!-- Welcome Section -->
    <el-row :gutter="20" class="welcome-row">
      <el-col :span="24">
        <el-card shadow="hover" class="welcome-card">
          <div class="welcome-content">
            <div class="user-avatar">
              <el-avatar :size="64" :src="userInfo.avatar">
                {{ userInfo.nickname?.charAt(0) }}
              </el-avatar>
            </div>
            <div class="welcome-info">
              <h2 class="greeting">{{ greeting }}，{{ userInfo.nickname }}</h2>
              <p class="welcome-text">欢迎使用Mod后台管理系统</p>
            </div>
            <div class="user-info">
              <div class="info-item">
                <span class="label">用户名：</span>
                <span class="value">{{ userInfo.username }}</span>
              </div>
              <div class="info-item">
                <span class="label">角色：</span>
                <span class="value">{{ userInfo.roles?.join(', ') || '暂无角色' }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Statistics Cards -->
    <el-row :gutter="20" class="stats-row" v-if="hasPerm('system:role:get')">
      <el-col :span="6" v-for="(item, index) in statsData" :key="index">
        <el-card shadow="hover" :body-style="{ padding: '20px' }">
          <div class="stats-card">
            <div class="stats-icon" :style="{ background: item.color }">
              <el-icon>
                <component :is="item.icon"/>
              </el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-title">{{ item.title }}</div>
              <div class="stats-value">{{ item.value }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- System Info -->
    <el-row :gutter="20" class="system-row">
      <el-col :span="12">
        <el-card shadow="hover" class="system-card">
          <template #header>
            <div class="card-header">
              <span>系统信息</span>
            </div>
          </template>
          <div class="system-info">
            <div class="info-item">
              <span class="label">系统名称：</span>
              <span class="value">Mod后台管理系统</span>
            </div>
            <div class="info-item">
              <span class="label">系统版本：</span>
              <span class="value">1.0.0</span>
            </div>
            <div class="info-item">
              <span class="label">后端框架：</span>
              <div class="value-container">
                <div>Spring Boot 3 + Spring Security</div>
                <div>MySQL + Redis</div>
              </div>
            </div>
            <div class="info-item">
              <span class="label">前端框架：</span>
              <span class="value">Vue 3 + Element Plus + Axios</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="quick-nav-card" v-if="hasPerm('system:role:get')">
          <template #header>
            <div class="card-header">
              <span>快捷导航</span>
            </div>
          </template>
          <div class="quick-nav">
            <el-button
                v-for="(nav, index) in quickNavs"
                :key="index"
                :type="nav.type"
                @click="handleNavClick(nav.path)"
            >
              <el-icon><component :is="nav.icon" /></el-icon>
              {{ nav.name }}
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
  import {ref, computed, onMounted} from 'vue'
  import {useRouter} from 'vue-router'
  import {User, Setting, Menu, Files, Edit, List, Upload} from '@element-plus/icons-vue'
  import {userApi} from '@/api/user-api'
  import {roleApi} from '@/api/role-api'
  import {menuApi} from '@/api/menu-api'
  import {fileApi} from '@/api/file-api'
  import {useUserStore} from '@/stores/user'
import {hasPerm} from "@/utils/permission.js";
  const router = useRouter()
  const userStore = useUserStore()

  // 用户信息
  const userInfo = computed(() => {
    return {
      username: userStore.username,
      nickname: userStore.nickname || userStore.username,
      avatar: userStore.avatar,
      roles: userStore.roles.map(role => role.roleName),
      gender: userStore.gender
    }
  })

  // 问候语
  const greeting = computed(() => {
    const hour = new Date().getHours()
    if (hour < 6) return '凌晨好'
    if (hour < 9) return '早上好'
    if (hour < 12) return '上午好'
    if (hour < 14) return '中午好'
    if (hour < 17) return '下午好'
    if (hour < 19) return '傍晚好'
    return '晚上好'
  })

  // Statistics Data
  const statsData = ref([
    {
      title: '总用户数',
      value: '0',
      icon: 'User',
      color: '#409EFF'
    },
    {
      title: '总角色数',
      value: '0',
      icon: 'Setting',
      color: '#67C23A'
    },
    {
      title: '总权限数',
      value: '0',
      icon: 'Menu',
      color: '#E6A23C'
    },
    {
      title: '总文件数',
      value: '0',
      icon: 'Files',
      color: '#F56C6C'
    }
  ])

  // 快捷导航
  const quickNavs = ref([
    {
      name: '用户管理',
      path: '/system/user',
      icon: 'User',
      type: 'primary'
    },
    {
      name: '角色管理',
      path: '/system/role',
      icon: 'Setting',
      type: 'success'
    },
    {
      name: '菜单管理',
      path: '/system/menu',
      icon: 'Menu',
      type: 'warning'
    },
    {
      name: '文件管理',
      path: '/system/file',
      icon: 'Files',
      type: 'danger'
    }
  ])

  // 获取统计数据
  const fetchStatsData = async () => {
    try {
      // 获取用户总数
      const userRes = await userApi.page({pageNum: 1, pageSize: 1})
      if (userRes?.data) {
        statsData.value[0].value = userRes.data.total.toString()
      }

      // 获取角色总数
      const roleRes = await roleApi.page({pageNum: 1, pageSize: 1})
      if (roleRes?.data) {
        statsData.value[1].value = roleRes.data.total.toString()
      }

      // 获取权限总数
      const menuRes = await menuApi.page({pageNum: 1, pageSize: 1})
      if (menuRes?.data) {
        statsData.value[2].value = menuRes.data.total.toString()
      }

      // 获取文件总数
      const fileRes = await fileApi.page({pageNum: 1, pageSize: 1})
      if (fileRes?.data) {
        statsData.value[3].value = fileRes.data.total.toString()
      }
    } catch (error) {
      console.error('获取统计数据失败:', error)
    }
  }

  // 导航点击处理
  const handleNavClick = (path) => {
    router.push(path)
  }

  onMounted(() => {
    fetchStatsData()
  })
</script>

<style scoped lang="scss">
  .home-container {
    padding: 20px;
    background-color: #f0f2f5;
    min-height: 100%;

    .welcome-row {
      margin-bottom: 20px;

      .welcome-card {
        .welcome-content {
          display: flex;
          align-items: center;
          padding: 20px;

          .user-avatar {
            margin-right: 24px;
          }

          .welcome-info {
            flex: 1;

            .greeting {
              font-size: 24px;
              color: #303133;
              margin: 0 0 8px 0;
            }

            .welcome-text {
              color: #909399;
              margin: 0;
            }
          }

          .user-info {
            margin-left: 40px;

            .info-item {
              margin-bottom: 8px;

              .label {
                color: #909399;
                margin-right: 8px;
              }

              .value {
                color: #303133;
                font-weight: 500;
              }
            }
          }
        }
      }
    }

    .stats-row {
      margin-bottom: 20px;

      .stats-card {
        display: flex;
        align-items: center;

        .stats-icon {
          width: 48px;
          height: 48px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;

          .el-icon {
            font-size: 24px;
            color: #fff;
          }
        }

        .stats-info {
          flex: 1;

          .stats-title {
            font-size: 14px;
            color: #909399;
            margin-bottom: 8px;
          }

          .stats-value {
            font-size: 24px;
            font-weight: bold;
          }
        }
      }
    }

    .system-row {
      .system-card, .quick-nav-card {
        .card-header {
          font-weight: bold;
        }

        .system-info {
          .info-item {
            margin-bottom: 16px;
            display: flex;

            .label {
              color: #909399;
              flex: 0 0 80px;
              text-align: right;
              margin-right: 12px;
              padding-top: 2px;
            }

            .value {
              color: #303133;
              flex: 1;
            }

            .value-container {
              flex: 1;
              color: #303133;

              div {
                line-height: 1.5;

                &:not(:last-child) {
                  margin-bottom: 4px;
                }
              }
            }
          }
        }

        .quick-nav {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 16px;

          :deep(.el-button) {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            margin: 0;

            .el-icon {
              margin-right: 8px;
            }
          }

          :deep(.el-button + .el-button) {
            margin-left: 0;
          }
        }
      }
    }
  }
</style>