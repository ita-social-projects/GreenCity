package greencity.service;

import greencity.dto.breaktime.BreakTimeVO;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BreakTimeServiceImplTest {
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BreakTimeRepo breakTimeRepo;

    @InjectMocks
    private BreakTimeServiceImpl breakTimeService;

    private final BreakTime validBreakTime = BreakTime.builder()
        .id(13L)
        .startTime(LocalTime.of(8, 0))
        .endTime(LocalTime.of(19, 0))
        .build();

    private final BreakTime notValidBreakTime = BreakTime.builder()
        .id(13L)
        .startTime(LocalTime.of(12, 0))
        .endTime(LocalTime.of(10, 30))
        .build();

    private final BreakTimeVO breakTimeVO = BreakTimeVO.builder()
        .id(13L)
        .startTime(LocalTime.of(8, 0))
        .endTime(LocalTime.of(19, 0))
        .build();

    private final BreakTimeVO notValidBreakTimeVO = BreakTimeVO.builder()
        .id(13L)
        .startTime(LocalTime.of(12, 0))
        .endTime(LocalTime.of(10, 30))
        .build();

    @Test
    void saveValidBreakTime() {
        when(breakTimeRepo.save(any())).thenReturn(validBreakTime);
        when(modelMapper.map(validBreakTime, BreakTimeVO.class)).thenReturn(breakTimeVO);
        BreakTimeVO save = breakTimeService.save(breakTimeVO);
        assertEquals(breakTimeVO, save);
    }

    @Test
    void saveNotValidBreakTime() {
        Assertions
            .assertThrows(BadRequestException.class,
                () -> breakTimeService.save(notValidBreakTimeVO));
    }

    @Test
    void findByExistingId() {
        when(breakTimeRepo.findById(validBreakTime.getId())).thenReturn(Optional.of(validBreakTime));
        when(modelMapper.map(validBreakTime, BreakTimeVO.class)).thenReturn(breakTimeVO);
        BreakTimeVO byId = breakTimeService.findById(validBreakTime.getId());
        assertEquals(breakTimeVO, byId);
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
        when(modelMapper.map(validBreakTime, BreakTimeVO.class)).thenReturn(breakTimeVO);
        when(modelMapper.map(breakTimeVO, BreakTime.class)).thenReturn(validBreakTime);

        assertEquals(validBreakTime.getId(), breakTimeService.deleteById(validBreakTime.getId()));
        verify(breakTimeRepo).delete(validBreakTime);
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
        List<BreakTimeVO> expectedVO = Arrays.asList(breakTimeVO, notValidBreakTimeVO);
        when(breakTimeRepo.findAll()).thenReturn(expected);
        when(modelMapper.map(expected, new TypeToken<List<BreakTimeVO>>() {
        }.getType())).thenReturn(expectedVO);
        assertEquals(expectedVO, breakTimeService.findAll());
    }

    @Test
    void findAllEmpty() {
        List<BreakTime> expected = Collections.emptyList();
        List<BreakTimeVO> expectedVO = Collections.emptyList();

        when(breakTimeRepo.findAll()).thenReturn(expected);
        when(modelMapper.map(expected, new TypeToken<List<BreakTimeVO>>() {
        }.getType())).thenReturn(expectedVO);
        assertEquals(expectedVO, breakTimeService.findAll());
    }
}