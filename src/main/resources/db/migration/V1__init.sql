-- Movie Tracker — boshlang'ich sxema

CREATE TYPE watch_status AS ENUM ('PLAN_TO_WATCH', 'WATCHING', 'COMPLETED', 'DROPPED');
CREATE TYPE serial_status AS ENUM ('ONGOING', 'ENDED', 'CANCELLED');

CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    google_id   VARCHAR(255) UNIQUE NOT NULL,
    email       VARCHAR(255) UNIQUE NOT NULL,
    name        VARCHAR(255),
    avatar_url  VARCHAR(500),
    created_at  TIMESTAMP DEFAULT now()
);

CREATE TABLE movie (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    tmdb_id         INT,
    title           VARCHAR(255) NOT NULL,
    release_year    INT,
    genres          TEXT[],
    director        VARCHAR(255),
    imdb_rating     NUMERIC(3,1),
    personal_rating NUMERIC(3,1),
    duration_min    INT,
    platform        VARCHAR(100),
    watch_url       VARCHAR(500),
    watch_count     INT NOT NULL DEFAULT 0,
    status          watch_status NOT NULL DEFAULT 'PLAN_TO_WATCH',
    personal_note   TEXT,
    poster_url      VARCHAR(500),
    language        VARCHAR(50),
    country         VARCHAR(100),
    created_at      TIMESTAMP DEFAULT now(),
    updated_at      TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_movie_user_id ON movie(user_id);
CREATE INDEX idx_movie_status ON movie(user_id, status);

CREATE TABLE movie_watch_history (
    id          BIGSERIAL PRIMARY KEY,
    movie_id    BIGINT NOT NULL REFERENCES movie(id) ON DELETE CASCADE,
    watched_at  DATE NOT NULL,
    platform    VARCHAR(100),
    note        TEXT
);

CREATE INDEX idx_movie_watch_history_movie_id ON movie_watch_history(movie_id);

CREATE TABLE serial (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    tmdb_id         INT,
    title           VARCHAR(255) NOT NULL,
    release_year    INT,
    genres          TEXT[],
    director        VARCHAR(255),
    imdb_rating     NUMERIC(3,1),
    personal_rating NUMERIC(3,1),
    season_count    INT,
    episode_count   INT,
    platform        VARCHAR(100),
    watch_url       VARCHAR(500),
    serial_status   serial_status DEFAULT 'ONGOING',
    watch_status    watch_status NOT NULL DEFAULT 'PLAN_TO_WATCH',
    personal_note   TEXT,
    poster_url      VARCHAR(500),
    language        VARCHAR(50),
    country         VARCHAR(100),
    created_at      TIMESTAMP DEFAULT now(),
    updated_at      TIMESTAMP DEFAULT now()
);

CREATE INDEX idx_serial_user_id ON serial(user_id);
CREATE INDEX idx_serial_watch_status ON serial(user_id, watch_status);

CREATE TABLE episode (
    id              BIGSERIAL PRIMARY KEY,
    serial_id       BIGINT NOT NULL REFERENCES serial(id) ON DELETE CASCADE,
    season_no       INT NOT NULL,
    episode_no      INT NOT NULL,
    title           VARCHAR(255),
    duration_min    INT,
    personal_note   TEXT,
    UNIQUE (serial_id, season_no, episode_no)
);

CREATE INDEX idx_episode_serial_id ON episode(serial_id);

CREATE TABLE episode_watch_history (
    id          BIGSERIAL PRIMARY KEY,
    episode_id  BIGINT NOT NULL REFERENCES episode(id) ON DELETE CASCADE,
    watched_at  DATE NOT NULL,
    platform    VARCHAR(100),
    note        TEXT
);

CREATE INDEX idx_episode_watch_history_episode_id ON episode_watch_history(episode_id);
