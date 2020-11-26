INSERT INTO goals(id) VALUES (1), (2), (3), (4);

INSERT INTO languages(id, code) VALUES(1,'uk'), (2,'en'), (3,'ru');

INSERT INTO goal_translations(id, content, goal_id, language_id)
VALUES(1, 'Купіть бамбукову щітку', 1, 1),
      (2, 'Buy a bamboo brush', 1, 2),
      (3, 'Купите бамбуковую щетку', 1, 3),
      (4, 'Купіть компостер', 2, 1),
      (5, 'Buy composter', 2, 2),
      (6, 'Купитe компостер', 2, 3),
      (7, 'Почніть сортувати сміття', 3, 1),
      (8, 'Start sorting trash', 3, 2),
      (9, 'Начните сортировать мусор', 3, 3);


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

INSERT INTO habits (id, image, default_duration)
VALUES (1,'image1', 14),
       (2,'image2', 14);

INSERT INTO habit_assign (id, habit_id, user_id, acquired, create_date, suspended, duration)
VALUES (1, 1, 1, false, '2020-09-10 20:00:00', false, 14),
       (2, 2, 2, false, '2020-09-10 20:00:00', false, 14);

INSERT INTO user_goals(id, habit_assign_id, goal_id, status)
VALUES (1, 1, 1, 'ACTIVE'),
       (2, 2, 2, 'DONE');
