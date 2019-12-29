package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.dto.habitstatistic.HabitIdDto;
import greencity.dto.user.HabitDictionaryDto;
import greencity.dto.user.RoleDto;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserUpdateDto;
import greencity.entity.Habit;
import greencity.entity.HabitDictionary;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.exception.exceptions.*;
import greencity.mapping.HabitMapper;
import greencity.repository.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;


@RunWith(MockitoJUnitRunner.Silent.class)
public class UserServiceImplTest {
    @Mock
    UserRepo userRepo;

    @Mock
    UserGoalRepo userGoalRepo;

    @Mock
    GoalRepo goalRepo;

    @Mock
    HabitDictionaryRepo habitDictionaryRepo;

    @Mock
    HabitRepo habitRepo;

    @Mock
    HabitStatisticRepo habitStatisticRepo;

    @Mock
    HabitDictionaryTranslationRepo habitDictionaryTranslationRepo;

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
            .createDate(ZonedDateTime.now())
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
//        List<UserGoal> userGoals = new ArrayList<>(Arrays.asList(new UserGoal(), new UserGoal()));
//        List<UserGoalResponseDto> userGoalDto = userGoals
//            .stream()
//            .map(userGoal -> userGoalToResponseDtoMapper.convertToDto(userGoal))
//            .collect(Collectors.toList());
//        when(userGoalRepo.findAllByUserId(user.getId())).thenReturn(userGoals);
//        assertEquals(userService.getUserGoals(user), userGoalDto);
    }

    @Test(expected = UserHasNoGoalsException.class)
    public void getUserGoalsUserHasNoGoalTest() {
//        when(userGoalRepo.findAllByUserId(user.getId())).thenReturn(Collections.emptyList());
//        userService.getUserGoals(user);
    }

    @Test
    public void getAvailableGoalsTest() {
//        List<Goal> goals = new ArrayList<>(Arrays.asList(new Goal(), new Goal()));
//        List<GoalDto> goalDto = modelMapper.map(goals, new TypeToken<List<GoalDto>>(){}.getType());
//        when(goalRepo.findAvailableGoalsByUser(user)).thenReturn(goals);
//        assertEquals(userService.getAvailableGoals(user), goalDto);
    }

    @Test(expected = UserHasNoAvailableGoalsException.class)
    public void getAvailableGoalsNoAvailableGoalsTest() {
//        when(goalRepo.findAvailableGoalsByUser(user)).thenReturn(Collections.emptyList());
//        userService.getAvailableGoals(user);
    }

    @Test(expected = UserHasNoAvailableHabitDictionaryException.class)
    public void getAvailableHabitDictionaryNoAvailable() {
        when(habitDictionaryTranslationRepo.findAvailableHabitDictionaryByUser(1L, "en")).thenReturn(Collections.emptyList());
        userService.getAvailableHabitDictionary(user, "en");
    }

    @Test
    public void createUserHabitTest() {
        when(habitMapper.convertToDto(new Habit(), "en")).thenReturn(new HabitCreateDto());
        when(habitMapper.convertToEntity(1L, user)).thenReturn(new Habit());
        when(habitRepo.saveAll(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(habitRepo.findByUserIdAndStatusHabit(user.getId())).thenReturn(Collections.emptyList());
        assertEquals(userService.createUserHabit(user, Collections.emptyList(), anyString()), Collections.emptyList());
    }

    @Test
    public void addDefaultHabitTest() {
        when(habitRepo.findByUserIdAndHabitDictionaryId(user.getId(), 1L)).thenReturn(Optional.empty());
        when(habitMapper.convertToDto(new Habit(), "en")).thenReturn(new HabitCreateDto());
        when(habitMapper.convertToEntity(1L, user)).thenReturn(new Habit());
        when(habitRepo.saveAll(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(habitRepo.findByUserIdAndStatusHabit(user.getId())).thenReturn(Collections.emptyList());
        when(userService.createUserHabit(user, Collections.singletonList(new HabitIdDto()), anyString()))
            .thenReturn(Collections.singletonList(new HabitCreateDto()));
        userService.addDefaultHabit(user, "en");
        verify(habitRepo, times(1)).saveAll(Collections.singletonList(new Habit()));
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
}
