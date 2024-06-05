package greencity.mapping;

import greencity.dto.habit.MutualHabitAssignDto;
import greencity.dto.habit.MutualHabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.HabitTranslation;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitAssign} into
 * {@link MutualHabitAssignDto}.
 */
@Component
public class MutualHabitAssignDtoMapper extends AbstractConverter<HabitAssign, MutualHabitAssignDto> {
    /**
     * Method convert {@link HabitAssign} to {@link MutualHabitAssignDto}.
     *
     * @return {@link MutualHabitAssignDto}
     */
    @Override
    protected MutualHabitAssignDto convert(HabitAssign habitAssign) {
        Habit habit = habitAssign.getHabit();
        HabitTranslation habitTranslationUa = habit.getHabitTranslations().stream()
            .filter(translation -> translation.getLanguage().getCode().equalsIgnoreCase("ua"))
            .findFirst().orElse(null);
        HabitTranslation habitTranslation = habit.getHabitTranslations().stream()
            .filter(translation -> !translation.getLanguage().getCode().equalsIgnoreCase("en"))
            .findFirst().orElse(null);
        MutualHabitDto mutualHabitDto = null;
        if (habitTranslation != null && habitTranslationUa != null) {
            HabitTranslationDto habitTranslationDto = HabitTranslationDto.builder()
                .name(habitTranslation.getName())
                .nameUa(habitTranslationUa.getName())
                .habitItem(habitTranslation.getHabitItem())
                .habitItemUa(habitTranslationUa.getHabitItem())
                .description(habitTranslation.getDescription())
                .descriptionUa(habitTranslationUa.getDescription())
                .build();
            mutualHabitDto = MutualHabitDto.builder()
                .id(habit.getId())
                .image(habit.getImage())
                .habitTranslation(habitTranslationDto)
                .build();
        }
        return MutualHabitAssignDto.builder()
            .id(habitAssign.getId())
            .status(habitAssign.getStatus())
            .userId(habitAssign.getUser().getId())
            .duration(habitAssign.getDuration())
            .workingDays(habitAssign.getWorkingDays())
            .habit(mutualHabitDto)
            .build();
    }
}
