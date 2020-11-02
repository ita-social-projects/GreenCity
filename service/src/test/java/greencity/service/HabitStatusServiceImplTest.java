package greencity.service;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habitstatus.HabitStatusVO;
import greencity.dto.habitstatus.UpdateHabitStatusDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.HabitStatusRepo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class HabitStatusServiceImplTest {
    @Mock
    private HabitStatusRepo habitStatusRepo;
    @Mock
    private HabitStatusCalendarService habitStatusCalendarService;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    HabitStatusServiceImpl habitStatusService;

    @Test
    void getByIdFound() {
        Long habitAssignId = 1L;
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        when(habitStatusRepo.findById(habitAssignId)).thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusDto.class)).thenReturn(new HabitStatusDto());
        HabitStatusDto actual = habitStatusService.getById(habitAssignId);
        HabitStatusDto expected = new HabitStatusDto();

        assertEquals(expected, actual);
    }

    @Test
    void getByIdNotFound() {
        Long habitAssignId = 1L;
        when(habitStatusRepo.findById(habitAssignId)).thenThrow(WrongIdException.class);

        assertThrows(WrongIdException.class, () -> {
            habitStatusService.getById(habitAssignId);
        });
    }

    @Test
    void saveStatusByHabitAssign() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();

        HabitStatus habitStatus = new HabitStatus();
        habitStatus.setHabitStreak(0);
        habitStatus.setWorkingDays(0);
        habitStatus.setHabitAssign(habitAssign);
        habitStatus.setLastEnrollmentDate(LocalDateTime.now());
        HabitAssignVO habitAssignVO = ModelUtils.getHabitAssignVO();

        when(habitStatusRepo.save(any())).thenReturn(habitStatus);
        when(modelMapper.map(habitAssignVO, HabitAssign.class)).thenReturn(habitAssign);
        habitStatusService.saveStatusByHabitAssign(habitAssignVO);

        verify(habitStatusRepo).save(any(HabitStatus.class));
    }

    @Test
    void findStatusByHabitAssignIdFound() {
        Long habitAssignId = 1L;
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusDto.class)).thenReturn(new HabitStatusDto());
        HabitStatusDto actual = habitStatusService.findStatusByHabitAssignId(habitAssignId);
        HabitStatusDto expected = new HabitStatusDto();

        assertEquals(expected, actual);
    }

    @Test
    void findStatusByHabitAssignIdNotFound() {
        Long habitAssignId = 1L;
        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenThrow(WrongIdException.class);

        assertThrows(WrongIdException.class, () -> {
            habitStatusService.findStatusByHabitAssignId(habitAssignId);
        });
    }

    @Test
    void enrollHabit() {
        Long habitAssignId = 1L;
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusDto habitStatusDto = ModelUtils.getHabitStatusDto();
        HabitStatusCalendarVO habitStatusCalendarVO = ModelUtils.getHabitStatusCalendarVO();
        HabitStatusVO habitStatusVO = modelMapper.map(habitStatus, HabitStatusVO.class);

        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenReturn(Optional.of(habitStatus));
        when(habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatusVO))
            .thenReturn(LocalDate.of(2020, 10, 15));
        when(modelMapper.map(habitStatus, HabitStatusVO.class)).thenReturn(habitStatusVO);

        when(modelMapper.map(any(), eq(HabitStatusCalendarVO.class))).thenReturn(habitStatusCalendarVO);

        when(habitStatusRepo.save(any())).thenReturn(habitStatus);
        when(modelMapper.map(habitStatus, HabitStatusDto.class)).thenReturn(habitStatusDto);

        habitStatusService.enrollHabit(habitAssignId);
        verify(habitStatusRepo).save(habitStatus);
    }

    @Test
    void enrollHabitThrowWrongIdException() {
        Long habitAssignId = 1L;
        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenThrow(WrongIdException.class);

        assertThrows(WrongIdException.class, () -> habitStatusService.enrollHabit(habitAssignId));
    }

    @Test
    void enrollHabitThrowBadRequestException() {
        Long habitAssignId = 1L;
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusVO habitStatusVO = ModelUtils.getHabitStatusVO();
        when(modelMapper.map(habitStatus, HabitStatusVO.class)).thenReturn(habitStatusVO);
        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenReturn(Optional.of(habitStatus));
        when(habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatusVO))
            .thenReturn(LocalDate.of(2100, 10, 16));

        assertThrows(BadRequestException.class, () -> {
            habitStatusService.enrollHabit(habitAssignId);
        });
    }

    @Test
    void enrollHabitInDate() {
        Long habitAssignId = 1L;
        LocalDate now = LocalDate.now();
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusVO habitStatusVO = ModelUtils.getHabitStatusVO();
        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusVO.class)).thenReturn(habitStatusVO);

        habitStatusService.enrollHabitInDate(habitAssignId, now);

        verify(habitStatusRepo).save(habitStatus);
    }

    @Test
    void enrollHabitInDateThrowBadRequestException() {
        Long habitAssignId = 1L;
        LocalDate enrollDate = LocalDate.now();
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusVO habitStatusVO = ModelUtils.getHabitStatusVO();
        when(habitStatusRepo.findByHabitAssignId(habitAssignId))
            .thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusVO.class)).thenReturn(habitStatusVO);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(enrollDate, habitStatusVO))
            .thenReturn(new HabitStatusCalendarVO());

        assertThrows(BadRequestException.class, () -> habitStatusService.enrollHabitInDate(habitAssignId, enrollDate));
    }

    @Test
    void enrollHabitInDateThrowWrongIdException() {
        Long habitAssignId = 1L;
        LocalDate enrollDate = LocalDate.now();
        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenThrow(WrongIdException.class);

        assertThrows(WrongIdException.class, () -> {
            habitStatusService.enrollHabitInDate(habitAssignId, enrollDate);
        });
    }

    @Test
    void unenrollHabit() {
        Long habitAssignId = 1L;
        LocalDate enrollDate = LocalDate.now();
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusVO habitStatusVO = ModelUtils.getHabitStatusVO();
        HabitStatusCalendarVO habitStatusCalendarVO = HabitStatusCalendarVO.builder()
            .enrollDate(enrollDate).build();
        when(habitStatusRepo.findByHabitAssignId(habitAssignId))
            .thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusVO.class)).thenReturn(habitStatusVO);
        when(habitStatusRepo.save(habitStatus)).thenReturn(habitStatus);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(enrollDate, habitStatusVO))
            .thenReturn(habitStatusCalendarVO);

        habitStatusService.unenrollHabit(enrollDate, habitAssignId);
        verify(habitStatusCalendarService).delete(habitStatusCalendarVO);
    }

    @Test
    void unenrollHabitThrowBadRequestException() {
        Long habitAssignId = 1L;
        LocalDate enrollDate = LocalDate.now();
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusVO habitStatusVO = ModelUtils.getHabitStatusVO();
        when(habitStatusRepo.findByHabitAssignId(habitAssignId))
            .thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusVO.class)).thenReturn(habitStatusVO);
        when(habitStatusRepo.save(habitStatus)).thenReturn(habitStatus);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(enrollDate, habitStatusVO))
            .thenReturn(null);

        assertThrows(BadRequestException.class, () -> {
            habitStatusService.unenrollHabit(enrollDate, habitAssignId);
        });
    }

    @Test
    void unenrollHabitThrowWrongIdException() {
        Long habitAssignId = 1L;
        LocalDate enrollDate = LocalDate.now();
        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenThrow(WrongIdException.class);

        assertThrows(WrongIdException.class, () -> {
            habitStatusService.unenrollHabit(enrollDate, habitAssignId);
        });
    }

    @Test
    void update() {
        Long habitAssignId = 1L;
        UpdateHabitStatusDto updateHabitStatusDto = new UpdateHabitStatusDto(1, 1, LocalDateTime.now());
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenReturn(Optional.of(habitStatus));
        when(habitStatusRepo.save(habitStatus)).thenReturn(habitStatus);
        HabitStatusDto expected = HabitStatusDto.builder()
            .habitStreak(habitStatus.getHabitStreak())
            .workingDays(habitStatus.getWorkingDays())
            .lastEnrollmentDate(habitStatus.getLastEnrollmentDate()).build();
        when(modelMapper.map(habitStatus, HabitStatusDto.class)).thenReturn(expected);
        HabitStatusDto actual = habitStatusService.update(habitAssignId, updateHabitStatusDto);

        assertEquals(expected, actual);
    }
}
