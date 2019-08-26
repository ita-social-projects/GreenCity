package greencity.service.impl;

import greencity.dto.place.PlaceAddDto;
import greencity.entity.*;
import greencity.GreenCityApplication;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.exception.BadIdException;
import greencity.mapping.PlaceAddDtoMapper;
import greencity.repository.*;
import greencity.service.*;
import org.junit.Assert;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.repository.PlaceRepo;
import greencity.service.PlaceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class PlaceServiceImplTest {

    @MockBean private PlaceRepo placeRepo;

    @MockBean private ModelMapper modelMapper;

    @MockBean private CategoryService categoryService;

    @MockBean private LocationService locationService;

    @MockBean private OpenHoursService openHoursService;

    @MockBean private PlaceAddDtoMapper placeAddDtoMapper;

    @MockBean
    private UserService userService;

    @Autowired private PlaceService placeService;

    @Test
    public  void saveTest() {

    }

    @Test
    public void deleteByIdTest() {
        Place placeToDelete = new Place();
        Mockito.when(placeRepo.findById(1L)).thenReturn(Optional.of(placeToDelete));

        Assert.assertEquals(true, placeService.deleteById(1L));
    }

    @Test(expected = BadIdException.class)
    public void findByIdBadIdTest() {
        when(placeRepo.findById(any())).thenThrow(BadIdException.class);
        placeService.findById(1L);
    }

    @Test
    public void updateStatusTest() {
        Place genericEntity = Place.builder().id(1L).status(PlaceStatus.PROPOSED).build();

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        when(placeRepo.save(any())).thenReturn(genericEntity);

        placeService.updateStatus(genericEntity.getId(), PlaceStatus.DECLINED);

        assertEquals(PlaceStatus.DECLINED, genericEntity.getStatus());
    }

    @Test(expected = PlaceStatusException.class)
    public void updateStatusGivenTheSameStatusThenThrowException() {
        Place genericEntity = Place.builder().status(PlaceStatus.PROPOSED).build();

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        when(placeRepo.save(any())).thenReturn(genericEntity);

        placeService.updateStatus(anyLong(), PlaceStatus.PROPOSED);
    }

    @Test(expected = NotFoundException.class)
    public void updateStatusGivenPlaceIdNullThenThrowException() {
        Place genericEntity = Place.builder().status(PlaceStatus.PROPOSED).build();

        when(placeRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        placeService.updateStatus(null, PlaceStatus.DECLINED);
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
