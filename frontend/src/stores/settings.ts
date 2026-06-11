import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import type { ViewMode } from '@/api/types'

type Theme = 'light' | 'dark'

const THEME_KEY = 'mt_theme'
const VIEW_KEY = 'mt_view'

function initialTheme(): Theme {
  const saved = localStorage.getItem(THEME_KEY) as Theme | null
  if (saved === 'light' || saved === 'dark') return saved
  return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

function initialView(): ViewMode {
  return localStorage.getItem(VIEW_KEY) === 'list' ? 'list' : 'grid'
}

export const useSettingsStore = defineStore('settings', () => {
  const theme = ref<Theme>(initialTheme())
  const view = ref<ViewMode>(initialView())

  function setView(value: ViewMode): void {
    view.value = value
    localStorage.setItem(VIEW_KEY, value)
  }

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

  return { theme, view, toggle, setView }
})
