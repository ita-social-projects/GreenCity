INSERT INTO user (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status) VALUES
  (1, '2019-09-03 11:42:37.823000', 'nazar.stasyuk@gmail.com', 'Назар', 'Стасюк', '2019-09-03 11:42:37.823000', 0, 2);
INSERT INTO user_own_security (id, password, user_id)
VALUES (1, '$2a$10$mAzH0BvVs/g2m6zl8CYxDuiLCu1V.PZjuGhKzHRdVxndDfExts4oO', 1);
INSERT INTO user (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status) VALUES
  (2, '2019-09-03 12:17:18.345000', 'dovgal.dmytr@gmail.com', 'Dima', 'Dovhal', '2019-09-03 12:17:18.346000', 0, 2);
INSERT INTO user_own_security (id, password, user_id)
VALUES (2, '$2a$10$HqJME/hE.0THMpGbzBci5usUe9T7t4dfLyL./JbndpWgGhjD2qyqC', 2);

INSERT INTO category (name) values ('Food');

INSERT INTO place (name, phone, email, modified_date, status, author_id, category_id, description)
values ('Forum', '0322 489 850', 'forum_lviv@gmail.com', '2004-05-23T14:25:10', 2, 1, 1, 'Shopping center');
INSERT INTO place (name, phone, email, modified_date, status, author_id, category_id, description) values
  ('Victoria Gardens', '0322 590 202', 'victoria_gardens@gmail.com', '2005-05-23T14:25:10', 2, 1, 1, 'Shopping center');
INSERT INTO place (name, phone, email, modified_date, status, author_id, category_id, description)
values ('Pravda', '0322 157 694', 'pravda_lviv@gmail.com', '2016-09-23T14:25:10', 2, 1, 1, 'Restaurant');
INSERT INTO place (name, phone, email, modified_date, status, author_id, category_id, description)
values ('Malevych', '0322 849 348', 'malevych_lviv@gmail.com', '2011-08-23T14:25:10', 2, 1, 1, 'Restaurant');
INSERT INTO place (name, phone, email, modified_date, status, author_id, category_id, description)
values ('Kryivka', '067 310 3145', 'kryivka_lviv@gmail.com', '2009-07-23T14:25:10', 2, 1, 1, 'Restaurant');

INSERT INTO specification (name) VALUES ('cafe');
INSERT INTO specification (name) VALUES ('own cup');
INSERT INTO specification (name) VALUES ('vegetarian food');
INSERT INTO specification (name) VALUES ('ith pets');
INSERT INTO specification (name) VALUES ('shop');

INSERT INTO specification_value (value, place_id, specification_id) VALUES ('disc', 1, 1);
INSERT INTO specification_value (value, place_id, specification_id) VALUES ('disc', 2, 2);
INSERT INTO specification_value (value, place_id, specification_id) VALUES ('disc', 3, 2);
INSERT INTO specification_value (value, place_id, specification_id) VALUES ('disc', 2, 3);
INSERT INTO specification_value (value, place_id, specification_id) VALUES ('disc', 1, 4);

INSERT INTO favorite_place (place_id, user_id) values (1, 1);
INSERT INTO favorite_place (place_id, user_id) values (2, 2);
INSERT INTO favorite_place (place_id, user_id) values (2, 2);

INSERT INTO rate (rate, place_id, user_id) values (5, 1, 1);
INSERT INTO rate (rate, place_id, user_id) values (4, 2, 2);
INSERT INTO rate (rate, place_id, user_id) values (2, 3, 1);
INSERT INTO rate (rate, place_id, user_id) values (3, 4, 2);
INSERT INTO rate (rate, place_id, user_id) values (5, 5, 1);

INSERT INTO opening_hours (close_time, open_time, week_day, place_id) VALUES ('20:00:00', '06:00:00', 0, 1);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id) VALUES ('20:00:00', '06:00:00', 1, 1);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id) VALUES ('20:00:00', '06:00:00', 2, 1);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id) VALUES ('20:00:00', '06:00:00', 3, 1);

INSERT INTO opening_hours (close_time, open_time, week_day, place_id) VALUES ('20:00:00', '07:00:00', 0, 2);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id) VALUES ('20:00:00', '07:00:00', 1, 2);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id) VALUES ('20:00:00', '07:00:00', 2, 2);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id) VALUES ('20:00:00', '07:00:00', 3, 2);


INSERT INTO comment (date, text, place_id, user_id) values ('2019-08-11', 'cool place', 1, 1);
INSERT INTO comment (date, text, place_id, user_id) values ('2019-02-11', 'great', 1, 1);
INSERT INTO comment (date, text, place_id, user_id) values ('2019-03-11', 'nice', 1, 1);
INSERT INTO comment (date, text, place_id, user_id) values ('2019-05-11', 'good', 1, 1);
INSERT INTO comment (date, text, place_id, user_id) values ('2019-08-11', 'well', 1, 1);

INSERT INTO location (address, lat, lng, place_id) VALUES ('вулиця Під Дубом', 49.84988, 24.022533, 1);
INSERT INTO location (address, lat, lng, place_id)
VALUES ('вулиця Кульпарківська, 226а, Львів, Львівська область, 79000', 49.807129, 23.977985, 2);
INSERT INTO location (address, lat, lng, place_id)
VALUES ('Torhovyy Dim Tsipperiv, площа Ринок, Львів, Львівська область, 79000', 49.842042, 24.030359, 3);
INSERT INTO location (address, lat, lng, place_id)
VALUES ('проспект В''ячеслава Чорновола, 2, Львів, Львівська область, 79000', 49.847489, 24.025975, 4);
INSERT INTO location (address, lat, lng, place_id)
VALUES ('пл. Ринок, 14 (підвал), Львів, Львівська область, 79000', 49.841311, 24.03229, 5);



