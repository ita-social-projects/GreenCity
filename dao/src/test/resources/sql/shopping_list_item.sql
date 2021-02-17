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

INSERT INTO shoppingListItems(id) VALUES (1), (2);

INSERT INTO user_goals(id, user_id, goal_id, status)
VALUES (1, 1, 1, 'DONE'),
       (2, 1, 1, 'DONE');

INSERT INTO languages(id, code) VALUES (1, 'en');

INSERT INTO goal_translations(id, content, goal_id, language_id) VALUES (1, 'shoppingListItem translation', 1, 1);
