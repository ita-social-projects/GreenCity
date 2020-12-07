package greencity.security.controller;

import greencity.security.service.GoogleSecurityService;
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
class GoogleSecurityControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private GoogleSecurityController googleSecurityController;

    @Mock
    private GoogleSecurityService googleSecurityService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(googleSecurityController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void authenticateTest() throws Exception {
        mockMvc.perform(get("/googleSecurity")
            .param("idToken", "almostSecretToken"))
            .andExpect(status().isOk());
        verify(googleSecurityService).authenticate("almostSecretToken");
    }
}
