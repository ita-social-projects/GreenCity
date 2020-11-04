package greencity.service.impl;

import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
import greencity.service.HabitAssignServiceImpl;
import greencity.service.HabitStatisticService;
import greencity.service.HabitStatusService;
import java.time.ZonedDateTime;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

@ExtendWith(MockitoExtension.class)
class HabitAssignServiceImplTest {
    @Mock
    HabitRepo habitRepo;
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

    private HabitDto habitDto = HabitDto.builder().id(1L).build();

    private HabitAssignDto habitAssignDto = HabitAssignDto.builder().id(1L)
        .createDateTime(zonedDateTime).habitId(habitDto.getId()).suspended(false).build();

    private Habit habit =
        Habit.builder().id(1L).image("src/main/resources/static/css/background-image-footer.svg").build();

    private HabitVO habitVO =
        HabitVO.builder().id(1L).image("src/main/resources/static/css/background-image-footer.svg").build();

    private UserVO userVO = UserVO.builder().id(1L).build();

    private User user = User.builder().id(1L).build();

    private HabitAssign habitAssign = HabitAssign.builder().id(1L)
        .suspended(false).acquired(false).createDate(zonedDateTime).user(user).habit(habit).build();

    private HabitAssign habitAssignNew = HabitAssign.builder()
        .suspended(false).user(user).habit(habit).build();


    private HabitAssignStatDto habitAssignStatDto = HabitAssignStatDto.builder()
        .acquired(true).suspended(false).build();

    private List<HabitAssignDto> habitAssignDtos = Collections.singletonList(habitAssignDto);

    private List<HabitAssign> habitAssigns = Collections.singletonList(habitAssign);


    @Test
    void getByIdTest() {
        when(habitAssignRepo.findById(1L)).thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        assertEquals(habitAssignDto, habitAssignService.getById(1L));
    }

    @Test
    void getByIdFailTest() {
        assertThrows(NotFoundException.class, () -> habitAssignService.getById(1L));
    }

    @Test
    void assignHabitForUserTest() {
        HabitAssignVO habitAssignVO = HabitAssignVO.builder().id(habitAssign.getId()).build();
        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habit.getId(), user.getId()))
            .thenReturn(Optional.empty());
        when(habitAssignRepo.save(any())).thenReturn(habitAssign);
        when(modelMapper.map(habitAssign, HabitAssignVO.class)).thenReturn(habitAssignVO);
        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        HabitAssignDto actual = habitAssignService.assignHabitForUser(habit.getId(), userVO);
        verify(habitStatusService, times(1)).saveStatusByHabitAssign(any());
        assertEquals(habitAssignDto, actual);
    }

    @Test
    void findActiveHabitAssignByUserIdAndHabitIdTest() {
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(1L, 1L))
            .thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssign,
            HabitAssignDto.class)).thenReturn(habitAssignDto);
        assertEquals(habitAssignDto, habitAssignService.findActiveHabitAssignByUserIdAndHabitId(1L, 1L));
    }

    @Test
    void getAllHabitAssignsByUserIdAndAcquiredStatusTest() {
        when(habitAssignRepo.findAllByUserIdAndAcquiredAndSuspendedFalse(1L, true)).thenReturn(habitAssigns);
        when(modelMapper.map(habitAssigns,
            new TypeToken<List<HabitAssignDto>>() {
            }.getType())).thenReturn(habitAssignDtos);
        List<HabitAssignDto> actual = habitAssignService.getAllHabitAssignsByUserIdAndAcquiredStatus(1L, true);
        assertEquals(habitAssignDtos, actual);
    }

    @Test
    void updateStatusByHabitIdAndUserId() {
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(1L, 1L)).thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssignRepo.save(habitAssign), HabitAssignDto.class)).thenReturn(habitAssignDto);
        assertEquals(habitAssignDto, habitAssignService.updateStatusByHabitIdAndUserId(1L, 1L, habitAssignStatDto));
    }
}
