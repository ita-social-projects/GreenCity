package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.goal.GoalDto;
import greencity.entity.localization.GoalTranslation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
class GoalDtoMapperTest {
    @InjectMocks
    private GoalDtoMapper goalDtoMapper;

    @Test
    void convertTest() {
        GoalTranslation goalTranslation = ModelUtils.getGoalTranslation();

        GoalDto expected = new GoalDto(goalTranslation.getGoal().getId(), goalTranslation.getText());

        assertEquals(expected, goalDtoMapper.convert(goalTranslation));
    }
}
