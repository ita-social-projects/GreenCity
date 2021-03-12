insert into languages(id, code)
values(1,'ua');
INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_visit,
                   role,
                   user_status,
                   refresh_token_key,
                   language_id)
VALUES (1, current_date, 'foo@bar.com', 1, 'foo', current_date, 1, 1, 'quux',1);
INSERT INTO eco_news(id, creation_date, image_path, author_id, text, title)
VALUES (1, '2020-04-11 18:33:51', 'шлях до картинки', 1,
        'No matter where you live , you can make a difference in the impact of big agriculture. Purchasing foods produced by small, local farms, opting for organic produce whenever possible',
        'A New Way To Buy Food'),
       (2, '2020-04-11 18:55:18', 'шлях до картинки', 1,
        'The benefits of biodegradable substances are only felt when they are disposed of properly. Compost piles capture and return all of the recycled nutrients to the environment, and help to sustain new life. ',
        'Why Biodegradable Products are Better for the Planet');
insert into econews_comment (id, text, created_date, modified_date, parent_comment_id, user_id, eco_news_id, deleted)

values (1, 'Comment 1', '2020-09-18 12:42:41.998649 +03:00', '2020-09-18 12:42:41.998649 +03:00', null, 1, 1, false),
       (2, 'Comment 2', '2020-09-18 12:42:50.998649 +03:00', '2020-09-18 12:42:50.998649 +03:00', null, 1, 1, false),
       (3, 'Reply 1', '2020-09-18 12:42:52.998649 +03:00', '2020-09-18 12:42:52.998649 +03:00', 1, 1, 1, false),
       (4, 'Reply 2', '2020-09-18 12:43:52.998649 +03:00', '2020-09-18 12:43:52.998649 +03:00', 1, 1, 1, false),
       (5, 'Comment 3', '2020-09-18 14:45:52.998649 +03:00', '2020-09-18 14:45:52.998649 +03:00', null, 1, 1, true),
       (6, 'Reply 3', '2020-09-18 13:50:58.998649 +03:00', '2020-09-18 13:50:58.998649 +03:00', 2, 1, 1, false),
       (7, 'Comment 4', '2020-09-20 12:42:50.998649 +03:00', '2020-09-20 12:42:50.998649 +03:00', null, 1, 2, false);
