package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.tag.TagDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.LanguageService;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

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

    private List<TagDto> tagDtos = Collections.emptyList();

    private EcoNewsTranslation ecoNewsTranslation = new EcoNewsTranslation(1L, language, "title", "text", null);

    private EcoNews ecoNews = new EcoNews(null, ZonedDateTime.now(), "imagePath", author,
        Collections.singletonList(ecoNewsTranslation), Collections.emptyList());

    @Test
    public void convertTest() {
        when(languageService.extractLanguageCodeFromRequest()).thenReturn("en");
        when(ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(ecoNews,
            languageService.extractLanguageCodeFromRequest())).thenReturn(ecoNewsTranslation);
        when(ecoNewsAuthorDtoMapper.convert(author)).thenReturn(ecoNewsAuthorDto);

        AddEcoNewsDtoResponse expected = new AddEcoNewsDtoResponse(ecoNews.getId(), ecoNewsTranslation.getTitle(),
            ecoNewsTranslation.getText(), ecoNewsAuthorDto, ecoNews.getCreationDate(), ecoNews.getImagePath(),
            tagDtos);

        assertEquals(expected, mapper.convert(ecoNews));
    }
}
