<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useMovies } from '@/composables/useMovies'
import { useSettingsStore } from '@/stores/settings'
import FilterBar from '@/components/FilterBar.vue'
import MediaCollection from '@/components/MediaCollection.vue'
import SelectButton from 'primevue/selectbutton'
import Paginator from 'primevue/paginator'
import ProgressSpinner from 'primevue/progressspinner'
import Message from 'primevue/message'
import type { CollectionItem, ListFilter, ViewMode } from '@/api/types'

const { t } = useI18n()
const { movies, loading, error, totalElements, fetchMovies } = useMovies()
const settings = useSettingsStore()

const SIZE = 20
const filter = ref<ListFilter>({})
const page = ref(0)

const viewOptions = [
  { value: 'grid', icon: 'pi pi-th-large' },
  { value: 'list', icon: 'pi pi-bars' },
]

function load(): void {
  void fetchMovies({ ...filter.value, page: page.value, size: SIZE })
}

onMounted(load)

function onFilter(f: ListFilter): void {
  filter.value = f
  page.value = 0
  load()
}

function onPage(e: { page: number }): void {
  page.value = e.page
  load()
}

const items = computed<CollectionItem[]>(() =>
  movies.value.map((m) => ({
    key: `m${m.id}`,
    title: m.title,
    posterUrl: m.posterUrl,
    releaseYear: m.releaseYear,
    status: m.status,
    imdbRating: m.imdbRating,
    kind: 'movie',
    to: { name: 'movie-detail', params: { id: m.id } },
  })),
)
</script>

<template>
  <section class="movies">
    <div class="head">
      <h1>{{ t('movies.title') }}</h1>
      <SelectButton
        :model-value="settings.view"
        :options="viewOptions"
        option-value="value"
        :allow-empty="false"
        @update:model-value="(v: ViewMode) => settings.setView(v)"
      >
        <template #option="{ option }"><i :class="option.icon" /></template>
      </SelectButton>
    </div>

    <FilterBar @change="onFilter" />

    <div v-if="loading" class="centered"><ProgressSpinner style="width: 42px; height: 42px" /></div>
    <Message v-else-if="error" severity="error">{{ t('movies.error') }}: {{ error }}</Message>
    <Message v-else-if="items.length === 0" severity="secondary">{{ t('movies.empty') }}</Message>

    <template v-else>
      <MediaCollection :items="items" :view="settings.view" />
      <Paginator
        v-if="totalElements > SIZE"
        :rows="SIZE"
        :total-records="totalElements"
        :first="page * SIZE"
        class="pager"
        @page="onPage"
      />
    </template>
  </section>
</template>

<style scoped>
.movies {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0.5rem 0 2rem;
}
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.2rem;
}
h1 {
  font-size: 1.6rem;
  font-weight: 700;
  margin: 0;
}
.centered {
  display: flex;
  justify-content: center;
  padding: 3rem 0;
}
.pager {
  margin-top: 1.5rem;
  background: transparent;
}
</style>
