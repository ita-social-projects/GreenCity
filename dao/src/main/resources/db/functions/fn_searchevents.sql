CREATE OR REPLACE FUNCTION public.fn_searchevents (
    search_query text, language_code text
)
    RETURNS TABLE (
                      id bigint,
                      title character varying,
                      description character varying,
                      organizer_id bigint,
                      title_image character varying,
                      is_open boolean,
                      creation_date date,
                      events_comments_id bigint
                  )
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
    ROWS 1000
AS $BODY$
BEGIN
    RETURN QUERY
        SELECT DISTINCT e.id,
                        e.title,
                        e.description,
                        e.organizer_id,
                        e.title_image,
                        e.is_open,
                        e.creation_date,
                        e.events_comments_id
        FROM events e
                 JOIN public.events_tags et on e.id=et.event_id
                 JOIN public.tag_translations tg on tg.tag_id=et.tag_id

        WHERE
                LOWER(e.title) LIKE LOWER(CONCAT('%', search_query, '%'))
           OR LOWER(e.description) LIKE LOWER(CONCAT('%', search_query, '%'))
           OR et.tag_id IN (
            SELECT  et.tag_id FROM events_tags et
                                       JOIN tag_translations ttt
                                            ON ttt.tag_id = et.tag_id
                                       JOIN languages l
                                            ON ttt.language_id = l.id

            WHERE LOWER(ttt.name) LIKE LOWER(CONCAT('%', search_query, '%')) AND l.code = language_code)
    ;

END
$BODY$;