package greencity.security.service;

import greencity.entity.OwnSecurity;
import greencity.security.dto.AccessTokenDto;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;

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
    void signUp(OwnSignUpDto dto);

    /**
     * Method that allow you to delete {@link OwnSecurity}.
     *
     * @param userOwnSecurity a value of {@link OwnSecurity}
     */
    void delete(OwnSecurity userOwnSecurity);

    /**
     * Method that delete {@link greencity.entity.VerifyEmail} when user not submit email.
     */
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
     * @return {@link AccessTokenDto} this is DTO with new access token
     */
    AccessTokenDto updateAccessToken(String refreshToken);
}