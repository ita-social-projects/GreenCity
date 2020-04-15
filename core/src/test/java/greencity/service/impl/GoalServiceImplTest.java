package greencity.service.impl;

import static org.junit.Assert.assertEquals;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

@RunWith(MockitoJUnitRunner.class)
public class GoalServiceImplTest {
    @Mock
    GoalTranslationRepo goalTranslationRepo;
    @InjectMocks
    private GoalServiceImpl goalService;
    @Mock
    private ModelMapper modelMapper;

    private String language = "uk";
    private List<GoalTranslation> goalTranslations = Arrays.asList(
        new GoalTranslation(1L, new Language(1L, language, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList()), "TEST", new Goal(1L, Collections.emptyList(), Collections.emptyList())),
        new GoalTranslation(2L, new Language(1L, language, Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList()), "TEST", new Goal(2L, Collections.emptyList(), Collections.emptyList())));

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
}