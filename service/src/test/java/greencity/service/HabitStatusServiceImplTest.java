package greencity.service;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habitstatus.UpdateHabitStatusDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarVO;
import greencity.entity.*;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.HabitStatusRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        HabitAssignDto habitAssignDto = ModelUtils.getHabitAssignDto();

        when(habitStatusRepo.save(any())).thenReturn(habitStatus);
        when(modelMapper.map(habitAssignDto, HabitAssign.class)).thenReturn(habitAssign);
        habitStatusService.saveStatusByHabitAssign(habitAssignDto);
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
        HabitStatusCalendar habitStatusCalendar = new HabitStatusCalendar(LocalDate.now(), habitStatus);
        HabitStatusDto habitStatusDto = modelMapper.map(habitStatus, HabitStatusDto.class);
        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenReturn(Optional.of(habitStatus));
        when(habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatusDto))
                .thenReturn(LocalDate.of(2020, 10, 15));
        HabitStatusCalendarVO habitStatusCalendarVO = modelMapper.map(habitStatusCalendar, HabitStatusCalendarVO.class);
        habitStatusService.enrollHabit(habitAssignId);

        verify(habitStatusCalendarService).save(habitStatusCalendarVO);
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
        HabitStatusDto habitStatusDto = ModelUtils.getHabitStatusDto();
        when(modelMapper.map(habitStatus, HabitStatusDto.class)).thenReturn(habitStatusDto);
        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenReturn(Optional.of(habitStatus));
        when(habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatusDto))
                .thenReturn(LocalDate.of(2100, 10, 16));

        assertThrows(BadRequestException.class, () -> {
            habitStatusService.enrollHabit(habitAssignId);
        });
    }

    @Test
    void enrollHabitInDate() {
        Long habitAssignId = 1L;
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusCalendar habitStatusCalendar = new HabitStatusCalendar(LocalDate.now(), habitStatus);
        HabitStatusDto habitStatusDto = modelMapper.map(habitStatus, HabitStatusDto.class);
        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenReturn(Optional.of(habitStatus));
        when(habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatusDto))
                .thenReturn(null);
        HabitStatusCalendarVO habitStatusCalendarVO = modelMapper.map(habitStatusCalendar, HabitStatusCalendarVO.class);
        habitStatusService.enrollHabit(habitAssignId);

        verify(habitStatusCalendarService).save(habitStatusCalendarVO);
        verify(habitStatusRepo).save(habitStatus);
    }

    @Test
    void enrollHabitInDateThrowBadRequestException() {
        Long habitAssignId = 1L;
        LocalDate enrollDate = LocalDate.now();
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusDto habitStatusDto = ModelUtils.getHabitStatusDto();
        when(habitStatusRepo.findByHabitAssignId(habitAssignId))
                .thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusDto.class)).thenReturn(habitStatusDto);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(enrollDate, habitStatusDto))
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
        HabitStatusDto habitStatusDto = ModelUtils.getHabitStatusDto();
        HabitStatusCalendarVO habitStatusCalendarVO = HabitStatusCalendarVO.builder()
                .habitStatus(habitStatusDto).enrollDate(enrollDate).build();
        when(habitStatusRepo.findByHabitAssignId(habitAssignId))
                .thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusDto.class)).thenReturn(habitStatusDto);
        when(habitStatusRepo.save(habitStatus)).thenReturn(habitStatus);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(enrollDate, habitStatusDto))
                .thenReturn(habitStatusCalendarVO);
        habitStatusService.unenrollHabit(enrollDate, habitAssignId);

        verify(habitStatusCalendarService).delete(habitStatusCalendarVO);
    }

    @Test
    void unenrollHabitThrowBadRequestException() {
        Long habitAssignId = 1L;
        LocalDate enrollDate = LocalDate.now();
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusDto habitStatusDto = ModelUtils.getHabitStatusDto();
        when(habitStatusRepo.findByHabitAssignId(habitAssignId))
                .thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusDto.class)).thenReturn(habitStatusDto);
        when(habitStatusRepo.save(habitStatus)).thenReturn(habitStatus);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(enrollDate, habitStatusDto))
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
