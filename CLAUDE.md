# CLAUDE.md — Movie Tracker Proyekti

> Bu fayl proyektning "big picture"i. Har yangi session boshida o'qi.
> Kod yozishdan oldin bu faylni to'liq o'qib chiq.

---

## 📌 Proyekt nima?

**Movie Tracker** — foydalanuvchi ko'rgan kino va seriallarni kuzatib boruvchi shaxsiy dastur.

**Asosiy maqsad:** Ko'rgan kontentni qayd qilish — qayerda ko'rdim, qachon, qanday baho berdim.

**Muhim xususiyatlar:**
- TMDB API orqali kino/serial ma'lumotlarini avtomatik to'ldirish
- Google OAuth orqali kirish
- Har bir foydalanuvchi faqat o'zining ma'lumotlarini ko'radi
- Ko'rish tarixi (watch history) alohida saqlanadi
- Poster rasmlar MinIO da saqlanadi

---

## 🏗️ Tech Stack

| Layer | Texnologiya |
|---|---|
| Backend | Java + Spring Boot |
| Libraries | Lombok + MapStruct |
| Frontend | Vue 3 (Composition API + Pinia + vue-i18n) |
| Database | PostgreSQL |
| Object Storage | MinIO (poster rasmlar uchun) |
| Auth | Google OAuth2 + JWT |
| External API | TMDB (The Movie Database) |
| Logging | Logback → fayl |
| Migration | Flyway |

---

## 📐 Arxitektura

```
Controller → Service → Repository → DB
                  ↓
           Domain Model
                  ↓
         MinIO / TMDB / Google
```

### Qoidalar:
- Controller faqat HTTP — biznes logika **yo'q**
- Service — barcha biznes logika shu yerda
- Repository faqat DB operatsiyalari (Spring Data JPA)
- Controller to'g'ridan Repository ni chaqirmaydi, **hech qachon**

### Paket tuzilmasi:
```
com.example.movietracker
├── controller/
├── service/
├── repository/
├── domain/          ← Rich Domain Model (JPA Entity)
├── dto/             ← request/response DTO lar
├── factory/         ← TMDB response → Domain object
├── facade/          ← TMDB API murakkabligini yashiradi
├── exception/       ← custom exception lar
├── config/          ← Spring config lar
└── infrastructure/
    ├── minio/       ← MinIO integratsiya
    └── tmdb/        ← TMDB API client
```

---

## 🎯 Arxitektura Patternlar

### Rich Domain Model (MUHIM!)
Entitylar faqat data tashimaydi — o'z biznes logikasini o'zi biladi.

```java
// ❌ YOZMA — Anemic (kamqon)
public class MovieService {
    public void complete(Movie movie) {
        movie.setStatus(WatchStatus.COMPLETED);
        movie.setWatchCount(movie.getWatchCount() + 1);
    }
}

// ✅ YOZ — Rich Domain Model
@Entity
@Getter
@Builder
public class Movie {
    private WatchStatus status;
    private int watchCount;
    private Double imdbRating;

    public void markAsWatched() {
        this.status = WatchStatus.COMPLETED;
        this.watchCount++;
    }

    public boolean isHighRated() {
        return imdbRating != null && imdbRating > 8.0;
    }

    public boolean canBeDropped() {
        return status == WatchStatus.WATCHING;
    }
}
```

### Lombok qoidalari
- Entity larda: `@Getter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- `@Data` ishlatma Entity larda — `equals/hashCode` muammolari bo'ladi
- DTO larda: `@Data` yoki Java `record` ishlatish mumkin

```java
// DTO — Java record
public record MovieResponse(
    Long id,
    String title,
    Double imdbRating,
    WatchStatus status
) {}

// DTO — Lombok (murakkab DTO uchun)
@Data
@Builder
public class MovieRequest {
    @NotBlank
    private String title;
    @DecimalMin("0.0") @DecimalMax("10.0")
    private Double personalRating;
}
```

### MapStruct — Mapper
```java
@Mapper(componentModel = "spring")
public interface MovieMapper {
    MovieResponse toResponse(Movie movie);
    Movie toEntity(MovieRequest request);
    List<MovieResponse> toResponseList(List<Movie> movies);
}
```

### Factory Pattern
TMDB response dan domain obyekt yaratish:
```java
@Component
public class MovieFactory {
    public Movie fromTmdb(TmdbMovieResponse response) { ... }
}
```

### Builder Pattern
Lombok `@Builder` orqali avtomatik:
```java
Movie movie = Movie.builder()
    .title("Inception")
    .imdbRating(8.8)
    .status(WatchStatus.PLAN_TO_WATCH)
    .build();
```

### Facade Pattern
TMDB integratsiyasini yashirish:
```java
@Service
public class TmdbFacade {
    public List<TmdbSearchResult> searchMovie(String query) { ... }
    public TmdbMovieDetails getMovieDetails(int tmdbId) { ... }
}
```

### Frontend Patterns
- **Composables** — `useMovies.ts`, `useSerials.ts`, `useSearch.ts`, `useAuth.ts`
- **Pinia Store** — global auth holati, user ma'lumotlari

---

## 🗄️ Ma'lumotlar modeli

### ENUMlar

```sql
-- Kino/Serial ko'rish holati
watch_status: PLAN_TO_WATCH | WATCHING | COMPLETED | DROPPED

-- Serialning hozirgi holati
serial_status: ONGOING | ENDED | CANCELLED
```

### Jadvallar

#### `users`
```sql
id          BIGSERIAL PRIMARY KEY
google_id   VARCHAR(255) UNIQUE NOT NULL
email       VARCHAR(255) UNIQUE NOT NULL
name        VARCHAR(255)
avatar_url  VARCHAR(500)
created_at  TIMESTAMP DEFAULT now()
```

#### `movie`
```sql
id              BIGSERIAL PRIMARY KEY
user_id         BIGINT REFERENCES users(id)
tmdb_id         INT
title           VARCHAR(255) NOT NULL
release_year    INT
genres          TEXT[]                    -- ['Sci-Fi', 'Thriller']
director        VARCHAR(255)
imdb_rating     NUMERIC(3,1)
personal_rating NUMERIC(3,1)
duration_min    INT
platform        VARCHAR(100)
watch_url       VARCHAR(500)
watch_count     INT DEFAULT 0
status          watch_status NOT NULL DEFAULT 'PLAN_TO_WATCH'
personal_note   TEXT
poster_url      VARCHAR(500)              -- MinIO URL
language        VARCHAR(50)
country         VARCHAR(100)
created_at      TIMESTAMP DEFAULT now()
updated_at      TIMESTAMP DEFAULT now()
```

#### `movie_watch_history`
```sql
id          BIGSERIAL PRIMARY KEY
movie_id    BIGINT REFERENCES movie(id) ON DELETE CASCADE
watched_at  DATE NOT NULL
platform    VARCHAR(100)
note        TEXT
```

#### `serial`
```sql
id              BIGSERIAL PRIMARY KEY
user_id         BIGINT REFERENCES users(id)
tmdb_id         INT
title           VARCHAR(255) NOT NULL
release_year    INT
genres          TEXT[]
director        VARCHAR(255)
imdb_rating     NUMERIC(3,1)
personal_rating NUMERIC(3,1)
season_count    INT
episode_count   INT
platform        VARCHAR(100)
watch_url       VARCHAR(500)
serial_status   serial_status DEFAULT 'ONGOING'
watch_status    watch_status NOT NULL DEFAULT 'PLAN_TO_WATCH'
personal_note   TEXT
poster_url      VARCHAR(500)              -- MinIO URL
language        VARCHAR(50)
country         VARCHAR(100)
created_at      TIMESTAMP DEFAULT now()
updated_at      TIMESTAMP DEFAULT now()
```

#### `episode`
```sql
id              BIGSERIAL PRIMARY KEY
serial_id       BIGINT REFERENCES serial(id) ON DELETE CASCADE
season_no       INT NOT NULL
episode_no      INT NOT NULL
title           VARCHAR(255)
duration_min    INT
personal_note   TEXT
```

#### `episode_watch_history`
```sql
id          BIGSERIAL PRIMARY KEY
episode_id  BIGINT REFERENCES episode(id) ON DELETE CASCADE
watched_at  DATE NOT NULL
platform    VARCHAR(100)
note        TEXT
```

---

## 🌐 API Endpointlar

**Base URL:** `/api/v1`

### Auth
```
GET  /api/v1/auth/google          ← Google OAuth redirect
GET  /api/v1/auth/google/callback ← Google callback
POST /api/v1/auth/logout
GET  /api/v1/auth/me              ← joriy foydalanuvchi
```

### TMDB Qidiruv (DB ga saqlamaydi)
```
GET /api/v1/tmdb/search/movies?query=Inception
GET /api/v1/tmdb/search/serials?query=Breaking
GET /api/v1/tmdb/movies/{tmdbId}
GET /api/v1/tmdb/serials/{tmdbId}
```

### Movies
```
GET    /api/v1/movies              ← ro'yxat (filter + pagination)
POST   /api/v1/movies              ← qo'shish
GET    /api/v1/movies/{id}         ← bitta batafsil
PUT    /api/v1/movies/{id}         ← tahrirlash
DELETE /api/v1/movies/{id}         ← o'chirish

GET    /api/v1/movies/{id}/history       ← watch history
POST   /api/v1/movies/{id}/history       ← yangi ko'rish qo'shish
DELETE /api/v1/movies/{id}/history/{hid} ← history o'chirish
```

### Serials
```
GET    /api/v1/serials
POST   /api/v1/serials
GET    /api/v1/serials/{id}
PUT    /api/v1/serials/{id}
DELETE /api/v1/serials/{id}

GET    /api/v1/serials/{id}/episodes
POST   /api/v1/serials/{id}/episodes
PUT    /api/v1/serials/{id}/episodes/{epId}
DELETE /api/v1/serials/{id}/episodes/{epId}

GET    /api/v1/episodes/{id}/history
POST   /api/v1/episodes/{id}/history
DELETE /api/v1/episodes/{id}/history/{hid}
```

### Filter parametrlar (GET /movies va /serials)
```
search=inception            ← title bo'yicha qidiruv
releaseYearFrom=2010
releaseYearTo=2020
watchedYearFrom=2023
watchedYearTo=2024
genre=Sci-Fi
minRating=7.0
maxRating=9.5
minDuration=90              ← faqat movie uchun
maxDuration=180             ← faqat movie uchun
language=English
country=USA
status=COMPLETED
page=0
size=20
```

---

## 🖼️ MinIO — Poster Saqlash

**Flow:**
```
1. TMDB dan poster_path keladi (masalan: /lMrx...jpg)
2. Backend TMDB dan rasmni yuklab oladi
3. MinIO ga saqlaydi
4. MinIO URL DB ga yoziladi
```

**MinIO bucket:** `movie-tracker`
**URL pattern:** `http://minio:9000/movie-tracker/posters/{tmdb_id}.jpg`

---

## 🔐 Auth Flow

```
1. Foydalanuvchi "Google bilan kirish" bosadi
2. Google OAuth2 → backend callback
3. Google ID bilan users jadvalidan qidiriladi
4. Topilmasa — avtomatik ro'yxatdan o'tkaziladi
5. JWT token qaytariladi
6. Frontend token ni localStorage da saqlaydi
```

---

## 🎨 Frontend

### Sahifalar
| Sahifa | Route | Tavsif |
|---|---|---|
| Home | `/` | Dashboard, statistika |
| Movies | `/movies` | Ro'yxat, filter, search |
| Movie Detail | `/movies/:id` | Batafsil + watch history |
| Serials | `/serials` | Ro'yxat, filter, search |
| Serial Detail | `/serials/:id` | Batafsil + epizodlar |
| Statistics | `/stats` | Grafiklar, janr taqsimoti |
| Watchlist | `/watchlist` | PLAN_TO_WATCH ro'yxati |
| Discover | `/discover` | TMDB qidiruv |
| Favorites | `/favorites` | Eng yuqori personal rating |
| Profile | `/profile` | Foydalanuvchi ma'lumotlari |

### Xususiyatlar
- **Dark mode** — togglable
- **Til** — O'zbek / Rus / Ingliz (`vue-i18n`)
- **Composables:** `useMovies`, `useSerials`, `useEpisodes`, `useSearch`, `useAuth`, `useTmdb`
- **Pinia stores:** `authStore`, `settingsStore`

---

## ✍️ Kod Yozish Qoidalari

### Java / Backend
- Entity larda `@Getter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` (Lombok)
- Entity larda `@Data` ishlatma — `equals/hashCode` JPA muammolari bo'ladi
- DTO larda Java `record` yoki Lombok `@Data` ishlatish mumkin
- Mapper uchun MapStruct (`@Mapper(componentModel = "spring")`)
- Magic number yo'q — `static final` konstantalar
- Har bir service methodi `@Transactional` (o'qish uchun `readOnly = true`)
- `null` check uchun `Optional` ishlatma service da — domain ichida hal qil

### Validatsiya
- DTO larda `jakarta.validation` annotatsiyalari
- Controller da `@Valid` annotatsiyasi majburiy
- `rating` 0.0–10.0 oralig'ida (`@DecimalMin`, `@DecimalMax`)

### Error Handling
Global `@ControllerAdvice` — barcha exception shu yerda.

Response format:
```json
{
  "status": 404,
  "message": "Movie not found with id: 5",
  "timestamp": "2026-06-09T10:00:00"
}
```

### Vue / Frontend
- Composition API (`<script setup>`) — Options API ishlatilmaydi
- API chaqiruvlar `composables/` ichida
- `any` type ishlatma — TypeScript dan to'liq foydalan

---

## 🚫 Nima qilma

- Controller ichida biznes logika yozma
- Repository ni controller dan to'g'ridan inject qilma
- Anemic domain model yozma — logikani entity ichiga qo'y
- Entity larda `@Data` ishlatma
- `@Setter` ni entity larda ochma — faqat domain metodlar orqali o'zgartir
- Frontend da `any` type ishlatma
- TMDB URL ni to'g'ridan poster sifatida saqlaMA — MinIO ga yukla
- MapStruct o'rniga qo'lda mapping yozma

---

## 📚 Qo'shimcha fayllar (har session da o'qi)

- `DECISIONS.md` — arxitektura va texnologiya qarorlari, sabablar bilan
- `CHANGELOG.md` — qaysi session da nima qilindi

---

## 📁 Muhim fayllar

```
src/main/resources/application.yml          ← DB, MinIO, TMDB config
src/main/resources/db/migration/            ← Flyway migration lar
src/main/kotlin/.../domain/                 ← Rich Domain Entity lar
src/main/kotlin/.../dto/                    ← DTO lar
src/main/kotlin/.../factory/                ← TMDB → Domain factory
src/main/kotlin/.../facade/                 ← TMDB facade
src/main/kotlin/.../infrastructure/minio/   ← MinIO service
frontend/src/composables/                   ← API logika
frontend/src/stores/                        ← Pinia stores
frontend/src/views/                         ← Sahifalar
frontend/src/components/                    ← Reusable komponentlar
frontend/src/i18n/                          ← Tarjima fayllari
logs/                                       ← Logback log fayllari
```
