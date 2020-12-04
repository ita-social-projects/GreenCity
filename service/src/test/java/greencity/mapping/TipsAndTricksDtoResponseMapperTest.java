package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.dto.user.AuthorDto;
import greencity.entity.TipsAndTricks;
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
class TipsAndTricksDtoResponseMapperTest {

    @InjectMocks
    private TipsAndTricksDtoResponseMapper mapper;

    @Test
    void convert() {
        TipsAndTricks tipsAndTricks = ModelUtils.getTipsAndTricks();
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        String language = "en";

        TipsAndTricksDtoResponse actual = TipsAndTricksDtoResponse.builder()
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
        TipsAndTricksDtoResponse expected = mapper.convert(tipsAndTricks);

        assertEquals(expected, actual);
    }
}