INSERT INTO tags(id, type)
VALUES (1, 'TIPS_AND_TRICKS'),(2, 'TIPS_AND_TRICKS'),(3, 'TIPS_AND_TRICKS');

INSERT INTO languages(id, code)
VALUES(1, 'ua'),(2, 'en'), (3, 'ru');

INSERT INTO tag_translations(id, name, tag_id, language_id) VALUES(1, 'Новини', 1, 1),
(2, 'News', 1, 2),(3, 'Новины', 1, 3),(4, 'Освіта', 2, 1),(5, 'Education', 2, 2),
(6, 'Образование', 2, 3),(7, 'Реклами', 3, 1),(8, 'Ads', 3, 2),(9, 'Рекламы', 3, 3);

INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key)
VALUES (1, current_date, 'mail@.com', 1, 'Tom', current_date, 1, 1, 'quux'),
       (2, current_date, 'test@.com', 2, 'John', current_date, 1, 1, 'quux');

INSERT INTO tips_and_tricks (id, creation_date, author_id, image_path, source)
VALUES (1, '2020-10-03T00:00', 2, 'image path', 'source'),
       (2, '2020-10-08T00:00', 1, 'image path2', 'source2'),
       (3, '2020-10-08T00:00', 1, 'image path3', 'source3'),
       (4, '2020-10-08T00:00', 1, 'image path4', 'source4'),
       (5, '2020-10-08T00:00', 1, 'image path5', 'source5'),
       (6, '2020-10-08T00:00', 2, 'image path6', 'source6');

INSERT INTO title_translations(id, content, tips_and_tricks_id, language_id)
VALUES(1, 'Заголовок', 1, 1), (2, 'Title', 1, 2), (3, 'Заглави', 1, 3),
      (4, 'ЗаголовокТест', 2, 1), (5, 'TitleTest', 2, 2), (6, 'ЗаглавиеТест', 2, 3);

INSERT INTO text_translations(id, content, tips_and_tricks_id, language_id)
      VALUES(1, 'ТекстТекстТекстТекстТекст', 1, 1), (2, 'TextTextTextTextTextText', 1, 2), (3, 'ТекстТекстТекстТекстТекст', 1, 3),
            (4, 'ТекстТекстТекстТекстТекст2', 2, 1), (5, 'TextTextTextTextTextText2', 2, 2), (6, 'ТекстТекстТекстТекстТекст2', 2, 3);


INSERT INTO tips_and_tricks_tags (tips_and_tricks_id, tags_id)
VALUES (1,1), (2,2), (3,3), (4,3), (5,2), (6,1);