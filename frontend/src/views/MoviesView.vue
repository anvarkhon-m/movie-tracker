<script setup lang="ts">
import { onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useMovies } from '@/composables/useMovies'
import FilterBar from '@/components/FilterBar.vue'
import Tag from 'primevue/tag'
import ProgressSpinner from 'primevue/progressspinner'
import Message from 'primevue/message'
import type { ListFilter, WatchStatus } from '@/api/types'

const { t } = useI18n()
const { movies, loading, error, fetchMovies } = useMovies()

const SEVERITY: Record<WatchStatus, string> = {
  PLAN_TO_WATCH: 'secondary',
  WATCHING: 'info',
  COMPLETED: 'success',
  DROPPED: 'danger',
}

onMounted(() => {
  void fetchMovies({ page: 0, size: 20 })
})

function onFilter(filter: ListFilter): void {
  void fetchMovies({ ...filter, page: 0, size: 20 })
}
</script>

<template>
  <section class="movies">
    <h1>{{ t('movies.title') }}</h1>

    <FilterBar @change="onFilter" />

    <div v-if="loading" class="centered"><ProgressSpinner style="width: 42px; height: 42px" /></div>
    <Message v-else-if="error" severity="error">{{ t('movies.error') }}: {{ error }}</Message>
    <Message v-else-if="movies.length === 0" severity="secondary">{{ t('movies.empty') }}</Message>

    <ul v-else class="grid">
      <li v-for="movie in movies" :key="movie.id" class="card">
        <RouterLink :to="{ name: 'movie-detail', params: { id: movie.id } }" class="card-link">
          <div class="poster-wrap">
            <img v-if="movie.posterUrl" :src="movie.posterUrl" :alt="movie.title" class="poster" />
            <div v-else class="poster placeholder"><i class="pi pi-video" /></div>
            <Tag
              :value="t(`status.${movie.status}`)"
              :severity="SEVERITY[movie.status]"
              class="status-tag"
            />
          </div>
          <div class="meta">
            <span class="title">{{ movie.title }}</span>
            <div class="sub">
              <span v-if="movie.releaseYear" class="muted">{{ movie.releaseYear }}</span>
              <span v-if="movie.personalRating != null" class="rating">
                <i class="pi pi-star-fill" /> {{ movie.personalRating }}
              </span>
            </div>
          </div>
        </RouterLink>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.movies {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0.5rem 0 2rem;
}
h1 {
  font-size: 1.6rem;
  font-weight: 700;
  margin: 0 0 1.2rem;
}
.centered {
  display: flex;
  justify-content: center;
  padding: 3rem 0;
}
.grid {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
  gap: 1.4rem;
}
.card {
  background: var(--p-content-background);
  border: 1px solid var(--p-content-border-color);
  border-radius: var(--p-border-radius-lg, 12px);
  overflow: hidden;
  transition: transform 0.15s ease, box-shadow 0.15s ease, border-color 0.15s ease;
}
.card:hover {
  transform: translateY(-3px);
  border-color: var(--p-primary-color);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.18);
}
.card-link {
  display: block;
  color: inherit;
}
.poster-wrap {
  position: relative;
}
.poster {
  width: 100%;
  aspect-ratio: 2 / 3;
  object-fit: cover;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.4rem;
  color: var(--p-text-muted-color);
}
.placeholder {
  background: var(--p-surface-100);
}
:global(.app-dark) .placeholder {
  background: var(--p-surface-800);
}
.status-tag {
  position: absolute;
  top: 0.55rem;
  left: 0.55rem;
}
.meta {
  padding: 0.7rem 0.8rem 0.85rem;
}
.title {
  display: block;
  font-weight: 600;
  font-size: 0.95rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 0.35rem;
}
.sub {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.82rem;
}
.muted {
  color: var(--p-text-muted-color);
}
.rating {
  color: #f5a623;
  font-weight: 600;
}
</style>
