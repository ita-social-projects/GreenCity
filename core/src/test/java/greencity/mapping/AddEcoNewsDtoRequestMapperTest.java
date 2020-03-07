package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.EcoNewsTranslationDto;
import greencity.dto.language.LanguageRequestDto;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.repository.LanguageRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddEcoNewsDtoRequestMapperTest {
    @InjectMocks
    private AddEcoNewsDtoRequestMapper mapper;
    @Mock
    private LanguageRepository languageRepository;

    private Language language = new Language(1L, "en", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList());

    private EcoNewsTranslation ecoNewsTranslation = new EcoNewsTranslation(1L, language, "title", "text", null);

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

    private EcoNews ecoNews = new EcoNews(null, ZonedDateTime.now(), "imagePath", author,
            Collections.singletonList(ecoNewsTranslation), new ArrayList<Tag>());

    private EcoNewsTranslationDto ecoNewsTranslationDto = new EcoNewsTranslationDto(
            new LanguageRequestDto("en"), "title", "text");

    @Test
    public void convertTest() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest(Collections.singletonList(
                ecoNewsTranslationDto), ecoNews.getImagePath());

        when(languageRepository.findByCode(anyString())).thenReturn(Optional.of(language));

        EcoNews actual = mapper.convert(request);
        actual.setAuthor(author);
        actual.setCreationDate(ecoNews.getCreationDate());

        Assert.assertEquals(ecoNews, actual);
    }

    @Test(expected = LanguageNotFoundException.class)
    public void convertFailsWithLanguageNotFoundException() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest(Collections.singletonList(
                ecoNewsTranslationDto), ecoNews.getImagePath());

        when(languageRepository.findByCode(anyString())).thenReturn(Optional.of(language));

        when(languageRepository.findByCode(anyString())).thenThrow(LanguageNotFoundException.class);

        mapper.convert(request);
    }
}
