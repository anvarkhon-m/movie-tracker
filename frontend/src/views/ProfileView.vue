<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useSettingsStore } from '@/stores/settings'
import { setLocale, type AppLocale } from '@/i18n'

const { t, locale } = useI18n()
const auth = useAuthStore()
const settings = useSettingsStore()
const router = useRouter()

onMounted(() => {
  if (!auth.user) void auth.fetchMe()
})

function changeLocale(event: Event): void {
  setLocale((event.target as HTMLSelectElement).value as AppLocale)
}

function logout(): void {
  auth.logout()
  void router.push({ name: 'login' })
}
</script>

<template>
  <section class="profile">
    <h1>{{ t('nav.profile') }}</h1>

    <div v-if="auth.user" class="account">
      <img v-if="auth.user.avatarUrl" :src="auth.user.avatarUrl" :alt="auth.user.name ?? ''" class="avatar" />
      <div v-else class="avatar placeholder">{{ (auth.user.name ?? auth.user.email).charAt(0).toUpperCase() }}</div>
      <div class="who">
        <span class="name">{{ auth.user.name ?? '—' }}</span>
        <span class="email">{{ auth.user.email }}</span>
      </div>
    </div>

    <div class="settings">
      <label class="row">
        <span>{{ t('profile.language') }}</span>
        <select :value="locale" @change="changeLocale">
          <option value="uz">O'zbek</option>
          <option value="ru">Русский</option>
          <option value="en">English</option>
        </select>
      </label>
      <div class="row">
        <span>{{ t('profile.theme') }}</span>
        <button class="theme-btn" @click="settings.toggle">
          {{ settings.theme === 'dark' ? `🌙 ${t('profile.dark')}` : `☀️ ${t('profile.light')}` }}
        </button>
      </div>
    </div>

    <button class="logout" @click="logout">{{ t('nav.logout') }}</button>
  </section>
</template>

<style scoped>
.profile {
  max-width: 520px;
  margin: 0 auto;
  padding: 1rem 0;
}
.account {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin: 1.5rem 0;
}
.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.6rem;
  font-weight: 600;
}
.avatar.placeholder {
  background: #646cff;
  color: white;
}
.who {
  display: flex;
  flex-direction: column;
}
.name {
  font-size: 1.1rem;
  font-weight: 600;
}
.email {
  opacity: 0.7;
  font-size: 0.9rem;
}
.settings {
  border-top: 1px solid rgba(128, 128, 128, 0.3);
  border-bottom: 1px solid rgba(128, 128, 128, 0.3);
  padding: 1rem 0;
  margin: 1.5rem 0;
}
.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}
.row + .row {
  margin-top: 0.8rem;
}
.theme-btn {
  background: transparent;
  border: 1px solid rgba(128, 128, 128, 0.4);
}
.logout {
  background: #b53737;
}
</style>
