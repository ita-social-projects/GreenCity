package greencity.mapping;

import greencity.dto.econews.EcoNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.User;
import greencity.entity.localization.EcoNewsTranslation;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link EcoNewsTranslation} into
 * {@link EcoNewsDto}.
 */
@Component
public class EcoNewsDtoMapper extends AbstractConverter<EcoNewsTranslation, EcoNewsDto> {
    @Autowired
    TagDtoMapper tagDtoMapper;

    /**
     * Method for converting {@link EcoNewsTranslation} into {@link EcoNewsDto}.
     *
     * @param ecoNewsTranslation object ot convert.
     * @return converted object.
     */
    @Override
    protected EcoNewsDto convert(EcoNewsTranslation ecoNewsTranslation) {
        EcoNews ecoNews = ecoNewsTranslation.getEcoNews();
        User author = ecoNews.getAuthor();
        EcoNewsAuthorDto ecoNewsAuthorDto = new EcoNewsAuthorDto(author.getId(),
                author.getFirstName(), author.getLastName());

        return new EcoNewsDto(ecoNews.getCreationDate(), ecoNews.getImagePath(), ecoNews.getId(),
                ecoNewsTranslation.getTitle(), ecoNewsTranslation.getText(), ecoNewsAuthorDto,
                tagDtoMapper.convert(ecoNews.getTags()));
    }
}
