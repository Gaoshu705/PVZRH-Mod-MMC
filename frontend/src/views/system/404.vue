<template>
  <el-result
    status="404"
    title="对不起，您访问的内容不存在！"
    sub-title="请检查您输入的地址是否正确"
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
