# java-filmorate

ER-диаграмма базы данных проекта Filmorate
![ER-диаграмма](src/main/resources/er_diagram.png)

Примеры SQL запросов:

- Получение всех фильмов. Даёт информацию о фильмах, включая жанры и количество лайков.
```sql
SELECT 
    f.id,
    f.name,
    f.description,
    f.release_date,
    f.duration,
    m.name AS mpa_name,
    COUNT(l.id) AS likes_count,
    STRING_AGG(g.name, ', ') AS genres
FROM film f
LEFT JOIN mpa m ON f.mpa_id = m.id
LEFT JOIN "like" l ON f.id = l.film_id
LEFT JOIN film_genre fg ON f.id = fg.film_id
LEFT JOIN genre g ON fg.genre_id = g.id
GROUP BY f.id, f.name, f.description, f.release_date, f.duration, m.name;
```
- Получение всех пользователей. Показывает пользователей и количество подтверждённых друзей.
```sql
SELECT 
    u.*,
    (
        SELECT COUNT(*)
        FROM friends f
        WHERE (f.request_from_id = u.id OR f.request_to_id = u.id)
          AND f.is_accepted = TRUE
    ) AS friends_count
FROM "user" u;
```
- Топ N наиболее популярных фильмов
```sql
SELECT 
    f.id,
    f.name,
    COUNT(l.id) AS likes_count
FROM film f
LEFT JOIN "like" l ON f.id = l.film_id
GROUP BY f.id, f.name
ORDER BY likes_count DESC
LIMIT N;  -- например, 10
```
- Список общих друзей двух пользователей (id=1, id=2)
```sql
SELECT u.* FROM "user" u
JOIN friends a
  ON a.request_to_id = u.id
JOIN friends b
  ON a.request_to_id = b.request_to_id
WHERE a.request_from_id = 1
  AND b.request_from_id = 2;
```