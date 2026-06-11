# CHANGELOG.md — Qilingan Ishlar Tarixi

> Har session oxirida nima qilinganini yoz.
> Format: sana, nima qilindi, nima qoldi.

---

## 2026-06-10 — Session 1: Rejalashtirish

### Qilinganlar
- Proyekt maqsadi aniqlashtirildi (Movie Tracker)
- Tech stack belgilandi: Java + Spring Boot, Vue 3, PostgreSQL, MinIO, TMDB, Google OAuth
- DB sxemasi loyihalashtirildi (6 jadval: users, movie, serial, episode, movie_watch_history, episode_watch_history)
- API endpointlar aniqlashtirildi (`/api/v1/`)
- Filter parametrlar aniqlashtirildi
- Design patternlar belgilandi: Rich Domain Model, Factory, Builder, Facade, MapStruct, Pinia
- Frontend sahifalar aniqlashtirildi (10 ta sahifa)
- UI mockup lar ko'rildi (Home, Movies, Movie Detail, Serial Detail, Statistics)
- `CLAUDE.md`, `DECISIONS.md`, `CHANGELOG.md` yaratildi

### Keyingi session uchun
- [ ] Spring Initializr da proyekt yaratish
- [ ] Flyway V1 migration yozish
- [ ] Domain entity lar yaratish (Movie, Serial, Episode, User)
- [ ] Repository lar yaratish
- [ ] DTO + MapStruct mapper lar
- [ ] Service layer
- [ ] Controller layer
- [ ] Google OAuth sozlash
- [ ] TMDB facade + factory
- [ ] MinIO integratsiya

---

## 2026-06-10 — Session 2: Backend skelet (to'liq)

### Qilinganlar
- `pom.xml` to'ldirildi: MapStruct, MinIO client, JJWT, Testcontainers qo'shildi
- `application.properties` → `application.yml` (DB, OAuth, TMDB, MinIO, JWT, logging → `logs/movie-tracker.log`)
- `compose.yaml` — dev uchun PostgreSQL 16 + MinIO
- Flyway `V1__init.sql` — 6 jadval + `watch_status`, `serial_status` PG enum lari, indekslar
- Rich Domain entitylar: `User`, `Movie`, `MovieWatchHistory`, `Serial`, `Episode`, `EpisodeWatchHistory`
  - Domain metodlar: `markAsWatched()`, `recordWatch()`, `drop()`, `rate()`, `addEpisode()` va h.k.
  - Rating: `BigDecimal` (DB `NUMERIC(3,1)` bilan mos), genres: `text[]` (`@JdbcTypeCode(SqlTypes.ARRAY)`)
- Repositorylar + JPA Specification filtrlar (search, year, genre `array_contains`, rating, duration, language, country, status, watchedYear — subquery orqali)
- DTO record lar (validation bilan) + MapStruct mapperlar
- `GlobalExceptionHandler` — `{status, message, timestamp}` formatda
- TMDB: `TmdbClient` (RestClient) → `TmdbFacade` → `MovieFactory`/`SerialFactory`
- MinIO: `PosterStorageService` — poster TMDB dan yuklab olinib `movie-tracker/posters/{tmdbId}.jpg` ga saqlanadi (public-read policy), xato bo'lsa kino postersiz saqlanadi
- Auth: Google OAuth2 (`/api/v1/auth/google` → callback → JWT bilan frontend ga redirect), `JwtAuthenticationFilter`, avtomatik ro'yxatdan o'tkazish, `/auth/me`, `/auth/logout`
- Barcha controllerlar: movies, serials, episodes, watch history, TMDB search
- Test: Testcontainers (PostgreSQL) bilan `contextLoads` — **BUILD SUCCESS** (Flyway + Hibernate validate o'tdi)

### Muammolar va yechimlar
- Muammo: PG `NUMERIC(3,1)` ustunlar `Double` bilan Hibernate `validate` da mos kelmasligi mumkin.
  Yechim: Entity larda `BigDecimal` ishlatildi (DTO larda ham).
- Muammo: PG enum tiplarini JPA bilan map qilish.
  Yechim: `@JdbcTypeCode(SqlTypes.NAMED_ENUM)` + `columnDefinition` (Hibernate 6.6).

### Keyingi session uchun
- [ ] Google OAuth real client-id/secret bilan sinash (env: `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`)
- [ ] TMDB API key bilan real qidiruv/poster oqimini sinash (env: `TMDB_API_KEY`)
- [ ] Service layer uchun unit/integration testlar
- [ ] Frontend: Vue 3 + Pinia + vue-i18n proyektini boshlash (`frontend/`)
- [ ] Statistics endpoint (frontend `/stats` sahifasi uchun)

---

## 2026-06-11 — Session 3: Swagger, smoke-test, statistika

### Qilinganlar
- **Swagger** qo'shildi: `springdoc-openapi`, `OpenApiConfig` (JWT bearer scheme),
  `/swagger-ui.html` va `/v3/api-docs` security da permit qilindi
- **OAuth bug tuzatildi:** authorization endpoint `/api/v1/auth` ni egallab,
  `/api/v1/auth/me` ni `me` nomli client registration deb o'qiyotgan edi.
  Endi default `/oauth2/authorization/google`, `/api/v1/auth/google` esa unga redirect qiladi.
- **Dev tooling:** `README.md` (lokal qo'llanma), `run-dev.sh` (`.env` ni yuklaydi),
  `.env` (gitignore) — TMDB kalit shu yerda, commit qilinmaydi
- MinIO portlari 9100/9101 ga ko'chirildi (9000 band edi)
- **Smoke-test:** JWT auth zanjiri (filter→controller), `/auth/me`, jonli TMDB qidiruv — ishladi
- **Statistika endpoint:** `GET /api/v1/stats`
  - `StatsResponse` (record): movie/serial soni, status taqsimoti (EnumMap, 0 bilan to'ldirilgan),
    top-10 janr (movie+serial birlashtirilgan), o'rtacha personal rating, yillar bo'yicha ko'rishlar
  - `StatsService` + `StatsController` + projection interfeyslar (StatusCount, GenreCount, YearCount)
  - Native SQL: `unnest(genres)` janr taqsimoti uchun, `EXTRACT(YEAR FROM watched_at)` yillar uchun
  - **5 ta integration test** (Testcontainers) — barchasi o'tdi

### Muammolar va yechimlar
- Muammo: TMDB "Invalid API key" — ilova `TMDB_API_KEY` env siz ishga tushirilgan edi.
  Yechim: `./run-dev.sh` orqali `.env` dan yuklash (yoki IDE run config ga env qo'shish).
- Muammo: `/auth/me` 401 (OAuth filter ushlab qolyapti).
  Yechim: yuqoridagi OAuth endpoint o'zgarishi.

### Keyingi session uchun
- [ ] Frontend: Vue 3 + Pinia + vue-i18n (`frontend/`) — auth + movies ro'yxatidan boshlash
- [ ] Service layer testlarini kengaytirish (MovieService/SerialService, ownership izolyatsiya)
- [ ] Google OAuth real login (Google Cloud credentials kerak)

---

## 2026-06-11 — Session 4: Frontend skelet (vertical slice)

### Qilinganlar
- GitHub remote ulandi va push qilindi: github.com/anvarkhon-m/movie-tracker
- **Frontend scaffold:** `frontend/` — Vite + Vue 3 + TypeScript
  - Qo'shildi: Pinia, vue-router, vue-i18n, axios
  - `@` alias (`@/*` → `src/*`) vite + tsconfig da
- **Dev proxy:** Vite `/api` → `localhost:8080` (frontend 5173, backend 8080 — bir origin)
- **Infrastructure:**
  - `api/client.ts` — axios instance, JWT request interceptor, 401 da logout+login redirect
  - `api/types.ts` — backend DTO lariga mos tiplar (no `any`)
  - `stores/auth.ts` — Pinia auth store (token localStorage da, fetchMe, logout)
  - `router/index.ts` — route guard (requiresAuth)
  - `i18n/` — uz/ru/en, til localStorage da saqlanadi
- **Vertical slice (auth + movies):**
  - `LoginView` — Google login tugmasi + dev token paste (Google kaliti yo'qligi uchun)
  - `AuthCallbackView` — `?token=` ni o'qib saqlaydi
  - `composables/useMovies.ts` — `GET /api/v1/movies`
  - `MoviesView` — poster grid, status, rating
  - `HomeView` — dashboard placeholder
  - `App.vue` — nav shell + til tanlash + logout
- `npm run build` (vue-tsc + vite) — **muvaffaqiyatli**, route lar code-split

### Muammolar va yechimlar
- Muammo: TS 7.0 da `baseUrl` deprecated.
  Yechim: `baseUrl` olib tashlandi, `paths` o'zi yetarli (TS 5+).

### Keyingi session uchun
- [ ] Jonli sinov: `./run-dev.sh` (backend) + `npm run dev` (frontend), dev token bilan kirish
- [ ] Movie detail sahifa + watch history UI
- [ ] Serials ro'yxati + detail + epizodlar
- [ ] Filter/search UI (movies)
- [ ] Statistics sahifa (`/stats` endpoint dan grafiklar)
- [ ] TMDB qidiruv (Discover) sahifa + kino qo'shish formasi

---

## 2026-06-11 — Session 5: Google login jonli + Discover sahifa

### Qilinganlar
- **Google OAuth jonli ishladi** (real client credentials, brauzerda login tasdiqlandi)
- **Bug fix:** `findOrCreateUser` faqat google_id bo'yicha qidirardi — mavjud email bilan
  to'qnashganda 500 (duplicate email). Endi email bo'yicha fallback + Google akkauntni
  bog'laydi (`linkGoogleAccount`). Dev token va Google login bir userga birlashdi.
- **Discover sahifa (`/discover`):**
  - `useDiscover` composable — TMDB qidiruv (movie/serial) + kutubxonaga qo'shish
  - `DiscoverView` — type toggle, qidiruv, natija kartalari, "Qo'shish" tugmasi (added/adding holatlari)
  - Route + nav link + i18n (uz/ru/en)
  - Jonli sinov: `/api/v1/tmdb/search/movies?query=Inception` → natija qaytdi
- `npm run build` — muvaffaqiyatli

### Movie detail sahifa (qo'shildi)
- `useMovie` composable — movie + history yuklash, watch qo'shish/o'chirish, movie o'chirish
- `MovieDetailView` (`/movies/:id`) — to'liq ma'lumot, ko'rish tarixi (qo'shish formasi + o'chirish), kinoni o'chirish
- MoviesView kartalari endi detail ga link
- Jonli sinov (to'liq zanjir tasdiqlandi):
  - POST /movies {tmdbId:27205} → Inception qo'shildi, poster MinIO ga saqlandi
  - MinIO poster public: HTTP 200, image/jpeg, 105 KB
  - POST history → recordWatch ishladi: watchCount=1, status=COMPLETED

### Serials section (qo'shildi)
- `useSerials` (ro'yxat) + `useSerial` (detail + epizodlar) composable lar
- `SerialsView` (`/serials`) — grid, detail ga link
- `SerialDetailView` (`/serials/:id`) — to'liq ma'lumot, epizodlar fasllar bo'yicha guruhlangan,
  epizod qo'shish/o'chirish, "ko'rildi" deb belgilash (episode history endpoint)
- Nav + routes + i18n (uz/ru/en), serialStatus tarjimalari
- Jonli sinov (to'liq zanjir):
  - POST /serials {tmdbId:1396} → Breaking Bad (5 fasl, 62 epizod, ENDED), poster MinIO da
  - POST episode → POST episode history → GET: watched=true (isWatched domain logikasi)

---

## 2026-06-11 — Session 6: Filter UI, Statistika sahifa, inline tahrir

### Qilinganlar
- **Filter/search UI:** `FilterBar` komponenti (debounced search, status, janr, min reyting, reset),
  MoviesView + SerialsView ga ulandi, `useSerials` filtr parametrlari kengaytirildi
- **Statistika sahifa (`/stats`):** `useStats` + `StatsView` — summary kartalar + CSS bar chart lar
  (status taqsimoti, top janrlar, yillar bo'yicha ko'rishlar), zero-dependency `BarChart` komponenti
- **Inline tahrir:** Movie va Serial detail sahifalarida shaxsiy maydonlar (baho, status,
  serial holati, platforma, izoh) tahrirlanadi — to'liq PUT yuboriladi
- Jonli sinov: PUT movie personalRating=9.0 → /stats averageMovieRating=9.0 (to'liq loop tasdiqlandi)

### Keyingi session uchun
- [ ] Watchlist / Favorites / Discover-da serial qo'shish sahifalari (route lar mavjud emas)
- [ ] Dark mode toggle
- [ ] Pagination UI (hozir faqat birinchi 20 ta)
- [ ] Backend: service-layer testlarni kengaytirish

---

## 2026-06-11 — Session 7: Haqiqiy IMDb reyting (OMDb) + refresh

### Qilinganlar
- **Muammo:** `imdbRating` aslida TMDB `vote_average` edi — Google/IMDb bilan mos kelmasdi
- **OMDb integratsiyasi:** TMDB dan `imdb_id` (movie) / `external_ids.imdb_id` (serial) olinadi,
  OMDb orqali haqiqiy IMDb reytingi olinadi (`OmdbClient`, `OmdbProperties`)
  - TMDB facade endi `imdbRating(imdbId, voteAverage)` — OMDb topsa real IMDb, topmasa TMDB ga fallback
  - `OMDB_API_KEY` env orqali (`.env` da, commit qilinmaydi)
- **Refresh rating:** `POST /api/v1/movies/{id}/refresh-rating` va `/serials/{id}/refresh-rating`
  - Domain: `refreshImdbRating()`; Service tmdbId orqali qayta oladi
  - Frontend: detail sahifalarda IMDb yonida ⟳ tugma (null bo'lsa ham TMDB-linked bo'lsa ko'rinadi)
- Sinov: OMDb tt1375666 → 8.8 (Google bilan mos)

### OMDb kunlik limit himoyasi (1000/kun)
- **24 soatlik cooldown:** Flyway `V2` — `imdb_rating_updated_at` ustuni (movie + serial)
- `refreshImdbRating` 24 soat ichida yangilangan bo'lsa OMDb ni chaqirmaydi (no-op)
- Domain: `isImdbRatingFresh(Duration)`; `refreshImdbRating` vaqtni belgilaydi
- Response da `imdbRatingUpdatedAt` — frontend tugmani cooldown da o'chiradi (tooltip bilan)
- Natija: bir sarlavha kuniga ≈1 OMDb chaqiruv, spam himoyalangan
- Sinov: V2 migration + schema validate (Testcontainers) — o'tdi

---

<!-- 
Keyingi session shablon:

## YYYY-MM-DD — Session N: [mavzu]

### Qilinganlar
- ...

### Muammolar va yechimlar
- Muammo: ...
  Yechim: ...

### Keyingi session uchun
- [ ] ...
-->
