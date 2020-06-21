package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.dto.user.AuthorDto;
import greencity.entity.TipsAndTricks;
import greencity.entity.TipsAndTricksTag;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link TipsAndTricks} into
 * {@link TipsAndTricksDtoResponse}.
 */
@Component
public class TipsAndTricksDtoResponseMapper extends AbstractConverter<TipsAndTricks, TipsAndTricksDtoResponse> {
    /**
     * Method for converting {@link TipsAndTricks} into {@link AddEcoNewsDtoResponse}.
     *
     * @param tipsAndTricks object to convert.
     * @return converted object.
     */
    @Override
    protected TipsAndTricksDtoResponse convert(TipsAndTricks tipsAndTricks) {
        return TipsAndTricksDtoResponse.builder()
            .id(tipsAndTricks.getId())
            .text(tipsAndTricks.getText())
            .title(tipsAndTricks.getTitle())
            .source(tipsAndTricks.getSource())
            .imagePath(tipsAndTricks.getImagePath())
            .creationDate(tipsAndTricks.getCreationDate())
            .author(AuthorDto.builder()
                .id(tipsAndTricks.getAuthor().getId())
                .name(tipsAndTricks.getAuthor().getName())
                .build())
            .tipsAndTricksTags(tipsAndTricks.getTipsAndTricksTags()
                .stream()
                .map(TipsAndTricksTag::getName)
                .collect(Collectors.toList()))
            .build();
    }
}
