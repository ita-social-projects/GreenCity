package greencity.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.exceptions.EmailNotVerified;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.events.SignInEvent;
import greencity.security.events.SignUpEvent;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.impl.OwnSecurityServiceImpl;
import greencity.service.UserService;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

public class OwnSecurityServiceImplTest {

    @Mock
    OwnSecurityRepo ownSecurityRepo;

    @Mock
    UserService userService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtTool jwtTool;

    @Mock
    ApplicationEventPublisher appEventPublisher;

    @Mock
    RabbitTemplate rabbitTemplate;

    private OwnSecurityService ownSecurityService;

    private User verifiedUser;
    private OwnSignInDto ownSignInDto;
    private User notVerifiedUser;

    @Before
    public void init() {
        initMocks(this);
        ownSecurityService = new OwnSecurityServiceImpl(ownSecurityRepo, userService, passwordEncoder,
            jwtTool, 1, appEventPublisher, rabbitTemplate);

        verifiedUser = User.builder()
            .email("test@gmail.com")
            .id(1L)
            .userStatus(UserStatus.ACTIVATED)
            .ownSecurity(OwnSecurity.builder().password("password").build())
            .role(ROLE.ROLE_USER)
            .build();
        ownSignInDto = OwnSignInDto.builder()
            .email("test@gmail.com")
            .password("password")
            .build();
        notVerifiedUser = User.builder()
            .email("test@gmail.com")
            .id(1L)
            .userStatus(UserStatus.ACTIVATED)
            .verifyEmail(new VerifyEmail())
            .ownSecurity(OwnSecurity.builder().password("password").build())
            .role(ROLE.ROLE_USER)
            .build();
    }

    @Test
    public void signUp() {
        User user = User.builder().verifyEmail(new VerifyEmail()).build();
        when(userService.save(any(User.class))).thenReturn(user);
        when(jwtTool.generateTokenKey()).thenReturn("New-token-key");

        ownSecurityService.signUp(new OwnSignUpDto());

        verify(userService, times(1)).save(any(User.class));
        verify(appEventPublisher, times(1)).publishEvent(any(SignUpEvent.class));
        verify(jwtTool, times(2)).generateTokenKey();
    }

    @Test
    public void signIn() {
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(verifiedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        doNothing().when(appEventPublisher).publishEvent(any(SignUpEvent.class));
        when(jwtTool.createAccessToken(anyString(), any(ROLE.class))).thenReturn("new-access-token");
        when(jwtTool.createRefreshToken(any(User.class))).thenReturn("new-refresh-token");

        ownSecurityService.signIn(ownSignInDto);

        verify(appEventPublisher, times(1)).publishEvent(any(SignInEvent.class));
        verify(userService, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(jwtTool, times(1)).createAccessToken(anyString(), any(ROLE.class));
        verify(jwtTool, times(1)).createRefreshToken(any(User.class));
    }

    @Test(expected = EmailNotVerified.class)
    public void signInNotVerifiedUser() {
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(notVerifiedUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        doNothing().when(appEventPublisher).publishEvent(any(SignUpEvent.class));
        when(jwtTool.createAccessToken(anyString(), any(ROLE.class))).thenReturn("new-access-token");
        when(jwtTool.createRefreshToken(any(User.class))).thenReturn("new-refresh-token");

        ownSecurityService.signIn(ownSignInDto);
    }
}