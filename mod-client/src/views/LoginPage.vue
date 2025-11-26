<template>
  <div class="login-container">
    <div class="page-header">
      <h1>登录页</h1>
    </div>
    
    <div class="login-content">
      <div class="card">
        <div class="card-content">
          <p>这是一个空白的登录页页面，您可以在这里添加登录表单。</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 登录页逻辑可以在这里添加
</script>

<style scoped>
.login-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.page-header {
  margin-bottom: 24px;
  text-align: center;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1d2129;
}

.login-content {
  width: 100%;
  max-width: 400px;
}

.card {
  background: #ffffff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.card-content {
  color: #4e5969;
  font-size: 14px;
  line-height: 1.6;
  text-align: center;
}
</style>