package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.econews.EcoNewsTranslationDto;
import greencity.dto.language.LanguageRequestDto;
import greencity.dto.tag.TagDto;
import greencity.dto.user.EcoNewsAuthorDto;
import greencity.entity.EcoNews;
import greencity.entity.Language;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.repository.LanguageRepository;
import greencity.repository.TagRepo;
import greencity.repository.UserRepo;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AddEcoNewsDtoRequestMapperTest {
    @InjectMocks
    private AddEcoNewsDtoRequestMapper mapper;
    @Mock
    private LanguageRepository languageRepository;
    @Mock
    private UserRepo userRepo;
    @Mock
    private TagRepo tagRepo;

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

    private EcoNewsAuthorDto ecoNewsAuthorDto = new EcoNewsAuthorDto(1L, "Nazar", "Stasyuk");

    private EcoNews ecoNews = new EcoNews(null, ZonedDateTime.now(), "imagePath", author,
        Collections.singletonList(ecoNewsTranslation), Collections.emptyList());

    private EcoNewsTranslationDto ecoNewsTranslationDto = new EcoNewsTranslationDto(
        new LanguageRequestDto("en"), "title", "text");

    private Tag tag = new Tag(1L, "tag", Collections.emptyList());

    private List<TagDto> tagDtos = Collections.singletonList(new TagDto("tag"));

    @Test
    public void convertTest() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest(Collections.singletonList(
            ecoNewsTranslationDto), tagDtos, ecoNewsAuthorDto, ecoNews.getImagePath());

        when(languageRepository.findByCode(anyString())).thenReturn(Optional.of(language));
        when(userRepo.findById(request.getAuthor().getId())).thenReturn(Optional.ofNullable(author));
        when(tagRepo.findByName(tagDtos.get(0).getName())).thenReturn(Optional.ofNullable(tag));

        EcoNews actual = mapper.convert(request);
        actual.setAuthor(author);
        actual.setCreationDate(ecoNews.getCreationDate());
        actual.setTags(Collections.emptyList());

        Assert.assertEquals(ecoNews, actual);
    }

    @Test(expected = LanguageNotFoundException.class)
    public void convertFailsWithLanguageNotFoundException() {
        AddEcoNewsDtoRequest request = new AddEcoNewsDtoRequest(Collections.singletonList(
            ecoNewsTranslationDto), tagDtos, ecoNewsAuthorDto, ecoNews.getImagePath());

        when(languageRepository.findByCode(anyString())).thenReturn(Optional.of(language));
        when(userRepo.findById(request.getAuthor().getId())).thenReturn(Optional.ofNullable(author));
        when(tagRepo.findByName(tagDtos.get(0).getName())).thenReturn(Optional.ofNullable(tag));
        when(languageRepository.findByCode(anyString())).thenThrow(LanguageNotFoundException.class);

        mapper.convert(request);
    }
}
