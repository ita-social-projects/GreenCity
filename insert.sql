USE GreenCity;

INSERT INTO users (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status,
                   email_notification)
VALUES (1, '2019-09-03 11:42:37.823000', 'nazar.stasyuk@gmail.com', 'Назар', 'Стасюк', '2019-09-03 11:42:37.823000', 0,
        2, 0);
INSERT INTO own_security (id, password, user_id)
VALUES (1, '$2a$10$mAzH0BvVs/g2m6zl8CYxDuiLCu1V.PZjuGhKzHRdVxndDfExts4oO', 1);
INSERT INTO users (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status,
                   email_notification)
VALUES (2, '2019-09-03 12:17:18.345000', 'dovgal.dmytr@gmail.com', 'Dima', 'Dovhal', '2019-09-03 12:17:18.346000', 0,
        2, 0);
INSERT INTO own_security (id, password, user_id)
VALUES (2, '$2a$10$HqJME/hE.0THMpGbzBci5usUe9T7t4dfLyL./JbndpWgGhjD2qyqC', 2);
INSERT INTO users (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status,
                   email_notification)
VALUES (3, '2019-09-04 13:20:31.755000', 'rapac@clockus.ru', 'Paul', 'Kos', '2019-09-04 13:20:31.757000', 1, 2, 0);
INSERT INTO own_security (id, password, user_id)
VALUES (3, '$2a$10$Mdu2vmDtmjgATCh0EpZ6V.Q3uhJn5Kz4biDu.Ol3EX55Pv4D7Ltla', 3);
INSERT INTO users (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status,
                   email_notification)
VALUES (4, '2019-09-04 13:38:44.518000', 'rsssac@clockus.ru', 'Roman', 'KOcak', '2019-09-04 13:38:44.518000', 0, 2, 0);
INSERT INTO own_security (id, password, user_id)
VALUES (4, '$2a$10$5ms4Ni.xuIfSXp1RxScQjOlQvLbrCUlZUNNtkQm23jc99NQXkppMe', 4);
INSERT INTO users (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status,
                   email_notification)
VALUES (5, '2019-09-04 13:39:25.827000', 'rsssasssc@clockus.ru', 'Misha', 'Pavluv', '2019-09-04 13:39:25.827000', 0, 2,
        0);
INSERT INTO own_security (id, password, user_id)
VALUES (5, '$2a$10$QCM1BwKCmM2GCSeK6pTpOeaXrcYOLCleifvrytgwgB3FnoE497dV.', 5);
INSERT INTO users (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status,
                   email_notification)
VALUES (6, '2019-09-04 13:40:21.531000', '1warsssasssc@clockus.ru', 'Taras', 'Tymkiv', '2019-09-04 13:40:21.531000', 0,
        2, 0);
INSERT INTO own_security (id, password, user_id)
VALUES (6, '$2a$10$bQggp0SIPwHh5D/ahmm4reKJtsod6dcEo79WJBO0aIAUIs/j9JGrC', 6);
INSERT INTO users (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status,
                   email_notification)
VALUES (7, '2019-09-04 13:43:00.061000', '1warssssssssasssc@clockus.ru', 'Ihor', 'Zdebskiy',
        '2019-09-04 13:43:00.061000', 0, 2, 0);
INSERT INTO own_security (id, password, user_id)
VALUES (7, '$2a$10$v.k/53rC6NnIsosZEH0ezecuKWrG8fL4yOHXW5w3AudFeFUub0It2', 7);
INSERT INTO users (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status,
                   email_notification)
VALUES (8, '2019-09-04 13:44:08.939000', '1warssssssssassssc@clockus.ru', 'Amon', 'Azarov',
        '2019-09-04 13:44:08.939000', 0, 2, 0);
INSERT INTO own_security (id, password, user_id)
VALUES (8, '$2a$10$hSKDmeUboTyvBpUnXj8c2ulIXBeHq5rd4h.H0Oj8gpzYlwS0L78qO', 8);
INSERT INTO users (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status,
                   email_notification)
VALUES (9, '2019-09-04 14:04:14.931000', 'rostuk.khasanov@gmail.com', 'Rostyslav', 'Khasanov',
        '2019-09-04 14:04:14.931000', 1, 2, 0);
INSERT INTO own_security (id, password, user_id)
VALUES (9, '$2a$10$cgaNMFjdAFX6k810YSZDSuvKVlWFcq6/F7p2lcgLzK6sFB48dky2W', 9);
INSERT INTO users (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status)
VALUES (10, '2019-10-01 19:22:25.209000', 'milanmarian@mail.ru', 'Marian', 'Milian', '2019-10-01 19:22:25.209000', 1,
        2);
INSERT INTO own_security (id, password, user_id)
VALUES (10, '$2a$10$uptb7Z/KAiv9Bi5iaMPOHer4ScyDaiSVNR33eUcGqTjDHQ9twdmOS', 10);

INSERT INTO users (id, date_of_registration, email, first_name, last_name, last_visit, role, user_status)
VALUES (11, '2019-10-01 19:22:25.209000', 'milan@mail.ru', 'M', 'M', '2019-10-01 19:22:25.209000', 1, 2);
INSERT INTO own_security (id, password, user_id)
VALUES (11, '$2a$10$uptb7Z/KAiv9Bi5iaMPOHer4ScyDaiSVNR33eUcGqTjDHQ9twdmOS', 11);



INSERT INTO categories (name)
values ('Food');

INSERT INTO locations (address, lat, lng)
VALUES ('вулиця Під Дубом', 49.84988, 24.022533);
INSERT INTO locations (address, lat, lng)
VALUES ('Вулиця Кульпарківська, 226а, Львів, Львівська область, 79000', 49.807129, 23.977985);
INSERT INTO locations (address, lat, lng)
VALUES ('Площа Ринок, Львів, Львівська область, 79000', 49.842042, 24.030359);
INSERT INTO locations (address, lat, lng)
VALUES ('Проспект В''ячеслава Чорновола, 2, Львів, Львівська область, 79000', 49.847489, 24.025975);
INSERT INTO locations (address, lat, lng)
VALUES ('Площа Ринок, 14 (підвал), Львів, Львівська область, 79000', 49.841311, 24.03229);

INSERT INTO places (name, phone, email, modified_date, status, author_id, category_id, description, location_id)
values ('Forum', '0322 489 850', 'forum_lviv@gmail.com', '2004-05-23T14:25:10', 2, 1, 1, 'Shopping center', 1);
INSERT INTO places (name, phone, email, modified_date, status, author_id, category_id, description, location_id)
values ('Victoria Gardens', '0322 590 202', 'victoria_gardens@gmail.com', '2005-05-23T14:25:10', 2, 1, 1,
        'Shopping center',
        2);
INSERT INTO places (name, phone, email, modified_date, status, author_id, category_id, description, location_id)
values ('Pravda', '0322 157 694', 'pravda_lviv@gmail.com', '2016-09-23T14:25:10', 2, 1, 1, 'Restaurant', 3);
INSERT INTO places (name, phone, email, modified_date, status, author_id, category_id, description, location_id)
values ('Malevych', '0322 849 348', 'malevych_lviv@gmail.com', '2011-08-23T14:25:10', 2, 1, 1, 'Restaurant', 4);
INSERT INTO places (name, phone, email, modified_date, status, author_id, category_id, description, location_id)
values ('Kryivka', '067 310 3145', 'kryivka_lviv@gmail.com', '2009-07-23T14:25:10', 2, 1, 1, 'Restaurant', 5);

INSERT INTO specifications (name)
VALUES ('Animal');
INSERT INTO specifications (name)
VALUES ('Own cup');
INSERT INTO specifications (name)
VALUES ('Karaoke');
INSERT INTO specifications (name)
VALUES ('Shopping');
INSERT INTO specifications (name)
VALUES ('Ukrainian food');
INSERT INTO specifications (name)
VALUES ('Dance');

# INSERT INTO specifications (value, place_id, specification_id)
# VALUES ('disc', 1, 4);
# INSERT INTO specifications (value, place_id, specification_id)
# VALUES ('disc', 2, 2);
# INSERT INTO specifications (value, place_id, specification_id)
# VALUES ('disc', 3, 3);
# INSERT INTO specifications (value, place_id, specification_id)
# VALUES ('disc', 4, 6);
# INSERT INTO specifications (value, place_id, specification_id)
# VALUES ('disc', 5, 5);

INSERT INTO favorite_places (place_id, user_id, name)
values (1, 1, 'Forum');
INSERT INTO favorite_places (place_id, user_id, name)
values (2, 2, 'Victoria Gardens');
INSERT INTO favorite_places (place_id, user_id, name)
values (3, 3, 'Pravda');

INSERT INTO estimates (rate, place_id, user_id)
values (5, 1, 1);
INSERT INTO estimates (rate, place_id, user_id)
values (4, 2, 2);
INSERT INTO estimates (rate, place_id, user_id)
values (2, 3, 1);
INSERT INTO estimates (rate, place_id, user_id)
values (3, 4, 2);
INSERT INTO estimates (rate, place_id, user_id)
values (5, 5, 1);

INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 0, 1);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 1, 1);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 2, 1);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 3, 1);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 4, 1);

INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 0, 2);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 1, 2);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 2, 2);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 3, 2);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 4, 2);

INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 0, 3);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 1, 3);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 2, 3);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 3, 3);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 4, 3);

INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 0, 4);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 1, 4);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 2, 4);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 3, 4);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 4, 4);

INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 0, 5);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 1, 5);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 2, 5);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 3, 5);
INSERT INTO opening_hours (close_time, open_time, week_day, place_id)
VALUES ('20:00:00', '06:00:00', 4, 5);

insert into discount_values(id, value, place_id, specification_id)
values (1, 3, 1, 1),
       (2, 13, 2, 2),
       (3, 33, 3, 3),
       (4, 63, 4, 4),
       (5, 93, 5, 5),
       (6, 50, 5, 6);

# INSERT INTO advices(habit_dictionary_id, name)
# VALUES (1, 'Покладіть  до кожної сумки чи рюкзаку одну еко-сумку, так вона буде завжди з вами, якщо ви неочікувано зайдете в магазин.'),
#        (2, 'Не візьми стакан - збережи природу.'),
#        (1, 'Після переходу на багаторазові сумки вам більше не доведеться постійно купувати пакети для товарів і продуктів. Екосумки мають більший термін служби і повільно зношуються.'),
#        (1, 'Щоб зменшити побутові відходи, почніть із того, щоб відмовитися від пластикових пакетів.'),
#        (1, 'Всі багаторазові сумки дуже просто чистити. Немає необхідності турбуватися про те, що варення, мед або крихти потрапляють у куточки. Просто покладіть потім сумку в пральну або посудомийну машину.'),
#        (1, 'Екосумки дуже популярні серед спортсменів і всіх, хто веде здоровий спосіб життя.');

INSERT INTO habit_dictionary (name)
VALUES ('bag'),
       ('cap');

INSERT INTO habits (user_id, habit_dictionary_id, status, create_date)
VALUES (1, 1, 1, '2019-11-12 19:03:33'),
       (1, 2, 1, '2019-11-12 15:12:59'),
       (2, 1, 1, '2019-11-14 11:33:01'),
       (2, 2, 1, '2019-11-15 20:01:19'),
       (3, 1, 1, '2019-11-15 10:21:11'),
       (3, 2, 1, '2019-11-16 17:01:09'),
       (4, 1, 1, '2019-11-20 19:11:51'),
       (4, 2, 1, '2019-11-20 21:12:52');

INSERT INTO habit_statistics(rate, date, amount_of_items, habit_id)
VALUES ('GOOD', '2019-11-13', 12, 1),
       ('NORMAL', '2019-11-14', 9, 2),
       ('BAD', '2019-11-14', 2, 3),
       ('NORMAL', '2019-11-15', 5, 4),
       ('NORMAL', '2019-11-15', 7, 5),
       ('GOOD', '2019-11-16', 14, 6),
       ('NORMAL', '2019-11-16', 7, 7),
       ('GOOD', '2019-11-16', 15, 8);


INSERT INTO goals(text)
VALUES ('Buy a bamboo brush'),
       ('Buy composter'),
       ('Start sorting trash'),
       ('Start recycling batteries'),
       ('Finish book about vegans');

INSERT INTO advices(habit_dictionary_id, name)
VALUES (1, 'Put one eco-bag in each bag or backpack, so it will always be with you if you unexpectedly go to the store.'),
       (2, 'Don''t take a glass - protect nature.'),
       (1, 'After switching to reusable bags, you no longer have to constantly buy packages for goods and products. Eco bags have a longer life and wear out slowly.'),
       (1, 'To reduce household waste, start by giving up plastic bags.'),
       (1, 'All reusable bags are very easy to clean. There is no need to worry about jam, honey or crumbs falling into the corners. Just put the bag in the washing machine or dishwasher.'),
       (1, 'Eco Bags are very popular with athletes and anyone who leads a healthy lifestyle.');

INSERT INTO user_goals(user_id, goal_id, status, date_completed)
VALUES (1, 1, 'ACTIVE', null),
       (1, 2, 'DONE', '2019-11-15 12:44:36'),
       (2, 3, 'ACTIVE', null),
       (2, 4, 'DONE', '2019-11-14 19:04:51'),
       (3, 5, 'ACTIVE', null),
       (3, 4, 'DONE', '2019-11-11 13:55:13');