package greencity.security.service;

import greencity.entity.OwnSecurity;
import greencity.security.dto.AccessRefreshTokensDto;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.dto.ownsecurity.UpdatePasswordDto;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Provides the interface to manage {@link OwnSecurityService} entity.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 1.0
 */
public interface OwnSecurityService {
    /**
     * Method that allow you sign-up user.
     *
     * @param dto a value of {@link OwnSignUpDto}
     * @author Yurii Koval
     */
    void signUp(OwnSignUpDto dto);

    /**
     * Method that allow you to delete {@link OwnSecurity}.
     *
     * @param userOwnSecurity a value of {@link OwnSecurity}
     */
    void delete(OwnSecurity userOwnSecurity);

    /**
     * Deletes {@link greencity.entity.VerifyEmail} when user did not submit email.
     * @author Yurii Koval
     */
    @Scheduled(fixedRate = 86400000)
    void deleteAllUsersThatDidNotVerifyEmail();

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
     * @return {@link AccessRefreshTokensDto} this is DTO with new access token
     */
    AccessRefreshTokensDto updateAccessTokens(String refreshToken);

    /**
     * Method for updating password.
     *
     * @param pass {@link String}
     * @param id   {@link Long}
     */
    void updatePassword(String pass, Long id);

    /**
     * Method for updating current password.
     *
     * @param updatePasswordDto {@link UpdatePasswordDto}
     * @param email        {@link String} - user email.
     * @author Dmytro Dovhal
     */
    void updateCurrentPassword(UpdatePasswordDto updatePasswordDto, String email);
}
