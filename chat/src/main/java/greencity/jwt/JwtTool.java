package greencity.jwt;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class that provides methods for working with JWT.
 *
 */
@Slf4j
@Component
public class JwtTool {
    private final String accessTokenKey;

    /**
     * Constructor.
     */
    @Autowired
    public JwtTool(@Value("${tokenKey}") String accessTokenKey) {
        this.accessTokenKey = accessTokenKey;
    }

    /**
     * Returns access token key.
     *
     * @return accessTokenKey
     */
    public String getAccessTokenKey() {
        return accessTokenKey;
    }

    /**
     * Method that get token from {@link HttpServletRequest}.
     *
     * @param servletRequest this is your request.
     * @return {@link String} of token or null.
     */
    public String getTokenFromHttpServletRequest(HttpServletRequest servletRequest) {
        return Optional
            .ofNullable(servletRequest.getHeader("Authorization"))
            .filter(authHeader -> authHeader.startsWith("Bearer "))
            .map(token -> token.substring(7))
            .orElse(null);
    }
}
