package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.dto.user.AuthorDto;
import greencity.entity.TipsAndTricks;
import greencity.entity.localization.TagTranslation;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link TipsAndTricks} into
 * {@link TipsAndTricksDtoResponse}.
 */
@Component
public class TipsAndTricksDtoResponseMapper extends AbstractConverter<TipsAndTricks, TipsAndTricksDtoResponse> {
    /**
     * Method for converting {@link TipsAndTricks} into
     * {@link AddEcoNewsDtoResponse}.
     *
     * @param tipsAndTricks object to convert.
     * @return converted object.
     */

    @Override
    protected TipsAndTricksDtoResponse convert(TipsAndTricks tipsAndTricks) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        return TipsAndTricksDtoResponse.builder()
            .id(tipsAndTricks.getId())
            .title(tipsAndTricks.getTitleTranslations()
                .stream()
                .filter(elem -> elem.getLanguage().getCode().equals(language))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Not found tipsAndTricks with language " + language))
                .getContent())
            .text(tipsAndTricks.getTextTranslations()
                .stream()
                .filter(elem -> elem.getLanguage().getCode().equals(language))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Not found tipsAndTricks with language " + language))
                .getContent())
            .source(tipsAndTricks.getSource())
            .imagePath(tipsAndTricks.getImagePath())
            .creationDate(tipsAndTricks.getCreationDate())
            .author(AuthorDto.builder()
                .id(tipsAndTricks.getAuthor().getId())
                .name(tipsAndTricks.getAuthor().getName())
                .build())
            .tags(tipsAndTricks.getTags().stream().flatMap(t -> t.getTagTranslations().stream())
                .filter(t -> t.getLanguage().getCode().equals(language))
                .map(TagTranslation::getName).collect(Collectors.toList()))
            .build();
    }
}
