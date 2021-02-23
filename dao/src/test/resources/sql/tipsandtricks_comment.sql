INSERT INTO languages (id, code)
VALUES (1, 'ua');

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
VALUES (1, current_date, 'mail@.com', 1, 'Tom', current_date, 1, 1, 'quux',1),
       (2, current_date, 'test@.com', 0, 'John', current_date, 1, 1, 'quux',1);

INSERT INTO tips_and_tricks (id, creation_date, author_id, image_path, source)
VALUES (1, '2020-10-03T00:00', 2, 'image path', 'source'),
       (2, '2020-10-08T00:00', 1, 'image path2', 'source2'),
       (3, '2020-10-08T00:00', 1, 'image path3', 'source3'),
       (4, '2020-10-08T00:00', 1, 'image path4', 'source4'),
       (5, '2020-10-08T00:00', 1, 'image path5', 'source5'),
       (6, '2020-10-08T00:00', 2, 'image path6', 'source6');


INSERT INTO tipsandtricks_comment (id,
                   text,
                   created_date,
                   modified_date,
                   parent_comment_id,
                   user_id,
                   tips_and_tricks_id,
                   deleted)
VALUES (1, 'test comment', '2020-10-03T00:00', current_date, null, 2, 5, 'false'),
       (2, 'text', '2020-10-04T00:00', current_date, null, 2, 6, 'false'),
       (3, 'comment for text', '2020-10-05T00:00', current_date, 2, 1, 6, 'false'),
       (4, 'second comment for text', '2020-10-06T00:00', current_date, 2, 1, 6, 'false'),
       (5, 'deleted comment', '2020-10-06T00:00', current_date, 2, 1, 6, 'true');


INSERT INTO tipsandtricks_comment_users_liked(tipsandtricks_comment_id,users_liked_id)
VALUES(2,1),(2,2);


