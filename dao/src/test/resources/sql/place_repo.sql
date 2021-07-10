INSERT INTO languages (id, code)
VALUES (1, 'ua'),
       (2, 'en'),
       (3, 'ru');

INSERT INTO users (id, date_of_registration, email, email_notification, name, role, user_status,
                   refresh_token_key, rating, last_activity_time, first_name, city, user_credo, show_location,
                   show_eco_place, show_shopping_list, language_id)
VALUES (1, '2020-09-10 11:00:00+00', 'majboroda.artur@mail.com', 0, 'artur', 0, 2,
        'z4hbh12chxb6vg1urh117-btu1wf-9jltmjl', 10, '2020-09-10 11:00:00', 'Artur', 'Lviv',
        'Те, що противно природі, до добра ніколи не веде.', true, true, true, 1),
       (2, '2020-09-10 11:00:00+00', 'komarov.eduard@mail.com', 0, 'eduard', 0, 2,
        '4u-qx6uyo-k1-6bscsluy3phvhkhvxvul2-3', 20, '2020-09-10 11:00:00', 'Eduard', 'Kyiv',
        'Людина і природа - приклад ідеальної гармонії.', true, true, true, 2),
       (3, '2020-03-10 11:00:00+00', 'oleksiv.shamil@mail.com', 0, 'shamil', 0, 2,
        '5rrbeueh431wzhhpwso070ci813u678jopzn', 20, '2020-03-10 11:00:00', 'Shamil', 'Dnipro',
        'У свою годину своя поезія в природі', true, true, true, 3);

INSERT INTO locations(id, address, lat, lng)
VALUES (1, 'Brooklyn', 49.84988, 24.022533);

INSERT INTO places (id, name, status, author_id, modified_date, location_id)
VALUES (1, 'InterestingPlace', 0, 1, '2020-09-24T00:00', 1),
       (2, 'InterestingPlace2', 0, 1, '2020-09-25T00:00', 1),
       (3, 'InterestingPlace3', 0, 2, '2020-09-26T00:00', 1),
       (4, 'InterestingPlace4', 0, 2, '2020-09-27T00:00', 1),
       (5, 'InterestingPlace5', 0, 2, '2020-09-28T00:00', 1),
       (6, 'InterestingPlace6', 1, 3, '2020-09-30T00:00', 1);

INSERT INTO estimates(id, rate, place_id)
VALUES (1, 6, 1),
       (2, 10, 1);