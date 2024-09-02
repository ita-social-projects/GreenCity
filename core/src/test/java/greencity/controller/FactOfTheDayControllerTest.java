package greencity.controller;

import greencity.service.FactOfTheDayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FactOfTheDayControllerTest {
    private static final String factOfTheDayLink = "/fact-of-the-day";

    private MockMvc mockMvc;
    @InjectMocks
    private FactOfTheDayController factOfTheDayController;
    @Mock
    private FactOfTheDayService factOfTheDayService;

    @Mock
    private Validator mockValidator;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(factOfTheDayController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void getRandomFactOfTheDayTest() throws Exception {
        mockMvc.perform(get(factOfTheDayLink + "/random"))
            .andExpect(status().isOk());
        verify(factOfTheDayService).getRandomFactOfTheDayByLanguage("en");
    }

    @Test
    void getAllFactOfTheDayTest() throws Exception {
        mockMvc.perform(get(factOfTheDayLink + "?page=0&size=10"))
            .andExpect(status().isOk());
        Pageable pageable = PageRequest.of(0, 10);
        verify(factOfTheDayService).getAllFactsOfTheDay(pageable);
    }

    @Test
    void findFactOfTheDayTest() throws Exception {
        mockMvc.perform(get(factOfTheDayLink + "/{factId}", 1))
            .andExpect(status().isOk());
        verify(factOfTheDayService).getFactOfTheDayById(1L);
    }
}
