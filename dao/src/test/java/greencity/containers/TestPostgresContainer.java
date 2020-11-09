package greencity.containers;

import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

public class TestPostgresContainer extends PostgreSQLContainer<TestPostgresContainer> {
    private static final String IMAGE_VERSION = "postgres:11";
    private static TestPostgresContainer container;

    private TestPostgresContainer() {
        super(IMAGE_VERSION);
    }

    public static TestPostgresContainer getInstance() {
        if (container == null) {
            container = new TestPostgresContainer();
        }
        return container;
    }

    @Bean
    public PostgreSQLContainer postgreSQLContainer() {
        final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer()
            .withDatabaseName("integration-tests-db")
            .withUsername("user")
            .withPassword("password");
        postgreSQLContainer.start();
        return postgreSQLContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }
}
