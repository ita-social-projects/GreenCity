package greencity.controller;

import greencity.service.SocialNetworkImageService;
import greencity.service.SocialNetworkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SocialNetworkControllerTest {

    private static final String socialNetworksLink = "/social-networks";

    private MockMvc mockMvc;

    @InjectMocks
    private SocialNetworkController socialNetworkController;

    @Mock
    private SocialNetworkImageService socialNetworkImageService;

    @Mock
    private SocialNetworkService socialNetworkService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(socialNetworkController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void getSocialNetworkImageByUrl() throws Exception {
        mockMvc.perform(get(socialNetworksLink + "/image?url=test-image")).andExpect(status().isOk());
        verify(socialNetworkImageService).getSocialNetworkImageByUrl("test-image");
    }

    @Test
    void deleteSocialNetwork() throws Exception {
        mockMvc.perform(delete(socialNetworksLink + "?id=1")).andExpect(status().isOk());
        verify(socialNetworkService).delete(1L);
    }
}