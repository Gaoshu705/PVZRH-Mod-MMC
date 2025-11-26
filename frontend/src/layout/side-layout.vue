<template>
  <el-container class="mod-layout" style="height: 100%">
    <el-aside class="mod-layout-aside"
              width="auto">
      <SideMenu :collapse="collapse"/>
    </el-aside>
    <el-container class="mod-layout-container" :style="`height: ${windowHeight}px`">
      <el-header class="mod-layout-header">
        <el-row class="mod-layout-header-action">
          <el-col class="mod-layout-header-left" :span="12">
            <span class="collapse-button">
              <el-icon v-if="collapse" @click="() => (collapse = !collapse)">
                <expand/>
              </el-icon>
              <el-icon v-else @click="() => (collapse = !collapse)">
                <fold/>
              </el-icon>
            </span>
            <el-tooltip content="首页" placement="bottom">
              <span class="home-button" @click="goHome">
                <el-icon>
                  <home-filled/>
                </el-icon>
              </span>
            </el-tooltip>
            <span class="menu-breadcrumb" v-if="breadCrumbShow">
              <Breadcrumb/>
            </span>
          </el-col>
          <!---用户区域 -->
          <el-col class="mod-layout-header-right" :span="12">
            <HeaderUser></HeaderUser>
          </el-col>
        </el-row>
        <span v-if="pageTagShow"> <PageTab/></span>
      </el-header>

      <el-main class="mod-layout-main">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" :key="route.name"/>
          </keep-alive>
        </router-view>
      </el-main>

      <el-footer class="mod-layout-footer" v-show="footerShow">
        <div>Copyright © 2025-2025 Mod后台管理系统</div>
      </el-footer>
    </el-container>
  </el-container>
</template>

<script setup>
import {computed, onMounted, ref, watch} from 'vue'
import {useAdminConfigStore} from "@/stores/admin-config.js";
import SideMenu from "@/layout/side-menu/side-menu.vue";
import {useRoute, useRouter} from "vue-router";
import {HOME_PAGE} from "@/constants/index.js";
import PageTab from "@/layout/page-tab/page-tab.vue";
import Breadcrumb from "@/layout/breadcrumb/breadcrumb.vue";
import HeaderUser from "@/layout/header-user/header-user.vue";


const windowHeight = ref(window.innerHeight)

const collapse = ref(false)


const breadCrumbShow = computed(() => useAdminConfigStore().breadCrumbShow)
const pageTagShow = computed(() => useAdminConfigStore().pageTabShow)
const footerShow = computed(() => useAdminConfigStore().footerShow)

const router = useRouter()

function goHome() {
  router.push({name: HOME_PAGE})
}

const route = useRoute()
</script>

<style lang="scss" scoped>
.mod-layout {
  .mod-layout-aside {
    height: 100vh;
    overflow-x: hidden;
  }

  .mod-layout-container {
    .mod-layout-header {
      background: #fff;
      padding: 0;
      z-index: 21;
      height: auto;

      .mod-layout-header-action {
        height: 40px;
        display: flex;
        align-items: center;
        border-bottom: 1px solid #f6f6f6;

        .mod-layout-header-left {
          display: flex;
          align-items: center;
          flex: 1 1 0;

          .collapse-button {
            margin-left: 15px;
            cursor: pointer;
          }

          .home-button {
            margin-left: 15px;
            cursor: pointer;
            padding: 0 5px;

            &:hover {
              background-color: #efefef;
            }
          }

          .menu-breadcrumb {
            margin-left: 15px;
          }
        }

        .mod-layout-header-right {
          display: flex;
          justify-content: flex-end;
        }
      }
    }

    .mod-layout-main {
      padding: 10px;
    }

    .mod-layout-footer {
      position: relative;
      padding: 7px 0px;
      display: flex;
      justify-content: center;
      background-color: white;
      height: 40px;
      border-top: #f0f0f0 1px solid;
    }
  }
}
</style>
