<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useMovies } from '@/composables/useMovies'
import { useSettingsStore } from '@/stores/settings'
import FilterBar from '@/components/FilterBar.vue'
import MediaCollection from '@/components/MediaCollection.vue'
import Select from 'primevue/select'
import SelectButton from 'primevue/selectbutton'
import Paginator from 'primevue/paginator'
import ProgressSpinner from 'primevue/progressspinner'
import Message from 'primevue/message'
import type { CollectionItem, GridSize, ListFilter, ViewMode } from '@/api/types'

const { t } = useI18n()
const { movies, loading, error, totalElements, fetchMovies } = useMovies()
const settings = useSettingsStore()

const ALL = 10000
const filter = ref<ListFilter>({})
const page = ref(0)
const pageSize = ref(24)
const sort = ref('id,desc')

const sortOptions = computed(() => [
  { label: t('sort.added'), value: 'id,desc' },
  { label: t('sort.yearDesc'), value: 'releaseYear,desc' },
  { label: t('sort.yearAsc'), value: 'releaseYear,asc' },
  { label: t('sort.watched'), value: 'lastWatchedAt,desc' },
  { label: t('sort.imdb'), value: 'imdbRating,desc' },
  { label: t('sort.personal'), value: 'personalRating,desc' },
])
const pageSizeOptions = computed(() => [
  { label: '24', value: 24 },
  { label: '50', value: 50 },
  { label: '100', value: 100 },
  { label: t('page.all'), value: ALL },
])
const viewOptions = [
  { value: 'grid', icon: 'pi pi-th-large' },
  { value: 'list', icon: 'pi pi-bars' },
]
const sizeOptions: { value: GridSize; icon: string }[] = [
  { value: 's', icon: 'pi pi-minus' },
  { value: 'm', icon: 'pi pi-equals' },
  { value: 'l', icon: 'pi pi-plus' },
]

function load(): void {
  void fetchMovies({ ...filter.value, page: page.value, size: pageSize.value, sort: sort.value })
}

onMounted(load)

function reload(): void {
  page.value = 0
  load()
}

function onFilter(f: ListFilter): void {
  filter.value = f
  reload()
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
    personalRating: m.personalRating,
    durationMin: m.durationMin,
    lastWatchedAt: m.lastWatchedAt,
    kind: 'movie',
    to: { name: 'movie-detail', params: { id: m.id } },
  })),
)
</script>

<template>
  <section class="page">
    <div class="head">
      <h1>{{ t('movies.title') }}</h1>
      <div class="controls">
        <Select v-model="sort" :options="sortOptions" option-label="label" option-value="value" class="sort" @change="reload" />
        <Select v-model="pageSize" :options="pageSizeOptions" option-label="label" option-value="value" class="psize" @change="reload" />
        <SelectButton
          :model-value="settings.gridSize"
          :options="sizeOptions"
          option-value="value"
          :allow-empty="false"
          @update:model-value="(v: GridSize) => settings.setGridSize(v)"
        >
          <template #option="{ option }"><i :class="option.icon" /></template>
        </SelectButton>
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
    </div>

    <FilterBar @change="onFilter" />

    <div v-if="loading" class="centered"><ProgressSpinner style="width: 42px; height: 42px" /></div>
    <Message v-else-if="error" severity="error">{{ t('movies.error') }}: {{ error }}</Message>
    <Message v-else-if="items.length === 0" severity="secondary">{{ t('movies.empty') }}</Message>

    <template v-else>
      <MediaCollection :items="items" :view="settings.view" :size="settings.gridSize" />
      <Paginator
        v-if="totalElements > pageSize"
        :rows="pageSize"
        :total-records="totalElements"
        :first="page * pageSize"
        class="pager"
        @page="onPage"
      />
    </template>
  </section>
</template>

<style scoped>
.page {
  max-width: 1500px;
  margin: 0 auto;
  padding: 0.5rem 0 2rem;
}
.head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-bottom: 1.2rem;
  flex-wrap: wrap;
}
.controls {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  flex-wrap: wrap;
}
.sort {
  min-width: 170px;
}
.psize {
  min-width: 90px;
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
