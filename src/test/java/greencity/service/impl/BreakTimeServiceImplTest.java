package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import greencity.entity.BreakTime;
import greencity.exception.BadRequestException;
import greencity.exception.NotFoundException;
import greencity.repository.BreakTimeRepo;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BreakTimeServiceImplTest {
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
    public void saveValidBreakTime() {
        when(breakTimeRepo.save(validBreakTime)).thenReturn(validBreakTime);

        assertEquals(validBreakTime, breakTimeService.save(validBreakTime));
    }

    @Test(expected = BadRequestException.class)
    public void saveNotValidBreakTime() {
        breakTimeService.save(notValidBreakTime);
    }

    @Test
    public void findByExistingId() {
        when(breakTimeRepo.findById(validBreakTime.getId())).thenReturn(Optional.of(validBreakTime));

        assertEquals(validBreakTime, breakTimeService.findById(validBreakTime.getId()));
    }

    @Test(expected = NotFoundException.class)
    public void findByNotExistingId() {
        when(breakTimeRepo.findById(anyLong())).thenReturn(Optional.empty());

        breakTimeService.findById(13L);
    }

    @Test
    public void deleteByExistingId() {
        when(breakTimeRepo.findById(validBreakTime.getId())).thenReturn(Optional.of(validBreakTime));

        assertEquals(validBreakTime.getId(), breakTimeService.deleteById(validBreakTime.getId()));
        verify(breakTimeRepo, times(1)).delete(validBreakTime);
    }

    @Test(expected = NotFoundException.class)
    public void deleteByNotExistingId() {
        when(breakTimeRepo.findById(anyLong())).thenReturn(Optional.empty());

        breakTimeService.deleteById(13L);
    }

    @Test
    public void findAll() {
        List<BreakTime> expected = Arrays.asList(validBreakTime, notValidBreakTime);

        when(breakTimeRepo.findAll()).thenReturn(expected);
        assertEquals(expected, breakTimeService.findAll());
    }

    @Test
    public void findAllEmpty() {
        List<BreakTime> expected = Collections.emptyList();

        when(breakTimeRepo.findAll()).thenReturn(expected);
        assertEquals(expected, breakTimeService.findAll());
    }
}