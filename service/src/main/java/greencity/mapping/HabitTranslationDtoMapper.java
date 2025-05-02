package greencity.mapping;

import greencity.client.UserRemoteClient;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.HabitTranslation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HabitTranslationDtoMapper extends AbstractConverter<HabitTranslation, HabitTranslationDto> {

    UserRemoteClient userRemoteClient;

    @Override
    protected HabitTranslationDto convert(HabitTranslation habitTranslation) {
        String languageCode = userRemoteClient.findLanguageCodeByd(habitTranslation.getLanguageId());

        return HabitTranslationDto.builder()
            .description(habitTranslation.getDescription())
            .habitItem(habitTranslation.getHabitItem())
            .name(habitTranslation.getName())
            .languageCode(languageCode)
            .build();
    }

    /**
     * Method that build {@link List} of {@link HabitTranslationDto} from
     * {@link List} of {@link HabitTranslation}.
     *
     * @param habitTranslationList {@link List} of {@link HabitTranslation}
     * @return {@link List} of {@link HabitTranslationDto}
     * @author Lilia Mokhnatska
     */
    public List<HabitTranslationDto> mapAllToList(List<HabitTranslation> habitTranslationList) {
        return habitTranslationList.stream().map(this::convert).collect(Collectors.toList());
    }
}
