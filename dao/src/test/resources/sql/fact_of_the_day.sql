INSERT INTO fact_of_the_day(id, name, create_date)
VALUES (1, 'fact of the day', '2020-09-30T00:00');

INSERT INTO languages(id, code) VALUES (1, 'en');

INSERT INTO fact_of_the_day_translations(id, content, fact_of_the_day_id, language_id)
VALUES (1, 'fact translation', 1, 1);
