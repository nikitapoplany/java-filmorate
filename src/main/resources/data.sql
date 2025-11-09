-- Инициализация данных для таблицы рейтингов MPA (Motion Picture Association)
-- Используем американскую систему рейтингов MPAA
MERGE INTO mpa KEY(mpa_id) VALUES (1, 'G');
MERGE INTO mpa KEY(mpa_id) VALUES (2, 'PG');
MERGE INTO mpa KEY(mpa_id) VALUES (3, 'PG-13');
MERGE INTO mpa KEY(mpa_id) VALUES (4, 'R');
MERGE INTO mpa KEY(mpa_id) VALUES (5, 'NC-17');

-- Инициализация данных для таблицы жанров
MERGE INTO genre KEY(genre_id) VALUES (1, 'Комедия');
MERGE INTO genre KEY(genre_id) VALUES (2, 'Драма');
MERGE INTO genre KEY(genre_id) VALUES (3, 'Мультфильм');
MERGE INTO genre KEY(genre_id) VALUES (4, 'Триллер');
MERGE INTO genre KEY(genre_id) VALUES (5, 'Документальный');
MERGE INTO genre KEY(genre_id) VALUES (6, 'Боевик');