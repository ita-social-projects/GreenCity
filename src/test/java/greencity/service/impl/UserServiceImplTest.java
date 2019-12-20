package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.dto.habitstatistic.HabitIdDto;
import greencity.dto.user.*;
import greencity.entity.*;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.GoalStatus;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.exceptions.*;
import greencity.mapping.HabitMapper;
import greencity.mapping.UserGoalToResponseDtoMapper;
import greencity.repository.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserServiceImplTest {

    @Mock
    UserRepo userRepo;

    @Mock
    UserGoalRepo userGoalRepo;

    @Mock
    GoalRepo goalRepo;

    @Mock
    CustomGoalRepo customGoalRepo;

    @Mock
    HabitDictionaryRepo habitDictionaryRepo;

    @Mock
    UserGoalToResponseDtoMapper userGoalToResponseDtoMapper;

    @Mock
    HabitRepo habitRepo;

    @Mock
    HabitStatisticRepo habitStatisticRepo;

    private User user =
        User.builder()
            .id(1L)
            .firstName("test")
            .lastName("test")
            .email("test@gmail.com")
            .role(ROLE.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    private User user2 =
        User.builder()
            .id(2L)
            .firstName("test")
            .lastName("test")
            .email("test@gmail.com")
            .role(ROLE.ROLE_MODERATOR)
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    private Habit habit =
        Habit.builder()
            .id(1L)
            .habitDictionary(new HabitDictionary())
            .user(user)
            .statusHabit(true)
            .createDate(LocalDate.now())
            .build();

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private HabitMapper habitMapper;

    @Test
    public void saveTest() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepo.save(user)).thenReturn(user);
        assertEquals(user, userService.save(user));
    }

    @Test
    public void updateUserStatusDeactivatedTest() {
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(userRepo.save(any())).thenReturn(user);
        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());
        assertEquals(
            UserStatus.DEACTIVATED,
            userService.updateStatus(user.getId(), UserStatus.DEACTIVATED, any()).getUserStatus());
    }

    @Test(expected = LowRoleLevelException.class)
    public void updateUserStatusLowRoleLevelException() {
        user.setRole(ROLE.ROLE_MODERATOR);
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        userService.updateStatus(user.getId(), UserStatus.DEACTIVATED, "email");
    }

    @Test
    public void updateRoleTest() {
        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(userRepo.save(any())).thenReturn(user);
        assertEquals(
            ROLE.ROLE_MODERATOR,
            userService.updateRole(user.getId(), ROLE.ROLE_MODERATOR, any()).getRole());
        verify(userRepo, times(1)).save(any());
    }

    @Test(expected = BadUpdateRequestException.class)
    public void updateRoleOnTheSameUserTest() {
        when(userService.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));
        userService.updateRole(user.getId(), null, user.getEmail());
    }

    @Test
    public void findByIdTest() {
        Long id = 1L;

        User user = new User();
        user.setId(1L);

        when(userRepo.findById(id)).thenReturn(Optional.of(user));

        assertEquals(user, userService.findById(id));
        verify(userRepo, times(1)).findById(id);
    }

    @Test(expected = BadIdException.class)
    public void findByIdBadIdTest() {
        when(userRepo.findById(any())).thenThrow(BadIdException.class);
        userService.findById(1L);
    }

    @Test(expected = BadIdException.class)
    public void deleteByIdExceptionBadIdTest() {
        userService.deleteById(1L);
    }

    @Test(expected = BadIdException.class)
    public void deleteByNullIdExceptionTest() {
        userService.deleteById(null);
        verifyZeroInteractions(userRepo);
    }

    @Test
    public void deleteByExistentIdTest() {
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        userService.deleteById(user.getId());
        verify(userRepo).delete(user);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    public void findIdByEmail() {
        String email = "email";
        when(userRepo.findIdByEmail(email)).thenReturn(Optional.of(2L));
        assertEquals(2L, (long) userService.findIdByEmail(email));
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test(expected = BadEmailException.class)
    public void findIdByEmailNotFound() {
        userService.findIdByEmail(any());
    }

    @Test
    public void findByPage() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        User user = new User();
        user.setFirstName("Roman");

        UserForListDto userForListDto = new UserForListDto();
        userForListDto.setFirstName("Roman");

        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        List<UserForListDto> userForListDtos = Collections.singletonList(userForListDto);

        PageableDto<UserForListDto> userPageableDto =
            new PageableDto<>(userForListDtos,
                userForListDtos.size(), 0);

        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());

        when(userRepo.findAll(pageable)).thenReturn(usersPage);

        assertEquals(userPageableDto, userService.findByPage(pageable));
        verify(userRepo, times(1)).findAll(pageable);
    }

    @Test
    public void getRoles() {
        RoleDto roleDto = new RoleDto(ROLE.class.getEnumConstants());
        assertEquals(roleDto, userService.getRoles());
    }

    @Test
    public void getEmailStatusesTest() {
        List<EmailNotification> placeStatuses =
            Arrays.asList(EmailNotification.class.getEnumConstants());

        TestCase.assertEquals(placeStatuses, userService.getEmailNotificationsStatuses());
    }

    @Test
    public void updateLastVisit() {
        LocalDateTime localDateTime = user.getLastVisit().minusHours(1);
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        assertNotEquals(localDateTime, userService.updateLastVisit(user).getLastVisit());
    }


    @Test
    public void getUsersByFilter() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        FilterUserDto filterUserDto = new FilterUserDto();

        User user = new User();
        user.setFirstName("Roman");

        UserForListDto userForListDto = new UserForListDto();
        userForListDto.setFirstName("Roman");

        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        List<UserForListDto> userForListDtos = Collections.singletonList(userForListDto);

        PageableDto<UserForListDto> userPageableDto =
            new PageableDto<>(userForListDtos,
                userForListDtos.size(), 0);

        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());

        when(userRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(usersPage);

        assertEquals(userPageableDto, userService.getUsersByFilter(filterUserDto, pageable));
    }


    @Test
    public void getUserUpdateDtoByEmail() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName(user.getFirstName());
        userUpdateDto.setLastName(user.getLastName());
        userUpdateDto.setEmailNotification(user.getEmailNotification());
        when(modelMapper.map(any(), any())).thenReturn(userUpdateDto);
        UserUpdateDto userInitialsByEmail = userService.getUserUpdateDtoByEmail("");
        assertEquals(userInitialsByEmail.getFirstName(), user.getFirstName());
        assertEquals(userInitialsByEmail.getLastName(), user.getLastName());
        assertEquals(userInitialsByEmail.getEmailNotification(), user.getEmailNotification());
    }

    @Test
    public void update() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setFirstName(user.getFirstName());
        userUpdateDto.setLastName(user.getLastName());
        userUpdateDto.setEmailNotification(user.getEmailNotification());
        User user = userService.update(userUpdateDto, "");
        assertEquals(userUpdateDto.getFirstName(), user.getFirstName());
        assertEquals(userUpdateDto.getLastName(), user.getLastName());
        assertEquals(userUpdateDto.getEmailNotification(), user.getEmailNotification());
        verify(userRepo, times(1)).save(any());
    }

    @Test
    public void getUserGoalsTest() {
        List<UserGoal> userGoals = new ArrayList<>(Arrays.asList(new UserGoal(), new UserGoal()));
        List<UserGoalResponseDto> userGoalDto = userGoals
            .stream()
            .map(userGoal -> userGoalToResponseDtoMapper.convertToDto(userGoal))
            .collect(Collectors.toList());
        when(userGoalRepo.findAllByUserId(user.getId())).thenReturn(userGoals);
        assertEquals(userService.getUserGoals(user), userGoalDto);
    }

    @Test(expected = UserHasNoGoalsException.class)
    public void getUserGoalsUserHasNoGoalTest() {
        when(userGoalRepo.findAllByUserId(user.getId())).thenReturn(Collections.emptyList());
        userService.getUserGoals(user);
    }

    @Test
    public void updateUserRefreshTokenForUserWithExistentIdTest() {
        when(userRepo.updateUserRefreshToken("foo", user.getId())).thenReturn(1);
        int updatedRows = userService.updateUserRefreshToken("foo", user.getId());
        assertEquals(1, updatedRows);
    }

    @Test(expected = UserGoalStatusNotUpdatedException.class)
    public void updateUserGoalStatusWithNonExistentGoalIdTest() {
        user.setUserGoals(Collections.singletonList(new UserGoal(1L, null, null, null, null, null)));
        userService.updateUserGoalStatus(user, 2L);
        verifyZeroInteractions(userGoalRepo);
    }

    @Test
    public void updateUserGoalStatusWithDisabledGoalStateTest() {
        UserGoal userGoal = new UserGoal(1L, null, null, null, GoalStatus.DISABLED, null);
        when(userGoalRepo.getOne(userGoal.getId())).thenReturn(userGoal);
        user.setUserGoals(Collections.singletonList(userGoal));
        userService.updateUserGoalStatus(user, userGoal.getId());
        verify(userGoalRepo, times(0)).save(userGoal);
    }

    @Test
    public void updateUserGoalStatusWithActiveGoalStateTest() {
        UserGoal userGoal = new UserGoal(1L, null, null, null, GoalStatus.ACTIVE, null);
        when(userGoalRepo.getOne(userGoal.getId())).thenReturn(userGoal);
        when(userGoalToResponseDtoMapper.convertToDto(userGoal)).thenReturn(new UserGoalResponseDto(1L, null, GoalStatus.DONE));
        user.setUserGoals(Collections.singletonList(userGoal));
        UserGoalResponseDto userGoalResponseDto = userService.updateUserGoalStatus(user, userGoal.getId());
        assertEquals(GoalStatus.DONE, userGoal.getStatus());
        assertEquals(userGoalResponseDto, new UserGoalResponseDto(1L, null, GoalStatus.DONE));
        verify(userGoalRepo).save(userGoal);
    }

    @Test
    public void updateUserGoalStatusWithDoneGoalStateTest() {
        UserGoal userGoal = new UserGoal(1L, null, null, null, GoalStatus.DONE, null);
        when(userGoalRepo.getOne(userGoal.getId())).thenReturn(userGoal);
        UserGoalResponseDto expectedUserGoalResponseDto = new UserGoalResponseDto(1L, null, GoalStatus.ACTIVE);
        when(userGoalToResponseDtoMapper.convertToDto(userGoal)).thenReturn(expectedUserGoalResponseDto);
        user.setUserGoals(Collections.singletonList(userGoal));
        UserGoalResponseDto userGoalResponseDto = userService.updateUserGoalStatus(user, userGoal.getId());
        assertEquals(GoalStatus.ACTIVE, userGoal.getStatus());
        assertEquals(userGoalResponseDto, expectedUserGoalResponseDto);
        verify(userGoalRepo).save(userGoal);
    }

    @Test
    public void getAvailableGoalsTest() {
        List<Goal> goals = new ArrayList<>(Arrays.asList(new Goal(), new Goal()));
        List<GoalDto> goalDto = modelMapper.map(goals, new TypeToken<List<GoalDto>>(){}.getType());
        when(goalRepo.findAvailableGoalsByUser(user)).thenReturn(goals);
        assertEquals(userService.getAvailableGoals(user), goalDto);
    }

    @Test
    public void saveUserGoalsWithNullUserGoalsAndExistentCustomGoalsTest() {
        user.setUserGoals(new ArrayList<>());
        when(modelMapper.map(any(), any())).thenReturn(new UserGoal());
        when(userGoalRepo.saveAll(any())).thenReturn(Collections.emptyList());
        UserCustomGoalDto userCustomGoalDto = new UserCustomGoalDto();
        BulkSaveUserGoalDto nullUserGoalsDto =
            new BulkSaveUserGoalDto(null, Collections.singletonList(userCustomGoalDto));
        userService.saveUserGoals(user, nullUserGoalsDto);
        verify(userGoalRepo).saveAll(user.getUserGoals());
        verify(modelMapper).map(userCustomGoalDto, UserGoal.class);
    }

    @Test
    public void saveUserGoalsWithExistentUserGoalsAndNullCustomGoalsTest() {
        user.setUserGoals(new ArrayList<>());
        when(modelMapper.map(any(), any())).thenReturn(new UserGoal());
        when(userGoalRepo.saveAll(any())).thenReturn(Collections.emptyList());
        UserGoalDto userGoalDto = new UserGoalDto();
        BulkSaveUserGoalDto nullCustomGoalsDto =
            new BulkSaveUserGoalDto(Collections.singletonList(userGoalDto), null);
        userService.saveUserGoals(user, nullCustomGoalsDto);
        verify(userGoalRepo).saveAll(user.getUserGoals());
        verify(modelMapper).map(userGoalDto, UserGoal.class);
    }

    @Test
    public void saveUserGoalsWithExistentUserGoalsAndExistentCustomGoalsTest() {
        user.setUserGoals(new ArrayList<>());
        when(modelMapper.map(any(), any())).thenReturn(new UserGoal());
        when(userGoalRepo.saveAll(any())).thenReturn(Collections.emptyList());
        UserGoalDto userGoalDto = new UserGoalDto();
        UserCustomGoalDto userCustomGoalDto = new UserCustomGoalDto();
        BulkSaveUserGoalDto userGoalsAndCustomGoalsDto = new BulkSaveUserGoalDto(
            Collections.singletonList(userGoalDto), Collections.singletonList(userCustomGoalDto)
        );
        userService.saveUserGoals(user, userGoalsAndCustomGoalsDto);
        verify(userGoalRepo, times(2)).saveAll(user.getUserGoals());
        verify(modelMapper).map(userGoalDto, UserGoal.class);
        verify(modelMapper).map(userCustomGoalDto, UserGoal.class);
    }

    @Test
    public void deleteUserGoalsWithEmptyInputIdsTest() {
        List<Long> deletedGoals = userService.deleteUserGoals("");
        assertEquals(0, deletedGoals.size());
    }

    @Test
    public void deleteUserGoalsWithValidInputIdsTest() {
        UserServiceImpl userServiceSpy = PowerMockito.spy(userService);
        UserGoal userGoalToDelete = new UserGoal(1L, null, null, null, null, null);
        when(userGoalRepo.findById(anyLong())).thenReturn(Optional.of(userGoalToDelete));
        PowerMockito.doNothing().when(userGoalRepo).delete(userGoalToDelete);
        List<Long> deletedGoals = userServiceSpy.deleteUserGoals("1,2,3");
        ArrayList<Long> expectedDeletedGoals = new ArrayList<>(3);
        expectedDeletedGoals.add(1L);
        expectedDeletedGoals.add(2L);
        expectedDeletedGoals.add(3L);
        assertEquals(deletedGoals, expectedDeletedGoals);
    }

    @Test(expected = UserHasNoAvailableGoalsException.class)
    public void getAvailableGoalsNoAvailableGoalsTest() {
        when(goalRepo.findAvailableGoalsByUser(user)).thenReturn(Collections.emptyList());
        userService.getAvailableGoals(user);
    }

    @Test
    public void getAvailableHabitDictionaryTest() {
        List<HabitDictionary> habitDictionaries = new ArrayList<>(Arrays.asList(new HabitDictionary(), new HabitDictionary()));
        List<HabitDictionaryDto> habitDictionaryDtos = modelMapper.map(habitDictionaries, new TypeToken<List<HabitDictionaryDto>>() {
        }.getType());
        when(habitDictionaryRepo.findAvailableHabitDictionaryByUser(user)).thenReturn(habitDictionaries);
        assertEquals(userService.getAvailableHabitDictionary(user), habitDictionaryDtos);
    }

    @Test(expected = UserHasNoAvailableHabitDictionaryException.class)
    public void getAvailableHabitDictionaryNoAvailable() {
        when(habitDictionaryRepo.findAvailableHabitDictionaryByUser(user)).thenReturn(Collections.emptyList());
        userService.getAvailableHabitDictionary(user);
    }

    @Test
    public void createUserHabitTest() {
        when(habitMapper.convertToDto(new Habit())).thenReturn(new HabitCreateDto());
        when(habitMapper.convertToEntity(1L, user)).thenReturn(new Habit());
        when(habitRepo.saveAll(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(habitRepo.findByUserIdAndStatusHabit(user.getId())).thenReturn(Collections.emptyList());
        assertEquals(userService.createUserHabit(user, Collections.emptyList()), Collections.emptyList());
    }

    @Test
    public void createUserHabitWithEmptyDtoIdListAndNotEmptyUserHabitsTest() {
        when(habitRepo.findByUserIdAndStatusHabit(user.getId())).thenReturn(Collections.singletonList(new Habit()));
        assertEquals(Collections.emptyList(), userService.createUserHabit(user, Collections.emptyList()));
    }

    @Test
    public void createUserHabitWithExistentHabitIdsNotMatchingTest() {
        when(habitRepo.findByUserIdAndStatusHabit(user.getId())).thenReturn(Collections.singletonList(new Habit(1L, new HabitDictionary(1L, null, null, null, null), null, null, null, null)));
        assertEquals(Collections.emptyList(), userService.createUserHabit(user, Collections.singletonList(new HabitIdDto(2L))));
    }

    @Test(expected = BadIdException.class)
    public void createUserHabitWithExistentHabitTest() {
        when(habitRepo.findByUserIdAndStatusHabit(user.getId()))
            .thenReturn(
                Collections.singletonList(
                    Habit.builder()
                        .habitDictionary(HabitDictionary.builder().id(1L).build())
                        .build()
                )
            );
        userService.createUserHabit(user, Collections.singletonList(new HabitIdDto(1L)));
        verify(habitRepo, times(0)).saveAll(any());
    }

    @Test
    public void addDefaultHabitTest() {
        when(habitRepo.findByUserIdAndHabitDictionaryId(user.getId(), 1L)).thenReturn(Optional.empty());
        when(habitMapper.convertToDto(new Habit())).thenReturn(new HabitCreateDto());
        when(habitMapper.convertToEntity(1L, user)).thenReturn(new Habit());
        when(habitRepo.saveAll(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(habitRepo.findByUserIdAndStatusHabit(user.getId())).thenReturn(Collections.emptyList());
        when(userService.createUserHabit(user, Collections.singletonList(new HabitIdDto())))
            .thenReturn(Collections.singletonList(new HabitCreateDto()));
        userService.addDefaultHabit(user);
        verify(habitRepo, times(1)).saveAll(Collections.singletonList(new Habit()));
    }

    @Test
    public void getAvailableCustomGoalsForUserWithNoGoalsTest() {
        when(customGoalRepo.findAllAvailableCustomGoalsForUser(user)).thenReturn(Collections.emptyList());
        assertNull(userService.getAvailableCustomGoals(user));
    }

    @Test
    public void getAvailableCustomGoalsForUserWithExistentGoalTest() {
        List<CustomGoalResponseDto> customGoalsDtos = Collections.singletonList(new CustomGoalResponseDto(1L, "foo"));
        when(modelMapper.map(customGoalRepo.findAllAvailableCustomGoalsForUser(user),
            new TypeToken<List<CustomGoalResponseDto>>() {
            }.getType()))
            .thenReturn(customGoalsDtos);
        assertNotNull(userService.getAvailableCustomGoals(user));
        assertEquals(userService.getAvailableCustomGoals(user), customGoalsDtos);
    }

    @Test(expected = BadIdException.class)
    public void deleteHabitByUserIdAndHabitDictionaryEmptyHabitTest() {
        when(habitRepo.findById(anyLong())).thenReturn(Optional.empty());
        userService.deleteHabitByUserIdAndHabitDictionary(1L, 1L);
    }

    @Test(expected = NotDeletedException.class)
    public void deleteHabitByUserIdAndHabitDictionaryNotDeletedExceptionTest() {
        when(habitRepo.findById(anyLong())).thenReturn(Optional.of(new Habit()));
        when(habitRepo.countHabitByUserId(user.getId())).thenReturn(1);
        when(habitStatisticRepo.findAllByHabitId(1L)).thenReturn(Collections.emptyList());
        userService.deleteHabitByUserIdAndHabitDictionary(1L, 1L);
    }

    @Test(expected = BadIdException.class)
    public void deleteHabitByUserIdAndHabitDictionaryExceptionTest() {
        userService.deleteHabitByUserIdAndHabitDictionary(null, 1L);
    }

    @Test
    public void deleteHabitByUserIdAndHabitDictionaryTest() {
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepo.countHabitByUserId(1L)).thenReturn(2);
        when(habitStatisticRepo.findAllByHabitId(1L)).thenReturn(Collections.emptyList());
        userService.deleteHabitByUserIdAndHabitDictionary(user.getId(), habit.getId());
        verify(habitRepo, times(1)).deleteById(habit.getId());
    }

    @Test(expected = NotDeletedException.class)
    public void deleteHabitByUserAndNullHabit() {
        userService.deleteHabitByUserIdAndHabitDictionary(user.getId(), null);
    }

    @Test(expected = NotDeletedException.class)
    public void deleteHabitByNullUserAndNullHabit() {
        userService.deleteHabitByUserIdAndHabitDictionary(null, null);
    }

    @Test
    public void deleteHabitByUserWithExistentHabits() {
        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(habitStatisticRepo.findAllByHabitId(habit.getId())).thenReturn(Collections.singletonList(null));
        when(habitRepo.countHabitByUserId(user.getId())).thenReturn(2);
        userService.deleteHabitByUserIdAndHabitDictionary(user.getId(), habit.getId());
        verify(habitRepo).updateHabitStatusById(habit.getId(), false);
    }
}
