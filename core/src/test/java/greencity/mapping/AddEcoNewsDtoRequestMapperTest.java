package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.entity.EcoNews;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.repository.LanguageRepo;
import greencity.repository.TagRepo;
import greencity.repository.UserRepo;
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
    private LanguageRepo languageRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private TagRepo tagRepo;

    private EcoNews ecoNews = ModelUtils.getEcoNews();

    @Test
    public void convertTest() {
        AddEcoNewsDtoRequest request = ModelUtils.getAddEcoNewsDtoRequest();

        when(languageRepo.findByCode(anyString()))
            .thenReturn(Optional.of(ModelUtils.getLanguage()));
        when(userRepo.findById(request.getAuthor().getId()))
            .thenReturn(Optional.of(ModelUtils.getUser()));
        when(tagRepo.findByName(ModelUtils.getTagDto().getName()))
            .thenReturn(Optional.of(ModelUtils.getTag()));

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

        when(languageRepo.findByCode(anyString()))
            .thenReturn(Optional.of(ModelUtils.getLanguage()));
        when(userRepo.findById(request.getAuthor().getId()))
            .thenReturn(Optional.of(ModelUtils.getUser()));
        when(tagRepo.findByName(ModelUtils.getTagDto().getName()))
            .thenReturn(Optional.of(ModelUtils.getTag()));
        when(languageRepo.findByCode(anyString())).thenThrow(LanguageNotFoundException.class);

        mapper.convert(request);
    }
}
