<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useSettingsStore } from '@/stores/settings'
import { setLocale, type AppLocale } from '@/i18n'
import Select from 'primevue/select'
import Button from 'primevue/button'

const { t, locale } = useI18n()
const auth = useAuthStore()
const settings = useSettingsStore()
const router = useRouter()

const localeOptions = [
  { label: "O'zbek", value: 'uz' },
  { label: 'Русский', value: 'ru' },
  { label: 'English', value: 'en' },
]

onMounted(() => {
  if (!auth.user) void auth.fetchMe()
})

function changeLocale(value: AppLocale): void {
  setLocale(value)
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
      <div class="row">
        <span>{{ t('profile.language') }}</span>
        <Select
          :model-value="locale"
          :options="localeOptions"
          option-label="label"
          option-value="value"
          @update:model-value="changeLocale"
        />
      </div>
      <div class="row">
        <span>{{ t('profile.theme') }}</span>
        <Button
          :label="settings.theme === 'dark' ? t('profile.dark') : t('profile.light')"
          :icon="settings.theme === 'dark' ? 'pi pi-moon' : 'pi pi-sun'"
          severity="secondary"
          outlined
          @click="settings.toggle"
        />
      </div>
    </div>

    <Button :label="t('nav.logout')" icon="pi pi-sign-out" severity="danger" @click="logout" />
  </section>
</template>

<style scoped>
.profile {
  max-width: 520px;
  margin: 0 auto;
  padding: 0.5rem 0 2rem;
}
h1 {
  font-size: 1.6rem;
  font-weight: 700;
  margin: 0 0 1.5rem;
}
.account {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: var(--p-content-background);
  border: 1px solid var(--p-content-border-color);
  border-radius: var(--p-border-radius-lg, 12px);
  padding: 1.2rem;
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
  font-weight: 700;
}
.avatar.placeholder {
  background: var(--p-primary-color);
  color: var(--p-primary-contrast-color, #fff);
}
.who {
  display: flex;
  flex-direction: column;
}
.name {
  font-size: 1.1rem;
  font-weight: 700;
}
.email {
  color: var(--p-text-muted-color);
  font-size: 0.9rem;
}
.settings {
  margin: 1.5rem 0;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}
</style>
