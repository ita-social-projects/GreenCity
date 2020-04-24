package greencity.service.impl;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.UserGoal;
import greencity.mapping.UserGoalResponseDtoMapper;
import greencity.repository.CustomGoalRepo;
import greencity.repository.GoalRepo;
import greencity.service.LanguageService;
import java.util.Optional;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Mockito.when;
import greencity.dto.goal.GoalDto;
import greencity.entity.localization.GoalTranslation;
import greencity.repository.GoalTranslationRepo;
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

    @Test
    public void findAllTest() {
        List<GoalTranslation> goalTranslations = ModelUtils.getGoalTranslations();

        List<GoalDto> goalsDto = goalTranslations
            .stream()
            .map(goalTranslation -> new GoalDto(goalTranslation.getGoal().getId(), goalTranslation.getText()))
            .collect(Collectors.toList());

        when(modelMapper.map(goalTranslations.get(0), GoalDto.class)).thenReturn(goalsDto.get(0));
        when(modelMapper.map(goalTranslations.get(1), GoalDto.class)).thenReturn(goalsDto.get(1));

        when(goalTranslationRepo.findAllByLanguageCode(AppConstant.DEFAULT_LANGUAGE_CODE)).thenReturn(goalTranslations);
        assertEquals(goalService.findAll(AppConstant.DEFAULT_LANGUAGE_CODE), goalsDto);
    }

    @Test
    public void convertTestWhenGoalIsPredefined() {
        UserGoal predefinedUserGoal = ModelUtils.getPredefinedUserGoal();
        GoalTranslation goalTranslation = ModelUtils.getGoalTranslation();
        when(goalRepo.findById(predefinedUserGoal.getGoal().getId()))
            .thenReturn(Optional.of(predefinedUserGoal.getGoal()));
        when(goalTranslationRepo
            .findByGoalAndLanguageCode(predefinedUserGoal.getGoal(), AppConstant.DEFAULT_LANGUAGE_CODE))
            .thenReturn(Optional.of(goalTranslation));
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(AppConstant.DEFAULT_LANGUAGE_CODE);

        UserGoalResponseDto expected = new UserGoalResponseDto(predefinedUserGoal.getId(), goalTranslation.getText(),
            predefinedUserGoal.getStatus());

        UserGoalResponseDto actual = userGoalResponseDtoMapper.convert(predefinedUserGoal);
        actual.setText(goalTranslation.getText());

        assertEquals(expected, actual);
    }

    @Test
    public void convertTestWhenGoalIsCustom() {
        UserGoal customUserGoal = ModelUtils.getCustomUserGoal();
        when(customGoalRepo.findById(customUserGoal.getCustomGoal().getId()))
            .thenReturn(Optional.of(customUserGoal.getCustomGoal()));

        UserGoalResponseDto expected =
            new UserGoalResponseDto(customUserGoal.getId(), customUserGoal.getCustomGoal().getText(),
                customUserGoal.getStatus());

        UserGoalResponseDto actual = userGoalResponseDtoMapper.convert(customUserGoal);
        actual.setText(customUserGoal.getCustomGoal().getText());

        assertEquals(expected, actual);
    }
}