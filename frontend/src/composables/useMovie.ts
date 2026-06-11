import { ref } from 'vue'
import client from '@/api/client'
import type {
  MovieRequest,
  MovieResponse,
  WatchHistoryRequest,
  WatchHistoryResponse,
} from '@/api/types'

export function useMovie() {
  const movie = ref<MovieResponse | null>(null)
  const history = ref<WatchHistoryResponse[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function load(id: number): Promise<void> {
    loading.value = true
    error.value = null
    try {
      const [movieRes, historyRes] = await Promise.all([
        client.get<MovieResponse>(`/movies/${id}`),
        client.get<WatchHistoryResponse[]>(`/movies/${id}/history`),
      ])
      movie.value = movieRes.data
      history.value = historyRes.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'error'
    } finally {
      loading.value = false
    }
  }

  async function addWatch(id: number, payload: WatchHistoryRequest): Promise<void> {
    await client.post(`/movies/${id}/history`, payload)
    // recordWatch backend da watchCount/status ni o'zgartiradi — qayta yuklaymiz.
    await load(id)
  }

  async function deleteWatch(id: number, historyId: number): Promise<void> {
    await client.delete(`/movies/${id}/history/${historyId}`)
    await load(id)
  }

  async function updateMovie(id: number, payload: MovieRequest): Promise<void> {
    await client.put(`/movies/${id}`, payload)
    await load(id)
  }

  async function refreshRating(id: number): Promise<void> {
    const { data } = await client.post<MovieResponse>(`/movies/${id}/refresh-rating`)
    movie.value = data
  }

  async function removeMovie(id: number): Promise<void> {
    await client.delete(`/movies/${id}`)
  }

  return {
    movie,
    history,
    loading,
    error,
    load,
    addWatch,
    deleteWatch,
    updateMovie,
    refreshRating,
    removeMovie,
  }
}
