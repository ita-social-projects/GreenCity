package greencity.mapping;

import greencity.dto.econews.EcoNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.localization.EcoNewsTranslation;
import java.util.stream.Collectors;
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
    public EcoNewsDto convert(EcoNewsTranslation ecoNewsTranslation) {
        EcoNews ecoNews = ecoNewsTranslation.getEcoNews();
        User author = ecoNews.getAuthor();
        EcoNewsAuthorDto ecoNewsAuthorDto = new EcoNewsAuthorDto(author.getId(),
            author.getFirstName(), author.getLastName());

        return new EcoNewsDto(ecoNews.getCreationDate(), ecoNews.getImagePath(), ecoNews.getId(),
            ecoNewsTranslation.getTitle(), ecoNewsTranslation.getText(), ecoNewsAuthorDto,
            ecoNews.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList()));
    }
}
