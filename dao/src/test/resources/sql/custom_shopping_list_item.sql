insert into languages(id, code)
values(1,'ua'),
       (2,'en');
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
VALUES (1, current_date, 'foo@bar.com', 1, 'foo', current_date, 1, 1, 'quux',1),
       (2, current_date, 'foot@bar.com', 1, 'doo', current_date, 1, 1, 'asdasd',2);
INSERT INTO custom_shopping_list_items(id, text, user_id, status, date_completed)
VALUES (1, 'Buy a bamboo brush', 1, 'DONE', '2020-09-10 20:00:001'),
       (2, 'Buy composter', 1, 'DONE', '2020-09-10 20:00:001'),
       (3, 'Buy composter 2', 1, 'DONE', '2020-09-10 20:00:001'),
       (4, 'Start sorting trash', 1, 'ACTIVE', null),
       (5, 'Start recycling batteries', 1, 'ACTIVE', null),
       (6, 'Finish book about vegans', 2, 'ACTIVE', null);
