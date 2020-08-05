package greencity.service.impl;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.goal.GoalDto;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.CustomGoal;
import greencity.entity.Goal;
import greencity.entity.UserGoal;
import greencity.entity.localization.GoalTranslation;
import greencity.exception.exceptions.GoalNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CustomGoalRepo;
import greencity.repository.GoalRepo;
import greencity.repository.GoalTranslationRepo;
import greencity.service.LanguageService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class GoalServiceImplTest {
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

    private UserGoal predefinedUserGoal = ModelUtils.getPredefinedUserGoal();
    private UserGoal customUserGoal = ModelUtils.getCustomUserGoal();
    private UserGoalResponseDto predefinedUserGoalDto = ModelUtils.getPredefinedUserGoalDto();
    private UserGoalResponseDto customUserGoalDto = ModelUtils.getCustomUserGoalDto();

    @Test
    void findAllTest() {
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
    void getUserGoalResponseDtoFromPredefinedGoal() {
        when(modelMapper.map(predefinedUserGoal, UserGoalResponseDto.class)).thenReturn(predefinedUserGoalDto);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(ModelUtils.getLanguage().getCode());
        Goal goal = predefinedUserGoal.getGoal();
        when(goalRepo.findById(anyLong())).thenReturn(Optional.ofNullable(goal));
        when(goalTranslationRepo.findByGoalAndLanguageCode(any(Goal.class), anyString())).thenReturn(Optional.of(ModelUtils.getGoalTranslation()));
        assertEquals(predefinedUserGoalDto, goalService.getUserGoalResponseDtoFromPredefinedGoal(predefinedUserGoal));
    }

    @Test
    void getUserGoalResponseDtoFromPredefinedGoalFailed() {
        when(modelMapper.map(predefinedUserGoal, UserGoalResponseDto.class)).thenReturn(predefinedUserGoalDto);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(ModelUtils.getLanguage().getCode());
        when(goalRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(GoalNotFoundException.class, () -> goalService.getUserGoalResponseDtoFromPredefinedGoal(predefinedUserGoal));
    }

    @Test
    void getUserGoalResponseDtoFromCustomGoal() {
        when(modelMapper.map(customUserGoal, UserGoalResponseDto.class)).thenReturn(customUserGoalDto);
        CustomGoal customGoal = customUserGoal.getCustomGoal();
        when(customGoalRepo.findById(anyLong())).thenReturn(Optional.ofNullable(customGoal));
        assertEquals(customUserGoalDto, goalService.getUserGoalResponseDtoFromCustomGoal(customUserGoal));
    }

    @Test
    void getUserGoalResponseDtoFromCustomGoalFailed() {
        when(modelMapper.map(customUserGoal, UserGoalResponseDto.class)).thenReturn(customUserGoalDto);
        when(customGoalRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> goalService.getUserGoalResponseDtoFromCustomGoal(customUserGoal));
    }
}