package greencity.service;

import greencity.dto.habit.*;
import greencity.dto.user.UserVO;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitAssignRepo;
import greencity.repository.HabitRepo;
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

@ExtendWith(MockitoExtension.class)
class HabitAssignServiceImplTest {
    @Mock
    HabitRepo habitRepo;
    @Mock
    HabitAssignRepo habitAssignRepo;
    @Mock
    HabitStatusService habitStatusService;
    @Mock
    HabitService habitService;
    @Mock
    ModelMapper modelMapper;
    @InjectMocks
    HabitAssignServiceImpl habitAssignService;

    private ZonedDateTime zonedDateTime = ZonedDateTime.now();

    private HabitDto habitDto = HabitDto.builder().id(1L).build();

    private HabitAssignDto habitAssignDto = HabitAssignDto.builder().id(1L)
        .createDateTime(zonedDateTime).habit(habitDto).suspended(false).build();

    private Habit habit =
        Habit.builder().id(1L).image("src/main/resources/static/css/background-image-footer.svg").build();

    private HabitAssignManagementDto habitAssignManagementDto = HabitAssignManagementDto.builder()
        .id(1L)
        .createDateTime(zonedDateTime).habitId(habit.getId()).suspended(false).build();

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

    private HabitAssignPropertiesDto habitAssignPropertiesDto = HabitAssignPropertiesDto.builder().duration(14).build();

    private String language = "en";

    @Test
    void getByIdTest() {
        when(habitAssignRepo.findById(1L)).thenReturn(Optional.of(habitAssign));

        when(habitService.getByIdAndLanguageCode(habitAssign.getHabit().getId(), language))
        .thenReturn(habitDto);
        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        assertEquals(habitAssignDto, habitAssignService.getById(1L, language));
    }

    @Test
    void getByIdFailTest() {
        assertThrows(NotFoundException.class, () -> habitAssignService.getById(1L, language));
    }

    @Test
    void assignDefaultHabitForUserTest() {
        HabitAssignVO habitAssignVO = HabitAssignVO.builder().id(habitAssign.getId()).build();
        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habit.getId(), user.getId()))
            .thenReturn(Optional.empty());
        when(habitAssignRepo.save(any())).thenReturn(habitAssign);
        when(modelMapper.map(habitAssign, HabitAssignVO.class)).thenReturn(habitAssignVO);
        when(modelMapper.map(habitAssign, HabitAssignManagementDto.class)).thenReturn(habitAssignManagementDto);
        HabitAssignManagementDto actual = habitAssignService.assignDefaultHabitForUser(habit.getId(), userVO);
        verify(habitStatusService, times(1)).saveStatusByHabitAssign(any());
        assertEquals(habitAssignManagementDto, actual);
    }

    @Test
    void assignCustomHabitForUserTest() {
        HabitAssignVO habitAssignVO = HabitAssignVO.builder().id(habitAssign.getId()).build();
        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(habit.getId(), user.getId()))
            .thenReturn(Optional.empty());
        when(habitAssignRepo.save(any())).thenReturn(habitAssign);
        when(modelMapper.map(habitAssign, HabitAssignVO.class)).thenReturn(habitAssignVO);
        when(modelMapper.map(habitAssign, HabitAssignManagementDto.class)).thenReturn(habitAssignManagementDto);
        HabitAssignManagementDto actual = habitAssignService.assignCustomHabitForUser(habit.getId(), userVO,
            habitAssignPropertiesDto);
        verify(habitStatusService, times(1)).saveStatusByHabitAssign(any());
        assertEquals(habitAssignManagementDto, actual);
    }

    @Test
    void findActiveHabitAssignByUserIdAndHabitIdTest() {
        when(habitService.getByIdAndLanguageCode(1L, language)).thenReturn(habitDto);
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(1L, 1L))
            .thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssign,
            HabitAssignDto.class)).thenReturn(habitAssignDto);
        assertEquals(habitAssignDto, habitAssignService.findActiveHabitAssignByUserIdAndHabitId(1L, 1L, "en"));
    }

    @Test
    void getAllHabitAssignsByUserIdAndAcquiredStatusTest() {
        when(habitAssignRepo.findAllByUserIdAndAcquiredAndSuspendedFalse(1L, true)).thenReturn(habitAssigns);
        when(modelMapper.map(habitAssign, HabitAssignDto.class)).thenReturn(habitAssignDto);
        when(habitService.getByIdAndLanguageCode(1L, language)).thenReturn(habitDto);
        List<HabitAssignDto> actual = habitAssignService.getAllHabitAssignsByUserIdAndAcquiredStatus(1L, true, "en");
        assertEquals(habitAssignDtos, actual);
    }

    @Test
    void updateStatusByHabitIdAndUserId() {
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(1L, 1L)).thenReturn(Optional.of(habitAssign));
        when(modelMapper.map(habitAssignRepo.save(habitAssign), HabitAssignManagementDto.class)).thenReturn(habitAssignManagementDto);
        assertEquals(habitAssignManagementDto, habitAssignService.updateStatusByHabitIdAndUserId(1L, 1L, habitAssignStatDto));
    }
}
