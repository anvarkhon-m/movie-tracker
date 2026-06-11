<script setup lang="ts">
import { computed, reactive } from 'vue'
import { useI18n } from 'vue-i18n'
import IconField from 'primevue/iconfield'
import InputIcon from 'primevue/inputicon'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import InputNumber from 'primevue/inputnumber'
import Button from 'primevue/button'
import type { ListFilter, WatchStatus } from '@/api/types'

const emit = defineEmits<{ change: [filter: ListFilter] }>()
const { t } = useI18n()

const statuses: WatchStatus[] = ['PLAN_TO_WATCH', 'WATCHING', 'COMPLETED', 'DROPPED']

const statusOptions = computed(() =>
  statuses.map((s) => ({ label: t(`status.${s}`), value: s })),
)

const state = reactive<{
  search: string
  status: WatchStatus | null
  genre: string
  minRating: number | null
}>({ search: '', status: null, genre: '', minRating: null })

function build(): ListFilter {
  return {
    search: state.search.trim() || undefined,
    status: state.status ?? undefined,
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
  state.status = null
  state.genre = ''
  state.minRating = null
  emitNow()
}
</script>

<template>
  <div class="filter-bar">
    <IconField class="search">
      <InputIcon class="pi pi-search" />
      <InputText v-model="state.search" :placeholder="t('filter.search')" fluid @input="emitDebounced" />
    </IconField>
    <Select
      v-model="state.status"
      :options="statusOptions"
      option-label="label"
      option-value="value"
      :placeholder="t('filter.allStatuses')"
      show-clear
      class="status"
      @change="emitNow"
    />
    <InputText v-model="state.genre" :placeholder="t('filter.genre')" class="genre" @input="emitDebounced" />
    <InputNumber
      v-model="state.minRating"
      :min="0"
      :max="10"
      :placeholder="t('filter.minRating')"
      class="rating"
      @blur="emitNow"
    />
    <Button :label="t('filter.reset')" severity="secondary" outlined @click="reset" />
  </div>
</template>

<style scoped>
.filter-bar {
  display: flex;
  gap: 0.6rem;
  flex-wrap: wrap;
  margin-bottom: 1.5rem;
  align-items: center;
}
.search {
  flex: 1;
  min-width: 200px;
}
.genre {
  width: 140px;
}
.rating {
  width: 120px;
}
</style>
