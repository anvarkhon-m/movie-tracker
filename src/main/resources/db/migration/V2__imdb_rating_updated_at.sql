-- IMDb reyting refresh cooldown uchun: oxirgi yangilangan vaqt

ALTER TABLE movie ADD COLUMN imdb_rating_updated_at TIMESTAMP;
ALTER TABLE serial ADD COLUMN imdb_rating_updated_at TIMESTAMP;
