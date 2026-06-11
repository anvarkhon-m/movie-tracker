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
