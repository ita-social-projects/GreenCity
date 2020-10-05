INSERT INTO tags(id, name)
VALUES (1, 'News'),
       (2, 'Events'),
       (3, 'Education');

INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_visit,
                   role,
                   user_status,
                   refresh_token_key)
VALUES (1, current_date, 'mail@.com', 1, 'Tom', current_date, 1, 1, 'quux'),
       (2, current_date, 'test@.com', 0, 'John', current_date, 1, 1, 'quux');

INSERT INTO tips_and_tricks (id, title, text, creation_date, author_id, image_path, source)
VALUES (1, 'TestTitle', 'Text', '2020-10-03T00:00', 1, 'image path', 'source'),
       (2, 'TestTitle2', 'Text3', '2020-10-04T00:00', 1, 'image path2', 'source2'),
       (3, 'TestTitle3', 'Text2', '2020-10-05T00:00', 1, 'image path3', 'source3'),
       (4, 'TestTitle4', 'Text4', '2020-10-06T00:00', 1, 'image path4', 'source4'),
       (5, 'TestTitle5', 'Text5', '2020-10-07T00:00', 2, 'image path5', 'source5'),
       (6, 'TestTitle6', 'Text6', '2020-10-08T00:00', 1, 'image path6', 'source6');

INSERT INTO tips_and_tricks_tags (tips_and_tricks_id, tags_id)
VALUES (1,1), (2,2), (3,3), (4,3), (5,2), (6,1);