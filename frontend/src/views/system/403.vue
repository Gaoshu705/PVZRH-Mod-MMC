<template>
  <el-result
    status="403"
    title="对不起，您没有权限访问此内容"
    subTitle="请尝试其他操作或联系管理员"
  >
    <template #extra>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </template>
  </el-result>
</template>
<script setup>
import { useRouter } from 'vue-router'
import {HOME_PAGE} from "@/constants/index.js";

const router = useRouter()

function goHome() {
  router.push({ name: HOME_PAGE })
}
</script>
