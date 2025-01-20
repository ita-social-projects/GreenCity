package greencity.repository.impl;

import greencity.dto.filter.FilterEventDto;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

@SpringBootTest
public class EventSearchRepoImplTest {
    @Test
    public void findEventIdsTest() {
        var zonedNow = ZonedDateTime.now();
        var filterDto = FilterEventDto.builder()
            .from(zonedNow.plusDays(1)).to(zonedNow.plusDays(10)).build();
    }
}
