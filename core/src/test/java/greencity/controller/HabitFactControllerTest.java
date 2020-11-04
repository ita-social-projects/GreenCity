package greencity.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import greencity.dto.habitfact.HabitFactPostDto;
import greencity.service.HabitFactService;
import greencity.service.HabitFactTranslationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import java.util.Locale;

@ExtendWith(MockitoExtension.class)
class HabitFactControllerTest {

    private MockMvc mockMvc;

    @Mock
    HabitFactService habitFactService;
    @Mock
    HabitFactTranslationService habitFactTranslationService;
    @Mock
    ModelMapper mapper;
    @Mock
    private Validator mockValidator;

    @InjectMocks
    HabitFactController habitFactController;

    private static final String factLink = "/facts";

    public static final String content = "{\n"
        + "  \"habit\": {\n"
        + "    \"id\": 1\n"
        + "  },\n"
        + "  \"translations\": [\n"
        + "    {\n"
        + "      \"content\": \"тест\",\n"
        + "      \"language\": {\n"
        + "        \"code\": \"ua\",\n"
        + "        \"id\": 1\n"
        + "      }\n"
        + "    },\n"
        + "    {\n"
        + "      \"content\": \"тест\",\n"
        + "      \"language\": {\n"
        + "        \"code\": \"en\",\n"
        + "        \"id\": 2\n"
        + "      }\n"
        + "    },\n"
        + "    {\n"
        + "      \"content\": \"тест\",\n"
        + "      \"language\": {\n"
        + "        \"code\": \"ru\",\n"
        + "        \"id\": 3\n"
        + "      }\n"
        + "    }\n"
        + "  ]\n"
        + "}";


    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(habitFactController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void getRandomFactByHabitIdTest() throws Exception {
        Locale locale = new Locale("en");
        Gson gson = new Gson();
        String json = gson.toJson(locale);
        mockMvc.perform(get(factLink + "/random/{habitId}", 1)
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        verify(habitFactService).getRandomHabitFactByHabitIdAndLanguage(1L, locale.getLanguage());
    }

    @Test
    void getHabitFactOfTheDayTest() throws Exception {
        mockMvc.perform(get(factLink + "/dayFact/{languageId}", 1))
            .andExpect(status().isOk());
        verify(habitFactTranslationService).getHabitFactOfTheDay(1L);
    }

    @Test
    void getAllTest() throws Exception {
        int pageNumber = 0;
        int pageSize = 20;
        Locale locale = new Locale("en");
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(factLink))
            .andExpect(status().isOk());
        verify(habitFactService).getAllHabitFacts(pageable, locale.getLanguage());
    }


    @Test
    void deleteTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(factLink + "/{factId}", 1))
            .andExpect(status().isOk());
        verify(habitFactService).delete(1L);
    }

    @Test
    void saveTest() throws Exception {
        mockMvc.perform(post(factLink)
            .content(content)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        HabitFactPostDto habitFactPostDto = mapper.readValue(content, HabitFactPostDto.class);
        verify(habitFactTranslationService, times(1))
            .saveHabitFactAndFactTranslation(eq(habitFactPostDto));
    }

    @Test
    void updateTest() throws Exception {
        Long factId = 1L;
        mockMvc.perform(put(factLink + "/{factId}", factId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        HabitFactPostDto habitFactPostDto = mapper.readValue(content, HabitFactPostDto.class);
        verify(habitFactService).update(eq(habitFactPostDto), eq(factId));
    }

    @Test
    void deleteFailedTest() throws Exception {
        mockMvc.perform(delete(factLink + "/{factId}", "invalidId"))
            .andExpect(status().isBadRequest());

        verify(habitFactService, times(0)).delete(anyLong());
    }
}
