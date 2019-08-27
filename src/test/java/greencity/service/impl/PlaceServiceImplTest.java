package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.dto.place.PlaceStatusDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.repository.PlaceRepo;
import greencity.service.PlaceService;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class PlaceServiceImplTest {
    @MockBean PlaceRepo placeRepo;
    @Autowired private PlaceService placeService;

    @Test
    public void updateStatusTest() {
        Place genericEntity = Place.builder().id(1L).status(PlaceStatus.PROPOSED).build();
        PlaceStatusDto genericDto = new PlaceStatusDto(1L, PlaceStatus.DECLINED);

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        when(placeRepo.save(any())).thenReturn(genericEntity);

        placeService.updateStatus(genericDto);

        assertEquals(PlaceStatus.DECLINED, genericEntity.getStatus());
    }

    @Test(expected = PlaceStatusException.class)
    public void updateStatusGivenTheSameStatusThenThrowException() {
        Place genericEntity = Place.builder().status(PlaceStatus.PROPOSED).build();
        PlaceStatusDto genericDto = new PlaceStatusDto(1L, PlaceStatus.PROPOSED);

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        when(placeRepo.save(any())).thenReturn(genericDto);

        placeService.updateStatus(genericDto);
    }

    @Test(expected = NotFoundException.class)
    public void updateStatusGivenPlaceIdNullThenThrowException() {
        Place genericEntity = Place.builder().status(PlaceStatus.PROPOSED).build();
        PlaceStatusDto genericDto = new PlaceStatusDto(null, PlaceStatus.DECLINED);

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        placeService.updateStatus(genericDto);
    }

    @Test
    public void findByIdTest() {
        Place genericEntity = new Place();

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        Place foundEntity = placeService.findById(anyLong());

        assertEquals(genericEntity, foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdGivenIdNullThenThrowException() {
        placeService.findById(null);
    }
}
