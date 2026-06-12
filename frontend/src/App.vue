<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'

const { t } = useI18n()
const auth = useAuthStore()

// Bu sahifalar keshlanadi — filtr/sort/qidiruv holati saqlanadi.
const keepAlive = ['MoviesView', 'SerialsView', 'DiscoverView']

const links = [
  { to: '/', key: 'home', icon: 'pi-home' },
  { to: '/movies', key: 'movies', icon: 'pi-video' },
  { to: '/serials', key: 'serials', icon: 'pi-desktop' },
  { to: '/watchlist', key: 'watchlist', icon: 'pi-bookmark' },
  { to: '/favorites', key: 'favorites', icon: 'pi-star' },
  { to: '/stats', key: 'stats', icon: 'pi-chart-bar' },
  { to: '/discover', key: 'discover', icon: 'pi-search' },
]
</script>

<template>
  <div class="app">
    <header v-if="auth.isAuthenticated" class="topbar">
      <RouterLink to="/" class="brand">🎬 <span>Movie Tracker</span></RouterLink>
      <nav class="nav">
        <RouterLink v-for="l in links" :key="l.to" :to="l.to" class="nav-link">
          <i :class="['pi', l.icon]" />
          <span>{{ t(`nav.${l.key}`) }}</span>
        </RouterLink>
      </nav>
      <RouterLink to="/profile" class="profile-link" :title="t('nav.profile')">
        <i class="pi pi-user" />
      </RouterLink>
    </header>

    <main class="content">
      <RouterView v-slot="{ Component }">
        <keep-alive :include="keepAlive">
          <component :is="Component" />
        </keep-alive>
      </RouterView>
    </main>
  </div>
</template>

<style scoped>
.topbar {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  padding: 0.7rem 1.5rem;
  background: var(--p-content-background);
  border-bottom: 1px solid var(--p-content-border-color);
  position: sticky;
  top: 0;
  z-index: 10;
  flex-wrap: wrap;
}
.brand {
  font-weight: 800;
  font-size: 1.05rem;
  color: var(--p-text-color);
  white-space: nowrap;
}
.brand span {
  background: linear-gradient(90deg, var(--p-primary-color), #a855f7);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}
.nav {
  display: flex;
  gap: 0.3rem;
  flex-wrap: wrap;
  flex: 1;
}
.nav-link {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  padding: 0.45rem 0.7rem;
  border-radius: var(--p-border-radius-md, 8px);
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--p-text-muted-color);
  transition: background 0.15s, color 0.15s;
}
.nav-link:hover {
  background: color-mix(in srgb, var(--p-text-color) 8%, transparent);
  color: var(--p-text-color);
}
.nav-link.router-link-active {
  color: var(--p-primary-color);
  background: color-mix(in srgb, var(--p-primary-color) 15%, transparent);
}
.nav-link i {
  font-size: 0.95rem;
}
.profile-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: color-mix(in srgb, var(--p-text-color) 10%, transparent);
  color: var(--p-text-color);
}
.profile-link.router-link-active {
  background: var(--p-primary-color);
  color: var(--p-primary-contrast-color, #fff);
}
.content {
  padding: 1.5rem;
}
@media (max-width: 720px) {
  .nav-link span {
    display: none;
  }
}
</style>
