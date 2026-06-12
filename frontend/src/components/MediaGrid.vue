<script setup lang="ts">
import type { MediaItem } from '@/api/types'

defineProps<{ items: MediaItem[] }>()
</script>

<template>
  <ul class="grid">
    <li v-for="item in items" :key="item.key" class="card">
      <RouterLink :to="item.to" class="card-link">
        <div class="poster-wrap">
          <img v-if="item.posterUrl" :src="item.posterUrl" :alt="item.title" class="poster" />
          <div v-else class="poster placeholder">
            <i :class="['pi', item.kind === 'movie' ? 'pi-video' : 'pi-desktop']" />
          </div>
          <span v-if="item.badge" class="badge">{{ item.badge }}</span>
          <i :class="['kind', 'pi', item.kind === 'movie' ? 'pi-video' : 'pi-desktop']" />
        </div>
        <span class="title">{{ item.title }}</span>
      </RouterLink>
    </li>
  </ul>
</template>

<style scoped>
.grid {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 1.2rem;
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
.kind {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  font-size: 0.85rem;
  color: #fff;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.6);
}
.title {
  display: block;
  padding: 0.55rem 0.7rem;
  font-size: 0.88rem;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
