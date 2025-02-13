package greencity.config;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.FunctionalityNotAvailableException;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy
public class DotenvConfig {
    private static final String DOTENV_FILENAME = "secretKeys.env";

    @Bean
    Dotenv dotenv() {
        try {
            return Dotenv.configure()
                .filename(DOTENV_FILENAME)
                .load();
        } catch (DotenvException ex) {
            throw new FunctionalityNotAvailableException(ErrorMessage.FUNCTIONALITY_NOT_AVAILABLE);
        }
    }
}