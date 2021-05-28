insert into languages(id, code)
values(1,'ua');
INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key,
                   language_id)
VALUES (1, current_date, 'foo@bar.com', 1, 'foo', current_date, 1, 1, 'quux',1);
INSERT INTO specifications (id, name)
VALUES (1, 'Animal'),
       (2, 'Own cup'),
       (3, 'Karaoke'),
       (4, 'Shopping'),
       (5, 'Ukrainian food'),
       (6, 'Dance');
insert into categories(id, name)
values (1, 'Food'),
       (2, 'Sjja');
INSERT INTO locations (id, address, lat, lng)
VALUES (1, 'вулиця Під Дубом', 49.84988, 24.022533),
       (2, 'Вулиця Кульпарківська, 226а, Львів, Львівська область, 79000', 49.807129, 23.977985),
       (3, 'Площа Ринок, Львів, Львівська область, 79000', 49.842042, 24.030359),
       (4, 'Проспект В''ячеслава Чорновола, 2, Львів, Львівська область, 79000', 49.847489, 24.025975),
       (5, 'Площа Ринок, 14 (підвал), Львів, Львівська область, 79000', 49.841311, 24.03229);
INSERT INTO places (id, name, phone, email, modified_date, status, author_id, category_id, description, location_id)
VALUES (1, 'Forum', '0322 489 850', 'forum_lviv@gmail.com', '2004-05-23T14:25:10', 2, 1, 1, 'Shopping center', 1),
       (2, 'Victoria Gardens', '0322 590 202', 'victoria_gardens@gmail.com', '2005-05-23T14:25:10', 2, 1, 1,
        'Shopping center', 2),
       (3, 'Pravda', '0322 157 694', 'pravda_lviv@gmail.com', '2016-09-23T14:25:10', 2, 1, 1, 'Restaurant', 3),
       (4, 'Malevych', '0322 849 348', 'malevych_lviv@gmail.com', '2011-08-23T14:25:10', 2, 1, 1, 'Restaurant', 4),
       (5, 'Kryivka', '067 310 3145', 'kryivka_lviv@gmail.com', '2009-07-23T14:25:10', 2, 1, 1, 'Restaurant', 5);

INSERT INTO discount_values(id, value, place_id, specification_id)
VALUES (1, 3, 1, 1),
       (2, 13, 2, 2),
       (3, 33, 3, 3),
       (4, 63, 4, 4),
       (5, 93, 5, 5),
       (6, 50, 5, 6);
