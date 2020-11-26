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

INSERT INTO goals(id) VALUES (1), (2);

INSERT INTO habits (image, default_duration)
VALUES ('image1', 14),
       ('image2', 14);

INSERT INTO habit_assign (habit_id, user_id, acquired, create_date, suspended, duration)
VALUES (1, 1, false, '2020-09-10 20:00:00', false, 14);

INSERT INTO user_goals(id, habit_assign_id, goal_id, status)
VALUES (1, 1, 1, 'DONE');