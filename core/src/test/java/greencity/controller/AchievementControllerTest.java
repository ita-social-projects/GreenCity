package greencity.controller;

import greencity.enums.AchievementCategoryType;
import greencity.repository.AchievementRepo;
import greencity.service.AchievementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static greencity.ModelUtils.getPrincipal;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AchievementControllerTest {
    private static final String achievementLink = "/achievements";
    private MockMvc mockMvc;
    private final Principal principal = getPrincipal();

    @InjectMocks
    private AchievementController achievementController;

    @Mock
    private AchievementService achievementService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(achievementController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void findAllTest() throws Exception {
        mockMvc.perform(get(achievementLink)).andExpect(status().isOk());
        verify(achievementService).findAll();
    }

    @Test
    void findAllByUserIDTest() throws Exception {
        mockMvc.perform(get(achievementLink + "/achieved").principal(principal)).andExpect(status().isOk());
        verify(achievementService).findAllByUserEmail(anyString());
    }
}
