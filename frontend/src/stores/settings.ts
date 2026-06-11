import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

type Theme = 'light' | 'dark'

const THEME_KEY = 'mt_theme'

function initialTheme(): Theme {
  const saved = localStorage.getItem(THEME_KEY) as Theme | null
  if (saved === 'light' || saved === 'dark') return saved
  return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

export const useSettingsStore = defineStore('settings', () => {
  const theme = ref<Theme>(initialTheme())

  // Butun UI rangi `color-scheme` ga bog'liq — root da majburlaymiz.
  watch(
    theme,
    (value) => {
      localStorage.setItem(THEME_KEY, value)
      document.documentElement.style.colorScheme = value
      // PrimeVue Aura dark mode `.app-dark` klassi orqali ishlaydi.
      document.documentElement.classList.toggle('app-dark', value === 'dark')
    },
    { immediate: true },
  )

  function toggle(): void {
    theme.value = theme.value === 'dark' ? 'light' : 'dark'
  }

  return { theme, toggle }
})
