package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.LanguageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddEcoNewsDtoResponseMapperTest {
    @Mock
    private LanguageService languageService;
    @Mock
    private EcoNewsTranslationRepo ecoNewsTranslationRepo;
    @Mock
    private EcoNewsAuthorDtoMapper ecoNewsAuthorDtoMapper;
    @InjectMocks
    private AddEcoNewsDtoResponseMapper mapper;

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

    private Language language = new Language(1L, "en", Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList());

    private EcoNewsAuthorDto ecoNewsAuthorDto = new EcoNewsAuthorDtoMapper().convert(author);

    private EcoNewsTranslation ecoNewsTranslation = new EcoNewsTranslation(1L, language, "title", "text", null);

    private EcoNews ecoNews = new EcoNews(null, ZonedDateTime.now(), "imagePath", author,
            Collections.singletonList(ecoNewsTranslation), new ArrayList<Tag>());

    @Test
    public void convertTest() {
        when(languageService.extractLanguageCodeFromRequest()).thenReturn("en");
        when(ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(ecoNews,
                languageService.extractLanguageCodeFromRequest())).thenReturn(ecoNewsTranslation);
        when(ecoNewsAuthorDtoMapper.convert(author)).thenReturn(ecoNewsAuthorDto);

        AddEcoNewsDtoResponse expected = new AddEcoNewsDtoResponse(ecoNews.getId(), ecoNewsTranslation.getTitle(),
                ecoNewsTranslation.getText(), ecoNewsAuthorDto, ecoNews.getCreationDate(), ecoNews.getImagePath());

        assertEquals(expected, mapper.convert(ecoNews));
    }
}
