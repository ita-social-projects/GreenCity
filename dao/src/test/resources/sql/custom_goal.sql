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
INSERT INTO custom_goals(id, text, user_id)
VALUES (1, 'Buy a bamboo brush', 1),
       (2, 'Buy composter', 1),
       (3, 'Start sorting trash', 1),
       (4, 'Start recycling batteries', 1),
       (5, 'Finish book about vegans', 2);

INSERT INTO user_goals(id, user_id, goal_id, custom_goal_id, status, date_completed)
VALUES (1, 1, null, 1, 'ACTIVE', null),
       (2, 1, null, 2, 'DISABLED', null),
       (3, 1, null, 3, 'DONE', '2019-11-14 19:04:51'),
       (4, 2, null, 5, 'ACTIVE', null);