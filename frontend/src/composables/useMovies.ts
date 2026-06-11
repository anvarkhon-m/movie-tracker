import { ref } from 'vue'
import client from '@/api/client'
import type { MovieResponse, PagedModel, MovieFilterParams } from '@/api/types'

export function useMovies() {
  const movies = ref<MovieResponse[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const totalElements = ref(0)

  async function fetchMovies(params: MovieFilterParams = {}): Promise<void> {
    loading.value = true
    error.value = null
    try {
      const { data } = await client.get<PagedModel<MovieResponse>>('/movies', { params })
      movies.value = data.content
      totalElements.value = data.page.totalElements
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'error'
    } finally {
      loading.value = false
    }
  }

  return { movies, loading, error, totalElements, fetchMovies }
}
