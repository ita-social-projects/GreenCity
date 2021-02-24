CREATE OR REPLACE FUNCTION public.fn_recommended_friends(
    current_user_id bigint)
    RETURNS TABLE
            (
                id              bigint,
                name            character varying,
                city            character varying,
                rating          double precision,
                profile_picture character varying
            )
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000

AS
$BODY$
BEGIN

    RETURN QUERY
        WITH recomended_habits
                 AS (
                SELECT u.id, h.habit_id, COUNT(h.habit_id) OVER (PARTITION BY u.id) AS habit_rating
                FROM public.users u
                         JOIN public.habit_assign h
                              ON h.user_id = u.id
                WHERE h.habit_id IN (SELECT ha.habit_id
                                     FROM habit_assign ha
                                     WHERE ha.user_id = current_user_id)
            ),
             recomended_comments
                 AS (
                 SELECT u.id,
                        ec.eco_news_id                                 AS news_id,
                        COUNT(ec.eco_news_id) OVER (PARTITION BY u.id) AS comment_rating
                 FROM public.users u
                          JOIN (SELECT DISTINCT eco_news_id, user_id FROM econews_comment) AS ec
                               ON ec.user_id = u.id
                 WHERE ec.eco_news_id IN (SELECT DISTINCT ecd.eco_news_id
                                          FROM econews_comment ecd
                                          WHERE ecd.user_id = current_user_id)
             ),
             common_friends
                 AS (
                 SELECT u.id, uf.friend_id, COUNT(uf.friend_id) OVER (PARTITION BY u.id) AS friend_rating
                 FROM public.users u
                          JOIN users_friends uf
                               ON u.id = uf.user_id
                 WHERE uf.friend_id IN (SELECT DISTINCT u.id
                                        FROM public.users u
                                        WHERE u.id = current_user_id)
             )

        SELECT DISTINCT u.id, u.name,u.city,u.rating,u.profile_picture
        FROM users u
                 JOIN recomended_habits rh
                      ON rh.id = u.id AND rh.id <> current_user_id
        UNION
        SELECT DISTINCT u.id, u.name,u.city,u.rating, u.profile_picture
        FROM users u
                 JOIN recomended_comments rc
                      ON rc.id = u.id AND rc.id <> current_user_id
        WHERE comment_rating >= 3
        UNION
        SELECT DISTINCT u.id, u.name,u.city,u.rating, u.profile_picture
        FROM users u
                 JOIN common_friends cf
                      ON cf.id = u.id AND cf.id <> current_user_id;
END
$BODY$;

ALTER FUNCTION public.fn_recommended_friends(bigint)
    OWNER TO postgres;