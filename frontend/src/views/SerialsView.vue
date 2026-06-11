<script setup lang="ts">
import { onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useSerials } from '@/composables/useSerials'
import FilterBar from '@/components/FilterBar.vue'
import type { ListFilter } from '@/api/types'

const { t } = useI18n()
const { serials, loading, error, fetchSerials } = useSerials()

onMounted(() => {
  void fetchSerials({ page: 0, size: 20 })
})

function onFilter(filter: ListFilter): void {
  void fetchSerials({ ...filter, page: 0, size: 20 })
}
</script>

<template>
  <section class="serials">
    <h1>{{ t('serials.title') }}</h1>

    <FilterBar @change="onFilter" />

    <p v-if="loading">{{ t('movies.loading') }}</p>
    <p v-else-if="error" class="error">{{ t('movies.error') }}: {{ error }}</p>
    <p v-else-if="serials.length === 0">{{ t('serials.empty') }}</p>

    <ul v-else class="grid">
      <li v-for="serial in serials" :key="serial.id" class="card">
        <RouterLink :to="{ name: 'serial-detail', params: { id: serial.id } }" class="card-link">
          <img v-if="serial.posterUrl" :src="serial.posterUrl" :alt="serial.title" class="poster" />
          <div v-else class="poster placeholder">📺</div>
          <div class="meta">
            <h3>{{ serial.title }}</h3>
            <p class="sub">
              <span v-if="serial.releaseYear">{{ serial.releaseYear }}</span>
              <span class="status">{{ t(`status.${serial.watchStatus}`) }}</span>
            </p>
            <p v-if="serial.personalRating != null" class="rating">⭐ {{ serial.personalRating }}</p>
          </div>
        </RouterLink>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.serials {
  max-width: 960px;
  margin: 0 auto;
  padding: 1rem 0;
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
  transition: border-color 0.2s;
}
.card:hover {
  border-color: #646cff;
}
.card-link {
  display: block;
  color: inherit;
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
}
.meta h3 {
  font-size: 0.95rem;
  margin: 0 0 0.3rem;
}
.sub {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  opacity: 0.75;
  margin: 0;
}
.rating {
  margin: 0.3rem 0 0;
  font-size: 0.85rem;
}
.error {
  color: #e05252;
}
</style>
