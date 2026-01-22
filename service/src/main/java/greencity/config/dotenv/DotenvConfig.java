package greencity.config.dotenv;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.FunctionalityNotAvailableException;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

@Configuration
@Lazy
public class DotenvConfig {
    @Bean
    @ConditionalOnMissingBean(Dotenv.class)
    public Dotenv fallbackDotenv() {
        return Dotenv.configure().ignoreIfMissing().load();
    }

    @Bean
    @ConditionalOnExpression("#{T(greencity.config.dotenv.DotEnvConditionChecker).isEnabled()}")
    @Primary
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