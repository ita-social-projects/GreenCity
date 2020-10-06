/*
package greencity.controller;

import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.HabitItemsAmountStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.service.HabitStatisticService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class HabitStatisticControllerTest {
    private static final String habitStatisticLink = "/habit/statistic/";
    private MockMvc mockMvc;

    @InjectMocks
    private HabitController habitController;

    @Mock
    private HabitStatisticService habitStatisticService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitController)
            .build();
    }

    @Test
    public void getTodayStatisticsForAllHabitItemsStatusCodeTest() throws Exception {
        when(habitStatisticService.getTodayStatisticsForAllHabitItems(anyString()))
            .thenReturn(Collections.singletonList((new HabitItemsAmountStatisticDto())));

        mockMvc.perform(get(habitStatisticLink + "todayStatisticsForAllHabitItems"))
            .andExpect(status().isOk());
    }

    @Test
    public void findAllByHabitIdTest() throws Exception {
        when(habitStatisticService.findAllByHabitId(anyLong()))
            .thenReturn(Collections.singletonList((new HabitStatisticDto())));

        mockMvc.perform(get(habitStatisticLink + "/{habitId}", 1))
            .andExpect(status().isOk());
    }

    @Test
    public void saveTest() throws Exception {
        when(habitStatisticService.save(any(AddHabitStatisticDto.class)))
            .thenReturn(new AddHabitStatisticDto());

        mockMvc.perform(post(habitStatisticLink)
            .content("{\n" +
                "\"amountOfItems\": 0,\n" +
                "\"createdOn\": \"2020-01-01T00:00:00.000Z\",\n" +
                "\"habitId\": 0,\n" +
                "\"habitRate\": \"DEFAULT\",\n" +
                "\"id\": 0\n}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        verify(habitStatisticService, times(1)).save(any(AddHabitStatisticDto.class));
    }


    @Test
    public void saveBadRequestTest() throws Exception {
        mockMvc.perform(post(habitStatisticLink)
            .content("{}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }


    @Test
    public void updateTest() throws Exception {
        when(habitStatisticService.update(anyLong(), any(UpdateHabitStatisticDto.class)))
            .thenReturn(new UpdateHabitStatisticDto());

        mockMvc.perform(patch(habitStatisticLink + "/{habitStatisticId}", 1)
            .content("{\n" +
                "  \"amountOfItems\": 0,\n" +
                "  \"habitRate\": \"DEFAULT\",\n" +
                "  \"id\": 0\n" +
                "}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(habitStatisticService, times(1)).update(anyLong(), any(UpdateHabitStatisticDto.class));
    }
}
*/
