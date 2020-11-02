package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceVO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.dto.user.HabitIdRequestDto;
import greencity.service.AdviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AdviceControllerTest {
    private static final String adviceLink = "/advices";

    private MockMvc mockMvc;

    @InjectMocks
    private AdviceController adviceController;

    @Mock
    private AdviceService adviceService;

    @Mock
    private Validator mockValidator;

    @Mock
    private ModelMapper modelMapper;

    public static final String content = "{\n"
            + "  \"habit\": {\n"
            + "    \"id\": 1\n"
            + "  },\n"
            + "  \"translations\": [\n"
            + "    {\n"
            + "      \"content\": \"Еко\",\n"
            + "      \"language\": {\n"
            + "        \"code\": \"ua\",\n"
            + "        \"id\": 1\n"
            + "      }\n"
            + "    },\n"
            + "    {\n"
            + "      \"content\": \"Eco\",\n"
            + "      \"language\": {\n"
            + "        \"code\": \"en\",\n"
            + "        \"id\": 2\n"
            + "      }\n"
            + "    },\n"
            + "    {\n"
            + "      \"content\": \"Эко\",\n"
            + "      \"language\": {\n"
            + "        \"code\": \"ru\",\n"
            + "        \"id\": 3\n"
            + "      }\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(adviceController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setValidator(mockValidator)
                .build();
    }

    /*@Test
    void getAllTest() throws Exception {
        mockMvc.perform(get(adviceLink))
                .andExpect(status().isOk());
        verify(adviceService).getAllAdvices();
    }*/

    @Test
    void getRandomAdviceHabitIdAndLanguageTest() throws Exception {
        mockMvc.perform(get(adviceLink + "/random/1?lang=en"))
                .andExpect(status().isOk());

        verify(adviceService).getRandomAdviceByHabitIdAndLanguage(1L, "en");
    }

    @Test
    void updateTest() throws Exception {
        List<LanguageTranslationDTO> languageTranslationDTOs = Arrays.asList(
                new LanguageTranslationDTO(new LanguageDTO(1L, "ua"), "hello"),
                new LanguageTranslationDTO(new LanguageDTO(2L, "en"), "привіт"),
                new LanguageTranslationDTO(new LanguageDTO(3L, "ru"), "привет"));
        AdvicePostDto advicePostDto = new AdvicePostDto(languageTranslationDTOs, new HabitIdRequestDto(1L));
        ObjectMapper objectMapper = new ObjectMapper();
        String advicePostDtoJson = objectMapper.writeValueAsString(advicePostDto);

        Long adviceId = 1L;
        mockMvc.perform(put(adviceLink + "/{adviceId}", adviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(advicePostDtoJson))
                .andExpect(status().isOk());

        verify(adviceService).update(advicePostDto, adviceId);
    }

    @Test
    void getRandomAdviceHabitWithInvalidIdAndLanguageTest() throws Exception {
        mockMvc.perform(get(adviceLink + "/random/{id}?lang=en", "invalidId"))
                .andExpect(status().isBadRequest());
        verify(adviceService, times(0)).getRandomAdviceByHabitIdAndLanguage(1L, "en");
    }

    @Test
    void saveTest() throws Exception {
        mockMvc.perform(post(adviceLink)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isCreated());
        ObjectMapper mapper = new ObjectMapper();
        AdvicePostDto advicePostDTO = mapper.readValue(content, AdvicePostDto.class);
        verify(adviceService, times(1)).save(advicePostDTO);
    }

    @Test
    void deleteTest() throws Exception {
        mockMvc.perform(delete(adviceLink + "/{adviceId}", 1)
        ).andExpect(status().isOk());

        verify(adviceService, times(1))
                .delete(1L);
    }

    @Test
    void deleteFailedTest() throws Exception {
        mockMvc.perform(delete(adviceLink + "/{adviceId}", "invalidId")
        ).andExpect(status().isBadRequest());

        verify(adviceService, times(0))
                .delete(anyLong());
    }

    @Test
    void getByIdTest() throws Exception {
        Long adviceId = 1L;
        mockMvc.perform(get(adviceLink + "/{adviceId}", adviceId))
                .andExpect(status().isOk());

        verify(adviceService, times(1))
                .getAdviceById(adviceId);
    }
}