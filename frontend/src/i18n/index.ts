import { createI18n } from 'vue-i18n'

const LOCALE_KEY = 'mt_locale'

const messages = {
  uz: {
    app: { title: 'Movie Tracker' },
    nav: { home: 'Bosh sahifa', movies: 'Kinolar', logout: 'Chiqish' },
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
  },
  ru: {
    app: { title: 'Movie Tracker' },
    nav: { home: 'Главная', movies: 'Фильмы', logout: 'Выйти' },
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
  },
  en: {
    app: { title: 'Movie Tracker' },
    nav: { home: 'Home', movies: 'Movies', logout: 'Logout' },
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
