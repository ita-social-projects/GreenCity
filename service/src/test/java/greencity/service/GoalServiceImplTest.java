package greencity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.goal.*;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.*;
import greencity.enums.EmailNotification;
import greencity.enums.GoalStatus;
import greencity.enums.Role;
import static greencity.enums.UserStatus.ACTIVATED;

import greencity.exception.exceptions.*;
import greencity.repository.*;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.entity.localization.GoalTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.stream.Collectors;
import org.modelmapper.TypeToken;

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
    void deleteTest() {
        goalService.delete(1L);
        verify(goalRepo).deleteById(1L);
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
    void getUserGoalsTest() {
        UserGoal userGoal = UserGoal.builder().id(1L).status(GoalStatus.ACTIVE).build();
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(userId, 1L))
            .thenReturn(Optional.of(habitAssign));
        when(userGoalRepo.findAllByHabitAssingId(habitAssign.getId())).thenReturn(Collections.singletonList(userGoal));
        when(modelMapper.map(userGoal, UserGoalResponseDto.class))
            .thenReturn(UserGoalResponseDto.builder().id(1L).build());
        when(goalTranslationRepo.findByLangAndUserGoalId("en", 1L))
            .thenReturn(GoalTranslation.builder().id(1L).build());
        assertEquals(1L, goalService.getUserGoals(userId, 1L, "en").get(0).getId());
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
    void saveUserGoalsThorows() {
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(1L, userId))
                .thenReturn(Optional.of(habitAssign));
        when(userGoalRepo.getAllGoalsIdForHabit(habitAssign.getHabit().getId()))
                .thenReturn(Collections.singletonList(1L));
        assertThrows(NotFoundException.class, () -> goalService
                .saveUserGoals(userId, 1L, Collections.singletonList(goalRequestDtos.get(2)), "en"));
    }

    @Test
    void saveUserGoalsThorowss() {
        when(habitAssignRepo.findByHabitIdAndUserIdAndSuspendedFalse(1L, userId))
                .thenReturn(Optional.of(habitAssign));
        when(userGoalRepo.getAllGoalsIdForHabit(habitAssign.getHabit().getId()))
                .thenReturn(Collections.singletonList(1L));
        when(userGoalRepo.getAllAssignedGoals(habitAssign.getId()))
                .thenReturn(Collections.singletonList(1L));
        assertThrows(WrongIdException.class, () -> goalService
                .saveUserGoals(userId, 1L, Collections.singletonList(goalRequestDtos.get(0)), "en"));
    }
}
