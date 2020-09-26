package greencity.service.impl;

import greencity.repository.UserRepo;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

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
        List<Integer> months = List.of(4, 4, 4);
        when(userRepo.findAllRegistrationMonths()).thenReturn(months);
        Map<Integer, Integer> map = new LinkedHashMap<>(11);
        for (int i = 0; i < 12; i++) {
            if (i == 4) {
                map.put(i, 3);
            } else {
                map.put(i, 0);
            }
        }
        assertEquals(map, graphService.getRegistrationStatistics());
    }
}
