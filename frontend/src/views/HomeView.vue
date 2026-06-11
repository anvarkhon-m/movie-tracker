<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useStats } from '@/composables/useStats'
import { useMovies } from '@/composables/useMovies'
import { useSerials } from '@/composables/useSerials'

const { t } = useI18n()
const auth = useAuthStore()
const { stats, load: loadStats } = useStats()
const { movies, fetchMovies } = useMovies()
const { serials, fetchSerials } = useSerials()

onMounted(() => {
  if (!auth.user) void auth.fetchMe()
  void loadStats()
  void fetchMovies({ status: 'WATCHING', size: 12 })
  void fetchSerials({ status: 'WATCHING', size: 12 })
})

interface WatchingItem {
  key: string
  id: number
  title: string
  posterUrl: string | null
  to: { name: string; params: { id: number } }
  kind: 'movie' | 'serial'
}

const watching = computed<WatchingItem[]>(() => [
  ...movies.value.map((m) => ({
    key: `m${m.id}`,
    id: m.id,
    title: m.title,
    posterUrl: m.posterUrl,
    to: { name: 'movie-detail', params: { id: m.id } },
    kind: 'movie' as const,
  })),
  ...serials.value.map((s) => ({
    key: `s${s.id}`,
    id: s.id,
    title: s.title,
    posterUrl: s.posterUrl,
    to: { name: 'serial-detail', params: { id: s.id } },
    kind: 'serial' as const,
  })),
])

const displayName = computed(() => auth.user?.name ?? auth.user?.email ?? '')
</script>

<template>
  <section class="home">
    <h1>{{ t('home.greeting') }}, {{ displayName }} 👋</h1>

    <div v-if="stats" class="cards">
      <RouterLink to="/movies" class="card">
        <span class="num">{{ stats.movieCount }}</span>
        <span class="lbl">{{ t('nav.movies') }}</span>
      </RouterLink>
      <RouterLink to="/serials" class="card">
        <span class="num">{{ stats.serialCount }}</span>
        <span class="lbl">{{ t('nav.serials') }}</span>
      </RouterLink>
      <RouterLink to="/stats" class="card">
        <span class="num">{{ stats.averageMovieRating ?? '—' }}</span>
        <span class="lbl">{{ t('stats.avgMovie') }}</span>
      </RouterLink>
      <RouterLink to="/stats" class="card">
        <span class="num">{{ stats.averageSerialRating ?? '—' }}</span>
        <span class="lbl">{{ t('stats.avgSerial') }}</span>
      </RouterLink>
    </div>

    <section class="watching">
      <h2>{{ t('home.continueWatching') }}</h2>
      <ul v-if="watching.length" class="grid">
        <li v-for="item in watching" :key="item.key" class="poster-card">
          <RouterLink :to="item.to" class="card-link">
            <img v-if="item.posterUrl" :src="item.posterUrl" :alt="item.title" class="poster" />
            <div v-else class="poster placeholder">{{ item.kind === 'movie' ? '🎬' : '📺' }}</div>
            <span class="title">{{ item.title }}</span>
          </RouterLink>
        </li>
      </ul>
      <p v-else class="empty">
        {{ t('home.nothingWatching') }}
        <RouterLink to="/discover" class="discover-link">{{ t('home.goDiscover') }}</RouterLink>
      </p>
    </section>
  </section>
</template>

<style scoped>
.home {
  max-width: 900px;
  margin: 0 auto;
  padding: 1rem 0;
}
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
  gap: 1rem;
  margin: 1.5rem 0 2.5rem;
}
.card {
  border: 1px solid rgba(128, 128, 128, 0.3);
  border-radius: 8px;
  padding: 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  align-items: center;
  transition: border-color 0.2s;
}
.card:hover {
  border-color: #646cff;
}
.num {
  font-size: 1.8rem;
  font-weight: 700;
}
.lbl {
  font-size: 0.8rem;
  opacity: 0.75;
}
.watching h2 {
  font-size: 1.1rem;
  margin: 0 0 1rem;
}
.grid {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 1rem;
}
.poster-card {
  border: 1px solid rgba(128, 128, 128, 0.3);
  border-radius: 8px;
  overflow: hidden;
  transition: border-color 0.2s;
}
.poster-card:hover {
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
.title {
  display: block;
  padding: 0.4rem 0.6rem;
  font-size: 0.85rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.empty {
  opacity: 0.8;
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  align-items: flex-start;
}
.discover-link {
  color: #646cff;
  font-weight: 600;
}
</style>
