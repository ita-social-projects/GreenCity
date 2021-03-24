INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key)
VALUES (1, current_date, 'foo@bar.com', 1, 'foo', current_date, 1, 1, 'quux');
INSERT INTO users (id,
                   date_of_registration,
                   email,
                   email_notification,
                   name,
                   last_activity_time,
                   role,
                   user_status,
                   refresh_token_key)
VALUES (2, current_date, 'baz@bar.com', 1, 'foofoo', current_date, 1, 1, 'quuxbaz');

INSERT INTO languages (id, code)
VALUES (1, 'en');

INSERT INTO habit_dictionary (id, image)
VALUES (1, 'foo');
INSERT INTO habit_dictionary (id, image)
VALUES (2, 'quux');

INSERT INTO habit_dictionary_translation (id, name, description, habit_item, language_id, habit_dictionary_id)
VALUES (1, 'foobar', 'bar', 'baz', 1, 1);
INSERT INTO habit_dictionary_translation (id, name, description, habit_item, language_id, habit_dictionary_id)
VALUES (2, 'quux', 'bar', 'eggs', 1, 2);

INSERT INTO habits (id, habit_dictionary_id, status, create_date)
VALUES (1, 1, true, current_date);
INSERT INTO habits (id, habit_dictionary_id, status, create_date)
VALUES (2, 2, true, current_date);
INSERT INTO habits (id, habit_dictionary_id, status, create_date)
VALUES (3, 1, true, current_date);

INSERT INTO habit_statistics (id, rate, date, amount_of_items, habit_id)
VALUES (1, 'quux', current_date - 1, 42, 1); -- This statistic is outdated
INSERT INTO habit_statistics (id, rate, date, amount_of_items, habit_id)
VALUES (2, 'quux', current_date, 8, 2);
INSERT INTO habit_statistics (id, rate, date, amount_of_items, habit_id)
VALUES (3, 'quux', current_date, 3, 3);
