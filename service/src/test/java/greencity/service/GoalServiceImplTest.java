package greencity.service;

import greencity.dto.goal.*;
import greencity.exception.exceptions.GoalNotFoundException;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.Goal;
import greencity.entity.UserGoal;
import greencity.entity.localization.GoalTranslation;
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
import org.modelmapper.TypeToken;


@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {
    @Mock
    private GoalTranslationRepo goalTranslationRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private GoalRepo goalRepo;
    @InjectMocks
    private GoalServiceImpl goalService;

    @Spy
    @InjectMocks
    private GoalServiceImpl goalServiceSpy;


    private final Goal goal = Goal.builder().id(1L).translations(ModelUtils.getGoalTranslations()).build();
    private final GoalPostDto goalPostDto =
        new GoalPostDto(Collections.singletonList(ModelUtils.getLanguageTranslationDTO()), new GoalRequestDto(1L));
    private final List<GoalTranslationVO> goalTranslationVOs = Collections.singletonList(
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

    @Test
    void updateTest() {
        when(goalRepo.findById(goalPostDto.getGoal().getId())).thenReturn(Optional.of(goal));
        doReturn(goalTranslationVOs).when(goalServiceSpy).saveTranslations(goalPostDto,goal);
        List<GoalTranslationVO> res = goalServiceSpy.update(goalPostDto);
        assertEquals(goal.getId(),res.get(0).getId());
    }

    @Test
    void updateThrowsTest() {
        assertThrows(GoalNotFoundException.class, () -> goalService.update(goalPostDto));
    }

    @Test
    void deleteTest() {
        when(goalRepo.findById(1L)).thenReturn(Optional.of(goal));
        goalService.delete(1L);
        verify(goalRepo).deleteById(1L);
    }

    @Test
    void deleteThrowTest() {
        assertThrows(GoalNotFoundException.class, () -> goalService.delete(1L));
    }
}
