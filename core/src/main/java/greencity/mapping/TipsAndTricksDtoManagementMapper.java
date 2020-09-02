package greencity.mapping;

import greencity.dto.tipsandtricks.TipsAndTricksDtoManagement;
import greencity.entity.Tag;
import greencity.entity.TipsAndTricks;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link TipsAndTricks} into
 * {@link TipsAndTricksDtoManagement}.
 */
@Component
public class TipsAndTricksDtoManagementMapper extends AbstractConverter<TipsAndTricks, TipsAndTricksDtoManagement> {
    /**
     * Method for converting {@link TipsAndTricks} into {@link TipsAndTricksDtoManagement}.
     *
     * @param tipsAndTricks object to convert.
     * @return converted object.
     */
    @Override
    protected TipsAndTricksDtoManagement convert(TipsAndTricks tipsAndTricks) {
        return TipsAndTricksDtoManagement.builder()
            .id(tipsAndTricks.getId())
            .text(tipsAndTricks.getText())
            .title(tipsAndTricks.getTitle())
            .source(tipsAndTricks.getSource())
            .imagePath(tipsAndTricks.getImagePath())
            .creationDate(tipsAndTricks.getCreationDate())
            .emailAuthor(tipsAndTricks.getAuthor().getEmail())
            .tags(tipsAndTricks.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList()))
            .build();
    }
}
