INSERT INTO tags(id, type)
VALUES (1, 'ECO_NEWS'),(2, 'HABIT'),(3, 'TIPS_AND_TRICKS');

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
       (2, current_date, 'test@.com', 0, 'John', current_date, 1, 1, 'quux');

INSERT INTO eco_news(id,creation_date, image_path, author_id, text, title)
VALUES (1,'2020-10-05 18:33:51', 'image path', 1,
        'No matter where you live , you can make a difference in the impact of big agriculture.',
        'A New Way To Buy Food'),
       (2, '2020-10-06 18:55:18', 'image path', 2,
        'The benefits of biodegradable substances are only felt when they are disposed of properly.',
        'Why Biodegradable Products are Better for the Planet'),
       (3,'2020-04-11 19:06:36', 'image path', 2,
        'Over six gallons of water are required to produce one gallon of wine.',
        'Sustainable Wine Is Less Damaging to the Environment, But How Can You Spot It?'),
       (4,'2020-04-11 19:14:15', 'image path', 1,
        'Instead of trying to get rid of those lawn and garden weeds, harvest them for free homegrown meals.',
        'Please eat the dandelions: 9 edible garden weeds');

INSERT INTO habits(id, image, default_duration) VALUES(1, 'image1', 14), (2, 'image2', 14), (3, 'image3', 14);

INSERT INTO eco_news_tags(eco_news_id, tags_id)
VALUES(1,1),(2,1),(3,3),(4,2);

INSERT INTO tips_and_tricks (id, creation_date, author_id, image_path, source)
VALUES (1, '2020-10-03T00:00', 2, 'image path', 'source'),
       (2, '2020-10-08T00:00', 1, 'image path2', 'source2'),
       (3, '2020-10-08T00:00', 1, 'image path3', 'source3'),
       (4, '2020-10-08T00:00', 1, 'image path4', 'source4'),
       (5, '2020-10-08T00:00', 1, 'image path5', 'source5'),
       (6, '2020-10-08T00:00', 2, 'image path6', 'source6');

INSERT INTO tips_and_tricks_tags (tips_and_tricks_id, tags_id)
VALUES (1,3),(2,3),(3,3),(4,3),(5,3),(6,3);

INSERT INTO habits_tags(habit_id, tag_id) values(1, 1), (1, 2), (1, 3), (2, 2), (3, 3);


