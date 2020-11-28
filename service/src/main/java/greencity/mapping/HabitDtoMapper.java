package greencity.mapping;

import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitTranslation} into
 * {@link HabitDto}.
 */
@Component
public class HabitDtoMapper extends AbstractConverter<HabitTranslation, HabitDto> {
    /**
     * Method convert {@link HabitTranslation} to {@link HabitDto}.
     *
     * @return {@link HabitDto}
     */
    @Override
    protected HabitDto convert(HabitTranslation habitTranslation) {
        return HabitDto.builder()
            .id(habitTranslation.getHabit().getId())
            .image(habitTranslation.getHabit().getImage())
            .defaultDuration(habitTranslation.getHabit().getDefaultDuration())
            .habitTranslation(HabitTranslationDto.builder()
                .description(habitTranslation.getDescription())
                .habitItem(habitTranslation.getHabitItem())
                .name(habitTranslation.getName())
                .languageCode(habitTranslation.getLanguage().getCode())
                .build())
            .tags(habitTranslation.getHabit().getTags().stream().flatMap(t -> t.getTagTranslations().stream())
                .map(TagTranslation::getName).collect(Collectors.toList()))
            .build();
    }
}
