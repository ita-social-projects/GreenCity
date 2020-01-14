package greencity.mapping;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.CustomGoal;
import greencity.entity.Goal;
import greencity.entity.Language;
import greencity.entity.UserGoal;
import greencity.entity.enums.GoalStatus;
import greencity.entity.localization.GoalTranslation;
import greencity.repository.CustomGoalRepo;
import greencity.repository.GoalRepo;
import greencity.repository.GoalTranslationRepo;
import greencity.service.LanguageService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserGoalResponseDtoMapperTest {
    @Mock
    private LanguageService languageService;
    @Mock
    private GoalRepo goalRepo;
    @Mock
    private GoalTranslationRepo goalTranslationRepo;
    @Mock
    private CustomGoalRepo customGoalRepo;
    @InjectMocks
    private UserGoalResponseDtoMapper mapper;

    private String languageCode = "uk";
    private Goal goal = new Goal(1L, Collections.emptyList(), Collections.emptyList());
    private CustomGoal customGoal = new CustomGoal(1L, "TEST", null, Collections.emptyList());
    private UserGoal predefinedUserGoal = new UserGoal(1L, null, goal, null,
        GoalStatus.ACTIVE, LocalDateTime.now());
    private UserGoal customUserGoal = new UserGoal(1L, null, null, customGoal,
        GoalStatus.ACTIVE, LocalDateTime.now());
    private GoalTranslation goalTranslation = new GoalTranslation(2L, new Language(1L, languageCode,
        Collections.emptyList(), Collections.emptyList(), Collections.emptyList()), "TEST", goal);

    @Test
    public void convertTestWhenGoalIsPredefined() {
        when(goalRepo.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(goalTranslationRepo.findByGoalAndLanguageCode(goal, languageCode))
            .thenReturn(Optional.of(goalTranslation));
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(languageCode);

        UserGoalResponseDto expected = new UserGoalResponseDto(predefinedUserGoal.getId(), goalTranslation.getText(),
            predefinedUserGoal.getStatus());

        assertEquals(expected, mapper.convert(predefinedUserGoal));
    }

    @Test
    public void convertTestWhenGoalIsCustom() {
        when(customGoalRepo.findById(customUserGoal.getCustomGoal().getId())).thenReturn(Optional.of(customGoal));

        UserGoalResponseDto expected = new UserGoalResponseDto(customUserGoal.getId(), customGoal.getText(),
            predefinedUserGoal.getStatus());

        assertEquals(expected, mapper.convert(customUserGoal));
    }
}
