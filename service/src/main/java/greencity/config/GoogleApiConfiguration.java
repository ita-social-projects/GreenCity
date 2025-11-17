package greencity.config;

import com.google.maps.GeoApiContext;
import greencity.properties.GoogleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GoogleApiConfiguration {
    private final GoogleProperties googleProperties;

    /**
     * Method create ApiContext.
     *
     * @return {@link GeoApiContext}
     */
    @Bean
    GeoApiContext context() {
        return new GeoApiContext.Builder().apiKey(googleProperties.getGoogleApiKey()).build();
    }
}
