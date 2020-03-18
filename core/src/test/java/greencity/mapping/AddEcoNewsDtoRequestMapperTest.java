package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.entity.EcoNews;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.repository.UserRepo;
import greencity.service.LanguageService;
import greencity.service.TagService;
import java.util.Collections;
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
    private LanguageService languageService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private TagService tagService;

    private EcoNews ecoNews = ModelUtils.getEcoNews();

    @Test
    public void convertTest() {
        AddEcoNewsDtoRequest request = ModelUtils.getAddEcoNewsDtoRequest();

        when(languageService.findByCode(anyString()))
            .thenReturn(ModelUtils.getLanguage());
        when(userRepo.findById(request.getAuthor().getId()))
            .thenReturn(Optional.of(ModelUtils.getUser()));
        when(tagService.findByName(ModelUtils.getTagDto().getName()))
            .thenReturn(ModelUtils.getTag());

        EcoNews actual = mapper.convert(request);
        actual.setId(1L);
        actual.setAuthor(ModelUtils.getUser());
        actual.setCreationDate(ecoNews.getCreationDate());
        actual.setTags(Collections.singletonList(ModelUtils.getTag()));

        Assert.assertEquals(ecoNews, actual);
    }

    @Test(expected = LanguageNotFoundException.class)
    public void convertFailsWithLanguageNotFoundException() {
        AddEcoNewsDtoRequest request = ModelUtils.getAddEcoNewsDtoRequest();

        when(languageService.findByCode(anyString()))
            .thenReturn(ModelUtils.getLanguage());
        when(userRepo.findById(request.getAuthor().getId()))
            .thenReturn(Optional.of(ModelUtils.getUser()));
        when(tagService.findByName(ModelUtils.getTagDto().getName()))
            .thenReturn(ModelUtils.getTag());
        when(languageService.findByCode(anyString()))
            .thenThrow(LanguageNotFoundException.class);

        mapper.convert(request);
    }
}
