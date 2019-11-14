package greencity.security.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.security.jwt.JwtTool;
import greencity.service.UserService;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Payload.class)
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
    private JwtTool tokenTool;
    @Mock
    private GoogleIdTokenVerifier verifier;
    @InjectMocks
    private GoogleSecurityServiceImpl googleSecurityService;

    /*@Test
    public void authenticate() throws Exception {
        String idToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImFmZGU4MGViMWVkZjlmM2JmNDQ4NmRkODc3YzM0YmE0NmFmYmJhMWYiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiODIwNjAwNTU5Nzc0LXJvZ2g0Mmlzbzk2NDRvaHNwcnM2ZzVncmI4Mjh2MjBuLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiODIwNjAwNTU5Nzc0LXJvZ2g0Mmlzbzk2NDRvaHNwcnM2ZzVncmI4Mjh2MjBuLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTAxMzI0NTYzNTk3MzE0MDM0NDI4IiwiZW1haWwiOiJhdmFzaGNoZW5vY2tAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJFUXoyQnM2amlhRHFNZ3BScU5aa2xBIiwibmFtZSI6IkFuZHJldyBWYXNoY2hlbm9rIiwicGljdHVyZSI6Imh0dHBzOi8vbGg0Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tNlZZRDltY3NKYk0vQUFBQUFBQUFBQUkvQUFBQUFBQUFCSzAvTlpTeGxBYkRZLUUvczk2LWMvcGhvdG8uanBnIiwiZ2l2ZW5fbmFtZSI6IkFuZHJldyIsImZhbWlseV9uYW1lIjoiVmFzaGNoZW5vayIsImxvY2FsZSI6ImVuIiwiaWF0IjoxNTYyMDAxMDE1LCJleHAiOjE1NjIwMDQ2MTUsImp0aSI6IjY3OTVjNjNhMDdhNjQ0ODVlMmM2MGE1NjIwZGQxYWVmNGNiMTMwY2YifQ.KVVUSRhtHPGkT0argWwY-OxzOZ2jmrHnXLk5hd2oVHvhbzpukDuwEy2VsDlJR2gbRtqc0uGOVPQ1KWgFgSOs7sPbG7kMaA2EKvxIK54F5yRi4ffdu4bv2Iv3fS6XB-kScG2C4yKSkmYK1SdptxI70hKVXGiNePAT5ahz8AJUQH-1aYDp9M0jkZRb1aFPqZA-3TU8OvulrP9dedWnSaY2cfItJTXmgfPhEInXvGDVz3Zlqiky41mD62Q5uwRlHuBo4zHuV86nHn9Q8LVmJb-DsZFWHUj2nmwXRTV9ox5gpb7vHWL4g8OwJtYQlkOqUREgkJTOJsxD0nelMXM4FXb3Pw";

        Payload payload = mock(Payload.class);
        when(payload.getEmail()).thenReturn(user.getEmail());
        when(payload.get(anyString())).thenReturn(user.getLastName());
        when(payload.get(anyString())).thenReturn(user.getFirstName());

        whenNew(GoogleIdToken.Payload.class).withAnyArguments().thenReturn(payload);


        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        SuccessSignInDto authenticate = googleSecurityService.authenticate(idToken);
        assertEquals(authenticate.getFirstName(), user.getFirstName());
    }*/

    @Test(expected = IllegalArgumentException.class)
    public void authenticate() {
        String idToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImFmZGU4MGViMWVkZjlmM2JmNDQ4NmRkODc3YzM0YmE0NmFmYmJhMWYiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiODIwNjAwNTU5Nzc0LXJvZ2g0Mmlzbzk2NDRvaHNwcnM2ZzVncmI4Mjh2MjBuLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiODIwNjAwNTU5Nzc0LXJvZ2g0Mmlzbzk2NDRvaHNwcnM2ZzVncmI4Mjh2MjBuLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTAxMzI0NTYzNTk3MzE0MDM0NDI4IiwiZW1haWwiOiJhdmFzaGNoZW5vY2tAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJFUXoyQnM2amlhRHFNZ3BScU5aa2xBIiwibmFtZSI6IkFuZHJldyBWYXNoY2hlbm9rIiwicGljdHVyZSI6Imh0dHBzOi8vbGg0Lmdvb2dsZXVzZXJjb250ZW50LmNvbS8tNlZZRDltY3NKYk0vQUFBQUFBQUFBQUkvQUFBQUFBQUFCSzAvTlpTeGxBYkRZLUUvczk2LWMvcGhvdG8uanBnIiwiZ2l2ZW5fbmFtZSI6IkFuZHJldyIsImZhbWlseV9uYW1lIjoiVmFzaGNoZW5vayIsImxvY2FsZSI6ImVuIiwiaWF0IjoxNTYyMDAxMDE1LCJleHAiOjE1NjIwMDQ2MTUsImp0aSI6IjY3OTVjNjNhMDdhNjQ0ODVlMmM2MGE1NjIwZGQxYWVmNGNiMTMwY2YifQ.KVVUSRhtHPGkT0argWwY-OxzOZ2jmrHnXLk5hd2oVHvhbzpukDuwEy2VsDlJR2gbRtqc0uGOVPQ1KWgFgSOs7sPbG7kMaA2EKvxIK54F5yRi4ffdu4bv2Iv3fS6XB-kScG2C4yKSkmYK1SdptxI70hKVXGiNePAT5ahz8AJUQH-1aYDp9M0jkZRb1aFPqZA-3TU8OvulrP9dedWnSaY2cfItJTXmgfPhEInXvGDVz3Zlqiky41mD62Q5uwRlHuBo4zHuV86nHn9Q8LVmJb-DsZFWHUj2nmwXRTV9ox5gpb7vHWL4g8OwJtYQlkOqUREgkJTOJsxD0nelMXM4FXb3Pw";
        googleSecurityService.authenticate(idToken);
    }
}