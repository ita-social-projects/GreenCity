package greencity.service.impl;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.CustomGoalRequestDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.goal.GoalRequestDto;
import greencity.dto.habitstatistic.HabitIdDto;
import greencity.dto.user.BulkSaveUserGoalDto;
import greencity.dto.user.RoleDto;
import greencity.dto.user.UserCustomGoalDto;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserGoalDto;
import greencity.dto.user.UserGoalResponseDto;
import greencity.dto.user.UserUpdateDto;
import greencity.entity.*;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.GoalStatus;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import greencity.entity.localization.GoalTranslation;
import greencity.exception.exceptions.BadUpdateRequestException;
import greencity.exception.exceptions.LowRoleLevelException;
import greencity.exception.exceptions.NotDeletedException;
import greencity.exception.exceptions.UserGoalStatusNotUpdatedException;
import greencity.exception.exceptions.UserHasNoAvailableGoalsException;
import greencity.exception.exceptions.UserHasNoAvailableHabitDictionaryException;
import greencity.exception.exceptions.UserHasNoGoalsException;
import greencity.exception.exceptions.WrongEmailException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.CustomGoalRepo;
import greencity.repository.GoalTranslationRepo;
import greencity.repository.HabitDictionaryTranslationRepo;
import greencity.repository.HabitRepo;
import greencity.repository.HabitStatisticRepo;
import greencity.repository.UserGoalRepo;
import greencity.repository.UserRepo;
import greencity.service.FileService;
import greencity.service.HabitDictionaryService;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import greencity.service.ProfilePictureService;
import junit.framework.TestCase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class UserServiceImplTest {
    @Mock
    UserRepo userRepo;

    @Mock
    UserGoalRepo userGoalRepo;

    @Mock
    CustomGoalRepo customGoalRepo;

    @Mock
    GoalTranslationRepo goalTranslationRepo;

    @Mock
    HabitRepo habitRepo;

    @Mock
    HabitStatisticRepo habitStatisticRepo;

    @Mock
    HabitDictionaryService habitDictionaryService;

    @Mock
    HabitDictionaryTranslationRepo habitDictionaryTranslationRepo;

    @Mock
    FileService fileService;

    @Mock
    ProfilePictureService profilePictureService;

    private User user =
        User.builder()
            .id(1L)
            .name("Test Testing")
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
            .name("Test Testing")
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
    private String language = "uk";
    private List<GoalTranslation> goalTranslations = Arrays.asList(
        new GoalTranslation(1L, new Language(1L, language, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList()), "TEST", new Goal(1L, Collections.emptyList(), Collections.emptyList())),
        new GoalTranslation(2L, new Language(1L, language, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList()), "TEST", new Goal(2L, Collections.emptyList(), Collections.emptyList())));

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private ModelMapper modelMapper;

    @Test
    public void saveTest() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.ofNullable(user));
        when(userService.findByEmail(user.getEmail()))
            .thenThrow(new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
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

    @Test
    public void updateUserStatusLowRoleLevelException() {
        user.setRole(ROLE.ROLE_MODERATOR);
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        assertThrows(LowRoleLevelException.class, () ->
            userService.updateStatus(user.getId(), UserStatus.DEACTIVATED, "email")
        );
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
    public void updateRoleOnTheSameUserTest() {
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThrows(BadUpdateRequestException.class, () ->
            userService.updateRole(user.getId(), null, user.getEmail())
        );
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

    @Test
    public void findByIdBadIdTest() {
        when(userRepo.findById(any())).thenThrow(WrongIdException.class);
        assertThrows(WrongIdException.class, () ->
            userService.findById(1L)
        );
    }

    @Test
    public void deleteByIdExceptionBadIdTest() {
        assertThrows(WrongIdException.class, () ->
            userService.deleteById(1L)
        );
    }

    @Test
    public void deleteByNullIdExceptionTest() {
        when(userRepo.findById(null)).thenThrow(new WrongIdException(""));
        assertThrows(WrongIdException.class, () ->
            userService.deleteById(null)
        );
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
    @Test
    public void findIdByEmailNotFound() {
        assertThrows(WrongEmailException.class, () ->
            userService.findIdByEmail(any())
        );
    }

    @Test
    public void findByPage() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        User user = new User();
        user.setName("Roman Romanovich");

        UserForListDto userForListDto = new UserForListDto();
        userForListDto.setName("Roman Romanovich");

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
        user.setName("Roman Bezos");

        UserForListDto userForListDto = new UserForListDto();
        userForListDto.setName("Roman Bezos");

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
        userUpdateDto.setName(user.getName());
        userUpdateDto.setEmailNotification(user.getEmailNotification());
        when(modelMapper.map(any(), any())).thenReturn(userUpdateDto);
        UserUpdateDto userInitialsByEmail = userService.getUserUpdateDtoByEmail("");
        assertEquals(userInitialsByEmail.getName(), user.getName());
        assertEquals(userInitialsByEmail.getEmailNotification(), user.getEmailNotification());
    }

    @SneakyThrows
    @Test
    public void update() {
        MultipartFile multipartFile = ModelUtils.getFile();
        ProfilePicture profilePicture = ModelUtils.getProfilePicture();
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        when(profilePictureService.getProfilePictureByUserId(anyLong())).thenReturn(Optional.of(profilePicture));
        when(fileService.upload(any(MultipartFile.class))).thenReturn(ModelUtils.getUrl());
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName(user.getName());
        userUpdateDto.setEmailNotification(user.getEmailNotification());
        User user = userService.update(userUpdateDto, "",multipartFile);
        assertEquals(userUpdateDto.getName(), user.getName());
        assertEquals(userUpdateDto.getEmailNotification(), user.getEmailNotification());
        assertEquals(profilePicture,user.getProfilePicture());
        verify(userRepo, times(1)).save(any());
    }

    @Test
    public void getUserGoalsTest() {
        UserGoal customUserGoal = ModelUtils.getCustomUserGoal();
        UserGoal predefinedUserGoal = ModelUtils.getPredefinedUserGoal();
        List<UserGoal> userGoals = Arrays.asList(customUserGoal, predefinedUserGoal);
        UserGoalResponseDto customDtoWithoutText =
            UserGoalResponseDto.builder().id(1L).status(GoalStatus.ACTIVE).build();
        UserGoalResponseDto predefinedDtoWithoutText =
            UserGoalResponseDto.builder().id(2L).status(GoalStatus.ACTIVE).build();
        UserGoalResponseDto customUserGoalDto = ModelUtils.getCustomUserGoalDto();
        UserGoalResponseDto predefinedUserGoalDto = ModelUtils.getPredefinedUserGoalDto();
        List<UserGoalResponseDto> userGoalDtos = Arrays.asList(customUserGoalDto, predefinedUserGoalDto);
        List<GoalTranslation> goalTranslations = ModelUtils.getGoalTranslations();
        CustomGoal customGoal = CustomGoal.builder().id(8L).text("Buy electric car").build();

        when(userGoalRepo.findAllByUserId(user.getId())).thenReturn(userGoals);
        when(modelMapper.map(userGoals.get(0), UserGoalResponseDto.class)).thenReturn(customDtoWithoutText);
        when(modelMapper.map(userGoals.get(1), UserGoalResponseDto.class)).thenReturn(predefinedDtoWithoutText);
        when(userGoalRepo.findGoalByUserGoalId(userGoals.get(0).getId())).thenReturn(Optional.empty());
        when(userGoalRepo.findGoalByUserGoalId(userGoals.get(1).getId()))
            .thenReturn(Optional.of(predefinedUserGoal.getGoal()));
        when(goalTranslationRepo.findByUserIdLangAndUserGoalId(anyLong(), anyString(), anyLong()))
            .thenReturn(goalTranslations.get(0));
        when(customGoalRepo.findByUserGoalIdAndUserId(anyLong(), anyLong())).thenReturn(customGoal);

        assertEquals(userService.getUserGoals(user.getId(), "en"), userGoalDtos);
    }

    @Test
    public void getUserGoalsUserHasNoGoalTest() {
        when(userGoalRepo.findAllByUserId(user.getId())).thenReturn(Collections.emptyList());
        assertThrows(UserHasNoGoalsException.class, () ->
            userService.getUserGoals(user.getId(), "en")
        );
    }

    @Test
    public void updateUserRefreshTokenForUserWithExistentIdTest() {
        when(userRepo.updateUserRefreshToken("foo", user.getId())).thenReturn(1);
        int updatedRows = userService.updateUserRefreshToken("foo", user.getId());
        assertEquals(1, updatedRows);
    }

    @Test
    public void updateUserGoalStatusWithNonExistentGoalIdTest() {
        user.setUserGoals(Collections.singletonList(new UserGoal(1L, null, null, null, null, null)));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        assertThrows(UserGoalStatusNotUpdatedException.class, () ->
            userService.updateUserGoalStatus(user.getId(), 2L, "en")
        );
        verifyNoInteractions(userGoalRepo);
    }

    @Test
    public void updateUserGoalStatusWithDisabledGoalStateTest() {
        CustomGoal customgoal = CustomGoal.builder().id(3L).text("foo").build();
        UserGoal userGoal = new UserGoal(1L, null, null, customgoal, GoalStatus.DISABLED, null);
        when(userGoalRepo.getOne(userGoal.getId())).thenReturn(userGoal);
        user.setUserGoals(Collections.singletonList(userGoal));
        when(modelMapper.map(any(), eq(UserGoalResponseDto.class))).thenReturn(
            new UserGoalResponseDto(1L, null, GoalStatus.ACTIVE));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userGoalRepo.findGoalByUserGoalId(anyLong())).thenReturn(Optional.empty());
        when(customGoalRepo.findByUserGoalIdAndUserId(anyLong(), anyLong())).thenReturn(customgoal);
        UserGoalResponseDto result = userService.updateUserGoalStatus(user.getId(), userGoal.getId(), "en");
        assertEquals("foo", result.getText());
        verify(userGoalRepo, times(0)).save(userGoal);
    }

    @Test
    public void updateUserGoalStatusWithActiveGoalStateTest() {
        UserGoal userGoal = ModelUtils.getPredefinedUserGoal();
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userGoalRepo.getOne(userGoal.getId())).thenReturn(userGoal);
        when(modelMapper.map(any(), eq(UserGoalResponseDto.class)))
            .thenReturn(new UserGoalResponseDto(2L, null, GoalStatus.DONE));
        user.setUserGoals(Collections.singletonList(userGoal));
        when(userGoalRepo.findGoalByUserGoalId(anyLong())).thenReturn(Optional.of(userGoal.getGoal()));
        when(goalTranslationRepo.findByUserIdLangAndUserGoalId(anyLong(), anyString(), anyLong()))
            .thenReturn(goalTranslations.get(0));
        UserGoalResponseDto userGoalResponseDto =
            userService.updateUserGoalStatus(user.getId(), userGoal.getId(), "en");

        assertEquals(GoalStatus.DONE, userGoal.getStatus());
        assertEquals(userGoalResponseDto,
            new UserGoalResponseDto(2L, goalTranslations.get(0).getText(), GoalStatus.DONE));
        verify(userGoalRepo).save(userGoal);
    }

    @Test
    public void updateUserGoalStatusWithDoneGoalStateTest() {
        CustomGoal customgoal = CustomGoal.builder().id(3L).text("foo").build();
        UserGoal userGoal = new UserGoal(1L, null, null, customgoal, GoalStatus.DONE, null);
        when(userGoalRepo.getOne(userGoal.getId())).thenReturn(userGoal);
        when(modelMapper.map(any(), eq(UserGoalResponseDto.class)))
            .thenReturn(new UserGoalResponseDto(1L, null, GoalStatus.ACTIVE));
        user.setUserGoals(Collections.singletonList(userGoal));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userGoalRepo.findGoalByUserGoalId(anyLong())).thenReturn(Optional.empty());
        when(customGoalRepo.findByUserGoalIdAndUserId(anyLong(), anyLong())).thenReturn(customgoal);
        UserGoalResponseDto userGoalResponseDto =
            userService.updateUserGoalStatus(user.getId(), userGoal.getId(), "en");
        UserGoalResponseDto expectedUserGoalResponseDto = new UserGoalResponseDto(1L, "foo", GoalStatus.ACTIVE);

        assertEquals(GoalStatus.ACTIVE, userGoal.getStatus());
        assertEquals(userGoalResponseDto, expectedUserGoalResponseDto);
        verify(userGoalRepo).save(userGoal);
    }

    @Test
    public void getAvailableGoalsTest() {
        List<GoalDto> goalDto = Arrays.asList(new GoalDto(1L, "TEST"), new GoalDto(2L, "TEST"));

        when(goalTranslationRepo.findAvailableByUserId(user.getId(), language)).thenReturn(goalTranslations);
        when(modelMapper.map(goalTranslations.get(0), GoalDto.class)).thenReturn(goalDto.get(0));
        when(modelMapper.map(goalTranslations.get(1), GoalDto.class)).thenReturn(goalDto.get(1));

        assertEquals(goalDto, userService.getAvailableGoals(user.getId(), language));
    }

    @Test
    public void saveUserGoalsWithNullUserGoalsAndExistentCustomGoalsTest() {
        user.setUserGoals(new ArrayList<>());
        UserCustomGoalDto userCustomGoalDto = new UserCustomGoalDto(new CustomGoalRequestDto(8L));
        BulkSaveUserGoalDto nullUserGoalsDto =
            new BulkSaveUserGoalDto(null, Collections.singletonList(userCustomGoalDto));
        UserGoal customUserGoal = ModelUtils.getCustomUserGoal();
        List<UserGoal> userGoals = Collections.singletonList(customUserGoal);
        UserGoalResponseDto customUserGoalDto = ModelUtils.getCustomUserGoalDto();
        List<UserGoalResponseDto> userGoalDtos = Collections.singletonList(customUserGoalDto);
        CustomGoal customGoal = CustomGoal.builder().id(8L).text("Buy electric car").build();

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(userCustomGoalDto, UserGoal.class)).thenReturn(customUserGoal);
        when(userGoalRepo.findAllByUserId(user.getId())).thenReturn(userGoals);
        when(modelMapper.map(userGoals.get(0), UserGoalResponseDto.class)).thenReturn(userGoalDtos.get(0));
        when(customGoalRepo.findByUserGoalIdAndUserId(userGoalDtos.get(0).getId(), user.getId()))
            .thenReturn(customGoal);

        List<UserGoalResponseDto> result = userService.saveUserGoals(user.getId(), nullUserGoalsDto, "en");
        assertEquals("Buy electric car", result.get(0).getText());
        verify(userGoalRepo).saveAll(user.getUserGoals());
        verify(modelMapper).map(userCustomGoalDto, UserGoal.class);
    }

    @Test
    public void saveUserGoalsWithExistentUserGoalsAndNullCustomGoalsTest() {
        user.setUserGoals(new ArrayList<>());
        UserGoalDto userGoalDto = new UserGoalDto(new GoalRequestDto(2L));
        BulkSaveUserGoalDto nullCustomGoalsDto = new BulkSaveUserGoalDto(
            Collections.singletonList(userGoalDto), null);
        UserGoal predefinedUserGoal = ModelUtils.getPredefinedUserGoal();
        List<UserGoal> userGoals = Collections.singletonList(predefinedUserGoal);
        UserGoalResponseDto predefinedUserGoalDto = ModelUtils.getPredefinedUserGoalDto();
        List<UserGoalResponseDto> userGoalDtos = Collections.singletonList(predefinedUserGoalDto);
        List<GoalTranslation> goalTranslations = ModelUtils.getGoalTranslations();

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(userGoalDto, UserGoal.class)).thenReturn(predefinedUserGoal);
        when(userGoalRepo.findAllByUserId(user.getId())).thenReturn(userGoals);
        when(modelMapper.map(userGoals.get(0), UserGoalResponseDto.class)).thenReturn(userGoalDtos.get(0));
        when(userGoalRepo.findGoalByUserGoalId(userGoals.get(0).getId()))
            .thenReturn(Optional.of(predefinedUserGoal.getGoal()));
        when(goalTranslationRepo.findByUserIdLangAndUserGoalId(anyLong(), anyString(), anyLong()))
            .thenReturn(goalTranslations.get(0));

        List<UserGoalResponseDto> result = userService.saveUserGoals(user.getId(), nullCustomGoalsDto, "en");
        assertEquals("Buy a bamboo toothbrush", result.get(0).getText());
        verify(userGoalRepo).saveAll(user.getUserGoals());
        verify(modelMapper).map(userGoalDto, UserGoal.class);
    }

    @Test
    public void saveUserGoalsWithExistentUserGoalsAndExistentCustomGoalsTest() {
        user.setUserGoals(new ArrayList<>());
        UserGoalDto userGoalDto = new UserGoalDto(new GoalRequestDto(2L));
        UserCustomGoalDto userCustomGoalDto = new UserCustomGoalDto(new CustomGoalRequestDto(8L));
        BulkSaveUserGoalDto userGoalsAndCustomGoalsDto = new BulkSaveUserGoalDto(
            Collections.singletonList(userGoalDto), Collections.singletonList(userCustomGoalDto));
        UserGoal customUserGoal = ModelUtils.getCustomUserGoal();
        UserGoal predefinedUserGoal = ModelUtils.getPredefinedUserGoal();
        List<UserGoal> userGoals = Arrays.asList(customUserGoal, predefinedUserGoal);
        UserGoalResponseDto customUserGoalDto = ModelUtils.getCustomUserGoalDto();
        UserGoalResponseDto predefinedUserGoalDto = ModelUtils.getPredefinedUserGoalDto();
        List<UserGoalResponseDto> userGoalDtos = Arrays.asList(customUserGoalDto, predefinedUserGoalDto);
        List<GoalTranslation> goalTranslations = ModelUtils.getGoalTranslations();
        CustomGoal customGoal = CustomGoal.builder().id(8L).text("Buy electric car").build();

        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(userGoalDto, UserGoal.class)).thenReturn(predefinedUserGoal);
        when(modelMapper.map(userCustomGoalDto, UserGoal.class)).thenReturn(customUserGoal);
        when(userGoalRepo.findAllByUserId(user.getId())).thenReturn(userGoals);
        when(modelMapper.map(userGoals.get(0), UserGoalResponseDto.class)).thenReturn(userGoalDtos.get(0));
        when(modelMapper.map(userGoals.get(1), UserGoalResponseDto.class)).thenReturn(userGoalDtos.get(1));
        when(userGoalRepo.findGoalByUserGoalId(userGoals.get(0).getId())).thenReturn(Optional.empty());
        when(userGoalRepo.findGoalByUserGoalId(userGoals.get(1).getId()))
            .thenReturn(Optional.of(predefinedUserGoal.getGoal()));
        when(goalTranslationRepo.findByUserIdLangAndUserGoalId(anyLong(), anyString(), anyLong()))
            .thenReturn(goalTranslations.get(0));
        when(customGoalRepo.findByUserGoalIdAndUserId(anyLong(), anyLong())).thenReturn(customGoal);

        List<UserGoalResponseDto> result = userService.saveUserGoals(user.getId(), userGoalsAndCustomGoalsDto, "en");
        assertEquals("Buy electric car", result.get(0).getText());
        verify(userGoalRepo, times(2)).saveAll(user.getUserGoals());
        verify(modelMapper).map(userGoalDto, UserGoal.class);
        verify(modelMapper).map(userCustomGoalDto, UserGoal.class);
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

    @Test
    public void getAvailableGoalsNoAvailableGoalsTest() {
        when(goalTranslationRepo.findAvailableByUserId(user.getId(), language)).thenReturn(Collections.emptyList());
        assertThrows(UserHasNoAvailableGoalsException.class, () ->
            userService.getAvailableGoals(user.getId(), language)
        );
    }

    @Test
    public void getAvailableHabitDictionaryNoAvailable() {
        when(habitDictionaryTranslationRepo.findAvailableHabitDictionaryByUser(1L, "en"))
            .thenReturn(Collections.emptyList());
        assertThrows(UserHasNoAvailableHabitDictionaryException.class, () ->
            userService.getAvailableHabitDictionary(user.getId(), "en")
        );
    }

    @Test
    public void createUserHabitTest() {
        when(habitRepo.saveAll(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(habitRepo.findByUserIdAndStatusHabit(user.getId())).thenReturn(Collections.emptyList());
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        assertEquals(Collections.emptyList(),
            userService.createUserHabit(user.getId(), Collections.emptyList(), anyString()));
    }

    @Test
    public void createUserHabitWithEmptyDtoIdListAndNotEmptyUserHabitsTest() {
        when(habitRepo.findByUserIdAndStatusHabit(user.getId())).thenReturn(Collections.singletonList(new Habit()));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        assertEquals(Collections.emptyList(), userService.createUserHabit(user.getId(), Collections.emptyList(), "en"));
    }

    @Test
    public void createUserHabitWithExistentHabitIdsNotMatchingTest() {
        when(habitRepo.findByUserIdAndStatusHabit(user.getId())).thenReturn(Collections
            .singletonList(new Habit(1L, new HabitDictionary(1L, null, null, null), null, null, null, null)));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(habitDictionaryService.findById(1L)).thenReturn(new HabitDictionary(1L, null, null, null));
        when(modelMapper.map(user, Habit.class))
            .thenReturn(new Habit(1L, new HabitDictionary(1L, null, null, null), null, null, null, null));
        assertEquals(Collections.emptyList(),
            userService.createUserHabit(user.getId(), Collections.singletonList(new HabitIdDto(2L)), "en"));
    }

    @Test
    public void createUserHabitWithExistentHabitTest() {
        when(habitRepo.findByUserIdAndStatusHabit(user.getId()))
            .thenReturn(
                Collections.singletonList(
                    Habit.builder()
                        .habitDictionary(HabitDictionary.builder().id(1L).build())
                        .build()
                )
            );
        assertThrows(WrongIdException.class, () ->
            userService.createUserHabit(user.getId(), Collections.singletonList(new HabitIdDto(1L)), "en")
        );
        verify(habitRepo, times(0)).saveAll(any());
    }

    @Test
    public void addDefaultHabitTest() {
        when(modelMapper.map(user, Habit.class)).thenReturn(new Habit());
        when(habitRepo.findByUserIdAndStatusHabit(user.getId())).thenReturn(Collections.emptyList());
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        userService.addDefaultHabit(user.getId(), "en");
        verify(habitRepo, times(1)).saveAll(Collections.singletonList(new Habit()));
    }

    @Test
    public void getAvailableCustomGoalsForUserWithNoGoalsTest() {
        when(customGoalRepo.findAllAvailableCustomGoalsForUserId(user.getId())).thenReturn(Collections.emptyList());
        assertNull(userService.getAvailableCustomGoals(user.getId()));
    }

    @Test
    public void getAvailableCustomGoalsForUserWithExistentGoalTest() {
        List<CustomGoalResponseDto> customGoalsDtos = Collections.singletonList(new CustomGoalResponseDto(1L, "foo"));
        when(modelMapper.map(customGoalRepo.findAllAvailableCustomGoalsForUserId(user.getId()),
            new TypeToken<List<CustomGoalResponseDto>>() {
            }.getType()))
            .thenReturn(customGoalsDtos);
        assertNotNull(userService.getAvailableCustomGoals(user.getId()));
        assertEquals(userService.getAvailableCustomGoals(user.getId()), customGoalsDtos);
    }

    @Test
    public void deleteHabitByUserIdAndHabitDictionaryEmptyHabitTest() {
        when(habitRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(WrongIdException.class, () ->
            userService.deleteHabitByUserIdAndHabitDictionary(1L, 1L)
        );
    }

    @Test
    public void deleteHabitByUserIdAndHabitDictionaryNotDeletedExceptionTest() {
        when(habitRepo.findById(anyLong())).thenReturn(Optional.of(new Habit()));
        when(habitRepo.countHabitByUserId(user.getId())).thenReturn(1);
        assertThrows(NotDeletedException.class, () ->
            userService.deleteHabitByUserIdAndHabitDictionary(1L, 1L)
        );
    }

    @Test
    public void deleteHabitByUserIdAndHabitDictionaryExceptionTest() {
        assertThrows(WrongIdException.class, () ->
            userService.deleteHabitByUserIdAndHabitDictionary(null, 1L)
        );
    }

    @Test
    public void deleteHabitByUserIdAndHabitDictionaryTest() {
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepo.countHabitByUserId(1L)).thenReturn(2);
        when(habitStatisticRepo.findAllByHabitId(1L)).thenReturn(Collections.emptyList());
        userService.deleteHabitByUserIdAndHabitDictionary(user.getId(), habit.getId());
        verify(habitRepo, times(1)).deleteById(habit.getId());
    }

    @Test
    public void deleteHabitByUserAndNullHabit() {
        assertThrows(NotDeletedException.class, () ->
            userService.deleteHabitByUserIdAndHabitDictionary(user.getId(), null)
        );
    }

    @Test
    public void deleteHabitByNullUserAndNullHabit() {
        assertThrows(NotDeletedException.class, () ->
            userService.deleteHabitByUserIdAndHabitDictionary(null, null)
        );
    }

    @Test
    public void deleteHabitByUserWithExistentHabits() {
        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(habitStatisticRepo.findAllByHabitId(habit.getId())).thenReturn(Collections.singletonList(null));
        when(habitRepo.countHabitByUserId(user.getId())).thenReturn(2);
        userService.deleteHabitByUserIdAndHabitDictionary(user.getId(), habit.getId());
        verify(habitRepo).updateHabitStatusById(habit.getId(), false);
    }

    @Test
    public void getActivatedUsersAmountTest() {
        when(userRepo.countAllByUserStatus(UserStatus.ACTIVATED)).thenReturn(1L);
        long activatedUsersAmount = userService.getActivatedUsersAmount();
        assertEquals(1L, activatedUsersAmount);
    }
}