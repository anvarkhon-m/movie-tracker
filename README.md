# Movie Tracker

Ko'rgan kino va seriallarni kuzatuvchi shaxsiy dastur. TMDB orqali ma'lumotlar
avtomatik to'ldiriladi, posterlar MinIO da saqlanadi, kirish Google OAuth orqali.

Loyiha "big picture"i: [`CLAUDE.md`](CLAUDE.md) ·
qarorlar: [`DECISIONS.md`](DECISIONS.md) · tarix: [`CHANGELOG.md`](CHANGELOG.md)

## Tech stack

Java 21 · Spring Boot 3.5 · PostgreSQL · Flyway · MinIO · Lombok + MapStruct ·
Google OAuth2 + JWT · TMDB API · springdoc-openapi (Swagger)

---

## Lokal ishga tushirish

### 1. Secret lar — `.env`

`.env` fayli gitignore qilingan (commit qilinmaydi). Quyidagilarni o'z ichiga oladi:

```env
TMDB_API_KEY=<tmdb-kalit>          # https://www.themoviedb.org/settings/api
GOOGLE_CLIENT_ID=<...>             # https://console.cloud.google.com/apis/credentials
GOOGLE_CLIENT_SECRET=<...>
```

> TMDB kaliti `.env` da mavjud. Google OAuth ixtiyoriy — faqat real login uchun
> kerak (pastdagi **dev token** bilan Google kaliti shart emas).

### 2. Infratuzilma (Postgres + MinIO)

```bash
docker compose up -d
```

| Servis     | URL / Port                          | Login                   |
|------------|-------------------------------------|-------------------------|
| PostgreSQL | `localhost:5432` / `movie_tracker`  | `postgres` / `postgres` |
| MinIO API  | `http://localhost:9100`             | `minioadmin` / `minioadmin` |
| MinIO UI   | `http://localhost:9101`             | `minioadmin` / `minioadmin` |

> MinIO 9100/9101 da — chunki standart 9000 boshqa konteyner tomonidan band.

### 3. Ilova

```bash
./run-dev.sh        # .env ni yuklab, ./mvnw spring-boot:run ishga tushiradi
```

App: **http://localhost:8080**

---

## Swagger / OpenAPI

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

Himoyalangan endpoint larni sinash uchun:

1. **Authorize** tugmasini bos
2. JWT tokenni qo'y — **faqat token, `Bearer ` prefiksisiz** (Swagger o'zi qo'shadi)
3. Authorize → Close

---

## Auth — JWT olish

### Variant A — Dev token (Google kaliti shart emas)

Test foydalanuvchi (`id=1`) DB ga qo'shilgan. Quyidagi token shu foydalanuvchi
uchun, `application.yml` dagi dev `jwt.secret` bilan imzolangan (7 kun amal qiladi):

```
eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJzdWIiOiAiMSIsICJlbWFpbCI6ICJhbnZhcmtob25tdW5hdnZhcm92QGdtYWlsLmNvbSIsICJuYW1lIjogIkFudmFya2hvbiAoZGV2KSIsICJpYXQiOiAxNzgxMDk1ODQzLCAiZXhwIjogMTc4MTcwMDY0M30.vhpUig9Rt86Xt0xjafuZXYCXLJ3jX6fxQdsjqLZGVj4
```

Terminalda sinash:

```bash
TOKEN="eyJhbGci...GVj4"   # yuqoridagi to'liq token
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/auth/me
```

> Bu token faqat lokal dev uchun — dev `jwt.secret` ommaviy (default), shuning
> uchun xavfsizlik bermaydi. Prod da `JWT_SECRET` env orqali almashtiriladi.

### Variant B — Real Google login

`.env` ga `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` qo'yib, brauzerda:

```
http://localhost:8080/api/v1/auth/google
```

Login dan so'ng `http://localhost:5173/auth/callback?token=eyJ...` ga redirect
bo'ladi — frontend hali yo'q, lekin token URL da ko'rinadi.

---

## Tez sinov oqimi (Swagger)

1. `GET /api/v1/auth/me` → foydalanuvchi (id=1)
2. `GET /api/v1/tmdb/search/movies?query=Inception` → TMDB qidiruv
3. `POST /api/v1/movies` body `{"tmdbId": 27205}` → Inception qo'shiladi
   (TMDB dan to'ldiriladi, poster MinIO ga saqlanadi)
4. `GET /api/v1/movies` → ro'yxat (saqlangan `posterUrl` bilan)

---

## Foydali buyruqlar

```bash
./mvnw test          # Testcontainers (Postgres) bilan testlar
./mvnw compile       # kompilyatsiya
docker compose down  # infratuzilmani to'xtatish
```

Environment o'zgaruvchilari (`application.yml` default lari): `DB_URL`,
`DB_USERNAME`, `DB_PASSWORD`, `TMDB_API_KEY`, `GOOGLE_CLIENT_ID`,
`GOOGLE_CLIENT_SECRET`, `MINIO_ENDPOINT`, `JWT_SECRET`, `FRONTEND_URL`.
