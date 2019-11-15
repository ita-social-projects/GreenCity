package greencity.security.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.entity.enums.ROLE;
import greencity.exception.BadIdException;
import greencity.exception.BadRefreshTokenException;
import greencity.exception.UserAlreadyRegisteredException;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.service.impl.UserServiceImpl;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class OwnSecurityServiceImplTest {

    private User user =
        User.builder()
            .email("Nazar.stasyuk@gmail.com")
            .firstName("Nazar")
            .lastName("Stasyuk")
            .role(ROLE.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    private OwnSignUpDto dto =
        OwnSignUpDto.builder()
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .password("123123123")
            .build();
    @InjectMocks
    private OwnSecurityServiceImpl service;
    @Mock
    private OwnSecurityRepo repo;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private VerifyEmailServiceImpl verifyEmailService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager manager;
    @Mock
    private JwtTool tokenTool;

    @Test
    public void signUp() {
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userService.save(any(User.class))).thenReturn(user);
        when(repo.save(any())).thenReturn(new OwnSecurity());
        doNothing().when(verifyEmailService).save(any());
        service.signUp(dto);
        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).save(any());
        verify(repo, times(1)).save(any());
    }

    @Test(expected = UserAlreadyRegisteredException.class)
    public void signUpSameUser() {
        User user =
            User.builder()
                .email("Nazar.stasyuk@gmail.com")
                .firstName("Nazar")
                .lastName("Stasyuk")
                .role(ROLE.ROLE_USER)
                .lastVisit(LocalDateTime.now())
                .ownSecurity(new OwnSecurity())
                .dateOfRegistration(LocalDateTime.now())
                .build();
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        service.signUp(dto);
    }

    @Test
    public void delete() {
        doNothing().when(repo).delete(any(OwnSecurity.class));
        when(repo.existsById(anyLong())).thenReturn(true);
        service.delete(OwnSecurity.builder().id(1L).build());
        verify(repo, times(1)).delete(any(OwnSecurity.class));
    }

    @Test(expected = BadIdException.class)
    public void deleteNoExist() {
        when(repo.existsById(anyLong())).thenReturn(false);
        service.delete(OwnSecurity.builder().id(1L).build());
    }

    @Test
    public void signIn() {
        when(manager.authenticate(any()))
            .thenReturn(
                new UsernamePasswordAuthenticationToken(
                    "", "", Collections.singleton(new SimpleGrantedAuthority("user"))));
        when(userService.findByEmail(anyString()))
            .thenReturn(Optional.of(User.builder().email("").role(ROLE.ROLE_USER).build()));
        service.signIn(OwnSignInDto.builder().email("").password("").build());
        verify(manager, times(1)).authenticate(any());
        verify(userService, times(1)).findByEmail(any());
        verify(tokenTool, times(1)).createAccessToken(any(), any());
        verify(tokenTool, times(1)).createRefreshToken(any());
    }

    @Test(expected = BadRefreshTokenException.class)
    public void updateAccessToken() {
        when(tokenTool.isTokenValid(anyString())).thenReturn(false);
        service.updateAccessToken("");

    }
}
