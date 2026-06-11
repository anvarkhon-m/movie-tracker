<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'

const { t } = useI18n()
const auth = useAuthStore()
const router = useRouter()
const devToken = ref('')

function googleLogin(): void {
  // To'liq sahifa navigatsiyasi — Vite proxy /api ni backend ga uzatadi.
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
    <h1>{{ t('login.title') }}</h1>

    <button class="btn-google" @click="googleLogin">{{ t('login.google') }}</button>

    <details class="dev-login">
      <summary>{{ t('login.devTitle') }}</summary>
      <form @submit.prevent="devLogin">
        <input v-model="devToken" :placeholder="t('login.devPlaceholder')" />
        <button type="submit">{{ t('login.devSubmit') }}</button>
      </form>
    </details>
  </div>
</template>

<style scoped>
.login {
  max-width: 380px;
  margin: 12vh auto;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}
.btn-google {
  padding: 0.8rem 1rem;
  font-size: 1rem;
  font-weight: 600;
}
.dev-login {
  text-align: left;
  font-size: 0.85rem;
  opacity: 0.85;
}
.dev-login form {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.5rem;
}
.dev-login input {
  flex: 1;
  padding: 0.4rem;
}
</style>
