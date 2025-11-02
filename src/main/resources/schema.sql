-- Создание таблицы рейтингов MPA (Motion Picture Association)
CREATE TABLE IF NOT EXISTS mpa (
    mpa_id INT PRIMARY KEY,
    name VARCHAR(10) NOT NULL
);

-- Создание таблицы жанров
CREATE TABLE IF NOT EXISTS genre (
    genre_id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100),
    birthday DATE NOT NULL
);

-- Создание таблицы фильмов
CREATE TABLE IF NOT EXISTS film (
    film_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INT NOT NULL,
    mpa_id INT NOT NULL,
    FOREIGN KEY (mpa_id) REFERENCES mpa(mpa_id)
);

-- Создание таблицы связи фильмов и жанров (многие ко многим)
CREATE TABLE IF NOT EXISTS film_genre (
    film_id INT NOT NULL,
    genre_id INT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES film(film_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre(genre_id) ON DELETE CASCADE
);

-- Создание таблицы лайков (связь между пользователями и фильмами)
CREATE TABLE IF NOT EXISTS likes (
    film_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    FOREIGN KEY (film_id) REFERENCES film(film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Создание таблицы дружбы (односторонняя)
CREATE TABLE IF NOT EXISTS friendship (
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Создание индексов для оптимизации запросов
CREATE INDEX IF NOT EXISTS idx_film_mpa ON film(mpa_id);
CREATE INDEX IF NOT EXISTS idx_film_genre_film ON film_genre(film_id);
CREATE INDEX IF NOT EXISTS idx_film_genre_genre ON film_genre(genre_id);
CREATE INDEX IF NOT EXISTS idx_likes_film ON likes(film_id);
CREATE INDEX IF NOT EXISTS idx_likes_user ON likes(user_id);
CREATE INDEX IF NOT EXISTS idx_friendship_user ON friendship(user_id);
CREATE INDEX IF NOT EXISTS idx_friendship_friend ON friendship(friend_id);