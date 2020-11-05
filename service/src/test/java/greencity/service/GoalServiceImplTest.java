package greencity.service;

import greencity.dto.goal.*;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.Goal;
import greencity.entity.UserGoal;
import greencity.entity.localization.GoalTranslation;
import greencity.repository.CustomGoalRepo;
import greencity.repository.GoalRepo;
import greencity.repository.GoalTranslationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.List;
import java.util.stream.Collectors;


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

    @Spy
    @InjectMocks
    private GoalServiceImpl goalServiceSpy;


    private Goal goal = Goal.builder().id(1L).translations(ModelUtils.getGoalTranslations()).build();
    private UserGoal predefinedUserGoal = ModelUtils.getPredefinedUserGoal();
    private UserGoalResponseDto predefinedUserGoalDto = ModelUtils.getPredefinedUserGoalDto();
    private GoalPostDto goalPostDto =
        new GoalPostDto(Collections.singletonList(ModelUtils.getLanguageTranslationDTO()), new GoalRequestDto(1L));
    private List<GoalTranslationVO> goalTranslationVOs = Collections.singletonList(
        GoalTranslationVO.builder().id(1L).goal(ModelUtils.getUserGoalVO().getGoal()).build());

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
        when(goalRepo.save(modelMapper.map(goalPostDto, Goal.class))).thenReturn(goal);
        doReturn(goalTranslationVOs).when(goalServiceSpy).saveTranslations(goalPostDto,goal);
        List<GoalTranslationVO> res = goalServiceSpy.saveGoal(goalPostDto);
        assertEquals(goal.getId(),res.get(0).getId());
    }
}
