package greencity.security.service.impl;

import static greencity.constant.RabbitConstants.SEND_USER_APPROVAL_ROUTING_KEY;
import static greencity.constant.RabbitConstants.VERIFY_EMAIL_ROUTING_KEY;
import greencity.dto.user.UserManagementDto;
import greencity.entity.OwnSecurity;
import greencity.entity.User;
import greencity.entity.VerifyEmail;
import greencity.enums.ROLE;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.*;
import greencity.message.UserApprovalMessage;
import greencity.message.VerifyEmailMessage;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.dto.ownsecurity.UpdatePasswordDto;
import greencity.security.jwt.JwtTool;
import greencity.security.repository.OwnSecurityRepo;
import greencity.security.repository.RestorePasswordEmailRepo;
import greencity.security.service.OwnSecurityService;
import greencity.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OwnSecurityServiceImplTest {

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
    private UpdatePasswordDto updatePasswordDto;
    private UserManagementDto userManagementDto;

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
        updatePasswordDto = UpdatePasswordDto.builder()
            .currentPassword("password")
            .password("newPassword")
            .confirmPassword("newPassword")
            .build();
        userManagementDto = UserManagementDto.builder()
            .name("Tester")
            .email("test@gmail.com")
            .role(ROLE.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .build();
    }

    @Test
    void signUp() {
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
    void signUpThrowsUserAlreadyRegisteredExceptionTest() {
        OwnSignUpDto ownSignUpDto = new OwnSignUpDto();
        User user = User.builder().verifyEmail(new VerifyEmail()).build();
        when(jwtTool.generateTokenKey()).thenReturn("New-token-key");
        when(userService.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);
        assertThrows(UserAlreadyRegisteredException.class,
            () -> ownSecurityService.signUp(ownSignUpDto));
    }

    @Test
    void signIn() {
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
    void signInNotVerifiedUser() {
        when(userService.findByEmail(anyString())).thenReturn(notVerifiedUser);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTool.createAccessToken(anyString(), any(ROLE.class))).thenReturn("new-access-token");
        when(jwtTool.createRefreshToken(any(User.class))).thenReturn("new-refresh-token");
        assertThrows(EmailNotVerified.class,
            () -> ownSecurityService.signIn(ownSignInDto));
    }

    @Test
    void signInNullUserTest() {
        when(userService.findByEmail("test@gmail.com")).thenReturn(null);
        assertThrows(WrongEmailException.class, () -> ownSecurityService.signIn(ownSignInDto));
    }

    @Test
    void signInWrongPasswordTest() {
        User user = User.builder()
            .email("test@gmail.com")
            .id(1L)
            .userStatus(UserStatus.ACTIVATED)
            .ownSecurity(null)
            .role(ROLE.ROLE_USER)
            .build();
        when(userService.findByEmail("test@gmail.com")).thenReturn(user);
        assertThrows(WrongPasswordException.class, () -> ownSecurityService.signIn(ownSignInDto));
    }

    @Test
    void signInDeactivatedUserTest() {
        User user = User.builder()
            .email("test@gmail.com")
            .id(1L)
            .userStatus(UserStatus.DEACTIVATED)
            .ownSecurity(OwnSecurity.builder().password("password").build())
            .role(ROLE.ROLE_USER)
            .build();
        when(userService.findByEmail("test@gmail.com")).thenReturn(user);
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        assertThrows(UserDeactivatedException.class, () -> ownSecurityService.signIn(ownSignInDto));
    }

    @Test
    void updateAccessTokensTest() {
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenReturn("test@gmail.com");
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        when(jwtTool.generateTokenKey()).thenReturn("token-key");
        when(jwtTool.isTokenValid("12345", verifiedUser.getRefreshTokenKey())).thenReturn(true);
        ownSecurityService.updateAccessTokens("12345");
        verify(jwtTool).createAccessToken(verifiedUser.getEmail(), verifiedUser.getRole());
        verify(jwtTool).createRefreshToken(verifiedUser);
    }

    @Test
    void updateAccessTokensBadRefreshTokenExceptionTest() {
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenThrow(ExpiredJwtException.class);
        assertThrows(BadRefreshTokenException.class,
            () -> ownSecurityService.updateAccessTokens("12345"));
    }

    @Test
    void updateAccessTokensBadRefreshTokenTest() {
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenReturn("test@gmail.com");
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        when(jwtTool.isTokenValid("12345", verifiedUser.getRefreshTokenKey())).thenReturn(false);
        assertThrows(BadRefreshTokenException.class,
            () -> ownSecurityService.updateAccessTokens("12345"));
    }

    @Test
    void updateAccessTokensBlockedUserTest() {
        verifiedUser.setUserStatus(UserStatus.BLOCKED);
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenReturn("test@gmail.com");
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        assertThrows(UserBlockedException.class,
            () -> ownSecurityService.updateAccessTokens("12345"));
    }

    @Test
    void updateAccessTokensDeactivatedUserTest() {
        verifiedUser.setUserStatus(UserStatus.DEACTIVATED);
        when(jwtTool.getEmailOutOfAccessToken("12345")).thenReturn("test@gmail.com");
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        assertThrows(UserDeactivatedException.class,
            () -> ownSecurityService.updateAccessTokens("12345"));
    }

    @Test
    void updatePasswordTest() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        ownSecurityService.updatePassword("password", 1L);
        verify(ownSecurityRepo).updatePassword("encodedPassword", 1L);
    }

    @Test
    void updateCurrentPasswordTest() {
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        when(passwordEncoder.matches(updatePasswordDto.getCurrentPassword(),
            verifiedUser.getOwnSecurity().getPassword())).thenReturn(true);
        when(passwordEncoder.encode(updatePasswordDto.getPassword())).thenReturn(updatePasswordDto.getPassword());
        ownSecurityService.updateCurrentPassword(updatePasswordDto, "test@gmail.com");
        verify(ownSecurityRepo).updatePassword(updatePasswordDto.getPassword(), 1L);
    }

    @Test
    void updateCurrentPasswordDifferentPasswordsTest() {
        updatePasswordDto.setPassword("123");
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        assertThrows(PasswordsDoNotMatchesException.class, ()
            -> ownSecurityService.updateCurrentPassword(updatePasswordDto, "test@gmail.com"));
    }

    @Test
    void updateCurrentPasswordPasswordsDoNotMatchTest() {
        when(userService.findByEmail("test@gmail.com")).thenReturn(verifiedUser);
        when(passwordEncoder.matches(updatePasswordDto.getCurrentPassword(),
            verifiedUser.getOwnSecurity().getPassword())).thenReturn(false);
        assertThrows(PasswordsDoNotMatchesException.class, ()
            -> ownSecurityService.updateCurrentPassword(updatePasswordDto, "test@gmail.com"));
    }

    @Test
    void managementRegisterUserTest() {
        UserApprovalMessage message =
            new UserApprovalMessage(null, "Tester", "test@gmail.com", "token-key");
        when(jwtTool.generateTokenKey()).thenReturn("token-key");
        ownSecurityService.managementRegisterUser(userManagementDto);
        verify(rabbitTemplate).convertAndSend(sendEmailTopic, SEND_USER_APPROVAL_ROUTING_KEY, message);
    }
}
