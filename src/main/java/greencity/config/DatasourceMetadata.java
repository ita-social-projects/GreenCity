package greencity.config;

import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Configuration class that obtains metadata of the database.
 * @author Yurii Koval
 */
@Slf4j
@Configuration
public class DatasourceMetadata {

    private final JdbcTemplate jdbcTemplate;

    /**
     *
     * @param jdbcTemplate {@link JdbcTemplate}
     */
    @Autowired
    public DatasourceMetadata(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Gets zoneId of the database.
     * WARNING: this method uses native query for PostgreSQL.
     * It may NOT WORK with another DBMS.
     *
     * @return zoneId instance of the database.
     */
    @Bean
    public ZoneId datasourceTimezone() {
        String zoneId = jdbcTemplate.queryForObject("SHOW TIMEZONE", String.class);
        if (zoneId == null) {
            throw new FailedToObtainDatasourceTimezone();
        }
        log.info("Obtained timezone of the database is {}", zoneId);
        return ZoneId.of(zoneId);
    }

    public static class FailedToObtainDatasourceTimezone extends RuntimeException {}
}
