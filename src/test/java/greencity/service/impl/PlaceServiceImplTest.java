package greencity.service.impl;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.dto.location.LocationDto;
import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.entity.Place;
import greencity.repository.PlaceRepo;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class PlaceServiceImplTest {
    @MockBean
    private PlaceRepo placeRepo;
    @Autowired
    private PlaceServiceImpl placeService;

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
