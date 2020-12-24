package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.goal.GoalResponseDto;
import greencity.dto.goal.GoalTranslationDTO;
import greencity.dto.language.LanguageVO;
import greencity.entity.Goal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class GoalResponseDtoMapperTest {
    @InjectMocks
    GoalResponseDtoMapper goalResponseDtoMapper;

    @Test
    void convert() {
        Goal goal = Goal.builder()
            .id(1L)
            .translations(ModelUtils.getGoalTranslations())
            .build();
        GoalResponseDto expected = GoalResponseDto.builder()
            .id(goal.getId())
            .translations(goal.getTranslations().stream().map(
                goalTranslation -> GoalTranslationDTO.builder()
                    .id(goalTranslation.getId())
                    .content(goalTranslation.getContent())
                    .language(LanguageVO.builder()
                        .id(goalTranslation.getLanguage().getId())
                        .code(goalTranslation.getLanguage().getCode())
                        .build())
                    .build())
                .collect(Collectors.toList()))
            .build();
        GoalResponseDto actual = goalResponseDtoMapper.convert(goal);
        assertEquals(expected, actual);
    }
}
