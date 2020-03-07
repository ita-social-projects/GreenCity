package greencity.mapping;

import greencity.dto.econews.EcoNewsDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.localization.EcoNewsTranslation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class EcoNewsDtoMapperTest {
    private User author =
            User.builder()
                    .id(1L)
                    .email("Nazar.stasyuk@gmail.com")
                    .firstName("Nazar")
                    .lastName("Stasyuk")
                    .role(ROLE.ROLE_USER)
                    .lastVisit(LocalDateTime.now())
                    .dateOfRegistration(LocalDateTime.now())
                    .build();

    private EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper = new EcoNewsAuthorDtoMapper();

    private EcoNewsAuthorDto ecoNewsAuthorDto = ecoNewsAuthorDtoMapper.convert(author);

    private EcoNewsDtoMapper ecoNewsDtoMapper = new EcoNewsDtoMapper();
    private Language language = new Language(1L, "en", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList());
    private EcoNews ecoNews = new EcoNews(1L, ZonedDateTime.now(), "imagePath", author,
            Collections.emptyList());
    private EcoNewsTranslation ecoNewsTranslation = new EcoNewsTranslation(1L, language, "title", "text", ecoNews);


    @Test
    public void convertTest() {
        EcoNewsDto expected = new EcoNewsDto(1L, ecoNewsTranslation.getTitle(),
                ecoNewsTranslation.getText(), ecoNews.getImagePath(), ecoNewsAuthorDto, ecoNews.getCreationDate());

        assertEquals(expected, ecoNewsDtoMapper.convert(ecoNewsTranslation));
    }
}
