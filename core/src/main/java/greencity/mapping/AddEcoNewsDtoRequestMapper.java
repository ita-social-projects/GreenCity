package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.localization.EcoNewsTranslation;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link AddEcoNewsDtoRequest} into
 * {@link EcoNews}.
 */
@Component
public class AddEcoNewsDtoRequestMapper extends AbstractConverter<AddEcoNewsDtoRequest, EcoNews> {
    /**
     * Method for converting {@link AddEcoNewsDtoRequest} into {@link EcoNews}.
     *
     * @param addEcoNewsDtoRequest object to convert.
     * @return converted object.
     */
    @Override
    protected EcoNews convert(AddEcoNewsDtoRequest addEcoNewsDtoRequest) {
        EcoNews ecoNews = EcoNews.builder()
            .creationDate(ZonedDateTime.now())
            .imagePath(addEcoNewsDtoRequest.getImagePath())
            .build();

        ecoNews.setTranslations(addEcoNewsDtoRequest.getTranslations()
            .stream()
            .map(translation ->
                new EcoNewsTranslation(null,
                    new Language(null, translation.getLanguage().getCode(), null, null, null),
                    translation.getTitle(), translation.getText(), ecoNews))
            .collect(Collectors.toList())
        );

        return ecoNews;
    }
}
