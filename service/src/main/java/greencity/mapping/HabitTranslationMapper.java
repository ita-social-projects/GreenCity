package greencity.mapping;

import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.Language;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;

@Component
public class HabitTranslationMapper extends AbstractConverter<HabitTranslationDto, HabitTranslation> {
    @Override
    protected HabitTranslation convert(HabitTranslationDto habitTranslationDto) {
        return HabitTranslation.builder()
            .description(habitTranslationDto.getDescription())
            .habitItem(habitTranslationDto.getHabitItem())
            .name(habitTranslationDto.getName())
            .build();
    }

    /**
     * Method that build {@link List} of {@link HabitTranslation} from {@link List}
     * of {@link HabitTranslationDto}.
     *
     * @param dtoList {@link List} of {@link HabitTranslationDto}
     * @return {@link List} of {@link HabitTranslation}
     * @author Lilia Mokhnatska
     */
    public List<HabitTranslation> mapAllToList(List<HabitTranslationDto> dtoList) {
        return dtoList.stream().map(this::convert).toList();
    }

    /**
     * Method that build {@link List} of {@link HabitTranslation} from {@link List}
     * of {@link HabitTranslationDto} and {@link String} language.
     *
     * @param dtoList  {@link List} of {@link HabitTranslationDto}
     * @param language {@link String}
     *
     * @return {@link List} of {@link HabitTranslation}
     *
     * @author Chernenko Vitaliy
     */
    public List<HabitTranslation> mapAllToList(List<HabitTranslationDto> dtoList, String language) {
        return dtoList.stream().filter(dto -> Objects.equals(language, dto.getLanguageCode()))
            .map(this::convert).toList();
    }

    /**
     * Method that builds {@link List} of {@link HabitTranslation} from {@link List}
     * of {@link HabitTranslationDto}, {@link Language} language and {@link Habit}
     * habit.
     *
     * @param dtoList  {@link List} of {@link HabitTranslationDto}
     * @param language {@link Language}
     * @param habit    {@link Habit}
     *
     * @return {@link List} of {@link HabitTranslation}
     *
     * @author Bulhakova Oleksandra
     */
    public List<HabitTranslation> mapAllToList(List<HabitTranslationDto> dtoList, Language language, Habit habit) {
        return dtoList.stream()
            .filter(dto -> Objects.equals(language.getCode(), dto.getLanguageCode()))
            .map(dto -> {
                HabitTranslation habitTranslation = convert(dto);
                habitTranslation.setLanguageCode(language.getCode());
                habitTranslation.setHabit(habit);
                return habitTranslation;
            })
            .toList();
    }
}
