// Backend DTO lariga mos TypeScript tiplar.

export type WatchStatus = 'PLAN_TO_WATCH' | 'WATCHING' | 'COMPLETED' | 'DROPPED'
export type SerialStatus = 'ONGOING' | 'ENDED' | 'CANCELLED'

export interface UserResponse {
  id: number
  email: string
  name: string | null
  avatarUrl: string | null
}

export interface MovieResponse {
  id: number
  tmdbId: number | null
  title: string
  releaseYear: number | null
  genres: string[]
  director: string | null
  imdbRating: number | null
  personalRating: number | null
  durationMin: number | null
  platform: string | null
  watchUrl: string | null
  watchCount: number
  status: WatchStatus
  personalNote: string | null
  posterUrl: string | null
  language: string | null
  country: string | null
  createdAt: string
  updatedAt: string
}

// Spring `PagedModel` (pageSerializationMode = VIA_DTO) javob formati.
export interface PagedModel<T> {
  content: T[]
  page: {
    size: number
    number: number
    totalElements: number
    totalPages: number
  }
}

export interface MovieFilterParams {
  search?: string
  status?: WatchStatus
  genre?: string
  minRating?: number
  page?: number
  size?: number
}

export interface WatchHistoryResponse {
  id: number
  watchedAt: string
  platform: string | null
  note: string | null
}

export interface WatchHistoryRequest {
  watchedAt: string
  platform?: string
  note?: string
}

// TMDB qidiruv natijasi (movie va serial uchun bir xil shakl).
export interface TmdbSummary {
  tmdbId: number
  title: string
  releaseYear: number | null
  posterUrl: string | null
  rating: number | null
  overview: string | null
}

export type DiscoverType = 'movie' | 'serial'
