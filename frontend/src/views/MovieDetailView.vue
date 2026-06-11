<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useMovie } from '@/composables/useMovie'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const { movie, history, loading, error, load, addWatch, deleteWatch, removeMovie } = useMovie()

const id = computed(() => Number(route.params.id))

const today = new Date().toISOString().slice(0, 10)
const form = reactive<{ watchedAt: string; platform: string; note: string }>({
  watchedAt: today,
  platform: '',
  note: '',
})

onMounted(() => {
  void load(id.value)
})

async function submitWatch(): Promise<void> {
  await addWatch(id.value, {
    watchedAt: form.watchedAt,
    platform: form.platform || undefined,
    note: form.note || undefined,
  })
  form.platform = ''
  form.note = ''
  form.watchedAt = today
}

async function onDeleteWatch(historyId: number): Promise<void> {
  await deleteWatch(id.value, historyId)
}

async function onDeleteMovie(): Promise<void> {
  if (!window.confirm(t('detail.deleteConfirm'))) return
  await removeMovie(id.value)
  await router.push({ name: 'movies' })
}
</script>

<template>
  <section class="detail">
    <RouterLink class="back" to="/movies">{{ t('detail.back') }}</RouterLink>

    <p v-if="loading">{{ t('movies.loading') }}</p>
    <p v-else-if="error" class="error">{{ t('movies.error') }}: {{ error }}</p>

    <template v-else-if="movie">
      <div class="header">
        <img v-if="movie.posterUrl" :src="movie.posterUrl" :alt="movie.title" class="poster" />
        <div v-else class="poster placeholder">🎬</div>

        <div class="info">
          <h1>{{ movie.title }} <span v-if="movie.releaseYear" class="year">({{ movie.releaseYear }})</span></h1>
          <p class="status-line">
            <span class="badge">{{ t(`status.${movie.status}`) }}</span>
            <span class="muted">{{ t('detail.watchCount') }}: {{ movie.watchCount }}</span>
          </p>

          <dl class="facts">
            <template v-if="movie.genres.length">
              <dt>{{ t('detail.genres') }}</dt>
              <dd>{{ movie.genres.join(', ') }}</dd>
            </template>
            <template v-if="movie.director">
              <dt>{{ t('detail.director') }}</dt>
              <dd>{{ movie.director }}</dd>
            </template>
            <template v-if="movie.imdbRating != null">
              <dt>{{ t('detail.imdb') }}</dt>
              <dd>⭐ {{ movie.imdbRating }}</dd>
            </template>
            <template v-if="movie.personalRating != null">
              <dt>{{ t('detail.personal') }}</dt>
              <dd>⭐ {{ movie.personalRating }}</dd>
            </template>
            <template v-if="movie.platform">
              <dt>{{ t('detail.platform') }}</dt>
              <dd>{{ movie.platform }}</dd>
            </template>
          </dl>

          <p v-if="movie.personalNote" class="note">{{ movie.personalNote }}</p>

          <button class="danger" @click="onDeleteMovie">{{ t('detail.delete') }}</button>
        </div>
      </div>

      <section class="history">
        <h2>{{ t('history.title') }}</h2>

        <form class="watch-form" @submit.prevent="submitWatch">
          <input v-model="form.watchedAt" type="date" :max="today" required />
          <input v-model="form.platform" :placeholder="t('history.platform')" />
          <input v-model="form.note" :placeholder="t('history.note')" />
          <button type="submit">{{ t('history.add') }}</button>
        </form>

        <p v-if="history.length === 0" class="muted">{{ t('history.empty') }}</p>
        <ul v-else class="history-list">
          <li v-for="entry in history" :key="entry.id">
            <span class="date">{{ entry.watchedAt }}</span>
            <span v-if="entry.platform" class="muted">{{ entry.platform }}</span>
            <span v-if="entry.note" class="entry-note">{{ entry.note }}</span>
            <button class="link-danger" @click="onDeleteWatch(entry.id)">✕</button>
          </li>
        </ul>
      </section>
    </template>
  </section>
</template>

<style scoped>
.detail {
  max-width: 820px;
  margin: 0 auto;
  padding: 1rem 0;
}
.back {
  display: inline-block;
  margin-bottom: 1rem;
  opacity: 0.8;
}
.header {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
}
.poster {
  width: 200px;
  aspect-ratio: 2 / 3;
  object-fit: cover;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
}
.placeholder {
  background: rgba(128, 128, 128, 0.15);
}
.info {
  flex: 1;
  min-width: 260px;
}
.info h1 {
  margin: 0 0 0.5rem;
}
.year {
  font-weight: 400;
  opacity: 0.7;
}
.status-line {
  display: flex;
  gap: 1rem;
  align-items: center;
  margin: 0 0 1rem;
}
.badge {
  background: #646cff;
  color: white;
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  font-size: 0.8rem;
}
.muted {
  opacity: 0.7;
  font-size: 0.9rem;
}
.facts {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 0.3rem 1rem;
  margin: 0 0 1rem;
}
.facts dt {
  font-weight: 600;
  opacity: 0.8;
}
.facts dd {
  margin: 0;
}
.note {
  font-style: italic;
  opacity: 0.85;
  margin: 0 0 1rem;
}
.danger {
  background: #b53737;
}
.history {
  margin-top: 2.5rem;
}
.watch-form {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 1rem;
}
.history-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.history-list li {
  display: flex;
  gap: 1rem;
  align-items: center;
  padding: 0.5rem 0.7rem;
  border: 1px solid rgba(128, 128, 128, 0.3);
  border-radius: 6px;
}
.date {
  font-weight: 600;
}
.entry-note {
  flex: 1;
  opacity: 0.85;
}
.link-danger {
  margin-left: auto;
  background: transparent;
  border: none;
  color: #e05252;
  padding: 0.2rem 0.4rem;
}
.error {
  color: #e05252;
}
</style>
