package greencity.service.impl;

import greencity.GreenCityApplication;
import greencity.entity.Location;
import greencity.exception.NotFoundException;
import greencity.repository.LocationRepo;
import greencity.service.LocationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class LocationServiceImplTest {
    @MockBean private LocationRepo locationRepo;
    @Autowired private LocationService locationService;

    @Test
    public void saveTest() {
        Location genericEntity = new Location();

        when(locationRepo.save(genericEntity)).thenReturn(genericEntity);

        assertEquals(genericEntity, locationService.save(genericEntity));
    }

    @Test
    public void findByIdTest() {
        Location genericEntity = new Location();

        when(locationRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        Location foundEntity = locationService.findById(anyLong());

        assertEquals(genericEntity, foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdGivenIdNullThenThrowException() {
        locationService.findById(null);
    }

    @Test
    public void updateTest() {
        Location updated = new Location();

        when(locationRepo.findById(anyLong())).thenReturn(Optional.of(updated));
        when(locationRepo.save(any())).thenReturn(updated);

        locationService.update(anyLong(), updated);
        Location foundEntity = locationService.findById(anyLong());

        assertEquals(updated, foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void updateGivenIdNullThenThrowException() {
        locationService.update(null, new Location());
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdThrowExceptionWhenCallFindById() {
        Location generic = new Location();

        when(locationRepo.findById(anyLong())).thenReturn(Optional.of(generic));
        when(locationService.findById(anyLong())).thenThrow(NotFoundException.class);

        locationService.deleteById(1L);
        locationService.findById(1L);
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdGivenIdNullThenThrowException() {
        locationService.deleteById(null);
    }

    @Test
    public void findAllTest() {
        List<Location> genericEntities =
                new ArrayList<>(Arrays.asList(new Location(), new Location()));

        when(locationRepo.findAll()).thenReturn(genericEntities);

        List<Location> foundEntities = locationService.findAll();

        assertEquals(genericEntities, foundEntities);
    }
}
