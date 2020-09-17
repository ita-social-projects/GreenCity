package greencity.service.impl;

import static greencity.constant.RabbitConstants.VERIFY_EMAIL_ROUTING_KEY;
import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.exceptions.EmailNotVerified;
import greencity.message.VerifyEmailMessage;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.impl.OwnSecurityServiceImpl;
import greencity.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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
    RabbitTemplate rabbitTemplate;

    @Mock
    RestorePasswordEmailRepo restorePasswordEmailRepo;

    private OwnSecurityService ownSecurityService;

    private User verifiedUser;
    private OwnSignInDto ownSignInDto;
    private User notVerifiedUser;

    @Value("${messaging.rabbit.email.topic}")
    private String sendEmailTopic;
    @Value("${defaultProfilePicture}")
    private String defaultProfilePicture;

    @BeforeEach
    public void init() {
        initMocks(this);
        ownSecurityService = new OwnSecurityServiceImpl(ownSecurityRepo, userService, passwordEncoder,
            jwtTool, 1, rabbitTemplate, defaultProfilePicture, restorePasswordEmailRepo);

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
        verify(rabbitTemplate, times(1)).convertAndSend(
            refEq(sendEmailTopic),
            refEq(VERIFY_EMAIL_ROUTING_KEY),
            refEq(new VerifyEmailMessage(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getVerifyEmail().getToken()
                )
            )
        );
        verify(jwtTool, times(2)).generateTokenKey();
    }

    @Test
    public void signIn() {
        when(userService.findByEmail(anyString())).thenReturn(verifiedUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTool.createAccessToken(anyString(), any(ROLE.class))).thenReturn("new-access-token");
        when(jwtTool.createRefreshToken(any(User.class))).thenReturn("new-refresh-token");

        ownSecurityService.signIn(ownSignInDto);

        verify(userService, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(jwtTool, times(1)).createAccessToken(anyString(), any(ROLE.class));
        verify(jwtTool, times(1)).createRefreshToken(any(User.class));
    }

    @Test
    public void signInNotVerifiedUser() {
        when(userService.findByEmail(anyString())).thenReturn(notVerifiedUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTool.createAccessToken(anyString(), any(ROLE.class))).thenReturn("new-access-token");
        when(jwtTool.createRefreshToken(any(User.class))).thenReturn("new-refresh-token");
        Assertions
            .assertThrows(EmailNotVerified.class,
                () -> ownSecurityService.signIn(ownSignInDto));

    }
}