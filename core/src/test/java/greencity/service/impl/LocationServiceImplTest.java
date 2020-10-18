package greencity.service.impl;

import greencity.entity.Location;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.LocationRepo;
import greencity.service.LocationServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {
//    @Mock
//    private LocationRepo locationRepo;
//    @InjectMocks
//    private LocationServiceImpl locationService;
//
//    private Location genericEntity = new Location();
//
//    @Test
//    void saveTest() {
//        when(locationRepo.save(genericEntity)).thenReturn(genericEntity);
//
//        assertEquals(genericEntity, locationService.save(genericEntity));
//    }
//
//    @Test
//    void findByIdTest() {
//        when(locationRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
//
//        Location foundEntity = locationService.findById(anyLong());
//
//        assertEquals(genericEntity, foundEntity);
//    }
//
//    @Test
//    void findByIdGivenIdNullThenThrowException() {
//        Assertions
//            .assertThrows(NotFoundException.class,
//                () -> locationService.findById(null));
//    }
//
//    @Test
//    void updateTest() {
//        when(locationRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
//        when(locationRepo.save(any())).thenReturn(genericEntity);
//
//        locationService.update(anyLong(), genericEntity);
//        Location foundEntity = locationService.findById(anyLong());
//
//        assertEquals(genericEntity, foundEntity);
//    }
//
//    @Test
//    void updateGivenIdNullThenThrowException() {
//        Assertions
//            .assertThrows(NotFoundException.class,
//                () -> locationService.update(null, genericEntity));
//    }
//
//    @Test
//    void deleteByIdTest() {
//        when(locationRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
//
//        assertEquals(new Long(1), locationService.deleteById(1L));
//    }
//
//    @Test
//    void deleteByIdGivenIdNullThenThrowException() {
//        Assertions
//            .assertThrows(NotFoundException.class,
//                () -> locationService.deleteById(null));
//    }
//
//    @Test
//    void findAllTest() {
//        List<Location> genericEntities =
//            new ArrayList<>(Arrays.asList(new Location(), new Location()));
//
//        when(locationRepo.findAll()).thenReturn(genericEntities);
//
//        List<Location> foundEntities = locationService.findAll();
//
//        assertEquals(genericEntities, foundEntities);
//    }
}
