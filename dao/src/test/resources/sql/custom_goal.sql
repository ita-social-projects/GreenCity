INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_visit,
                   role,
                   user_status,
                   refresh_token_key)
VALUES (1, current_date, 'foo@bar.com', 1, 'foo', current_date, 1, 1, 'quux'),
       (2, current_date, 'foot@bar.com', 1, 'doo', current_date, 1, 1, 'asdasd');
INSERT INTO custom_goals(id, text, user_id, status, date_completed)
VALUES (1, 'Buy a bamboo brush', 1, 'DONE', '2020-09-10 20:00:001'),
       (2, 'Buy composter', 1, 'DONE', '2020-09-10 20:00:001'),
       (3, 'Start sorting trash', 1, 'ACTIVE', null),
       (4, 'Start recycling batteries', 1, 'ACTIVE', null),
       (5, 'Finish book about vegans', 2, 'ACTIVE', null);

INSERT INTO user_goals(id, user_id, goal_id, status, date_completed)
VALUES (1, 1, null, 'ACTIVE', null),
       (2, 1, null, 'DISABLED', null),
       (3, 1, null, 'DONE', '2019-11-14 19:04:51'),
       (4, 2, null, 'ACTIVE', null);