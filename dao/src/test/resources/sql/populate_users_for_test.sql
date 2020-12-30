INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_visit,
                   role,
                   user_status,
                   refresh_token_key,
                   rating,
                   city)
VALUES (1, '2020-09-30T00:00', 'test@email.com', 0, 'SuperTest', '2020-09-30T00:00', 0, 2, 'secret', 10, 'New York'),
       (2, '2020-09-29T00:00', 'test2@email.com', 0, 'SuperTest2', '2020-09-29T00:00', 1, 2, 'secret2', 20, 'Kyiv'),
       (3, '2020-09-28T00:00', 'test3@email.com', 0, 'SuperTest3', '2020-09-28T00:00', 0, 2, 'secret3', 30, 'New York'),
       (4, '2020-09-27T00:00', 'test4@email.com', 0, 'SuperTest4', '2020-09-27T00:00', 0, 2, 'secret4', 40, 'New York'),
       (5, '2020-09-26T00:00', 'test5@email.com', 0, 'SuperTest5', '2020-09-26T00:00', 0, 2, 'secret5', 50, 'New York'),
       (6, '2020-09-25T00:00', 'test6@email.com', 0, 'SuperTest6', '2020-09-25T00:00', 0, 2, 'secret6', 60, 'New York'),
       (7, '2020-09-24T00:00', 'test7@email.com', 0, 'SuperTest7', '2020-09-24T00:00', 0, 2, 'secret7', 70, 'New York'),
       (8, '2020-09-23T00:00', 'test8@email.com', 0, 'SuperTest8', '2020-09-23T00:00', 0, 2, 'secret8', 80, 'New York'),
       (9, '2020-09-22T00:00', 'test9@email.com', 0, 'SuperTest9', '2020-09-22T00:00', 0, 2, 'secret9', 90, 'New York'),
       (10, '2020-09-21T00:00', 'test10@email.com', 0, 'SuperTest10', '2020-09-21T00:00', 0, 2, 'secret10', 100,
        'New York');

UPDATE users
SET profile_picture = 'someSecretPathToPicture'
WHERE id = 1;

INSERT INTO users_friends
VALUES (1, 2, 1, CURRENT_TIMESTAMP),
       (1, 4, 1, CURRENT_TIMESTAMP),
       (1, 5, 1, CURRENT_TIMESTAMP),
       (1, 6, 1, CURRENT_TIMESTAMP),
       (1, 7, 1, CURRENT_TIMESTAMP),
       (1, 8, 1, CURRENT_TIMESTAMP),
       (1, 9, 1, CURRENT_TIMESTAMP);
