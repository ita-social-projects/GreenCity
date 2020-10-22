
package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.advice.AdvicePostDto;
import greencity.dto.advice.AdviceTranslationVO;
import greencity.service.AdviceService;
import greencity.service.AdviceTranslationService;
import java.util.Collections;
import java.util.List;
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

@ExtendWith(MockitoExtension.class)
class AdviceControllerTest {
    private static final String adviceLink = "/advices";

    private MockMvc mockMvc;

    @Mock
    AdviceService adviceService;

    @Mock
    ModelMapper modelMapper;

    @Mock
    AdviceTranslationService adviceTranslationService;

    @Mock
    Validator mockValidator;

    @InjectMocks
    AdviceController adviceController;

    private List<AdviceTranslationVO> adviceTranslationsVO =
        Collections.singletonList(AdviceTranslationVO.builder().build());

    private AdvicePostDto advicePostDto = new AdvicePostDto();

    StringBuilder build = new StringBuilder();

    public static final String content =
            "{\n"
            + "   \"habit\": {\n"
            + "       \"id\": 1\n"
            + "     },\n"
            + "   \"translations\": [\n"
            + "    {\n"
            + "      \"content\": \"Eco\",\n"
            + "      \"language\": {\n"
            + "        \"code\": \"en\",\n"
            + "        \"id\": 2\n"
            + "      }\n"
            + "    },\n"
            + "    {\n"
            + "      \"content\": \"Еко\",\n"
            + "      \"language\": {\n"
            + "        \"code\": \"uk\",\n"
            + "        \"id\": 1\n"
            + "      }\n"
            + "    },\n"
            + "    {\n"
            + "      \"content\": \"Эко\",\n"
            + "      \"language\": {\n"
            + "        \"code\": \"ru\",\n"
            + "        \"id\": 3\n"
            + "       }\n"
            + "     } \n"
            + "     ] \n"
            + "},\n";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(adviceController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setValidator(mockValidator)
            .build();
    }

    @Test
    void findAllTest() throws Exception {
        mockMvc.perform(get(adviceLink)).andExpect(status().isOk());
        verify(adviceService, times(1)).getAllAdvices();
    }

    @Test
    void getRandomAdviceHabitIdAndLanguageTest() throws Exception {
        mockMvc.perform(get(adviceLink + "/random/1?lang=en"))
            .andExpect(status().isOk());

        verify(adviceService).getRandomAdviceByHabitIdAndLanguage(1L, "en");
    }

    @Test
    void updateTest() throws Exception {
        mockMvc.perform(put(adviceLink + "/{adviceId}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        ObjectMapper mapper = new ObjectMapper();
        AdvicePostDto advicePostDTO = mapper.readValue(content, AdvicePostDto.class);

        verify(adviceService, times(1)).update(advicePostDTO, 1L);
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
        verify(adviceTranslationService, times(1)).saveAdviceAndAdviceTranslation(advicePostDTO);
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
}


