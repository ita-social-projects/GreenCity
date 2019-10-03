package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.entity.BreakTime;
import greencity.entity.OpeningHours;
import greencity.exception.BadRequestException;
import greencity.exception.NotFoundException;
import greencity.repository.OpenHoursRepo;
import greencity.service.BreakTimeService;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
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
    @Mock
    private BreakTimeService breakTimeService;
    @InjectMocks
    private OpenHoursServiceImpl openHoursService;

    @Test
    public void saveTestWOBreakTime() {
        OpeningHours openingHours = OpeningHours.builder()
            .openTime(LocalTime.of(9, 0))
            .closeTime(LocalTime.of(20, 0))
            .build();

        when(openHoursRepo.save(openingHours)).thenReturn(openingHours);

        assertEquals(openingHours, openHoursService.save(openingHours));
    }

    @Test
    public void saveTestWithBreakTime() {
         BreakTime breakTime = BreakTime.builder()
            .startTime(LocalTime.of(13, 0))
            .endTime(LocalTime.of(14, 0))
            .build();

        OpeningHours openingHours = OpeningHours.builder()
            .openTime(LocalTime.of(9, 0))
            .closeTime(LocalTime.of(20, 0))
            .breakTime(breakTime)
            .build();

        when(openHoursRepo.save(openingHours)).thenReturn(openingHours);
        when(breakTimeService.save(any(BreakTime.class))).thenReturn(breakTime);

        assertEquals(openingHours, openHoursService.save(openingHours));
    }

    @Test(expected = BadRequestException.class)
    public void saveTestWithOpeningHoursBiggerThanEndOpeningHours_ThrowException() {
        OpeningHours openingHours = OpeningHours.builder()
            .openTime(LocalTime.of(20, 0))
            .closeTime(LocalTime.of(9, 0))
            .build();

        openHoursService.save(openingHours);
    }

    @Test(expected = BadRequestException.class)
    public void saveTestWithStartBreakTimeBiggerThanEndBreakTime_ThrowException() {
         BreakTime breakTime = BreakTime.builder()
            .startTime(LocalTime.of(13, 0))
            .endTime(LocalTime.of(21, 0))
            .build();

        OpeningHours openingHours = OpeningHours.builder()
            .openTime(LocalTime.of(9, 0))
            .closeTime(LocalTime.of(20, 0))
            .breakTime(breakTime)
            .build();

       openHoursService.save(openingHours);
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

    @Test
    public void deleteByIdTest() {
        when(openHoursRepo.findById(anyLong())).thenReturn(Optional.of(new OpeningHours()));

        assertEquals(new Long(1), openHoursService.deleteById(1L));
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
