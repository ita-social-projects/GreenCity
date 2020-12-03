CREATE OR REPLACE FUNCTION public.fn_searcheconews(
    search_query text
)
    RETURNS TABLE(id bigint, creation_date timestamp with time zone, image_path character varying, author_id bigint, text character varying, title character varying, source character varying)
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
    ROWS 1000
AS $BODY$
BEGIN
    RETURN QUERY
        SELECT DISTINCT en.id,
                        en.creation_date,
                        en.image_path,
                        en.author_id,
                        en.text,
                        en.title,
                        en.source
        FROM eco_news en
                 JOIN public.eco_news_tags et on en.id=et.eco_news_id
                 JOIN public.tags tg on tg.id=et.tags_id

        WHERE
                LOWER(en.title) LIKE LOWER(CONCAT('%', search_query, '%'))
           OR LOWER(en.text) LIKE LOWER(CONCAT('%', search_query, '%'))
           OR et.tags_id IN (
            SELECT  et.tags_id FROM eco_news_tags et
                                        JOIN tags ttt
                                             ON ttt.id = et.tags_id
            WHERE LOWER(ttt.name) LIKE LOWER(CONCAT('%', search_query, '%')))

    ;

END
$BODY$;