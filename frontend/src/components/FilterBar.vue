<script setup lang="ts">
import { reactive } from 'vue'
import { useI18n } from 'vue-i18n'
import type { ListFilter, WatchStatus } from '@/api/types'

const emit = defineEmits<{ change: [filter: ListFilter] }>()
const { t } = useI18n()

const statuses: WatchStatus[] = ['PLAN_TO_WATCH', 'WATCHING', 'COMPLETED', 'DROPPED']

const state = reactive<{ search: string; status: '' | WatchStatus; genre: string; minRating: number | null }>({
  search: '',
  status: '',
  genre: '',
  minRating: null,
})

function build(): ListFilter {
  return {
    search: state.search.trim() || undefined,
    status: state.status || undefined,
    genre: state.genre.trim() || undefined,
    minRating: state.minRating ?? undefined,
  }
}

let timer: ReturnType<typeof setTimeout> | undefined

function emitDebounced(): void {
  if (timer) clearTimeout(timer)
  timer = setTimeout(() => emit('change', build()), 300)
}

function emitNow(): void {
  emit('change', build())
}

function reset(): void {
  state.search = ''
  state.status = ''
  state.genre = ''
  state.minRating = null
  emitNow()
}
</script>

<template>
  <div class="filter-bar">
    <input
      v-model="state.search"
      class="search"
      :placeholder="t('filter.search')"
      @input="emitDebounced"
    />
    <select v-model="state.status" @change="emitNow">
      <option value="">{{ t('filter.allStatuses') }}</option>
      <option v-for="s in statuses" :key="s" :value="s">{{ t(`status.${s}`) }}</option>
    </select>
    <input
      v-model="state.genre"
      class="genre"
      :placeholder="t('filter.genre')"
      @input="emitDebounced"
    />
    <input
      v-model.number="state.minRating"
      class="rating"
      type="number"
      min="0"
      max="10"
      step="0.5"
      :placeholder="t('filter.minRating')"
      @change="emitNow"
    />
    <button class="reset" @click="reset">{{ t('filter.reset') }}</button>
  </div>
</template>

<style scoped>
.filter-bar {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 1.2rem;
  align-items: center;
}
.search {
  flex: 1;
  min-width: 180px;
}
.genre {
  width: 130px;
}
.rating {
  width: 100px;
}
.reset {
  background: transparent;
  border: 1px solid rgba(128, 128, 128, 0.4);
}
</style>
