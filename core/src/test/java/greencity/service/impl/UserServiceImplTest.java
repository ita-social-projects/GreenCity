package greencity.service.impl;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.goal.CustomGoalRequestDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.goal.GoalRequestDto;
import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.dto.habitstatistic.HabitIdDto;
import greencity.dto.user.*;
import greencity.entity.*;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.GoalStatus;
import greencity.entity.enums.ROLE;
import static greencity.entity.enums.UserStatus.ACTIVATED;
import static greencity.entity.enums.UserStatus.DEACTIVATED;
import greencity.entity.localization.GoalTranslation;
import greencity.exception.exceptions.*;
import greencity.repository.*;
import greencity.service.FileService;
import greencity.service.HabitDictionaryService;
import greencity.service.HabitService;
import greencity.service.SocialNetworkImageService;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(SpringExtension.class)
class UserServiceImplTest {
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
    FileService fileService;

    @Mock
    HabitDictionaryService habitDictionaryService;

    @Mock
    HabitDictionaryTranslationRepo habitDictionaryTranslationRepo;

    @Mock
    EcoNewsRepo ecoNewsRepo;

    @Mock
    TipsAndTricksRepo tipsAndTricksRepo;

    @Mock
    HabitService habitService;

    @Mock
    SocialNetworkImageService socialNetworkImageService;

    private User user =
        User.builder()
            .id(1L)
            .name("Test Testing")
            .email("test@gmail.com")
            .role(ROLE.ROLE_USER)
            .userStatus(ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .socialNetworks(new ArrayList<>())
            .build();
    private User user2 =
        User.builder()
            .id(2L)
            .name("Test Testing")
            .email("test@gmail.com")
            .role(ROLE.ROLE_MODERATOR)
            .userStatus(ACTIVATED)
            .emailNotification(EmailNotification.DISABLED)
            .lastVisit(LocalDateTime.now())
            .dateOfRegistration(LocalDateTime.now())
            .build();
    private Habit habit =
        Habit.builder()
            .id(1L)
            .habitDictionary(new HabitDictionary())
            .statusHabit(true)
            .createDate(ZonedDateTime.now())
            .build();
    private String language = "uk";
    private List<GoalTranslation> goalTranslations = Arrays.asList(
        new GoalTranslation(1L, new Language(1L, language, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()), "TEST",
            new Goal(1L, Collections.emptyList(), Collections.emptyList())),
        new GoalTranslation(2L, new Language(1L, language, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()), "TEST",
            new Goal(2L, Collections.emptyList(), Collections.emptyList())));
    private Long userId = user.getId();
    private String userEmail = user.getEmail();

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private ModelMapper modelMapper;

    @Test
    void saveTest() {
        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.ofNullable(user));
        when(userService.findByEmail(userEmail))
            .thenThrow(new WrongEmailException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL));
        when(userRepo.save(user)).thenReturn(user);
        assertEquals(user, userService.save(user));
    }

    @Test
    void updateUserStatusDeactivatedTest() {
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(userRepo.save(any())).thenReturn(user);
        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());
        assertEquals(
            DEACTIVATED,
            userService.updateStatus(userId, DEACTIVATED, any()).getUserStatus());
    }

    @Test
    void updateUserStatusLowRoleLevelException() {
        user.setRole(ROLE.ROLE_MODERATOR);
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        assertThrows(LowRoleLevelException.class, () ->
            userService.updateStatus(userId, DEACTIVATED, "email")
        );
    }

    @Test
    void updateRoleTest() {
        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user2));
        when(userRepo.save(any())).thenReturn(user);
        assertEquals(
            ROLE.ROLE_MODERATOR,
            userService.updateRole(userId, ROLE.ROLE_MODERATOR, any()).getRole());
        verify(userRepo, times(1)).save(any());
    }

    @Test
    void updateRoleOnTheSameUserTest() {
        when(userRepo.findByEmail(userEmail)).thenReturn(Optional.of(user));
        assertThrows(BadUpdateRequestException.class, () ->
            userService.updateRole(userId, null, userEmail)
        );
    }

    @Test
    void findByIdTest() {
        Long id = 1L;

        User user = new User();
        user.setId(1L);

        when(userRepo.findById(id)).thenReturn(Optional.of(user));

        assertEquals(user, userService.findById(id));
        verify(userRepo, times(1)).findById(id);
    }

    @Test
    void findByIdBadIdTest() {
        when(userRepo.findById(any())).thenThrow(WrongIdException.class);
        assertThrows(WrongIdException.class, () ->
            userService.findById(1L)
        );
    }

    @Test
    void deleteByIdExceptionBadIdTest() {
        assertThrows(WrongIdException.class, () ->
            userService.deleteById(1L)
        );
    }

    @Test
    void deleteByNullIdExceptionTest() {
        when(userRepo.findById(1L)).thenThrow(new WrongIdException(""));
        assertThrows(WrongIdException.class, () ->
            userService.deleteById(1L)
        );
    }

    @Test
    void deleteByExistentIdTest() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        userService.deleteById(userId);
        verify(userRepo).delete(user);
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    void findIdByEmail() {
        String email = "email";
        when(userRepo.findIdByEmail(email)).thenReturn(Optional.of(2L));
        assertEquals(2L, (long) userService.findIdByEmail(email));
    }

    /**
     * @author Zakhar Skaletskyi
     */
    @Test
    void findIdByEmailNotFound() {
        String email = "email";

        assertThrows(WrongEmailException.class, () ->
            userService.findIdByEmail(email)
        );
    }

    @Test
    void findByPage() {
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
                userForListDtos.size(), 0, 1);

        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());

        when(userRepo.findAll(pageable)).thenReturn(usersPage);

        assertEquals(userPageableDto, userService.findByPage(pageable));
        verify(userRepo, times(1)).findAll(pageable);
    }

    @Test
    void getRoles() {
        RoleDto roleDto = new RoleDto(ROLE.class.getEnumConstants());
        assertEquals(roleDto, userService.getRoles());
    }

    @Test
    void getEmailStatusesTest() {
        List<EmailNotification> placeStatuses =
            Arrays.asList(EmailNotification.class.getEnumConstants());

        assertEquals(placeStatuses, userService.getEmailNotificationsStatuses());
    }

    @Test
    void updateLastVisit() {
        LocalDateTime localDateTime = user.getLastVisit().minusHours(1);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        assertNotEquals(localDateTime, userService.updateLastVisit(user).getLastVisit());
    }


    @Test
    void getUsersByFilter() {
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
                userForListDtos.size(), 0, 1);

        ReflectionTestUtils.setField(userService, "modelMapper", new ModelMapper());

        when(userRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(usersPage);

        assertEquals(userPageableDto, userService.getUsersByFilter(filterUserDto, pageable));
    }


    @Test
    void getUserUpdateDtoByEmail() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName(user.getName());
        userUpdateDto.setEmailNotification(user.getEmailNotification());
        when(modelMapper.map(any(), any())).thenReturn(userUpdateDto);
        UserUpdateDto userInitialsByEmail = userService.getUserUpdateDtoByEmail("");
        assertEquals(userInitialsByEmail.getName(), user.getName());
        assertEquals(userInitialsByEmail.getEmailNotification(), user.getEmailNotification());
    }

    @Test
    void update() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName(user.getName());
        userUpdateDto.setEmailNotification(user.getEmailNotification());
        assertEquals(userUpdateDto, userService.update(userUpdateDto, ""));
        verify(userRepo, times(1)).save(any());
    }

    @Test
    void getUserGoalsTest() {
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

        when(userGoalRepo.findAllByUserId(userId)).thenReturn(userGoals);
        when(modelMapper.map(userGoals.get(0), UserGoalResponseDto.class)).thenReturn(customDtoWithoutText);
        when(modelMapper.map(userGoals.get(1), UserGoalResponseDto.class)).thenReturn(predefinedDtoWithoutText);
        when(userGoalRepo.findGoalByUserGoalId(userGoals.get(0).getId())).thenReturn(Optional.empty());
        when(userGoalRepo.findGoalByUserGoalId(userGoals.get(1).getId()))
            .thenReturn(Optional.of(predefinedUserGoal.getGoal()));
        when(goalTranslationRepo.findByUserIdLangAndUserGoalId(anyLong(), anyString(), anyLong()))
            .thenReturn(goalTranslations.get(0));
        when(customGoalRepo.findByUserGoalIdAndUserId(anyLong(), anyLong())).thenReturn(customGoal);

        assertEquals(userService.getUserGoals(userId, "en"), userGoalDtos);
    }

    @Test
    void getUserGoalsUserHasNoGoalTest() {
        when(userGoalRepo.findAllByUserId(userId)).thenReturn(Collections.emptyList());
        assertThrows(UserHasNoGoalsException.class, () ->
            userService.getUserGoals(userId, "en")
        );
    }

    @Test
    void updateUserRefreshTokenForUserWithExistentIdTest() {
        when(userRepo.updateUserRefreshToken("foo", userId)).thenReturn(1);
        int updatedRows = userService.updateUserRefreshToken("foo", userId);
        assertEquals(1, updatedRows);
    }

    @Test
    void updateUserGoalStatusWithNonExistentGoalIdTest() {
        user.setUserGoals(Collections.singletonList(new UserGoal(1L, null, null, null, null, null)));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        assertThrows(UserGoalStatusNotUpdatedException.class, () ->
            userService.updateUserGoalStatus(userId, 2L, "en")
        );
        verifyNoInteractions(userGoalRepo);
    }

    @Test
    void updateUserGoalStatusWithDisabledGoalStateTest() {
        CustomGoal customgoal = CustomGoal.builder().id(3L).text("foo").build();
        UserGoal userGoal = new UserGoal(1L, null, null, customgoal, GoalStatus.DISABLED, null);
        when(userGoalRepo.getOne(userGoal.getId())).thenReturn(userGoal);
        user.setUserGoals(Collections.singletonList(userGoal));
        when(modelMapper.map(any(), eq(UserGoalResponseDto.class))).thenReturn(
            new UserGoalResponseDto(1L, null, GoalStatus.ACTIVE));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userGoalRepo.findGoalByUserGoalId(anyLong())).thenReturn(Optional.empty());
        when(customGoalRepo.findByUserGoalIdAndUserId(anyLong(), anyLong())).thenReturn(customgoal);
        UserGoalResponseDto result = userService.updateUserGoalStatus(userId, userGoal.getId(), "en");
        assertEquals("foo", result.getText());
        verify(userGoalRepo, times(0)).save(userGoal);
    }

    @Test
    void updateUserGoalStatusWithActiveGoalStateTest() {
        UserGoal userGoal = ModelUtils.getPredefinedUserGoal();
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userGoalRepo.getOne(userGoal.getId())).thenReturn(userGoal);
        when(modelMapper.map(any(), eq(UserGoalResponseDto.class)))
            .thenReturn(new UserGoalResponseDto(2L, null, GoalStatus.DONE));
        user.setUserGoals(Collections.singletonList(userGoal));
        when(userGoalRepo.findGoalByUserGoalId(anyLong())).thenReturn(Optional.of(userGoal.getGoal()));
        when(goalTranslationRepo.findByUserIdLangAndUserGoalId(anyLong(), anyString(), anyLong()))
            .thenReturn(goalTranslations.get(0));
        UserGoalResponseDto userGoalResponseDto =
            userService.updateUserGoalStatus(userId, userGoal.getId(), "en");

        assertEquals(GoalStatus.DONE, userGoal.getStatus());
        assertEquals(userGoalResponseDto,
            new UserGoalResponseDto(2L, goalTranslations.get(0).getText(), GoalStatus.DONE));
        verify(userGoalRepo).save(userGoal);
    }

    @Test
    void updateUserGoalStatusWithDoneGoalStateTest() {
        CustomGoal customgoal = CustomGoal.builder().id(3L).text("foo").build();
        UserGoal userGoal = new UserGoal(1L, null, null, customgoal, GoalStatus.DONE, null);
        when(userGoalRepo.getOne(userGoal.getId())).thenReturn(userGoal);
        when(modelMapper.map(any(), eq(UserGoalResponseDto.class)))
            .thenReturn(new UserGoalResponseDto(1L, null, GoalStatus.ACTIVE));
        user.setUserGoals(Collections.singletonList(userGoal));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userGoalRepo.findGoalByUserGoalId(anyLong())).thenReturn(Optional.empty());
        when(customGoalRepo.findByUserGoalIdAndUserId(anyLong(), anyLong())).thenReturn(customgoal);
        UserGoalResponseDto userGoalResponseDto =
            userService.updateUserGoalStatus(userId, userGoal.getId(), "en");
        UserGoalResponseDto expectedUserGoalResponseDto = new UserGoalResponseDto(1L, "foo", GoalStatus.ACTIVE);

        assertEquals(GoalStatus.ACTIVE, userGoal.getStatus());
        assertEquals(userGoalResponseDto, expectedUserGoalResponseDto);
        verify(userGoalRepo).save(userGoal);
    }

    @Test
    void getAvailableGoalsTest() {
        List<GoalDto> goalDto = Arrays.asList(new GoalDto(1L, "TEST"), new GoalDto(2L, "TEST"));

        when(goalTranslationRepo.findAvailableByUserId(userId, language)).thenReturn(goalTranslations);
        when(modelMapper.map(goalTranslations.get(0), GoalDto.class)).thenReturn(goalDto.get(0));
        when(modelMapper.map(goalTranslations.get(1), GoalDto.class)).thenReturn(goalDto.get(1));

        assertEquals(goalDto, userService.getAvailableGoals(userId, language));
    }

    @Test
    void saveUserGoalsWithNullUserGoalsAndExistentCustomGoalsTest() {
        user.setUserGoals(new ArrayList<>());
        UserCustomGoalDto userCustomGoalDto = new UserCustomGoalDto(new CustomGoalRequestDto(8L));
        BulkSaveUserGoalDto nullUserGoalsDto =
            new BulkSaveUserGoalDto(null, Collections.singletonList(userCustomGoalDto));
        UserGoal customUserGoal = ModelUtils.getCustomUserGoal();
        List<UserGoal> userGoals = Collections.singletonList(customUserGoal);
        UserGoalResponseDto customUserGoalDto = ModelUtils.getCustomUserGoalDto();
        List<UserGoalResponseDto> userGoalDtos = Collections.singletonList(customUserGoalDto);
        CustomGoal customGoal = CustomGoal.builder().id(8L).text("Buy electric car").build();

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(userCustomGoalDto, UserGoal.class)).thenReturn(customUserGoal);
        when(userGoalRepo.findAllByUserId(userId)).thenReturn(userGoals);
        when(modelMapper.map(userGoals.get(0), UserGoalResponseDto.class)).thenReturn(userGoalDtos.get(0));
        when(customGoalRepo.findByUserGoalIdAndUserId(userGoalDtos.get(0).getId(), userId))
            .thenReturn(customGoal);

        List<UserGoalResponseDto> result = userService.saveUserGoals(userId, nullUserGoalsDto, "en");
        assertEquals("Buy electric car", result.get(0).getText());
        verify(userGoalRepo).saveAll(user.getUserGoals());
        verify(modelMapper).map(userCustomGoalDto, UserGoal.class);
    }

    @Test
    void saveUserGoalsWithExistentUserGoalsAndNullCustomGoalsTest() {
        user.setUserGoals(new ArrayList<>());
        UserGoalDto userGoalDto = new UserGoalDto(new GoalRequestDto(2L));
        BulkSaveUserGoalDto nullCustomGoalsDto = new BulkSaveUserGoalDto(
            Collections.singletonList(userGoalDto), null);
        UserGoal predefinedUserGoal = ModelUtils.getPredefinedUserGoal();
        List<UserGoal> userGoals = Collections.singletonList(predefinedUserGoal);
        UserGoalResponseDto predefinedUserGoalDto = ModelUtils.getPredefinedUserGoalDto();
        List<UserGoalResponseDto> userGoalDtos = Collections.singletonList(predefinedUserGoalDto);
        List<GoalTranslation> goalTranslations = ModelUtils.getGoalTranslations();

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(userGoalDto, UserGoal.class)).thenReturn(predefinedUserGoal);
        when(userGoalRepo.findAllByUserId(userId)).thenReturn(userGoals);
        when(modelMapper.map(userGoals.get(0), UserGoalResponseDto.class)).thenReturn(userGoalDtos.get(0));
        when(userGoalRepo.findGoalByUserGoalId(userGoals.get(0).getId()))
            .thenReturn(Optional.of(predefinedUserGoal.getGoal()));
        when(goalTranslationRepo.findByUserIdLangAndUserGoalId(anyLong(), anyString(), anyLong()))
            .thenReturn(goalTranslations.get(0));

        List<UserGoalResponseDto> result = userService.saveUserGoals(userId, nullCustomGoalsDto, "en");
        assertEquals("Buy a bamboo toothbrush", result.get(0).getText());
        verify(userGoalRepo).saveAll(user.getUserGoals());
        verify(modelMapper).map(userGoalDto, UserGoal.class);
    }

    @Test
    void saveUserGoalsWithExistentUserGoalsAndExistentCustomGoalsTest() {
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

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(userGoalDto, UserGoal.class)).thenReturn(predefinedUserGoal);
        when(modelMapper.map(userCustomGoalDto, UserGoal.class)).thenReturn(customUserGoal);
        when(userGoalRepo.findAllByUserId(userId)).thenReturn(userGoals);
        when(modelMapper.map(userGoals.get(0), UserGoalResponseDto.class)).thenReturn(userGoalDtos.get(0));
        when(modelMapper.map(userGoals.get(1), UserGoalResponseDto.class)).thenReturn(userGoalDtos.get(1));
        when(userGoalRepo.findGoalByUserGoalId(userGoals.get(0).getId())).thenReturn(Optional.empty());
        when(userGoalRepo.findGoalByUserGoalId(userGoals.get(1).getId()))
            .thenReturn(Optional.of(predefinedUserGoal.getGoal()));
        when(goalTranslationRepo.findByUserIdLangAndUserGoalId(anyLong(), anyString(), anyLong()))
            .thenReturn(goalTranslations.get(0));
        when(customGoalRepo.findByUserGoalIdAndUserId(anyLong(), anyLong())).thenReturn(customGoal);

        List<UserGoalResponseDto> result = userService.saveUserGoals(userId, userGoalsAndCustomGoalsDto, "en");
        assertEquals("Buy electric car", result.get(0).getText());
        verify(userGoalRepo, times(2)).saveAll(user.getUserGoals());
        verify(modelMapper).map(userGoalDto, UserGoal.class);
        verify(modelMapper).map(userCustomGoalDto, UserGoal.class);
    }

    @Test
    void deleteUserGoalsWithValidInputIdsTest() {
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
    void getAvailableGoalsNoAvailableGoalsTest() {
        when(goalTranslationRepo.findAvailableByUserId(userId, language)).thenReturn(Collections.emptyList());
        assertThrows(UserHasNoAvailableGoalsException.class, () ->
            userService.getAvailableGoals(userId, language)
        );
    }

    @Test
    void getAvailableHabitDictionaryNoAvailable() {
        when(habitDictionaryTranslationRepo.findAvailableHabitDictionaryByUser(1L, "en"))
            .thenReturn(Collections.emptyList());
        assertThrows(UserHasNoAvailableHabitDictionaryException.class, () ->
            userService.getAvailableHabitDictionary(userId, "en")
        );
    }

    @Test
    void createUserHabitTest() {
        when(habitRepo.saveAll(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(habitRepo.findByUserIdAndStatusHabit(userId)).thenReturn(Collections.emptyList());
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        assertEquals(Collections.emptyList(),
            userService.createUserHabit(userId, Collections.emptyList(), anyString()));
    }

    @Test
    void createUserHabitWithEmptyDtoIdListAndNotEmptyUserHabitsTest() {
        when(habitRepo.findByUserIdAndStatusHabit(userId)).thenReturn(Collections.singletonList(new Habit()));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        assertEquals(Collections.emptyList(), userService.createUserHabit(userId, Collections.emptyList(), "en"));
    }

    @Test
    void createUserHabitWithExistentHabitIdsNotMatchingTest() {
        when(habitRepo.findByUserIdAndStatusHabit(userId)).thenReturn(Collections
            .singletonList(new Habit(1L, new HabitDictionary(1L, null, null, null), null, null, null, null, null)));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(habitDictionaryService.findById(1L)).thenReturn(new HabitDictionary(1L, null, null, null));
        when(modelMapper.map(user, Habit.class))
            .thenReturn(new Habit(1L, new HabitDictionary(1L, null, null, null), null, null, null, null, null));
        assertEquals(Collections.emptyList(),
            userService.createUserHabit(userId, Collections.singletonList(new HabitIdDto(2L)), "en"));
    }

    @Test
    void createUserHabitWithExistentHabitTest() {
        when(habitRepo.findByUserIdAndStatusHabit(userId))
            .thenReturn(
                Collections.singletonList(
                    Habit.builder()
                        .habitDictionary(HabitDictionary.builder().id(1L).build())
                        .build()
                )
            );

        List<HabitIdDto> dto = Collections.singletonList(new HabitIdDto(1L));

        assertThrows(WrongIdException.class, () ->
            userService.createUserHabit(userId, dto, "en")
        );
        verify(habitRepo, times(0)).saveAll(any());
    }

    @Test
    void addDefaultHabitTest() {
        when(modelMapper.map(user, Habit.class)).thenReturn(new Habit());
        when(habitRepo.findByUserIdAndStatusHabit(userId)).thenReturn(Collections.emptyList());
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        userService.addDefaultHabit(userId, "en");
        verify(habitRepo, times(1)).saveAll(Collections.singletonList(new Habit()));
    }

    @Test
    void getAvailableCustomGoalsForUserWithNoGoalsTest() {
        when(customGoalRepo.findAllAvailableCustomGoalsForUserId(userId)).thenReturn(Collections.emptyList());
        assertNull(userService.getAvailableCustomGoals(userId));
    }

    @Test
    void getAvailableCustomGoalsForUserWithExistentGoalTest() {
        List<CustomGoalResponseDto> customGoalsDtos = Collections.singletonList(new CustomGoalResponseDto(1L, "foo"));
        when(modelMapper.map(customGoalRepo.findAllAvailableCustomGoalsForUserId(userId),
            new TypeToken<List<CustomGoalResponseDto>>() {
            }.getType()))
            .thenReturn(customGoalsDtos);
        assertNotNull(userService.getAvailableCustomGoals(userId));
        assertEquals(userService.getAvailableCustomGoals(userId), customGoalsDtos);
    }

    @Test
    void deleteHabitByUserIdAndHabitDictionaryEmptyHabitTest() {
        when(habitRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(WrongIdException.class, () ->
            userService.deleteHabitByUserIdAndHabitDictionary(1L, 1L)
        );
    }

    @Test
    void deleteHabitByUserIdAndHabitDictionaryNotDeletedExceptionTest() {
        when(habitRepo.findById(anyLong())).thenReturn(Optional.of(new Habit()));
        when(habitRepo.countHabitByUserId(userId)).thenReturn(1);
        assertThrows(NotDeletedException.class, () ->
            userService.deleteHabitByUserIdAndHabitDictionary(1L, 1L)
        );
    }

    @Test
    void deleteHabitByUserIdAndHabitDictionaryExceptionTest() {
        assertThrows(WrongIdException.class, () ->
            userService.deleteHabitByUserIdAndHabitDictionary(null, 1L)
        );
    }

    @Test
    void deleteHabitByUserIdAndHabitDictionaryTest() {
        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(habitRepo.countHabitByUserId(1L)).thenReturn(2);
        when(habitStatisticRepo.findAllByHabitId(1L)).thenReturn(Collections.emptyList());
        userService.deleteHabitByUserIdAndHabitDictionary(userId, habit.getId());
        verify(habitRepo, times(1)).deleteById(habit.getId());
    }

    @Test
    void deleteHabitByUserAndNullHabit() {
        assertThrows(NotDeletedException.class, () ->
            userService.deleteHabitByUserIdAndHabitDictionary(userId, null)
        );
    }

    @Test
    void deleteHabitByNullUserAndNullHabit() {
        assertThrows(NotDeletedException.class, () ->
            userService.deleteHabitByUserIdAndHabitDictionary(null, null)
        );
    }

    @Test
    void deleteHabitByUserWithExistentHabits() {
        when(habitRepo.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(habitStatisticRepo.findAllByHabitId(habit.getId())).thenReturn(Collections.singletonList(null));
        when(habitRepo.countHabitByUserId(userId)).thenReturn(2);
        userService.deleteHabitByUserIdAndHabitDictionary(userId, habit.getId());
        verify(habitRepo).updateHabitStatusById(habit.getId(), false);
    }

    @Test
    void getActivatedUsersAmountTest() {
        when(userRepo.countAllByUserStatus(ACTIVATED)).thenReturn(1L);
        long activatedUsersAmount = userService.getActivatedUsersAmount();
        assertEquals(1L, activatedUsersAmount);
    }

    @Test
    void getProfilePicturePathByUserIdNotFoundExceptionTest() {
        assertThrows(NotFoundException.class, () ->
            userService.getProfilePicturePathByUserId(1L)
        );
    }

    @Test
    void getProfilePicturePathByUserIdTest() {
        when(userRepo.getProfilePicturePathByUserId(1L)).thenReturn(Optional.of(anyString()));
        userService.getProfilePicturePathByUserId(1L);
        verify(userRepo).getProfilePicturePathByUserId(1L);
    }

    @Test
    void updateUserProfilePictureTest() throws MalformedURLException {
        UserProfilePictureDto userProfilePictureDto = ModelUtils.getUserProfilePictureDto();
        userProfilePictureDto.setProfilePicturePath(null);

        MultipartFile image = new MockMultipartFile("data", "filename.png",
            "image/png", "some xml".getBytes());
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(fileService.upload(image)).thenReturn(new URL("http://test.com"));

        userService.updateUserProfilePicture(image, anyString(), userProfilePictureDto);
        verify(userRepo).save(user);
    }

    @Test
    void updateUserProfilePictureNotUpdatedExceptionTest() {
        UserProfilePictureDto userProfilePictureDto = ModelUtils.getUserProfilePictureDto();
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class, () ->
            userService.updateUserProfilePicture(null, "testmail@gmail.com", userProfilePictureDto)
        );
    }

    @Test
    void deleteUserFriendByIdCheckRepeatingValueExceptionTest() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user2));
        assertThrows(CheckRepeatingValueException.class, () ->
            userService.deleteUserFriendById(1L, 1L));
    }

    @Test
    void deleteUserFriendByIdTest() {
        List<User> list = Collections.singletonList(user2);
        when(userRepo.getAllUserFriends(anyLong())).thenReturn(list);
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user2));
        userService.deleteUserFriendById(userId, user2.getId());
        verify(userRepo).deleteUserFriendById(userId, user2.getId());
    }

    @Test
    void deleteUserFriendByIdNotDeletedExceptionTest() {
        when(userRepo.getAllUserFriends(1L)).thenReturn(Collections.emptyList());
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        assertThrows(NotDeletedException.class, () ->
            userService.deleteUserFriendById(1L, 2L));
    }

    @Test
    void deleteUserFriendByIdNotDeletedExceptionTest2() {
        when(userRepo.getAllUserFriends(any())).thenReturn(Collections.singletonList(user));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user2));
        assertThrows(NotDeletedException.class, () ->
            userService.deleteUserFriendById(3L, 2L));
    }


    @Test
    void addNewFriendCheckRepeatingValueExceptionWithSameIdTest() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        assertThrows(CheckRepeatingValueException.class, () ->
            userService.addNewFriend(1L, 1L)
        );
    }

    @Test
    void addNewFriendCheckRepeatingValueExceptionWithSameIdTest2() {
        when(userRepo.getAllUserFriends(any())).thenReturn(Collections.singletonList(user2));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        assertThrows(CheckRepeatingValueException.class, () ->
            userService.addNewFriend(1L, 2L)
        );
    }

    @Test
    void addNewFriendTest() {
        when(userRepo.getAllUserFriends(anyLong())).thenReturn(Collections.singletonList(user));
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user2));
        user.setUserFriends(Collections.singletonList(user2));
        userService.addNewFriend(1L, 2L);
        verify(userRepo).addNewFriend(1L, 2L);
    }

    @Test
    void getSixFriendsWithTheHighestRatingExceptionTest() {
        assertThrows(NotFoundException.class, () ->
            userService.getSixFriendsWithTheHighestRating(1L)
        );
    }

    @Test
    void getSixFriendsWithTheHighestRatingTest() {
        UserProfilePictureDto e = new UserProfilePictureDto();
        List<UserProfilePictureDto> list = Collections.singletonList(e);
        when(userRepo.getSixFriendsWithTheHighestRating(1L)).thenReturn(Collections.singletonList(user));
        when(modelMapper.map(user, UserProfilePictureDto.class)).thenReturn(e);
        assertEquals(list, userService.getSixFriendsWithTheHighestRating(1L));
    }

    @Test
    void saveUserProfileTest() {
        UserProfileDtoRequest request = new UserProfileDtoRequest();
        request.setSocialNetworks(new ArrayList<>());
        SocialNetworkImage socialNetworkImage = new SocialNetworkImage();
        socialNetworkImage.builder()
            .id(1L)
            .imagePath("test")
            .hostPath("test")
            .build();
        SocialNetwork socialNetwork = new SocialNetwork();
        socialNetwork.builder()
            .id(1L)
            .url("http://test.com")
            .user(user)
            .socialNetworkImage(socialNetworkImage)
            .build();
        request.setSocialNetworks(Collections.singletonList("test"));
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(socialNetworkImageService.getSocialNetworkImageByUrl(anyString())).thenReturn(socialNetworkImage);
        userService.saveUserProfile(request, "teststring");
        verify(userRepo).save(user);
        verify(modelMapper).map(user, UserProfileDtoResponse.class);
    }

    @Test
    void getUserProfileInformationTest() {
        UserProfileDtoResponse response = new UserProfileDtoResponse();
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserProfileDtoResponse.class)).thenReturn(response);
        assertEquals(response, userService.getUserProfileInformation(1L));
    }

    @Test
    void getUserProfileInformationExceptionTest() {
        assertThrows(WrongIdException.class, () -> userService.getUserProfileInformation(null));
    }

    @Test
    void updateUserLastActivityTimeTest() {
        Date currentTime = new Date();
        userService.updateUserLastActivityTime(userId, currentTime);
        verify(userRepo).updateUserLastActivityTime(userId, currentTime);
    }

    @Test
    void checkIfTheUserIsOnlineExceptionTest() {
        assertThrows(WrongIdException.class, () ->
            userService.checkIfTheUserIsOnline(null)
        );
    }

    @Test
    void checkIfTheUserIsOnlineEqualsTrueTest() {
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        Timestamp userLastActivityTime = Timestamp.valueOf(LocalDateTime.now());
        User user = ModelUtils.getUser();

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(userLastActivityTime));

        assertTrue(userService.checkIfTheUserIsOnline(1L));
    }

    @Test
    void checkIfTheUserIsOnlineEqualsFalseTest() {
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        LocalDateTime localDateTime = LocalDateTime.of(
            2015, Month.JULY, 29, 19, 30, 40);
        Timestamp userLastActivityTime = Timestamp.valueOf(localDateTime);
        User user = ModelUtils.getUser();

        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.empty());

        assertFalse(userService.checkIfTheUserIsOnline(1L));
    }

    @Test
    void findUserForManagementByPage() {
        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<User> userList = Collections.singletonList(ModelUtils.getUser());
        Page<User> users = new PageImpl<>(userList, pageable, userList.size());
        List<UserManagementDto> userManagementDtos =
            users.getContent().stream()
                .map(user -> modelMapper.map(user, UserManagementDto.class))
                .collect(Collectors.toList());
        PageableAdvancedDto<UserManagementDto> userManagementDtoPageableDto = new PageableAdvancedDto<>(
            userManagementDtos,
            users.getTotalElements(),
            users.getPageable().getPageNumber(),
            users.getTotalPages(),
            users.getNumber(),
            users.hasPrevious(),
            users.hasNext(),
            users.isFirst(),
            users.isLast());
        when(userRepo.findAll(pageable)).thenReturn(users);
        assertEquals(userManagementDtoPageableDto, userService.findUserForManagementByPage(pageable));
    }

    @Test
    void updateUser() {
        UserManagementDto userManagementDto = new UserManagementDto();
        userManagementDto.setId(1L);
        userManagementDto.setName(user.getName());
        userManagementDto.setRole(user.getRole());
        userManagementDto.setUserStatus(user.getUserStatus());
        userManagementDto.setEmail(user.getEmail());
        userManagementDto.setUserCredo(user.getUserCredo());
        User excepted = user;
        excepted.setName(userManagementDto.getName());
        excepted.setEmail(userManagementDto.getEmail());
        excepted.setRole(userManagementDto.getRole());
        excepted.setUserCredo(userManagementDto.getUserCredo());
        excepted.setUserStatus(userManagementDto.getUserStatus());
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        userService.updateUser(userManagementDto);
        assertEquals(excepted, user);
    }

    @Test
    void findNotDeactivatedByEmail() {
        String email = "test@gmail.com";
        user.setEmail(email);
        when(userRepo.findNotDeactivatedByEmail(email)).thenReturn(Optional.of(user));
        assertEquals(Optional.of(user), userService.findNotDeactivatedByEmail(email));
    }


    @Test
    void getAvailableHabitDictionary() {
        HabitDictionaryTranslation habitDictionaryTranslation = ModelUtils.getHabitDictionaryTranslation();

        HabitDictionaryDto hd = new HabitDictionaryDto();
        List<HabitDictionaryDto> habitDictionaryDtos = new ArrayList<>();
        hd.setId(habitDictionaryTranslation.getHabitDictionary().getId());
        hd.setName(habitDictionaryTranslation.getName());
        hd.setDescription(habitDictionaryTranslation.getDescription());
        hd.setHabitItem(habitDictionaryTranslation.getHabitItem());
        hd.setImage(habitDictionaryTranslation.getHabitDictionary().getImage());
        habitDictionaryDtos.add(hd);

        when(habitDictionaryTranslationRepo.findAvailableHabitDictionaryByUser(userId, "en"))
            .thenReturn(Collections.singletonList(habitDictionaryTranslation));
        assertEquals(habitDictionaryDtos, userService.getAvailableHabitDictionary(userId, "en"));
    }


    @Test
    void getUserProfileStatistics() {
        UserProfileStatisticsDto userProfileStatisticsDto = UserProfileStatisticsDto.builder()
            .amountWrittenTipsAndTrick(1L)
            .amountPublishedNews(1L)
            .amountHabitsAcquired(1L)
            .amountHabitsInProgress(1L)
            .build();

        when(ecoNewsRepo.getAmountOfPublishedNewsByUserId(userId)).thenReturn(1L);
        when(tipsAndTricksRepo.getAmountOfWrittenTipsAndTrickByUserId(userId)).thenReturn(1L);
        when(habitStatisticRepo.getAmountOfAcquiredHabitsByUserId(userId)).thenReturn(1L);
        when(habitStatisticRepo.getAmountOfHabitsInProgressByUserId(userId)).thenReturn(1L);
        assertEquals(userProfileStatisticsDto, userService.getUserProfileStatistics(userId));
    }

    @Test
    void getUserAndSixFriendsWithOnlineStatus() {
        UserWithOnlineStatusDto userWithOnlineStatusDto = UserWithOnlineStatusDto.builder()
            .id(userId)
            .onlineStatus(true)
            .build();
        List<UserWithOnlineStatusDto> sixFriendsWithOnlineStatusDtos = new ArrayList<>();
        sixFriendsWithOnlineStatusDtos = Collections.singletonList(user)
            .stream()
            .map(u -> new UserWithOnlineStatusDto(u.getId(), true))
            .collect(Collectors.toList());
        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        Timestamp userLastActivityTime = Timestamp.valueOf(LocalDateTime.now());
        UserAndFriendsWithOnlineStatusDto userAndFriendsWithOnlineStatusDto = UserAndFriendsWithOnlineStatusDto.builder()
            .user(userWithOnlineStatusDto)
            .friends(sixFriendsWithOnlineStatusDtos)
            .build();

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(userLastActivityTime));
        when(userRepo.getSixFriendsWithTheHighestRating(userId)).thenReturn(Collections.singletonList(user));
        assertEquals(userAndFriendsWithOnlineStatusDto, userService.getUserAndSixFriendsWithOnlineStatus(userId));
    }

    @Test
    void getAllFriendsWithTheOnlineStatus() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user), pageable, 1);
        UserWithOnlineStatusDto userWithOnlineStatusDto = UserWithOnlineStatusDto.builder()
            .id(userId)
            .onlineStatus(true)
            .build();
        List<UserWithOnlineStatusDto> friendsWithOnlineStatusDtos = new ArrayList<>();
        friendsWithOnlineStatusDtos = usersPage
            .getContent()
            .stream()
            .map(u -> new UserWithOnlineStatusDto(u.getId(), true))
            .collect(Collectors.toList());

        UserAndAllFriendsWithOnlineStatusDto userAndAllFriendsWithOnlineStatusDto = new UserAndAllFriendsWithOnlineStatusDto().builder()
            .user(userWithOnlineStatusDto)
            .friends(new PageableDto<>(friendsWithOnlineStatusDtos, usersPage.getTotalElements(),
                usersPage.getPageable().getPageNumber(), usersPage.getTotalPages()))
            .build();

        ReflectionTestUtils.setField(userService, "timeAfterLastActivity", 300000);
        Timestamp userLastActivityTime = Timestamp.valueOf(LocalDateTime.now());

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.findLastActivityTimeById(anyLong())).thenReturn(Optional.of(userLastActivityTime));
        when(userRepo.getAllUserFriends(userId, pageable)).thenReturn(usersPage);


        assertEquals(userAndAllFriendsWithOnlineStatusDto, userService.getAllFriendsWithTheOnlineStatus(userId, pageable));
    }

    @Test
    void deactivateUser() {
        User expecteduUser = user;
        expecteduUser.setUserStatus(DEACTIVATED);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        userService.deactivateUser(userId);
        assertEquals(expecteduUser, user);
    }

    @Test
    void deactivateAllUsers() {
        List<Long> longList = List.of(1L, 2L);
        assertEquals(longList, userService.deactivateAllUsers(longList));
    }

    @Test
    void setActivatedStatus() {
        User expecteduUser = user;
        user.setUserStatus(DEACTIVATED);
        expecteduUser.setUserStatus(ACTIVATED);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        userService.setActivatedStatus(userId);
        assertEquals(expecteduUser, user);
    }

    @Test
    void findByIdAndToken() {
        VerifyEmail verifyEmail = new VerifyEmail();
        verifyEmail.setId(1L);
        verifyEmail.setExpiryDate(LocalDateTime.now());
        verifyEmail.setToken("test");
        verifyEmail.setUser(user);
        user.setVerifyEmail(verifyEmail);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        assertEquals(Optional.of(user), userService.findByIdAndToken(userId, "test"));
    }

    @Test
    void findByIdAndToken2() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        assertEquals(Optional.empty(), userService.findByIdAndToken(userId, "test"));
    }

    @Test
    void searchBy() {
        List<TipsAndTricks> tipsAndTricks = Collections.singletonList(ModelUtils.getTipsAndTricks());
        List<User> userList = Collections.singletonList(ModelUtils.getUser());
        Pageable pageable = PageRequest.of(0, 1);
        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user), pageable, 1);

        List<UserManagementDto> users = usersPage.stream()
            .map(user -> modelMapper.map(user, UserManagementDto.class))
            .collect(Collectors.toList());

        PageableAdvancedDto<UserManagementDto> pageableDto = new PageableAdvancedDto<>(
            users,
            usersPage.getTotalElements(),
            usersPage.getPageable().getPageNumber(),
            usersPage.getTotalPages(),
            usersPage.getNumber(),
            usersPage.hasPrevious(),
            usersPage.hasNext(),
            usersPage.isFirst(),
            usersPage.isLast());

        when(userRepo.searchBy(pageable, userList.get(0).getName())).thenReturn(usersPage);


        assertEquals(pageableDto, userService.searchBy(pageable, userList.get(0).getName()));
    }


    @Test
    void createUserHabitTest2() {
        greencity.dto.habitstatistic.HabitDictionaryDto habitDictionaryDto = new greencity.dto.habitstatistic.HabitDictionaryDto();
        habitDictionaryDto.setId(1L);
        habitDictionaryDto.setImage("test");
        habitDictionaryDto.setDescription("test");
        habitDictionaryDto.setName("test");
        HabitCreateDto habitCreateDto = new HabitCreateDto();
        habitCreateDto.setId(1L);
        habitCreateDto.setStatus(true);
        habitCreateDto.setHabitDictionary(habitDictionaryDto);

        HabitIdDto habitIdDto = new HabitIdDto();
        habitIdDto.setHabitDictionaryId(1L);
        List<HabitIdDto> habitIdDtoList = Collections.singletonList(habitIdDto);
        HabitDictionary habitDictionaries = new HabitDictionary();
        habitDictionaries.setId(1L);
        habitDictionaries.setHabit(Collections.singletonList(habit));
        habitDictionaries.setHabitDictionaryTranslations(Collections.singletonList(ModelUtils.getHabitDictionaryTranslation()));
        habitDictionaries.setImage("test");

        HabitDictionaryTranslation habitDictionaryTranslation = ModelUtils.getHabitDictionaryTranslation();

        when(habitRepo.findByUserIdAndStatusHabit(userId)).thenReturn(Collections.singletonList(habit));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(habitDictionaryService.findById(1L)).thenReturn(habitDictionaries);
        when(modelMapper.map(user, Habit.class)).thenReturn(habit);
        when(habitRepo.saveAll(Collections.singletonList(habit))).thenReturn(Collections.singletonList(habit));
        when(habitRepo.findByUserIdAndStatusHabit(userId)).thenReturn(Collections.singletonList(habit));
        when(modelMapper.map(habit, HabitCreateDto.class)).thenReturn(habitCreateDto);
        when(habitService.getHabitDictionaryTranslation(habit, language)).thenReturn(habitDictionaryTranslation);


        assertEquals(Collections.singletonList(habitCreateDto), userService.createUserHabit(userId, habitIdDtoList, language));
    }

}
