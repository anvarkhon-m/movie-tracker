<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useStats } from '@/composables/useStats'
import { useMovies } from '@/composables/useMovies'
import { useSerials } from '@/composables/useSerials'
import MediaGrid from '@/components/MediaGrid.vue'
import type { MediaItem } from '@/api/types'

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

const watching = computed<MediaItem[]>(() => [
  ...movies.value.map((m) => ({
    key: `m${m.id}`,
    title: m.title,
    posterUrl: m.posterUrl,
    kind: 'movie' as const,
    to: { name: 'movie-detail', params: { id: m.id } },
  })),
  ...serials.value.map((s) => ({
    key: `s${s.id}`,
    title: s.title,
    posterUrl: s.posterUrl,
    kind: 'serial' as const,
    to: { name: 'serial-detail', params: { id: s.id } },
  })),
])

const displayName = computed(() => auth.user?.name ?? auth.user?.email ?? '')

const cards = computed(() =>
  stats.value
    ? [
        { to: '/movies', num: stats.value.movieCount, lbl: t('nav.movies'), icon: 'pi-video' },
        { to: '/serials', num: stats.value.serialCount, lbl: t('nav.serials'), icon: 'pi-desktop' },
        { to: '/stats', num: stats.value.averageMovieRating ?? '—', lbl: t('stats.avgMovie'), icon: 'pi-star' },
        { to: '/stats', num: stats.value.averageSerialRating ?? '—', lbl: t('stats.avgSerial'), icon: 'pi-star' },
      ]
    : [],
)
</script>

<template>
  <section class="home">
    <h1>{{ t('home.greeting') }}, {{ displayName }} 👋</h1>

    <div class="cards">
      <RouterLink v-for="c in cards" :key="c.lbl" :to="c.to" class="stat">
        <i :class="['pi', c.icon]" />
        <span class="num">{{ c.num }}</span>
        <span class="lbl">{{ c.lbl }}</span>
      </RouterLink>
    </div>

    <section class="watching">
      <h2>{{ t('home.continueWatching') }}</h2>
      <MediaGrid v-if="watching.length" :items="watching" />
      <div v-else class="empty">
        <p>{{ t('home.nothingWatching') }}</p>
        <RouterLink to="/discover" class="discover-link">{{ t('home.goDiscover') }}</RouterLink>
      </div>
    </section>
  </section>
</template>

<style scoped>
.home {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0.5rem 0 2rem;
}
h1 {
  font-size: 1.7rem;
  font-weight: 700;
  margin: 0 0 1.5rem;
}
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
  margin-bottom: 2.5rem;
}
.stat {
  background: var(--p-content-background);
  border: 1px solid var(--p-content-border-color);
  border-radius: var(--p-border-radius-lg, 12px);
  padding: 1.3rem;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  align-items: center;
  color: var(--p-text-color);
  transition: transform 0.15s, border-color 0.15s;
}
.stat:hover {
  transform: translateY(-3px);
  border-color: var(--p-primary-color);
}
.stat i {
  font-size: 1.2rem;
  color: var(--p-primary-color);
  margin-bottom: 0.3rem;
}
.num {
  font-size: 1.9rem;
  font-weight: 800;
}
.lbl {
  font-size: 0.82rem;
  color: var(--p-text-muted-color);
}
.watching h2 {
  font-size: 1.15rem;
  margin: 0 0 1.1rem;
}
.empty {
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  align-items: flex-start;
  color: var(--p-text-muted-color);
}
.discover-link {
  color: var(--p-primary-color);
  font-weight: 600;
}
</style>
