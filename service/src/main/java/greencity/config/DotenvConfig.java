package greencity.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@Lazy
public class DotenvConfig {
    @Bean
    Dotenv dotenv() {
        return Dotenv.configure().load();
    }
}
//TODO: maybe remove