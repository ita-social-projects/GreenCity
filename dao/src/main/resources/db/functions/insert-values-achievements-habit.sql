DO
$$
DECLARE
habit_id INTEGER;

BEGIN
SELECT id INTO habit_id FROM achievement_categories WHERE name = 'HABIT';
DELETE
FROM achievements;

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES
    ('ENROLLED_HABIT_1_TIMES', 'Почато 1 звичку', 'Started 1 habit', 5, enroll_habit_id, 1),
    ('ENROLLED_HABIT_5_TIMES', 'Почато 5 звичок', 'Started 5 habits', 10, enroll_habit_id, 5),
    ('ENROLLED_HABIT_10_TIMES', 'Почато 10 звичок', 'Started 10 habits', 15, enroll_habit_id, 10),
    ('ENROLLED_HABIT_25_TIMES', 'Почато 25 звичок', 'Started 25 habits', 25, enroll_habit_id, 25),
    ('ENROLLED_HABIT_50_TIMES', 'Почато 50 звичок', 'Started 50 habits', 50, enroll_habit_id, 50),
    ('ENROLLED_HABIT_100_TIMES', 'Почато 100 звичок', 'Started 100 habits', 100, enroll_habit_id, 100);

END $$;
