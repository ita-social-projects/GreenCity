package greencity.controller;

import greencity.service.FactOfTheDayService;
import greencity.service.FactOfTheDayTranslationService;
import greencity.service.LanguageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@ExtendWith(MockitoExtension.class)
public class FactOfTheDayControllerTest {
    private static final String factOfTheDayLink = "/factoftheday";

    private MockMvc mockMvc;
    @InjectMocks
    private FactOfTheDayController factOfTheDayController;
    @Mock
    private FactOfTheDayTranslationService factOfTheDayTranslationService;
    @Mock
    private FactOfTheDayService factOfTheDayService;
    @Mock
    private LanguageService languageService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(factOfTheDayController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    public void getRandomFactOfTheDayTest() throws Exception {
        mockMvc.perform(get(factOfTheDayLink + "/"))
            .andExpect(status().isOk());
        verify(factOfTheDayTranslationService).getRandomFactOfTheDayByLanguage("en");
    }

    @Test
    public void getAllFactOfTheDayTest() throws Exception {
        mockMvc.perform(get(factOfTheDayLink + "/all?page=0&size=10"))
            .andExpect(status().isOk());
        Pageable pageable = PageRequest.of(0, 10);
        verify(factOfTheDayService).getAllFactsOfTheDay(pageable);
    }

    @Test
    public void findFactOfTheDayTest() throws Exception {
        mockMvc.perform(get(factOfTheDayLink + "/find?id=1"))
            .andExpect(status().isOk());
        verify(factOfTheDayService).getFactOfTheDayById(1L);
    }


    @Test
    public void getLanguagesTest() throws Exception {
        mockMvc.perform(get(factOfTheDayLink + "/languages"))
            .andExpect(status().isOk());
        verify(languageService).getAllLanguages();
    }
}

