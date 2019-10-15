package greencity.config;

import greencity.entity.User;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration JPA audit .
 *
 * @author Marian Milian
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@Slf4j
public class JPAAuditConfig {
    /**
     * Bean {@link AuditorAware} that uses in coding password.
     */
    @Bean
    public AuditorAware<User> auditorProvider() {
        return Optional::empty;
    }
}
