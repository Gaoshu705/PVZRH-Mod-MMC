<template>
  <div class="form-container">
    <div class="page-header">
      <h1>表单项</h1>
    </div>
    
    <div class="form-content">
      <div class="card">
        <div class="card-content">
          <p>这是一个空白的表单项页面，您可以在这里添加表单组件用于数据录入。</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 表单项逻辑可以在这里添加
</script>

<style scoped>
.form-container {
  height: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1d2129;
}

.form-content {
  display: grid;
  gap: 20px;
}

.card {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.card-content {
  color: #4e5969;
  font-size: 14px;
  line-height: 1.6;
}
</style>