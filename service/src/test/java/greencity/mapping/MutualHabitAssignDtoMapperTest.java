package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.MutualHabitAssignDto;
import greencity.entity.HabitAssign;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MutualHabitAssignDtoMapperTest {
    @InjectMocks
    private MutualHabitAssignDtoMapper mutualHabitAssignDtoMapper;

    @Test
    void convertTest() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        MutualHabitAssignDto actual = mutualHabitAssignDtoMapper.convert(habitAssign);

        MutualHabitAssignDto expected = MutualHabitAssignDto.builder()
            .id(habitAssign.getId())
            .status(habitAssign.getStatus())
            .userId(habitAssign.getUser().getId())
            .duration(habitAssign.getDuration())
            .workingDays(habitAssign.getWorkingDays())
            .build();
        habitAssign.getHabit().setHabitTranslations(List.of(
            HabitTranslation.builder()
                .id(1L)
                .name("name")
                .habitItem("habitItem")
                .description("description")
                .language(Language.builder().id(1L).code("ua").build())
                .build(),
            HabitTranslation.builder()
                .id(2L)
                .name("nameUa")
                .habitItem("habitItemUa")
                .description("descriptionUa")
                .language(Language.builder().id(1L).code("en").build())
                .build()));

        assertEquals(expected, actual);
    }
}
