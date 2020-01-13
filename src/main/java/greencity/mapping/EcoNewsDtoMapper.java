package greencity.mapping;

import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class EcoNewsDtoMapper extends AbstractConverter<EcoNewsTranslation, EcoNewsDto> {
    @Override
    protected EcoNewsDto convert(EcoNewsTranslation ecoNewsTranslation) {
        EcoNews ecoNews = ecoNewsTranslation.getEcoNews();

        return new EcoNewsDto(ecoNews.getId(), ecoNewsTranslation.getTitle(),
            ecoNews.getCreationDate(), ecoNews.getText(), ecoNews.getImagePath());
    }
}
