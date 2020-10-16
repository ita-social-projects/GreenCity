package greencity.service.impl;

import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.*;
import greencity.entity.enums.HabitRate;
import greencity.repository.HabitAssignRepo;
import greencity.service.HabitService;
import greencity.service.HabitStatisticService;
import greencity.service.HabitStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import org.modelmapper.TypeToken;

@ExtendWith(MockitoExtension.class)
class HabitAssignServiceImplTest {
    @Mock
    HabitService habitService;
    @Mock
    HabitAssignRepo habitAssignRepo;
    @Mock
    HabitStatisticService habitStatisticService;
    @Mock
    HabitStatusService habitStatusService;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    HabitAssignServiceImpl habitAssignService;

    private ZonedDateTime zonedDateTime = ZonedDateTime.now();


    private HabitDto habitDto =
        HabitDto.builder().id(1L).build();

    private HabitAssignDto habitAssignDto =
        HabitAssignDto.builder().id(1L)
            .createDateTime(zonedDateTime).suspended(false).habit(habitDto).build();


    private Habit habit =
        Habit.builder().id(1L).image("src/main/resources/static/css/background-image-footer.svg").build();
//            .habitAssigns(new LinkedList<HabitAssign>(Arrays.asList(habitAssign))).build();

    private User user =
        User.builder().id(1L).build();
//            .habitAssigns(new LinkedList<HabitAssign>(Arrays.asList(habitAssign))).build();

    private HabitAssign habitAssign = HabitAssign.builder().id(1L)
        .acquired(true).createDate(zonedDateTime).suspended(false).user(user).habit(habit).build();



    @Test
    void getByIdTest() {
        when(habitAssignRepo.findById(1L)).thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        assertEquals(habitAssignDto, habitAssignService.getById(1L));
    }

    @Test
    void findActiveHabitAssignByUserIdAndHabitIdTest() {
        when(habitService.getById(1L)).thenReturn(habit);
        when(modelMapper.map(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(1L, 1L),
            HabitAssignDto.class)).thenReturn(habitAssignDto);
        assertEquals(habitAssignDto, habitAssignService.findActiveHabitAssignByUserIdAndHabitId(1L, 1L));
    }

   @Test
    void suspendHabitAssignByHabitIdAndUserIdTest() {

   }

}