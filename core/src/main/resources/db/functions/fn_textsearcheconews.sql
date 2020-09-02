CREATE OR REPLACE FUNCTION public.fn_textsearcheconews(
    search_phrase text)
    RETURNS TABLE(id bigint, creation_date timestamp with time zone, image_path character varying, author_id bigint, text character varying, title character varying, source character varying)
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
    ROWS 1000
AS '


BEGIN
    RETURN QUERY
        SELECT vn.id, vn.creation_date, vn.image_path, vn.author_id, vn.text, vn.title, vn.source
        FROM VW_EcoNewsWithTags AS vn
        WHERE vector @@ to_tsquery('simple',  REPLACE(REPLACE(search_phrase, ' '  , '|' ),'||', '|'))
        ORDER BY ts_rank(vector, plainto_tsquery('simple',  REPLACE(REPLACE(search_phrase, ' '  , '|' ),'||', '|'))) DESC;

END
';