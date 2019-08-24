package greencity.service.impl;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.dto.location.MapBoundsDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.repository.PlaceRepo;
import greencity.service.PlaceService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void findPlacesByMapsBoundsTest() {
        MapBoundsDto mapBoundsDto = new MapBoundsDto(20.0, 60.0, 60.0, 10.0);
        List<Place> placeExpected =
                new ArrayList<Place>() {
                    {
                        add(Place.builder().name("MyPlace").id(1L).build());
                    }
                };
        when(placeRepo.findPlacesByMapsBounds(
                        mapBoundsDto.getNorthEastLat(),
                        mapBoundsDto.getNorthEastLng(),
                        mapBoundsDto.getSouthWestLat(),
                        mapBoundsDto.getSouthWestLng()))
                .thenReturn(placeExpected);
        assertEquals(
                placeExpected.size(), placeService.findPlacesByMapsBounds(mapBoundsDto).size());
    }
}
