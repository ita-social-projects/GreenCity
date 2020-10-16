package greencity.service;

import greencity.dto.goal.GoalDto;
import greencity.dto.user.UserGoalResponseDto;
import greencity.dto.user.UserGoalVO;
import greencity.entity.CustomGoal;
import greencity.entity.Goal;
import greencity.entity.UserGoal;
import greencity.entity.localization.GoalTranslation;
import greencity.exception.exceptions.GoalNotFoundException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CustomGoalRepo;
import greencity.repository.GoalRepo;
import greencity.repository.GoalTranslationRepo;
import greencity.service.constant.AppConstant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        when(goalTranslationRepo.findAllByLanguageCode(AppConstant.DEFAULT_LANGUAGE_CODE))
            .thenReturn(goalTranslations);

        assertEquals(goalService.findAll(AppConstant.DEFAULT_LANGUAGE_CODE), goalsDto);
    }

    @Test
    void getUserGoalResponseDtoFromPredefinedGoal() {
        UserGoalVO map =ModelUtils.getUserGoalVO();
        Goal goal = predefinedUserGoal.getGoal();
        String code = ModelUtils.getLanguage().getCode();

        when(modelMapper.map(map, UserGoal.class)).thenReturn(predefinedUserGoal);
        when(modelMapper.map(predefinedUserGoal, UserGoalResponseDto.class)).thenReturn(predefinedUserGoalDto);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(code);
        when(goalRepo.findById(1L)).thenReturn(Optional.ofNullable(goal));
        when(goalTranslationRepo.findByGoalAndLanguageCode(goal, code))
            .thenReturn(Optional.of(ModelUtils.getGoalTranslation()));

        UserGoalResponseDto actual = goalService.getUserGoalResponseDtoFromPredefinedGoal(map);

        assertEquals(predefinedUserGoalDto, actual);
    }

    @Test
    void getUserGoalResponseDtoFromPredefinedGoalFailed() {
        UserGoalVO map = ModelUtils.getUserGoalVO();

        when(modelMapper.map(map, UserGoal.class)).thenReturn(predefinedUserGoal);
        when(modelMapper.map(predefinedUserGoal, UserGoalResponseDto.class)).thenReturn(predefinedUserGoalDto);
        when(languageService.extractLanguageCodeFromRequest()).thenReturn(ModelUtils.getLanguage().getCode());
        when(goalRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GoalNotFoundException.class, () ->
            goalService.getUserGoalResponseDtoFromPredefinedGoal(map));
    }

    @Test
    void getUserGoalResponseDtoFromCustomGoal() {
        UserGoalVO map = ModelUtils.getUserGoalVO();
        CustomGoal customGoal = customUserGoal.getCustomGoal();

        when(modelMapper.map(map, UserGoal.class)).thenReturn(customUserGoal);
        when(modelMapper.map(customUserGoal, UserGoalResponseDto.class)).thenReturn(customUserGoalDto);
        when(customGoalRepo.findById(8L)).thenReturn(Optional.ofNullable(customGoal));

        UserGoalResponseDto actual = goalService.getUserGoalResponseDtoFromCustomGoal(map);

        assertEquals(customUserGoalDto, actual);
    }

    @Test
    void getUserGoalResponseDtoFromCustomGoalFailed() {
        UserGoalVO map = ModelUtils.getUserGoalVO();

        when(modelMapper.map(map, UserGoal.class)).thenReturn(customUserGoal);
        when(modelMapper.map(customUserGoal, UserGoalResponseDto.class)).thenReturn(customUserGoalDto);
        when(customGoalRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
            goalService.getUserGoalResponseDtoFromCustomGoal(map));
    }
}
