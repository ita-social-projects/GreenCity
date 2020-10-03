CREATE OR REPLACE FUNCTION public.pg_stat_statements(
	showtext boolean,
	OUT userid oid,
	OUT dbid oid,
	OUT queryid bigint,
	OUT query text,
	OUT calls bigint,
	OUT total_time double precision,
	OUT min_time double precision,
	OUT max_time double precision,
	OUT mean_time double precision,
	OUT stddev_time double precision,
	OUT rows bigint,
	OUT shared_blks_hit bigint,
	OUT shared_blks_read bigint,
	OUT shared_blks_dirtied bigint,
	OUT shared_blks_written bigint,
	OUT local_blks_hit bigint,
	OUT local_blks_read bigint,
	OUT local_blks_dirtied bigint,
	OUT local_blks_written bigint,
	OUT temp_blks_read bigint,
	OUT temp_blks_written bigint,
	OUT blk_read_time double precision,
	OUT blk_write_time double precision)
    RETURNS SETOF record 
    LANGUAGE 'c'
    COST 1
    VOLATILE STRICT 
    ROWS 1000
AS '$libdir/pg_stat_statements', 'pg_stat_statements_1_3'
;