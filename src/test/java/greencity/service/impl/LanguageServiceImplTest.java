package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import greencity.constant.AppConstant;
import greencity.dto.language.LanguageDTO;
import greencity.repository.LanguageRepo;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
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

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private LanguageServiceImpl languageService;

    @Test
    public void getAllAdvices() {
        List<LanguageDTO> expected = Collections.emptyList();
        when(modelMapper.map(languageRepo.findAll(), new TypeToken<List<LanguageDTO>>() {
        }.getType())).thenReturn(expected);
        assertEquals(expected, languageService.getAllLanguages());
    }

    @Test
    public void extractExistingLanguageCodeFromRequest() {
        String expectedLanguageCode = "uk";

        when(request.getParameter("language")).thenReturn(expectedLanguageCode);
        assertEquals(expectedLanguageCode, languageService.extractLanguageCodeFromRequest());
    }

    @Test
    public void extractNotExistingLanguageCodeFromRequest() {
        when(request.getParameter("language")).thenReturn(null);
        assertEquals(AppConstant.DEFAULT_LANGUAGE_CODE, languageService.extractLanguageCodeFromRequest());
    }
}