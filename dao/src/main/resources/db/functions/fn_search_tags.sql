CREATE OR REPLACE FUNCTION public.fn_search_tags(
  filterr text)
    RETURNS TABLE(tag_id bigint, typee character varying,tag_translation_id bigint, namee character varying, language_code character varying) 
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    ROWS 1000
    
AS $BODY$
BEGIN
RETURN QUERY


SELECT DISTINCT tt.id, tt.type, ttt.id ,ttt.name, l.code
FROM public.tags tt
JOIN  public.tag_translations AS ttt
on tt.id=ttt.tag_id
join public.languages l 
on l.id=ttt.language_id
WHERE CONCAT(tt.id, '') LIKE LOWER(CONCAT(filterr, ''))
OR LOWER(CONCAT(tt.type, '')) LIKE LOWER(CONCAT('%', filterr, '%'))
OR CONCAT(ttt.id, '') LIKE LOWER(CONCAT(filterr, ''))
OR LOWER(l.code) LIKE LOWER(CONCAT('%',filterr, '%'))
OR LOWER(ttt.name) LIKE LOWER(CONCAT('%', filterr, '%'));


END
$BODY$;