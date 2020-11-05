package greencity.mapping;

import greencity.dto.tipsandtricks.TextTranslationDTO;
import greencity.dto.tipsandtricks.TipsAndTricksDtoManagement;
import greencity.dto.tipsandtricks.TitleTranslationEmbeddedPostDTO;
import greencity.entity.Tag;
import greencity.entity.TipsAndTricks;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link TipsAndTricks} into
 * {@link TipsAndTricksDtoManagement}.
 */
@Component
public class TipsAndTricksDtoManagementMapper extends AbstractConverter<TipsAndTricks, TipsAndTricksDtoManagement> {
    /**
     * Method for converting {@link TipsAndTricks} into
     * {@link TipsAndTricksDtoManagement}.
     *
     * @param tipsAndTricks object to convert.
     * @return converted object.
     */
    @Override
    protected TipsAndTricksDtoManagement convert(TipsAndTricks tipsAndTricks) {
        return TipsAndTricksDtoManagement.builder()
            .id(tipsAndTricks.getId())
            .textTranslations(tipsAndTricks.getTextTranslations()
                .stream()
                .map(elem -> new TextTranslationDTO(elem.getContent(), elem.getLanguage().getCode()))
                .collect(Collectors.toList()))
            .titleTranslations(tipsAndTricks.getTitleTranslations()
                .stream()
                .map(elem -> new TitleTranslationEmbeddedPostDTO(elem.getContent(),
                    elem.getLanguage().getCode()))
                .collect(Collectors.toList()))
            .source(tipsAndTricks.getSource())
            .imagePath(tipsAndTricks.getImagePath())
            .creationDate(tipsAndTricks.getCreationDate())
            .authorName(tipsAndTricks.getAuthor().getName())
            .tags(tipsAndTricks.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toList()))
            .build();
    }
}
