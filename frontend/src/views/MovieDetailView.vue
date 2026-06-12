<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useMovie } from '@/composables/useMovie'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import Textarea from 'primevue/textarea'
import Select from 'primevue/select'
import DatePicker from 'primevue/datepicker'
import Tag from 'primevue/tag'
import Message from 'primevue/message'
import ProgressSpinner from 'primevue/progressspinner'
import type { WatchStatus } from '@/api/types'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const {
  movie,
  history,
  loading,
  error,
  load,
  addWatch,
  deleteWatch,
  updateMovie,
  refreshRating,
  removeMovie,
} = useMovie()

const SEVERITY: Record<WatchStatus, string> = {
  PLAN_TO_WATCH: 'secondary',
  WATCHING: 'info',
  COMPLETED: 'success',
  DROPPED: 'danger',
}

const id = computed(() => Number(route.params.id))

const refreshingRating = ref(false)
const COOLDOWN_MS = 24 * 60 * 60 * 1000

const ratingOnCooldown = computed(() => {
  const ts = movie.value?.imdbRatingUpdatedAt
  return ts != null && Date.now() - new Date(ts).getTime() < COOLDOWN_MS
})

async function onRefreshRating(): Promise<void> {
  refreshingRating.value = true
  try {
    await refreshRating(id.value)
  } finally {
    refreshingRating.value = false
  }
}

const STATUSES: WatchStatus[] = ['PLAN_TO_WATCH', 'WATCHING', 'COMPLETED', 'DROPPED']
const statusOptions = computed(() => STATUSES.map((s) => ({ label: t(`status.${s}`), value: s })))

const editing = ref(false)
const edit = reactive<{
  personalRating: number | null
  status: WatchStatus
  platform: string
  language: string
  personalNote: string
}>({ personalRating: null, status: 'PLAN_TO_WATCH', platform: '', language: '', personalNote: '' })

function startEdit(): void {
  if (!movie.value) return
  edit.personalRating = movie.value.personalRating
  edit.status = movie.value.status
  edit.platform = movie.value.platform ?? ''
  edit.language = movie.value.language ?? ''
  edit.personalNote = movie.value.personalNote ?? ''
  editing.value = true
}

async function saveEdit(): Promise<void> {
  if (!movie.value) return
  const m = movie.value
  await updateMovie(id.value, {
    title: m.title,
    releaseYear: m.releaseYear,
    genres: m.genres,
    director: m.director,
    imdbRating: m.imdbRating,
    personalRating: edit.personalRating,
    durationMin: m.durationMin,
    platform: edit.platform || null,
    watchUrl: m.watchUrl,
    status: edit.status,
    personalNote: edit.personalNote || null,
    language: edit.language || null,
    country: m.country,
  })
  editing.value = false
}

const maxDate = new Date()
const form = reactive<{ watchedAt: Date; platform: string; note: string }>({
  watchedAt: new Date(),
  platform: '',
  note: '',
})

function toIsoDate(d: Date): string {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

onMounted(() => {
  void load(id.value)
})

async function submitWatch(): Promise<void> {
  await addWatch(id.value, {
    watchedAt: toIsoDate(form.watchedAt),
    platform: form.platform || undefined,
    note: form.note || undefined,
  })
  form.platform = ''
  form.note = ''
  form.watchedAt = new Date()
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
    <RouterLink class="back" to="/movies"><i class="pi pi-arrow-left" /> {{ t('nav.movies') }}</RouterLink>

    <div v-if="loading" class="centered"><ProgressSpinner style="width: 42px; height: 42px" /></div>
    <Message v-else-if="error" severity="error">{{ t('movies.error') }}: {{ error }}</Message>

    <template v-else-if="movie">
      <div class="header">
        <img v-if="movie.posterUrl" :src="movie.posterUrl" :alt="movie.title" class="poster" />
        <div v-else class="poster placeholder"><i class="pi pi-video" /></div>

        <div class="info">
          <h1>{{ movie.title }} <span v-if="movie.releaseYear" class="year">({{ movie.releaseYear }})</span></h1>
          <div class="status-line">
            <Tag :value="t(`status.${movie.status}`)" :severity="SEVERITY[movie.status]" />
            <span class="muted">{{ t('detail.watchCount') }}: {{ movie.watchCount }}</span>
          </div>

          <dl class="facts">
            <template v-if="movie.genres.length">
              <dt>{{ t('detail.genres') }}</dt>
              <dd>{{ movie.genres.join(', ') }}</dd>
            </template>
            <template v-if="movie.director">
              <dt>{{ t('detail.director') }}</dt>
              <dd>{{ movie.director }}</dd>
            </template>
            <template v-if="movie.imdbRating != null || movie.tmdbId != null">
              <dt>{{ t('detail.imdb') }}</dt>
              <dd class="imdb">
                <span>{{ movie.imdbRating != null ? `⭐ ${movie.imdbRating}` : '—' }}</span>
                <Button
                  v-if="movie.tmdbId != null"
                  text
                  rounded
                  size="small"
                  icon="pi pi-refresh"
                  :loading="refreshingRating"
                  :disabled="ratingOnCooldown"
                  :title="ratingOnCooldown ? t('detail.ratingFresh') : t('detail.refreshRating')"
                  @click="onRefreshRating"
                />
              </dd>
            </template>
            <template v-if="movie.personalRating != null">
              <dt>{{ t('detail.personal') }}</dt>
              <dd>⭐ {{ movie.personalRating }}</dd>
            </template>
            <template v-if="movie.durationMin">
              <dt>{{ t('detail.runtime') }}</dt>
              <dd>{{ movie.durationMin }} {{ t('detail.minutes') }}</dd>
            </template>
            <template v-if="movie.platform">
              <dt>{{ t('detail.platform') }}</dt>
              <dd>{{ movie.platform }}</dd>
            </template>
            <template v-if="movie.language">
              <dt>{{ t('detail.watchedLanguage') }}</dt>
              <dd>{{ movie.language }}</dd>
            </template>
          </dl>

          <p v-if="movie.overview" class="overview">{{ movie.overview }}</p>
          <p v-if="movie.personalNote" class="note">{{ movie.personalNote }}</p>

          <div v-if="!editing" class="actions">
            <Button :label="t('edit.edit')" icon="pi pi-pencil" outlined @click="startEdit" />
            <Button :label="t('detail.delete')" icon="pi pi-trash" severity="danger" outlined @click="onDeleteMovie" />
          </div>

          <form v-else class="edit-form" @submit.prevent="saveEdit">
            <label>{{ t('edit.status') }}
              <Select v-model="edit.status" :options="statusOptions" option-label="label" option-value="value" />
            </label>
            <label>{{ t('edit.personalRating') }}
              <InputNumber v-model="edit.personalRating" :min="0" :max="10" :max-fraction-digits="1" show-buttons />
            </label>
            <label>{{ t('edit.platform') }}
              <InputText v-model="edit.platform" />
            </label>
            <label>{{ t('edit.watchedLanguage') }}
              <InputText v-model="edit.language" :placeholder="t('edit.watchedLanguageHint')" />
            </label>
            <label>{{ t('edit.note') }}
              <Textarea v-model="edit.personalNote" rows="3" auto-resize />
            </label>
            <div class="actions">
              <Button type="submit" :label="t('edit.save')" icon="pi pi-check" />
              <Button type="button" :label="t('edit.cancel')" severity="secondary" text @click="editing = false" />
            </div>
          </form>
        </div>
      </div>

      <section class="history">
        <h2>{{ t('history.title') }}</h2>

        <form class="watch-form" @submit.prevent="submitWatch">
          <DatePicker v-model="form.watchedAt" :max-date="maxDate" date-format="yy-mm-dd" show-icon />
          <InputText v-model="form.platform" :placeholder="t('history.platform')" />
          <InputText v-model="form.note" :placeholder="t('history.note')" />
          <Button type="submit" :label="t('history.add')" icon="pi pi-plus" />
        </form>

        <Message v-if="history.length === 0" severity="secondary">{{ t('history.empty') }}</Message>
        <ul v-else class="history-list">
          <li v-for="entry in history" :key="entry.id">
            <span class="date"><i class="pi pi-calendar" /> {{ entry.watchedAt }}</span>
            <span v-if="entry.platform" class="muted">{{ entry.platform }}</span>
            <span v-if="entry.note" class="entry-note">{{ entry.note }}</span>
            <Button text rounded severity="danger" size="small" icon="pi pi-times" @click="onDeleteWatch(entry.id)" />
          </li>
        </ul>
      </section>
    </template>
  </section>
</template>

<style scoped>
.detail {
  max-width: 860px;
  margin: 0 auto;
  padding: 0.5rem 0 2rem;
}
.back {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  margin-bottom: 1.2rem;
  color: var(--p-text-muted-color);
  font-weight: 500;
}
.back:hover {
  color: var(--p-primary-color);
}
.centered {
  display: flex;
  justify-content: center;
  padding: 3rem 0;
}
.header {
  display: flex;
  gap: 1.8rem;
  flex-wrap: wrap;
}
.poster {
  width: 220px;
  aspect-ratio: 2 / 3;
  object-fit: cover;
  border-radius: var(--p-border-radius-lg, 12px);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  color: var(--p-text-muted-color);
}
.placeholder {
  background: color-mix(in srgb, var(--p-text-color) 8%, transparent);
}
.info {
  flex: 1;
  min-width: 280px;
}
.info h1 {
  margin: 0 0 0.6rem;
  font-size: 1.6rem;
}
.year {
  font-weight: 400;
  color: var(--p-text-muted-color);
}
.status-line {
  display: flex;
  gap: 1rem;
  align-items: center;
  margin: 0 0 1.2rem;
}
.muted {
  color: var(--p-text-muted-color);
  font-size: 0.9rem;
}
.facts {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 0.45rem 1.2rem;
  margin: 0 0 1.2rem;
}
.facts dt {
  font-weight: 600;
  color: var(--p-text-muted-color);
}
.facts dd {
  margin: 0;
}
.imdb {
  display: flex;
  align-items: center;
  gap: 0.3rem;
}
.overview {
  margin: 0 0 1.2rem;
  line-height: 1.6;
  color: var(--p-text-color);
}
.note {
  font-style: italic;
  color: var(--p-text-muted-color);
  margin: 0 0 1.2rem;
  padding-left: 0.8rem;
  border-left: 3px solid var(--p-content-border-color);
}
.actions {
  display: flex;
  gap: 0.6rem;
  flex-wrap: wrap;
}
.edit-form {
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
  max-width: 360px;
}
.edit-form label {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  font-size: 0.82rem;
  color: var(--p-text-muted-color);
}
.history {
  margin-top: 3rem;
}
.history h2 {
  font-size: 1.2rem;
  margin: 0 0 1rem;
}
.watch-form {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 1.2rem;
}
.history-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}
.history-list li {
  display: flex;
  gap: 1rem;
  align-items: center;
  padding: 0.6rem 0.9rem;
  background: var(--p-content-background);
  border: 1px solid var(--p-content-border-color);
  border-radius: var(--p-border-radius-md, 8px);
}
.date {
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
}
.entry-note {
  flex: 1;
  color: var(--p-text-muted-color);
}
.history-list li :deep(.p-button) {
  margin-left: auto;
}
</style>
