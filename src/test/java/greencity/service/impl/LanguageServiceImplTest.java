package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import greencity.constant.AppConstant;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LanguageServiceImplTest {
    @InjectMocks
    private LanguageServiceImpl languageService;
    @Mock
    private HttpServletRequest request;

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
