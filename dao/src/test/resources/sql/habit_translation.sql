INSERT INTO users (id, date_of_registration, email, email_notification, name, role, user_status,
                   refresh_token_key, rating, last_activity_time, first_name, city, user_credo, show_location,
                   show_eco_place, show_shopping_list)
VALUES (1, '2020-09-10 20:00:00+00', 'majboroda.artur@mail.com', 0, 'artur', 0, 2,'z4hbh12chxb6vg1urh117-btu1wf-9jltmjl', 10, '2020-09-10 21:00:00', 'Artur', 'Lviv','Те, що противно природі, до добра ніколи не веде.', true, true, true),
       (2, '2020-09-10 20:00:00+00', 'komarov.eduard@mail.com', 0, 'eduard', 0, 2,'4u-qx6uyo-k1-6bscsluy3phvhkhvxvul2-3', 20, '2020-09-10 21:00:00', 'Eduard', 'Kyiv','Людина і природа - приклад ідеальної гармонії.', true, true, true),
       (3, '2020-03-10 20:00:00+00', 'oleksiv.shamil@mail.com', 0, 'shamil', 0, 2,'5rrbeueh431wzhhpwso070ci813u678jopzn', 20, '2020-03-10 21:00:00', 'Shamil', 'Dnipro','У свою годину своя поезія в природі', true, true, true),
       (4, '2020-08-10 20:00:00+00', 'irshov.grugorij@mail.com', 0, 'grugorij', 0, 2,'hluhf7-n-ppl18sws9i-cfnve8dh2w2yzvth', 20, '2020-08-10 21:00:00', 'Grugorij', 'Kyiv','Немає нічого більш винахідливого, ніж природа.', true, true, true),
       (5, '2020-09-10 20:00:00+00', 'liakh.roman@mail.com', 0, 'roman', 0, 2,'urx64695muw9dv56xefu8tzh-h5lxuj3tt3s', 20, '2020-09-10 21:00:00', 'Roman', 'Lviv','Людина не стане паном природи, поки вона не стала паном самого себе', true, true, true),
       (6, '2020-09-10 20:00:00+00', 'miasnuk.ihor@mail.com', 0, 'ihor', 0, 2,'71-9b3xllh-bhf6tyre9kfrwax1p7f1nrbai', 20, '2020-09-10 21:00:00', 'Ihor', 'Kyiv','Шукання цілей в природі має своїм джерелом невігластво.', true, true, true),
       (7, '2020-06-10 20:00:00+00', 'efimov.ivan@mail.com', 0, 'ivan', 0, 2,'gfzymgr7smf11pc8ib2g-2xx3w04cc65reqb', 20, '2020-06-10 21:00:00', 'Ivan', 'Dnipro','Основним законом природи є збереження людства.', true, true, true);

INSERT INTO habits (id, image)
VALUES (1, 'image1'),
       (2, 'image2'),
       (3, 'image3'),
       (4, 'image4'),
       (5, 'image5'),
       (6, 'image6'),
       (7, 'image7');

INSERT INTO habit_assign (id, habit_id, user_id, status, create_date)
VALUES (1, 1, 1, 'ACTIVE', '2020-09-09 20:00:00+00'),
       (2, 1, 2, 'ACTIVE', '2020-09-10 20:00:00+00'),
       (3, 3, 3, 'ACTIVE', '2020-09-11 20:00:00+00'),
       (4, 4, 4, 'ACTIVE', '2020-09-12 20:00:00+00'),
       (5, 5, 5, 'ACTIVE', '2020-09-13 20:00:00+00'),
       (6, 6, 6, 'ACTIVE', '2020-09-14 20:00:00+00');

INSERT INTO languages (id, code)
VALUES (1, 'ua'),
       (2, 'en'),
       (3, 'ru');

INSERT INTO habit_translation (id, name, description, habit_item, language_id, habit_id)
VALUES (1, 'Економити пакети', 'Опис пакетів', 'Пакети', 1, 1),
       (2, 'Save bags', 'bag description', 'bags', 2, 1),
       (3, 'экономить пакеты', 'описание пакетов', 'Пакеты', 3, 1),
       (4, 'Відмовитись від одноразових стаканчиків', 'Опис стаканчиків', 'Стаканчики', 1, 6),
       (5, 'Discard disposable cups', 'cap description', 'caps', 2, 1),
       (6, 'Отказаться от одноразовых стаканчиков', 'описание стаканчиков', 'Стаканчики', 3, 6);
