package greencity.initializer;

import lombok.experimental.UtilityClass;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

@UtilityClass
public class PostgreSQLInitializer {
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.3");

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                "spring.liquibase.enabled=true",
                "spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.xml")
                .applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
