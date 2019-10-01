package greencity.config;

import greencity.entity.User;
import greencity.service.UserService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@Slf4j
public class JPAAuditConfig {
    private final UserService userService;

    public JPAAuditConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public AuditorAware<User> auditorProvider() {
        return new AuditorAware<User>() {
            @Override
            public Optional<User> getCurrentAuditor() {
                return Optional.empty();
            }
        };
    }
}
