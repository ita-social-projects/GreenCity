package greencity.security.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.security.jwt.JwtTokenTool;
import greencity.service.UserService;
import java.time.LocalDateTime;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class GoogleSecurityServiceImplTest {

    private User user =
        User.builder()
            .email("Nazar.stasyuk@gmail.com")
            .firstName("Nazar")
            .lastName("Stasyuk")
            .role(ROLE.ROLE_USER)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();

    @Mock
    private UserService userService;
    @Mock
    private GoogleIdTokenVerifier verifier;
    @Mock
    private JwtTokenTool tokenTool;
    @Mock
    private Payload payload;
    @InjectMocks
    private GoogleSecurityServiceImpl googleSecurityService;

   /* @Test
    public void authenticate() throws GeneralSecurityException, IOException {
        GoogleIdToken googleIdToken = mock(GoogleIdToken.class);
        when(verifier.verify(anyString())).thenReturn(googleIdToken);


        when(payload.getEmail()).thenReturn(user.getEmail());
        when(payload.get(anyString())).thenReturn(user.getLastName());
        when(payload.get(anyString())).thenReturn(user.getFirstName());

        when(googleIdToken.getPayload()).thenReturn(payload);


        when(userService.findByEmail(anyString())).thenReturn(user);
        SuccessSignInDto authenticate = googleSecurityService.authenticate("");
        assertEquals(authenticate.getFirstName(), user.getFirstName());
    }*/
}