package greencity.config;

import com.deepl.api.DeepLClient;
import com.deepl.api.DeepLClientOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeepLConfig {
    @Value("${deepl.api.key}")
    private String apiKey;

    @Bean
    public DeepLClient getDeepLClient() {
        DeepLClientOptions options = new DeepLClientOptions();
        options.setSendPlatformInfo(false);
        options.setMaxRetries(3);
        return new DeepLClient(apiKey, options);
    }
}
