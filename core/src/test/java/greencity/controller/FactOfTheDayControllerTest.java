package greencity.controller;

import greencity.TestConst;
import greencity.converters.UserIdArgumentResolver;
import greencity.security.jwt.JwtTool;
import greencity.service.FactOfTheDayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import java.security.Principal;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FactOfTheDayControllerTest {
    private static final String factOfTheDayLink = "/fact-of-the-day";

    private MockMvc mockMvc;
    @InjectMocks
    private FactOfTheDayController factOfTheDayController;
    @Mock
    private FactOfTheDayService factOfTheDayService;
    @Mock
    JwtTool jwtTool;
    @Mock
    private Validator mockValidator;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(factOfTheDayController)
            .setCustomArgumentResolvers(new UserIdArgumentResolver(jwtTool))
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void getRandomFactOfTheDayTest() throws Exception {
        mockMvc.perform(get(factOfTheDayLink + "/random"))
            .andExpect(status().isOk());
        verify(factOfTheDayService).getRandomGeneralFactOfTheDay();
    }

    @Test
    void getRandomFactOfTheDayByTags() throws Exception {
        Principal mockPrincipal = () -> "testUser@example.com";
        Locale mockLocale = Locale.ENGLISH;
        String jwt = "jwt";

        when(jwtTool.extractJwtFromNativeWebRequest(any()))
            .thenReturn(jwt);
        when(jwtTool.extractUserId(jwt))
            .thenReturn(TestConst.USER_ID);

        mockMvc.perform(get(factOfTheDayLink + "/random/by-tags")
            .principal(mockPrincipal)
            .locale(mockLocale)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(factOfTheDayService).getRandomFactOfTheDayForUser(TestConst.USER_ID);
    }
}
