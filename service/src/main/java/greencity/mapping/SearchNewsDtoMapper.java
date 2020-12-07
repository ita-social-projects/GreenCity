package greencity.mapping;

import greencity.dto.search.SearchNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.User;
import java.util.stream.Collectors;

import greencity.entity.localization.TagTranslation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class SearchNewsDtoMapper extends AbstractConverter<EcoNews, SearchNewsDto> {
    @Override
    protected SearchNewsDto convert(EcoNews ecoNews) {
        User author = ecoNews.getAuthor();

        return SearchNewsDto.builder()
            .id(ecoNews.getId())
            .title(ecoNews.getTitle())
            .author(new EcoNewsAuthorDto(author.getId(),
                author.getName()))
            .creationDate(ecoNews.getCreationDate())
            .tags(ecoNews.getTags().stream().flatMap(t -> t.getTagTranslations().stream())
                .map(TagTranslation::getName).collect(Collectors.toList()))
            .build();
    }
}
