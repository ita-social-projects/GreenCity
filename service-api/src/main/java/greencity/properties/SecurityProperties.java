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
public class SecurityProperties {
    private final Environment environment;

    @PostConstruct
    public void validateProperties() {
        getJwtAccessTokenExpiration();
        getAccessTokenKey();
        log.info("All security properties validated successfully.");
    }

    public String getAccessTokenKey() {
        String accessTokenKey = environment.getProperty("security.jwt.secret-key");
        if (!StringUtils.hasText(accessTokenKey)) {
            log.error(ErrorMessage.ACCESS_TOKEN_NOT_SET);
            throw new IllegalStateException(ErrorMessage.ACCESS_TOKEN_NOT_SET);
        }
        return accessTokenKey;
    }

    public Integer getJwtAccessTokenExpiration() {
        Integer accesssTokenExpiration =
            environment.getProperty("security.jwt.access-token.expiration-minutes", Integer.class);
        if (accesssTokenExpiration == null) {
            log.error(ErrorMessage.JWT_ACCESS_TOKEN_EXPIRATION_NOT_SET);
            throw new IllegalStateException(ErrorMessage.JWT_ACCESS_TOKEN_EXPIRATION_NOT_SET);
        }
        return accesssTokenExpiration;
    }
}
