package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tipsandtricks.TextTranslationDTO;
import greencity.dto.tipsandtricks.TipsAndTricksDtoManagement;
import greencity.dto.tipsandtricks.TipsAndTricksDtoResponse;
import greencity.dto.tipsandtricks.TitleTranslationEmbeddedPostDTO;
import greencity.dto.user.AuthorDto;
import greencity.entity.TipsAndTricks;
import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class TipsAndTricksDtoManagementMapperTest {

    @InjectMocks
    private TipsAndTricksDtoManagementMapper tipsAndTricksDtoManagementMapper;

    @Test
    void convert() {
        TipsAndTricks tipsAndTricks = ModelUtils.getTipsAndTricks();

        TipsAndTricksDtoManagement actual = TipsAndTricksDtoManagement.builder()
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
            .tags(tipsAndTricks.getTags().stream().flatMap(t -> t.getTagTranslations().stream())
                .map(TagTranslation::getName).collect(Collectors.toList()))
            .build();

        TipsAndTricksDtoManagement expected = tipsAndTricksDtoManagementMapper
            .convert(tipsAndTricks);


        assertEquals(expected, actual);
    }
}