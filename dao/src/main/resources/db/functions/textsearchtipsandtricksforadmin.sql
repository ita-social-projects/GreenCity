-- FUNCTION: public.fn_textsearchtipsandtricksforadmin(text, text)

-- DROP FUNCTION public.fn_textsearchtipsandtricksforadmin(text, text);

CREATE OR REPLACE FUNCTION public.fn_textsearchtipsandtricksforadmin(
    search_query text,
    language_code text)
    RETURNS TABLE(id bigint, creation_date timestamp with time zone, image_path character varying, author_id bigint, text character varying, title character varying, source character varying)
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE
    ROWS 1000
AS $BODY$
BEGIN
    RETURN QUERY
        select distinct tt.id,
                        tt.creation_date,
                        tt.image_path,
                        tt.author_id,
                        title.content,
                        textTrans.content,
                        tt.source
        from public.tips_and_tricks tt
                 left JOIN public.title_translations  title
                           on tt.id=title.tips_and_tricks_id
                 left JOIN public.text_translations textTrans
                           on textTrans.tips_and_tricks_id=tt.id
                 inner JOIN languages l
                            ON l.id = title.language_id
                 inner JOIN languages
                            ON l.id = textTrans.language_id

        where (CONCAT(tt.id,'') like lower(CONCAT('%', search_query, '%'))
            or lower(title.content) like lower(CONCAT('%', search_query, '%'))
            or lower(textTrans.content) like lower(CONCAT('%', search_query, '%'))
            or tt.id in (select tt.id from public.tips_and_tricks tt inner join public.users us
                                                                                on us.id=tt.author_id
                         where lower(us.name) like lower(CONCAT('%', search_query, '%')))
                   and l.code = language_code);

END
$BODY$;