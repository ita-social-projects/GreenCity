package greencity.mapping;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.econews.EcoNewsDto;
import greencity.entity.EcoNews;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.entity.localization.TagTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class EcoNewsDtoMapperTest {
    @InjectMocks
    EcoNewsDtoMapper ecoNewsDtoMapper;

    @Test
    void convertTest() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        String defaultLanguage = AppConstant.DEFAULT_LANGUAGE_CODE;

        EcoNewsDto expected = new EcoNewsDto(ecoNews.getCreationDate(), ecoNews.getImagePath(),
            ecoNews.getId(), ecoNews.getTitle(), ecoNews.getText(), ecoNews.getSource(),
            ModelUtils.getEcoNewsAuthorDto(),
            ecoNews.getTags().stream()
                .flatMap(t -> t.getTagTranslations().stream())
                .filter(t -> t.getLanguage().getCode().equals(defaultLanguage))
                .map(TagTranslation::getName)
                .collect(Collectors.toList()), 0);

        assertEquals(expected, ecoNewsDtoMapper.convert(ecoNews));
    }
}
