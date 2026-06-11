<script setup lang="ts">
import type { MediaItem } from '@/api/types'

defineProps<{ items: MediaItem[] }>()
</script>

<template>
  <ul class="grid">
    <li v-for="item in items" :key="item.key" class="poster-card">
      <RouterLink :to="item.to" class="card-link">
        <div class="poster-wrap">
          <img v-if="item.posterUrl" :src="item.posterUrl" :alt="item.title" class="poster" />
          <div v-else class="poster placeholder">{{ item.kind === 'movie' ? '🎬' : '📺' }}</div>
          <span v-if="item.badge" class="badge">{{ item.badge }}</span>
          <span class="kind">{{ item.kind === 'movie' ? '🎬' : '📺' }}</span>
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
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 1rem;
}
.poster-card {
  border: 1px solid rgba(128, 128, 128, 0.3);
  border-radius: 8px;
  overflow: hidden;
  transition: border-color 0.2s;
}
.poster-card:hover {
  border-color: #646cff;
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
  font-size: 2rem;
}
.placeholder {
  background: rgba(128, 128, 128, 0.15);
}
.badge {
  position: absolute;
  top: 0.4rem;
  left: 0.4rem;
  background: rgba(0, 0, 0, 0.75);
  color: #ffd54a;
  padding: 0.1rem 0.4rem;
  border-radius: 6px;
  font-size: 0.78rem;
}
.kind {
  position: absolute;
  top: 0.4rem;
  right: 0.4rem;
  font-size: 0.85rem;
  opacity: 0.85;
}
.title {
  display: block;
  padding: 0.4rem 0.6rem;
  font-size: 0.85rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
