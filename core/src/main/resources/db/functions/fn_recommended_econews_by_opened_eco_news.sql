CREATE OR REPLACE FUNCTION public.fn_recommended_econews_by_opened_eco_news(
	current_eco_news_id bigint)
    RETURNS TABLE(id bigint, title character varying, text character varying, creation_date timestamp with time zone, image_path character varying, author_id bigint, source character varying, news_rating bigint) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
    ROWS 1000
AS '
BEGIN

RETURN QUERY

WITH recomendet_news AS (
SELECT uniq_ent.*,
	   count(tags_id) OVER(PARTITION BY eco_news_id) AS news_rating
FROM (SELECT DISTINCT ent.* FROM eco_news_tags AS ent) AS uniq_ent

	WHERE uniq_ent.tags_id IN (SELECT t.id FROM eco_news_tags AS ent
				  				JOIN tags AS t ON t.id = ent.tags_id
				  				WHERE ent.eco_news_id = current_eco_news_id)
UNION

SELECT uniq_ent.*,
	   0 AS news_rating
FROM (SELECT DISTINCT ent.* FROM eco_news_tags AS ent) AS uniq_ent

	WHERE uniq_ent.tags_id NOT IN (SELECT t.id FROM eco_news_tags AS ent
				  				JOIN tags AS t ON t.id = ent.tags_id
				  				WHERE ent.eco_news_id = current_eco_news_id)
)

 	SELECT DISTINCT en.id, en.title, en.text, en.creation_date,
					en.image_path, en.author_id, en.source, ren.news_rating
		FROM recomendet_news AS ren
		JOIN eco_news AS en ON en.id = ren.eco_news_id
			WHERE en.id <> current_eco_news_id
		ORDER BY ren.news_rating DESC, en.creation_date DESC, en.id
		LIMIT 3;

END ';