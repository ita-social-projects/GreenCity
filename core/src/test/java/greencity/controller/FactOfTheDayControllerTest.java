package greencity.controller;

import greencity.constant.ErrorMessage;
import greencity.dto.factoftheday.FactOfTheDayPostDTO;
import greencity.exception.exceptions.NotUpdatedException;
import greencity.service.FactOfTheDayService;
import greencity.service.FactOfTheDayTranslationService;
import greencity.service.LanguageService;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        String languageCode = "uk";
        mockMvc.perform(get(factOfTheDayLink + "/")
            .param("languageCode", languageCode))
            .andExpect(status().isOk());

        verify(factOfTheDayTranslationService).getRandomFactOfTheDayByLanguage(languageCode);
    }

    @Test
    public void getAllFactOfTheDayTest() throws Exception {
        Pageable pageable = PageRequest.of(0, 9);
        mockMvc.perform(get(factOfTheDayLink + "/all?page=0&size=9"))
            .andExpect(status().isOk());

        verify(factOfTheDayService).getAllFactsOfTheDay(pageable);
    }

    @Test
    public void findFactOfTheDayTest() throws Exception {
        mockMvc.perform(get(factOfTheDayLink + "/find?id=1"))
            .andExpect(status().isOk());

        verify(factOfTheDayService).getFactOfTheDayById(1L);
    }

    @Test
    public void saveFactOfTheDayTest() throws Exception {
        FactOfTheDayPostDTO factOfTheDayPostDTO = getFactOfTheDayPostDTO();
        mockMvc.perform(post(factOfTheDayLink + "/")
            .content("{\n" +
                "\"id\": \"1\",\n" +
                "\"name\": \"New Fact\"\n" +
                "}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(factOfTheDayService).saveFactOfTheDayAndTranslations(eq(factOfTheDayPostDTO));
    }

    private FactOfTheDayPostDTO getFactOfTheDayPostDTO() {
        FactOfTheDayPostDTO factOfTheDayPostDTO = new FactOfTheDayPostDTO();
        factOfTheDayPostDTO.setName("New Fact");
        factOfTheDayPostDTO.setId(1L);
        return factOfTheDayPostDTO;
    }

    @Test
    public void saveFactOfTheDayWithoutIdTest() throws Exception {
        mockMvc.perform(post(factOfTheDayLink + "/")
            .content("{\"name\": \"testName\"}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(factOfTheDayService, never()).saveFactOfTheDayAndTranslations(any(FactOfTheDayPostDTO.class));
    }

    @Test
    public void updateFactOfTheDayTest() throws Exception {
        FactOfTheDayPostDTO factOfTheDayPostDTO = getFactOfTheDayPostDTO();
        mockMvc.perform(put(factOfTheDayLink + "/")
            .content("{\n" +
                "\"id\": \"1\",\n" +
                "\"name\": \"New Fact\"\n" +
                "}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(factOfTheDayService).updateFactOfTheDayAndTranslations(eq(factOfTheDayPostDTO));
    }

    @Test
    public void updateFactOfTheDayWithoutIdTest() throws Exception {
        mockMvc.perform(put(factOfTheDayLink + "/")
            .content("{\"name\": \"testName\"}")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(factOfTheDayService, never()).updateFactOfTheDayAndTranslations(any(FactOfTheDayPostDTO.class));
    }

    @Test
    public void getLanguagesTest() throws Exception {
        mockMvc.perform(get(factOfTheDayLink + "/languages"))
            .andExpect(status().isOk());

        verify(languageService).getAllLanguages();
    }

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete(factOfTheDayLink + "/?id=1"))
            .andExpect(status().isOk());

        verify(factOfTheDayService).deleteFactOfTheDayAndTranslations(1L);
    }

    @Test
    public void deleteBadRequestTest() {
        when(factOfTheDayService.deleteFactOfTheDayAndTranslations(1L))
            .thenThrow(new NotUpdatedException(ErrorMessage.FACT_OF_THE_DAY_NOT_DELETED));
        Assertions.assertThatThrownBy(() -> mockMvc
            .perform(delete(factOfTheDayLink + "/?id=1")))
            .hasCause(new NotUpdatedException(ErrorMessage.FACT_OF_THE_DAY_NOT_DELETED));

        verify(factOfTheDayService).deleteFactOfTheDayAndTranslations(1L);
    }

    @Test
    public void deleteAllTest() throws Exception {
        List<Long> longList = Arrays.asList(1L, 2L);
        mockMvc.perform(delete(factOfTheDayLink + "/deleteAll")
            .content("[1,2]")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(factOfTheDayService).deleteAllFactOfTheDayAndTranslations(longList);
    }

    @Test
    public void deleteAllBadRequestTest() {
        List<Long> longList = Arrays.asList(1L, 2L);
        when(factOfTheDayService.deleteAllFactOfTheDayAndTranslations(longList))
            .thenThrow(new NotUpdatedException(ErrorMessage.FACT_OF_THE_DAY_NOT_DELETED));
        Assertions.assertThatThrownBy(() -> mockMvc.perform(delete(factOfTheDayLink + "/deleteAll")
            .content("[1,2]")
            .characterEncoding("utf-8")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)))
            .hasCause(new NotUpdatedException(ErrorMessage.FACT_OF_THE_DAY_NOT_DELETED));

        verify(factOfTheDayService).deleteAllFactOfTheDayAndTranslations(longList);
    }

}
