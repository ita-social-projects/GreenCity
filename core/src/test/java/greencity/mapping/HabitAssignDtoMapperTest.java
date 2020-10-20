package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitAssign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
public class HabitAssignDtoMapperTest {

    @InjectMocks
    private HabitAssignDtoMapper habitAssignDtoMapper;

    @Test
    void convertTest() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();

        HabitAssignDto expected = HabitAssignDto.builder()
            .id(habitAssign.getId())
            .acquired(habitAssign.getAcquired())
            .suspended(habitAssign.getSuspended())
            .createDateTime(habitAssign.getCreateDate())
            .habit(HabitDto.builder()
                .id(habitAssign.getHabit().getId())
                .image(habitAssign.getHabit().getImage())
                .habitTranslations(habitAssign.getHabit().getHabitTranslations()
                    .stream().map(habitTranslation -> HabitTranslationDto.builder()
                        .id(habitTranslation.getId())
                        .description(habitTranslation.getDescription())
                        .habitItem(habitTranslation.getHabitItem())
                        .name(habitTranslation.getName())
                        .build()).collect(Collectors.toList()))
                .build())
            .build();

        assertEquals(expected, habitAssignDtoMapper.convert(habitAssign));
    }
}
