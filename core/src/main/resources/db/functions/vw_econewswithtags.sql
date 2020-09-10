CREATE IF NOT EXISTS MATERIALIZED VIEW public.vw_econewswithtags
    TABLESPACE pg_default
AS
SELECT en.id,
       en.creation_date,
       en.image_path,
       en.author_id,
       en.text,
       en.title,
       en.source,
       t.name AS tags,
       (setweight(to_tsvector('simple'::regconfig, COALESCE(t.name, ''::character varying)::text), 'A'::"char") || setweight(to_tsvector('simple'::regconfig, COALESCE(en.title, ''::character varying)::text), 'B'::"char")) || setweight(to_tsvector('simple'::regconfig, COALESCE(en.text, ''::character varying)::text), 'C'::"char") AS vector
FROM eco_news en
         JOIN eco_news_tags ent ON en.id = ent.eco_news_id
         JOIN tags t ON t.id = ent.tags_id
WITH DATA;
