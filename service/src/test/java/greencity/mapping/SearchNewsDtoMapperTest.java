package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.search.SearchNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.User;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;

@ExtendWith(MockitoExtension.class)
class SearchNewsDtoMapperTest {
    @InjectMocks
    private SearchNewsDtoMapper searchNewsDtoMapper;

    private EcoNews ecoNewsTest = ModelUtils.getEcoNews();

    private User user = ModelUtils.getUser();

    @BeforeEach
    void init() {
        searchNewsDtoMapper = new SearchNewsDtoMapper();
    }

    @Test
    void convertTest() {
        String language = LocaleContextHolder.getLocale().getLanguage();

        SearchNewsDto searchedNews = SearchNewsDto.builder()
            .id(1L)
            .title(ecoNewsTest.getTitle())
            .author(new EcoNewsAuthorDto(user.getId(),
                user.getName()))
            .creationDate(ecoNewsTest.getCreationDate())
            .tags(ecoNewsTest.getTags()
                .stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .filter(tagTranslation -> tagTranslation.getLanguage().getCode().equals(language))
                .map(TagTranslation::getName)
                .collect(Collectors.toList()))
            .build();

        assertEquals(searchedNews, searchNewsDtoMapper.convert(ecoNewsTest));
    }
}
