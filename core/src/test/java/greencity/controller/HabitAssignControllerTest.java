package greencity.controller;

import com.google.gson.Gson;
import greencity.ModelUtils;
import static greencity.ModelUtils.getPrincipal;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import greencity.service.HabitAssignService;
import java.security.Principal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class HabitAssignControllerTest {
    private MockMvc mockMvc;

    @Mock
    HabitAssignService habitAssignService;

    @InjectMocks
    HabitAssignController habitAssignController;

    private Principal principal = getPrincipal();

    private static final String habitLink = "/habit/assign";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitAssignController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void assign() throws Exception {
        UserVO user = ModelUtils.getUserVO();
        mockMvc.perform(post(habitLink + "/{habitId}", 1)
            .principal(principal))
            .andExpect(status().isCreated());
        Long id = 1L;
        verify(habitAssignService, never()).assignDefaultHabitForUser(eq(id), eq(user));
    }

    @Test
    void getHabitAssign() throws Exception {
        mockMvc.perform(get(habitLink + "/{habitAssignId}", 1))
            .andExpect(status().isOk());
        verify(habitAssignService).getById(1L, "en");
    }

    @Test
    void updateAssignByHabitId() throws Exception {
        HabitAssignStatDto habitAssignStatDto = new HabitAssignStatDto();
        habitAssignStatDto.setStatus(HabitAssignStatus.ACTIVE);
        Gson gson = new Gson();
        String json = gson.toJson(habitAssignStatDto);
        mockMvc.perform(patch(habitLink + "/{habitId}", 1)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitAssignService).updateStatusByHabitIdAndUserId(1L, null, habitAssignStatDto);
    }

    @Test
    void enrollHabit() throws Exception {
        mockMvc.perform(post(habitLink + "/{habitId}/enroll/{date}", 1, LocalDate.now()))
            .andExpect(status().isOk());
        verify(habitAssignService).enrollHabit(1L, null, LocalDate.now());
    }

    @Test
    void unenrollHabit() throws Exception {
        mockMvc.perform(post(habitLink + "/{habitId}/unenroll/{date}", 1, LocalDate.now()))
            .andExpect(status().isOk());
        verify(habitAssignService).unenrollHabit(1L, null, LocalDate.now());
    }
}
