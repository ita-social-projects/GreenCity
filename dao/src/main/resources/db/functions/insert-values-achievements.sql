DO $$
DECLARE
habit_id INTEGER;
  create_news_id INTEGER;
  comment_or_reply_id INTEGER;
  like_comment_or_reply_id INTEGER;
  share_news_id INTEGER;
  achievement_id INTEGER;
BEGIN
  -- Acquire IDs from achievement_categories
SELECT id INTO habit_id FROM achievement_categories WHERE name = 'HABIT';
SELECT id INTO create_news_id FROM achievement_categories WHERE name = 'CREATE_NEWS';
SELECT id INTO comment_or_reply_id FROM achievement_categories WHERE name = 'COMMENT_OR_REPLY';
SELECT id INTO like_comment_or_reply_id FROM achievement_categories WHERE name = 'LIKE_COMMENT_OR_REPLY';
SELECT id INTO share_news_id FROM achievement_categories WHERE name = 'SHARE_NEWS';
SELECT id INTO achievement_id FROM achievement_categories WHERE name = 'ACHIEVEMENT';

-- Insert statements for each category with condition
INSERT INTO achievements(name, achievement_category_id, score, condition)
VALUES
    ('ACQUIRED_HABIT_14_DAYS', habit_id, 10, 14),
    ('ACQUIRED_HABIT_21_DAYS', habit_id, 10, 21),
    ('ACQUIRED_HABIT_30_DAYS', habit_id, 10, 30);

INSERT INTO achievements(name, achievement_category_id, score, condition)
VALUES
    ('CREATED_5_NEWS', create_news_id, 10, 5),
    ('CREATED_10_NEWS', create_news_id, 10, 10),
    ('CREATED_25_NEWS', create_news_id, 10, 25),
    ('CREATED_50_NEWS', create_news_id, 10, 50),
    ('CREATED_100_NEWS', create_news_id, 10, 100);

INSERT INTO achievements(name, achievement_category_id, score, condition)
VALUES
    ('COMMENT_OR_REPLY_1_TIMES', comment_or_reply_id, 10, 1),
    ('COMMENT_OR_REPLY_5_TIMES', comment_or_reply_id, 10, 5),
    ('COMMENT_OR_REPLY_10_TIMES', comment_or_reply_id, 10, 10),
    ('COMMENT_OR_REPLY_25_TIMES', comment_or_reply_id, 10, 25),
    ('COMMENT_OR_REPLY_50_TIMES', comment_or_reply_id, 10, 50),
    ('COMMENT_OR_REPLY_100_TIMES', comment_or_reply_id, 10, 100);

INSERT INTO achievements(name, achievement_category_id, score, condition)
VALUES
    ('LIKE_COMMENT_OR_REPLY_1_TIMES', like_comment_or_reply_id, 10, 1),
    ('LIKE_COMMENT_OR_REPLY_5_TIMES', like_comment_or_reply_id, 10, 5),
    ('LIKE_COMMENT_OR_REPLY_10_TIMES', like_comment_or_reply_id, 10, 10),
    ('LIKE_COMMENT_OR_REPLY_25_TIMES', like_comment_or_reply_id, 10, 25),
    ('LIKE_COMMENT_OR_REPLY_50_TIMES', like_comment_or_reply_id, 10, 50),
    ('LIKE_COMMENT_OR_REPLY_100_TIMES', like_comment_or_reply_id, 10, 100);

INSERT INTO achievements(name, achievement_category_id, score, condition)
VALUES
    ('SHARE_NEWS_1_TIMES', share_news_id, 10, 1),
    ('SHARE_NEWS_5_TIMES', share_news_id, 10, 5),
    ('SHARE_NEWS_10_TIMES', share_news_id, 10, 10),
    ('SHARE_NEWS_25_TIMES', share_news_id, 10, 25),
    ('SHARE_NEWS_50_TIMES', share_news_id, 10, 50),
    ('SHARE_NEWS_100_TIMES', share_news_id, 10, 100);

INSERT INTO achievements(name, achievement_category_id, score, condition)
VALUES
    ('FIRST_5_ACHIEVEMENTS', achievement_id, 10, 5),
    ('FIRST_10_ACHIEVEMENTS', achievement_id, 10, 10),
    ('FIRST_25_ACHIEVEMENTS', achievement_id, 10, 25),
    ('FIRST_50_ACHIEVEMENTS', achievement_id, 10, 50),
    ('FIRST_100_ACHIEVEMENTS', achievement_id, 10, 100);

END $$;
