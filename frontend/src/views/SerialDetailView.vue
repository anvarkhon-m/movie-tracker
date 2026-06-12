<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useSerial } from '@/composables/useSerial'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import Textarea from 'primevue/textarea'
import Select from 'primevue/select'
import Tag from 'primevue/tag'
import Message from 'primevue/message'
import ProgressSpinner from 'primevue/progressspinner'
import type { SerialStatus, WatchStatus } from '@/api/types'

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const {
  serial,
  seasons,
  loading,
  error,
  load,
  addEpisode,
  deleteEpisode,
  markEpisodeWatched,
  updateSerial,
  refreshRating,
  removeSerial,
} = useSerial()

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
  const ts = serial.value?.imdbRatingUpdatedAt
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

const WATCH_STATUSES: WatchStatus[] = ['PLAN_TO_WATCH', 'WATCHING', 'COMPLETED', 'DROPPED']
const SERIAL_STATUSES: SerialStatus[] = ['ONGOING', 'ENDED', 'CANCELLED']
const watchStatusOptions = computed(() => WATCH_STATUSES.map((s) => ({ label: t(`status.${s}`), value: s })))
const serialStatusOptions = computed(() => SERIAL_STATUSES.map((s) => ({ label: t(`serialStatus.${s}`), value: s })))

const editing = ref(false)
const edit = reactive<{
  personalRating: number | null
  watchStatus: WatchStatus
  serialStatus: SerialStatus
  platform: string
  language: string
  personalNote: string
}>({
  personalRating: null,
  watchStatus: 'PLAN_TO_WATCH',
  serialStatus: 'ONGOING',
  platform: '',
  language: '',
  personalNote: '',
})

function startEdit(): void {
  if (!serial.value) return
  edit.personalRating = serial.value.personalRating
  edit.watchStatus = serial.value.watchStatus
  edit.serialStatus = serial.value.serialStatus ?? 'ONGOING'
  edit.platform = serial.value.platform ?? ''
  edit.language = serial.value.language ?? ''
  edit.personalNote = serial.value.personalNote ?? ''
  editing.value = true
}

async function saveEdit(): Promise<void> {
  if (!serial.value) return
  const s = serial.value
  await updateSerial(id.value, {
    title: s.title,
    releaseYear: s.releaseYear,
    genres: s.genres,
    director: s.director,
    imdbRating: s.imdbRating,
    personalRating: edit.personalRating,
    seasonCount: s.seasonCount,
    episodeCount: s.episodeCount,
    platform: edit.platform || null,
    watchUrl: s.watchUrl,
    serialStatus: edit.serialStatus,
    watchStatus: edit.watchStatus,
    personalNote: edit.personalNote || null,
    language: edit.language || null,
    country: s.country,
  })
  editing.value = false
}

const form = reactive<{ seasonNo: number; episodeNo: number; title: string; durationMin: number | null }>({
  seasonNo: 1,
  episodeNo: 1,
  title: '',
  durationMin: null,
})

onMounted(() => {
  void load(id.value)
})

async function submitEpisode(): Promise<void> {
  await addEpisode(id.value, {
    seasonNo: form.seasonNo,
    episodeNo: form.episodeNo,
    title: form.title || undefined,
    durationMin: form.durationMin ?? undefined,
  })
  form.episodeNo += 1
  form.title = ''
  form.durationMin = null
}

async function onDeleteSerial(): Promise<void> {
  if (!window.confirm(t('serialDetail.deleteConfirm'))) return
  await removeSerial(id.value)
  await router.push({ name: 'serials' })
}
</script>

<template>
  <section class="detail">
    <RouterLink class="back" to="/serials"><i class="pi pi-arrow-left" /> {{ t('nav.serials') }}</RouterLink>

    <div v-if="loading" class="centered"><ProgressSpinner style="width: 42px; height: 42px" /></div>
    <Message v-else-if="error" severity="error">{{ t('movies.error') }}: {{ error }}</Message>

    <template v-else-if="serial">
      <div class="header">
        <img v-if="serial.posterUrl" :src="serial.posterUrl" :alt="serial.title" class="poster" />
        <div v-else class="poster placeholder"><i class="pi pi-desktop" /></div>

        <div class="info">
          <h1>{{ serial.title }} <span v-if="serial.releaseYear" class="year">({{ serial.releaseYear }})</span></h1>
          <div class="status-line">
            <Tag :value="t(`status.${serial.watchStatus}`)" :severity="SEVERITY[serial.watchStatus]" />
            <Tag v-if="serial.serialStatus" :value="t(`serialStatus.${serial.serialStatus}`)" severity="contrast" />
          </div>

          <dl class="facts">
            <template v-if="serial.genres.length">
              <dt>{{ t('detail.genres') }}</dt>
              <dd>{{ serial.genres.join(', ') }}</dd>
            </template>
            <template v-if="serial.director">
              <dt>{{ t('detail.director') }}</dt>
              <dd>{{ serial.director }}</dd>
            </template>
            <template v-if="serial.seasonCount != null">
              <dt>{{ t('serialDetail.seasons') }}</dt>
              <dd>{{ serial.seasonCount }}</dd>
            </template>
            <template v-if="serial.episodeCount != null">
              <dt>{{ t('serialDetail.episodesCount') }}</dt>
              <dd>{{ serial.episodeCount }}</dd>
            </template>
            <template v-if="serial.imdbRating != null || serial.tmdbId != null">
              <dt>{{ t('detail.imdb') }}</dt>
              <dd class="imdb">
                <span>{{ serial.imdbRating != null ? `⭐ ${serial.imdbRating}` : '—' }}</span>
                <Button
                  v-if="serial.tmdbId != null"
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
            <template v-if="serial.personalRating != null">
              <dt>{{ t('detail.personal') }}</dt>
              <dd>⭐ {{ serial.personalRating }}</dd>
            </template>
            <template v-if="serial.language">
              <dt>{{ t('detail.watchedLanguage') }}</dt>
              <dd>{{ serial.language }}</dd>
            </template>
          </dl>

          <p v-if="serial.overview" class="overview">{{ serial.overview }}</p>
          <p v-if="serial.personalNote" class="note">{{ serial.personalNote }}</p>

          <div v-if="!editing" class="actions">
            <Button :label="t('edit.edit')" icon="pi pi-pencil" outlined @click="startEdit" />
            <Button :label="t('serialDetail.delete')" icon="pi pi-trash" severity="danger" outlined @click="onDeleteSerial" />
          </div>

          <form v-else class="edit-form" @submit.prevent="saveEdit">
            <label>{{ t('edit.status') }}
              <Select v-model="edit.watchStatus" :options="watchStatusOptions" option-label="label" option-value="value" />
            </label>
            <label>{{ t('edit.serialStatus') }}
              <Select v-model="edit.serialStatus" :options="serialStatusOptions" option-label="label" option-value="value" />
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

      <section class="episodes">
        <h2>{{ t('episodes.title') }}</h2>

        <form class="episode-form" @submit.prevent="submitEpisode">
          <label>{{ t('episodes.seasonNo') }}<InputNumber v-model="form.seasonNo" :min="1" show-buttons class="num" /></label>
          <label>{{ t('episodes.episodeNo') }}<InputNumber v-model="form.episodeNo" :min="1" show-buttons class="num" /></label>
          <InputText v-model="form.title" :placeholder="t('episodes.epTitle')" class="grow" />
          <InputNumber v-model="form.durationMin" :min="1" :placeholder="t('episodes.duration')" class="dur" />
          <Button type="submit" :label="t('episodes.add')" icon="pi pi-plus" />
        </form>

        <Message v-if="seasons.length === 0" severity="secondary">{{ t('episodes.empty') }}</Message>

        <div v-for="group in seasons" :key="group.season" class="season">
          <h3>{{ t('episodes.season') }} {{ group.season }}</h3>
          <ul class="episode-list">
            <li v-for="ep in group.episodes" :key="ep.id" :class="{ watched: ep.watched }">
              <span class="ep-no">E{{ String(ep.episodeNo).padStart(2, '0') }}</span>
              <span class="ep-title">{{ ep.title || '—' }}</span>
              <span v-if="ep.durationMin" class="muted">{{ ep.durationMin }}'</span>
              <Button
                v-if="!ep.watched"
                size="small"
                outlined
                icon="pi pi-check"
                :label="t('episodes.markWatched')"
                @click="markEpisodeWatched(id, ep.id)"
              />
              <Tag v-else :value="t('episodes.watched')" severity="success" />
              <Button text rounded severity="danger" size="small" icon="pi pi-times" @click="deleteEpisode(id, ep.id)" />
            </li>
          </ul>
        </div>
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
  gap: 0.6rem;
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
.episodes {
  margin-top: 3rem;
}
.episodes h2 {
  font-size: 1.2rem;
  margin: 0 0 1rem;
}
.episode-form {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  align-items: flex-end;
  margin-bottom: 1.5rem;
}
.episode-form label {
  display: flex;
  flex-direction: column;
  font-size: 0.75rem;
  gap: 0.25rem;
  color: var(--p-text-muted-color);
}
.episode-form .num :deep(.p-inputnumber-input) {
  width: 80px;
}
.episode-form .grow {
  flex: 1;
  min-width: 140px;
}
.episode-form .dur :deep(.p-inputnumber-input) {
  width: 90px;
}
.season {
  margin-bottom: 1.5rem;
}
.season h3 {
  font-size: 1rem;
  margin: 0 0 0.6rem;
  color: var(--p-text-muted-color);
}
.episode-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.episode-list li {
  display: flex;
  gap: 0.8rem;
  align-items: center;
  padding: 0.5rem 0.9rem;
  background: var(--p-content-background);
  border: 1px solid var(--p-content-border-color);
  border-radius: var(--p-border-radius-md, 8px);
}
.episode-list li.watched {
  border-color: var(--p-green-500, #22c55e);
}
.ep-no {
  font-weight: 700;
  font-size: 0.85rem;
  color: var(--p-text-muted-color);
}
.ep-title {
  flex: 1;
}
.episode-list li :deep(.p-button:last-child) {
  margin-left: auto;
}
</style>
