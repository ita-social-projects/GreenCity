package greencity.controller;

import com.google.gson.Gson;
import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.constant.RedirectUrl;
import greencity.dto.habit.HabitAssignCustomPropertiesDto;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.habit.UserShoppingAndCustomShoppingListsDto;
import greencity.dto.user.UserVO;
import greencity.enums.HabitAssignStatus;
import greencity.service.HabitAssignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Locale;
import static greencity.ModelUtils.getPrincipal;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitAssignControllerTest {
    private MockMvc mockMvc;

    @Mock
    private RestClient restClient;

    @Mock
    HabitAssignService habitAssignService;

    @Mock
    private RedirectUrl redirectUrl;

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
        verify(habitAssignService).getByHabitAssignIdAndUserId(1L, null, "en");
    }

    @Test
    void updateAssignByHabitAssignId() throws Exception {
        HabitAssignStatDto habitAssignStatDto = new HabitAssignStatDto();
        habitAssignStatDto.setStatus(HabitAssignStatus.INPROGRESS);
        Gson gson = new Gson();
        String json = gson.toJson(habitAssignStatDto);
        mockMvc.perform(patch(habitLink + "/{habitAssignId}", 1)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitAssignService).updateStatusByHabitAssignId(1L, habitAssignStatDto);
    }

    @Test
    void updateStatusAndDurationOfHabitAssignTest() throws Exception {
        mockMvc.perform(put(habitLink + "/{habitAssignId}/update-status-and-duration?duration=15", 1L)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitAssignService).updateStatusAndDurationOfHabitAssign(1L, null, 15);
    }

    @Test
    void updateHabitAssignDurationTest() throws Exception {
        mockMvc.perform(put(habitLink + "/{habitAssignId}/update-habit-duration?duration=15", 1L)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitAssignService).updateUserHabitInfoDuration(1L, null, 15);
    }

    @Test
    void enrollHabit() throws Exception {
        Long habitAssignId = 2L;
        LocalDate date = LocalDate.now();
        mockMvc.perform(post(habitLink + "/{habitAssignId}/enroll/{date}", habitAssignId, date))
            .andExpect(status().isOk());
        verify(habitAssignService).enrollHabit(habitAssignId, null, date, "en");
    }

    @Test
    void unenrollHabit() throws Exception {
        Long habitAssignId = 1L;
        LocalDate date = LocalDate.now();

        mockMvc.perform(post(habitLink + "/{habitAssignId}/unenroll/{date}", habitAssignId, date))
            .andExpect(status().isOk());
        verify(habitAssignService).unenrollHabit(habitAssignId, null, date);
    }

    @Test
    void getHabitAssignBetweenDatesTest() throws Exception {
        LocalDate from = LocalDate.now();
        LocalDate to = from.plusDays(2L);

        mockMvc.perform(get(habitLink + "/activity/{from}/to/{to}", from, to))
            .andExpect(status().isOk());

        verify(habitAssignService).findHabitAssignsBetweenDates(null, from, to, "en");
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
        mockMvc.perform(get(habitLink + "/allForCurrentUser")
            .principal(principal))
            .andExpect(status().isOk());

        verify(habitAssignService).getAllHabitAssignsByUserIdAndStatusNotCancelled(null, "en");
    }

    @Test
    void deleteHabitAssignTest() throws Exception {
        Long habitAssignId = 1L;

        Principal principal = () -> "xd87@ukr.net";
        mockMvc.perform(delete(habitLink + "/delete/{habitAssignId}", habitAssignId)
            .principal(principal)).andExpect(status().isOk());
        verify(habitAssignService).deleteHabitAssign(habitAssignId, null);
    }

    @Test
    void assignCustom() throws Exception {
        HabitAssignCustomPropertiesDto propertiesDto = ModelUtils.getHabitAssignCustomPropertiesDto();
        Gson gson = new Gson();
        String json = gson.toJson(propertiesDto);
        UserVO userVO = new UserVO();
        mockMvc.perform(post(habitLink + "/{habitId}/custom", 1L)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
        verify(habitAssignService).assignCustomHabitForUser(1L, userVO, propertiesDto);
    }

    @Test
    void getAllHabitAssignsByHabitIdAndAcquired() throws Exception {
        mockMvc.perform(get(habitLink + "/{habitId}/all", 1L)
            .principal(principal))
            .andExpect(status().isOk());
        verify(habitAssignService).getAllHabitAssignsByHabitIdAndStatusNotCancelled(1L, "en");
    }

    @Test
    void getInprogressHabitAssignOnDate() throws Exception {
        mockMvc.perform(get(habitLink + "/active/{date}", LocalDate.now()))
            .andExpect(status().isOk());

        verify(habitAssignService).findInprogressHabitAssignsOnDate(null, LocalDate.now(), "en");
    }

    @Test
    void getUsersHabitByHabitId() throws Exception {
        Long habitAssignId = 1L;
        mockMvc.perform(get(habitLink + "/{habitAssignId}/more", habitAssignId))
            .andExpect(status().isOk());

        verify(habitAssignService).findHabitByUserIdAndHabitAssignId(null, habitAssignId, "en");
    }

    @Test
    void getUserAndCustomListByUserIdAndHabitId() throws Exception {
        Long habitAssignId = 1L;
        mockMvc.perform(get(habitLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId))
            .andExpect(status().isOk());
        verify(habitAssignService).getUserShoppingAndCustomShoppingLists(null, habitAssignId, "en");
    }

    @Test
    void getUserAndCustomListByUserIdAndHabitIdAndLocale() throws Exception {
        Long habitAssignId = 1L;
        mockMvc.perform(get(habitLink + "/{habitAssignId}/allUserAndCustomList", habitAssignId)
            .locale(Locale.forLanguageTag("ua")))
            .andExpect(status().isOk());
        verify(habitAssignService).getUserShoppingAndCustomShoppingLists(null, habitAssignId, "ua");
    }

    @Test
    void getListOfUserAndCustomShoppingListsInprogress() throws Exception {
        mockMvc.perform(get(habitLink + "/allUserAndCustomShoppingListsInprogress")
            .principal(principal)
            .locale(Locale.forLanguageTag("en")))
            .andExpect(status().isOk());
        verify(habitAssignService).getListOfUserAndCustomShoppingListsWithStatusInprogress(null, "en");
    }

    @Test
    void updateUserAndCustomShoppingLists() throws Exception {
        UserShoppingAndCustomShoppingListsDto dto = ModelUtils.getUserShoppingAndCustomShoppingListsDto();
        Gson gson = new Gson();
        String json = gson.toJson(dto);
        mockMvc.perform(put(habitLink + "/{habitAssignId}/allUserAndCustomList", 1L)
            .locale(Locale.forLanguageTag("ua"))
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitAssignService).fullUpdateUserAndCustomShoppingLists(null, 1L, dto, "ua");
    }

    @Test
    void updateProgressNotificationHasDisplayedTest() throws Exception {
        mockMvc.perform(put(habitLink + "/{habitAssignId}/updateProgressNotificationHasDisplayed", 1L)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitAssignService).updateProgressNotificationHasDisplayed(1L, null);
    }

    @Test
    void confirmInvitation() throws Exception {
        when(redirectUrl.getClintAddress()).thenReturn("http://localhost:8080/");
        mockMvc.perform(get(habitLink + "/confirm/{habitAssignId}", 1L))
            .andExpect(status().is3xxRedirection());
        verify(habitAssignService).confirmHabitInvitation(1L);
    }
}
