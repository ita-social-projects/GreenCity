package greencity.security.controller;

import greencity.security.service.FacebookSecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FacebookSecurityControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private FacebookSecurityController facebookSecurityController;

    @Mock
    private FacebookSecurityService facebookSecurityService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(facebookSecurityController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void generateFacebookAuthorizeURLTest() throws Exception {
        mockMvc.perform(get("/facebookSecurity/generateFacebookAuthorizeURL"))
            .andExpect(status().isOk());
        verify(facebookSecurityService).generateFacebookAuthorizeURL();
    }

    @Test
    void generateFacebookAccessTokenTest() throws Exception {
        mockMvc.perform(get("/facebookSecurity/facebook")
            .param("code","almostSecretCode"))
            .andExpect(status().isOk());
        verify(facebookSecurityService).generateFacebookAccessToken("almostSecretCode");
    }
}
