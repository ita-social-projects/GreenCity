package greencity.security.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.BadEmailOrPasswordException;
import greencity.exception.UserDeactivatedException;
import greencity.exception.UserUnverifiedException;
import greencity.service.UserService;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class JwtAuthenticationProviderTest {

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private JwtAuthenticationProvider provider;

    @Test
    public void authenticate() {
        User user = User.builder()
            .email("email")
            .ownSecurity(OwnSecurity.builder()
                .password("password").build())
            .role(ROLE.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .build();

        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        Authentication authenticate = provider.authenticate(new UsernamePasswordAuthenticationToken(
            user.getEmail(),
            user.getOwnSecurity().getPassword()));
        assertEquals(authenticate.getPrincipal(), user.getEmail());
    }

    @Test(expected = BadEmailOrPasswordException.class)
    public void authenticateWithBadCred() {
        User user = User.builder()
            .email("email")
            .ownSecurity(OwnSecurity.builder()
                .password("password").build())
            .role(ROLE.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .build();

        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        provider.authenticate(new UsernamePasswordAuthenticationToken(
            user.getEmail(),
            user.getOwnSecurity().getPassword()));
    }

    @Test(expected = UserUnverifiedException.class)
    public void authenticateToNotVerifiedAccount() {
        User user = User.builder()
            .email("email")
            .ownSecurity(OwnSecurity.builder()
                .password("password").build())
            .role(ROLE.ROLE_USER)
            .verifyEmail(new VerifyEmail())
            .userStatus(UserStatus.ACTIVATED)
            .build();

        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        provider.authenticate(new UsernamePasswordAuthenticationToken(
            user.getEmail(),
            user.getOwnSecurity().getPassword()));
    }


    @Test(expected = UserDeactivatedException.class)
    public void authenticateToDeactivatedAccount() {
        User user = User.builder()
            .email("email")
            .ownSecurity(OwnSecurity.builder()
                .password("password").build())
            .role(ROLE.ROLE_USER)
            .userStatus(UserStatus.DEACTIVATED)
            .build();

        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        provider.authenticate(new UsernamePasswordAuthenticationToken(
            user.getEmail(),
            user.getOwnSecurity().getPassword()));
    }

    @Test
    public void supports() {
        assertTrue(provider.supports(UsernamePasswordAuthenticationToken.class));
    }
}