package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link EcoNews} into
 * {@link AddEcoNewsDtoResponse}.
 */
@Component
public class AddEcoNewsDtoResponseMapper extends AbstractConverter<EcoNews, AddEcoNewsDtoResponse> {

    /**
     * Method for converting {@link EcoNews} into {@link AddEcoNewsDtoResponse}.
     *
     * @param ecoNews object to convert.
     * @return converted object.
     */
    @Override
    protected AddEcoNewsDtoResponse convert(EcoNews ecoNews) {
        EcoNewsAuthorDto ecoNewsAuthorDto = EcoNewsAuthorDto.builder()
            .id(ecoNews.getAuthor().getId())
            .firstName(ecoNews.getAuthor().getFirstName())
            .lastName(ecoNews.getAuthor().getLastName())
            .build();

        List<String> tags = ecoNews.getTags()
            .stream()
            .map(Tag::getName)
            .collect(Collectors.toList());

        return AddEcoNewsDtoResponse.builder()
            .id(ecoNews.getId())
            .ecoNewsAuthorDto(ecoNewsAuthorDto)
            .creationDate(ecoNews.getCreationDate())
            .imagePath(ecoNews.getImagePath())
            .tags(tags)
            .build();
    }
}
