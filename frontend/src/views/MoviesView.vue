<script setup lang="ts">
import { onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useMovies } from '@/composables/useMovies'
import FilterBar from '@/components/FilterBar.vue'
import type { ListFilter } from '@/api/types'

const { t } = useI18n()
const { movies, loading, error, fetchMovies } = useMovies()

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

    <p v-if="loading">{{ t('movies.loading') }}</p>
    <p v-else-if="error" class="error">{{ t('movies.error') }}: {{ error }}</p>
    <p v-else-if="movies.length === 0">{{ t('movies.empty') }}</p>

    <ul v-else class="grid">
      <li v-for="movie in movies" :key="movie.id" class="card">
        <RouterLink :to="{ name: 'movie-detail', params: { id: movie.id } }" class="card-link">
          <img v-if="movie.posterUrl" :src="movie.posterUrl" :alt="movie.title" class="poster" />
          <div v-else class="poster placeholder">🎬</div>
          <div class="meta">
            <h3>{{ movie.title }}</h3>
            <p class="sub">
              <span v-if="movie.releaseYear">{{ movie.releaseYear }}</span>
              <span class="status">{{ t(`status.${movie.status}`) }}</span>
            </p>
            <p v-if="movie.personalRating != null" class="rating">
              ⭐ {{ movie.personalRating }}
            </p>
          </div>
        </RouterLink>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.movies {
  max-width: 960px;
  margin: 0 auto;
  padding: 1rem 0;
}
.grid {
  list-style: none;
  padding: 0;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 1rem;
}
.card {
  border: 1px solid rgba(128, 128, 128, 0.3);
  border-radius: 8px;
  overflow: hidden;
  transition: border-color 0.2s;
}
.card:hover {
  border-color: #646cff;
}
.card-link {
  display: block;
  color: inherit;
}
.poster {
  width: 100%;
  aspect-ratio: 2 / 3;
  object-fit: cover;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
}
.placeholder {
  background: rgba(128, 128, 128, 0.15);
}
.meta {
  padding: 0.5rem 0.7rem;
}
.meta h3 {
  font-size: 0.95rem;
  margin: 0 0 0.3rem;
}
.sub {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  opacity: 0.75;
  margin: 0;
}
.rating {
  margin: 0.3rem 0 0;
  font-size: 0.85rem;
}
.error {
  color: #e05252;
}
</style>
