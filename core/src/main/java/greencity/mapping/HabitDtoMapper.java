package greencity.mapping;

import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.dto.language.LanguageDTO;
import greencity.entity.Habit;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link Habit} into
 * {@link HabitDto}.
 */
@Component
public class HabitDtoMapper extends AbstractConverter<Habit, HabitDto> {
    /**
     * Method convert {@link Habit} to {@link HabitDto}.
     *
     * @return {@link HabitDto}
     */
    @Override
    protected HabitDto convert(Habit habit) {
        return HabitDto.builder()
            .id(habit.getId())
            .image(habit.getImage())
            .habitTranslations(habit.getHabitTranslations()
                .stream().map(habitTranslation -> HabitTranslationDto.builder()
                    .id(habitTranslation.getId())
                    .description(habitTranslation.getDescription())
                    .habitItem(habitTranslation.getHabitItem())
                    .name(habitTranslation.getName())
                    .language(LanguageDTO.builder()
                        .code(habitTranslation.getLanguage().getCode())
                    .id(habitTranslation.getLanguage().getId()).build())
                    .build()).collect(Collectors.toList()))
            .build();
    }
}
