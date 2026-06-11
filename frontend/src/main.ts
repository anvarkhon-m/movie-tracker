import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'
import App from './App.vue'
import router from './router'
import { i18n } from './i18n'
import { useSettingsStore } from './stores/settings'

const app = createApp(App)

const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(i18n)

// Saqlangan mavzuni mount dan oldin qo'llaymiz (flash bo'lmasligi uchun).
useSettingsStore(pinia)

app.mount('#app')
