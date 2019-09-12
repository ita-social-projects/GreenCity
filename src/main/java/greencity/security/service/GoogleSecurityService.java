package greencity.security.service;

import greencity.security.dto.SuccessSignInDto;

/**
 * Provides the google social logic.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */

public interface GoogleSecurityService {
    /**
     * Method that allow you authenticate with google idToken.
     *
     * @param idToken {@link String} - google id token.
     */
    SuccessSignInDto authenticate(String idToken);
}
