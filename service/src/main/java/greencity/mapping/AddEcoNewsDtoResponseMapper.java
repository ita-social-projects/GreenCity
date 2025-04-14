package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import java.util.stream.Collectors;

import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link EcoNews} into
 * {@link AddEcoNewsDtoResponse}.
 */
@Component
@RequiredArgsConstructor
public class AddEcoNewsDtoResponseMapper extends AbstractConverter<EcoNews, AddEcoNewsDtoResponse> {

    private final ModelMapper modelMapper;

    /**
     * Method for converting {@link EcoNews} into {@link AddEcoNewsDtoResponse}.
     *
     * @param ecoNews object to convert.
     * @return converted object.
     */
    @Override
    protected AddEcoNewsDtoResponse convert(EcoNews ecoNews) {
        User author = ecoNews.getAuthor();
        UserVO authorVO = modelMapper.map(author, UserVO.class);

        return AddEcoNewsDtoResponse.builder()
            .id(ecoNews.getId())
            .text(ecoNews.getText())
            .title(ecoNews.getTitle())
            .source(ecoNews.getSource())
            .imagePath(ecoNews.getImagePath())
            .creationDate(ecoNews.getCreationDate())
            .shortInfo(ecoNews.getShortInfo())
            .ecoNewsAuthorDto(EcoNewsAuthorDto.builder()
                .id(author.getId())
                .name(authorVO.getName())
                .build())
            .tags(ecoNews.getTags().stream().flatMap(t -> t.getTagTranslations().stream())
                .map(TagTranslation::getName).collect(Collectors.toList()))
            .build();
    }
}
