import { computed, ref } from 'vue'
import client from '@/api/client'
import type { EpisodeRequest, EpisodeResponse, SerialResponse } from '@/api/types'

interface SeasonGroup {
  season: number
  episodes: EpisodeResponse[]
}

export function useSerial() {
  const serial = ref<SerialResponse | null>(null)
  const episodes = ref<EpisodeResponse[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // Epizodlarni fasllar bo'yicha guruhlaydi (backend allaqachon tartiblangan qaytaradi).
  const seasons = computed<SeasonGroup[]>(() => {
    const map = new Map<number, EpisodeResponse[]>()
    for (const ep of episodes.value) {
      const list = map.get(ep.seasonNo) ?? []
      list.push(ep)
      map.set(ep.seasonNo, list)
    }
    return [...map.entries()]
      .sort((a, b) => a[0] - b[0])
      .map(([season, eps]) => ({ season, episodes: eps }))
  })

  async function load(id: number): Promise<void> {
    loading.value = true
    error.value = null
    try {
      const [serialRes, episodesRes] = await Promise.all([
        client.get<SerialResponse>(`/serials/${id}`),
        client.get<EpisodeResponse[]>(`/serials/${id}/episodes`),
      ])
      serial.value = serialRes.data
      episodes.value = episodesRes.data
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'error'
    } finally {
      loading.value = false
    }
  }

  async function loadEpisodes(id: number): Promise<void> {
    const { data } = await client.get<EpisodeResponse[]>(`/serials/${id}/episodes`)
    episodes.value = data
  }

  async function addEpisode(id: number, payload: EpisodeRequest): Promise<void> {
    await client.post(`/serials/${id}/episodes`, payload)
    await loadEpisodes(id)
  }

  async function deleteEpisode(id: number, episodeId: number): Promise<void> {
    await client.delete(`/serials/${id}/episodes/${episodeId}`)
    await loadEpisodes(id)
  }

  async function markEpisodeWatched(id: number, episodeId: number): Promise<void> {
    const today = new Date().toISOString().slice(0, 10)
    await client.post(`/episodes/${episodeId}/history`, { watchedAt: today })
    await loadEpisodes(id)
  }

  async function removeSerial(id: number): Promise<void> {
    await client.delete(`/serials/${id}`)
  }

  return {
    serial,
    episodes,
    seasons,
    loading,
    error,
    load,
    addEpisode,
    deleteEpisode,
    markEpisodeWatched,
    removeSerial,
  }
}
