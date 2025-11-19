package greencity.properties;

import greencity.constant.ErrorMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleProperties {
    private final Environment environment;

    @PostConstruct
    public void validateProperties() {
        getGoogleApiKey();
        getGoogleMapApiKey();
        log.info("All google properties validated successfully.");
    }

    public String getGoogleApiKey() {
        String googleApiKey = environment.getProperty("greencity.authorization.googleApiKey");
        if (!StringUtils.hasText(googleApiKey)) {
            log.error(ErrorMessage.GOOGLE_API_KEY_NOT_SET);
            throw new IllegalStateException(ErrorMessage.GOOGLE_API_KEY_NOT_SET);
        }
        return googleApiKey;
    }

    public String getGoogleMapApiKey() {
        String googleMapsApiKey = environment.getProperty("google.maps.api.key");
        if (!StringUtils.hasText(googleMapsApiKey)) {
            log.error(ErrorMessage.GOOGLE_MAP_API_KEY_NOT_SET);
            throw new IllegalStateException(ErrorMessage.GOOGLE_MAP_API_KEY_NOT_SET);
        }
        return googleMapsApiKey;
    }
}
