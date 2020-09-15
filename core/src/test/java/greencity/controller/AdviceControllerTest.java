package greencity.controller;

import greencity.service.AdviceService;
import greencity.service.LanguageService;
import greencity.validator.LanguageTranslationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdviceControllerTest {
    private static final String adviceLink = "/advices";

    private MockMvc mockMvc;

    @Mock
    AdviceService adviceService;

    @Mock
    ModelMapper modelMapper;

    @Mock
    LanguageService languageService;

    @Mock
    LanguageTranslationValidator languageTranslationValidator;

    @InjectMocks
    AdviceController adviceController;

    public static final String content = "{\n" +
        "  \"habitDictionary\": {\n" +
        "    \"id\": 1\n" +
        "  },\n" +
        "  \"translations\": [\n" +
        "    {\n" +
        "      \"content\": \"Eco\",\n" +
        "      \"language\": {\n" +
        "        \"code\": \"en\",\n" +
        "        \"id\": 1\n" +
        "      }\n" +
        "    },\n" +
        "    {\n" +
        "      \"content\": \"Еко\",\n" +
        "      \"language\": {\n" +
        "        \"code\": \"uk\",\n" +
        "        \"id\": 2\n" +
        "      }\n" +
        "    },\n" +
        "    {\n" +
        "      \"content\": \"Эко\",\n" +
        "      \"language\": {\n" +
        "        \"code\": \"ru\",\n" +
        "        \"id\": 3\n" +
        "       }\n" +
        "     } \n" +
        "  ]\n" +
        "}";

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(adviceController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    public void findAllTest() throws Exception {
        mockMvc.perform(get(adviceLink)).andExpect(status().isOk());
        verify(adviceService, times(1)).getAllAdvices();
    }

    @Test
    public void getRandomAdviceHabitIdAndLanguageTest() throws Exception{
        mockMvc.perform(get(adviceLink + "/random/1?lang=en"))
            .andExpect(status().isOk());

        verify(adviceService).getRandomAdviceByHabitIdAndLanguage(1L, "en");
    }

    @Test
    public void getRandomAdviceHabitWithInvalidIdAndLanguageTest() throws Exception{
        mockMvc.perform(get(adviceLink + "/random/{id}?lang=en", "invalidId"))
            .andExpect(status().isBadRequest());

        verify(adviceService, times(0)).getRandomAdviceByHabitIdAndLanguage(1L, "en");
    }

    @Test
    @Disabled
    public void saveTest() throws Exception {
        //AdvicePostDTO advicePostDTO = ModelUtils.getAdvicePostDTO();

        //when(languageService.getAllLanguages()).thenReturn(Collections.singletonList(ModelUtils.getLanguageDTO()));

        mockMvc.perform(post(adviceLink)
        .contentType(MediaType.APPLICATION_JSON)
        .content(content))
        .andExpect(status().isCreated());
    }

    @Test
    @Disabled
    public void updateTest() throws Exception {
        mockMvc.perform(put(adviceLink + "/{adviceId}",1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());
    }

    @Test
    public void deleteTest() throws Exception {
        mockMvc.perform(delete(adviceLink + "/{adviceId}",1)
        ).andExpect(status().isOk());

        verify(adviceService, times(1))
            .delete(1L);
    }

    @Test
    public void deleteFailedTest() throws Exception {
        mockMvc.perform(delete(adviceLink + "/{adviceId}","invalidId")
        ).andExpect(status().isBadRequest());

        verify(adviceService, times(0))
            .delete(anyLong());
    }



}