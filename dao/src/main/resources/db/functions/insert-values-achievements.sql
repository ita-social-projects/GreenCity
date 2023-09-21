DO
$$
DECLARE
habit_id INTEGER;
  create_news_id
INTEGER;
  comment_or_reply_id
INTEGER;
  like_comment_or_reply_id
INTEGER;
  share_news_id
INTEGER;
  achievement_id
INTEGER;
BEGIN
SELECT id
INTO habit_id
FROM achievement_categories
WHERE name = 'HABIT';
SELECT id
INTO create_news_id
FROM achievement_categories
WHERE name = 'CREATE_NEWS';
SELECT id
INTO comment_or_reply_id
FROM achievement_categories
WHERE name = 'COMMENT_OR_REPLY';
SELECT id
INTO like_comment_or_reply_id
FROM achievement_categories
WHERE name = 'LIKE_COMMENT_OR_REPLY';
SELECT id
INTO share_news_id
FROM achievement_categories
WHERE name = 'SHARE_NEWS';
SELECT id
INTO achievement_id
FROM achievement_categories
WHERE name = 'ACHIEVEMENT';

DELETE
FROM achievements;

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('ACQUIRED_HABIT_14_DAYS', 'Набуття звички протягом 14 днів', 'Acquired habit 14 days', 20, habit_id, 14),
       ('ACQUIRED_HABIT_21_DAYS', 'Набуття звички протягом 21 днів', 'Acquired habit 21 days', 30, habit_id, 21),
       ('ACQUIRED_HABIT_30_DAYS', 'Набуття звички протягом 30 днів', 'Acquired habit 30 days', 40, habit_id, 30);

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('CREATED_1_NEWS', 'Створено 1 новину', 'Created 1 newі', 10, create_news_id, 5),
       ('CREATED_5_NEWS', 'Створено 5 новин', 'Created 5 news', 10, create_news_id, 5),
       ('CREATED_10_NEWS', 'Створено 10 новин', 'Created 10 news', 10, create_news_id, 10),
       ('CREATED_25_NEWS', 'Створено 25 новин', 'Created 25 news', 10, create_news_id, 25),
       ('CREATED_50_NEWS', 'Створено 50 новин', 'Created 50 news', 10, create_news_id, 50),
       ('CREATED_100_NEWS', 'Створено 100 новин', 'Created 100 news', 10, create_news_id, 100);

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('COMMENT_OR_REPLY_1_TIMES', 'Коментування або відповідь 1 раз', 'Comment or reply 1 time', 1,
        comment_or_reply_id, 1),
       ('COMMENT_OR_REPLY_5_TIMES', 'Коментування або відповідь 5 разів', 'Comment or reply 5 times', 5,
        comment_or_reply_id, 5),
       ('COMMENT_OR_REPLY_10_TIMES', 'Коментування або відповідь 10 разів', 'Comment or reply 10 times', 10,
        comment_or_reply_id, 10),
       ('COMMENT_OR_REPLY_25_TIMES', 'Коментування або відповідь 25 разів', 'Comment or reply 25 times', 25,
        comment_or_reply_id, 25),
       ('COMMENT_OR_REPLY_50_TIMES', 'Коментування або відповідь 50 разів', 'Comment or reply 50 times', 50,
        comment_or_reply_id, 50),
       ('COMMENT_OR_REPLY_100_TIMES', 'Коментування або відповідь 100 разів', 'Comment or reply 100 times', 100,
        comment_or_reply_id, 100);

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('LIKE_COMMENT_OR_REPLY_1_TIMES', 'Лайкнуто коментар або відповідь 1 раз', 'Liked a comment or reply 1 time', 1,
        like_comment_or_reply_id, 1),
       ('LIKE_COMMENT_OR_REPLY_5_TIMES', 'Лайкнуто коментарі або відповіді 5 разів',
        'Liked comments or replies 5 times', 5, like_comment_or_reply_id, 5),
       ('LIKE_COMMENT_OR_REPLY_10_TIMES', 'Лайкнуто коментарі або відповіді 10 разів',
        'Liked comments or replies 10 times', 10, like_comment_or_reply_id, 10),
       ('LIKE_COMMENT_OR_REPLY_25_TIMES', 'Лайкнуто коментарі або відповіді 25 разів',
        'Liked comments or replies 25 times', 25, like_comment_or_reply_id, 25),
       ('LIKE_COMMENT_OR_REPLY_50_TIMES', 'Лайкнуто коментарі або відповіді 50 разів',
        'Liked comments or replies 50 times', 50, like_comment_or_reply_id, 50),
       ('LIKE_COMMENT_OR_REPLY_100_TIMES', 'Лайкнуто коментарі або відповіді 100 разів',
        'Liked comments or replies 100 times', 100, like_comment_or_reply_id, 100);

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('SHARE_NEWS_1_TIMES', 'Поділено новину 1 раз', 'Shared news 1 time', 1, share_news_id, 1),
       ('SHARE_NEWS_5_TIMES', 'Поділено новини 5 разів', 'Shared news 5 times', 5, share_news_id, 5),
       ('SHARE_NEWS_10_TIMES', 'Поділено новини 10 разів', 'Shared news 10 times', 10, share_news_id, 10),
       ('SHARE_NEWS_25_TIMES', 'Поділено новини 25 разів', 'Shared news 25 times', 25, share_news_id, 25),
       ('SHARE_NEWS_50_TIMES', 'Поділено новини 50 разів', 'Shared news 50 times', 50, share_news_id, 50),
       ('SHARE_NEWS_100_TIMES', 'Поділено новини 100 разів', 'Shared news 100 times', 100, share_news_id, 100);

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('FIRST_5_ACHIEVEMENTS', 'Перші 5 досягнень', 'First 5 Achievements', 10, achievement_id, 5),
       ('FIRST_10_ACHIEVEMENTS', 'Перші 10 досягнень', 'First 10 Achievements', 25, achievement_id, 10),
       ('FIRST_25_ACHIEVEMENTS', 'Перші 25 досягнень', 'First 25 Achievements', 50, achievement_id, 25),
       ('FIRST_50_ACHIEVEMENTS', 'Перші 50 досягнень', 'First 50 Achievements', 100, achievement_id, 50),
       ('FIRST_100_ACHIEVEMENTS', 'Перші 100 досягнень', 'First 100 Achievements', 200, achievement_id, 100);
END $$;
