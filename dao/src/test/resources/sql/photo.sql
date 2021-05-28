INSERT INTO languages(id,code)
values (1, 'ua');
INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key,
                   language_id)
VALUES (1, current_date, 'foo@bar.com', 1, 'foo', current_date, 1, 1, 'quux',1);
insert into photos(id, name)
values (1, 'image');
