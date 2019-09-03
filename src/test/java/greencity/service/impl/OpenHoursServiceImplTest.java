package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.entity.OpeningHours;
import greencity.exception.NotFoundException;
import greencity.repository.OpenHoursRepo;
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
public class OpenHoursServiceImplTest {
    @Mock
    private OpenHoursRepo openHoursRepo;
    @InjectMocks
    private OpenHoursServiceImpl openHoursService;

    @Test
    public void saveTest() {
        OpeningHours genericEntity = new OpeningHours();

        when(openHoursRepo.save(genericEntity)).thenReturn(genericEntity);

        assertEquals(genericEntity, openHoursService.save(genericEntity));
    }

    @Test
    public void findByIdTest() {
        OpeningHours genericEntity = new OpeningHours();

        when(openHoursRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        OpeningHours foundEntity = openHoursService.findById(anyLong());

        assertEquals(genericEntity, foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdGivenIdNullThenThrowException() {
        openHoursService.findById(null);
    }

    @Test
    public void updateTest() {
        OpeningHours updated = new OpeningHours();

        when(openHoursRepo.findById(anyLong())).thenReturn(Optional.of(updated));
        when(openHoursRepo.save(any())).thenReturn(updated);

        openHoursService.update(anyLong(), updated);
        OpeningHours foundEntity = openHoursService.findById(anyLong());

        assertEquals(updated, foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void updateGivenIdNullThenThrowException() {
        openHoursService.update(null, new OpeningHours());
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdGivenIdNullThenThrowException() {
        openHoursService.deleteById(null);
    }

    @Test
    public void findAllTest() {
        List<OpeningHours> genericEntities =
            new ArrayList<>(Arrays.asList(new OpeningHours(), new OpeningHours()));

        when(openHoursRepo.findAll()).thenReturn(genericEntities);

        List<OpeningHours> foundEntities = openHoursService.findAll();

        assertEquals(genericEntities, foundEntities);
    }
}
