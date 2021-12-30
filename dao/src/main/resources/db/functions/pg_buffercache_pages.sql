CREATE OR REPLACE FUNCTION public.pg_buffercache_pages(
	)
    RETURNS SETOF record 
    LANGUAGE 'c'
    COST 1
    VOLATILE 
    ROWS 1000
AS '$libdir/pg_buffercache', 'pg_buffercache_pages'
;