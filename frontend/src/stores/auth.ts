import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { UserResponse } from '@/api/types'

const TOKEN_KEY = 'mt_token'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem(TOKEN_KEY))
  const user = ref<UserResponse | null>(null)

  const isAuthenticated = computed(() => token.value !== null)

  function setToken(value: string): void {
    token.value = value
    localStorage.setItem(TOKEN_KEY, value)
  }

  function logout(): void {
    token.value = null
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
  }

  async function fetchMe(): Promise<void> {
    // client.ts auth store ga bog'liq — aylanma importdan qochish uchun dinamik import.
    const { default: client } = await import('@/api/client')
    const { data } = await client.get<UserResponse>('/auth/me')
    user.value = data
  }

  return { token, user, isAuthenticated, setToken, logout, fetchMe }
})
