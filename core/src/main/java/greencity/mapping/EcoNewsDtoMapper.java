package greencity.mapping;

import greencity.dto.econews.EcoNewsDto;
import greencity.dto.tag.TagDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
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
    TagDtoMapper tagDtoMapper;

    /**
     * All args constructor.
     *
     * @param tagDtoMapper needed to convert {@link Tag} to {@link TagDto}.
     */
    @Autowired
    public EcoNewsDtoMapper(TagDtoMapper tagDtoMapper) {
        this.tagDtoMapper = tagDtoMapper;
    }

    /**
     * Method for converting {@link EcoNewsTranslation} into {@link EcoNewsDto}.
     *
     * @param ecoNewsTranslation object ot convert.
     * @return converted object.
     */
    @Override
    public EcoNewsDto convert(EcoNewsTranslation ecoNewsTranslation) {
        EcoNews ecoNews = ecoNewsTranslation.getEcoNews();
        User author = ecoNews.getAuthor();
        EcoNewsAuthorDto ecoNewsAuthorDto = new EcoNewsAuthorDto(author.getId(),
                author.getFirstName(), author.getLastName());

        return new EcoNewsDto(ecoNews.getCreationDate(), ecoNews.getImagePath(), ecoNews.getId(),
                ecoNewsTranslation.getTitle(), ecoNewsTranslation.getText(), ecoNewsAuthorDto,
                tagDtoMapper.convert(ecoNews.getTags()));
    }
}
