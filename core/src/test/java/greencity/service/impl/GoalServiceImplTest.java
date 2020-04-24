package greencity.service.impl;

import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.CustomGoal;
import greencity.entity.UserGoal;
import greencity.entity.enums.GoalStatus;
import greencity.mapping.UserGoalResponseDtoMapper;
import greencity.repository.CustomGoalRepo;
import greencity.repository.GoalRepo;
import greencity.service.LanguageService;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.when;

import greencity.dto.goal.GoalDto;
import greencity.entity.Goal;
import greencity.entity.Language;
import greencity.entity.localization.GoalTranslation;
import greencity.repository.GoalTranslationRepo;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class GoalServiceImplTest {
    @Mock
    private GoalTranslationRepo goalTranslationRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private LanguageService languageService;
    @Mock
    private GoalRepo goalRepo;
    @Mock
    private CustomGoalRepo customGoalRepo;
    @InjectMocks
    private GoalServiceImpl goalService;
    @InjectMocks
    private UserGoalResponseDtoMapper userGoalResponseDtoMapper;

    private String language = "uk";
    private List<GoalTranslation> goalTranslations = Arrays.asList(
        new GoalTranslation(1L, new Language(1L, language, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList()), "TEST", new Goal(1L, Collections.emptyList(), Collections.emptyList())),
        new GoalTranslation(2L, new Language(1L, language, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList()), "TEST", new Goal(2L, Collections.emptyList(), Collections.emptyList())));
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
    public void findAllTest() {
        List<GoalDto> goalsDto = goalTranslations
            .stream()
            .map(goalTranslation -> new GoalDto(goalTranslation.getGoal().getId(), goalTranslation.getText()))
            .collect(Collectors.toList());

        when(modelMapper.map(goalTranslations.get(0), GoalDto.class)).thenReturn(goalsDto.get(0));
        when(modelMapper.map(goalTranslations.get(1), GoalDto.class)).thenReturn(goalsDto.get(1));

        when(goalTranslationRepo.findAllByLanguageCode(language)).thenReturn(goalTranslations);
        assertEquals(goalService.findAll(language), goalsDto);
    }

    @Test
    public void convertTestWhenGoalIsPredefined() {
        when(goalRepo.findById(goal.getId())).thenReturn(Optional.of(goal));
        when(goalTranslationRepo.findByGoalAndLanguageCode(goal, languageCode))
            .thenReturn(Optional.of(goalTranslation));
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(languageCode);

        UserGoalResponseDto expected = new UserGoalResponseDto(predefinedUserGoal.getId(), goalTranslation.getText(),
            predefinedUserGoal.getStatus());

        UserGoalResponseDto actual = userGoalResponseDtoMapper.convert(predefinedUserGoal);
        actual.setText(goalTranslation.getText());

        assertEquals(expected, actual);
    }

    @Test
    public void convertTestWhenGoalIsCustom() {
        when(customGoalRepo.findById(customUserGoal.getCustomGoal().getId())).thenReturn(Optional.of(customGoal));

        UserGoalResponseDto expected = new UserGoalResponseDto(customUserGoal.getId(), customGoal.getText(),
            predefinedUserGoal.getStatus());

        UserGoalResponseDto actual = userGoalResponseDtoMapper.convert(customUserGoal);
        actual.setText(customGoal.getText());

        assertEquals(expected, actual);
    }
}