<template>
  <el-avatar
    :size="size"
    :src="displaySrc"
    :shape="shape"
    :fit="fit"
    class="user-avatar"
  >
    <span class="user-avatar-fallback" :style="fallbackStyle">{{ fallbackChar }}</span>
  </el-avatar>
</template>

<script setup>
import { computed } from 'vue'
import { getAvatarFallbackChar } from '@/utils/avatar.js'

const props = defineProps({
  src: { type: String, default: '' },
  username: { type: String, default: '' },
  nickname: { type: String, default: '' },
  size: { type: [Number, String], default: 40 },
  shape: { type: String, default: 'circle' },
  fit: { type: String, default: 'cover' },
})

const displaySrc = computed(() => props.src || undefined)
const fallbackChar = computed(() => getAvatarFallbackChar(props.username, props.nickname))
const fallbackStyle = computed(() => {
  const size = Number(props.size) || 40
  return {
    fontSize: `${Math.max(12, Math.round(size * 0.42))}px`,
    fontWeight: 600,
    lineHeight: 1,
  }
})
</script>
