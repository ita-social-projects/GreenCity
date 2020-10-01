package greencity.repository;

import greencity.GreenCityApplication;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GreenCityApplication.class)
@Sql("classpath:sql/place_repo.sql")
class PlaceRepoTest {
    @Autowired
    PlaceRepo placeRepo;

    @Test
    void findAllByStatusOrderByModifiedDateDescTest() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<Place> page = placeRepo.findAllByStatusOrderByModifiedDateDesc(PlaceStatus.PROPOSED, pageable);
        List<Place> places = page.get().collect(Collectors.toList());
        assertEquals(5, places.size());
    }

    @Test
    void findAllByStatusOrderByModifiedDateDescOrderTest() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<Place> page = placeRepo.findAllByStatusOrderByModifiedDateDesc(PlaceStatus.PROPOSED, pageable);
        List<Place> places = page.get().collect(Collectors.toList());
        assertEquals(5, places.get(0).getId());
    }

    @Test
    void getAverageRateTest() {
        Double averageRate = placeRepo.getAverageRate(1L);
        System.out.println(averageRate);
        assertEquals(8.0, averageRate);
    }

    @Test
    void getPlacesByStatusTest() {
        List<Place> places = placeRepo.getPlacesByStatus(PlaceStatus.PROPOSED);
        assertEquals(5, places.size());
    }

    @Test
    void findPlacesByMapsBoundsTest() {
        List<Place> places = placeRepo.findPlacesByMapsBounds(
            50.575, 25.0455, 45.475, 23.0212, PlaceStatus.PROPOSED);
        assertEquals(1, places.get(0).getId());
    }

    @Test
    void searchByTest() {
        Pageable pageable = PageRequest.of(0, 6);
        Page<Place> page = placeRepo.searchBy(pageable, "InterestingPlace2");
        List<Place> places = page.get().collect(Collectors.toList());
        assertEquals("InterestingPlace2", places.get(0).getName());
    }
}
