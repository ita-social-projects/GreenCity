package greencity.repository;

import greencity.entity.OpeningHours;
import greencity.entity.Place;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Sql("classpath:sql/open_hours.sql")
class OpenHoursRepoTest {

    @Autowired
    OpenHoursRepo openHoursRepo;

    private final Place place = Place.builder()
        .id(1L)
        .name("test")
        .build();

    @Test
    void findAllByPlaceTest() {
        List<OpeningHours> openingHours = openHoursRepo.findAllByPlace(place);
        assertEquals(2, openingHours.size());
        assertEquals(1, openingHours.get(0).getId());
    }

    @Test
    void findAllByPlaceIdTest() {
        Set<OpeningHours> openingHours = openHoursRepo.findAllByPlaceId(place.getId());
        assertEquals(2, openingHours.size());
    }

    @Test
    void deleteAllByPlaceIdTest() {
        openHoursRepo.deleteAllByPlaceId(1L);
        Set<OpeningHours> all = getOpeningHours();
        assertEquals(0, all.size());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Set<OpeningHours> getOpeningHours() {
        return openHoursRepo.findAllByPlaceId(place.getId());
    }
}
