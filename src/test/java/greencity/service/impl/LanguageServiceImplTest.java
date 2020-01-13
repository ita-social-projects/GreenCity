package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import greencity.dto.language.LanguageDTO;
import greencity.repository.LanguageRepo;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

@RunWith(MockitoJUnitRunner.class)
public class LanguageServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private LanguageRepo languageRepo;

    @InjectMocks
    private LanguageServiceImpl languageService;

    @Test
    public void getAllAdvices() {
        List<LanguageDTO> expected = Collections.emptyList();
        when(modelMapper.map(languageRepo.findAll(), new TypeToken<List<LanguageDTO>>() {
        }.getType())).thenReturn(expected);
        assertEquals(expected, languageService.getAllLanguages());
    }
}