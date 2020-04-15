package greencity.mapping;

import greencity.dto.goal.GoalDto;
import greencity.entity.Goal;
import greencity.entity.Language;
import greencity.entity.localization.GoalTranslation;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class GoalDtoMapperTest {
    private GoalDtoMapper goalDtoMapper;
    private String language = "uk";
    private GoalTranslation goalTranslation = new GoalTranslation(2L, new Language(1L, language,
        Collections.emptyList(), Collections.emptyList(), Collections.emptyList()), "TEST",
        new Goal(2L, Collections.emptyList(), Collections.emptyList()));

    @BeforeEach
    public void setUp() {
        goalDtoMapper = new GoalDtoMapper();
    }

    @Test
    public void convertTest() {
        GoalDto expected = new GoalDto(goalTranslation.getGoal().getId(), goalTranslation.getText());
        assertEquals(expected, goalDtoMapper.convert(goalTranslation));
    }
}
