import { createI18n } from 'vue-i18n'

const LOCALE_KEY = 'mt_locale'

const messages = {
  uz: {
    app: { title: 'Movie Tracker' },
    nav: { home: 'Bosh sahifa', movies: 'Kinolar', discover: 'Topish', logout: 'Chiqish' },
    login: {
      title: 'Movie Tracker ga kirish',
      google: 'Google bilan kirish',
      devTitle: 'Dev token (sinov uchun)',
      devPlaceholder: 'JWT tokenni qo\'ying',
      devSubmit: 'Token bilan kirish',
    },
    movies: {
      title: 'Kinolar',
      empty: 'Hali kino qo\'shilmagan',
      loading: 'Yuklanmoqda...',
      error: 'Xatolik yuz berdi',
      rating: 'Reyting',
    },
    status: {
      PLAN_TO_WATCH: 'Rejada',
      WATCHING: 'Ko\'rilmoqda',
      COMPLETED: 'Ko\'rilgan',
      DROPPED: 'Tashlangan',
    },
    discover: {
      title: 'Topish (TMDB)',
      movies: 'Kinolar',
      serials: 'Seriallar',
      placeholder: 'Nom bo\'yicha qidiring...',
      searchBtn: 'Qidirish',
      add: 'Qo\'shish',
      adding: 'Qo\'shilmoqda...',
      added: 'Qo\'shildi ✓',
      empty: 'Hech narsa topilmadi',
      hint: 'Qidiruvni boshlang',
    },
  },
  ru: {
    app: { title: 'Movie Tracker' },
    nav: { home: 'Главная', movies: 'Фильмы', discover: 'Поиск', logout: 'Выйти' },
    login: {
      title: 'Вход в Movie Tracker',
      google: 'Войти через Google',
      devTitle: 'Dev токен (для теста)',
      devPlaceholder: 'Вставьте JWT токен',
      devSubmit: 'Войти с токеном',
    },
    movies: {
      title: 'Фильмы',
      empty: 'Фильмы ещё не добавлены',
      loading: 'Загрузка...',
      error: 'Произошла ошибка',
      rating: 'Рейтинг',
    },
    status: {
      PLAN_TO_WATCH: 'В планах',
      WATCHING: 'Смотрю',
      COMPLETED: 'Просмотрено',
      DROPPED: 'Брошено',
    },
    discover: {
      title: 'Поиск (TMDB)',
      movies: 'Фильмы',
      serials: 'Сериалы',
      placeholder: 'Искать по названию...',
      searchBtn: 'Искать',
      add: 'Добавить',
      adding: 'Добавление...',
      added: 'Добавлено ✓',
      empty: 'Ничего не найдено',
      hint: 'Начните поиск',
    },
  },
  en: {
    app: { title: 'Movie Tracker' },
    nav: { home: 'Home', movies: 'Movies', discover: 'Discover', logout: 'Logout' },
    login: {
      title: 'Sign in to Movie Tracker',
      google: 'Sign in with Google',
      devTitle: 'Dev token (for testing)',
      devPlaceholder: 'Paste JWT token',
      devSubmit: 'Sign in with token',
    },
    movies: {
      title: 'Movies',
      empty: 'No movies added yet',
      loading: 'Loading...',
      error: 'Something went wrong',
      rating: 'Rating',
    },
    status: {
      PLAN_TO_WATCH: 'Plan to watch',
      WATCHING: 'Watching',
      COMPLETED: 'Completed',
      DROPPED: 'Dropped',
    },
    discover: {
      title: 'Discover (TMDB)',
      movies: 'Movies',
      serials: 'Serials',
      placeholder: 'Search by title...',
      searchBtn: 'Search',
      add: 'Add',
      adding: 'Adding...',
      added: 'Added ✓',
      empty: 'Nothing found',
      hint: 'Start a search',
    },
  },
}

export type AppLocale = keyof typeof messages

const saved = (localStorage.getItem(LOCALE_KEY) as AppLocale | null) ?? 'uz'

export const i18n = createI18n({
  legacy: false,
  locale: saved,
  fallbackLocale: 'en',
  messages,
})

export function setLocale(locale: AppLocale): void {
  i18n.global.locale.value = locale
  localStorage.setItem(LOCALE_KEY, locale)
}
