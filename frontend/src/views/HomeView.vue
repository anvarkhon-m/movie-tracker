<script setup lang="ts">
import { onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'

const { t } = useI18n()
const auth = useAuthStore()

onMounted(() => {
  if (!auth.user) {
    void auth.fetchMe()
  }
})
</script>

<template>
  <section class="home">
    <h1>{{ t('app.title') }}</h1>
    <p v-if="auth.user">{{ auth.user.name ?? auth.user.email }} 👋</p>
    <RouterLink class="cta" to="/movies">{{ t('nav.movies') }} →</RouterLink>
  </section>
</template>

<style scoped>
.home {
  max-width: 720px;
  margin: 0 auto;
  padding: 2rem 0;
}
.cta {
  display: inline-block;
  margin-top: 1rem;
  font-weight: 600;
}
</style>
