import { ref } from 'vue'
import client from '@/api/client'
import type { StatsResponse } from '@/api/types'

export function useStats() {
  const stats = ref<StatsResponse | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function load(): Promise<void> {
    loading.value = true
    error.value = null
    try {
      const { data } = await client.get<StatsResponse>('/stats')
      stats.value = data
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'error'
    } finally {
      loading.value = false
    }
  }

  return { stats, loading, error, load }
}
