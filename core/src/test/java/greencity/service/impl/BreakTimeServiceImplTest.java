package greencity.service.impl;

import greencity.entity.BreakTime;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.BreakTimeRepo;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BreakTimeServiceImplTest {
    @Mock
    private BreakTimeRepo breakTimeRepo;

    @InjectMocks
    private BreakTimeServiceImpl breakTimeService;

    private BreakTime validBreakTime = BreakTime.builder()
        .id(13L)
        .startTime(LocalTime.of(8, 0))
        .endTime(LocalTime.of(19, 0))
        .build();

    private BreakTime notValidBreakTime = BreakTime.builder()
        .id(13L)
        .startTime(LocalTime.of(12, 0))
        .endTime(LocalTime.of(10, 30))
        .build();

    @Test
    void saveValidBreakTime() {
        when(breakTimeRepo.save(validBreakTime)).thenReturn(validBreakTime);

        assertEquals(validBreakTime, breakTimeService.save(validBreakTime));
    }

    @Test
    void saveNotValidBreakTime() {
        Assertions
            .assertThrows(BadRequestException.class,
                () -> breakTimeService.save(notValidBreakTime));
    }

    @Test
    void findByExistingId() {
        when(breakTimeRepo.findById(validBreakTime.getId())).thenReturn(Optional.of(validBreakTime));

        assertEquals(validBreakTime, breakTimeService.findById(validBreakTime.getId()));
    }

    @Test
    void findByNotExistingId() {
        when(breakTimeRepo.findById(anyLong())).thenReturn(Optional.empty());
        Assertions
            .assertThrows(NotFoundException.class,
                () -> breakTimeService.findById(13L));
    }

    @Test
    void deleteByExistingId() {
        when(breakTimeRepo.findById(validBreakTime.getId())).thenReturn(Optional.of(validBreakTime));

        assertEquals(validBreakTime.getId(), breakTimeService.deleteById(validBreakTime.getId()));
        verify(breakTimeRepo, times(1)).delete(validBreakTime);
    }

    @Test
    void deleteByNotExistingId() {
        when(breakTimeRepo.findById(anyLong())).thenReturn(Optional.empty());
        Assertions
            .assertThrows(NotFoundException.class,
                () -> breakTimeService.deleteById(13L));
    }

    @Test
    void findAll() {
        List<BreakTime> expected = Arrays.asList(validBreakTime, notValidBreakTime);

        when(breakTimeRepo.findAll()).thenReturn(expected);
        assertEquals(expected, breakTimeService.findAll());
    }

    @Test
    void findAllEmpty() {
        List<BreakTime> expected = Collections.emptyList();

        when(breakTimeRepo.findAll()).thenReturn(expected);
        assertEquals(expected, breakTimeService.findAll());
    }
}