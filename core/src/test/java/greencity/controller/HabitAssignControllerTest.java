package greencity.controller;

import com.google.gson.Gson;
import greencity.ModelUtils;
import static greencity.ModelUtils.getPrincipal;

import greencity.client.RestClient;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import greencity.service.HabitAssignService;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class HabitAssignControllerTest {
    private MockMvc mockMvc;

    @Mock
    private RestClient restClient;

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
        verify(habitAssignService, never()).assignDefaultHabitForUser(id, user);
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
        habitAssignStatDto.setStatus(HabitAssignStatus.INPROGRESS);
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
        verify(habitAssignService).enrollHabit(1L, null, LocalDate.now(), "en");
    }

    @Test
    void unenrollHabit() throws Exception {
        mockMvc.perform(post(habitLink + "/{habitId}/unenroll/{date}", 1, LocalDate.now()))
            .andExpect(status().isOk());
        verify(habitAssignService).unenrollHabit(1L, null, LocalDate.now());
    }

    @Test
    void getHabitAssignBetweenDatesTest() throws Exception {
        Locale locale = new Locale("en", "US");
        mockMvc.perform(get(habitLink + "/activity/{from}/to/{to}", LocalDate.now(), LocalDate.now().plusDays(2L)))
            .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignsBetweenDates(null, LocalDate.now(),
            LocalDate.now().plusDays(2L), "en");
    }

    @Test
    void cancelHabitAssign() throws Exception {
        mockMvc.perform(patch(habitLink + "/cancel/{habitId}", 1L)
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitAssignService).cancelHabitAssign(1L, null);
    }

    @Test
    void getHabitAssignByHabitIdTest() throws Exception {
        mockMvc.perform(get(habitLink + "/{habitId}/active", 1L)
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignByUserIdAndHabitId(null, 1L, "en");
    }

    @Test
    void getCurrentUserHabitAssignsByIdAndAcquired() throws Exception {
        mockMvc.perform(get(habitLink)
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByUserIdAndAcquiredStatus(null, "en");
    }
}
