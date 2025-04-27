package greencity.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.fasterxml.jackson.databind.SerializationFeature.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Jackson JSON processor. This class configures the
 * Jackson `ObjectMapper` bean with custom settings to handle JSON serialization
 * and deserialization in a Spring Boot application.
 */
@Configuration
@JsonInclude(NON_EMPTY)
public class JacksonConfig {
    /**
     * Creates and configures an {@link ObjectMapper} bean. The {@link ObjectMapper}
     * is configured with the following settings:
     * <ul>
     * <li>Registers the {@link JavaTimeModule} to handle Java 8 date and time types
     * (such as {@link java.time.LocalDate}, {@link java.time.LocalDateTime}).</li>
     * <li>Includes only non-null properties in the JSON output (null values will be
     * excluded).</li>
     * <li>Disables writing dates as timestamps (dates will be serialized in the
     * ISO-8601 format).</li>
     * <li>Ignores unknown properties during deserialization (additional properties
     * in JSON will not cause errors).</li>
     * <li>Enables pretty printing of JSON output (with indentation for better
     * readability).</li>
     * </ul>
     * These settings are designed to improve the clarity, flexibility, and
     * compatibility of JSON handling in your Spring Boot application.
     *
     * @return a configured {@link ObjectMapper} instance that will be used
     *         throughout the application.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(INDENT_OUTPUT, true);
        return objectMapper;
    }
}
