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
import org.springframework.stereotype.Service;

/**
 * Class that provide authentication logic.
 *
 * @author Nazar Stasyuk and Yurii Koval
 * @version 1.1
 */
@Service
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
        return authentication;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
