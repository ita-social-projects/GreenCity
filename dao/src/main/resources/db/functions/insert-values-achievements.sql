DO
$$
DECLARE
habit_id INTEGER;
create_news_id INTEGER;
comment_or_reply_id INTEGER;
like_comment_or_reply_id INTEGER;
share_news_id INTEGER;
achievement_id INTEGER;
create_event_id INTEGER;
join_event_id INTEGER;

BEGIN
SELECT id INTO habit_id FROM achievement_categories WHERE name = 'HABIT';
SELECT id INTO create_news_id FROM achievement_categories WHERE name = 'CREATE_NEWS';
SELECT id INTO comment_or_reply_id FROM achievement_categories WHERE name = 'COMMENT_OR_REPLY';
SELECT id INTO like_comment_or_reply_id FROM achievement_categories WHERE name = 'LIKE_COMMENT_OR_REPLY';
SELECT id INTO share_news_id FROM achievement_categories WHERE name = 'SHARE_NEWS';
SELECT id INTO achievement_id FROM achievement_categories WHERE name = 'ACHIEVEMENT';
SELECT id INTO create_event_id FROM achievement_categories WHERE name = 'CREATE_EVENT';
SELECT id INTO join_event_id FROM achievement_categories WHERE name = 'JOIN_EVENT';
DELETE
FROM achievements;

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('ACQUIRED_HABIT_14_DAYS', 'Набуття звички протягом 14 днів', 'Acquired habit 14 days', 20, habit_id, 14),
       ('ACQUIRED_HABIT_21_DAYS', 'Набуття звички протягом 21 днів', 'Acquired habit 21 days', 30, habit_id, 21),
       ('ACQUIRED_HABIT_30_DAYS', 'Набуття звички протягом 30 днів', 'Acquired habit 30 days', 40, habit_id, 30);

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('CREATED_1_NEWS', 'Створено перший новину', 'Created first news', 10, create_news_id, 1),
       ('CREATED_5_NEWS', 'Створено 5 новин', 'Created 5 news', 25, create_news_id, 5),
       ('CREATED_10_NEWS', 'Створено 10 новин', 'Created 10 news', 50, create_news_id, 10),
       ('CREATED_25_NEWS', 'Створено 25 новин', 'Created 25 news', 150, create_news_id, 25),
       ('CREATED_50_NEWS', 'Створено 50 новин', 'Created 50 news', 250, create_news_id, 50),
       ('CREATED_100_NEWS', 'Створено 100 новин', 'Created 100 news', 500, create_news_id, 100);

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('COMMENT_OR_REPLY_1_TIMES', 'Коментування або відповідь перший раз', 'Comment or reply first time', 5,
        comment_or_reply_id, 1),
       ('COMMENT_OR_REPLY_5_TIMES', 'Коментування або відповідь 5 разів', 'Comment or reply 5 times', 10,
        comment_or_reply_id, 5),
       ('COMMENT_OR_REPLY_10_TIMES', 'Коментування або відповідь 10 разів', 'Comment or reply 10 times', 15,
        comment_or_reply_id, 10),
       ('COMMENT_OR_REPLY_25_TIMES', 'Коментування або відповідь 25 разів', 'Comment or reply 25 times', 25,
        comment_or_reply_id, 25),
       ('COMMENT_OR_REPLY_50_TIMES', 'Коментування або відповідь 50 разів', 'Comment or reply 50 times', 50,
        comment_or_reply_id, 50),
       ('COMMENT_OR_REPLY_100_TIMES', 'Коментування або відповідь 100 разів', 'Comment or reply 100 times', 100,
        comment_or_reply_id, 100);

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('LIKE_COMMENT_OR_REPLY_1_TIMES', 'Лайкнуто коментар або відповідь перший раз', 'Liked a comment or reply first time', 1,
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
        'Liked comments or replies 100 times', 50, like_comment_or_reply_id, 100);

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('SHARE_NEWS_1_TIMES', 'Поширено новину перший раз', 'Shared news first time', 10, share_news_id, 1),
       ('SHARE_NEWS_5_TIMES', 'Поширено новини 5 разів', 'Shared news 5 times', 25, share_news_id, 5),
       ('SHARE_NEWS_10_TIMES', 'Поширено новини 10 разів', 'Shared news 10 times', 50, share_news_id, 10),
       ('SHARE_NEWS_25_TIMES', 'Поширено новини 25 разів', 'Shared news 25 times', 150, share_news_id, 25),
       ('SHARE_NEWS_50_TIMES', 'Поширено новини 50 разів', 'Shared news 50 times', 250, share_news_id, 50),
       ('SHARE_NEWS_100_TIMES', 'Поширено новини 100 разів', 'Shared news 100 times', 500, share_news_id, 100);

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('FIRST_5_ACHIEVEMENTS', 'Перші 5 досягнень', 'First 5 Achievements', 10, achievement_id, 5),
       ('FIRST_10_ACHIEVEMENTS', 'Перші 10 досягнень', 'First 10 Achievements', 25, achievement_id, 10),
       ('FIRST_25_ACHIEVEMENTS', 'Перші 25 досягнень', 'First 25 Achievements', 50, achievement_id, 25),
       ('FIRST_50_ACHIEVEMENTS', 'Перші 50 досягнень', 'First 50 Achievements', 100, achievement_id, 50),
       ('FIRST_100_ACHIEVEMENTS', 'Перші 100 досягнень', 'First 100 Achievements', 200, achievement_id, 100);
INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('CREATE_EVENT_1_TIMES', 'Створено подію 1 раз', 'Created event 1 time', 10, create_event_id, 1),
       ('CREATE_EVENT_5_TIMES', 'Створено події 5 разів', 'Created events 5 times', 25, create_event_id, 5),
       ('CREATE_EVENT_10_TIMES', 'Створено події 10 разів', 'Created events 10 times', 50, create_event_id, 10),
       ('CREATE_EVENT_25_TIMES', 'Створено події 25 разів', 'Created events 25 times', 150, create_event_id, 25),
       ('CREATE_EVENT_50_TIMES', 'Створено події 50 разів', 'Created events 50 times', 250, create_event_id, 50),
       ('CREATE_EVENT_100_TIMES', 'Створено події 100 разів', 'Created events 100 times', 500, create_event_id, 100);

INSERT INTO achievements(title, name, name_eng, score, achievement_category_id, condition)
VALUES ('JOIN_EVENT_1_TIMES', 'Приєднано до події 1 раз', 'Joined event 1 time', 5, join_event_id, 1),
       ('JOIN_EVENT_5_TIMES', 'Приєднано до подій 5 разів', 'Joined events 5 times', 10, join_event_id, 5),
       ('JOIN_EVENT_10_TIMES', 'Приєднано до подій 10 разів', 'Joined events 10 times', 25, join_event_id, 10),
       ('JOIN_EVENT_25_TIMES', 'Приєднано до подій 25 разів', 'Joined events 25 times', 50, join_event_id, 25),
       ('JOIN_EVENT_50_TIMES', 'Приєднано до подій 50 разів', 'Joined events 50 times', 100, join_event_id, 50),
       ('JOIN_EVENT_100_TIMES', 'Приєднано до подій 100 разів', 'Joined events 100 times', 200, join_event_id, 100);

END $$;
