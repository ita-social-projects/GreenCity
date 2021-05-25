package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.search.SearchTipsAndTricksDto;
import greencity.dto.user.AuthorDto;
import greencity.entity.TipsAndTricks;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Locale;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class SearchTipsAndTricksDtoMapperTest {

    @InjectMocks
    private SearchTipsAndTricksDtoMapper mapper;

    @Test
    void convert() {
        TipsAndTricks tipsAndTricks = ModelUtils.getTipsAndTricks();
        User author = tipsAndTricks.getAuthor();
        LocaleContextHolder.setLocale(Locale.ENGLISH);

        SearchTipsAndTricksDto actual = SearchTipsAndTricksDto.builder()
            .id(tipsAndTricks.getId())
            .title(tipsAndTricks.getTitleTranslations()
                .stream()
                .filter(elem -> elem.getLanguage().getCode().equals("en"))
                .findFirst().orElseThrow(RuntimeException::new).getContent())
            .author(new AuthorDto(author.getId(),
                author.getName()))
            .creationDate(tipsAndTricks.getCreationDate())
            .tags(tipsAndTricks.getTags().stream().flatMap(t -> t.getTagTranslations().stream())
                .map(TagTranslation::getName).collect(Collectors.toList()))
            .build();
        SearchTipsAndTricksDto expected = mapper.convert(tipsAndTricks);

        assertEquals(expected, actual);
    }
}