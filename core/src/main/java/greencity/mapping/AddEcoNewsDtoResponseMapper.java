package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Tag;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link EcoNews} into
 * {@link AddEcoNewsDtoResponse}.
 */
@Component
public class AddEcoNewsDtoResponseMapper extends AbstractConverter<EcoNews, AddEcoNewsDtoResponse> {
    private EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper;

    /**
     * All args constructor.
     */
    @Autowired
    public AddEcoNewsDtoResponseMapper(EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper) {
        this.ecoNewsAuthorDtoMapper = ecoNewsAuthorDtoMapper;
    }

    /**
     * Method for converting {@link EcoNews} into {@link AddEcoNewsDtoResponse}.
     *
     * @param ecoNews object to convert.
     * @return converted object.
     */
    @Override
    protected AddEcoNewsDtoResponse convert(EcoNews ecoNews) {
        EcoNewsAuthorDto ecoNewsAuthorDto = ecoNewsAuthorDtoMapper.convert(ecoNews.getAuthor());
        List<String> tags = ecoNews.getTags()
            .stream()
            .map(Tag::getName)
            .collect(Collectors.toList());

        return new AddEcoNewsDtoResponse(ecoNews.getId(), null, null,
            ecoNewsAuthorDto, ecoNews.getCreationDate(), ecoNews.getImagePath(), tags);
    }
}
