package greencity.security.service;

import greencity.security.dto.SuccessSignInDto;

/**
 * Provides the facebook social logic.
 *
 * @author Oleh Yurchuk
 * @version 1.0
 */

public interface FacebookSecurityService {
    /**
     * Method that generate facebook authorize url.
     *
     * @return {@link String} facebook auth url
     */
    String generateFacebookAuthorizeURL();

    /**
     * Method that allow you authenticate with google idToken.
     *
     * @param code {@link String} - facebook token.
     * @return {@link SuccessSignInDto} if token valid
     */
    SuccessSignInDto generateFacebookAccessToken(String code);
}
