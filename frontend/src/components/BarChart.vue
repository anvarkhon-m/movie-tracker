<script setup lang="ts">
import { computed } from 'vue'

interface BarItem {
  label: string
  value: number
}

const props = defineProps<{ items: BarItem[] }>()

const max = computed(() => Math.max(1, ...props.items.map((i) => i.value)))
</script>

<template>
  <div class="bars">
    <div v-for="item in items" :key="item.label" class="row">
      <span class="label">{{ item.label }}</span>
      <div class="track">
        <div class="fill" :style="{ width: `${(item.value / max) * 100}%` }"></div>
      </div>
      <span class="value">{{ item.value }}</span>
    </div>
  </div>
</template>

<style scoped>
.bars {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}
.row {
  display: grid;
  grid-template-columns: 120px 1fr 2.5rem;
  align-items: center;
  gap: 0.6rem;
  font-size: 0.85rem;
}
.label {
  text-align: right;
  opacity: 0.85;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.track {
  background: color-mix(in srgb, var(--p-text-color) 12%, transparent);
  border-radius: 4px;
  height: 18px;
  overflow: hidden;
}
.fill {
  height: 100%;
  background: linear-gradient(90deg, var(--p-primary-color), #a855f7);
  border-radius: 4px;
  min-width: 2px;
  transition: width 0.3s;
}
.value {
  font-variant-numeric: tabular-nums;
}
</style>
