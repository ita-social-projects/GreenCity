package greencity.mapping;

import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

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

    protected HabitTranslation convertUa(HabitTranslationDto habitTranslationDto) {
        return HabitTranslation.builder()
                .description(habitTranslationDto.getDescriptionUa())
                .habitItem(habitTranslationDto.getHabitItemUa())
                .name(habitTranslationDto.getNameUa())
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
        return dtoList.stream().map(this::convert).collect(Collectors.toList());
    }


    /**
     * Method that build {@link List} of {@link HabitTranslation} from {@link List}
     * of {@link HabitTranslationDto} depending on language
     *
     * @param dtoList {@link List} of {@link HabitTranslationDto}
     * @param language {@link String}
     * @return {@link List} of {@link HabitTranslation}
     * @author Dmytro Fedotov
     */
    public List<HabitTranslation> mapAllToList(List<HabitTranslationDto> dtoList, String language) {
        if(language.equals("ua")){
            return dtoList.stream().map(this::convertUa).collect(Collectors.toList());
        } else return dtoList.stream().map(this::convert).collect(Collectors.toList());
    }
}
