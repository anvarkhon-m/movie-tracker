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

  <!-- List: ustunlarga tekislangan qatorlar -->
  <ul v-else class="list">
    <li v-for="item in items" :key="item.key" class="row">
      <RouterLink :to="item.to" class="row-link">
        <img v-if="item.posterUrl" :src="item.posterUrl" :alt="item.title" class="thumb" />
        <div v-else class="thumb placeholder">
          <i :class="['pi', item.kind === 'movie' ? 'pi-video' : 'pi-desktop']" />
        </div>

        <span class="c-title">
          {{ item.title }}
          <span v-if="item.releaseYear" class="t-year muted">· {{ item.releaseYear }}</span>
        </span>

        <span class="c-watched muted" :title="t('list.watched')">
          <template v-if="item.lastWatchedAt"><i class="pi pi-calendar" /> {{ item.lastWatchedAt }}</template>
        </span>
        <span class="c-runtime muted" :title="t('list.runtime')">
          <template v-if="item.durationMin"><i class="pi pi-clock" /> {{ item.durationMin }}m</template>
        </span>
        <span class="c-personal" :title="t('list.personal')">
          <template v-if="item.personalRating != null"><i class="pi pi-star-fill" /> {{ item.personalRating }}</template>
        </span>
        <span class="c-imdb" :title="t('detail.imdb')">
          <template v-if="item.imdbRating != null">IMDb {{ item.imdbRating }}</template>
        </span>
        <span class="c-status">
          <Tag :value="t(`status.${item.status}`)" :severity="SEVERITY[item.status]" />
        </span>
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
  background: color-mix(in srgb, var(--p-text-color) 8%, transparent);
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

/* --- List (aligned grid) --- */
.list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.55rem;
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
  display: grid;
  grid-template-columns: 44px minmax(0, 1fr) 118px 66px 56px 80px 104px;
  align-items: center;
  gap: 0.9rem;
  padding: 0.5rem 0.9rem;
  color: inherit;
}
.thumb {
  width: 40px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--p-text-muted-color);
}
.c-title {
  font-weight: 600;
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.t-year {
  font-weight: 400;
}
.c-watched,
.c-runtime,
.c-personal,
.c-imdb {
  display: inline-flex;
  align-items: center;
  gap: 0.3rem;
  font-size: 0.82rem;
  white-space: nowrap;
  justify-content: flex-end;
}
.c-status {
  justify-self: end;
}
.c-personal {
  color: #f5a623;
  font-weight: 600;
}
.c-imdb {
  font-weight: 600;
  color: var(--p-text-color);
}

/* --- Shared --- */
.muted {
  color: var(--p-text-muted-color);
}
.rating {
  color: #f5a623;
  font-weight: 600;
  font-size: 0.85rem;
}

@media (max-width: 820px) {
  .row-link {
    grid-template-columns: 44px minmax(0, 1fr) 56px 80px 104px;
  }
  .c-watched,
  .c-runtime {
    display: none;
  }
}
@media (max-width: 540px) {
  .row-link {
    grid-template-columns: 44px minmax(0, 1fr) 104px;
  }
  .c-personal,
  .c-imdb {
    display: none;
  }
}
</style>
