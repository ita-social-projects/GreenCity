package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.MutualHabitAssignDto;
import greencity.dto.habit.MutualHabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
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
        Habit habit = habitAssign.getHabit();
        HabitTranslation habitTranslationUa = habit.getHabitTranslations().stream()
            .filter(translation -> translation.getLanguage().getCode().equalsIgnoreCase("ua"))
            .findFirst().orElse(null);
        HabitTranslation habitTranslation = habit.getHabitTranslations().stream()
            .filter(translation -> !translation.getLanguage().getCode().equalsIgnoreCase("en"))
            .findFirst().orElse(null);
        HabitTranslationDto habitTranslationDto = HabitTranslationDto.builder()
            .name(habitTranslation.getName())
            .nameUa(habitTranslationUa.getName())
            .habitItem(habitTranslation.getHabitItem())
            .habitItemUa(habitTranslationUa.getHabitItem())
            .description(habitTranslation.getDescription())
            .descriptionUa(habitTranslationUa.getDescription())
            .build();
        MutualHabitDto mutualHabitDto = MutualHabitDto.builder()
            .id(habit.getId())
            .image(habit.getImage())
            .habitTranslation(habitTranslationDto)
            .build();
        MutualHabitAssignDto expected = MutualHabitAssignDto.builder()
            .id(habitAssign.getId())
            .status(habitAssign.getStatus())
            .userId(habitAssign.getUser().getId())
            .duration(habitAssign.getDuration())
            .workingDays(habitAssign.getWorkingDays())
            .habit(mutualHabitDto)
            .build();

        MutualHabitAssignDto actual = mutualHabitAssignDtoMapper.convert(habitAssign);

        assertEquals(expected, actual);
    }
}
