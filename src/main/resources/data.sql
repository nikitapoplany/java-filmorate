DELETE FROM mpa_rating;
DELETE FROM genre;

INSERT INTO mpa_rating (name) VALUES ('G');
INSERT INTO mpa_rating (name) VALUES ('PG');
INSERT INTO mpa_rating (name) VALUES ('PG-13');
INSERT INTO mpa_rating (name) VALUES ('R');
INSERT INTO mpa_rating (name) VALUES ('NC-17');

INSERT INTO genre (name) VALUES ('Комедия');
INSERT INTO genre (name) VALUES ('Драма');
INSERT INTO genre (name) VALUES ('Мультфильм');
INSERT INTO genre (name) VALUES ('Триллер');
INSERT INTO genre (name) VALUES ('Документальный');
INSERT INTO genre (name) VALUES ('Боевик');