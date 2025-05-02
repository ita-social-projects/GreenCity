package greencity.mapping;

import greencity.dto.search.SearchNewsDto;
import greencity.entity.EcoNews;
import greencity.entity.localization.TagTranslation;
import greencity.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchNewsDtoMapper extends AbstractConverter<EcoNews, SearchNewsDto> {
    private final LanguageService languageService;

    @Override
    protected SearchNewsDto convert(EcoNews ecoNews) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        return SearchNewsDto.builder()
            .id(ecoNews.getId())
            .title(ecoNews.getTitle())
            .tags(ecoNews.getTags().stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .filter(tagTranslation -> {
                    Long languageId = tagTranslation.getLanguageId();
                    String languageCode = languageService.findLanguageCodeById(languageId);
                    return languageCode.equals(language);
                })
                .map(TagTranslation::getName)
                .toList())
            .build();
    }
}
