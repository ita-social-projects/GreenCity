package greencity.controller;

import com.google.gson.Gson;
import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignStatDto;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.service.HabitAssignService;
import greencity.service.HabitService;
import greencity.service.HabitStatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Locale;

import static greencity.ModelUtils.getPrincipal;
import static greencity.enums.HabitRate.GOOD;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HabitControllerTest {

    private MockMvc mockMvc;

    @Mock
    HabitStatisticService habitStatisticService;

    @Mock
    HabitAssignService habitAssignService;

    @Mock
    HabitService habitService;;

    @InjectMocks
    HabitController habitController;

    private Principal principal = getPrincipal();

    private static final String habitLink = "/habit";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void assign() throws Exception {
        UserVO user = ModelUtils.getUserVO();
        mockMvc.perform(post(habitLink + "/assign/{habitId}", 1)
                .principal(principal))
                .andExpect(status().isCreated());
        Long id = 1L;
        verify(habitAssignService, never()).assignHabitForUser(eq(id) , eq(user));
    }

    @Test
    void getHabitAssign() throws Exception {
        mockMvc.perform(get(habitLink + "/assign/{habitAssignId}", 1))
                .andExpect(status().isOk());
        verify(habitAssignService).getById(1L);
    }

    @Test
    void updateAssign() throws Exception {
       HabitAssignStatDto habitAssignStatDto = new HabitAssignStatDto();
       habitAssignStatDto.setAcquired(true);
       habitAssignStatDto.setSuspended(true);
        Gson gson = new Gson();
        String json = gson.toJson(habitAssignStatDto);
        mockMvc.perform(patch(habitLink + "/assign/{habitAssignId}", 1)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(habitAssignService).updateStatus(1L, habitAssignStatDto);
    }

    @Test
    void getAll() throws Exception {
        int pageNumber = 1;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Locale locale = new Locale("en");
        Gson gson = new Gson();
        String json = gson.toJson(locale);
        mockMvc.perform(get(habitLink + "?page=1")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(habitService).getAllHabitsByLanguageCode(pageable, locale.getLanguage());
    }

    @Test
    void save() throws Exception {
        AddHabitStatisticDto addHabitStatisticDto = new AddHabitStatisticDto();
        addHabitStatisticDto.setAmountOfItems(1);
        addHabitStatisticDto.setCreateDate(ZonedDateTime.parse("2020-10-09T16:49:01.020Z[UTC]"));
        addHabitStatisticDto.setHabitAssignId(1L);
        addHabitStatisticDto.setHabitRate(GOOD);
        Gson gson = new Gson();
        String json = gson.toJson(addHabitStatisticDto);
        mockMvc.perform(post(habitLink + "/statistic/")
                .content("{\n" +
                        "  \"amountOfItems\": 1,\n" +
                        "  \"createDate\": \"2020-10-09T16:49:01.020Z\",\n" +
                        "  \"habitAssignId\": 1,\n" +
                        "  \"habitRate\": \"GOOD\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(habitStatisticService).save(addHabitStatisticDto);
    }

    @Test
    void updateStatistic() throws Exception {
        UpdateHabitStatisticDto habitStatisticForUpdateDto = new UpdateHabitStatisticDto();
        habitStatisticForUpdateDto.setAmountOfItems(1);
        habitStatisticForUpdateDto.setHabitRate(GOOD);
        Gson gson = new Gson();
        String json = gson.toJson(habitStatisticForUpdateDto);
        mockMvc.perform(patch(habitLink + "/statistic/{habitStatisticId}", 1)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(habitStatisticService).update(1L, habitStatisticForUpdateDto);
    }

    @Test
    void findAllByHabitId() throws Exception {
        mockMvc.perform(get(habitLink + "/statistic/{habitId}", 1))
                .andExpect(status().isOk());
        verify(habitStatisticService).findAllStatsByHabitId(1L);
    }

    @Test
    void getTodayStatisticsForAllHabitItems() throws Exception {
        Locale locale = new Locale("en");
        Gson gson = new Gson();
        String json = gson.toJson(locale);
        mockMvc.perform(get(habitLink + "/statistic/todayStatisticsForAllHabitItems")
                .content(json))
                .andExpect(status().isOk());
        verify(habitStatisticService).getTodayStatisticsForAllHabitItems(locale.getLanguage());
    }
}
