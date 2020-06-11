package greencity.security.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import static greencity.constant.AppConstant.GOOGLE_USERNAME;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.security.dto.SuccessSignInDto;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

public class GoogleSecurityServiceImplTest {

    @Mock
    GoogleIdTokenVerifier googleIdTokenVerifier;

    @Mock
    JwtTool jwtTool;

    @Mock
    GoogleIdToken googleIdToken;

    @Mock
    UserService userService;

    @Mock
    GoogleIdToken.Payload googlePayload;

    @Mock
    ApplicationEventPublisher appEventPublisher;

    @InjectMocks
    private GoogleSecurityServiceImpl googleSecurityService;

    private String tokenId = "testToken";
    private User user;
    private SuccessSignInDto successSignInDto;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.googleSecurityService = new GoogleSecurityServiceImpl(userService, jwtTool, googleIdTokenVerifier,
            appEventPublisher);
    }

    @Test
    public void authenticate() throws GeneralSecurityException, IOException {

        user = User.builder()
            .id(1L)
            .email("test@gmail.com")
            .name("TestUserName")
            .role(ROLE.ROLE_USER)
            .dateOfRegistration(LocalDateTime.now())
            .lastVisit(LocalDateTime.now())
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .refreshTokenKey("testToken")
            .build();

        Object str = new Object();

        when(googleIdTokenVerifier.verify(anyString())).thenReturn(googleIdToken);
        when(googleIdTokenVerifier.verify(anyString()).getPayload()).thenReturn(googlePayload);
        when(googleIdTokenVerifier.verify(anyString()).getPayload().getEmail()).thenReturn("test@gmail.com");
        when(googleIdTokenVerifier.verify(anyString()).getPayload().get(GOOGLE_USERNAME)).thenReturn(str);

        googleSecurityService.authenticate(tokenId);

        successSignInDto = new SuccessSignInDto(user.getId(),
            "testToken", "testToken", user.getName(), false);
        assertEquals(successSignInDto, googleSecurityService.authenticate(tokenId));

    }

    @Test(expected = IllegalArgumentException.class)
    public void authenticateTokenNotVerified() {
        googleSecurityService.authenticate("wrongToken");
    }


}
