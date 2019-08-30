package greencity.security.service;

import greencity.entity.OwnSecurity;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.own_security.OwnSignInDto;
import greencity.security.dto.own_security.OwnSignUpDto;

/**
 * Provides the interface to manage {@link OwnSecurityService} entity.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public interface OwnSecurityService {

    /**
     * Method that allow you sign-up user.
     *
     * @param dto a value of {@link OwnSignUpDto}
     */
    void register(OwnSignUpDto dto);

    /**
     * Method that allow you to delete {@link OwnSecurity}.
     *
     * @param ownSecurity a value of {@link OwnSecurity}
     */
    void delete(OwnSecurity ownSecurity);

    /** Method that delete {@link greencity.entity.VerifyEmail} when user not submit email. */
    void deleteNotActiveEmailUsers();

    /**
     * Method that allow you sign-in user.
     *
     * @param dto a value of {@link OwnSignInDto}
     * @return {@link SuccessSignInDto}
     */
    SuccessSignInDto signIn(OwnSignInDto dto);

    /**
     * Method that update your access token by refresh token.
     *
     * @param refreshToken a value of {@link String}
     * @return {@link String} this is new access token
     */
    String updateAccessToken(String refreshToken);
}
