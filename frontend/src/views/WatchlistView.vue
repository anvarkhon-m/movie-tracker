<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useMovies } from '@/composables/useMovies'
import { useSerials } from '@/composables/useSerials'
import MediaGrid from '@/components/MediaGrid.vue'
import ProgressSpinner from 'primevue/progressspinner'
import Message from 'primevue/message'
import type { MediaItem } from '@/api/types'

const { t } = useI18n()
const { movies, loading: lm, fetchMovies } = useMovies()
const { serials, loading: ls, fetchSerials } = useSerials()

onMounted(() => {
  void fetchMovies({ status: 'PLAN_TO_WATCH', size: 50 })
  void fetchSerials({ status: 'PLAN_TO_WATCH', size: 50 })
})

const items = computed<MediaItem[]>(() => [
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
</script>

<template>
  <section class="page">
    <h1>{{ t('watchlist.title') }}</h1>
    <div v-if="lm || ls" class="centered"><ProgressSpinner style="width: 42px; height: 42px" /></div>
    <Message v-else-if="items.length === 0" severity="secondary">{{ t('watchlist.empty') }}</Message>
    <MediaGrid v-else :items="items" />
  </section>
</template>

<style scoped>
.page {
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
</style>
