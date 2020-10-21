INSERT INTO languages (id, code)
VALUES (1, 'uk'),
       (2, 'en'),
       (3, 'ru');

INSERT INTO habits (id, image)
VALUES (1, 'image_one.png'),
       (2, 'image_two.png'),
       (3, 'image_three.png');

INSERT INTO advices (id, habit_id)
VALUES (1, 1),
       (2, 2),
       (3, 3);

INSERT INTO advice_translations (id, language_id, advice_id, content)
VALUES (1, 1, 1, 'Привіт'),
       (2, 2, 2, 'Hello'),
       (3, 3, 3, 'Привет');


