package greencity.security.jwt;

import static greencity.constant.ErrorMessage.*;

import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.enums.UserStatus;
import greencity.exception.BadEmailOrPasswordException;
import greencity.exception.UserDeactivatedException;
import greencity.exception.UserUnverifiedException;
import greencity.service.UserService;
import java.util.Collections;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Class that provide authentication logic.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    /**
     * Constructor.
     *
     * @param userService     {@link UserService} - service for user
     * @param passwordEncoder {@link PasswordEncoder} - encoder for password
     */
    public JwtAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Method that provide authentication.
     *
     * @param authentication {@link Authentication} - authentication.
     * @return {@link Authentication} if user successfully authenticated.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        User byEmail = userService.findByEmail(email).orElseThrow(
            () -> new BadEmailOrPasswordException(BAD_EMAIL_OR_PASSWORD)
        );

        OwnSecurity ownSecurity = byEmail.getOwnSecurity();

        if (ownSecurity == null) {
            throw new BadEmailOrPasswordException(BAD_EMAIL_OR_PASSWORD);
        }

        if (!passwordEncoder.matches(password, ownSecurity.getPassword())) {
            throw new BadEmailOrPasswordException(BAD_EMAIL_OR_PASSWORD);
        }

        if (byEmail.getVerifyEmail() != null) {
            throw new UserUnverifiedException(USER_NOT_VERIFIED);
        }

        if (byEmail.getUserStatus() == UserStatus.DEACTIVATED) {
            throw new UserDeactivatedException(USER_DEACTIVATED);
        }

        return new UsernamePasswordAuthenticationToken(
            email,
            "",
            Collections.singleton(new SimpleGrantedAuthority(byEmail.getRole().name()))
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
