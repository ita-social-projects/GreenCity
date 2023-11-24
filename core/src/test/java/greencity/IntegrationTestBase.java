package greencity;

import greencity.initializer.PostgreSQLInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@ContextConfiguration(initializers = {
    PostgreSQLInitializer.Initializer.class
})
@Transactional
public abstract class IntegrationTestBase {
    @BeforeAll
    static void init() {
        PostgreSQLInitializer.postgreSQLContainer.start();
    }
}
