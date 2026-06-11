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
  imdbRatingUpdatedAt: string | null
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

// FilterBar komponenti emit qiladigan umumiy filtr (movie + serial).
export interface ListFilter {
  search?: string
  status?: WatchStatus
  genre?: string
  minRating?: number
}

export interface MovieRequest {
  tmdbId?: number | null
  title: string
  releaseYear?: number | null
  genres?: string[]
  director?: string | null
  imdbRating?: number | null
  personalRating?: number | null
  durationMin?: number | null
  platform?: string | null
  watchUrl?: string | null
  status?: WatchStatus
  personalNote?: string | null
  language?: string | null
  country?: string | null
}

export interface SerialRequest {
  tmdbId?: number | null
  title: string
  releaseYear?: number | null
  genres?: string[]
  director?: string | null
  imdbRating?: number | null
  personalRating?: number | null
  seasonCount?: number | null
  episodeCount?: number | null
  platform?: string | null
  watchUrl?: string | null
  serialStatus?: SerialStatus | null
  watchStatus?: WatchStatus
  personalNote?: string | null
  language?: string | null
  country?: string | null
}

export interface GenreStat {
  genre: string
  count: number
}

export interface StatsResponse {
  movieCount: number
  serialCount: number
  moviesByStatus: Record<WatchStatus, number>
  serialsByStatus: Record<WatchStatus, number>
  topGenres: GenreStat[]
  averageMovieRating: number | null
  averageSerialRating: number | null
  moviesWatchedByYear: Record<string, number>
}

export interface SerialResponse {
  id: number
  tmdbId: number | null
  title: string
  releaseYear: number | null
  genres: string[]
  director: string | null
  imdbRating: number | null
  personalRating: number | null
  seasonCount: number | null
  episodeCount: number | null
  platform: string | null
  watchUrl: string | null
  serialStatus: SerialStatus | null
  watchStatus: WatchStatus
  personalNote: string | null
  posterUrl: string | null
  language: string | null
  country: string | null
  createdAt: string
  updatedAt: string
  imdbRatingUpdatedAt: string | null
}

export interface EpisodeResponse {
  id: number
  seasonNo: number
  episodeNo: number
  title: string | null
  durationMin: number | null
  personalNote: string | null
  watched: boolean
}

export interface EpisodeRequest {
  seasonNo: number
  episodeNo: number
  title?: string
  durationMin?: number
  personalNote?: string
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
