<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDiscover } from '@/composables/useDiscover'
import type { DiscoverType } from '@/api/types'

const { t } = useI18n()
const { results, loading, error, searched, addingId, addedIds, search, add } = useDiscover()

const type = ref<DiscoverType>('movie')
const query = ref('')

function submit(): void {
  void search(type.value, query.value)
}

function switchType(next: DiscoverType): void {
  if (type.value === next) return
  type.value = next
  if (query.value.trim()) submit()
}
</script>

<template>
  <section class="discover">
    <h1>{{ t('discover.title') }}</h1>

    <div class="controls">
      <div class="toggle">
        <button :class="{ active: type === 'movie' }" @click="switchType('movie')">
          {{ t('discover.movies') }}
        </button>
        <button :class="{ active: type === 'serial' }" @click="switchType('serial')">
          {{ t('discover.serials') }}
        </button>
      </div>
      <form class="search" @submit.prevent="submit">
        <input v-model="query" :placeholder="t('discover.placeholder')" />
        <button type="submit">{{ t('discover.searchBtn') }}</button>
      </form>
    </div>

    <p v-if="loading">{{ t('movies.loading') }}</p>
    <p v-else-if="error" class="error">{{ t('movies.error') }}: {{ error }}</p>
    <p v-else-if="!searched" class="hint">{{ t('discover.hint') }}</p>
    <p v-else-if="results.length === 0">{{ t('discover.empty') }}</p>

    <ul v-else class="grid">
      <li v-for="item in results" :key="item.tmdbId" class="card">
        <img v-if="item.posterUrl" :src="item.posterUrl" :alt="item.title" class="poster" />
        <div v-else class="poster placeholder">🎬</div>
        <div class="meta">
          <h3>{{ item.title }}</h3>
          <p class="sub">
            <span v-if="item.releaseYear">{{ item.releaseYear }}</span>
            <span v-if="item.rating != null">⭐ {{ item.rating }}</span>
          </p>
          <button
            class="add-btn"
            :disabled="addingId === item.tmdbId || addedIds.has(item.tmdbId)"
            @click="add(type, item.tmdbId)"
          >
            <template v-if="addedIds.has(item.tmdbId)">{{ t('discover.added') }}</template>
            <template v-else-if="addingId === item.tmdbId">{{ t('discover.adding') }}</template>
            <template v-else>{{ t('discover.add') }}</template>
          </button>
        </div>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.discover {
  max-width: 960px;
  margin: 0 auto;
  padding: 1rem 0;
}
.controls {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  margin-bottom: 1.2rem;
}
.toggle {
  display: flex;
  gap: 0.3rem;
}
.toggle button {
  background: transparent;
  border: 1px solid rgba(128, 128, 128, 0.4);
}
.toggle button.active {
  background: #646cff;
  border-color: #646cff;
}
.search {
  display: flex;
  gap: 0.5rem;
  flex: 1;
  min-width: 240px;
}
.search input {
  flex: 1;
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
  display: flex;
  flex-direction: column;
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
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  flex: 1;
}
.meta h3 {
  font-size: 0.95rem;
  margin: 0;
}
.sub {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  opacity: 0.75;
  margin: 0;
}
.add-btn {
  margin-top: auto;
  font-size: 0.85rem;
}
.add-btn:disabled {
  opacity: 0.6;
  cursor: default;
}
.hint {
  opacity: 0.7;
}
.error {
  color: #e05252;
}
</style>
