package greencity.service;

import greencity.dto.userownsecurity.AccessTokenDto;
import greencity.dto.userownsecurity.UserRegisterDto;
import greencity.dto.userownsecurity.UserSignInDto;
import greencity.dto.userownsecurity.UserSuccessSignInDto;
import greencity.entity.UserOwnSecurity;

/**
 * Provides the interface to manage {@link UserOwnSecurityService} entity.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public interface UserOwnSecurityService {
    /**
     * Method that allow you sign-up user.
     *
     * @param dto a value of {@link UserRegisterDto}
     */
    void signUp(UserRegisterDto dto);

    /**
     * Method that allow you to delete {@link UserOwnSecurity}.
     *
     * @param userOwnSecurity a value of {@link UserOwnSecurity}
     */
    void delete(UserOwnSecurity userOwnSecurity);

    /**
     * Method that delete {@link greencity.entity.VerifyEmail} when user not submit email.
     */
    void deleteNotActiveEmailUsers();

    /**
     * Method that allow you sign-in user.
     *
     * @param dto a value of {@link UserSignInDto}
     * @return {@link UserSuccessSignInDto}
     */
    UserSuccessSignInDto signIn(UserSignInDto dto);

    /**
     * Method that update your access token by refresh token.
     *
     * @param refreshToken a value of {@link String}
     * @return {@link AccessTokenDto} this is DTO with new access token
     */
    AccessTokenDto updateAccessToken(String refreshToken);
}
