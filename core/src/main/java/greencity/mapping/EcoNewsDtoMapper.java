package greencity.mapping;

import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link EcoNewsTranslation} into
 * {@link EcoNewsDto}.
 */
@Component
public class EcoNewsDtoMapper extends AbstractConverter<EcoNewsTranslation, EcoNewsDto> {
    /**
     * Method for converting {@link EcoNewsTranslation} into {@link EcoNewsDto}.
     *
     * @param ecoNewsTranslation object ot convert.
     * @return converted object.
     */
    @Override
    protected EcoNewsDto convert(EcoNewsTranslation ecoNewsTranslation) {
        EcoNews ecoNews = ecoNewsTranslation.getEcoNews();

        return new EcoNewsDto(ecoNews.getId(), ecoNewsTranslation.getTitle(),
            ecoNews.getCreationDate(), ecoNews.getText(), ecoNews.getImagePath());
    }
}
