package greencity.security.controller;

import greencity.ModelUtils;
import greencity.security.dto.ownsecurity.OwnSignInDto;
import greencity.security.dto.ownsecurity.OwnSignUpDto;
import greencity.security.dto.ownsecurity.UpdatePasswordDto;
import greencity.security.service.OwnSecurityService;
import greencity.security.service.PasswordRecoveryService;
import greencity.security.service.VerifyEmailService;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OwnSecurityControllerTest {
    private static final String LINK = "/ownSecurity";
    private MockMvc mockMvc;

    @InjectMocks
    private OwnSecurityController ownSecurityController;

    @Mock
    private OwnSecurityService ownSecurityService;

    @Mock
    private VerifyEmailService verifyEmailService;

    @Mock
    private PasswordRecoveryService passwordRecoveryService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(ownSecurityController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void singUpTest() throws Exception {
        String content = "{\n" +
            "  \"email\": \"test@mail.com\",\n" +
            "  \"name\": \"string\",\n" +
            "  \"password\": \"Qwerty123=\"\n" +
            "}";

        mockMvc.perform(post(LINK + "/signUp")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        OwnSignUpDto dto = ModelUtils.getObjectMapper().readValue(content, OwnSignUpDto.class);
        verify(ownSecurityService).signUp(dto);
    }

    @Test
    void signInTest() throws Exception {
        String content = "{\n" +
            "  \"email\": \"test@mail.com\",\n" +
            "  \"password\": \"string\"\n" +
            "}";

        mockMvc.perform(post(LINK + "/signIn")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        OwnSignInDto dto = ModelUtils.getObjectMapper().readValue(content, OwnSignInDto.class);
        verify(ownSecurityService).signIn(dto);
    }

    @Test
    void verifyEmailTest() throws Exception {
        mockMvc.perform(get(LINK + "/verifyEmail")
            .param("token", "12345")
            .param("user_id", String.valueOf(1L)))
            .andExpect(status().isOk());

        verify(verifyEmailService).verifyByToken(1L, "12345");
    }

    @Test
    void updateAccessTokenTest() throws Exception {
        mockMvc.perform(get(LINK + "/updateAccessToken")
            .param("refreshToken", "12345"))
            .andExpect(status().isOk());

        verify(ownSecurityService).updateAccessTokens("12345");
    }

    @Test
    void restoreTest() throws Exception {
        mockMvc.perform(get(LINK + "/restorePassword")
            .param("email", "test@mail.com"))
            .andExpect(status().isOk());

        verify(passwordRecoveryService).sendPasswordRecoveryEmailTo("test@mail.com");
    }

    @Test
    void changePasswordTest() throws Exception {
        String content = "{\n" +
            "  \"confirmPassword\": \"Qwerty123=\",\n" +
            "  \"password\": \"Qwerty123=\",\n" +
            "  \"token\": \"12345\"\n" +
            "}";

        mockMvc.perform(post(LINK + "/changePassword")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        verify(passwordRecoveryService).updatePasswordUsingToken("12345", "Qwerty123=");
    }

    @Test
    void updatePasswordTest() throws Exception {
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("test@mail.com");

        String content = "{\n" +
            "  \"confirmPassword\": \"Qwerty123=\",\n" +
            "  \"currentPassword\": \"Qwerty123=\",\n" +
            "  \"password\": \"Qwerty124=\"\n" +
            "}";

        mockMvc.perform(put(LINK)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        UpdatePasswordDto dto =
            ModelUtils.getObjectMapper().readValue(content, UpdatePasswordDto.class);

        verify(ownSecurityService).updateCurrentPassword(dto, "test@mail.com");
    }
}
