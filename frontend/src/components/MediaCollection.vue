<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import Tag from 'primevue/tag'
import type { CollectionItem, ViewMode, WatchStatus } from '@/api/types'

defineProps<{ items: CollectionItem[]; view: ViewMode }>()

const { t } = useI18n()

const SEVERITY: Record<WatchStatus, string> = {
  PLAN_TO_WATCH: 'secondary',
  WATCHING: 'info',
  COMPLETED: 'success',
  DROPPED: 'danger',
}
</script>

<template>
  <!-- Grid: katta poster kartalar -->
  <ul v-if="view === 'grid'" class="grid">
    <li v-for="item in items" :key="item.key" class="card">
      <RouterLink :to="item.to" class="card-link">
        <div class="poster-wrap">
          <img v-if="item.posterUrl" :src="item.posterUrl" :alt="item.title" class="poster" />
          <div v-else class="poster placeholder">
            <i :class="['pi', item.kind === 'movie' ? 'pi-video' : 'pi-desktop']" />
          </div>
        </div>
        <div class="meta">
          <Tag :value="t(`status.${item.status}`)" :severity="SEVERITY[item.status]" class="status-tag" />
          <span class="title">{{ item.title }}</span>
          <div class="sub">
            <span v-if="item.releaseYear" class="muted">{{ item.releaseYear }}</span>
            <span v-if="item.imdbRating != null" class="rating">
              <i class="pi pi-star-fill" /> {{ item.imdbRating }}
            </span>
          </div>
        </div>
      </RouterLink>
    </li>
  </ul>

  <!-- List: kichik thumbnail + qator -->
  <ul v-else class="list">
    <li v-for="item in items" :key="item.key" class="row">
      <RouterLink :to="item.to" class="row-link">
        <img v-if="item.posterUrl" :src="item.posterUrl" :alt="item.title" class="thumb" />
        <div v-else class="thumb placeholder">
          <i :class="['pi', item.kind === 'movie' ? 'pi-video' : 'pi-desktop']" />
        </div>
        <div class="row-main">
          <span class="row-title">{{ item.title }}</span>
          <span v-if="item.releaseYear" class="muted year">{{ item.releaseYear }}</span>
        </div>
        <div class="row-meta">
          <span v-if="item.lastWatchedAt" class="meta-cell watched" :title="t('list.watched')">
            <i class="pi pi-calendar" /> {{ item.lastWatchedAt }}
          </span>
          <span v-if="item.durationMin" class="meta-cell runtime" :title="t('list.runtime')">
            <i class="pi pi-clock" /> {{ item.durationMin }}m
          </span>
          <span v-if="item.personalRating != null" class="meta-cell personal" :title="t('list.personal')">
            <i class="pi pi-star-fill" /> {{ item.personalRating }}
          </span>
          <span v-if="item.imdbRating != null" class="meta-cell imdb" :title="t('detail.imdb')">
            IMDb {{ item.imdbRating }}
          </span>
          <Tag :value="t(`status.${item.status}`)" :severity="SEVERITY[item.status]" />
        </div>
      </RouterLink>
    </li>
  </ul>
</template>

<style scoped>
/* --- Grid --- */
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
  transition: transform 0.15s ease, box-shadow 0.15s ease, border-color 0.15s ease;
}
.card:hover {
  transform: translateY(-3px);
  border-color: var(--p-primary-color);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.18);
}
.card-link {
  display: block;
  color: inherit;
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
  font-size: 2.4rem;
  color: var(--p-text-muted-color);
}
.placeholder {
  background: var(--p-surface-100);
}
:global(.app-dark) .placeholder {
  background: var(--p-surface-800);
}
.meta {
  padding: 0.7rem 0.8rem 0.85rem;
  display: flex;
  flex-direction: column;
}
.status-tag {
  align-self: flex-start;
  margin-bottom: 0.5rem;
}
.title {
  font-weight: 600;
  font-size: 0.95rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 0.35rem;
}
.sub {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.82rem;
}

/* --- List --- */
.list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
}
.row {
  background: var(--p-content-background);
  border: 1px solid var(--p-content-border-color);
  border-radius: var(--p-border-radius-md, 8px);
  transition: border-color 0.15s;
}
.row:hover {
  border-color: var(--p-primary-color);
}
.row-link {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.5rem 0.8rem;
  color: inherit;
}
.thumb {
  width: 40px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--p-text-muted-color);
}
.row-main {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: baseline;
  gap: 0.6rem;
}
.row-title {
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.row-meta {
  display: flex;
  align-items: center;
  gap: 0.9rem;
  flex-shrink: 0;
}
.meta-cell {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  font-size: 0.82rem;
  white-space: nowrap;
}
.watched,
.runtime,
.year {
  color: var(--p-text-muted-color);
}
.personal {
  color: #f5a623;
  font-weight: 600;
}
.imdb {
  color: var(--p-text-color);
  font-weight: 600;
  background: var(--p-surface-100);
  padding: 0.1rem 0.4rem;
  border-radius: 4px;
}
:global(.app-dark) .imdb {
  background: var(--p-surface-800);
}

/* --- Shared --- */
.muted {
  color: var(--p-text-muted-color);
  font-size: 0.85rem;
}
.rating {
  color: #f5a623;
  font-weight: 600;
  font-size: 0.85rem;
  flex-shrink: 0;
}
@media (max-width: 720px) {
  .watched,
  .runtime {
    display: none;
  }
}
@media (max-width: 560px) {
  .year {
    display: none;
  }
}
</style>
