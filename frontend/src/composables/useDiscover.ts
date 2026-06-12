import { ref } from 'vue'
import client from '@/api/client'
import type { DiscoverType, TmdbSummary } from '@/api/types'

const SEARCH_PATH: Record<DiscoverType, string> = {
  movie: '/tmdb/search/movies',
  serial: '/tmdb/search/serials',
}

const BASE_PATH: Record<DiscoverType, string> = {
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
  // Avval qo'shilganlar (oldingi sessiyalarda) — backend dan.
  const inLibrary = ref<Set<number>>(new Set())

  async function loadLibrary(type: DiscoverType): Promise<void> {
    try {
      const { data } = await client.get<number[]>(`${BASE_PATH[type]}/tmdb-ids`)
      inLibrary.value = new Set(data)
    } catch {
      inLibrary.value = new Set()
    }
  }

  function isAdded(tmdbId: number): boolean {
    return inLibrary.value.has(tmdbId) || addedIds.value.has(tmdbId)
  }

  async function search(type: DiscoverType, query: string): Promise<void> {
    if (!query.trim()) return
    loading.value = true
    error.value = null
    searched.value = true
    addedIds.value = new Set()
    try {
      const [{ data }] = await Promise.all([
        client.get<TmdbSummary[]>(SEARCH_PATH[type], { params: { query } }),
        loadLibrary(type),
      ])
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
      await client.post(BASE_PATH[type], { tmdbId })
      addedIds.value.add(tmdbId)
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'error'
    } finally {
      addingId.value = null
    }
  }

  return { results, loading, error, searched, addingId, addedIds, isAdded, search, add }
}
