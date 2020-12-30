package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitManagementDto;
import greencity.dto.habittranslation.HabitTranslationManagementDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class HabitManagementDtoMapperTest {

    @InjectMocks
    HabitManagementDtoMapper habitManagementDtoMapper;

    @Test
    void convert() {
        HabitTranslation habitTranslation = ModelUtils.getHabitTranslation();
        Habit habit = ModelUtils.getHabit();
        List<HabitTranslation> habitTranslations = Collections.singletonList(habitTranslation);
        habit.setHabitTranslations(habitTranslations);
        HabitManagementDto habitManagementDto = HabitManagementDto.builder()
            .id(habit.getId())
            .image(habit.getImage())
            .habitTranslations(habit.getHabitTranslations().stream().map(ht -> HabitTranslationManagementDto.builder()
                .id(ht.getId())
                .description(ht.getDescription())
                .habitItem(ht.getHabitItem())
                .name(ht.getName())
                .languageCode(ht.getLanguage().getCode())
                .build())
                .collect(Collectors.toList()))
            .build();
        HabitManagementDto expected = habitManagementDtoMapper.convert(habit);
        assertEquals(expected, habitManagementDto);
    }
}