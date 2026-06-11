<script setup lang="ts">
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { setLocale, type AppLocale } from '@/i18n'

const { t, locale } = useI18n()
const auth = useAuthStore()
const router = useRouter()

function logout(): void {
  auth.logout()
  void router.push({ name: 'login' })
}

function changeLocale(event: Event): void {
  setLocale((event.target as HTMLSelectElement).value as AppLocale)
}
</script>

<template>
  <div class="app">
    <header v-if="auth.isAuthenticated" class="topbar">
      <nav class="nav">
        <RouterLink to="/">{{ t('nav.home') }}</RouterLink>
        <RouterLink to="/movies">{{ t('nav.movies') }}</RouterLink>
        <RouterLink to="/discover">{{ t('nav.discover') }}</RouterLink>
      </nav>
      <div class="actions">
        <select :value="locale" @change="changeLocale">
          <option value="uz">UZ</option>
          <option value="ru">RU</option>
          <option value="en">EN</option>
        </select>
        <button @click="logout">{{ t('nav.logout') }}</button>
      </div>
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
  padding: 0.8rem 1.5rem;
  border-bottom: 1px solid rgba(128, 128, 128, 0.3);
}
.nav {
  display: flex;
  gap: 1.2rem;
}
.nav a {
  font-weight: 500;
}
.nav a.router-link-active {
  text-decoration: underline;
}
.actions {
  display: flex;
  gap: 0.6rem;
  align-items: center;
}
.content {
  padding: 1.5rem;
}
</style>
