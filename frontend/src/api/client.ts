import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

// Vite proxy /api ni backend (8080) ga uzatadi.
const client = axios.create({
  baseURL: '/api/v1',
})

client.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const auth = useAuthStore()
      auth.logout()
      if (router.currentRoute.value.name !== 'login') {
        void router.push({ name: 'login' })
      }
    }
    return Promise.reject(error)
  },
)

export default client
