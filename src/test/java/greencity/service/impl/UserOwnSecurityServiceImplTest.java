package greencity.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;

import greencity.dto.user_own_security.UserRegisterDto;
import greencity.dto.user_own_security.UserSignInDto;
import greencity.entity.User;
import greencity.entity.UserOwnSecurity;
import greencity.entity.VerifyEmail;
import greencity.entity.enums.ROLE;
import greencity.exception.BadEmailException;
import greencity.exception.BadIdException;
import greencity.exception.BadRefreshTokenException;
import greencity.repository.UserOwnSecurityRepo;
import greencity.security.JwtTokenTool;
import greencity.security.JwtUser;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class UserOwnSecurityServiceImplTest {

    User user =
            User.builder()
                    .email("Nazar.stasyuk@gmail.com")
                    .firstName("Nazar")
                    .lastName("Stasyuk")
                    .role(ROLE.ROLE_USER)
                    .lastVisit(LocalDateTime.now())
                    .dateOfRegistration(LocalDateTime.now())
                    .build();
    UserRegisterDto dto =
            UserRegisterDto.builder()
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .password("123123123")
                    .build();
    @InjectMocks private UserOwnSecurityServiceImpl service;
    @Mock private UserOwnSecurityRepo repo;
    @Mock private UserServiceImpl userService;
    @Mock private VerifyEmailServiceImpl verifyEmailService;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager manager;
    @Mock private JwtTokenTool tokenTool;

    @Test
    public void signUp() {
        when(userService.findByEmail(anyString())).thenReturn(null);
        when(userService.save(any(User.class))).thenReturn(user);
        when(repo.save(any())).thenReturn(new UserOwnSecurity());
        doNothing().when(verifyEmailService).save(any());
        service.signUp(dto);
        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).save(any());
        verify(repo, times(1)).save(any());
    }

    @Test
    public void signUpUserThatWasRegisteredByAnotherMethod() {
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(repo.save(any())).thenReturn(new UserOwnSecurity());
        doNothing().when(verifyEmailService).save(any());

        service.signUp(dto);
        verify(userService, times(1)).findByEmail(anyString());
        verify(repo, times(1)).save(any());
    }

    @Test(expected = BadEmailException.class)
    public void signUpSameUser() {
        User user =
                User.builder()
                        .email("Nazar.stasyuk@gmail.com")
                        .firstName("Nazar")
                        .lastName("Stasyuk")
                        .role(ROLE.ROLE_USER)
                        .lastVisit(LocalDateTime.now())
                        .userOwnSecurity(new UserOwnSecurity())
                        .dateOfRegistration(LocalDateTime.now())
                        .build();
        when(userService.findByEmail(anyString())).thenReturn(user);
        service.signUp(dto);
    }

    @Test
    public void delete() {
        doNothing().when(repo).delete(any(UserOwnSecurity.class));
        when(repo.existsById(anyLong())).thenReturn(true);
        service.delete(UserOwnSecurity.builder().id(1L).build());
        verify(repo, times(1)).delete(any(UserOwnSecurity.class));
    }

    @Test(expected = BadIdException.class)
    public void deleteNoExist() {
        when(repo.existsById(anyLong())).thenReturn(false);
        service.delete(UserOwnSecurity.builder().id(1L).build());
    }

    @Test
    public void deleteNotActiveEmailUsers() {
        when(verifyEmailService.findAll())
                .thenReturn(
                        Collections.singletonList(
                                VerifyEmail.builder()
                                        .id(1L)
                                        .expiryDate(LocalDateTime.now().plusHours(2))
                                        .token("some token")
                                        .user(
                                                User.builder()
                                                        .id(2L)
                                                        .userOwnSecurity(new UserOwnSecurity())
                                                        .build())
                                        .build()));
        when(verifyEmailService.isDateValidate(any())).thenReturn(true);
        when(repo.existsById(any())).thenReturn(true);
        doNothing().when(repo).delete(any());
        doNothing().when(verifyEmailService).delete(any(VerifyEmail.class));
        doNothing().when(userService).deleteById(anyLong());
        service.deleteNotActiveEmailUsers();
        verify(verifyEmailService, times(1)).findAll();
        verify(verifyEmailService, times(1)).delete(any());
        verify(userService, times(1)).deleteById(any());
    }

    @Test
    public void signIn() {
        when(manager.authenticate(any()))
                .thenReturn(
                        new UsernamePasswordAuthenticationToken(
                                new JwtUser(new User()), "", Collections.singleton(new SimpleGrantedAuthority("user"))));
        when(userService.findByEmail(anyString()))
                .thenReturn(User.builder().email("").role(ROLE.ROLE_USER).build());
        service.signIn(UserSignInDto.builder().email("").password("").build());
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
