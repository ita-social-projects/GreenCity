package greencity.service;

import greencity.repository.UserRepo;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class GraphServiceImplTest {
    @Mock
    UserRepo userRepo;

    @InjectMocks
    GraphServiceImpl graphService;

    @Test
    void getGeneralStatisticsForAllUsersByCities() {
        List<String> months = List.of("Lviv", "Lviv", "Lviv", "Kyiv", "Kyiv", "Kharkiv");
        Map<String, Integer> map = Map.of("Lviv", 3, "Kyiv", 2, "Kharkiv", 1, "Other", 0);
        when(userRepo.findAllUsersCities()).thenReturn(months);
        assertEquals(map, graphService.getGeneralStatisticsForAllUsersByCities());
    }

    @Test
    void getRegistrationStatistics() {
        Map<Integer, Long> months = new LinkedHashMap<>(11);
        months.put(4, 3L);
        when(userRepo.findAllRegistrationMonthsMap()).thenReturn(months);

        for (int i = 0; i < 12; i++) {
            months.putIfAbsent(i, 0L);
        }
        assertEquals(months, graphService.getRegistrationStatistics());
    }
}
