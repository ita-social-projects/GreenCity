package greencity.service.impl;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habitstatistic.UpdateHabitStatisticDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import greencity.entity.enums.HabitRate;
import greencity.mapping.HabitStatisticMapper;
import greencity.repository.HabitRepo;
import greencity.repository.HabitStatisticRepo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

@RunWith(MockitoJUnitRunner.class)
public class HabitStatisticServiceImplTest {

    @Mock
    private HabitStatisticRepo habitStatisticRepo;

    @Mock
    HabitStatisticMapper habitStatisticMapper;

    @Mock
    HabitRepo habitRepo;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    private HabitStatisticServiceImpl habitStatisticService;


    private HabitStatistic habitStatistic = new HabitStatistic(
        1L, HabitRate.GOOD, LocalDate.of(2019, 11, 15), 10, null);

    @Test
    public void saveTest() {
        AddHabitStatisticDto addhs = AddHabitStatisticDto
            .builder().amountOfItems(10).habitRate(HabitRate.GOOD)
            .id(1L).habitId(1L).createdOn(LocalDate.of(2019, 11, 28)).build();
        when(habitStatisticService.save(addhs)).thenReturn(addhs);
        assertEquals(addhs, habitStatisticService.save(addhs));
    }

    @Test
    public void updateTest() {
        UpdateHabitStatisticDto updateHabStatDto = new UpdateHabitStatisticDto();
        HabitStatistic habitStatistic = new HabitStatistic();
        when(habitStatisticRepo.findById(anyLong())).thenReturn(Optional.of(habitStatistic));
        when(habitStatisticService.update(anyLong(), updateHabStatDto)).thenReturn(updateHabStatDto);

        assertEquals(updateHabStatDto, habitStatisticService.update(anyLong(), updateHabStatDto));
        Mockito.verify(habitStatisticRepo, times(2)).findById(anyLong());
    }

    @Test
    public void findByIdTest() {
        HabitStatistic habitStatistic = new HabitStatistic();
        when(habitStatisticRepo.findById(anyLong())).thenReturn(Optional.of(habitStatistic));
        assertEquals(habitStatistic, habitStatisticService.findById(anyLong()));
    }

    @Test
    public void findAllHabitsByUserIdTest() {
        List<Habit> habits = Arrays.asList(new Habit(), new Habit());
        when(habitRepo.findAllByUserId(anyLong())).thenReturn(Optional.of(habits));
        assertEquals(habits, habitStatisticService.findAllHabitsByUserId(anyLong()));
    }

    @Test
    public void findAllByHabitIdTest() {
        List<HabitStatistic> habitStatistic = new ArrayList<>();
        when(habitStatisticRepo.findAllByHabitId(anyLong())).thenReturn(habitStatistic);

        List<HabitStatisticDto> habitStatisticDtos =
            habitStatistic
                .stream()
                .map(source -> modelMapper.map(source, HabitStatisticDto.class))
                .collect(Collectors.toList());

        assertEquals(habitStatisticService.findAllByHabitId(anyLong()), habitStatisticDtos);
    }
}
