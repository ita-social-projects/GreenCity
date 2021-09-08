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

INSERT INTO habits (id, image)
VALUES (1, 'image1'),
       (2, 'image2'),
       (3, 'image3'),
       (4, 'image4');

INSERT INTO habit_assign (id, habit_id, user_id, status, create_date)
VALUES (1, 1, 1, 'INPROGRESS', '2020-09-09 11:00:00+00'),
       (2, 2, 2, 'CANCELLED', '2020-09-10 11:00:00+00'),
       (3, 3, 3, 'INPROGRESS', '2020-09-11 11:00:00+00'),
       (4, 4, 3, 'ACQUIRED', '2021-07-09 11:00:00+00');


INSERT INTO habit_translation (id, name, description, habit_item, language_id, habit_id)
VALUES (1, 'Економити пакети', 'Опис пакетів', 'Пакети', 1, 1),
       (2, 'Save bags', 'bag description', 'bags', 2, 1),
       (3, 'экономить пакеты', 'описание пакетов', 'Пакеты', 3, 1),
       (4, 'Відмовитись від одноразових стаканчиків', 'Опис стаканчиків', 'Стаканчики', 1, 2),
       (5, 'Discard disposable cups', 'cap description', 'caps', 2, 2),
       (6, 'Отказаться от одноразовых стаканчиков', 'описание стаканчиков', 'Стаканчики', 3, 2);