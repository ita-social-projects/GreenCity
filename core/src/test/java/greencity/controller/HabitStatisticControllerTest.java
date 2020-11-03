package greencity.controller;

import com.google.gson.Gson;
import static greencity.ModelUtils.getPrincipal;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import static greencity.enums.HabitRate.GOOD;
import greencity.service.HabitStatisticService;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class HabitStatisticControllerTest {

    private MockMvc mockMvc;

    @Mock
    HabitStatisticService habitStatisticService;

    @InjectMocks
    HabitStatisticController habitStatisticController;

    private Principal principal = getPrincipal();

    private static final String habitLink = "/habit";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitStatisticController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void save() throws Exception {
        AddHabitStatisticDto addHabitStatisticDto = new AddHabitStatisticDto();
        addHabitStatisticDto.setAmountOfItems(1);
        addHabitStatisticDto.setCreateDate(ZonedDateTime.parse("2020-10-09T16:49:01.020Z[UTC]"));
        addHabitStatisticDto.setHabitRate(GOOD);
        mockMvc.perform(post(habitLink + "/{id}/statistic/", 1L)
            .content("{\n" +
                "  \"amountOfItems\": 1,\n" +
                "  \"createDate\": \"2020-10-09T16:49:01.020Z\",\n" +
                "  \"habitRate\": \"GOOD\"\n" +
                "}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
        verify(habitStatisticService).saveByHabitIdAndUserId(1L, 1L, addHabitStatisticDto);
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
        verify(habitStatisticService).update(1L, 1L, habitStatisticForUpdateDto);
    }

    @Test
    void findAllByHabitId() throws Exception {
        mockMvc.perform(get(habitLink + "/{id}/statistic/", 1))
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
