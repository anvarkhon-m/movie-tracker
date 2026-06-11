import { ref } from 'vue'
import client from '@/api/client'
import type { DiscoverType, TmdbSummary } from '@/api/types'

const SEARCH_PATH: Record<DiscoverType, string> = {
  movie: '/tmdb/search/movies',
  serial: '/tmdb/search/serials',
}

const ADD_PATH: Record<DiscoverType, string> = {
  movie: '/movies',
  serial: '/serials',
}

export function useDiscover() {
  const results = ref<TmdbSummary[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const searched = ref(false)

  const addingId = ref<number | null>(null)
  const addedIds = ref<Set<number>>(new Set())

  async function search(type: DiscoverType, query: string): Promise<void> {
    if (!query.trim()) return
    loading.value = true
    error.value = null
    searched.value = true
    addedIds.value = new Set()
    try {
      const { data } = await client.get<TmdbSummary[]>(SEARCH_PATH[type], {
        params: { query },
      })
      results.value = data
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'error'
      results.value = []
    } finally {
      loading.value = false
    }
  }

  async function add(type: DiscoverType, tmdbId: number): Promise<void> {
    addingId.value = tmdbId
    error.value = null
    try {
      await client.post(ADD_PATH[type], { tmdbId })
      addedIds.value.add(tmdbId)
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'error'
    } finally {
      addingId.value = null
    }
  }

  return { results, loading, error, searched, addingId, addedIds, search, add }
}
