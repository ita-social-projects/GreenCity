DO
$$
DECLARE
habit_id INTEGER;

BEGIN
SELECT id INTO habit_id FROM achievement_categories WHERE name = 'HABIT';

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES
    ('COMPLETED_HABIT_1_TIME', 'Завершено 1 звичку', 'Completed 1 habit', 5, habit_id, 1),
    ('COMPLETED_HABIT_5_TIMES', 'Завершено 5 звичок', 'Completed 5 habits', 10, habit_id, 5),
    ('COMPLETED_HABIT_10_TIMES', 'Завершено 10 звичок', 'Completed 10 habits', 15, habit_id, 10),
    ('COMPLETED_HABIT_25_TIMES', 'Завершено 25 звичок', 'Completed 25 habits', 25, habit_id, 25),
    ('COMPLETED_HABIT_50_TIMES', 'Завершено 50 звичок', 'Completed 50 habits', 50, habit_id, 50),
    ('COMPLETED_HABIT_100_TIMES', 'Завершено 100 звичок', 'Completed 100 habits', 100, habit_id, 100);

END $$;

