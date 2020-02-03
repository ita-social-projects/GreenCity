package greencity.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.habitstatistic.CalendarUsefulHabitsDto;
import greencity.dto.habitstatistic.HabitDto;
import greencity.entity.User;
import greencity.service.HabitStatisticService;
import greencity.service.UserService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private HabitStatisticService habitStatisticService;

    @Test
    @WithMockUser
    public void getUserHabitsWithValidUserIdTest() throws Exception {
        long userId = 1L;
        HabitDto userHabitDto = new HabitDto();
        when(userService.findByEmail("user")).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(habitStatisticService.findAllHabitsAndTheirStatistics(userId, true, "en"))
            .thenReturn(Collections.singletonList(userHabitDto));
        MvcResult requestResult = mockMvc.perform(get("/user/" + userId + "/habits").param("language", "en"))
            .andExpect(status().isCreated()).andReturn();
        TypeReference<List<HabitDto>> typeRef =
            new TypeReference<List<HabitDto>>() {
            };
        String responseContent = requestResult.getResponse().getContentAsString();
        List<HabitDto> userHabits =
            new ObjectMapper().readValue(responseContent, typeRef);
        assertEquals(userHabitDto, userHabits.get(0));
    }

    @Test
    @WithMockUser
    public void getUserHabitsWithDifferentUserIdStatusCodeTest() throws Exception {
        long userId = 1L;
        when(userService.findByEmail("user")).thenReturn(Optional.of(User.builder().id(userId).build()));
        mockMvc.perform(get("/user/" + 2L + "/habits").param("language", "en"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void findInfoAboutUserHabitsWithValidUserIdTest() throws Exception {
        long userId = 1L;
        CalendarUsefulHabitsDto calendarUsefulHabitsDto = new CalendarUsefulHabitsDto();
        when(userService.findByEmail("user")).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(habitStatisticService.getInfoAboutUserHabits(userId))
            .thenReturn(calendarUsefulHabitsDto);
        MvcResult requestResult = mockMvc.perform(get("/user/" + userId + "/habits/statistic"))
            .andExpect(status().isOk()).andReturn();
        TypeReference<CalendarUsefulHabitsDto> typeRef =
            new TypeReference<CalendarUsefulHabitsDto>() {
            };
        String responseContent = requestResult.getResponse().getContentAsString();
        CalendarUsefulHabitsDto calendarUsefulHabitsDtoResponse =
            new ObjectMapper().readValue(responseContent, typeRef);
        assertEquals(calendarUsefulHabitsDtoResponse, calendarUsefulHabitsDtoResponse);
    }

    @Test
    @WithMockUser
    public void findInfoAboutUserHabitsWithDifferentUserIdStatusCodeTest() throws Exception {
        long userId = 1L;
        CalendarUsefulHabitsDto calendarUsefulHabitsDto = new CalendarUsefulHabitsDto();
        when(userService.findByEmail("user")).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(habitStatisticService.getInfoAboutUserHabits(userId))
            .thenReturn(calendarUsefulHabitsDto);
        mockMvc.perform(get("/user/" + 2L + "/habits/statistic"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void bulkDeleteUserGoalsWithEmptyInputIdsTest() throws Exception {
        mockMvc.perform(delete("/user/1/userGoals").param("ids", ""))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void bulkDeleteUserGoalsWithInvalidInputIdsTest() throws Exception {
        mockMvc.perform(delete("/user/1/userGoals").param("ids", "foo"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void bulkDeleteUserGoalsWithTrailingCommaInputIdsTest() throws Exception {
        mockMvc.perform(delete("/user/1/userGoals").param("ids", "1,2,3,"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void bulkDeleteUserGoalsWithValidInputIdTest() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(userService.deleteUserGoals("1")).thenReturn(Collections.emptyList());
        mockMvc.perform(delete("/user/1/userGoals").param("ids", "1"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void getAllActivatedUsersAmountStatusCodeTest() throws Exception {
        mockMvc.perform(get("/user/activatedUsersAmount"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void getAllActivatedUsersAmountWithServiceMockTest() throws Exception {
        when(userService.getActivatedUsersAmount()).thenReturn(42L);
        MvcResult requestResult = mockMvc.perform(get("/user/activatedUsersAmount"))
            .andExpect(status().isOk()).andReturn();
        assertEquals("42", requestResult.getResponse().getContentAsString());
    }
}
