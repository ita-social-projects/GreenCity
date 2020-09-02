package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.entity.Location;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.LocationRepo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class LocationServiceImplTest {
    @Mock
    private LocationRepo locationRepo;
    @InjectMocks
    private LocationServiceImpl locationService;

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

    @Test
    public void deleteByIdTest() {
        when(locationRepo.findById(anyLong())).thenReturn(Optional.of(new Location()));

        assertEquals(new Long(1), locationService.deleteById(1L));
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
