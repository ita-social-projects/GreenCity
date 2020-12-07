CREATE OR REPLACE FUNCTION public.fn_textsearchcolumn(
  search_column text,usid bigint)
    RETURNS TABLE(searchcolumn integer)
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE
    ROWS 1000

AS $BODY$
BEGIN
RETURN QUERY
  SELECT
  CASE search_column

      WHEN'eco_news_likes'  THEN eco_news_likes
      WHEN 'tips_and_tricks_likes'  THEN tips_and_tricks_likes
    when 'published_eco_news' then published_eco_news
    when 'eco_news_comment' then eco_news_comment
    when 'tips_and_tricks_comment' then tips_and_tricks_comment
      when 'acquired_habit' then acquired_habit
    when 'habit_streak' then habit_streak
    when 'social_networks' then social_networks
    when 'achievements' then achievements
      END

      FROM public.user_actions
      where user_id= usid;

END
$BODY$;
