package greencity.mapping.events;

import greencity.dto.search.SearchEventsDto;
import greencity.entity.event.Event;
import greencity.entity.localization.TagTranslation;
import greencity.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchEventsDtoMapper extends AbstractConverter<Event, SearchEventsDto> {
    private final LanguageService languageService;

    @Override
    protected SearchEventsDto convert(Event event) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        Long languageId = languageService.findLanguageIdByCode(language);

        return SearchEventsDto.builder()
            .id(event.getId())
            .title(event.getTitle())
            .tags(event.getTags().stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .filter(tagTranslation -> tagTranslation.getLanguageId().equals(languageId))
                .map(TagTranslation::getName)
                .toList())
            .build();
    }
}
