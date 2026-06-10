# DECISIONS.md — Arxitektura va Texnologiya Qarorlari

> Har bir muhim qaror — nima uchun shunday qilindi.
> Yangi qaror qabul qilinganda shu faylga qo'shib ket.

---

## 🏗️ Arxitektura

### Rich Domain Model tanlandi
**Sabab:** Anemic Domain Model (kamqon model) — biznes logikani Service da to'playdi,
Entity faqat data tashiydi. Bu OOP emas, protsedural dasturlash.
Rich Domain Model da Entity o'z qoidalarini o'zi biladi va himoya qiladi.
**Misol:** `movie.markAsWatched()` — Movie o'zi statusini va watch_count ni boshqaradi.

### Layered Architecture (Controller → Service → Repository)
**Sabab:** Har bir qatlam o'z vazifasini bajaradi. Controller HTTP,
Service biznes logika, Repository DB. Bu separation of concerns ni ta'minlaydi.

### Alohida jadvallar (movie va serial)
**Sabab:** Movie va Serial ning field lari farq qiladi.
Movie da `duration_min` bor, Serial da `season_count`, `episode_count`, `serial_status`.
Bitta jadvalda ko'p `nullable` field bo'lardi.

---

## 🛠️ Texnologiyalar

### Java tanlandi (Kotlin emas)
**Sabab:** Dasturchi Java bilan tanish, loyiha Java da yoziladi.

### Lombok tanlandi
**Sabab:** Java da boilerplate kod ko'p (getter, setter, constructor, builder).
Lombok buni annotatsiya bilan hal qiladi.
**Muhim:** Entity larda `@Data` emas, `@Getter` + `@Builder` — JPA `equals/hashCode` muammolari oldini olish uchun.

### Java Record — DTO uchun
**Sabab:** Oddiy immutable DTO lar uchun Java record ideal — qisqa va aniq.
JPA Entity uchun emas (immutable bo'lgani uchun Hibernate bilan muammo).

### MapStruct tanlandi (ModelMapper emas)
**Sabab:** MapStruct compile time da ishlaydi — tez va type-safe.
ModelMapper runtime da ishlaydi — sekinroq va xatolarni kech topadi.

### PostgreSQL tanlandi
**Sabab:** `TEXT[]` (array) tipi bor — genres ni alohida jadvalsiz saqlash mumkin.
Kuchli, ishonchli, Spring Boot bilan yaxshi integratsiya.

### Google OAuth tanlandi
**Sabab:** Alohida register/login sahifasi kerak emas.
Foydalanuvchi bitta tugma bilan kiradi — qulay va xavfsiz.
Birinchi kirishda avtomatik ro'yxatdan o'tkaziladi.

### MinIO tanlandi (TMDB URL emas)
**Sabab:** TMDB poster URL vaqt o'tishi bilan o'zgarishi yoki ishlamay qolishi mumkin.
Rasmlarni o'zimizda saqlash — mustaqillik va ishonchlilik.

### TMDB API tanlandi
**Sabab:** Bepul, keng ma'lumotlar bazasi. Kino nomidan kelib chiqib
title, director, rating, genres, poster, country, language — hammasini avtomatik to'ldiradi.
Foydalanuvchi faqat platform, personal_rating, note qo'shadi.

### Flyway tanlandi (Liquibase emas)
**Sabab:** Sodda SQL migration lar uchun Flyway yetarli va oson.

### Vue 3 + Composition API tanlandi
**Sabab:** Options API eski usul. Composition API + `<script setup>` — zamonaviy,
TypeScript bilan yaxshi ishlaydi, composables pattern qulay.

### Pinia tanlandi (Vuex emas)
**Sabab:** Vue 3 uchun rasmiy state manager. Vuex dan soddaroq.

### vue-i18n tanlandi
**Sabab:** 3 tilli interfeys kerak (O'zbek, Rus, Ingliz). vue-i18n Vue ekotizimida standart.

---

## 🗄️ DB Qarorlari

### genres TEXT[] sifatida saqlandi
**Sabab:** Har kino uchun 2-3 ta janr bo'ladi. Alohida `genre` jadvali va
`movie_genre` ko'prik jadvali ortiqcha murakkablik. PostgreSQL `TEXT[]` bilan
`WHERE 'Action' = ANY(genres)` filter qulay ishlaydi.

### watch_history alohida jadval
**Sabab:** Foydalanuvchi bir kinoni bir necha marta ko'rishi mumkin.
`watched_at` ni movie jadvalida saqlash imkonsiz — har safar yangi yozuv kerak.

### Episode watch_history — episode darajasida
**Sabab:** Serial uchun har epizodni qachon ko'rganini bilish muhim.
Serial darajasida history shart emas — epizod history yetarli.

### user_id movie va serial jadvallarida
**Sabab:** Har foydalanuvchi faqat o'zining ma'lumotlarini ko'rishi kerak.
`WHERE user_id = :currentUserId` — izolyatsiya ta'minlanadi.

---

## 🌐 API Qarorlari

### `/api/v1/` versioning
**Sabab:** Kelajakda API o'zgarsachi — `/api/v2/` qo'shish mumkin bo'lsin.
Mavjud clientlar `/api/v1/` bilan ishlashda davom etadi.

### Pagination: page/size
**Sabab:** Oddiy va keng tarqalgan. Spring Data JPA `Pageable` bilan avtomatik ishlaydi.

### Filter: query parameters
**Sabab:** REST standartiga mos. `GET /api/v1/movies?genre=Sci-Fi&status=COMPLETED`
— o'qish oson, bookmark qilish mumkin.
