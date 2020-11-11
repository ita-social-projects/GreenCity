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
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitStatusRepo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.dom4j.rule.Mode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class HabitStatusServiceImplTest {
    @Mock
    private HabitStatusRepo habitStatusRepo;
    @Mock
    private HabitStatusCalendarService habitStatusCalendarService;
    @Mock
    private HabitAssignRepo habitAssignRepo;
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
        when(habitStatusRepo.findById(habitAssignId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
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
        when(habitStatusRepo.findByHabitAssignId(habitAssignId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            habitStatusService.findStatusByHabitAssignId(habitAssignId);
        });
    }

    @Test
    void findStatusByHabitIdAndUserIdFound() {
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        when(habitStatusRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusDto.class)).thenReturn(new HabitStatusDto());
        HabitStatusDto actual = habitStatusService.findActiveStatusByHabitIdAndUserId(1L, 1L);
        HabitStatusDto expected = new HabitStatusDto();

        assertEquals(expected, actual);
    }

    @Test
    void findStatusByHabitIdAndUserIdNotFound() {
        when(habitStatusRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            habitStatusService.findActiveStatusByHabitIdAndUserId(1L, 1L);
        });
    }

    @Test
    void enrollHabit() {
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusDto habitStatusDto = ModelUtils.getHabitStatusDto();
        HabitStatusCalendarVO habitStatusCalendarVO = ModelUtils.getHabitStatusCalendarVO();
        HabitStatusVO habitStatusVO = ModelUtils.getHabitStatusVO();

        when(habitStatusRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.of(habitStatus));


        when(habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatusVO))
            .thenReturn(LocalDate.of(2020, 10, 15));
        when(modelMapper.map(habitStatus, HabitStatusVO.class)).thenReturn(habitStatusVO);

        when(modelMapper.map(any(), eq(HabitStatusCalendarVO.class))).thenReturn(habitStatusCalendarVO);

        when(habitStatusRepo.save(any())).thenReturn(habitStatus);
        when(modelMapper.map(habitStatus, HabitStatusDto.class)).thenReturn(habitStatusDto);

        habitStatusService.enrollHabit(1L, 1L);
        verify(habitStatusRepo).save(habitStatus);
    }

    @Test
    void enrollHabitThrowWrongIdException() {
        when(habitStatusRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> habitStatusService.enrollHabit(1L, 1L));
    }

    @Test
    void enrollHabitThrowBadRequestException() {
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusVO habitStatusVO = ModelUtils.getHabitStatusVO();
        when(habitStatusRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.of(habitStatus));

        when(modelMapper.map(habitStatus, HabitStatusVO.class)).thenReturn(habitStatusVO);
        when(habitStatusCalendarService.findTopByEnrollDateAndHabitStatus(habitStatusVO))
            .thenReturn(LocalDate.now());

        assertThrows(BadRequestException.class, () -> {
            habitStatusService.enrollHabit(1L, 1L);
        });
    }

    @Test
    void enrollHabitInDate() {
        LocalDate now = LocalDate.now();
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusVO habitStatusVO = ModelUtils.getHabitStatusVO();
        when(habitStatusRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusVO.class)).thenReturn(habitStatusVO);

        habitStatusService.enrollHabitInDate(1L, 1L, now);

        verify(habitStatusRepo).save(habitStatus);
    }

    @Test
    void enrollHabitInDateThrowBadRequestException() {
        LocalDate enrollDate = LocalDate.now();
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusVO habitStatusVO = ModelUtils.getHabitStatusVO();
        when(habitStatusRepo.findByHabitIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusVO.class)).thenReturn(habitStatusVO);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(enrollDate, habitStatusVO))
            .thenReturn(new HabitStatusCalendarVO());

        assertThrows(BadRequestException.class, () -> habitStatusService.enrollHabitInDate(1L, 1L, enrollDate));
    }

    @Test
    void enrollHabitInDateThrowWrongIdException() {
        LocalDate enrollDate = LocalDate.now();
        when(habitStatusRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            habitStatusService.enrollHabitInDate(1L, 1L, enrollDate);
        });
    }

    @Test
    void unenrollHabit() {
        LocalDate enrollDate = LocalDate.now();
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusVO habitStatusVO = ModelUtils.getHabitStatusVO();
        HabitStatusCalendarVO habitStatusCalendarVO = HabitStatusCalendarVO.builder()
            .enrollDate(enrollDate).build();
        when(habitStatusRepo.findByHabitIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusVO.class)).thenReturn(habitStatusVO);
        when(habitStatusRepo.save(habitStatus)).thenReturn(habitStatus);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(enrollDate, habitStatusVO))
            .thenReturn(habitStatusCalendarVO);

        habitStatusService.unenrollHabit(1L, 1L, enrollDate);
        verify(habitStatusCalendarService).delete(habitStatusCalendarVO);
    }

    @Test
    void unenrollHabitThrowBadRequestException() {
        LocalDate enrollDate = LocalDate.now();
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        HabitStatusVO habitStatusVO = ModelUtils.getHabitStatusVO();
        when(habitStatusRepo.findByHabitIdAndUserId(1L, 1L))
            .thenReturn(Optional.of(habitStatus));
        when(modelMapper.map(habitStatus, HabitStatusVO.class)).thenReturn(habitStatusVO);
        when(habitStatusRepo.save(habitStatus)).thenReturn(habitStatus);
        when(habitStatusCalendarService.findHabitStatusCalendarByEnrollDateAndHabitStatus(enrollDate, habitStatusVO))
            .thenReturn(null);

        assertThrows(BadRequestException.class, () -> {
            habitStatusService.unenrollHabit(1L, 1L, enrollDate);
        });
    }

    @Test
    void unenrollHabitThrowWrongIdException() {
        LocalDate enrollDate = LocalDate.now();
        when(habitStatusRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            habitStatusService.unenrollHabit(1L, 1L, enrollDate);
        });
    }

    @Test
    void update() {
        UpdateHabitStatusDto updateHabitStatusDto = new UpdateHabitStatusDto(1, 1, LocalDateTime.now());
        HabitStatus habitStatus = ModelUtils.getHabitStatus();
        when(habitStatusRepo.findByHabitIdAndUserId(1L, 1L)).thenReturn(Optional.of(habitStatus));
        when(habitStatusRepo.save(habitStatus)).thenReturn(habitStatus);
        HabitStatusDto expected = HabitStatusDto.builder()
            .habitStreak(habitStatus.getHabitStreak())
            .workingDays(habitStatus.getWorkingDays())
            .lastEnrollmentDate(habitStatus.getLastEnrollmentDate()).build();
        when(modelMapper.map(habitStatus, HabitStatusDto.class)).thenReturn(expected);
        HabitStatusDto actual = habitStatusService.update(1L, 1L, updateHabitStatusDto);

        assertEquals(expected, actual);
    }
}
