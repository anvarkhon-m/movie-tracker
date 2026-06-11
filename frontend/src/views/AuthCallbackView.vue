<script setup lang="ts">
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

onMounted(async () => {
  const token = route.query.token
  if (typeof token === 'string' && token.length > 0) {
    auth.setToken(token)
    await router.replace({ name: 'home' })
  } else {
    await router.replace({ name: 'login' })
  }
})
</script>

<template>
  <p class="callback">...</p>
</template>

<style scoped>
.callback {
  text-align: center;
  margin: 20vh auto;
}
</style>
