package greencity.config;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.FunctionalityNotAvailableException;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy
public class DotenvConfig {

    @Bean
    public Dotenv fallbackDotenv() {
        return Dotenv.configure().ignoreIfMissing().load();
    }

    @Bean
    @ConditionalOnMissingBean(Dotenv.class)
    Dotenv dotenv() {
        try {
            return Dotenv.configure()
                    .filename(AppConstant.DOTENV_FILENAME)
                    .load();
        } catch (DotenvException ignored) {
            throw new FunctionalityNotAvailableException(ErrorMessage.FUNCTIONALITY_NOT_AVAILABLE);
        }
    }
}