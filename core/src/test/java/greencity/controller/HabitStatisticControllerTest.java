package greencity.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.habitstatistic.HabitItemsAmountStatisticDto;
import greencity.repository.HabitStatisticRepo;
import java.util.Collections;
import java.util.List;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import org.junit.Ignore;
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

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class HabitStatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HabitStatisticRepo habitStatisticRepo;
    private static final String habitStatisticLink = "/habit/statistic/todayStatisticsForAllHabitItems";

    @Test
    @WithMockUser
    public void getTodayStatisticsForAllHabitItemsStatusCodeTest() throws Exception {
        mockMvc.perform(get(habitStatisticLink))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void getTodayStatisticsForAllHabitItemsWithLanguageCodeTest() throws Exception {
        mockMvc.perform(get(habitStatisticLink + "?language=en"))
            .andExpect(status().isOk());
    }

    @Ignore
    @Test
    @WithMockUser
    public void getTodayStatisticsForAllHabitItemsSingleHabitTest() throws Exception {
        when(habitStatisticRepo.getStatisticsForAllHabitItemsByDate(any(), anyString()))
            .thenReturn(Collections.singletonList(new Tuple() {
                @Override
                public <X> X get(TupleElement<X> tupleElement) {
                    return null;
                }

                @Override
                public <X> X get(String alias, Class<X> type) {
                    return null;
                }

                @Override
                public Object get(String alias) {
                    return null;
                }

                @Override
                public <X> X get(int i, Class<X> type) {
                    return null;
                }

                @Override
                public Object get(int i) {
                    if (i == 0) {
                        return "foo";
                    } else if (i == 1) {
                        return 42L;
                    }
                    return null;
                }

                @Override
                public Object[] toArray() {
                    return new Object[0];
                }

                @Override
                public List<TupleElement<?>> getElements() {
                    return null;
                }
            }));
        MvcResult requestResult = mockMvc.perform(get(habitStatisticLink))
            .andExpect(status().isOk()).andReturn();
        TypeReference<List<HabitItemsAmountStatisticDto>> typeRef =
            new TypeReference<List<HabitItemsAmountStatisticDto>>() {
            };
        String responseContent = requestResult.getResponse().getContentAsString();
        List<HabitItemsAmountStatisticDto> habitItemAndItsStatistic =
            new ObjectMapper().readValue(responseContent, typeRef);
        assertEquals(1, habitItemAndItsStatistic.size());
        assertEquals("foo", habitItemAndItsStatistic.get(0).getHabitItem());
        assertEquals(42L, habitItemAndItsStatistic.get(0).getNotTakenItems());
    }
}
