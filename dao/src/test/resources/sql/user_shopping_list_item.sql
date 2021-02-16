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
VALUES (1, '2020-09-30T00:00', 'test@email.com', 0, 'SuperTest', '2020-09-30T00:00', 0, 2, 'secret', 10, 'New York');

INSERT INTO shopping_list_items(id) VALUES (1), (2);

INSERT INTO habits (id, image, default_duration)
VALUES (1, 'image1', 14),
       (2, 'image2', 14);

INSERT INTO habit_assign (id,habit_id, user_id, status, create_date, duration)
VALUES (1, 1, 1, 'ACTIVE', '2020-09-10 20:00:00', 14);

INSERT INTO user_shopping_list(id, habit_assign_id, shopping_list_item_id, status)
VALUES (1, 1, 1, 'DONE');
