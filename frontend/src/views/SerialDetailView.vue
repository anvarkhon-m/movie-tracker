<script setup lang="ts">
import { computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useSerial } from '@/composables/useSerial'

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
  removeSerial,
} = useSerial()

const id = computed(() => Number(route.params.id))

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
    <RouterLink class="back" to="/serials">{{ t('serialDetail.back') }}</RouterLink>

    <p v-if="loading">{{ t('movies.loading') }}</p>
    <p v-else-if="error" class="error">{{ t('movies.error') }}: {{ error }}</p>

    <template v-else-if="serial">
      <div class="header">
        <img v-if="serial.posterUrl" :src="serial.posterUrl" :alt="serial.title" class="poster" />
        <div v-else class="poster placeholder">📺</div>

        <div class="info">
          <h1>{{ serial.title }} <span v-if="serial.releaseYear" class="year">({{ serial.releaseYear }})</span></h1>
          <p class="status-line">
            <span class="badge">{{ t(`status.${serial.watchStatus}`) }}</span>
            <span v-if="serial.serialStatus" class="badge alt">{{ t(`serialStatus.${serial.serialStatus}`) }}</span>
          </p>

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
            <template v-if="serial.imdbRating != null">
              <dt>{{ t('detail.imdb') }}</dt>
              <dd>⭐ {{ serial.imdbRating }}</dd>
            </template>
            <template v-if="serial.personalRating != null">
              <dt>{{ t('detail.personal') }}</dt>
              <dd>⭐ {{ serial.personalRating }}</dd>
            </template>
          </dl>

          <p v-if="serial.personalNote" class="note">{{ serial.personalNote }}</p>

          <button class="danger" @click="onDeleteSerial">{{ t('serialDetail.delete') }}</button>
        </div>
      </div>

      <section class="episodes">
        <h2>{{ t('episodes.title') }}</h2>

        <form class="episode-form" @submit.prevent="submitEpisode">
          <label>{{ t('episodes.seasonNo') }}<input v-model.number="form.seasonNo" type="number" min="1" required /></label>
          <label>{{ t('episodes.episodeNo') }}<input v-model.number="form.episodeNo" type="number" min="1" required /></label>
          <input v-model="form.title" :placeholder="t('episodes.epTitle')" class="grow" />
          <input v-model.number="form.durationMin" type="number" min="1" :placeholder="t('episodes.duration')" class="dur" />
          <button type="submit">{{ t('episodes.add') }}</button>
        </form>

        <p v-if="seasons.length === 0" class="muted">{{ t('episodes.empty') }}</p>

        <div v-for="group in seasons" :key="group.season" class="season">
          <h3>{{ t('episodes.season') }} {{ group.season }}</h3>
          <ul class="episode-list">
            <li v-for="ep in group.episodes" :key="ep.id" :class="{ watched: ep.watched }">
              <span class="ep-no">E{{ String(ep.episodeNo).padStart(2, '0') }}</span>
              <span class="ep-title">{{ ep.title || '—' }}</span>
              <span v-if="ep.durationMin" class="muted">{{ ep.durationMin }}'</span>
              <button
                v-if="!ep.watched"
                class="watch-btn"
                @click="markEpisodeWatched(id, ep.id)"
              >
                {{ t('episodes.markWatched') }}
              </button>
              <span v-else class="watched-tag">{{ t('episodes.watched') }}</span>
              <button class="link-danger" @click="deleteEpisode(id, ep.id)">✕</button>
            </li>
          </ul>
        </div>
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
  gap: 0.5rem;
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
.badge.alt {
  background: #2a8c5a;
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
.episodes {
  margin-top: 2.5rem;
}
.episode-form {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  align-items: flex-end;
  margin-bottom: 1.2rem;
}
.episode-form label {
  display: flex;
  flex-direction: column;
  font-size: 0.75rem;
  gap: 0.2rem;
}
.episode-form input[type='number'] {
  width: 70px;
}
.episode-form .grow {
  flex: 1;
  min-width: 140px;
}
.episode-form .dur {
  width: 80px;
}
.season {
  margin-bottom: 1.2rem;
}
.season h3 {
  font-size: 1rem;
  margin: 0 0 0.5rem;
  opacity: 0.85;
}
.episode-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}
.episode-list li {
  display: flex;
  gap: 0.8rem;
  align-items: center;
  padding: 0.4rem 0.7rem;
  border: 1px solid rgba(128, 128, 128, 0.3);
  border-radius: 6px;
}
.episode-list li.watched {
  border-color: rgba(42, 140, 90, 0.6);
}
.ep-no {
  font-weight: 700;
  font-size: 0.85rem;
  opacity: 0.8;
}
.ep-title {
  flex: 1;
}
.watch-btn {
  font-size: 0.78rem;
  padding: 0.3rem 0.6rem;
}
.watched-tag {
  font-size: 0.8rem;
  color: #2a8c5a;
}
.link-danger {
  background: transparent;
  border: none;
  color: #e05252;
  padding: 0.2rem 0.4rem;
}
.error {
  color: #e05252;
}
</style>
