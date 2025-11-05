-- Удаляем таблицы, если они существуют, чтобы избежать ошибок при перезапуске
DROP TABLE IF EXISTS friends CASCADE;
DROP TABLE IF EXISTS "like" CASCADE;
DROP TABLE IF EXISTS film_genre CASCADE;
DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS film CASCADE;
DROP TABLE IF EXISTS mpa CASCADE;
DROP TABLE IF EXISTS genre CASCADE;

-- Создание таблицы mpa_rating
CREATE TABLE IF NOT EXISTS mpa (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Создание таблицы genre
CREATE TABLE IF NOT EXISTS genre (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Создание таблицы film
CREATE TABLE IF NOT EXISTS film (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    release_date DATE,
    duration INTEGER,
    mpa_id INTEGER REFERENCES mpa(id)
);

-- Создание таблицы film_genre (связь многие-ко-многим между film и genre)
CREATE TABLE IF NOT EXISTS film_genre (
    id SERIAL PRIMARY KEY,
    film_id INTEGER REFERENCES film(id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genre(id) ON DELETE CASCADE,
    UNIQUE (film_id, genre_id)
);

-- Создание таблицы user
CREATE TABLE IF NOT EXISTS "user" (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    birthday DATE
);

-- Создание таблицы like (лайки к фильмам)
CREATE TABLE IF NOT EXISTS "like" (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES "user"(id) ON DELETE CASCADE,
    film_id INTEGER REFERENCES film(id) ON DELETE CASCADE,
    UNIQUE (user_id, film_id)
);

-- Создание таблицы friends (друзья)
CREATE TABLE IF NOT EXISTS friends (
    id SERIAL PRIMARY KEY,
    request_from_id INTEGER REFERENCES "user"(id) ON DELETE CASCADE,
    request_to_id INTEGER REFERENCES "user"(id) ON DELETE CASCADE,
    is_accepted BOOLEAN DEFAULT FALSE,
    UNIQUE (request_from_id, request_to_id)
);