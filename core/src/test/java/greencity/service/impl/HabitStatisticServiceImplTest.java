package greencity.service.impl;

import greencity.converters.DateService;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.HabitItemsAmountStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.entity.HabitAssign;
import greencity.entity.HabitStatistic;
import greencity.entity.enums.HabitRate;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.NotSavedException;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitStatisticRepo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class HabitStatisticServiceImplTest {
    @Mock
    HabitAssignRepo habitAssignRepo;
    @Mock
    ModelMapper modelMapper;
    @Mock
    DateService dateService;
    @Mock
    private HabitStatisticRepo habitStatisticRepo;
    @InjectMocks
    private HabitStatisticServiceImpl habitStatisticService;

    private ZonedDateTime zonedDateTime = ZonedDateTime.now();

    private AddHabitStatisticDto addhs = AddHabitStatisticDto
        .builder().amountOfItems(10).habitRate(HabitRate.GOOD)
        .habitAssignId(1L).createDate(ZonedDateTime.now()).build();

    private HabitStatisticDto habitStatisticDto =
        HabitStatisticDto.builder().id(1L).amountOfItems(10).habitRate(HabitRate.GOOD)
            .habitAssignId(1L).createDate(zonedDateTime).build();

    private HabitStatistic habitStatistic = HabitStatistic.builder()
        .id(1L).habitRate(HabitRate.GOOD).createDate(zonedDateTime)
        .amountOfItems(10).habitAssign(null).build();

    private HabitAssign habitAssign = HabitAssign.builder().id(1L)
        .acquired(true).createDate(zonedDateTime)
        .suspended(true).habitStatistic(Collections.singletonList(habitStatistic)).build();

    private List<HabitStatistic> habitStatistics = Collections.singletonList(habitStatistic);

    private List<HabitStatisticDto> habitStatisticDtos = Collections.singletonList(habitStatisticDto);

    @Test
    void saveTest() {
        when(habitStatisticRepo.findHabitAssignStatByDate(addhs.getCreateDate(),
            addhs.getHabitAssignId())).thenReturn(Optional.empty());
        when(dateService.convertToDatasourceTimezone(addhs.getCreateDate())).thenReturn(zonedDateTime);
        when(modelMapper.map(addhs, HabitStatistic.class)).thenReturn(habitStatistic);
        when(habitAssignRepo.findById(addhs.getHabitAssignId())).thenReturn(Optional.of(habitAssign));
        when(habitStatisticRepo.save(habitStatistic)).thenReturn(habitStatistic);
        when(modelMapper.map(habitStatistic, HabitStatisticDto.class)).thenReturn(habitStatisticDto);

        HabitStatisticDto actual = habitStatisticService.save(addhs);
        assertEquals(habitStatisticDto, actual);
    }

    @Test
    void saveExceptionTest() {
        when(habitStatisticRepo.findHabitAssignStatByDate(addhs.getCreateDate(),
            addhs.getHabitAssignId())).thenReturn(Optional.of(new HabitStatistic()));
        assertThrows(NotSavedException.class, () ->
            habitStatisticService.save(addhs)
        );
    }

    @Test
    void saveExceptionBadRequestTest() {
        when(habitStatisticRepo.findHabitAssignStatByDate(addhs.getCreateDate(),
            addhs.getHabitAssignId())).thenReturn(Optional.empty());
        when(dateService.convertToDatasourceTimezone(addhs.getCreateDate()))
            .thenReturn(zonedDateTime.plusDays(2));

        assertThrows(BadRequestException.class, () ->
            habitStatisticService.save(addhs)
        );
    }

    @Test
    void updateTest() {
        UpdateHabitStatisticDto updateHabitStatisticDto = new UpdateHabitStatisticDto();

        when(habitStatisticRepo.findById(1L)).thenReturn(Optional.of(habitStatistic));
        when(habitStatisticRepo.save(habitStatistic)).thenReturn(habitStatistic);
        when(modelMapper.map(habitStatistic, UpdateHabitStatisticDto.class))
            .thenReturn(updateHabitStatisticDto);

        UpdateHabitStatisticDto actual =
            habitStatisticService.update(1L, updateHabitStatisticDto);
        assertEquals(updateHabitStatisticDto, actual);
    }

    @Test
    void findByIdTest() {
        when(habitStatisticRepo.findById(1L)).thenReturn(Optional.of(habitStatistic));
        when(modelMapper.map(habitStatistic, HabitStatisticDto.class)).thenReturn(habitStatisticDto);
        assertEquals(habitStatisticDto, habitStatisticService.findById(1L));
    }

    @Test
    void findByIdExceptionTest() {
        when(habitStatisticRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            habitStatisticService.findById(1L)
        );
    }

    @Test
    void findAllStatsByHabitAssignIdTest() {
        when(habitStatisticRepo.findAllByHabitAssignId(1L)).thenReturn(habitStatistics);
        when(modelMapper.map(habitStatistics, new TypeToken<List<HabitStatisticDto>>() {
        }.getType())).thenReturn(habitStatisticDtos);

        List<HabitStatisticDto> actual = habitStatisticService.findAllStatsByHabitAssignId(1L);
        assertEquals(habitStatisticDtos, actual);
    }

    @Test
    void findAllStatsByHabitId() {
        when(habitStatisticRepo.findAllByHabitId(1L)).thenReturn(habitStatistics);
        when(modelMapper.map(habitStatistics, new TypeToken<List<HabitStatisticDto>>() {
        }.getType())).thenReturn(habitStatisticDtos);

        List<HabitStatisticDto> actual = habitStatisticService.findAllStatsByHabitId(1L);
        assertEquals(habitStatisticDtos, actual);
    }

    @Test
    void getTodayStatisticsForAllHabitItemsTest() {
        when(habitStatisticRepo.getStatisticsForAllHabitItemsByDate(zonedDateTime, "en"))
            .thenReturn(new ArrayList<>());
        assertEquals(new ArrayList<HabitItemsAmountStatisticDto>(),
            habitStatisticService.getTodayStatisticsForAllHabitItems("en"));
    }

    @Test
    void deleteAllStatsByHabitAssignTest() {
        when(habitAssignRepo.findById(1L)).thenReturn(Optional.of(habitAssign));
        habitStatisticService.deleteAllStatsByHabitAssign(habitAssign);
        verify(habitStatisticRepo, times(1)).deleteAllByHabitAssign(habitAssign);
    }
}