package greencity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.goal.GoalManagementDto;
import greencity.dto.goal.GoalPostDto;
import greencity.dto.goal.GoalRequestDto;
import greencity.dto.goal.GoalResponseDto;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.Goal;
import greencity.entity.HabitAssign;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.entity.UserGoal;
import greencity.entity.localization.GoalTranslation;
import greencity.enums.EmailNotification;
import greencity.enums.GoalStatus;
import greencity.enums.Role;
import static greencity.enums.UserStatus.ACTIVATED;
import greencity.exception.exceptions.*;
import greencity.repository.GoalRepo;
import greencity.repository.GoalTranslationRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.UserGoalRepo;
import greencity.repository.UserRepo;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {
    @Mock
    private GoalTranslationRepo goalTranslationRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private UserRepo userRepo;
    @Mock
    UserGoalRepo userGoalRepo;
    @Mock
    HabitAssignRepo habitAssignRepo;
    @Mock
    private GoalRepo goalRepo;
    @InjectMocks
    private GoalServiceImpl goalService;
    @Mock
    private final Goal goal = Goal.builder().id(1L).translations(ModelUtils.getGoalTranslations()).build();

    private final List<LanguageTranslationDTO> languageTranslationDTOS =
        Collections.singletonList(ModelUtils.getLanguageTranslationDTO());
    private final GoalPostDto goalPostDto =
        new GoalPostDto(languageTranslationDTOS, new GoalRequestDto(1L));

    private final HabitAssign habitAssign = ModelUtils.getHabitAssign();
    private User user = User.builder()
        .id(1L)
        .name("Test Testing")
        .email("test@gmail.com")
        .role(Role.ROLE_USER)
        .userStatus(ACTIVATED)
        .emailNotification(EmailNotification.DISABLED)
        .lastVisit(LocalDateTime.of(2020, 10, 10, 20, 10, 10))
        .dateOfRegistration(LocalDateTime.now())
        .socialNetworks(new ArrayList<>())
        .build();

    private String language = "uk";

    private List<GoalTranslation> goalTranslations = Arrays.asList(
        GoalTranslation.builder()
            .id(1L)
            .language(new Language(1L, language, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()))
            .content("TEST")
            .goal(new Goal(1L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
            .build(),
        GoalTranslation.builder()
            .id(2L)
            .language(new Language(1L, language, Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList()))
            .content("TEST")
            .goal(new Goal(2L, Collections.emptyList(), Collections.emptySet(), Collections.emptyList()))
            .build());

    List<GoalRequestDto> goalRequestDtos = Arrays.asList(new GoalRequestDto(1L), new GoalRequestDto(2L),
        new GoalRequestDto(3L));

    private Long userId = user.getId();

    @Test
    void saveUserGoals() {
        ObjectMapper mapper = new ObjectMapper();
        UserGoal userGoal = mapper.convertValue(goalRequestDtos.get(0), UserGoal.class);
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(1L, userId))
            .thenReturn(Optional.of(habitAssign));
        when(userGoalRepo.getAllGoalsIdForHabit(habitAssign.getHabit().getId()))
            .thenReturn(Collections.singletonList(1L));
        when(userGoalRepo.getAllAssignedGoals(habitAssign.getId()))
            .thenReturn(Collections.singletonList(2L));
        when(modelMapper.map(goalRequestDtos.get(0), UserGoal.class))
            .thenReturn(userGoal);
        getUserGoalsTest();
        userGoal.setHabitAssign(habitAssign);
        goalService.saveUserGoals(userId, 1L, Collections.singletonList(goalRequestDtos.get(0)), "en");
        verify(userGoalRepo).saveAll(Collections.singletonList(userGoal));
    }

    @Test
    void saveUserGoalsThorowsNotFoundException() {
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(1L, userId))
            .thenReturn(Optional.of(habitAssign));
        when(userGoalRepo.getAllGoalsIdForHabit(habitAssign.getHabit().getId()))
            .thenReturn(Collections.singletonList(1L));
        List<GoalRequestDto> goalRequestDto = Collections.singletonList(goalRequestDtos.get(2));
        assertThrows(NotFoundException.class, () -> goalService
            .saveUserGoals(userId, 1L, goalRequestDto, "en"));
    }


    @Test
    void saveUserGoalsThorowsWrongIdException() {
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(1L, userId))
            .thenReturn(Optional.of(habitAssign));
        when(userGoalRepo.getAllGoalsIdForHabit(habitAssign.getHabit().getId()))
            .thenReturn(Collections.singletonList(1L));
        when(userGoalRepo.getAllAssignedGoals(habitAssign.getId()))
            .thenReturn(Collections.singletonList(1L));
        List<GoalRequestDto> goalRequestDto = Collections.singletonList(goalRequestDtos.get(0));
        assertThrows(WrongIdException.class, () -> goalService
            .saveUserGoals(userId, 1L, goalRequestDto, "en"));
    }



    @Test
    void findAllTest() {
        List<GoalTranslation> goalTranslations = ModelUtils.getGoalTranslations();
        List<GoalDto> goalsDto = goalTranslations
            .stream()
            .map(goalTranslation -> new GoalDto(goalTranslation.getGoal().getId(), goalTranslation.getContent()))
            .collect(Collectors.toList());

        when(modelMapper.map(goalTranslations.get(0), GoalDto.class)).thenReturn(goalsDto.get(0));
        when(modelMapper.map(goalTranslations.get(1), GoalDto.class)).thenReturn(goalsDto.get(1));
        when(goalTranslationRepo.findAllByLanguageCode(AppConstant.DEFAULT_LANGUAGE_CODE))
            .thenReturn(goalTranslations);

        assertEquals(goalService.findAll(AppConstant.DEFAULT_LANGUAGE_CODE), goalsDto);
    }

    @Test
    void saveGoalTest() {
        when((modelMapper.map(goalPostDto, Goal.class))).thenReturn(goal);
        when(modelMapper.map(goal.getTranslations(),
            new TypeToken<List<LanguageTranslationDTO>>() {
            }.getType())).thenReturn(languageTranslationDTOS);
        List<LanguageTranslationDTO> res = goalService.saveGoal(goalPostDto);
        assertEquals(languageTranslationDTOS.get(0).getContent(), res.get(0).getContent());
    }

    @Test
    void updateTest() {
        when(goalRepo.findById(goalPostDto.getGoal().getId())).thenReturn(Optional.of(goal));
        when(modelMapper.map(goal.getTranslations(),
            new TypeToken<List<LanguageTranslationDTO>>() {
            }.getType())).thenReturn(languageTranslationDTOS);
        List<LanguageTranslationDTO> res = goalService.update(goalPostDto);
        assertEquals(languageTranslationDTOS.get(0).getContent(), res.get(0).getContent());
    }

    @Test
    void updateThrowsTest() {
        assertThrows(GoalNotFoundException.class, () -> goalService.update(goalPostDto));
    }

    @Test
    void updateUserGoalStatusWithNonExistentGoalIdTest() {
        assertThrows(NullPointerException.class, () -> goalService.updateUserGoalStatus(userId, 2L, "en"));
    }

    @Test
    void updateUserGoalStatusWithActiveGoalStateTest() {
        UserGoal userGoal = ModelUtils.getPredefinedUserGoal();
        when(userGoalRepo.getOne(userGoal.getId())).thenReturn(userGoal);
        when(modelMapper.map(any(), eq(UserGoalResponseDto.class)))
            .thenReturn(new UserGoalResponseDto(2L, null, GoalStatus.DONE));
        when(goalTranslationRepo.findByLangAndUserGoalId(language, userGoal.getId()))
            .thenReturn(goalTranslations.get(0));
        UserGoalResponseDto userGoalResponseDto =
            goalService.updateUserGoalStatus(userId, userGoal.getId(), "uk");

        assertEquals(GoalStatus.DONE, userGoal.getStatus());
        assertEquals(userGoalResponseDto.getId(),
            new UserGoalResponseDto(2L, goalTranslations.get(0).getContent(), GoalStatus.DONE).getId());
        verify(userGoalRepo).save(userGoal);
    }

    @Test
    void updateUserGoalStatusWithDoneGoalStateTest() {
        UserGoal userGoal = new UserGoal(1L, null, null, GoalStatus.DONE, null);
        when(userGoalRepo.getOne(userGoal.getId())).thenReturn(userGoal);
        Long userId = user.getId();
        Long userGoalId = userGoal.getId();
        assertThrows(UserGoalStatusNotUpdatedException.class,
            () -> goalService.updateUserGoalStatus(userId, userGoalId, "en"));
        assertNotEquals(GoalStatus.ACTIVE, userGoal.getStatus());
    }

    @Test
    void deleteTest() {
        goalService.delete(1L);
        verify(goalRepo).deleteById(1L);
    }

    @Test
    void deleteTestFailed() {
        doThrow(EmptyResultDataAccessException.class).when(goalRepo).deleteById(300000L);

        assertThrows(NotDeletedException.class, () -> goalService.delete(300000L));
    }

    @Test
    void findGoalByIdTest() {
        Optional<Goal> object = Optional.of(goal);
        when(goalRepo.findById(anyLong())).thenReturn(object);
        when(modelMapper.map(object.get(), GoalResponseDto.class)).thenReturn(new GoalResponseDto());

        assertNotNull(goalService.findGoalById(30L));
    }

    @Test
    void findGoalByIdTestFailed() {
        Optional<Goal> object = null;

        assertThrows(GoalNotFoundException.class, () -> goalService.findGoalById(30L));
    }

    @Test
    void getAllFactsOfTheDay() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<Goal> goals = Collections.singletonList(goal);
        Page<Goal> pageGoals = new PageImpl<>(goals, pageable, goals.size());

        List<GoalManagementDto> dtoList = Collections.singletonList(
            goals.stream().map(g ->
                (GoalManagementDto.builder().id(g.getId())).build()).findFirst().get());
        PageableAdvancedDto<GoalManagementDto> expected = new PageableAdvancedDto<>(dtoList, dtoList.size(),
            0, 1, 0, false, false, true, true);

        when(goalRepo.findAll(pageable)).thenReturn(pageGoals);
        when(modelMapper.map(goals.get(0), GoalManagementDto.class)).thenReturn(dtoList.get(0));

        PageableAdvancedDto<GoalManagementDto> actual = goalService.findGoalForManagementByPage(pageable);

        assertEquals(expected, actual);
    }

    @Test
    void searchBy() {
        int pageNumber = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<Goal> goals = Collections.singletonList(goal);
        Page<Goal> pageGoals = new PageImpl<>(goals, pageable, goals.size());

        List<GoalManagementDto> dtoList = Collections.singletonList(
            goals.stream().map(g ->
                (GoalManagementDto.builder().id(g.getId())).build()).findFirst().get());
        PageableAdvancedDto<GoalManagementDto> expected = new PageableAdvancedDto<>(dtoList, dtoList.size(),
            0, 1, 0, false, false, true, true);

        when(goalRepo.searchBy(pageable, "uk")).thenReturn(pageGoals);
        when(modelMapper.map(goals.get(0), GoalManagementDto.class)).thenReturn(dtoList.get(0));

        PageableAdvancedDto<GoalManagementDto> actual = goalService.searchBy(pageable, "uk");

        assertEquals(expected, actual);
    }

    @Test
    void deleteAllGoalByListOfId() {
        List<Long> idsToBeDeleted = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);

        goalService.deleteAllGoalByListOfId(idsToBeDeleted);
        verify(goalRepo, times(6)).deleteById(anyLong());
    }

    @Test
    void deleteUserGoals() {
        String ids = "1,2,3,4,5,6";
        List<Long> expected = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L);
        UserGoal userGoal = new UserGoal(1L, null, goal, GoalStatus.ACTIVE, null);

        when(userGoalRepo.findById(anyLong())).thenReturn(Optional.of(userGoal));

        assertEquals(expected, goalService.deleteUserGoals(ids));
        verify(userGoalRepo, times(6)).delete(userGoal);
    }

    @Test
    void deleteUserGoalsFailed() {
        String ids = "1,2,3,4,5,6";
        UserGoal userGoal = new UserGoal(1L, null, goal, GoalStatus.ACTIVE, null);

        when(userGoalRepo.findById(anyLong())).thenReturn(Optional.of(userGoal));
        when(userGoalRepo.findById(3L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> goalService.deleteUserGoals(ids));
    }


    @Test
    void getUserGoalsTest() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        UserGoal userGoal = UserGoal.builder().id(1L).status(GoalStatus.ACTIVE).build();
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(userId, 1L))
            .thenReturn(Optional.of(habitAssign));
        when(userGoalRepo.findAllByHabitAssingId(habitAssign.getId())).thenReturn(Collections.singletonList(userGoal));
        when(modelMapper.map(userGoal, UserGoalResponseDto.class))
            .thenReturn(UserGoalResponseDto.builder().id(1L).build());
        when(goalTranslationRepo.findByLangAndUserGoalId("en", 1L))
            .thenReturn(GoalTranslation.builder().id(1L).build());
        assertEquals(goalService.getUserGoals(userId, 1L, "en").get(0).getId(), 1L);
    }

    @Test
    void getUserGoalsTestTrows() {
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(userId, 1L))
            .thenReturn(Optional.of(habitAssign));
        when(userGoalRepo.findAllByHabitAssingId(habitAssign.getId())).thenReturn(Collections.emptyList());
        assertThrows(UserHasNoGoalsException.class, () -> goalService.getUserGoals(userId, 1L, "en"));
    }

    @Test
    void deleteUserGoalByGoalIdAndUserIdAndHabitIdTest() {
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(1L, userId))
            .thenReturn(Optional.of(habitAssign));
        goalService.deleteUserGoalByGoalIdAndUserIdAndHabitId(1L, userId, 1L);
        verify(userGoalRepo).deleteByGoalIdAndHabitAssignId(1L, 1L);
    }

    @Test
    void deleteUserGoalByGoalIdAndUserIdAndHabitIdTestThorows() {
        assertThrows(NotFoundException.class,
            () -> goalService.deleteUserGoalByGoalIdAndUserIdAndHabitId(1L, userId, 1L));
    }

    @Test
    void getUserGoalsIfThereAreNoGoals() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(userId, 1L))
            .thenReturn(Optional.of(habitAssign));
        when(userGoalRepo.findAllByHabitAssingId(habitAssign.getId())).thenReturn(Collections.emptyList());

        assertThrows(UserHasNoGoalsException.class, () -> goalService.getUserGoals(userId, 1L, "en"));
    }
}
