<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useStats } from '@/composables/useStats'
import BarChart from '@/components/BarChart.vue'
import ProgressSpinner from 'primevue/progressspinner'
import Message from 'primevue/message'
import type { WatchStatus } from '@/api/types'

const { t } = useI18n()
const { stats, loading, error, load } = useStats()

const STATUS_ORDER: WatchStatus[] = ['PLAN_TO_WATCH', 'WATCHING', 'COMPLETED', 'DROPPED']

onMounted(() => {
  void load()
})

const movieStatusItems = computed(() =>
  stats.value
    ? STATUS_ORDER.map((s) => ({ label: t(`status.${s}`), value: stats.value!.moviesByStatus[s] ?? 0 }))
    : [],
)

const serialStatusItems = computed(() =>
  stats.value
    ? STATUS_ORDER.map((s) => ({ label: t(`status.${s}`), value: stats.value!.serialsByStatus[s] ?? 0 }))
    : [],
)

const genreItems = computed(() =>
  stats.value ? stats.value.topGenres.map((g) => ({ label: g.genre, value: g.count })) : [],
)

const yearItems = computed(() =>
  stats.value
    ? Object.entries(stats.value.moviesWatchedByYear)
        .sort((a, b) => Number(a[0]) - Number(b[0]))
        .map(([year, count]) => ({ label: year, value: count }))
    : [],
)
</script>

<template>
  <section class="stats">
    <h1>{{ t('stats.title') }}</h1>

    <div v-if="loading" class="centered"><ProgressSpinner style="width: 42px; height: 42px" /></div>
    <Message v-else-if="error" severity="error">{{ t('movies.error') }}: {{ error }}</Message>

    <template v-else-if="stats">
      <div class="cards">
        <div class="card">
          <span class="num">{{ stats.movieCount }}</span>
          <span class="lbl">{{ t('stats.movies') }}</span>
        </div>
        <div class="card">
          <span class="num">{{ stats.serialCount }}</span>
          <span class="lbl">{{ t('stats.serials') }}</span>
        </div>
        <div class="card">
          <span class="num">{{ stats.averageMovieRating ?? '—' }}</span>
          <span class="lbl">{{ t('stats.avgMovie') }}</span>
        </div>
        <div class="card">
          <span class="num">{{ stats.averageSerialRating ?? '—' }}</span>
          <span class="lbl">{{ t('stats.avgSerial') }}</span>
        </div>
      </div>

      <div class="panels">
        <div class="panel">
          <h2>{{ t('stats.movieStatus') }}</h2>
          <BarChart :items="movieStatusItems" />
        </div>
        <div class="panel">
          <h2>{{ t('stats.serialStatus') }}</h2>
          <BarChart :items="serialStatusItems" />
        </div>
        <div class="panel">
          <h2>{{ t('stats.topGenres') }}</h2>
          <BarChart v-if="genreItems.length" :items="genreItems" />
          <p v-else class="muted">{{ t('stats.noData') }}</p>
        </div>
        <div class="panel">
          <h2>{{ t('stats.watchedByYear') }}</h2>
          <BarChart v-if="yearItems.length" :items="yearItems" />
          <p v-else class="muted">{{ t('stats.noData') }}</p>
        </div>
      </div>
    </template>
  </section>
</template>

<style scoped>
.stats {
  max-width: 900px;
  margin: 0 auto;
  padding: 1rem 0;
}
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}
.card {
  background: var(--p-content-background);
  border: 1px solid var(--p-content-border-color);
  border-radius: var(--p-border-radius-lg, 12px);
  padding: 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  align-items: center;
}
.num {
  font-size: 2rem;
  font-weight: 800;
}
.lbl {
  font-size: 0.85rem;
  color: var(--p-text-muted-color);
}
.panels {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 1.5rem;
}
.panel {
  background: var(--p-content-background);
  border: 1px solid var(--p-content-border-color);
  border-radius: var(--p-border-radius-lg, 12px);
  padding: 1.3rem;
}
.panel h2 {
  font-size: 1rem;
  margin: 0 0 1rem;
}
.muted {
  color: var(--p-text-muted-color);
  font-size: 0.9rem;
}
.centered {
  display: flex;
  justify-content: center;
  padding: 3rem 0;
}
</style>
