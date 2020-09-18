package greencity.service.impl;

import greencity.entity.BreakTime;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.OpenHoursRepo;
import greencity.service.BreakTimeService;
import java.time.LocalTime;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
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

    @Test
    public void saveTestWithOpeningHoursBiggerThanEndOpeningHours_ThrowException() {
        OpeningHours openingHours = OpeningHours.builder()
            .openTime(LocalTime.of(20, 0))
            .closeTime(LocalTime.of(9, 0))
            .build();
        Assertions
            .assertThrows(BadRequestException.class,
                () -> openHoursService.save(openingHours));
    }

    @Test
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
        Assertions
            .assertThrows(BadRequestException.class,
                () -> openHoursService.save(openingHours));
    }

    @Test
    public void findByIdTest() {
        OpeningHours genericEntity = new OpeningHours();

        when(openHoursRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        OpeningHours foundEntity = openHoursService.findById(anyLong());

        assertEquals(genericEntity, foundEntity);
    }

    @Test
    public void findByIdGivenIdNullThenThrowException() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> openHoursService.findById(null));
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

    @Test
    public void updateGivenIdNullThenThrowException() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> openHoursService.update(null, new OpeningHours()));
    }

    @Test
    public void deleteByIdTest() {
        when(openHoursRepo.findById(anyLong())).thenReturn(Optional.of(new OpeningHours()));

        assertEquals(new Long(1), openHoursService.deleteById(1L));
    }

    @Test
    public void deleteByIdGivenIdNullThenThrowException() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> openHoursService.deleteById(null));

    }

    @Test
    public void findAllTest() {
        List<OpeningHours> genericEntities =
            new ArrayList<>(Arrays.asList(new OpeningHours(), new OpeningHours()));

        when(openHoursRepo.findAll()).thenReturn(genericEntities);

        List<OpeningHours> foundEntities = openHoursService.findAll();

        assertEquals(genericEntities, foundEntities);
    }

    @Test
    public void getOpenHoursByPlaceTest() {
        Place place = new Place();
        List<OpeningHours> genericOpeningHours = Arrays.asList(new OpeningHours(), new OpeningHours());

        when(openHoursRepo.findAllByPlace(any())).thenReturn(genericOpeningHours);

        List<OpeningHours> foundOpeningHours = openHoursService.getOpenHoursByPlace(place);
        assertEquals(genericOpeningHours, foundOpeningHours);
        Mockito.verify(openHoursRepo, times(1)).findAllByPlace(any());
    }

    @Test
    public void findAllByPlaceIdTest() {
        Set<OpeningHours> genericOpeningHours = new HashSet<>();
        genericOpeningHours.add(new OpeningHours());
        genericOpeningHours.add(new OpeningHours());

        when(openHoursRepo.findAllByPlaceId(anyLong())).thenReturn(genericOpeningHours);

        Set<OpeningHours> foundOpeningHours = openHoursService.findAllByPlaceId(anyLong());
        assertEquals(foundOpeningHours, genericOpeningHours);
        Mockito.verify(openHoursRepo, times(1)).findAllByPlaceId(any());
    }
}
