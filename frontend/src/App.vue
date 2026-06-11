<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'

const { t } = useI18n()
const auth = useAuthStore()
</script>

<template>
  <div class="app">
    <header v-if="auth.isAuthenticated" class="topbar">
      <nav class="nav">
        <RouterLink to="/">{{ t('nav.home') }}</RouterLink>
        <RouterLink to="/movies">{{ t('nav.movies') }}</RouterLink>
        <RouterLink to="/serials">{{ t('nav.serials') }}</RouterLink>
        <RouterLink to="/watchlist">{{ t('nav.watchlist') }}</RouterLink>
        <RouterLink to="/favorites">{{ t('nav.favorites') }}</RouterLink>
        <RouterLink to="/stats">{{ t('nav.stats') }}</RouterLink>
        <RouterLink to="/discover">{{ t('nav.discover') }}</RouterLink>
      </nav>
      <RouterLink to="/profile" class="profile-link">{{ t('nav.profile') }}</RouterLink>
    </header>

    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  padding: 0.8rem 1.5rem;
  border-bottom: 1px solid rgba(128, 128, 128, 0.3);
  flex-wrap: wrap;
}
.nav {
  display: flex;
  gap: 1.2rem;
  flex-wrap: wrap;
}
.nav a {
  font-weight: 500;
}
.nav a.router-link-active {
  text-decoration: underline;
}
.profile-link {
  font-weight: 600;
  white-space: nowrap;
}
.profile-link.router-link-active {
  text-decoration: underline;
}
.content {
  padding: 1.5rem;
}
</style>
