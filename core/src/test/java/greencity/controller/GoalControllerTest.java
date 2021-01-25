package greencity.controller;

import greencity.dto.goal.GoalRequestDto;
import greencity.service.GoalService;
import java.util.Collections;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.validation.Validator;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GoalControllerTest {
    private static final String goalLink = "/user/goals";
    private MockMvc mockMvc;
    @InjectMocks
    private GoalController goalController;
    @Mock
    private GoalService goalService;
    @Mock
    private Validator mockValidator;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(goalController)
            .setValidator(mockValidator)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void bulkDeleteUserGoalsTest() throws Exception {
        mockMvc.perform(delete(goalLink + "/user-goals?ids=1,2", 1))
            .andExpect(status().isOk());

        verify(goalService).deleteUserGoals("1,2");
    }

    @Test
    void updateUserGoalStatusWithLanguageParamTest() throws Exception {
        mockMvc.perform(patch(goalLink + "/{userGoalId}", 1, 1)
            .locale(new Locale("ru")))
            .andExpect(status().isCreated());

        verify(goalService).updateUserGoalStatus(null, 1L, "ru");
    }

    @Test
    void updateUserGoalStatusWithoutLanguageParamTest() throws Exception {
        mockMvc.perform(patch(goalLink + "/{userGoalId}", 1, 1))
            .andExpect(status().isCreated());

        verify(goalService).updateUserGoalStatus(null, 1L, "en");
    }

    @Test
    void saveUserGoalsWithoutLanguageParamTest() throws Exception {
        String content = "[\n"
            + " {\n"
            + "    \"id\": 1\n"
            + " }\n"
            + "]\n";

        mockMvc.perform(post(goalLink + "?habitId=1&lang=en", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        GoalRequestDto dto = new GoalRequestDto(1L);
        verify(goalService).saveUserGoals(null, 1L, Collections.singletonList(dto), "en");
    }

    @Test
    void getUserGoalsWithLanguageParamTest() throws Exception {
        mockMvc.perform(get(goalLink + "/habits/1/shopping-list?lang=en", 1))
            .andExpect(status().isOk());

        verify(goalService).getUserGoals(null, 1L, "en");
    }

    @Test
    void getUserGoalsWithoutLanguageParamTest() throws Exception {
        mockMvc.perform(get(goalLink + "/habits/1/shopping-list", 1))
            .andExpect(status().isOk());

        verify(goalService).getUserGoals(null, 1L, "en");
    }
}
