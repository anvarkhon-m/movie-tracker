<script setup lang="ts">
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDiscover } from '@/composables/useDiscover'
import SelectButton from 'primevue/selectbutton'
import IconField from 'primevue/iconfield'
import InputIcon from 'primevue/inputicon'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import ProgressSpinner from 'primevue/progressspinner'
import Message from 'primevue/message'
import type { DiscoverType } from '@/api/types'

const { t } = useI18n()
const { results, loading, error, searched, addingId, addedIds, search, add } = useDiscover()

const type = ref<DiscoverType>('movie')
const query = ref('')

const typeOptions = computed(() => [
  { label: t('discover.movies'), value: 'movie' as DiscoverType },
  { label: t('discover.serials'), value: 'serial' as DiscoverType },
])

function submit(): void {
  void search(type.value, query.value)
}

function switchType(): void {
  if (query.value.trim()) submit()
}
</script>

<template>
  <section class="discover">
    <h1>{{ t('discover.title') }}</h1>

    <div class="controls">
      <SelectButton
        v-model="type"
        :options="typeOptions"
        option-label="label"
        option-value="value"
        :allow-empty="false"
        @change="switchType"
      />
      <form class="search" @submit.prevent="submit">
        <IconField class="field">
          <InputIcon class="pi pi-search" />
          <InputText v-model="query" :placeholder="t('discover.placeholder')" fluid />
        </IconField>
        <Button type="submit" :label="t('discover.searchBtn')" />
      </form>
    </div>

    <div v-if="loading" class="centered"><ProgressSpinner style="width: 42px; height: 42px" /></div>
    <Message v-else-if="error" severity="error">{{ t('movies.error') }}: {{ error }}</Message>
    <Message v-else-if="!searched" severity="secondary">{{ t('discover.hint') }}</Message>
    <Message v-else-if="results.length === 0" severity="secondary">{{ t('discover.empty') }}</Message>

    <ul v-else class="grid">
      <li v-for="item in results" :key="item.tmdbId" class="card">
        <div class="poster-wrap">
          <img v-if="item.posterUrl" :src="item.posterUrl" :alt="item.title" class="poster" />
          <div v-else class="poster placeholder"><i class="pi pi-image" /></div>
          <span v-if="item.rating != null" class="badge"><i class="pi pi-star-fill" /> {{ item.rating }}</span>
        </div>
        <div class="meta">
          <span class="title">{{ item.title }}</span>
          <span v-if="item.releaseYear" class="muted">{{ item.releaseYear }}</span>
          <Button
            class="add"
            size="small"
            :disabled="addingId === item.tmdbId || addedIds.has(item.tmdbId)"
            :severity="addedIds.has(item.tmdbId) ? 'success' : 'primary'"
            :icon="addedIds.has(item.tmdbId) ? 'pi pi-check' : 'pi pi-plus'"
            :label="addedIds.has(item.tmdbId) ? t('discover.added') : addingId === item.tmdbId ? t('discover.adding') : t('discover.add')"
            @click="add(type, item.tmdbId)"
          />
        </div>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.discover {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0.5rem 0 2rem;
}
h1 {
  font-size: 1.6rem;
  font-weight: 700;
  margin: 0 0 1.2rem;
}
.controls {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  align-items: center;
  margin-bottom: 1.5rem;
}
.search {
  display: flex;
  gap: 0.5rem;
  flex: 1;
  min-width: 260px;
}
.field {
  flex: 1;
}
.centered {
  display: flex;
  justify-content: center;
  padding: 3rem 0;
}
.grid {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
  gap: 1.4rem;
}
.card {
  background: var(--p-content-background);
  border: 1px solid var(--p-content-border-color);
  border-radius: var(--p-border-radius-lg, 12px);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
.poster-wrap {
  position: relative;
}
.poster {
  width: 100%;
  aspect-ratio: 2 / 3;
  object-fit: cover;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.2rem;
  color: var(--p-text-muted-color);
}
.placeholder {
  background: color-mix(in srgb, var(--p-text-color) 8%, transparent);
}
.badge {
  position: absolute;
  top: 0.5rem;
  left: 0.5rem;
  background: rgba(0, 0, 0, 0.78);
  color: #ffd54a;
  padding: 0.12rem 0.45rem;
  border-radius: 6px;
  font-size: 0.78rem;
  font-weight: 600;
}
.meta {
  padding: 0.7rem 0.8rem;
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  flex: 1;
}
.title {
  font-weight: 600;
  font-size: 0.92rem;
}
.muted {
  color: var(--p-text-muted-color);
  font-size: 0.82rem;
}
.add {
  margin-top: auto;
}
</style>
