package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.dto.place.AdminPlaceDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.repository.PlaceRepo;
import greencity.service.PlaceService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PlaceServiceImplTest {
    @MockBean PlaceRepo placeRepo;
    @Autowired private PlaceService placeService;

    @Test
    public void updateStatusTest() {
        Category category = Category.builder().name("categoryName").build();

        Place genericEntity =
                Place.builder()
                        .id(1L)
                        .name("placeName")
                        .description("placeDescription")
                        .email("placeEmail@gmail.com")
                        .phone("0973439892")
                        .status(PlaceStatus.PROPOSED)
                        .category(category)
                        .build();

        when(placeRepo.findById(any())).thenReturn(Optional.of(genericEntity));
        when(placeRepo.saveAndFlush(any())).thenReturn(genericEntity);

        placeService.updateStatus(genericEntity.getId(), PlaceStatus.DECLINED);

        assertEquals(PlaceStatus.DECLINED, genericEntity.getStatus());
    }

    @Test
    public void getPlacesByStatusTest() {
        Category category = Category.builder().name("categoryName").build();
        List<AdminPlaceDto> foundList;
        List<Place> places = new ArrayList<>(3);
        for (long i = 0; i < 3; i++) {
            Place place = Place.builder().id(i+1)
                .name("placeName"+i)
                .description("placeDescription"+i)
                .email("placeEmail@gmail.com"+i)
                .phone("066034022"+i)
                .modifiedDate(LocalDateTime.now().minusDays(i))
                .status(PlaceStatus.PROPOSED)
                .category(category).build();

            places.add(place);
        }

        when(placeRepo.findAllByStatusOrderByModifiedDateDesc(any())).thenReturn(places);
        foundList = placeService.getPlacesByStatus(PlaceStatus.PROPOSED);

        assertNotNull(foundList);
        for (AdminPlaceDto dto : foundList) {
            assertEquals(dto.getStatus(), PlaceStatus.PROPOSED);
            log.info(dto.toString());
        }
    }
}
