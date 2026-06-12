import { defineStore } from 'pinia'
import { computed, ref, watch } from 'vue'
import type { GridSize, ViewMode } from '@/api/types'

type Theme = 'light' | 'dark'

const THEME_KEY = 'mt_theme'
const VIEW_KEY = 'mt_view'
const GRID_KEY = 'mt_grid'

const GRID_MIN: Record<GridSize, number> = { s: 130, m: 168, l: 210 }

function initialTheme(): Theme {
  const saved = localStorage.getItem(THEME_KEY) as Theme | null
  if (saved === 'light' || saved === 'dark') return saved
  return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

function initialView(): ViewMode {
  return localStorage.getItem(VIEW_KEY) === 'list' ? 'list' : 'grid'
}

function initialGrid(): GridSize {
  const v = localStorage.getItem(GRID_KEY)
  return v === 's' || v === 'l' ? v : 'm'
}

export const useSettingsStore = defineStore('settings', () => {
  const theme = ref<Theme>(initialTheme())
  const view = ref<ViewMode>(initialView())
  const gridSize = ref<GridSize>(initialGrid())

  const gridMinWidth = computed(() => GRID_MIN[gridSize.value])

  function setView(value: ViewMode): void {
    view.value = value
    localStorage.setItem(VIEW_KEY, value)
  }

  function setGridSize(value: GridSize): void {
    gridSize.value = value
    localStorage.setItem(GRID_KEY, value)
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

  return { theme, view, gridSize, gridMinWidth, toggle, setView, setGridSize }
})
