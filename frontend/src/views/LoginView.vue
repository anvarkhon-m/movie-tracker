<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'

const { t } = useI18n()
const auth = useAuthStore()
const router = useRouter()
const devToken = ref('')
const showDev = ref(false)

function googleLogin(): void {
  window.location.href = '/api/v1/auth/google'
}

async function devLogin(): Promise<void> {
  const value = devToken.value.trim()
  if (!value) return
  auth.setToken(value)
  await router.push({ name: 'home' })
}
</script>

<template>
  <div class="login">
    <div class="brand">🎬 <span>Movie Tracker</span></div>
    <p class="tagline">{{ t('login.title') }}</p>

    <Button class="google" :label="t('login.google')" icon="pi pi-google" @click="googleLogin" />

    <button class="dev-toggle" @click="showDev = !showDev">{{ t('login.devTitle') }}</button>
    <form v-if="showDev" class="dev-form" @submit.prevent="devLogin">
      <InputText v-model="devToken" :placeholder="t('login.devPlaceholder')" fluid />
      <Button type="submit" :label="t('login.devSubmit')" severity="secondary" outlined />
    </form>
  </div>
</template>

<style scoped>
.login {
  max-width: 380px;
  margin: 14vh auto;
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
  align-items: center;
  text-align: center;
}
.brand {
  font-size: 1.8rem;
  font-weight: 800;
}
.brand span {
  background: linear-gradient(90deg, var(--p-primary-color), #a855f7);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}
.tagline {
  color: var(--p-text-muted-color);
  margin: 0 0 0.5rem;
}
.google {
  width: 100%;
}
.dev-toggle {
  background: none;
  border: none;
  color: var(--p-text-muted-color);
  font-size: 0.85rem;
  cursor: pointer;
  padding: 0;
}
.dev-form {
  display: flex;
  gap: 0.5rem;
  width: 100%;
}
.dev-form :deep(.p-inputtext) {
  flex: 1;
}
</style>
