package greencity.service.impl;

import greencity.exception.exceptions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.user.*;
import greencity.entity.Goal;
import greencity.entity.User;
import greencity.entity.UserGoal;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.mapping.UserGoalToResponseDtoMapper;
import greencity.repository.GoalRepo;
import greencity.repository.UserGoalRepo;
import greencity.repository.UserRepo;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    UserRepo userRepo;

    @Mock
    UserGoalRepo userGoalRepo;

    @Mock
    GoalRepo goalRepo;

    @Mock
    UserGoalToResponseDtoMapper userGoalToResponseDtoMapper;

    User user =
        User.builder()
            .id(1l)
            .firstName("test")
            .lastName("test")
            .email("test@gmail.com")
            .role(ROLE.ROLE_USER)
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    User user2 =
        User.builder()
            .id(2l)
            .firstName("test")
            .lastName("test")
            .email("test@gmail.com")
            .role(ROLE.ROLE_MODERATOR)
            .userStatus(UserStatus.ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private ModelMapper modelMapper;

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
        Long id = 1l;

        User user = new User();
        user.setId(1l);

        when(userRepo.findById(id)).thenReturn(Optional.of(user));

        assertEquals(user, userService.findById(id));
        verify(userRepo, times(1)).findById(id);
    }

    @Test(expected = BadIdException.class)
    public void findByIdBadIdTest() {
        when(userRepo.findById(any())).thenThrow(BadIdException.class);
        userService.findById(1l);
    }

    @Test(expected = UserAlreadyRegisteredException.class)
    public void saveExceptionTest() {
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user));
        userService.save(new User());
    }

    @Test(expected = BadIdException.class)
    public void deleteByIdExceptionBadIdTest() {
        userService.deleteById(1l);
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
            new PageableDto<UserForListDto>(userForListDtos,
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
            new PageableDto<UserForListDto>(userForListDtos,
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
    public void getAvailableGoalsTest() {
        List<Goal> goals = new ArrayList<>(Arrays.asList(new Goal(), new Goal()));
        List<GoalDto> goalDto = modelMapper.map(goals, new TypeToken<List<GoalDto>>(){}.getType());
        when(goalRepo.findAvailableGoalsByUser(user)).thenReturn(goals);
        assertEquals(userService.getAvailableGoals(user), goalDto);
    }

    @Test(expected = UserHasNoAvailableGoalsException.class)
    public void getAvailableGoalsNoAvailableGoalsTest() {
        when(goalRepo.findAvailableGoalsByUser(user)).thenReturn(Collections.emptyList());
        userService.getAvailableGoals(user);
    }

}
