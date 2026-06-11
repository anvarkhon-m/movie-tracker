import { ref } from 'vue'
import client from '@/api/client'
import type { PagedModel, SerialResponse } from '@/api/types'

interface SerialListParams {
  search?: string
  page?: number
  size?: number
}

export function useSerials() {
  const serials = ref<SerialResponse[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const totalElements = ref(0)

  async function fetchSerials(params: SerialListParams = {}): Promise<void> {
    loading.value = true
    error.value = null
    try {
      const { data } = await client.get<PagedModel<SerialResponse>>('/serials', { params })
      serials.value = data.content
      totalElements.value = data.page.totalElements
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'error'
    } finally {
      loading.value = false
    }
  }

  return { serials, loading, error, totalElements, fetchSerials }
}
