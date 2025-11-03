package greencity.controller;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.converters.UserClaimsArgumentResolver;
import greencity.converters.UserIdArgumentResolver;
import greencity.dto.achievement.ActionDto;
import static greencity.enums.AchievementStatus.ACHIEVED;
import static greencity.enums.AchievementStatus.UNACHIEVED;

import greencity.security.jwt.JwtTool;
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
import static greencity.ModelUtils.getActionDto;
import static greencity.ModelUtils.getPrincipal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AchievementControllerTest {
    private static final String achievementLink = "/achievements";
    private MockMvc mockMvc;
    private final Principal principal = getPrincipal();

    @Mock
    private JwtTool jwtTool;

    @Mock
    private AchievementService achievementService;

    @InjectMocks
    private AchievementController achievementController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(achievementController)
            .setCustomArgumentResolvers(
                new PageableHandlerMethodArgumentResolver(),
                new UserIdArgumentResolver(jwtTool),
                new UserClaimsArgumentResolver(jwtTool))
            .build();

        String jwt = "jwt";
        when(jwtTool.extractJwtFromNativeWebRequest(any()))
            .thenReturn(jwt);
        when(jwtTool.extractUserId(jwt))
            .thenReturn(TestConst.USER_ID);
        when(jwtTool.extractUserClaims(jwt))
            .thenReturn(ModelUtils.getUserClaims());
    }

    @Test
    void findAllTest() throws Exception {
        mockMvc.perform(get(achievementLink).principal(principal)).andExpect(status().isOk());
        verify(achievementService).findAllByTypeAndCategory(TestConst.USER_ID, TestConst.EMAIL, null, null);
    }

    @Test
    void findAllAchievedTest() throws Exception {
        mockMvc.perform(get(achievementLink).principal(principal).param("achievementStatus", ACHIEVED.toString()))
            .andExpect(status().isOk());
        verify(achievementService).findAllByTypeAndCategory(TestConst.USER_ID, TestConst.EMAIL, ACHIEVED, null);
    }

    @Test
    void findAllUnAchievedTest() throws Exception {
        mockMvc.perform(get(achievementLink).principal(principal).param("achievementStatus", UNACHIEVED.toString()))
            .andExpect(status().isOk());
        verify(achievementService).findAllByTypeAndCategory(TestConst.USER_ID, TestConst.EMAIL, UNACHIEVED, null);
    }

    @Test
    void achieveTest() {
        var dto = getActionDto();
        achievementController.achieve(ActionDto.builder().build());
        verify(achievementService).achieve(dto);
    }

    @Test
    void countAllTest() throws Exception {
        mockMvc.perform(get(achievementLink + "/count").principal(principal)).andExpect(status().isOk());
        verify(achievementService).findAchievementCountByTypeAndCategory(TestConst.USER_ID, "test@gmail.com", null,
            null);
    }

    @Test
    void countAllAchievedTest() throws Exception {
        mockMvc
            .perform(
                get(achievementLink + "/count").principal(principal).param("achievementStatus", ACHIEVED.toString()))
            .andExpect(status().isOk());
        verify(achievementService).findAchievementCountByTypeAndCategory(TestConst.USER_ID, "test@gmail.com", ACHIEVED,
            null);
    }

    @Test
    void countAllUnAchievedTest() throws Exception {
        mockMvc
            .perform(
                get(achievementLink + "/count").principal(principal).param("achievementStatus", UNACHIEVED.toString()))
            .andExpect(status().isOk());
        verify(achievementService).findAchievementCountByTypeAndCategory(TestConst.USER_ID, "test@gmail.com",
            UNACHIEVED, null);
    }

    @Test
    void findAllV2Test() throws Exception {
        mockMvc.perform(get(achievementLink + "/all")
            .principal(principal))
            .andExpect(status().isOk());
        verify(achievementService).findAll();
    }

    @Test
    void findAllUserAchievementsByUserIdTest() throws Exception {
        Long userId = 1L;
        mockMvc.perform(get(achievementLink + "/user-achievements/" + userId)
            .principal(principal))
            .andExpect(status().isOk());
        verify(achievementService).findAllUserAchievementsByUserId(userId);
    }

    @Test
    void findAllUserAchievementsByEmailTest() throws Exception {
        String email = "test@gmail";
        mockMvc.perform(get(achievementLink + "/user-achievements")
            .param("email", email)
            .principal(principal))
            .andExpect(status().isOk());
        verify(achievementService).findAllUserAchievementsByEmail(email);
    }
}
