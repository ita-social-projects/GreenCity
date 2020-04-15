package greencity.mapping;

import static org.junit.Assert.assertEquals;

import greencity.dto.goal.GoalDto;
import greencity.entity.Goal;
import greencity.entity.Language;
import greencity.entity.localization.GoalTranslation;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GoalDtoMapperTest {
    private GoalDtoMapper goalDtoMapper;
    private String language = "uk";
    private GoalTranslation goalTranslation = new GoalTranslation(2L, new Language(1L, language,
        Collections.emptyList(), Collections.emptyList(), Collections.emptyList()), "TEST",
        new Goal(2L, Collections.emptyList(), Collections.emptyList()));

    @Before
    public void setUp() {
        goalDtoMapper = new GoalDtoMapper();
    }

    @Test
    public void convertTest() {
        GoalDto expected = new GoalDto(goalTranslation.getGoal().getId(), goalTranslation.getText());

        assertEquals(expected, goalDtoMapper.convert(goalTranslation));
    }
}
